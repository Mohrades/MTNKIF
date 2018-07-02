package api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcBirthDayBonusSubscriberDao;
import dao.queries.JdbcSubscriberDao;
import domain.models.BirthDayBonusSubscriber;
import domain.models.Subscriber;
import filter.MSISDNValidator;
import product.PricePlanCurrentActions;
import product.ProductProperties;

@Component("happyBirthdayEventListener")
public class HappyBirthDayEventListener {

	private int dateIndex, air_errors_count;

	public HappyBirthDayEventListener() {
		dateIndex = Integer.parseInt((new SimpleDateFormat("yyMMddHH")).format(new Date()));
		air_errors_count = 0;
	}

	public int getAir_errors_count() {
		int dateIndexNew = Integer.parseInt((new SimpleDateFormat("yyMMddHH")).format(new Date()));

		if(dateIndex != dateIndexNew) {
			dateIndex = dateIndexNew;
			air_errors_count = 0;
		}

		return air_errors_count;
	}

	@Async("MultithreadedStepsTaskExecutor")
	public void handle(String msisdn, String name, int language, String originOperatorID, ProductProperties productProperties, DAO dao) {
		try {
			// (éviter d'inscrire les numéros en bd) pour gagner du temps : juste vérifier le statut de chaque subscriber
			if((getAir_errors_count() < 5) && ((new MSISDNValidator()).isFiltered(dao, productProperties, msisdn, "A"))) {
				originOperatorID = originOperatorID.trim();
				int status = checkPricePlanCurrent(productProperties, dao, msisdn);

				// register msisdn to benefit happy birthday bonus
				if(status == 0) {
					// store BirthdayBonusSubscriber
					(new JdbcBirthDayBonusSubscriberDao(dao)).saveOneBirthdayBonusSubscriber((new BirthDayBonusSubscriber(0, msisdn, name, language, new Date())));
				}
				else if(status == -1) {
					++air_errors_count;
				}
			}

		} catch (NullPointerException ex) {

		} catch (Exception ex) {

		} catch(Throwable th) {

		} finally {
			try {

			} catch (Exception e) {

			} catch(Throwable th) {

			}
		}
	}

	public int checkPricePlanCurrent(ProductProperties productProperties, DAO dao, String msisdn) {
		// attempts
		int retry = 0;

		while(productProperties.getAir_preferred_host() == -1) {
			if(retry >= 3) return -1;

			productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
			retry++;
		}

		Subscriber subscriber = new JdbcSubscriberDao(dao).getOneSubscriber(msisdn);

		 if((subscriber != null) && ((subscriber.isLocked()) || (!subscriber.isFlag()))) return 1;
		 else {
			 int status = (new PricePlanCurrentActions()).isActivated(productProperties, dao, msisdn);

			// re-check air connection
			if(status == -1) productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));

			return status;
		 }
	}

}
