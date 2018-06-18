package api;

import java.util.Date;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import dao.DAO;
import dao.queries.BirthdayBonusSubscriberDAOJdbc;
import domain.models.BirthdayBonusSubscriber;
import filter.MSISDNValidator;
import product.PricePlanCurrent;
import product.ProductProperties;

@Component("happyBirthdayEventListener")
public class HappyBirthdayEventListener {

	public HappyBirthdayEventListener() {

	}

	@Async("MultithreadedStepsTaskExecutor")
	public void handle(String msisdn, String name, int language, String originOperatorID, ProductProperties productProperties, DAO dao) {
		try {
			if((new MSISDNValidator()).isFiltered(dao, productProperties, msisdn, "A")) {
				originOperatorID = originOperatorID.trim();
				Object [] requestStatus = (new PricePlanCurrent()).getStatus(productProperties, null, dao, msisdn, language);

				// register msisdn to benefit happy birthday bonus
				if((int)(requestStatus[0]) >= 0) {
					if((int)(requestStatus[0]) == 0) {
						// store BirthdayBonusSubscriber
						(new BirthdayBonusSubscriberDAOJdbc(dao)).saveOneBirthdayBonusSubscriber((new BirthdayBonusSubscriber(0, msisdn, name, language, new Date())));
					}
					else ;
				}
				else {

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

}
