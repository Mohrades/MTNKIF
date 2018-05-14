package product;

import java.util.Locale;

import org.springframework.context.MessageSource;

import dao.DAO;
import dao.queries.SubscriberDAOJdbc;
import domain.models.Subscriber;

public class PricePlanCurrent {

	public PricePlanCurrent() {
		
	}
	
	public Object [] activation(DAO dao, String msisdn, MessageSource i18n, int language, ProductProperties productProperties, String originOperatorID) {
		return (new Activation()).execute(dao, msisdn, i18n, language, productProperties, "eBA");
	}

	public Object [] deactivation(DAO dao, String msisdn, MessageSource i18n, int language, ProductProperties productProperties, String originOperatorID) {
		return (new Deactivation()).execute(dao, msisdn, i18n, language, productProperties, "eBA");
	}

	public Object [] getStatus(ProductProperties productProperties, MessageSource i18n, DAO dao, String msisdn, int language) {
		Subscriber subscriber = new SubscriberDAOJdbc(dao).getOneSubscriber(msisdn);
		int statusCode = -1; // default

		if(subscriber == null) {
			statusCode = new ProductActions().isActivated(productProperties, dao, msisdn);
		}
		else {
			 if(subscriber.isLocked()) statusCode = -1;
			 else {
				 if(subscriber.isFlag()) {
					 statusCode = new ProductActions().isActivated(productProperties, dao, msisdn);

					 if(statusCode == 0) statusCode = 0; // success
					 else if(statusCode == 1) statusCode = -1; // anormal
					 else if(statusCode == -1) statusCode = -1; // erreur AIR
				 }
				 else statusCode = 1;
			 }
		}

		String message = null;

		if(statusCode == 0) {
			message = i18n.getMessage("status.successful", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}
		else if(statusCode == 1) {
			message = i18n.getMessage("status.failed", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}
		else {
			message = i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}

		return new Object [] {statusCode, message};
	}
}
