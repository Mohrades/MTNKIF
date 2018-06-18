package jobs;

import org.springframework.batch.item.ItemProcessor;
import connexions.AIRRequest;
import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import product.ProductProperties;

public class RunningPAMProcessor implements ItemProcessor<Subscriber, Subscriber> {

	private ProductProperties productProperties;

	private int itemProcessedCount;
	private boolean waitingForResponse;

	public RunningPAMProcessor() {
		itemProcessedCount = 0;
		waitingForResponse = true;
	}

	public boolean isWaitingForResponse() {
		return waitingForResponse;
	}

	public void setWaitingForResponse(boolean waitingForResponse) {
		this.waitingForResponse = waitingForResponse;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	@Override
	public Subscriber process(Subscriber subscriber) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

		try {
			// do action

			if(!request.isWaitingForResponse()) {
				if((itemProcessedCount % 500) == 0) {
					itemProcessedCount = 0;

					byte air_preferred_host = (byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host());
					productProperties.setAir_preferred_host(air_preferred_host);
					if(air_preferred_host == -1) {
						throw new AirAvailabilityException();
					}
				}
				itemProcessedCount++;
			}

			// don't waiting for the response : set waitingForResponse false
			request.setWaitingForResponse(isWaitingForResponse());

			if(request.runPeriodicAccountManagement(subscriber.getValue(), productProperties.getPamServiceID(), "eBA")) {
				subscriber.setFlag(true);
			}
			else {
				if(request.isWaitingForResponse()) {
					if(request.isSuccessfully()) subscriber.setFlag(false);
					else {
						productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
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
