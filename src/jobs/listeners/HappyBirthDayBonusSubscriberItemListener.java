package jobs.listeners;

import java.util.List;

import org.springframework.batch.core.listener.ItemListenerSupport;

import dao.DAO;
import domain.models.HappyBirthDayBonusSubscriber;
import product.ProductProperties;

public class HappyBirthDayBonusSubscriberItemListener extends ItemListenerSupport<HappyBirthDayBonusSubscriber, HappyBirthDayBonusSubscriber> {

	private DAO dao;

	private ProductProperties productProperties;

	public HappyBirthDayBonusSubscriberItemListener() {
		
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

	public void beforeProcess(HappyBirthDayBonusSubscriber item) {
		
	}

	public void beforeWrite(List<? extends HappyBirthDayBonusSubscriber> result) {
		
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
        	// Output of the CustomerItemLogger below :
        	// 2011-05-03 23:49:22,148 ERROR main [com.apress.springbatch.chapter7.CustomerItemListener] - 
			// <An error occured while processing the 1 line of the file.  Below was the faulty input. 
			// Michael   TMinella   123   4th Street          Chicago  IL60606ABCDE 
			// >

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

	public void onProcessError(HappyBirthDayBonusSubscriber item, Exception ex) {
		
	}

	public void onWriteError(Exception ex, List<? extends HappyBirthDayBonusSubscriber> result) {
		
	}

	public void afterRead(HappyBirthDayBonusSubscriber item) {
		
	}

	public void afterProcess(HappyBirthDayBonusSubscriber item, HappyBirthDayBonusSubscriber result) {
		
	}

	public void afterWrite(List<? extends HappyBirthDayBonusSubscriber> result) {
		
	}

}

