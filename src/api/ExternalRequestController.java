package api;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import connexions.AIRRequest;
import dao.DAO;
import domain.models.Subscriber;
import filter.MSISDNValidator;
import product.DefaultPricePlan;
import product.PricePlanCurrent;
import product.PricePlanCurrentActions;
import product.ProductProperties;
import tools.SMPPConnector;
import util.AccountDetails;

@RestController("api")
public class ExternalRequestController {

	@Autowired
	private MessageSource i18n;

	@Autowired
	private DAO dao;

	@Autowired
	private ProductProperties productProperties;

	@RequestMapping(value = "/info", method = RequestMethod.GET, params={"authentication=true", "originOperatorID"}, produces = "text/xml;charset=UTF-8")
	public ModelAndView handlePricePlanInfoRequest(HttpServletRequest request, @RequestParam(value="msisdn", required=false, defaultValue = "") String msisdn) throws Exception {
		String originOperatorID = request.getParameter("originOperatorID");

		if((originOperatorID == null) || (originOperatorID.trim().length() == 0) || (msisdn == null) || (!(new MSISDNValidator()).onNet(productProperties, msisdn))) {
			return callback(msisdn, -1, i18n.getMessage("service.internal.error", null, null, Locale.FRENCH));
		}

		return callback(msisdn, 0, (new PricePlanCurrentActions()).getInfo(i18n, productProperties, msisdn));
	}

	@RequestMapping(value = "/status", params={"authentication=true", "originOperatorID"}, produces = "text/xml;charset=UTF-8")
	public ModelAndView handlePricePlanStatusRequest(HttpServletRequest request) throws Exception {
		String msisdn = request.getParameter("msisdn");
		String originOperatorID = request.getParameter("originOperatorID");

		if((originOperatorID == null) || (originOperatorID.trim().length() == 0) || (msisdn == null) || (!(new MSISDNValidator()).onNet(productProperties, msisdn))) {
			return callback(msisdn, -1, i18n.getMessage("service.internal.error", null, null, Locale.FRENCH));
		}

		AccountDetails accountDetails = new AIRRequest().getAccountDetails(msisdn);
		int language = (accountDetails == null) ? 1 : accountDetails.getLanguageIDCurrent();

		originOperatorID = originOperatorID.trim();

		Object[] status = (new PricePlanCurrent()).getStatus(productProperties, i18n, dao, msisdn, language);
		return callback(msisdn, (int)(status[0]), (String)(status[1]));
	}

    /*@RequestMapping(value={"/subscription/{msisdn}", "/index.do"}, params={"auth=true", "refresh", "!authenticate"}, method=RequestMethod.POST, produces = "text/xml;charset=UTF-8")*/
    @RequestMapping(value="/subscription/{msisdn}", params={"authentication=true", "originOperatorID", "action"}, method=RequestMethod.POST, produces = "text/xml;charset=UTF-8")
	public ModelAndView handlePricePlanSubscriptionRequest(HttpServletRequest request, @RequestParam("msisdn") String msisdn, @PathVariable("msisdn") String msisdn_confirmation) throws Exception {
		String action = request.getParameter("msisdn");
		String originOperatorID = request.getParameter("originOperatorID");

		if((originOperatorID == null) || (originOperatorID.trim().length() == 0) || (action == null) || (!(action.equals("activation") || action.equals("deactivation"))) || (msisdn == null) || (msisdn_confirmation == null) || (!msisdn.equals(msisdn_confirmation)) || (!(new MSISDNValidator()).onNet(productProperties, msisdn))) {
			return callback(msisdn, -1, i18n.getMessage("service.internal.error", null, null, Locale.FRENCH));
		}

		AccountDetails accountDetails = new AIRRequest().getAccountDetails(msisdn);
		int language = (accountDetails == null) ? 1 : accountDetails.getLanguageIDCurrent();

		if((new MSISDNValidator()).isFiltered(dao, productProperties, msisdn, "A")) {
			originOperatorID = originOperatorID.trim();
			Object [] requestStatus = (new PricePlanCurrent()).getStatus(productProperties, i18n, dao, msisdn, language);

			if((int)(requestStatus[0]) >= 0) {
				if(action.equals("deactivation")) {
					// deactivation
					if((int)(requestStatus[0]) == 0) {
						requestStatus = (new PricePlanCurrent()).deactivation(dao, msisdn, (Subscriber)requestStatus[2], i18n, language, productProperties, originOperatorID);

						// notification via sms
						if((int)requestStatus[0] == 0) {
							requestSubmitSmToSmppConnector(productProperties, (String)requestStatus[1], msisdn, null, null, productProperties.getSms_notifications_header());
						}

						return callback(msisdn, (int)requestStatus[0], (String)requestStatus[1]);
					}
					else return callback(msisdn, -1, i18n.getMessage("status.unsuccessful.already", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
				}
				else if(action.equals("activation")) {
					// activation
					if((int)(requestStatus[0]) == 0) return callback(msisdn, -1, i18n.getMessage("status.successful.already", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					else {
						// check msisdn is in default price plan
						requestStatus[0] = productProperties.isDefault_price_plan_deactivated() ? (new DefaultPricePlan()).requestDefaultPricePlanStatus(productProperties, msisdn, originOperatorID) : 0;

						if((int)(requestStatus[0]) == 0) {
							requestStatus = (new PricePlanCurrent()).activation(dao, msisdn, (Subscriber)requestStatus[2], i18n, language, productProperties, originOperatorID);

							// notification via sms
							if((int)requestStatus[0] == 0) {
								requestSubmitSmToSmppConnector(productProperties, (String)requestStatus[1], msisdn, null, null, productProperties.getSms_notifications_header());
							}

							return callback(msisdn, (int)requestStatus[0], (String)requestStatus[1]);
						}
						else return callback(msisdn, -1, i18n.getMessage("default.price.plan.required", new Object [] {productProperties.getDefault_price_plan()}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
					}
				}
				else return callback(msisdn, -1, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
			}
			else {
				return callback(msisdn, -1, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
			}
		}
		else {
			return callback(msisdn, -1, i18n.getMessage("menu.disabled", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH));
		}
	}

	private String XMLResponse(String msisdn, int statusCode, String message) {
		String xml_response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

		// send empty response
		xml_response += "<response>\n";

			if((msisdn != null) && (!msisdn.isEmpty())) {
				xml_response += "<msisdn>" + msisdn + "</msisdn>\n";
			}

			xml_response += "<statusCode>" + statusCode + "</statusCode>\n";

			xml_response += "<applicationResponse>" + message + "</applicationResponse>\n";

		xml_response += "</response>\n";

		return xml_response;
	}

	public ModelAndView callback(String msisdn, int statusCode, String message) {
		// on crée le modèle de la vue à afficher
		Map<String, String> modele = new HashMap<String, String>();
		modele.put("response", XMLResponse(msisdn, statusCode, message));

		// on retourne le ModelAndView
		return new ModelAndView(new CallbackDataAndView(), modele);
	}
	
	public void requestSubmitSmToSmppConnector(ProductProperties productProperties, String messageA, String Anumber, String messageB, String Bnumber, String senderName) {
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

}