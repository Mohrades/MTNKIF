package jobs;

import org.springframework.batch.item.ItemProcessor;
import dao.DAO;
import domain.models.BirthDayBonusSubscriber;
import exceptions.AirAvailabilityException;
import product.HappyBirthdayBonusActions;
import product.ProductProperties;

public class HappyBirthdayBonusProcessor implements ItemProcessor<BirthDayBonusSubscriber, BirthDayBonusSubscriber> {

	private DAO dao;

	private ProductProperties productProperties;

	public HappyBirthdayBonusProcessor() {
		
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
	public BirthDayBonusSubscriber process(BirthDayBonusSubscriber birthdayBonusSubscriber) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		try {
			// set bonus choice (data and voice)
			birthdayBonusSubscriber.setBonus(1);

			if((new HappyBirthdayBonusActions(productProperties)).doActions(dao, birthdayBonusSubscriber) == 0) {
				return birthdayBonusSubscriber;
			}

		} catch(AirAvailabilityException ex) {
			throw ex;

		} catch(Throwable th) {

		}

		return null;
	}

}
