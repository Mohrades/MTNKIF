package com.huawei.crbt.server;

import java.util.HashMap;

public class DelInboxTone {

	private SocketConnection connection;

	public DelInboxTone(SocketConnection connection) {
		this.connection = connection;
	}

	public HashMap<String, String> execute(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String personId, String merchantID, String serviceID, String resourceCode, String resourceID, String resourceType) {
		String portalAccount = "admin"; String portalPwd = "Mtn123#$"; portalPwd = "admin";
		String rawResponse = null;

		try {
			if((connection != null) && (connection.isOpen())) {
				String rawQuery = getRequest(portalAccount, portalPwd, portalType, moduleCode, role, roleCode, phoneNumber, personId, merchantID, serviceID, resourceCode, resourceID, resourceType);
				rawResponse = connection.submitRequest("/jboss-net/services/UserToneManage", rawQuery);
			}

		} catch (Throwable th) {
			// TODO Auto-generated catch block

		} finally {
			// fermer la connexion
			if (connection != null) {
				try {
					connection.fermer();
					return parseResponse(rawResponse);

				} catch (Throwable th) {
					// traiter l'exception
				}
			}
		}

		return null;
	}

	public String getRequest(String portalAccount, String portalPwd, String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String personId, String merchantID, String serviceID, String resourceCode, String resourceID, String resourceType) {
		String rawQuery = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://usertonemanage.ivas.huawei.com\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"   <soapenv:Header/>\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <user:delInboxTone soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"         <event xsi:type=\"even:DelInboxToneEvt\" xmlns:even=\"http://event.usertonemanage.ivas.huawei.com\">\r\n" + 
				"            <portalAccount xsi:type=\"xsd:string\">" + portalAccount + "</portalAccount>\r\n" + 
				"            <portalPwd xsi:type=\"xsd:string\">" + portalPwd + "</portalPwd>\r\n" + 
				"            <portalType xsi:type=\"xsd:string\">" + portalType + "</portalType>\r\n" + 
				"            <moduleCode xsi:type=\"xsd:string\">" + moduleCode + "</moduleCode>\r\n" + 
				"            <role xsi:type=\"xsd:string\">" + role + "</role>\r\n" + 
				"            <roleCode xsi:type=\"xsd:string\">" + roleCode + "</roleCode>\r\n" + 
				"            <additionalParamName xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <additionalParamValue xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <contentID xsi:type=\"xsd:string\" xsi:nil=\"true\"></contentID>\r\n" + 
				"            <personId xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((personId == null) ? "" : personId) + "</personId>\r\n" + 
				"            <groupID xsi:type=\"xsd:string\" xsi:nil=\"true\"></groupID>\r\n" + 
				"            <merchantID xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((merchantID == null) ? "" : merchantID) + "</merchantID>\r\n" + 
				"            <partnerID xsi:type=\"xsd:string\" xsi:nil=\"true\"></partnerID>\r\n" + 
				"            <phoneNumber xsi:type=\"xsd:string\">" + phoneNumber + "</phoneNumber>\r\n" + 
				"            <productID xsi:type=\"xsd:string\" xsi:nil=\"true\"></productID>\r\n" + 
				"            <resourceCode xsi:type=\"xsd:string\">" + resourceCode + "</resourceCode>\r\n" + 
				"            <resourceID xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((resourceID == null) ? "" : resourceID) + "</resourceID>\r\n" + 
				"            <resourceType xsi:type=\"xsd:string\">" + resourceType + "</resourceType>\r\n" + 
				"            <serviceID xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((serviceID == null) ? "" : serviceID) + "</serviceID>\r\n" + 
				"            <transactionID xsi:type=\"xsd:string\" xsi:nil=\"true\"></transactionID>\r\n" + 
				"         </event>\r\n" + 
				"      </user:delInboxTone>\r\n" + 
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
