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
	public HappyBirthDayBonusSubscriber process(HappyBirthDayBonusSubscriber birthdayBonusSubscriber) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		try {
			// set bonus choice (data and voice)
			birthdayBonusSubscriber.setBonus(1);

			if((new HappyBirthDayBonusActions(productProperties)).doActions(dao, birthdayBonusSubscriber) == 0) {
				return birthdayBonusSubscriber;
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