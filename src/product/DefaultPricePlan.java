package product;

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
import java.util.Map;
import java.util.StringJoiner;

public class DefaultPricePlan {

	public DefaultPricePlan() {

	}

	public int requestDefaultPricePlanSubscription(ProductProperties productProperties, String msisdn, String action, String originOperatorID) {
		// TODO Auto-generated method stub
		HttpURLConnection http = null;
		int statusCode = -1;

		try {
			URL url = new URL(productProperties.getDefault_price_plan_url() + "/api/subscription/" + msisdn); // Starting with a URL
			URLConnection con = url.openConnection(); // convert it to a URLConnection using url.openConnection();
			http = (HttpURLConnection)con; // we need to cast it to a HttpURLConnection, so we can access its setRequestMethod() method to set our method
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setDoOutput(true); // We finally say that we are going to send data over the connection.

			// A normal POST coming from a http form has a well defined format. We need to convert our input to this format
			Map<String,String> arguments = new HashMap<>();
			arguments.put("action", action);
			arguments.put("msisdn", msisdn);
			arguments.put("authentication", "true");
			arguments.put("originOperatorID", originOperatorID);

			StringJoiner sj = new StringJoiner("&");
			for(Map.Entry<String,String> entry : arguments.entrySet()) {
				sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}

			byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			// We can then attach our form contents to the http request with proper headers and send it.
			http.setFixedLengthStreamingMode(length);
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
					response.append(inputLine);
				}
				in.close();

				// print result
				try {
					String XMLResponse = response.toString();
					statusCode = Integer.parseInt(XMLResponse.substring(XMLResponse.indexOf("<statusCode>") + 12, XMLResponse.indexOf("</statusCode>")));

				} catch (NullPointerException|NumberFormatException e) {

				} catch (Throwable e) {

				}
			}

		} catch (MalformedURLException | ProtocolException e) {

		} catch (IOException e) {

		} catch(Throwable th) {

		} finally {
			try {
				http.disconnect();

			} catch (Exception e) {

			} catch(Throwable th) {

			}
		}

		return statusCode;
	}

	public int requestDefaultPricePlanStatus(ProductProperties productProperties, String msisdn, String originOperatorID) {
		// TODO Auto-generated method stub
		HttpURLConnection http = null;
		int statusCode = -1;

		try {
			URL url = new URL(productProperties.getDefault_price_plan_url() + "/api/status"); // Starting with a URL
			URLConnection con = url.openConnection(); // convert it to a URLConnection using url.openConnection();
			http = (HttpURLConnection)con; // we need to cast it to a HttpURLConnection, so we can access its setRequestMethod() method to set our method
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setDoOutput(true); // We finally say that we are going to send data over the connection.

			// A normal POST coming from a http form has a well defined format. We need to convert our input to this format
			Map<String,String> arguments = new HashMap<>();
			arguments.put("msisdn", msisdn);
			arguments.put("authentication", "true");
			arguments.put("originOperatorID", originOperatorID);

			StringJoiner sj = new StringJoiner("&");
			for(Map.Entry<String,String> entry : arguments.entrySet()) {
				sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}

			byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			// We can then attach our form contents to the http request with proper headers and send it.
			http.setFixedLengthStreamingMode(length);
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
					response.append(inputLine);
				}
				in.close();

				// print result
				try {
					String XMLResponse = response.toString();
					statusCode = Integer.parseInt(XMLResponse.substring(XMLResponse.indexOf("<statusCode>") + 12, XMLResponse.indexOf("</statusCode>")));

				} catch (NullPointerException|NumberFormatException e) {

				} catch (Throwable e) {

				}
			}

		} catch (MalformedURLException | ProtocolException e) {

		} catch (IOException e) {

		} catch(Throwable th) {

		} finally {
			try {
				http.disconnect();

			} catch (Exception e) {

			} catch(Throwable th) {

			}
		}

		return statusCode;
	}

}
