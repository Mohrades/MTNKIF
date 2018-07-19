package product;

import java.text.SimpleDateFormat;
import java.util.Date;
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
				 if(subscriber.getLast_update_time() == null) { // update new initial status
					 statusCode = new PricePlanCurrentActions().isActivated(productProperties, dao, msisdn);

					 if(statusCode >= 0) {
						 if(((statusCode == 0) && (!subscriber.isFlag())) || ((statusCode == 1) && (subscriber.isFlag()))) {
							 subscriber.setFlag((statusCode == 0) ? true : false);

							 subscriber.setId(-subscriber.getId()); // negative id to update database
							 boolean registered = (new JdbcSubscriberDao(dao).saveOneSubscriber(subscriber) == 1) ? true : false;
							 subscriber.setId(-subscriber.getId()); // to release negative id

							 if(!registered) statusCode = -1; // check databse is actually updated
						 }
					 }
					 else if(statusCode == -1) statusCode = -1; // erreur AIR
				 }
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

	@SuppressWarnings("deprecation")
	public Object[] getBonusSMS(ProductProperties productProperties, String msisdn, int language) {

		/*On net
		ACC 212  0  Wed Jul 04 23:59:59 WAT 2018  null
		ACC 1  0  Sun Jul 01 23:59:59 WAT 2018  Wed Aug 01 23:59:59 WAT 2018
		ACC 211  55  Wed Jul 04 23:59:59 WAT 2018  null

		on net
		ACC 212  0  Wed Jul 04 23:59:59 WAT 2018  null
		ACC 1  0  Sun Jul 01 23:59:59 WAT 2018  Wed Aug 01 23:59:59 WAT 2018
		ACC 211  56  Wed Jul 04 23:59:59 WAT 2018  null

		off net
		ACC 212  1  Wed Jul 04 23:59:59 WAT 2018  null
		ACC 1  0  Sun Jul 01 23:59:59 WAT 2018  Wed Aug 01 23:59:59 WAT 2018
		ACC 211  57  Wed Jul 04 23:59:59 WAT 2018  null*/

		try {
			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
			HashSet<AccumulatorInformation> balanceBonusSms = (productProperties.getBonus_sms_remaining_accumulator() > 0) ? request.getAccumulators(msisdn, new int[][] {{productProperties.getBonus_sms_remaining_accumulator(), productProperties.getBonus_sms_remaining_accumulator()}}) : null;

			if((balanceBonusSms == null) || (balanceBonusSms.isEmpty())) {
				return null;
			}
			else {
				Object[] bonusSms = null;

				for(AccumulatorInformation accumulator : balanceBonusSms) {
					// System.out.println("ACC " + ac.getAccumulatorID() + "  " + ac.getAccumulatorValue() + "  " + ac.getAccumulatorStartDate()  + "  " + ac.getAccumulatorEndDate());
					if(accumulator.getAccumulatorID() == productProperties.getBonus_sms_remaining_accumulator()) {
						Date expires_in = accumulator.getAccumulatorEndDate();

						if((expires_in == null) || ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(expires_in).equals("9999-12-30 13:00:00"))) {
							expires_in = new Date();
							expires_in.setHours(23);
							expires_in.setMinutes(59);
							expires_in.setSeconds(59);
						}

						if(expires_in.after(new Date())) {
							bonusSms = new Object[] {(productProperties.getBonus_sms_threshold() - accumulator.getAccumulatorValue() + 50), (language == 2) ? (new SimpleDateFormat("HH'H'mm")).format(expires_in) : (new SimpleDateFormat("HH'H'mm")).format(expires_in)};
							break;
						}
					}
				}

				return bonusSms;
			}

		} catch(Throwable th) {
			
		}

		return null;
	}

	@SuppressWarnings({ "resource", "deprecation" })
	public Object[] getNightAdvantages(ProductProperties productProperties, String msisdn, int language) {
		if(((productProperties.getNight_advantages_call_da() == 0) && (productProperties.getNight_advantages_data_da() == 0))) return null;

		try {
			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
			HashSet<BalanceAndDate> balancesBonus = null;

			if((productProperties.getNight_advantages_call_da() > 0) && (productProperties.getNight_advantages_data_da() > 0)) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}, {productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});
			else if(productProperties.getNight_advantages_call_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}});
			else if(productProperties.getNight_advantages_data_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});

			// BONUS GRANTED
			// 141 299996  Thu Jul 05 23:59:59 WAT 2018 : System.out.println(balanceBonusData.getAccountID() + " " + balanceBonusData.getAccountValue() + "  " + balanceBonusData.getExpiryDate());
			// 118 360000  Thu Jul 05 23:59:59 WAT 2018 : System.out.println(balanceBonusCall.getAccountID() + " " + balanceBonusCall.getAccountValue() + "  " + balanceBonusCall.getExpiryDate());

			// NO BONUS
			// 141 0  Thu Dec 30 13:00:00 WAT 9999 : System.out.println(balanceBonusData.getAccountID() + " " + balanceBonusData.getAccountValue() + "  " + balanceBonusData.getExpiryDate());
			// 118 0  Thu Dec 30 13:00:00 WAT 9999 : System.out.println(balanceBonusCall.getAccountID() + " " + balanceBonusCall.getAccountValue() + "  " + balanceBonusCall.getExpiryDate());

			if((balancesBonus != null) && (!balancesBonus.isEmpty())) {
				BalanceAndDate balanceBonusCall = null;
				BalanceAndDate balanceBonusData = null;

				for(BalanceAndDate balanceAndDate : balancesBonus) {
					if(balanceAndDate.getExpiryDate() != null) {
						Date expiryDate = (Date)balanceAndDate.getExpiryDate();

						if((expiryDate == null) || ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(expiryDate).equals("9999-12-30 13:00:00")));
						else {
							expiryDate.setHours(06);
							expiryDate.setMinutes(00);
							expiryDate.setSeconds(00);

							if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_call_da())) {
								balanceBonusCall = balanceAndDate;
								balanceBonusCall.setExpiryDate(expiryDate);
							}
							else if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_data_da())) {
								balanceBonusData = balanceAndDate;
								balanceBonusData.setExpiryDate(expiryDate);
							}
						}
					}
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
