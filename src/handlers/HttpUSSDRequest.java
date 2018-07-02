package handlers;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcUSSDRequestDao;
import domain.models.USSDRequest;
import util.BalanceAndDate;

@SuppressWarnings("unused")
public class HttpUSSDRequest {

	public HttpUSSDRequest() {

	}

	public Map<String, String> GetHeaders(HttpServletRequest request) {
		HashMap<String, String> modele = new HashMap<String, String>();

		try {
			Enumeration<String> entetes = request.getHeaderNames();

		    while (entetes.hasMoreElements()) {
		    	String entete = entetes.nextElement();
		    	modele.put(entete, request.getHeader(entete));
		    }

		} catch(Throwable th) {

		}

	    return modele;
	}

	public Map<String, String> GetParameters(HttpServletRequest request, boolean parseURL) {
		HashMap<String, String> modele = new HashMap<String, String>();

		try {
			if(parseURL) {
				// request.getQueryString() : is null
				String url = request.getRequestURL().toString(); // http://10.77.73.244:8080/testapp/imput=250&sessionid=15218157004181684&msisdn=22962893693&transactionid=0442715218157006493		
				String queryString = url.substring(url.lastIndexOf("/") + 1);

				List<String> parameters = Splitter.onPattern("[&]").trimResults().omitEmptyStrings().splitToList(queryString);
				// List<String> attributes = Arrays.asList(parameters.split("\\s*&\\s*"));
				for(String parameter : parameters) {
					List<String> result = Splitter.onPattern("[=]").trimResults().omitEmptyStrings().splitToList(parameter);

					if(result.size() == 2) {
						modele.put(result.get(0), result.get(1));

					} else if(result.size() == 1) {
						modele.put(result.get(0), null);

					}
				}
			}
			else {
			    Enumeration<String> parameters = request.getParameterNames();

			    while (parameters.hasMoreElements()) {
			        String parameter = parameters.nextElement();
			        modele.put(parameter, request.getParameter(parameter));
			    }
			}

		} catch(Throwable th) {

		}

		return modele;
	}

}