package jobs.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

/**
 * 
Spring Batch provides the RetryListener interface to react to any retried operation. A retry listener can be useful to log retried operations and to gather information.
Once you know more about transient failures, you’re more likely to change the system to avoid them in subsequent executions (remember, retried operations always degrade performance).
  You  can directly implement the RetryListener interface; it defines two  life-cycle methods—open and close—that often remain empty, because you usually care only about the error thrown in the operation.
  A better way is to extend the RetryListenerSupport adapter class and override the onError method, as shown in the following listing.
*/
public class CustomRetryOperationsListener extends RetryListenerSupport {

	private Logger LOG;

	public CustomRetryOperationsListener() {
		LOG = LogManager.getLogger("logging.log4j.JobExecutionLogger");
	}

	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T,E> callback) {
		return true;
	}

	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T,E> callback, Throwable throwable) {
		/**
		 * 
		2018-09-04 17:22:13.578 [main] ERROR  - retried operation
		java.lang.ArithmeticException: / by zero
			at test.A.main(A.java:17) [classes/:?]
		*/

		// LOG.error(throwable.getMessage());
		LOG.error("retried operation", throwable);
	}

	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
		
	}

}
