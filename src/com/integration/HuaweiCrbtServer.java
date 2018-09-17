package com.integration;

import java.util.HashMap;

import com.huawei.crbt.server.AddToneBox;
import com.huawei.crbt.server.DelInboxTone;
import com.huawei.crbt.server.OrderTone;
import com.huawei.crbt.server.QueryInboxTone;
import com.huawei.crbt.server.QueryUser;
import com.huawei.crbt.server.SetTone;
import com.huawei.crbt.server.SocketConnection;
import com.huawei.crbt.server.Subscribe;
import com.huawei.crbt.server.UnSubscribe;

public class HuaweiCrbtServer {

	private String ip;
	private int port, sleep, timeout;

	public HuaweiCrbtServer() {

	}

	public HuaweiCrbtServer(String host, int sleep, int timeout) {
		try {
			int separator = host.indexOf(":");

			ip = host.substring(0, separator).trim();
			port = Integer.parseInt((host.substring(separator+1)).trim());

			this.sleep = sleep;
			this.timeout = timeout;

		} catch (Throwable th) {
			// TODO Auto-generated catch block
		}
	}

	private SocketConnection connection() {
		try {
			return new SocketConnection(ip, port, sleep, timeout);

		} catch (Throwable th) {
			// TODO Auto-generated catch block
		}

		return null;
	}

	public HashMap<String, String> addToneBox(String portalType, String moduleCode, String role, String roleCode, String loopToneType, String name, String toneBoxCode, String[] toneCodes, String[] toneIDs, String[] toneTypes, String type, String feeType, String price, String phoneNumber) {
		return new AddToneBox(connection()).execute(portalType, moduleCode, role, roleCode, loopToneType, name, toneBoxCode, toneCodes, toneIDs, toneTypes, type, feeType, price, phoneNumber);
	}
	
	public HashMap<String, String> delInboxTone(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String personId, String merchantID, String serviceID, String resourceCode, String resourceID, String resourceType) {
		return new DelInboxTone(connection()).execute(portalType, moduleCode, role, roleCode, phoneNumber, personId, merchantID, serviceID, resourceCode, resourceID, resourceType);
	}
	
	public HashMap<String, String> orderTone(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String recommendNumber, String resourceCode, String resourceType, String discount, String reorderMode, String smAccessCode) {
		return new OrderTone(connection()).execute(portalType, moduleCode, role, roleCode, phoneNumber, recommendNumber, resourceCode, resourceType, discount, reorderMode, smAccessCode);
	}
	
	public HashMap<String, String> queryInboxTone(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String startRecordNum, String endRecordNum, String queryType, String merchantID, String serviceID, String resourceCode, String resourceID, String resourceType, String status, String orderType, String orderBy, String smAccessCode) {
		return new QueryInboxTone(connection()).execute(portalType, moduleCode, role, roleCode, phoneNumber, startRecordNum, endRecordNum, queryType, merchantID, serviceID, resourceCode, resourceID, resourceType, status, orderType, orderBy, smAccessCode);
	}
	
	public HashMap<String, String> queryUser(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String startRecordNum, String endRecordNum, String queryType, String merchantID, String serviceID, String subCosID, String brand, String type, String status, String orderType, String orderBy, String moodModeID) {
		return new QueryUser(connection()).execute(portalType, moduleCode, role, roleCode, phoneNumber, startRecordNum, endRecordNum, queryType, merchantID, serviceID, subCosID, brand, type, status, orderType, orderBy, moodModeID);
	}
	
	public HashMap<String, String> setTone(String portalType, String moduleCode, String role, String roleCode, String phoneNumber, String calledUserID, String calledUserType, String callerNumber, String endTime, String loopType, String resourceType, String mode, String type, String startTime, String timeType, String toneBoxID, String toneType) {
		return new SetTone(connection()).execute(portalType, moduleCode, role, roleCode, phoneNumber, calledUserID, calledUserType, callerNumber, endTime, loopType, resourceType, mode, type, startTime, timeType, toneBoxID, toneType);
	}
	
	public HashMap<String, String> subscribe(String portalType, String moduleCode, String role, String roleCode, String phoneNumber) {
		return new Subscribe(connection()).execute(portalType, moduleCode, role, roleCode, phoneNumber);
	}
	
	public HashMap<String, String> unSubscribe(String portalType, String moduleCode, String role, String roleCode, String phoneNumber) {
		return new UnSubscribe(connection()).execute(portalType, moduleCode, role, roleCode, phoneNumber);
	}

}
