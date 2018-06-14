package product;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import org.springframework.context.MessageSource;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.RollBackDAOJdbc;
import dao.queries.SubscriberDAOJdbc;
import dao.queries.SubscriptionReportingDAOJdbc;
import domain.models.RollBack;
import domain.models.Subscriber;
import domain.models.SubscriptionReporting;
import util.BalanceAndDate;
import util.DedicatedAccount;

public class PricePlanCurrentDeactivation {

	public PricePlanCurrentDeactivation() {

	}

	@SuppressWarnings("deprecation")
	public Object [] execute(DAO dao, String msisdn, Subscriber subscriber, MessageSource i18n, int language, ProductProperties productProperties, String originOperatorID) {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold());
		// Object [] requestStatus = new Object [2];

		if((request.getBalanceAndDate(msisdn, 0)) != null) {
			boolean registered = false;

			if(subscriber == null) {
				subscriber = new Subscriber(0, msisdn, false, false, null, null, true);
				registered = (new SubscriberDAOJdbc(dao).saveOneSubscriber(subscriber) == 1) ? true : false;
			}
			else {
				subscriber.setFlag(false);
				registered = (new SubscriberDAOJdbc(dao).saveOneSubscriber(subscriber) == 1) ? true : false;
			}

			if(registered) {
				subscriber.setLocked(true); // synchronisation database and object

				// reserved chargingAmount
				HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
				if(productProperties.getChargingDA() == 0) balances.add(new BalanceAndDate(0, -productProperties.getDeactivation_chargingAmount(), null));
				else balances.add(new DedicatedAccount(productProperties.getChargingDA(), -productProperties.getDeactivation_chargingAmount(), null));

				Date now = new Date();
				now.setDate(now.getDate() - productProperties.getDeactivation_freeCharging_days());

				if((subscriber.getId() == 0) || (subscriber.getLast_update_time() == null) || ((subscriber.getLast_update_time().before(now)) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "DEACTIVATIONCHARGING", "eBA")))) {
					balances = new HashSet<BalanceAndDate>();
					if(productProperties.getChargingDA() == 0) balances.add(new BalanceAndDate(0, productProperties.getDeactivation_chargingAmount(), null));
					else balances.add(new DedicatedAccount(productProperties.getChargingDA(), productProperties.getDeactivation_chargingAmount(), null));

					// call default price plan
					int statusCode = productProperties.isDefault_price_plan_deactivated() ? (new DefaultPricePlan()).requestDefaultPricePlanSubscription(productProperties, msisdn, "activation", originOperatorID) : 0;
					// statusCode 1  === l'api n'a pas changé d'état (conditions non remplies, l'abonné est déjà bloqué avec une action en cours, abonné ne possède pas suffisamment d'unités, etc...), abandonner le change request
					// statusCode 0  === l'api a bien changé d'état, exécuter le change request
					// statusCode -1 === l'api a generé une exception, logs change request suspended ou mis en pause (step =0)

					if(statusCode == 0) {
						// statusCode = (new ProductActions()).deactivation(productProperties, dao, subscriber.getValue(), !((subscriber.getId() == 0) || (subscriber.getLast_update_time() == null) || (subscriber.getLast_update_time().before(now))));
						statusCode = (new PricePlanCurrentActions()).deactivation(productProperties, dao, subscriber, false, originOperatorID); // charged is false because charging already occurs with reservation

						if(statusCode == 0) { // change done successfully
							subscriber.setLocked(false); // synchronisation database and object

							new SubscriptionReportingDAOJdbc(dao).saveOneSubscriptionReporting(new SubscriptionReporting(0, (subscriber.getId() > 0) ? subscriber.getId() : (new SubscriberDAOJdbc(dao).getOneSubscriber(msisdn).getId()), false, (subscriber.getId() == 0) ? 0 : (subscriber.getLast_update_time() == null) ? 0 : (subscriber.getLast_update_time().before(now)) ? 0 : productProperties.getDeactivation_chargingAmount(), new Date(), originOperatorID)); // reporting
							new SubscriberDAOJdbc(dao).releasePricePlanCurrentStatusAndLock(subscriber, false, productProperties.getDeactivation_freeCharging_days()); // release Lock

							return new Object [] {0, i18n.getMessage("deactivation.change.successful", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH)};
						}
						else {
							// release reserved chargingAmount when deactivation failed
							if((subscriber.getId() == 0) || (subscriber.getLast_update_time() == null) || ((subscriber.getLast_update_time().before(now)) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "DEACTIVATIONREFUNDING", "eBA"))));
							else {
								if(request.isSuccessfully()) {
									if(statusCode == 1) new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 101, 2, msisdn, msisdn, null));
									else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 102, 2, msisdn, msisdn, null));
								}
								else {
									if(statusCode == 1) new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -101, 2, msisdn, msisdn, null));
									else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -102, 2, msisdn, msisdn, null));
								}
							}

							// statusCode = 1, change not done
							// statusCode = -1, change done unreliable (errors should occur) : subscriber may 
							new SubscriberDAOJdbc(dao).releasePricePlanCurrentStatusAndLock(subscriber, true, productProperties.getDeactivation_freeCharging_days()); // release Lock
							return new Object [] {statusCode, (statusCode == 1) ? i18n.getMessage("deactivation.change.failed", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH) : i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH)};
						}
					}
					else {
						// release reserved chargingAmount when api calling failed
						if((subscriber.getId() == 0) || (subscriber.getLast_update_time() == null) || ((subscriber.getLast_update_time().before(now)) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "RELEASE", "eBA"))));
						else {
							if(request.isSuccessfully()) {
								if(statusCode == 1) new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 99, 2, msisdn, msisdn, null));
								else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 98, 2, msisdn, msisdn, null));
							}
							else {
								if(statusCode == 1) new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -99, 2, msisdn, msisdn, null));
								else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -98, 2, msisdn, msisdn, null));
							}
						}

						new SubscriberDAOJdbc(dao).releasePricePlanCurrentStatusAndLock(subscriber, true, productProperties.getDeactivation_freeCharging_days()); // release Lock
						return new Object [] {statusCode, (statusCode == 1) ? i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH) : i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH)};
					}
				}
				else {
					if(request.isSuccessfully());
					else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -100, 2, msisdn, msisdn, null));

					new SubscriberDAOJdbc(dao).releasePricePlanCurrentStatusAndLock(subscriber, true, productProperties.getDeactivation_freeCharging_days()); // release Lock
					return new Object [] {request.isSuccessfully() ? 1 : -1, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH)};
				}
			}
			else {
				return new Object [] {-1, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH)};
			}
		}
		else {
			return new Object [] {-1, i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH)};
		}
	}

}
