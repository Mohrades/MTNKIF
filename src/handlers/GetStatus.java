package handlers;

import java.util.Locale;

import org.springframework.context.MessageSource;

import dao.DAO;
import dao.queries.SubscriberDAOJdbc;
import domain.models.Subscriber;
import product.ProductActions;
import product.ProductProperties;

public class GetStatus {

	public GetStatus() {

	}

	public int getCode(ProductProperties productProperties, DAO dao, String msisdn) {
		Subscriber subscriber = new SubscriberDAOJdbc(dao).getOneSubscriber(msisdn);

		if(subscriber == null) return 1;
		else {
			 if(subscriber.isLocked()) return -1;
			 else {
				 if(subscriber.isFlag()) {
					 int statusCode = new ProductActions().isActivated(productProperties, dao, msisdn);

					 if(statusCode == 0) return 0; // success
					 else if(statusCode == 1) return -1; // anormal
					 else if(statusCode == -1) return -1; // erreur AIR
				 }
				 else return 1;
			 }
		}

		return -1; // default
	}

	public String getMessage(MessageSource i18n, int language, int statusCode) {
		if(statusCode == 0) {
			return i18n.getMessage("status.successful", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}
		else if(statusCode == 1) {
			return i18n.getMessage("status.failed", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}
		else {
			return i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}
	}

}
