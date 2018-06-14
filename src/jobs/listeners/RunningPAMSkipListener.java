package jobs.listeners;

import org.springframework.batch.core.SkipListener;

import domain.models.Subscriber;

public class RunningPAMSkipListener implements SkipListener<Subscriber, Subscriber> {

	@Override
	public void onSkipInProcess(Subscriber item, Throwable th) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSkipInRead(Throwable item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSkipInWrite(Subscriber item, Throwable th) {
		// TODO Auto-generated method stub
		
	}

}
