package crbt;

import java.util.HashMap;

public class OrderTone {

	private String ip;
	private int port, sleep, timeout;

	public OrderTone(String host, int sleep, int timeout) {
		int separator = host.indexOf(":");

		ip = host.substring(0, separator).trim();
		port = Integer.parseInt((host.substring(separator+1)).trim());

		this.sleep = sleep;
		this.timeout = timeout;
	}

	public HashMap<String, String> execute(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String recommendNumber, String resourceCode, String resourceType, String discount, String reorderMode, String smAccessCode, boolean waitingForResponse) {
		SocketConnection connection;
		String portalAccount = "admin"; String portalPwd = "Mtn123#$"; portalPwd = "admin";

		try {
			connection = new SocketConnection(ip, port, waitingForResponse ? sleep : 0, timeout);

			if(connection.isOpen()) {
				String rawQuery = getRequest(portalAccount, portalPwd, portalType, moduleCode, role, roleCode, phoneNumber, recommendNumber, resourceCode, resourceType, discount, reorderMode, smAccessCode);
				String rawResponse = connection.submitRequest("/jboss-net/services/UserToneManage", rawQuery);
				connection.fermer();

				return parseResponse(rawResponse);
			}

		} catch (Throwable th) {
			// TODO Auto-generated catch block

		}

		return null;
	}

	public String getRequest(String portalAccount, String portalPwd, String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String recommendNumber, String resourceCode, String resourceType, String discount, String reorderMode, String smAccessCode) {
		String rawQuery = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://usertonemanage.ivas.huawei.com\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"   <soapenv:Header/>\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <user:orderTone soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"         <event xsi:type=\"even:OrderToneEvt\" xmlns:even=\"http://event.usertonemanage.ivas.huawei.com\">\r\n" + 
				"            <portalAccount xsi:type=\"xsd:string\">" + portalAccount + "</portalAccount>\r\n" + 
				"            <portalPwd xsi:type=\"xsd:string\">" + portalPwd + "</portalPwd>\r\n" + 
				"            <portalType xsi:type=\"xsd:string\">" + portalType + "</portalType>\r\n" + 
				"            <moduleCode xsi:type=\"xsd:string\">" + moduleCode + "</moduleCode>\r\n" + 
				"            <role xsi:type=\"xsd:string\">" + role + "</role>\r\n" + 
				"            <roleCode xsi:type=\"xsd:string\">" + roleCode + "</roleCode>\r\n" + 
				"            <additionalParamName xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <additionalParamValue xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <categoryId xsi:type=\"xsd:string\" xsi:nil=\"true\"></categoryId>\r\n" + 
				"            <chargeFeeMode xsi:type=\"xsd:string\" xsi:nil=\"true\"></chargeFeeMode>\r\n" + 
				"            <contentID xsi:type=\"xsd:string\" xsi:nil=\"true\"></contentID>\r\n" + 
				"            <couponCode xsi:type=\"xsd:string\" xsi:nil=\"true\"></couponCode>\r\n" + 
				/*"            <discount xsi:type=\"xsd:string\">0</discount>\r\n" + 
				"            <discount xsi:type=\"xsd:string\" xsi:nil=\"true\">0</discount>\r\n" + */
				"            <discount xsi:type=\"xsd:string\"" + ((discount == null) ? " xsi:nil=\"true\"" : "") + ">" + ((discount == null) ? "" : discount) + "</discount>\r\n" + 
				"            <fromFlag xsi:type=\"xsd:string\" xsi:nil=\"true\"></fromFlag>\r\n" + 
				"            <groupID xsi:type=\"xsd:string\" xsi:nil=\"true\"></groupID>\r\n" + 
				"            <keyword xsi:type=\"xsd:string\" xsi:nil=\"true\"></keyword>\r\n" + 
				"            <merchantID xsi:type=\"xsd:string\" xsi:nil=\"true\"></merchantID>\r\n" + 
				"            <partnerID xsi:type=\"xsd:string\" xsi:nil=\"true\"></partnerID>\r\n" + 
				"            <passID xsi:type=\"xsd:string\" xsi:nil=\"true\"></passID>\r\n" + 
				"            <phoneNumber xsi:type=\"xsd:string\">" + phoneNumber + "</phoneNumber>\r\n" + 
				"            <price xsi:type=\"xsd:string\" xsi:nil=\"true\"></price>\r\n" + 
				"            <productID xsi:type=\"xsd:string\" xsi:nil=\"true\"></productID>\r\n" + 
				"            <rankType xsi:type=\"xsd:string\" xsi:nil=\"true\"></rankType>\r\n" + 
				"            <recommendNumber xsi:type=\"xsd:string\">" + recommendNumber + "</recommendNumber>\r\n" + 
				/*"            <reorderMode xsi:type=\"xsd:string\" xsi:nil=\"true\"></reorderMode>\r\n" + */
				"            <reorderMode xsi:type=\"xsd:string\"" + ((reorderMode == null) ? " xsi:nil=\"true\"" : "") + ">" + ((reorderMode == null) ? "" : reorderMode) + "</reorderMode>\r\n" + 
				"            <resourceCode xsi:type=\"xsd:string\">" + resourceCode + "</resourceCode>\r\n" + 
				"            <resourceID xsi:type=\"xsd:string\" xsi:nil=\"true\"></resourceID>\r\n" + 
				"            <resourceType xsi:type=\"xsd:string\">" + resourceType + "</resourceType>\r\n" + 
				"            <serviceID xsi:type=\"xsd:string\" xsi:nil=\"true\"></serviceID>\r\n" + 
				"            <smAccessCode xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((smAccessCode == null) ? "" : smAccessCode) + "</smAccessCode>\r\n" + 
				"            <supportDailyCharging xsi:type=\"xsd:string\" xsi:nil=\"true\"></supportDailyCharging>\r\n" + 
				"            <toneValidDay xsi:type=\"xsd:string\" xsi:nil=\"true\"></toneValidDay>\r\n" + 
				"            <traceUniqueID xsi:type=\"xsd:string\" xsi:nil=\"true\"></traceUniqueID>\r\n" + 
				"            <tradeID xsi:type=\"xsd:string\" xsi:nil=\"true\"></tradeID>\r\n" + 
				"            <transactionID xsi:type=\"xsd:string\" xsi:nil=\"true\"></transactionID>\r\n" + 
				"            <useMonPackSerQuota xsi:type=\"xsd:string\" xsi:nil=\"true\"></useMonPackSerQuota>\r\n" + 
				"            <validDuration xsi:type=\"xsd:string\" xsi:nil=\"true\"></validDuration>\r\n" + 
				"            <voucherID xsi:type=\"xsd:string\" xsi:nil=\"true\"></voucherID>\r\n" + 
				"         </event>\r\n" + 
				"      </user:orderTone>\r\n" + 
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
