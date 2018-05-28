package jobs;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import connexions.AIRRequest;
import domain.models.Subscriber;
import product.ProductProperties;

@Component("runningPAMProcessor")
public class RunningPAMProcessor implements ItemProcessor<Subscriber, Subscriber> {

	@Autowired
	private ProductProperties productProperties;

	@Override
	public Subscriber process(Subscriber subscriber) {
		// TODO Auto-generated method stub

		try {
			// do action
			if((new AIRRequest()).runPeriodicAccountManagement(subscriber.getValue(), productProperties.getPamServiceID(), "eBA")) {
				subscriber.setFlag(true);
			}
			else {
				subscriber.setFlag(false);
			}

			return subscriber;

		} catch(Throwable th) {

		}

		return null;
	}

}
