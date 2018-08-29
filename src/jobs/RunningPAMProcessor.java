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
	/**
	 * 
	Filtering items : It is actually very easy to tell Spring Batch not to continue processing an item.  To do so, instead of the ItemProcessor returning an item, it returns null.
	
	let’s look at the filtering rules for item processors:
		If  the  process  method  returns  null,  Spring  Batch  filters  out  the  item  and  it won’t go to the item writer.
		Filtering is different from skipping.
		An exception thrown by an item processor results in a skip (if you configured the skip strategy accordingly).

	The basic contract for filtering is clear, but we must point out the distinction between filtering and skipping:
		Filtering means that Spring Batch shouldn’t write a given record. For example, the item writer can’t handle a record.
		Skipping  means that a given record is invalid. For example, the format of a phone number is invalid.
	*/
	/* Filtering items : It is actually very easy to tell Spring Batch not to continue processing an item. To do so, instead of the ItemProcessor returning an item, it returns null.*/
	public Subscriber process(Subscriber subscriber) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

		try {
			// attempts
			int retry = 0;

			while(productProperties.getAir_preferred_host() == -1) {
				if(retry >= 3) throw new AirAvailabilityException();

				productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
				retry++;
			}

			retry = 0;

			if(!request.isWaitingForResponse()) itemProcessedCount++;

			// don't waiting for the response : waitingForResponse is false
			if((!request.isWaitingForResponse()) && ((itemProcessedCount % 600) == 0)) {
				itemProcessedCount = 0;

				// change AIR HOST : to release current host and not to overload it
				int current_preferred_host = productProperties.getAir_preferred_host();
				current_preferred_host = (current_preferred_host + 1)  % (productProperties.getAir_hosts().size());
				// productProperties.setAir_preferred_host((byte) current_preferred_host);

				// waiting for the response to test connection
				request.setWaitingForResponse(true);
			}
			else request.setWaitingForResponse(isWaitingForResponse());

			// do action
			if(request.runPeriodicAccountManagement(subscriber.getValue(), productProperties.getPamServiceID(), "eBA")) {
				subscriber.setFlag(true);
			}
			else {
				if(request.isWaitingForResponse()) {
					if(request.isSuccessfully()) subscriber.setFlag(false);
					else {
						productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
						throw new AirAvailabilityException();
					}
				}
				else {
					subscriber.setFlag(request.isSuccessfully() ? true : false);
				}
			}

			return subscriber;

		} catch(AirAvailabilityException ex) {
			throw ex;

		} catch(Exception ex) {
			if(ex instanceof AirAvailabilityException) throw ex;

		} catch(Throwable th) {
			if(th instanceof AirAvailabilityException) throw th;
		}

		return null;
	}

}
