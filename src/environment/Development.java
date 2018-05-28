package environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.context.MessageSource;

public class Development {

	public Development() {

	}

	public void execute(String callbackUrl, Map<String, String> headers, Map<String, String> parameters, Map<String, Object> modele, MessageSource i18n) {
		redirect(i18n, callbackUrl, headers, parameters, modele);
	}

	public void redirect(MessageSource i18n, String callbackUrl, Map<String, String> headers, Map<String, String> parameters, Map<String, Object> modele) {
		// TODO Auto-generated method stub
		HttpURLConnection http = null;

		try {
			URL url = new URL(callbackUrl); // Starting with a URL
			URLConnection con = url.openConnection(); // convert it to a URLConnection using url.openConnection();
			http = (HttpURLConnection)con; // we need to cast it to a HttpURLConnection, so we can access its setRequestMethod() method to set our method
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setDoOutput(true); // We finally say that we are going to send data over the connection.

			// A normal POST coming from a http form has a well defined format. We need to convert our input to this format
			Map<String,String> arguments = new HashMap<>();
			arguments.put("Environment", "Production");
			for(Map.Entry<String,String> entry : parameters.entrySet()) {
				arguments.put(entry.getKey(), entry.getValue());
			}

			StringJoiner sj = new StringJoiner("&");
			for(Map.Entry<String,String> entry : arguments.entrySet()) {
				sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}

			byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			// We can then attach our form contents to the http request with proper headers and send it.			
			http.setFixedLengthStreamingMode(length);
			for(Map.Entry<String,String> entry : headers.entrySet()) {
				http.setRequestProperty(entry.getKey(), entry.getValue());
			}
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();

			try(OutputStream os = http.getOutputStream()) {
			    os.write(out);
				os.flush();
				os.close();
			}

			// Do something with http.getInputStream()
			int responseCode = http.getResponseCode();
			if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_CREATED) || (responseCode == HttpURLConnection.HTTP_ACCEPTED)) {
				BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					if(response.length() == 0) response.append(inputLine);
					else response.append("\n" + inputLine);
				}
				in.close();

				//print result
				try {
			        // List all HTTP Response Headers
			        /*Map<String, List<String>> map = http.getHeaderFields();

			        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			        	System.out.println(entry.getKey() + ": " + entry.getValue());
			        }*/

					// Get HTTP Response by key
					if(http.getHeaderField("Amount") != null) {
						modele.put("amount", http.getHeaderField("Amount"));
					}
					modele.put("next", http.getHeaderField("FreeFlow").equals("FC"));
					modele.put("message", response.toString());

				} catch (NullPointerException|NumberFormatException e) {

				} catch (Throwable e) {

				}
			}

		} catch (MalformedURLException | ProtocolException e) {

		} catch (IOException e) {

		} catch(Throwable th) {

		} finally {
			if(modele.size() == 0) {
				modele.put("next", false);
				modele.put("message", i18n.getMessage("error", null, "Desole, veuillez reessayer plus tard...", Locale.FRENCH));
			}

			try {
				http.disconnect();

			} catch (Exception e) {

			} catch(Throwable th) {

			}
		}
	}

}
