package product;

import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;

import org.springframework.context.MessageSource;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcSubscriberDao;
import domain.models.Subscriber;
import util.AccumulatorInformation;
import util.BalanceAndDate;

public class PricePlanCurrent {

	public PricePlanCurrent() {

	}

	public Object [] activation(DAO dao, String msisdn, Subscriber subscriber, MessageSource i18n, int language, ProductProperties productProperties, String originOperatorID) {
		return (new PricePlanCurrentActivation()).execute(dao, msisdn, subscriber, i18n, language, productProperties, "eBA");
	}

	public Object [] deactivation(DAO dao, String msisdn, Subscriber subscriber, MessageSource i18n, int language, ProductProperties productProperties, String originOperatorID) {
		return (new PricePlanCurrentDeactivation()).execute(dao, msisdn, subscriber, i18n, language, productProperties, "eBA");
	}

	public Object [] getStatus(ProductProperties productProperties, MessageSource i18n, DAO dao, String msisdn, int language, boolean bonus) {
		Subscriber subscriber = new JdbcSubscriberDao(dao).getOneSubscriber(msisdn);
		int statusCode = -1; // default

		if(subscriber == null) {
			statusCode = (new PricePlanCurrentActions()).isActivated(productProperties, dao, msisdn);

			// initialization the former price plan Status (formerly)
			if((statusCode == 0) || (statusCode == 1)) {
				subscriber = new Subscriber(0, msisdn, (statusCode == 0) ? true : false, (statusCode == 0) ? true : false, null, null, false);
				boolean registered = (new JdbcSubscriberDao(dao).saveOneSubscriber(subscriber) == 1) ? true : false;

				if(registered) {
					subscriber = (new JdbcSubscriberDao(dao)).getOneSubscriber(msisdn);
				}
				else {
					statusCode = -1;
				}
			}
		}
		else {
			 if(subscriber.isLocked()) statusCode = -1;
			 else {
				 if(subscriber.isFlag()) {
					 statusCode = new PricePlanCurrentActions().isActivated(productProperties, dao, msisdn);

					 if(statusCode == 0) statusCode = 0; // success
					 else if(statusCode == 1) statusCode = -1; // anormal
					 else if(statusCode == -1) statusCode = -1; // erreur AIR
				 }
				 else statusCode = 1;
			 }
		}

		String message = null;

		if((statusCode == 0) && bonus) {
			Object[] bonusSMS = getBonusSMS(productProperties, msisdn, language);
			Object[] bonusNight = getNightAdvantages(productProperties, msisdn, language);

			if((bonusSMS != null) && (bonusNight != null)) {
				message = i18n.getMessage("status.successful_with_bonus", new Object[] {(int)(bonusSMS[0]), (String)(bonusSMS[1]), (int)(bonusNight[0]), (int)(bonusNight[1]), (String)(bonusNight[2]), (String)(bonusNight[3]), (String)(bonusNight[4])}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
			}
			else if(bonusSMS != null) {
				message = i18n.getMessage("status.successful_with_bonus_only_sms_bonus", new Object[] {(int)(bonusSMS[0]), (String)(bonusSMS[1])}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
			}
			else if(bonusNight != null) {
				message = i18n.getMessage("status.successful_with_bonus_only_night_advantages", new Object[] {(int)(bonusNight[0]), (int)(bonusNight[1]), (String)(bonusNight[2]), (String)(bonusNight[3]), (String)(bonusNight[4])}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
			}
			else message = i18n.getMessage("status.successful", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}
		else message = i18n.getMessage((statusCode == 0) ? "status.successful" : (statusCode == 1) ? "status.failed" : "service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);

		return new Object [] {statusCode, message, subscriber};
	}

	public Object[] getBonusSMS(ProductProperties productProperties, String msisdn, int language) {
		try {
			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
			HashSet<AccumulatorInformation> balanceBonusSms = (productProperties.getBonus_sms_remaining_accumulator() > 0) ? request.getAccumulators(msisdn, new int[][] {{productProperties.getBonus_sms_remaining_accumulator(), productProperties.getBonus_sms_remaining_accumulator()}}) : null;

			if((balanceBonusSms == null) || (balanceBonusSms.isEmpty())) {
				return null;
			}
			else {
				for(AccumulatorInformation accumulator : balanceBonusSms) {
					return new Object[] {accumulator.getAccumulatorValue(), (language == 2) ? (new SimpleDateFormat("HH'H'mm")).format(accumulator.getAccumulatorEndDate()) : (new SimpleDateFormat("HH'H'mm")).format(accumulator.getAccumulatorEndDate())};
				}
			}

		} catch(Throwable th) {
			
		}

		return null;
	}

	@SuppressWarnings("resource")
	public Object[] getNightAdvantages(ProductProperties productProperties, String msisdn, int language) {
		if(((productProperties.getNight_advantages_call_da() == 0) && (productProperties.getNight_advantages_data_da() == 0))) return null;

		try {
			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
			HashSet<BalanceAndDate> balancesBonus = null;

			if((productProperties.getNight_advantages_call_da() > 0) && (productProperties.getNight_advantages_data_da() > 0)) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}, {productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});
			else if(productProperties.getNight_advantages_call_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}});
			else if(productProperties.getNight_advantages_data_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});

			if((balancesBonus != null) && (!balancesBonus.isEmpty())) {
				BalanceAndDate balanceBonusCall = null;
				BalanceAndDate balanceBonusData = null;

				for(BalanceAndDate balanceAndDate : balancesBonus) {
					if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_call_da())) balanceBonusCall = balanceAndDate;
					else if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_data_da())) balanceBonusData = balanceAndDate;
				}

				if((balanceBonusCall != null) && (balanceBonusData != null)) {
					long volumeData = balanceBonusData.getAccountValue()/(10*100);
					long volumeVoice = balanceBonusCall.getAccountValue()/100;

					if(volumeData >= 1024) {
						return new Object[] {(int)(volumeVoice/60), (int)(volumeVoice%60), new Formatter().format("%.2f", ((double)volumeData/1024)), (language == 2) ? "GB" : "Go", (new SimpleDateFormat("HH'H'mm")).format(balanceBonusData.getExpiryDate())};
					}
					else {
						return new Object[] {(int)(volumeVoice/60), (int)(volumeVoice%60), (volumeData + ""), (language == 2) ? "MB" : "Mo", (new SimpleDateFormat("HH'H'mm")).format(balanceBonusCall.getExpiryDate())};
					}
				}
				else if(balanceBonusCall != null) {
					long volumeVoice = balanceBonusCall.getAccountValue()/100;

					return new Object[] {(int)(volumeVoice/60), (int)(volumeVoice%60), "0", (language == 2) ? "MB" : "Mo", (new SimpleDateFormat("HH'H'mm")).format(balanceBonusCall.getExpiryDate())};
				}
				else if(balanceBonusData != null) {
					long volumeData = balanceBonusData.getAccountValue()/(10*100);

					if(volumeData >= 1024) {
						return new Object[] {0, 0, new Formatter().format("%.2f", ((double)volumeData/1024)), (language == 2) ? "GB" : "Go", (new SimpleDateFormat("HH'H'mm")).format(balanceBonusData.getExpiryDate())};
					}
					else {
						return new Object[] {0, 0, (volumeData + ""), (language == 2) ? "MB" : "Mo", (new SimpleDateFormat("HH'H'mm")).format(balanceBonusData.getExpiryDate())};
					}
				}
			}

		} catch(Throwable th) {
			
		}

		return null;
	}
}
