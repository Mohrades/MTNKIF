package jobs;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import domain.models.HappyBirthDayBonusSubscriber;

public class SynchronizingHappyBirthDayBonusSubscriberReader implements ItemReader<HappyBirthDayBonusSubscriber>, ItemStream {

	private ItemReader<HappyBirthDayBonusSubscriber> delegate;

	public ItemReader<HappyBirthDayBonusSubscriber> getDelegate() {
		return delegate;
	}

	public void setDelegate(ItemReader<HappyBirthDayBonusSubscriber> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void close() throws ItemStreamException {
		// TODO Auto-generated method stub
	    if (this.delegate instanceof ItemStream) { // Delegates for state management
	        ((ItemStream)this.delegate).close();
	    }
	}

	@Override
	public void open(ExecutionContext context) throws ItemStreamException {
		// TODO Auto-generated method stub
	    if (this.delegate instanceof ItemStream) { // Delegates for state management
	        ((ItemStream)this.delegate).open(context);
	    }
	}

	@Override
	public void update(ExecutionContext context) throws ItemStreamException {
		// TODO Auto-generated method stub
	    if (this.delegate instanceof ItemStream) {
	        ((ItemStream)this.delegate).update(context);
	    }
	}

	@Override
	public synchronized HappyBirthDayBonusSubscriber read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		// TODO Auto-generated method stub
		return delegate.read(); // Synchronizes read method
	}

}
