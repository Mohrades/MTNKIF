package jobs;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

	public MyAsyncUncaughtExceptionHandler(){

	}

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		// TODO Auto-generated method stub

		sendEmail(ex, method);
	}

	private void sendEmail(Throwable ex, Method method) {
		try{

		} catch(Throwable th) {

		}

	}

}
