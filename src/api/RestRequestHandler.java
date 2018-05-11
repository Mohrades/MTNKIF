package api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import dao.DAO;

@RestController("api")
public class RestRequestHandler {

	@Autowired
	private DAO dao;

	@RequestMapping(value = "/info", method = RequestMethod.GET, produces = "text/xml;charset=UTF-8")
	public ModelAndView handlePollPostRequest(HttpServletRequest request, @RequestParam(value="msisdn", required=false, defaultValue = "") String msisdn) throws Exception {
		return callback(msisdn, 0, "message");
	}

	@RequestMapping(value = "/status", produces = "text/xml;charset=UTF-8")
	public ModelAndView handlePollGetRequest(HttpServletRequest request, @RequestParam("msisdn") String msisdn) throws Exception {
		// String msisdn = request.getParameter("msisdn");

		return callback(msisdn, 0, "message");
	}

    @RequestMapping(value={"/subscription/{msisdn}", "/index.do"}, params={"auth=true", "refresh", "!authenticate"}, method=RequestMethod.POST, produces = "text/xml;charset=UTF-8")
	public ModelAndView handleHttpServletRequest(@RequestParam("msisdn") String msisdn, @PathVariable("msisdn") String msisdn_confirmation) throws Exception {
		// String msisdn = request.getParameter("msisdn");

    	return callback(msisdn, 0, "message");
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
		return new ModelAndView(new RestResponseView(), modele);		
	}

}