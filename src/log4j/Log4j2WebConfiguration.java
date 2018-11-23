package log4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.apache.logging.log4j.web.Log4jWebSupport;

public class Log4j2WebConfiguration implements ServletContextListener {

	private Log4jServletContextListener listener = new Log4jServletContextListener();

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub

		/*Starting in Log4j 2.6, you can now use org.apache.logging.log4j.LogManager.shutdown() to initiate shutdown manually.*/
		LogManager.shutdown();

		listener.contextDestroyed(event);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
	    String loggerPath = "WEB-INF/classes/log4j2.xml";
	    event.getServletContext().setInitParameter(Log4jWebSupport.LOG4J_CONFIG_LOCATION, loggerPath);
	    listener.contextInitialized(event);
	}

}
