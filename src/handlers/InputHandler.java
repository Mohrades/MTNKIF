package handlers;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;

import com.google.common.base.Splitter;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.USSDRequestDAOJdbc;
import dao.queries.USSDServiceDAOJdbc;
import domain.models.USSDRequest;
import domain.models.USSDService;
import filter.MSISDNValidator;
import product.DefaultPricePlan;
import product.PricePlanCurrent;
import product.PricePlanCurrentActions;
import product.ProductProperties;
import product.USSDMenu;
import tools.SMPPConnector;
import util.AccountDetails;

public class InputHandler {

	public InputHandler() {

	}

	public void handle(MessageSource i18n, ProductProperties productProperties, Map<String, String> parameters, Map<String, Object> modele, HttpServletRequest request, DAO dao) {
		USSDRequest ussd = null;

		AccountDetails accountDetails = new AIRRequest().getAccountDetails(parameters.get("msisdn"));
		int language = (accountDetails == null) ? 1 : accountDetails.getLanguageIDCurrent();

		try {
			long sessionId = Long.parseLong(parameters.get("sessionid"));
			ussd = new USSDRequestDAOJdbc(dao).getOneUSSD(sessionId, parameters.get("msisdn"));

			if(ussd == null) {
				USSDService service = new USSDServiceDAOJdbc(dao).getOneUSSDService(productProperties.getSc());
				Date now = new Date();

				if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
					modele.put("next", false);
					modele.put("message", i18n.getMessage("service.unavailable", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					return;
				}

				ussd = new USSDRequest(0, sessionId, parameters.get("msisdn"), parameters.get("input").trim(), 1, null);
			}
			else {
				ussd.setStep(ussd.getStep() + 1);
				ussd.setInput((ussd.getInput() + "*" + parameters.get("input").trim()).trim());
			}

			// USSD Flow Status
			Map<String, Object> flowStatus = new USSDFlow().validate(ussd, language, (new USSDMenu()).getContent(productProperties.getSc()), productProperties, i18n);

			// -1 : exit with error (delete state from ussd table; message)
			if(((Integer)flowStatus.get("status")) == -1) {
				endStep(dao, ussd, modele, productProperties, (String)flowStatus.get("message"), null, null, null, null);
			}

			// 0  : successful (delete state from ussd table; actions and message)
			else if(((Integer)flowStatus.get("status")) == 0) {
				String short_code = productProperties.getSc() + "";

				if(ussd.getInput().equals(short_code + "*2")) {
					// statut
					statut(i18n, language, productProperties, dao, ussd, modele);
				}
				else if(ussd.getInput().equals(short_code + "*3")) {
					// infos
					endStep(dao, ussd, modele, productProperties, (new PricePlanCurrentActions()).getInfo(i18n, productProperties, ussd.getMsisdn()), null, null, null, null);
				}
				else if((ussd.getInput().equals(short_code + "*1")) || (ussd.getInput().equals(short_code + "*0"))) {
					if((new MSISDNValidator()).isFiltered(dao, productProperties, ussd.getMsisdn(), "A")) {
						List<String> inputs = Splitter.onPattern("[*]").trimResults().omitEmptyStrings().splitToList(ussd.getInput());

						if(inputs.size() == 2) {
							int statusCode = (int)(((new PricePlanCurrent()).getStatus(productProperties, i18n, dao, ussd.getMsisdn(), language))[0]);

							if(statusCode > 0) {
								if(ussd.getInput().endsWith("*0")) {
									// deactivation
									if(statusCode == 0) deactivation(dao, ussd, i18n, language, productProperties, modele);
									else endStep(dao, ussd, modele, productProperties, i18n.getMessage("status.unsuccessful.already", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
								}
								else if(ussd.getInput().endsWith("*1")) {
									// activation
									if(statusCode == 0) endStep(dao, ussd, modele, productProperties, i18n.getMessage("status.successful.already", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
									else {
										// check msisdn is in default price plan
										statusCode = productProperties.isDefault_price_plan_deactivated() ? (new DefaultPricePlan()).requestDefaultPricePlanStatus(productProperties, ussd.getMsisdn(), "eBA") : 0;

										if(statusCode == 0) activation(dao, ussd, i18n, language, productProperties, modele);
										else endStep(dao, ussd, modele, productProperties, i18n.getMessage("default.price.plan.required", new Object [] {productProperties.getDefault_price_plan()}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
									}
								}
							}
							else {
								endStep(dao, ussd, modele, productProperties, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
							}
						}
						else {
							endStep(dao, ussd, modele, productProperties, i18n.getMessage("request.unavailable", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
						}
					}
					else endStep(dao, ussd, modele, productProperties, i18n.getMessage("menu.disabled", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
				}
				else {
					endStep(dao, ussd, modele, productProperties, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
				}
			}

			// 1  : flow continues (save state; message)
			else if(((Integer)flowStatus.get("status")) == 1) {
				nextStep(dao, ussd, false, (String)flowStatus.get("message"), modele, productProperties);
			}

			// this case should not occur
			else {
				endStep(dao, ussd, modele, productProperties, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH), null, null, null, null);
			}

		} catch(NullPointerException ex) {
			endStep(dao, ussd, modele, productProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);

		} catch(Throwable th) {
			endStep(dao, ussd, modele, productProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);
		}
	}

	public void statut(MessageSource i18n, int language, ProductProperties productProperties, DAO dao, USSDRequest ussd, Map<String, Object> modele) {
		endStep(dao, ussd, modele, productProperties, (String)(((new PricePlanCurrent()).getStatus(productProperties, i18n, dao, ussd.getMsisdn(), language))[1]), null, null, null, null);
	}

	public void endStep(DAO dao, USSDRequest ussd, Map<String, Object> modele, ProductProperties productProperties, String messageA, String Anumber, String messageB, String Bnumber, String senderName) {
		if((ussd != null) && (ussd.getId() > 0)) {
			new USSDRequestDAOJdbc(dao).deleteOneUSSD(ussd.getId());
		}

		modele.put("next", false);
		modele.put("message", messageA);

		if(senderName != null) {
			if(Anumber != null) {
				if(Anumber.startsWith(productProperties.getMcc() + "")) Anumber = Anumber.substring((productProperties.getMcc() + "").length());
				new SMPPConnector().submitSm(senderName, Anumber, messageA);
			}
			if(Bnumber != null) {
				if(Bnumber.startsWith(productProperties.getMcc() + "")) Bnumber = Bnumber.substring((productProperties.getMcc() + "").length());
				new SMPPConnector().submitSm(senderName, Bnumber, messageB);
			}
		}
	}

	public void nextStep(DAO dao, USSDRequest ussd, boolean reset, String message, Map<String, Object> modele, ProductProperties productProperties) {
		if(reset) {
			ussd.setStep(1);
			ussd.setInput(productProperties.getSc() + "");
		}
		else {
			//
		}

		new USSDRequestDAOJdbc(dao).saveOneUSSD(ussd);

		modele.put("next", true);
		modele.put("message", message);
	}

	public void activation(DAO dao, USSDRequest ussd, MessageSource i18n, int language, ProductProperties productProperties, Map<String, Object> modele) {
		Object [] requestStatus = (new PricePlanCurrent()).activation(dao, ussd.getMsisdn(), i18n, language, productProperties, "eBA");
		endStep(dao, ussd, modele, productProperties, (String)requestStatus[1], ((int)requestStatus[0] == 0) ? ussd.getMsisdn() : null, null, null, ((int)requestStatus[0] == 0) ? productProperties.getSms_notifications_header() : null);
	}

	public void deactivation(DAO dao, USSDRequest ussd, MessageSource i18n, int language, ProductProperties productProperties, Map<String, Object> modele) {
		Object [] requestStatus = (new PricePlanCurrent()).deactivation(dao, ussd.getMsisdn(), i18n, language, productProperties, "eBA");
		endStep(dao, ussd, modele, productProperties, (String)requestStatus[1], ((int)requestStatus[0] == 0) ? ussd.getMsisdn() : null, null, null, ((int)requestStatus[0] == 0) ? productProperties.getSms_notifications_header() : null);
	}

}
