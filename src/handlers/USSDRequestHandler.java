package handlers;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dao.DAO;
import dao.queries.MSISDNRedirectionDAOJdbc;
import dao.queries.USSDServiceDAOJdbc;
import domain.models.MSISDNRedirection;
import environment.Development;
import environment.Production;
import environment.USSDResponseView;
import filter.MSISDNValidator;
import product.ProductProperties;

@SuppressWarnings("unused")
@Controller
@RequestMapping(value={"/*"})
public class USSDRequestHandler {

	@Autowired
	private MessageSource i18n;

	@Autowired
	private DAO dao;

	@Autowired
	private ProductProperties productProperties;

	// ----------------------- récupérer le corps du POST
	/*@RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded; charset=UTF-8")*/
	/*public ModelAndView handleSubmitSmRequest(@RequestParam(value="source", required=true) String source, @RequestParam(value="callbackUrl", required=true) String callbackUrl, @RequestParam(value="destination", required=true) String destination, @RequestParam(value="messageText", required=true) String messageText, HttpServletResponse response) throws Exception {*/
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return handleUSSDRequest(request, response);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return handleUSSDRequest(request, response);
	}

	public ModelAndView handleUSSDRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> headers = new HttpUSSDRequest().GetHeaders(request);
		Map<String, String> parameters = new HttpUSSDRequest().GetParameters(request, false);

		// on crée le modèle de la vue à afficher
		Map<String, Object> modele = new HashMap<String, Object>();

		try {
			if(parameters.containsKey("Environment") && parameters.get("Environment").equals("Production")) {
				(new Production()).execute(i18n, productProperties, parameters, modele, dao, request, response);
			}
			else {
				MSISDNRedirection redirection = new MSISDNRedirectionDAOJdbc(dao).getOneMSISDNRedirection(productProperties.getSc(), parameters.get("msisdn"));

				if(redirection == null) {
					(new Production()).execute(i18n, productProperties, parameters, modele, dao, request, response);
				}
				else {
					if(redirection.getRedirection_url() != null) {
						(new Development()).execute(redirection.getRedirection_url(), headers, parameters, modele, i18n);
					}
					else {
						(new Development()).execute(new USSDServiceDAOJdbc(dao).getOneUSSDService(productProperties.getSc()).getRedirection(), headers, parameters, modele, i18n);
					}
				}
			}

		} catch(NullPointerException e) {
			modele.put("next", false);
			modele.put("message", i18n.getMessage("error", null, "Desole, veuillez reessayer plus tard...", Locale.FRENCH));

		} catch(Throwable e) {
			modele.put("next", false);
			modele.put("message", i18n.getMessage("error", null, "Desole, veuillez reessayer plus tard...", Locale.FRENCH));
		}

		// on retourne le ModelAndView
		return new ModelAndView(new USSDResponseView(), modele);
	}
}
