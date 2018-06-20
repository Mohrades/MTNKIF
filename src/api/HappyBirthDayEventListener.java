package api;

import java.util.Date;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import dao.DAO;
import dao.queries.BirthDayBonusSubscriberDAOJdbc;
import domain.models.BirthDayBonusSubscriber;
import filter.MSISDNValidator;
import product.PricePlanCurrent;
import product.ProductProperties;

@Component("happyBirthdayEventListener")
public class HappyBirthDayEventListener {

	public HappyBirthDayEventListener() {

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
						(new BirthDayBonusSubscriberDAOJdbc(dao)).saveOneBirthdayBonusSubscriber((new BirthDayBonusSubscriber(0, msisdn, name, language, new Date())));
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
