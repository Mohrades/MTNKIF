package jobs.listeners;

import java.util.List;

import org.springframework.batch.core.listener.ItemListenerSupport;

import dao.DAO;
import domain.models.Subscriber;
import product.ProductProperties;

public class SubscriberItemListener extends ItemListenerSupport<Subscriber, Subscriber> {

	private DAO dao;

	private ProductProperties productProperties;

	public SubscriberItemListener() {
		
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	public void beforeRead() {
		
	}

	public void beforeProcess(Subscriber item) {
		
	}

	public void beforeWrite(List<? extends Subscriber> result) {
		
	}

	// Logging Invalid Records
	/**
	 * While skipping problematic records is a useful tool, by itself it can raise an issue. In some scenarios, the 
	ability to skip a record is okay. Say you are mining data and come across something you can’t resolve; it’s 
	probably okay to skip it. However, when you get into situations where money is involved, say when 
	processing transactions, just skipping a record probably will not be a robust enough solution. In cases 
	like these, it is helpful to be able to log the record that was the cause of the error. In this section, you will 
	look at using an ItemListener to record records that were invalid.

		In this example, you will read data in from the Customer file from the beginning of the chapter. 
	When an Exception is thrown during input, you will log the record that caused the exception and the 
	exception itself. To do this, the CustomerItemListener will take the exception thrown and if it is a 
	FlatFileParseException, you will have access to the record that caused the issue and information on what 
	went wrong. Listing 7-64 shows the CustomerItemListener.
	 */
	public void onReadError(Exception ex) {
		/*Logger logger = LogManager.getLogger(HVConsumerItemListener.class);

        if(ex instanceof FlatFileParseException) { // The first is a FlatFileParseException for each record that is invalid
        	FlatFileParseException ffpe = (FlatFileParseException) e; 
            StringBuilder errorMessage = new StringBuilder(); 
            errorMessage.append("An error occured while processing the " + ffpe.getLineNumber() + " line of the file.  Below was the faulty " + "input.\n"); 
            errorMessage.append(ffpe.getInput() + "\n"); 
            logger.error(errorMessage.toString(), ffpe);

        }
        else { // The second are your log messages, others exceptions
            logger.error("An error has occured", ex); 
        }*/
	}

	public void onProcessError(Subscriber item, Exception ex) {
		
	}

	public void onWriteError(Exception ex, List<? extends Subscriber> result) {
		
	}

	public void afterRead(Subscriber item) {
		
	}

	public void afterProcess(Subscriber item, Subscriber result) {
		
	}

	public void afterWrite(List<? extends Subscriber> result) {
		
	}

}
