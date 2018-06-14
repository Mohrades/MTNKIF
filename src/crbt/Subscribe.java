package crbt;

import java.util.HashMap;

public class Subscribe {

	private String ip;
	private int port, sleep, timeout;

	public Subscribe(String host, int sleep, int timeout) {
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
				"      <user:subscribe soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"         <event xsi:type=\"even:SubscribeEvt\" xmlns:even=\"http://event.usermanage.ivas.huawei.com\">\r\n" + 
				"            <portalAccount xsi:type=\"xsd:string\">" + portalAccount + "</portalAccount>\r\n" + 
				"            <portalPwd xsi:type=\"xsd:string\">" + portalPwd + "</portalPwd>\r\n" + 
				"            <portalType xsi:type=\"xsd:string\">" + portalType + "</portalType>\r\n" + 
				"            <moduleCode xsi:type=\"xsd:string\">" + moduleCode + "</moduleCode>\r\n" + 
				"            <role xsi:type=\"xsd:string\">" + role + "</role>\r\n" + 
				"            <roleCode xsi:type=\"xsd:string\">" + roleCode + "</roleCode>\r\n" + 
				"            <acceptedID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <additionalParamName xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <additionalParamValue xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <categoryId xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <city xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <couponCode xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <dateOfBirth xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <discountDuration xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <discountPrice xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <discountTime xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <discountUnit xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <email xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <firstName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <groupID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <keyword xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <languageID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <lastName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <microChargingFlag xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <nationality xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <notifyHLR xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"\r\n" + 
				"            <phoneNumber xsi:type=\"xsd:string\">" + phoneNumber + "</phoneNumber>\r\n" + 
				"\r\n" + 
				"            <pwd xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <recommendNumber xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <renewalMode xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <resourceCode xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <resourceID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <resourceServiceType xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <resourceType xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <serviceID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <sex xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <smAccessCode xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <toneDiscount xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <tonePrice xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <toneValidDuration xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <traceUniqueID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <tradeMark xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <transactionID xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <triggerKey xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <triggerPhonenumber xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <triggerType xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <userType xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"            <validateCode xsi:type=\"xsd:string\" xsi:nil=\"true\"/>\r\n" + 
				"         </event>\r\n" + 
				"      </user:subscribe>\r\n" + 
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
