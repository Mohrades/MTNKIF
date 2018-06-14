package ema;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class SocketConnection {
	 private BufferedInputStream in;
     private PrintStream out;
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

	        out = new PrintStream(socket.getOutputStream());

			if(out != null) {
				this.sleep = sleep;

				in = new BufferedInputStream(socket.getInputStream());

		        byte[] lecteur = new byte[1024];
		        int link_timeout = 0;

		        while (in.available() == 0) {
		        	if(link_timeout >= (this.timeout)) {
		        		break;
		        	}

		        	link_timeout += (this.sleep);
		        	Thread.sleep(this.sleep);
		        }

		        Thread.sleep(230);

		        while (in.available() > 0) {
		        	in.read(lecteur);
		            lecteur = new byte[1024];
		        }

		        setAvailable(true);
			}

		} catch (UnknownHostException|InterruptedException e) {
			// TODO Auto-generated catch block

		} catch (SocketTimeoutException ex) {

        } catch (IOException e) {

        } catch (Exception e) {

		}
	}

	public boolean isOpen() {
		return (out == null) ? false : true;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public void fermer() {
		try {
			execute("LOGOUT;", false);
			socket.close();

		} catch (IOException ex) {

		} catch (Exception ex) {

		} catch (Throwable th) {

        }
	}

     public int login(String login, String password) {
        return execute("LOGIN:"+login+":"+password+";", true);
     }

     public int execute(String command, boolean waitingForResponse) {
    	try {
    		setAvailable(false);

            out.println(command + "\n");
            out.flush();

            if(!waitingForResponse) {
            	setAvailable(true);
            	return 0;
            }

            String reponse = new String("");
            byte[] lecteur = new byte[1024];
            int link_timeout = 0;

            while (in.available() == 0) {
            	if(link_timeout >= timeout) return -2;
            	link_timeout += sleep;

            	Thread.sleep(sleep);
            }

            Thread.sleep(230);

            while(in.available() > 0) {
	    		in.read(lecteur);
	    		reponse += new String(lecteur);
	        	lecteur = new byte[1024];
	        }

            setAvailable(true);

            int init = reponse.indexOf("RESP:");
            int last = reponse.indexOf(";", init);

            return Integer.parseInt(reponse.substring(init+5, last).trim());

        } catch (SocketTimeoutException ex) {

        } catch (InterruptedException|IOException ex) {

        } catch (Exception ex) {

        } catch (Throwable th) {

        }

    	 return -2;
    }

}