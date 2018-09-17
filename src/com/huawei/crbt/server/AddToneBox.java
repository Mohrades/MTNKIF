package com.huawei.crbt.server;

import java.util.HashMap;

public class AddToneBox {

	private SocketConnection connection;

	public AddToneBox(SocketConnection connection) {
		this.connection = connection;
	}

	public HashMap<String, String> execute(String portalType, String moduleCode, String role, String roleCode, String loopToneType, String name, String toneBoxCode, String[] toneCodes, String[] toneIDs, String[] toneTypes, String type, String feeType, String price, String phoneNumber) {
		String portalAccount = "admin"; String portalPwd = "Mtn123#$"; portalPwd = "admin";
		String rawResponse = null;

		try {
			if((connection != null) && (connection.isOpen())) {
				String rawQuery = getRequest(portalAccount, portalPwd, portalType, moduleCode, role, roleCode, loopToneType, name, toneBoxCode, toneCodes, toneIDs, toneTypes, type,feeType, price, phoneNumber);
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

	public String getRequest(String portalAccount, String portalPwd, String portalType, String moduleCode, String role, String roleCode, String loopToneType, String name, String toneBoxCode, String[] toneCodes, String[] toneIDs, String[] toneTypes, String type, String feeType, String price, String phoneNumber) {
		String tone_types = "";
		String tone_ids = "";
		String tone_codes = "";

		if(toneIDs != null) {
			for(String toneID : toneIDs) {
				if(tone_ids.isEmpty()) {
					tone_ids = "<toneID xsi:type=\"xsd:string\">" + toneID + "</toneID>\r\n";
				}
				else {
					tone_ids += "<toneID xsi:type=\"xsd:string\">" + toneID + "</toneID>\r\n"; 
				}
			}			
		}
		if(toneCodes != null) {
			for(String toneCode : toneCodes) {
				if(tone_codes.isEmpty()) {
					tone_codes = "<toneCode xsi:type=\"xsd:string\">" + toneCode + "</toneCode>\r\n";
				}
				else {
					tone_codes += "<toneCode xsi:type=\"xsd:string\">" + toneCode + "</toneCode>\r\n"; 
				}
			}			
		}
		if(toneTypes != null) {
			for(String toneType : toneTypes) {
				if(tone_codes.isEmpty()) {
					tone_types = "<toneType xsi:type=\"xsd:string\">" + toneType + "</toneType>\r\n";
				}
				else {
					tone_types += "<toneType xsi:type=\"xsd:string\">" + toneType + "</toneType>\r\n"; 
				}
			}			
		}

		String rawQuery = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://usertonemanage.ivas.huawei.com\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"   <soapenv:Header/>\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <user:addToneBox soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" + 
				"         <event xsi:type=\"even:AddToneBoxEvt\" xmlns:even=\"http://event.usertonemanage.ivas.huawei.com\">\r\n" + 
				"            <portalAccount xsi:type=\"xsd:string\">" + portalAccount + "</portalAccount>\r\n" + 
				"            <portalPwd xsi:type=\"xsd:string\">" + portalPwd + "</portalPwd>\r\n" + 
				"            <portalType xsi:type=\"xsd:string\">" + portalType + "</portalType>\r\n" + 
				"            <moduleCode xsi:type=\"xsd:string\">" + moduleCode + "</moduleCode>\r\n" + 
				"            <role xsi:type=\"xsd:string\">" + role + "</role>\r\n" + 
				"            <roleCode xsi:type=\"xsd:string\">" + roleCode + "</roleCode>\r\n" + 
				"            <aggregatorShare xsi:type=\"xsd:string\" xsi:nil=\"true\"></aggregatorShare>\r\n" + 
				"            <allowedChannel xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <canSplit xsi:type=\"xsd:string\" xsi:nil=\"true\"></canSplit>\r\n" + 
				"            <canUpdate xsi:type=\"xsd:string\" xsi:nil=\"true\"></canUpdate>\r\n" + 
				"            <catalogID xsi:type=\"xsd:string\" xsi:nil=\"true\"></catalogID>\r\n" + 
				"            <cpRevSharePercentage xsi:type=\"xsd:string\" xsi:nil=\"true\"></cpRevSharePercentage>\r\n" + 
				"            <cpType xsi:type=\"xsd:string\" xsi:nil=\"true\"></cpType>\r\n" + 
				"            <description xsi:type=\"xsd:string\" xsi:nil=\"true\"></description>\r\n" + 
				"            <enabledDate xsi:type=\"xsd:string\" xsi:nil=\"true\"></enabledDate>\r\n" + 
				"            <feeType xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((feeType == null) ? "" : feeType) + "</feeType>\r\n" + 
				"            <isMixMusicBox xsi:type=\"xsd:string\" xsi:nil=\"true\"></isMixMusicBox>\r\n" + 
				"            <languageID xsi:type=\"xsd:string\" xsi:nil=\"true\"></languageID>\r\n" + 
				"            <loopToneType xsi:type=\"xsd:string\" >" + loopToneType + "</loopToneType>\r\n" + 
				"            <maxDownloadLimit xsi:type=\"xsd:string\" xsi:nil=\"true\"></maxDownloadLimit>\r\n" + 
				"            <name xsi:type=\"xsd:string\">" + name + "</name>\r\n" + 
				"            <objectCode xsi:type=\"xsd:string\" xsi:nil=\"true\"></objectCode>\r\n" + 
				"            <objectRole xsi:type=\"xsd:string\" xsi:nil=\"true\"></objectRole>\r\n" + 
				"            <parentToneboxId xsi:type=\"xsd:string\" xsi:nil=\"true\">"  + name + "</parentToneboxId>\r\n" + 
				"            <price xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((price == null) ? "" : price) + "</price>\r\n" + 
				"            <priceGroupID xsi:type=\"xsd:string\" xsi:nil=\"true\"></priceGroupID>\r\n" + 
				"            <relativeTime xsi:type=\"xsd:string\" xsi:nil=\"true\"></relativeTime>\r\n" + 
				"            <renewFlag xsi:type=\"xsd:string\" xsi:nil=\"true\"></renewFlag>\r\n" + 
				"            <serviceID xsi:type=\"xsd:string\" xsi:nil=\"true\"></serviceID>\r\n" + 
				"            <toneBoxCode xsi:type=\"xsd:string\" xsi:nil=\"true\">" + ((toneBoxCode == null) ? "" : toneBoxCode) + "</toneBoxCode>\r\n" + 
				"            <toneCode xsi:type=\"soapenc:Array\" soapenc:arrayType=\"xsd:string[" + (((toneCodes == null) || (toneCodes.length == 0)) ? "" : toneCodes.length) + "]\"" + (((toneCodes == null) || (toneCodes.length == 0)) ? " xsi:nil=\"true\"" : "") + ">\r\n" + 
								tone_codes + 
				"			 </toneCode>\r\n" +
				"            <toneEndOffsets xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <toneFileNames xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"			 <toneID soapenc:arrayType=\"xsd:string[" + (((toneIDs == null) || (toneIDs.length == 0)) ? "" : toneIDs.length) + "]\" xsi:type=\"soapenc:Array\"" + (((toneIDs == null) || (toneIDs.length == 0)) ? " xsi:nil=\"true\"" : "") + ">\r\n" + 
								tone_ids + 
				"			 </toneID>\r\n" + 
				"            <toneOffsets xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[]\" xsi:nil=\"true\"/>\r\n" + 
				"            <toneType xsi:type=\"user:ArrayOf_xsd_string\" soapenc:arrayType=\"xsd:string[" + (((toneTypes == null) || (toneTypes.length == 0)) ? "" : toneTypes.length) + "]\"" + (((toneTypes == null) || (toneTypes.length == 0)) ? " xsi:nil=\"true\"" : "") + ">\r\n" + 
								tone_types + 
				"			 </toneType>\r\n" +
				"            <toneValidDay xsi:type=\"xsd:string\" xsi:nil=\"true\"></toneValidDay>\r\n" + 
				"            <transactionID xsi:type=\"xsd:string\" xsi:nil=\"true\"></transactionID>\r\n" + 
				"            <type xsi:type=\"xsd:string\" xsi:nil=\"true\"> " + type + "</type>\r\n" + 
				"            <uploadType xsi:type=\"xsd:string\" xsi:nil=\"true\"></uploadType>\r\n" + 
				"            <wapImagePath xsi:type=\"xsd:string\" xsi:nil=\"true\"></wapImagePath>\r\n" + 
				"            <webImagePath xsi:type=\"xsd:string\" xsi:nil=\"true\"></webImagePath>\r\n" + 
				"         </event>\r\n" + 
				"      </user:addToneBox>\r\n" + 
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
        if((rawResponse.contains("<toneBoxCode")) && (rawResponse.contains("</toneBoxCode>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<toneBoxCode"), rawResponse.indexOf("</toneBoxCode>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("toneBoxCode", souschaine.substring(last + 1).trim());
        }
        if((rawResponse.contains("<toneBoxID")) && (rawResponse.contains("</toneBoxID>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<toneBoxID"), rawResponse.indexOf("</toneBoxID>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("toneBoxID", souschaine.substring(last + 1).trim());
        }
        if((rawResponse.contains("<transactionID")) && (rawResponse.contains("</transactionID>"))) {
        	String souschaine = rawResponse.substring(rawResponse.indexOf("<transactionID"), rawResponse.indexOf("</transactionID>"));
        	int last = souschaine.lastIndexOf(">");
        	response.put("transactionID", souschaine.substring(last + 1).trim());
        }

		return response.isEmpty() ? null : response;
	}

}
