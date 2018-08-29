package jobs.listeners;

import org.springframework.batch.core.ItemReadListener;
import domain.models.HappyBirthDayBonusSubscriber;

public class HappyBirthDayBonusSubscriberItemReadListener implements ItemReadListener<HappyBirthDayBonusSubscriber> {

	@Override
	public void afterRead(HappyBirthDayBonusSubscriber item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeRead() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadError(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

}
