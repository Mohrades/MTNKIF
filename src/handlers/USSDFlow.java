package handlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.MessageSource;

import com.google.common.base.Splitter;

import dao.DAO;
import dao.queries.JdbcHappyBirthDayBonusSubscriberDao;
import domain.models.HappyBirthDayBonusSubscriber;
import domain.models.Subscriber;
import domain.models.USSDRequest;
import filter.MSISDNValidator;
import product.PricePlanCurrent;
import product.ProductProperties;

@SuppressWarnings("unused")
public class USSDFlow {

	public USSDFlow() {

	}

	@SuppressWarnings("deprecation")
	public Map<String, Object> validate(USSDRequest ussd, int language, Document document, ProductProperties productProperties, MessageSource i18n, DAO dao) {
		// on cr�e le mod�le de la vue � afficher
		Map<String, Object> modele = new HashMap<String, Object>();
		// initialization
		modele.put("status", -1);

		// on cr�e le mod�le de l'arborescence
		StringJoiner tree = new StringJoiner(".", ".", "");
		tree.setEmptyValue("");

		try {
			// USSD(int id, long sessionId, String msisdn, String input, int step, Date last_update_time)
			// 250*1**263*abc*1*97975506  ==>  [250, 1, , 263, abc, 1, 97975506]
			// List<String> inputs = Splitter.onPattern("[.|,|;]").trimResults().omitEmptyStrings().splitToList(ussd.getInput());
			// List<String> inputs = Splitter.onPattern("[*]").trimResults().splitToList(ussd.getInput());
			List<String> inputs = Splitter.onPattern("[*]").trimResults().omitEmptyStrings().splitToList(ussd.getInput());

			int index = 0;
			Element currentState = null;

			transitions : for(String input : inputs) {
				if((input == null) || (input.isEmpty()) ||(input.length() == 0)) {
					return handleInvalidInput(i18n.getMessage("request.unavailable", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
				}

				// on-entry : verify service code
				if(index == 0) {
					if(document.getRootElement().getName().equals("SERVICE-CODE-"  + input)) {
						currentState = (document.getRootElement()).getChild("menu");
						index++;
					}
					else {
						return handleInvalidInput(i18n.getMessage("service.unavailable", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					}
				}
				// transition : verify each step of the flow
				else {
					if(hasChildren(currentState)) {
						@SuppressWarnings("rawtypes")
						List children = currentState.getChildren("input");
						Element choice = currentState.getChild("choice-" + input);

						if(choice != null) {
							children = currentState.getChildren();

							@SuppressWarnings("rawtypes")
							ListIterator iterator = children.listIterator();
							int step = 0;

							while (iterator.hasNext()) {
								step++;
								Element element = (Element) iterator.next();

								if(element.getName().equals("choice-" + input)) {
									tree.add(step + "");
									break;
								}
							}

							currentState = choice;
							continue transitions;
						}
						else if(children.isEmpty()) {
							return handleInvalidInput(i18n.getMessage("integer.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
						}
						else {
							children = currentState.getChildren();

							@SuppressWarnings("rawtypes")
							ListIterator iterator = children.listIterator();
							int step = 0;

							while (iterator.hasNext()) {
								step++;
								Element element = (Element) iterator.next();

								if(element.getName().startsWith("choice-")) {

								}
								else if(element.getName().equals("input")) {
									if(element.getAttributeValue("type").equals("static")) {
										if(input.equals(element.getAttributeValue("value"))) {
											currentState = element;
											tree.add(step + "");
											continue transitions;
										}
										else {
											if(children.size() == 1) {
												return handleInvalidInput(i18n.getMessage("request.unavailable", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
											}
										}
									}
									else if(element.getAttributeValue("type").equals("text")) {
										if(input.isEmpty()) {
											if(children.size() == 1) {
												return handleInvalidInput(i18n.getMessage("argument.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
											}
										}
										else {
											currentState = element;
											tree.add(step + "");
											continue transitions;
										}
									}
									else if(element.getAttributeValue("type").equals("number")) {
										try {
											long number = Long.parseLong(input);

											if((((element.getAttributeValue("min") != null) && (number < Long.parseLong(element.getAttributeValue("min")))) || ((element.getAttributeValue("max") != null) && (number > Long.parseLong(element.getAttributeValue("max")))))) {
												if(children.size() == 1) {
													if((element.getAttributeValue("min") != null) && (element.getAttributeValue("max") != null)) {
														return handleInvalidInput(i18n.getMessage("integer.range", new Object[] {Long.parseLong(element.getAttributeValue("min")), Long.parseLong(element.getAttributeValue("max"))}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
													}
													else if(element.getAttributeValue("min") != null) {
														return handleInvalidInput(i18n.getMessage("integer.min", new Object[] {Long.parseLong(element.getAttributeValue("min"))}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
													}
													else if(element.getAttributeValue("max") != null) {
														return handleInvalidInput(i18n.getMessage("integer.max", new Object[] {Long.parseLong(element.getAttributeValue("max"))}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
													}
												}
											}
											else {
												currentState = element;
												tree.add(step + "");
												continue transitions;
											}

										} catch(NullPointerException|NumberFormatException ex) {
											if(children.size() == 1) {
												return handleInvalidInput(i18n.getMessage("integer.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
											}

										} catch(Throwable ex) {
											if(children.size() == 1) {
												return handleInvalidInput(i18n.getMessage("integer.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
											}
										}
									}
									else if(element.getAttributeValue("type").equals("msisdn")) {
										try {
											String msisdn = Long.parseLong(input) + "";

											if((element.getAttributeValue("ton").equals("International") && (msisdn.startsWith(productProperties.getMcc() + "")) && (((productProperties.getMcc() + "").length() + productProperties.getMsisdn_length()) == msisdn.length())) || ((element.getAttributeValue("ton").equals("National")) && (productProperties.getMsisdn_length() == msisdn.length()))) {
											/*if((element.getAttributeValue("ton").equals("International")) || ((element.getAttributeValue("ton").equals("National")) && (webAppProperties.getMsisdn_length() == msisdn.length()))) {*/
												if((element.getAttributeValue("network") == null) || (element.getAttributeValue("network").isEmpty()) || (element.getAttributeValue("network").equals("off"))) {
													currentState = element;
													tree.add(step + "");
													continue transitions;
												}
												else if(element.getAttributeValue("network").equals("off")) {
													if(((element.getAttributeValue("ton").equals("National")) && !(new MSISDNValidator()).onNet(productProperties, productProperties.getMcc() + "" + msisdn)) || ((element.getAttributeValue("ton").equals("International")) && !(new MSISDNValidator()).onNet(productProperties, msisdn))) {
														currentState = element;
														tree.add(step + "");
														continue transitions;
													}
													else {
														if(children.size() == 1) {
															return handleInvalidInput(i18n.getMessage("msisdn.offnet.required", new Object[] {productProperties.getGsm_name()}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
														}
													}
												}
												else if(element.getAttributeValue("network").equals("on")) {
													if(((element.getAttributeValue("ton").equals("National")) && (new MSISDNValidator()).onNet(productProperties, productProperties.getMcc() + "" + msisdn)) || ((element.getAttributeValue("ton").equals("International")) && (new MSISDNValidator()).onNet(productProperties, msisdn))) {
														currentState = element;
														tree.add(step + "");
														continue transitions;
													}
													else {
														if(children.size() == 1) {
															return handleInvalidInput(i18n.getMessage("msisdn.onnet.required", new Object[] {productProperties.getGsm_name()}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
														}
													}
												}
											}
											else {
												if(children.size() == 1) {
													return handleInvalidInput(i18n.getMessage("msisdn.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
												}
											}

										} catch(NullPointerException|NumberFormatException ex) {
											if(children.size() == 1) {
												return handleInvalidInput(i18n.getMessage("msisdn.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
											}

										} catch(Throwable ex) {
											if(children.size() == 1) {
												return handleInvalidInput(i18n.getMessage("msisdn.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
											}
										}
									}									
								}
							}

							return handleInvalidInput(i18n.getMessage("argument.required", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
						}
					}
					else {
						return handleInvalidInput(i18n.getMessage("request.unavailable", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					}
				}
			}

			// StringJoiner is used internally by static String.join().
			// String.join("-", "2015", "10", "31" ); // Join String by a delimiter ==> 2015-10-31
			// correct subscriber inputs in the good and recommended format
			ussd.setInput(String.join("*", inputs)); // Join a List by a delimiter

			// on-transition : view-state
			if(hasChildren(currentState)) {
				String transitions = tree.toString();
				/*transitions = transitions.replace(" ", "");
				transitions = transitions.replace("[", "");
				transitions = transitions.replace("]", "");
				transitions = transitions.replace(",", ".");*/
				transitions = transitions.trim();

				modele.put("status", 1);
				/*if(transitions.length() == 0) modele.put("message", i18n.getMessage("menu", null, null, null));
				else modele.put("message", i18n.getMessage("menu." + transitions, null, null, null));*/
				if((("menu" + transitions).equals("menu.5")) || (("menu" + transitions).equals("menu.1"))) {
					Object [] requestStatus = (new PricePlanCurrent()).getStatus(productProperties, i18n, dao, ussd.getMsisdn(), language, false);

					if(("menu" + transitions).equals("menu.1")) {
						modele.put("message", i18n.getMessage("menu.1", new Object[] {(((Subscriber)requestStatus[2] == null) || (((Subscriber)requestStatus[2]).getId() == 0) || (((Subscriber)requestStatus[2]).getLast_update_time() == null)) ? (0 + "") : ((productProperties.getActivation_chargingAmount()/100) + "")}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					}
					else {
						Date now = new Date();
						now.setDate(now.getDate() - productProperties.getDeactivation_freeCharging_days());
						modele.put("message", i18n.getMessage("menu.5", new Object[] {(((Subscriber)requestStatus[2] == null) || (((Subscriber)requestStatus[2]).getId() == 0) || (((Subscriber)requestStatus[2]).getLast_update_time() == null) || (((Subscriber)requestStatus[2]).getLast_update_time().before(now))) ? (0 + "") : ((productProperties.getDeactivation_chargingAmount()/100) + "")}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					}
				}
				else {
					if(transitions.isEmpty() || transitions.equals("")) {
						/*HappyBirthDayBonusSubscriber birthDayBonusSubscriber = (new JdbcHappyBirthDayBonusSubscriberDao(dao)).getOneBirthdayBonusSubscriber(ussd.getMsisdn(), true);

						if(birthDayBonusSubscriber == null) {
							modele.put("message", i18n.getMessage("menu" + transitions, null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
						}
						else {
							Object [] requestStatus = (new PricePlanCurrent()).getStatus(productProperties, i18n, dao, ussd.getMsisdn(), language, false);
							modele.put("message", i18n.getMessage(((int)(requestStatus[0]) == 0) ? "menu_with_hbd" : "menu", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
						}*/

						Object [] requestStatus = (new PricePlanCurrent()).hasBirthDayBonus(productProperties, i18n, dao, ussd.getMsisdn(), language);
						modele.put("message", i18n.getMessage((((int)(requestStatus[0]) == 0) && (requestStatus[1] != null) && ((int)(requestStatus[1]) == 0)) ? "menu_with_hbd" : "menu", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					}
					else {
						modele.put("message", i18n.getMessage("menu" + transitions, null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					}
				}
			}
			// on-end : end-state
			else {
				modele.put("status", 0);
			}

		} catch(NullPointerException ex) {
			handleException(modele, i18n, language);

		} catch(Throwable th) {
			handleException(modele, i18n, language);
		}

		return modele;
	}

	public void handleException(Map<String, Object> modele, MessageSource i18n, int language) {
		modele.put("status", -1);
		modele.put("message", i18n.getMessage("request.unavailable", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
	}

	public Map<String, Object> handleInvalidInput(String message) {
		// on cr�e le mod�le de la vue � afficher
		Map<String, Object> modele = new HashMap<String, Object>();

		modele.put("status", -1);
		modele.put("message", message);
		return modele;
	}

	public boolean hasChildren(Element currentSate) {
		return (currentSate == null) ? false : (currentSate.getChildren().size() > 0);
	}

}
