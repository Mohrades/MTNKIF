package jobs;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import connexions.AIRRequest;
import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import product.ProductProperties;

@Component("runningPAMProcessor")
public class RunningPAMProcessor implements ItemProcessor<Subscriber, Subscriber> {

	@Autowired
	private ProductProperties productProperties;

	@Override
	public Subscriber process(Subscriber subscriber) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold());

		try {
			// do action
			// don't waiting for the response : set waitingForResponse false
			request.setWaitingForResponse(false);

			if(request.runPeriodicAccountManagement(subscriber.getValue(), productProperties.getPamServiceID(), "eBA")) {
				subscriber.setFlag(true);
			}
			else {
				if(request.isWaitingForResponse()) {
					if(request.isSuccessfully()) subscriber.setFlag(false);
					else {
						throw new AirAvailabilityException();
						// return null;
					}
				}
				else {
					subscriber.setFlag(request.isSuccessfully() ? true : false);
				}
			}

			return subscriber;

		} catch(Throwable th) {

		}

		return null;
	}

}
