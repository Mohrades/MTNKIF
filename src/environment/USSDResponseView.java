package environment;

import java.net.HttpURLConnection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

public class USSDResponseView implements View {

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(Map<String, ?> modele, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

		// response.setContentType("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2; charset=UTF-8");
		// response.setContentType("text/xml; charset=UTF-8");
		// response.setContentType("text/plain;charset=UTF-8");
		response.setContentType("text/*; charset=UTF-8");

		if(modele.containsKey("next")) {
			try {
				if(((Boolean)modele.get("next")).booleanValue()) response.addHeader("FreeFlow", "FC");
				else response.addHeader("FreeFlow", "FB");

			} catch(Exception e) {
				response.setHeader("FreeFlow", "FC");

			} catch(Throwable e) {
				response.setHeader("FreeFlow", "FC");
			}
		}
		else {
			response.addHeader("FreeFlow", "FC");
		}

		if(modele.containsKey("amount")) {
			response.addHeader("Charge", "Y");

			try {
				response.addHeader("Amount", ((Long)modele.get("nextStep")).longValue() + "");

			} catch(NullPointerException e) {
				response.setHeader("Amount", "0");

			} catch(Exception e) {
				response.setHeader("Amount", "0");

			} catch(Throwable e) {
				response.setHeader("Amount", "0");
			}
		}

		// response.setHeader("Cache-Control", "no-cache");

		response.setStatus(HttpURLConnection.HTTP_OK);

		// on prépare les données du flux
		if(modele.containsKey("message")) {
			String flux = (String)modele.get("message");

			// on recupère le flux de sortie, puis on envoie le flux
			response.getWriter().write(flux);
		}
	}
}