package tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SMPPConnector {

	public SMPPConnector() {

	}

	public void submitSm(String senderName, String subscriber, String message) {
		HttpURLConnection http = null;

		try {
			URL url = new URL("http://10.77.73.16:9084/moseadmin/service?keyword=dispatcher&MSISDN=" + URLEncoder.encode(subscriber, "UTF-8") + "&ESMEADDRESS=" + URLEncoder.encode(senderName, "UTF-8") + "&applicationId=$G&channelId=$n&message=dispatcher+9922+" + URLEncoder.encode(message, "UTF-8"));
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("GET");

			InputStream is = http.getInputStream();
			// Do what you want with that stream
			is.close();

		} catch (MalformedURLException | ProtocolException e) {
			// TODO Auto-generated catch block

		} catch (IOException e) {
			// TODO Auto-generated catch block

		} catch (Throwable e) {
			// TODO Auto-generated catch block

		} finally {
			try {

			} catch (NullPointerException e) {

			} catch (Exception e) {

			} catch(Throwable th) {

			}
		}
	}
}
