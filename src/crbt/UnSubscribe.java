package crbt;

import java.util.HashMap;

public class UnSubscribe {

	private String ip;
	private int port, sleep, timeout;

	public UnSubscribe(String host, int sleep, int timeout) {
		int separator = host.indexOf(":");

		ip = host.substring(0, separator).trim();
		port = Integer.parseInt((host.substring(separator+1)).trim());

		this.sleep = sleep;
		this.timeout = timeout;
	}

	public HashMap<String, String> execute(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, boolean waitingForResponse) {
		SocketConnection connection;
		String portalAccount = "admin"; String portalPwd = "Mtn123#$"; portalPwd = "admin";

		try {
			connection = new SocketConnection(ip, port, waitingForResponse ? sleep : 0, timeout);

			if(connection.isOpen()) {
				String rawQuery = getRequest(portalAccount, portalPwd, portalType, moduleCode, role, roleCode, phoneNumber);
				String rawResponse = connection.submitRequest("/jboss-net/services/UserManage", rawQuery);
				connection.fermer();

				return parseResponse(rawResponse);
			}

		} catch (Throwable th) {
			// TODO Auto-generated catch block

		}

		return null;
	}

	public String getRequest(String portalAccount, String portalPwd, String portalType, String moduleCode, String role, String roleCode, String phoneNumber) {
		String rawQuery = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://usermanage.ivas.huawei.com\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"   <soapenv:Header/>\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <user:unSubscribe soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"         <event xsi:type=\"even:UnSubscribeEvt\" xmlns:even=\"http://event.usermanage.ivas.huawei.com\">\r\n" + 
				"            <portalAccount xsi:type=\"xsd:string\">" + portalAccount + "</portalAccount>\r\n" + 
				"            <portalPwd xsi:type=\"xsd:string\">" + portalPwd + "</portalPwd>\r\n" + 
				"            <portalType xsi:type=\"xsd:string\">" + portalType + "</portalType>\r\n" + 
				"            <moduleCode xsi:type=\"xsd:string\">" + moduleCode + "</moduleCode>\r\n" + 
				"            <role xsi:type=\"xsd:string\">" + role + "</role>\r\n" + 
				"            <roleCode xsi:type=\"xsd:string\">" + roleCode + "</roleCode>\r\n" + 
				"            <additionalParamName xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\"  xsi:nil=\"true\"/>\r\n" + 
				"            <additionalParamValue xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\"  xsi:nil=\"true\"/>\r\n" + 
				"            <deleteFlag xsi:type=\"xsd:string\" xsi:nil=\"true\"></deleteFlag>\r\n" + 
				"            <notifyHLR xsi:type=\"xsd:string\" xsi:nil=\"true\"></notifyHLR>\r\n" + 
				"            <phoneNumber xsi:type=\"xsd:string\">" + phoneNumber + "</phoneNumber>\r\n" +
				"            <serviceFlag xsi:type=\"xsd:string\" xsi:nil=\"true\"></serviceFlag>\r\n" +
				"            <traceUniqueID xsi:type=\"xsd:string\" xsi:nil=\"true\"></traceUniqueID>\r\n" + 
				"            <transactionID xsi:type=\"xsd:string\" xsi:nil=\"true\"></transactionID>\r\n" + 
				"         </event>\r\n" + 
				"      </user:unSubscribe>\r\n" + 
				"   </soapenv:Body>\r\n" + 
				"</soapenv:Envelope>";

		return rawQuery;
	}

	public HashMap<String, String> parseResponse(String rawResponse) {
		HashMap<String, String> response = new HashMap<String, String>();

        if((rawResponse.contains("<operationID")) && (rawResponse.contains("</operationID>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<operationID"), rawResponse.indexOf("</operationID>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("operationID", souschaine.substring(last + 1).trim());
        }
        if((rawResponse.contains("<resultCode")) && (rawResponse.contains("</resultCode>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<resultCode"), rawResponse.indexOf("</resultCode>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("resultCode", souschaine.substring(last + 1).trim());
        }
        if((rawResponse.contains("<resultInfo")) && (rawResponse.contains("</resultInfo>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<resultInfo"), rawResponse.indexOf("</resultInfo>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("resultInfo", souschaine.substring(last + 1).trim());
        }
        if((rawResponse.contains("<returnCode")) && (rawResponse.contains("</returnCode>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<returnCode"), rawResponse.indexOf("</returnCode>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("returnCode", souschaine.substring(last + 1).trim());
        }
        if((rawResponse.contains("<transactionID")) && (rawResponse.contains("</transactionID>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<transactionID"), rawResponse.indexOf("</transactionID>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("transactionID", souschaine.substring(last + 1).trim());
        }

		return response.isEmpty() ? null : response;
	}

}
