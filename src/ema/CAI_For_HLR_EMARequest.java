package ema;

import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CAI_For_HLR_EMARequest {

	private boolean successfully;
	private List<String> hosts;
	private int sleep, timeout;

	public CAI_For_HLR_EMARequest(List<String> hosts, int sleep, int timeout) {
		this.hosts = hosts;
		this.sleep = sleep;
		this.timeout = timeout;
	}

	public SocketConnection getConnection() {
		SocketConnection connection = null;

		try {
			if(hosts != null) {
				int retry = 0;
				HashSet<Integer> unavailableHosts = new HashSet<Integer>();

				while((connection == null) || (!(connection.isOpen() && connection.isAvailable()))) {
					if((unavailableHosts.size() == hosts.size()) || (((hosts.size() < 3) && (retry >= (3*hosts.size()))) || ((hosts.size() >= 3) && (retry >= (2*hosts.size()))))) {
						break;
					}

					try {
						if((retry >= hosts.size()) && ((retry % (hosts.size())) == 0)) {
							Thread.sleep(1500);
						}

						// bypass unavailable host
						if(unavailableHosts.contains((retry % (hosts.size())))) {
							retry++;
							continue;
						}

						String host = hosts.get((retry % (hosts.size())));
						int separator = host.indexOf(":");
						connection = new SocketConnection((host.substring(0, separator)).trim(), Integer.parseInt((host.substring(separator+1)).trim()), sleep, timeout);

					} catch (Throwable th) {

					} finally {
						if((connection == null) || (!connection.isAvailable())) {
							unavailableHosts.add((retry % (hosts.size())));
						}

						retry++;
					}
				}

				if((connection != null) && (connection.isOpen() && connection.isAvailable())) {
					return connection;
				}
			}

		} catch(Throwable th) {

		}

		return null;
	}

	public boolean isSuccessfully() {
		return successfully;
	}

	public void setSuccessfully(boolean successfully) {
		this.successfully = successfully;
	}

	public boolean execute(String command, HashSet<Integer> allResp, HashSet<Integer> successResp) {
		SocketConnection connection = getConnection();

		try {
			if(connection != null) {
				setSuccessfully(true); // host available status
		    	int retry = 0;
		    	int responseCode = connection.execute(command, true); // execute command
		    	retry++;

		    	// requires authentication : provide login
		    	if((responseCode == 3006) || (responseCode == 3007)) {
		    		retry = 0; // reset attempts

			        while((responseCode == 3006) || (responseCode == 3007)) {
			        	if(retry >= 3) break;

			        	responseCode = connection.login("psappuser", "psappuser@123");
			        	retry++;

			        	// login successful
				        if((responseCode == 3008) || (responseCode == 0)) {
				        	responseCode = 0;
				        	break;
				        }
				        else {
				        	if(connection.isAvailable()) Thread.sleep(1500); // wait before re-login attempt
				        	else {
				        		handleTimeoutException(connection); // logging by sending mail
				        		setSuccessfully(false);
				        		break;
				        	}
				        }
			        }

		        	// login successful
		        	if(responseCode == 0) {
		        		retry = 0; // reset attempts
		        		responseCode = connection.execute(command, true); // re-execute command
		        		retry++;
		        	}
		        	// login failed
		        	else {
				        connection.fermer(); // logout
				        return false;
		        	}
		    	}

		        boolean AfterError = false;

		        while(!allResp.contains(responseCode)) {
		        	if(responseCode == -2) {
		        		AfterError = true;
		        	}

		        	if(retry >= 3) break;

		        	try {
			        	if(connection.isAvailable()) Thread.sleep(1500); // wait before re-executing command
			        	else {
			        		setSuccessfully(false);
			        		handleTimeoutException(connection); // logging by sending mail
			        		break;
			        	}

			        	responseCode = connection.execute(command, true); // re-execute command

					} catch (InterruptedException e) {

					} catch (Exception e) {

					} catch (Throwable e) {

					} finally {
						retry++;
					}
		        }

		        if(AfterError && (successResp.contains(responseCode))) {
		        	responseCode = 0;
		        }

		        // logout
		        connection.fermer();

		        return responseCode == 0;
			}

		} catch(NullPointerException ex) {

		} catch(Throwable th) {

		}

		return false;
	}

	public void handleTimeoutException(SocketConnection connection) {
		Logger logger = LogManager.getLogger("logging.log4j.EmaAvailabilityLogger");
		logger.error("HOST = " + connection.getSocket().getInetAddress().getHostAddress() + ",   PORT = " + connection.getSocket().getPort() + ",   TIMEOUT = " + timeout);
		// logger.error("HOST = " + connection.getSocket().getLocalAddress().getHostAddress() + ",   PORT = " + connection.getSocket().getPort() + ",   TIMEOUT = " + timeout);
	}

}
