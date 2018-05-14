package api;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

public class CallbackDataAndView implements View {

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(Map<String, ?> modele, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

		response.setContentType("text/xml;charset=UTF-8");
		response.setStatus(HttpURLConnection.HTTP_OK);

		// on recupère le flux de sortie
		PrintWriter out = response.getWriter();

		// on prépare les données du flux
		String flux = (String)modele.get("response");

		// on envoie le flux
		out.println(flux);
	}

}