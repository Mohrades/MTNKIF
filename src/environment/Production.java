package environment;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

import dao.DAO;
import handlers.InputHandler;
import product.ProductProperties;

public class Production {

	public Production() {

	}

	public void execute(MessageSource i18n, ProductProperties productProperties, Map<String, String> parameters, Map<String, Object> modele, DAO dao,HttpServletRequest request, HttpServletResponse response) {
		if(parameters.isEmpty()) {
			modele.put("next", false);
			modele.put("message", i18n.getMessage("short.code.live", null, null, Locale.FRENCH));
		}
		else {
			// logging
			Logger logger = LogManager.getLogger("logging.log4j.TracesLogger");
			logger.log(Level.TRACE, "[" + productProperties.getSc() + "] " + "[USSD] " + "[" + parameters.get("msisdn") + "] " + "[" + parameters.get("input").trim() + "]");

			new InputHandler().handle(i18n, productProperties, parameters, modele, request, dao);
		}
	}

}
