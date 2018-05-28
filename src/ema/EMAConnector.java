package ema;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class EMAConnector {
	 private BufferedInputStream in;
     private PrintStream out;
     private int sleep;
     private Socket socket;
     private String login,password;

	public boolean isOpen() {
		return (out == null) ? false : true;
	}

	public EMAConnector(String ip, int port, int sleep, String login, String password) {
		try {
			socket = new Socket(ip, port);
	        out= new PrintStream(socket.getOutputStream());

			if(out != null) {
				this.login=login;
				this.password=password;
				this.sleep=sleep;
				in = new BufferedInputStream(socket.getInputStream());

		        byte[] lecteur = new byte[1024];
		        int totalsleep = 0;

		        while (in.available() == 0) {
		        	if(totalsleep >= 5000) {
		        		break;
		        	}

	            	totalsleep += sleep;
		        	Thread.sleep(sleep);
		        }

		        Thread.sleep(230);

		        while (in.available() > 0) {
		        	in.read(lecteur);
		            lecteur = new byte[1024];
		        }   
			}

		} catch (UnknownHostException|InterruptedException e) {
			// TODO Auto-generated catch block

		} catch (IOException e) {

        } catch (Exception e) {

		}
	}

	public void fermer() {
		try {
			execute("LOGOUT;", true);
			socket.close();

		} catch (IOException e) {

		}
		catch (Exception e) {

		}
	}

     public int login() {
        return execute("LOGIN:"+login+":"+password+";", false);
     }

     public int execute(String command, boolean noResponse) {
    	try {
    		int totalsleep = 0;

            out.println(command + "\n");
            out.flush();

            if(noResponse) return 0;

            String reponse = new String("");
            byte[] lecteur = new byte[1024];
            while (in.available() == 0) {
            	if(totalsleep >= 5000) return -2;
            	totalsleep += sleep;
            	Thread.sleep(sleep);
            }

            Thread.sleep(230);

            while(in.available() > 0) {
	    		in.read(lecteur);
	    		reponse += new String(lecteur);
	        	lecteur = new byte[1024];
	        }

            int init = reponse.indexOf("RESP:");
            int last = reponse.indexOf(";", init);

            int retour = Integer.parseInt(reponse.substring(init+5,last));

            return retour;

        } catch (InterruptedException ex) {
            return -2;

        } catch (IOException ex) {
            return -2;

        } catch (Exception ex) {
            return -2;

        }
    }

}