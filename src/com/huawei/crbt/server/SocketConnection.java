package com.huawei.crbt.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class SocketConnection {
	 private BufferedInputStream in;
     private OutputStream out;
	 private String ip;
	 private int port;
	 private int sleep, timeout = 12500;
     private Socket socket;

     private boolean available;

     public SocketConnection(String ip, int port, int sleep, int timeout) {
		try {
			socket = new Socket(ip, port);
        	// Par défaut, un objet DatagramSocket ne possède pas de timeout lors de l'utilisation de la méthode receive().
        	// La méthode bloque donc l'exécution de l'application jusqu'à la réception d'un packet de données.
        	// La méthode setSoTimeout() permet de préciser un timeout en millisecondes.
        	// Une fois ce délai écoulé sans réception d'un paquet de données, la méthode lève une exception du type SocketTimeoutException.
			if(timeout > 0) this.timeout = timeout;
			socket.setSoTimeout(this.timeout);

	        out = socket.getOutputStream();

			this.sleep = sleep;
			this.ip = ip;
			this.port = port;

		} catch (UnknownHostException e) {

		} catch (SocketTimeoutException ex) {

        } catch (IOException e) {

		}
	}

	public boolean isOpen() {
		return (out == null) ? false : true;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public void fermer() {
		try {
			if(isOpen()) {
				socket.close();
	    	}

		} catch (IOException e) {

		} catch (Exception e) {

		} catch (Throwable e) {

		}
	}

	public String submitRequest(String endpoint, String requete) {
    	String header="POST " + endpoint + " HTTP/1.1\n" +
    		"Content-Length: " + requete.length() + "\n" +
    		"Content-Type: text/xml\n" +
    		"SOAPAction: \n" +
    		"Host: " + ip + ":" + port + "\n\n";

		/*conn.setRequestProperty("SOAPAction", "");
		conn.setRequestProperty("Content-Type", "text/xml");*/

        byte data[] = (header + requete).getBytes();
        String reponse = null;

        try {
			out.write(data, 0, data.length);
			out.flush();

			if(sleep <= 0) {
				setAvailable(true);
				return "";
			}

			in = new BufferedInputStream(socket.getInputStream());
            reponse = "";
            byte[] lecteur = new byte[1024];

            int link_timeout = 0;

            while (in.available() == 0) {
            	link_timeout += sleep;

            	if(link_timeout >= timeout) {
            		return "";
            	}

            	Thread.sleep(sleep);
            }

            Thread.sleep(12);
            // Thread.sleep(10);

            while (in.available() > 0) {
                in.read(lecteur);
                reponse += new String(lecteur);
                lecteur = new byte[1024];
            }

            try {
            	reponse = reponse.substring(0, reponse.indexOf(String.valueOf((char) 0)));

            } catch(StringIndexOutOfBoundsException ex) {

            } catch (Throwable th) {

            }

            try {
                // int beginIndex = reponse.indexOf("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            	int beginIndex = reponse.indexOf("<?xml");
                reponse = reponse.substring(beginIndex);

            } catch(StringIndexOutOfBoundsException ex) {

            } catch (Throwable th) {

            }

            setAvailable(true);
            return reponse;

		} catch (SocketTimeoutException ex) {
			return "";

        } catch (IOException e) {
			// TODO Auto-generated catch block

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block

		} catch (StringIndexOutOfBoundsException ex) {

        } catch (Throwable th) {

        }

        return (reponse == null) ? "" : reponse;
     }

}
