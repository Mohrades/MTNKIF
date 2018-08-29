package jobs;

import org.springframework.batch.item.ItemProcessor;
import dao.DAO;
import domain.models.HappyBirthDayBonusSubscriber;
import exceptions.AirAvailabilityException;
import product.HappyBirthDayBonusActions;
import product.ProductProperties;

public class HappyBirthDayBonusProcessor implements ItemProcessor<HappyBirthDayBonusSubscriber, HappyBirthDayBonusSubscriber> {

	private DAO dao;

	private ProductProperties productProperties;

	public HappyBirthDayBonusProcessor() {
		
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
	/* Filtering items : It is actually very easy to tell Spring Batch not to continue processing an item.  To do so, instead of the ItemProcessor returning an item, it returns null.*/
	public HappyBirthDayBonusSubscriber process(HappyBirthDayBonusSubscriber happyBirthDayBonusSubscriber) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		try {
			// set bonus choice (data and voice)
			happyBirthDayBonusSubscriber.setBonus(1);

			if((new HappyBirthDayBonusActions(productProperties)).doActions(dao, happyBirthDayBonusSubscriber) == 0) {
				return happyBirthDayBonusSubscriber;
			}

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
