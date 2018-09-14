package jobs;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.MessageSource;

import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import product.ProductProperties;

public class MonthlyReminderProcessor implements ItemProcessor<Subscriber, Subscriber> {

	private MessageSource i18n;

	private ProductProperties productProperties;

	public MonthlyReminderProcessor() {
		
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	public MessageSource getI18n() {
		return i18n;
	}

	public void setI18n(MessageSource i18n) {
		this.i18n = i18n;
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

		return subscriber;
	}

}
