package crbt;

import java.util.HashMap;

public class SetTone {

	private String ip;
	private int port, sleep, timeout;

	public SetTone(String host, int sleep, int timeout) {
		int separator = host.indexOf(":");

		ip = host.substring(0, separator).trim();
		port = Integer.parseInt((host.substring(separator+1)).trim());

		this.sleep = sleep;
		this.timeout = timeout;
	}

	public HashMap<String, String> execute(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String calledUserID, String calledUserType, String callerNumber, String endTime, String loopType, String resourceType, String mode, String type, String startTime, String timeType, String toneBoxID, String toneType, boolean waitingForResponse) {
		SocketConnection connection;
		String portalAccount = "admin"; String portalPwd = "Mtn123#$"; portalPwd = "admin";

		try {
			connection = new SocketConnection(ip, port, waitingForResponse ? sleep : 0, timeout);

			if(connection.isOpen()) {
				String rawQuery = getRequest(portalAccount, portalPwd, portalType, moduleCode, role, roleCode, phoneNumber, calledUserID, calledUserType, callerNumber, endTime, loopType, resourceType, mode, type, startTime, timeType, toneBoxID, toneType);
				String rawResponse = connection.submitRequest("/jboss-net/services/UserToneManage", rawQuery);
				connection.fermer();

				return parseResponse(rawResponse);
			}

		} catch (Throwable th) {
			// TODO Auto-generated catch block

		}

		return null;
	}

	public String getRequest(String portalAccount, String portalPwd, String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String calledUserID, String calledUserType, String callerNumber, String endTime, String loopType, String resourceType, String mode, String type, String startTime, String timeType, String toneBoxID, String toneType) {
		String rawQuery = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://usertonemanage.ivas.huawei.com\">\r\n" + 
				"   <soapenv:Header/>\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <user:setTone soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"         <event xsi:type=\"even:SetToneEvt\" xmlns:even=\"http://event.usertonemanage.ivas.huawei.com\">\r\n" + 
				"            <portalAccount xsi:type=\"xsd:string\">" + portalAccount + "</portalAccount>\r\n" + 
				"            <portalPwd xsi:type=\"xsd:string\">" + portalPwd + "</portalPwd>\r\n" + 
				"            <portalType xsi:type=\"xsd:string\">" + portalType + "</portalType>\r\n" + 
				"            <moduleCode xsi:type=\"xsd:string\">" + moduleCode + "</moduleCode>\r\n" + 
				"            <role xsi:type=\"xsd:string\">" + role + "</role>\r\n" + 
				"            <roleCode xsi:type=\"xsd:string\">" + roleCode + "</roleCode>\r\n" + 
				"            <calledUserID xsi:type=\"xsd:string\">" + ((calledUserID == null) ? "" : calledUserID) + "</calledUserID>\r\n" + 
				"            <calledUserType xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((calledUserType == null) ? "" : calledUserType) + "</calledUserType>\r\n" + 
				"            <callerNumber xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((callerNumber == null) ? "" : callerNumber) + "</callerNumber>\r\n" + 
				"            <description xsi:type=\"xsd:string\" xsi:nil=\"true\"></description>\r\n" + 
				"            <endTime xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((endTime == null) ? "" : endTime) + "</endTime>\r\n" + 
				"            <loopType xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((loopType == null) ? "" : loopType) + "</loopType>\r\n" + 
				"            <moodModeID xsi:type=\"xsd:string\" xsi:nil=\"true\"></moodModeID>\r\n" + 
				"            <overlayFlag xsi:type=\"xsd:string\" xsi:nil=\"true\"></overlayFlag>\r\n" + 
				"            <phoneState xsi:type=\"xsd:string\" xsi:nil=\"true\"></phoneState>\r\n" + 
				"            <productID xsi:type=\"xsd:string\" xsi:nil=\"true\"></productID>\r\n" + 
				"            <resourceType xsi:type=\"xsd:string\">" + resourceType + "</resourceType>\r\n" + 
				"            <setMode xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((mode == null) ? "" : mode) + "</setMode>\r\n" + 
				"            <setType xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((type == null) ? "" : type) + "</setType>\r\n" + 
				"            <startTime xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((startTime == null) ? "" : startTime) + "</startTime>\r\n" + 
				"            <timeType xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((timeType == null) ? "" : timeType) + "</timeType>\r\n" + 
				"            <toneBoxID xsi:type=\"xsd:string\">" + toneBoxID + "</toneBoxID>\r\n" + 
				"            <toneType xsi:type=\"xsd:string\" xsi:nil=\"true\">" + toneType + "</toneType>\r\n" + 
				"            <transactionID xsi:type=\"xsd:string\" xsi:nil=\"true\"></transactionID>\r\n" + 
				"         </event>\r\n" + 
				"      </user:setTone>\r\n" + 
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
        if((rawResponse.contains("<settingID")) && (rawResponse.contains("</settingID>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<settingID"), rawResponse.indexOf("</settingID>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("settingID", souschaine.substring(last + 1).trim());
        }
        if((rawResponse.contains("<transactionID")) && (rawResponse.contains("</transactionID>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<transactionID"), rawResponse.indexOf("</transactionID>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("transactionID", souschaine.substring(last + 1).trim());
        }

		return response.isEmpty() ? null : response;
	}

}
