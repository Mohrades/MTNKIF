package ema;

import java.util.HashSet;

public class EMARequest {

	public EMARequest() {

	}

	public EMAConnector getConnection() {
		EMAConnector connection = null;

		try {
			connection = new EMAConnector("10.77.85.73", 3300, 5, "login", "password");
			int retry = 0;

			while(!connection.isOpen()) {
				if(retry >= 48) {
					break;
				}

				try {
					if((retry%3) == 0) {
						Thread.sleep(100);
					}
					else if((retry%3) != 1) {
						Thread.sleep(150);
					}
					else Thread.sleep(50);

					if((retry%2) == 0) {
						connection = new EMAConnector("10.77.85.75", 3300, 5, "login", "password");
					}
					else {
						connection = new EMAConnector("10.77.85.73", 3300, 5, "login", "password");
					}

					retry++;

				} catch (InterruptedException e) {
					retry++;

				} catch (Exception e) {
					retry++;
				}
			}
			
			if((connection != null) && (retry < 48) && (connection.isOpen())) {
				return connection;
			}

		} catch(Throwable th) {

		}

		return null;
	}

	public boolean execute(String command, HashSet<Integer> allResp, HashSet<Integer> successResp) {
		EMAConnector connection = getConnection();

		try {
			if(connection != null) {
		    	int retry = 0;
		    	int responseCode = connection.execute(command);

		        while((responseCode == 3006) || (responseCode == 3007)) {
		        	if(retry >= 5) {
		        		break;
		        	}

		        	retry++;
		        	responseCode = connection.login();
		        }

		        if((retry != 0) && (responseCode == 0)) {
		        	responseCode = connection.execute(command);
		        }

		        boolean AfterError = false;
		        retry = 0;

		        while(!allResp.contains(responseCode)) {
		        	if(responseCode == -2) {
		        		AfterError = true;
		        	}

		        	responseCode = connection.execute(command);

		        	if(retry > 150) {
		        		break;
		        	}

		        	try {
		        		if((retry%3) == 2) {
		        			Thread.sleep(150);
		        		}
						else Thread.sleep(100);

					} catch (InterruptedException e) {

					} catch (Exception e) {

					}

		        	retry++;
		        }

		        if(AfterError && (successResp.contains(responseCode))) {
		        	responseCode = 0;
		        }
		        return responseCode == 0;			
			}

		} catch(Throwable th) {

		}

		return false;
	}
}
