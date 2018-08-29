package jobs;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcSubscriberDao;
import domain.models.HappyBirthDayBonusSubscriber;
import domain.models.Subscriber;
import product.PricePlanCurrentActions;
import product.ProductProperties;

public class HappyBirthDayBonusSubscriberValidator implements Validator<HappyBirthDayBonusSubscriber> {

	private DAO dao;

	private ProductProperties productProperties;
	
	public HappyBirthDayBonusSubscriberValidator() {

	}

	public HappyBirthDayBonusSubscriberValidator(ProductProperties productProperties, DAO dao) {
		setProductProperties(productProperties);
		setDao(dao);
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
	public void validate(HappyBirthDayBonusSubscriber happyBirthDayBonusSubscriber) throws ValidationException {
		// TODO Auto-generated method stub

	    if(happyBirthDayBonusSubscriber.getAspu() >= productProperties.getHappy_birthday_bonus_aspu_minimum()) {
	    	/**
	    	 * check the current price plan of the subscriber : the current status of each subscriber is already checked when staging phase
	    	int status = checkPricePlanCurrent(happyBirthDayBonusSubscriber.getValue());

	    	if(status == 0) { // Successful

			}
			else if(status == 1) { // Failed
				throw new ValidationException("Subscriber is not a member of the current price plan !");
			}
			else if(status == -1) { // Data Inconsistency or Charging Error

			}
	    	*/
	    }
	    else {
	    	throw new ValidationException("ASPU cannot be less than " + productProperties.getHappy_birthday_bonus_aspu_minimum() + " !");
	    }
	}

	public int checkPricePlanCurrent(String msisdn) {
		// attempts
		int retry = 0;

		while(productProperties.getAir_preferred_host() == -1) {
			if(retry >= 3) return -1;

			productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
			retry++;
		}

		Subscriber subscriber = new JdbcSubscriberDao(dao).getOneSubscriber(msisdn);

		 if((subscriber != null) && ((subscriber.isLocked()) || ((!subscriber.isFlag()) && (subscriber.getLast_update_time() != null)))) return 1;
		 else {
			 int status = (new PricePlanCurrentActions()).isActivated(productProperties, dao, msisdn);

			// re-check air connection
			if(status == -1) productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));

			return status;
		 }
	}

}
