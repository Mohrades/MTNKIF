package product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;
import org.springframework.context.MessageSource;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcHappyBirthDayBonusSubscriberDao;
import dao.queries.JdbcHappyBirthDayBonusRollBackDao;
import domain.models.HappyBirthDayBonusSubscriber;
import domain.models.RollBack;
import exceptions.AirAvailabilityException;
import util.BalanceAndDate;
import util.DedicatedAccount;
import util.OfferInformation;

public class HappyBirthDayBonusActions {

	ProductProperties productProperties;

	public HappyBirthDayBonusActions(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	@SuppressWarnings("deprecation")
	public int doActions(DAO dao, HappyBirthDayBonusSubscriber birthdayBonusSubscriber) throws AirAvailabilityException {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

		int responseCode = -1;

		// attempts
		int retry = 0;

		while(productProperties.getAir_preferred_host() == -1) {
			if(retry >= 3) throw new AirAvailabilityException();

			productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
			retry++;
		}

		retry = 0;

		try {
			Date expires = new Date();
			expires.setDate(expires.getDate() + 0);
			expires.setSeconds(59); expires.setMinutes(59); expires.setHours(23);
			// set bonus expiry date
			birthdayBonusSubscriber.setBonus_expires_in(expires);

			if((new JdbcHappyBirthDayBonusSubscriberDao(dao)).locking(birthdayBonusSubscriber, true) > 0) {
				responseCode = 1;

				HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
				// data bonus
				if(productProperties.getHappy_birthday_bonus_data_volume() != 0) {
					if(productProperties.getHappy_birthday_bonus_data_da() == 0) balances.add(new BalanceAndDate(0, productProperties.getHappy_birthday_bonus_data_volume(), null));
					else balances.add(new DedicatedAccount(productProperties.getHappy_birthday_bonus_data_da(), productProperties.getHappy_birthday_bonus_data_volume(), expires));
				}
				// voice bonus
				if(productProperties.getHappy_birthday_bonus_voice_volume() != 0) {
					if(productProperties.getHappy_birthday_bonus_voice_da() == 0) balances.add(new BalanceAndDate(0, productProperties.getHappy_birthday_bonus_voice_volume(), null));
					else balances.add(new DedicatedAccount(productProperties.getHappy_birthday_bonus_voice_da(), productProperties.getHappy_birthday_bonus_voice_volume(), expires));
				}

				// update Anumber Balance
				if((balances.isEmpty()) || (request.updateBalanceAndDate(birthdayBonusSubscriber.getValue(), balances, productProperties.getSms_notifications_header(), "HappyBirthdayBonus", "eBA"))) {
					// update Anumber Offer
					if((productProperties.getHappy_birthday_bonus_offer_id() == 0) || (request.updateOffer(birthdayBonusSubscriber.getValue(), productProperties.getHappy_birthday_bonus_offer_id(), null, expires, null, "eBA"))) {
						if((new JdbcHappyBirthDayBonusSubscriberDao(dao)).saveOneBirthdayBonusSubscriber(birthdayBonusSubscriber, false) > 0) {
							responseCode = 0;
						}
						else {
							responseCode = -1;
							new JdbcHappyBirthDayBonusRollBackDao(dao).saveOneRollBack(new RollBack(0, 3, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
						}
					}
					// rollback
					else {
						if(request.isSuccessfully()) {
							balances.clear();
							// data bonus
							if(productProperties.getHappy_birthday_bonus_data_volume() != 0) {
								if(productProperties.getHappy_birthday_bonus_data_da() == 0) balances.add(new BalanceAndDate(0, -productProperties.getHappy_birthday_bonus_data_volume(), null));
								else balances.add(new DedicatedAccount(productProperties.getHappy_birthday_bonus_data_da(), -productProperties.getHappy_birthday_bonus_data_volume(), expires));
							}
							// voice bonus
							if(productProperties.getHappy_birthday_bonus_voice_volume() != 0) {
								if(productProperties.getHappy_birthday_bonus_voice_da() == 0) balances.add(new BalanceAndDate(0, -productProperties.getHappy_birthday_bonus_voice_volume(), null));
								else balances.add(new DedicatedAccount(productProperties.getHappy_birthday_bonus_voice_da(), -productProperties.getHappy_birthday_bonus_voice_volume(), expires));
							}

							if((balances.isEmpty()) || (request.updateBalanceAndDate(birthdayBonusSubscriber.getValue(), balances, productProperties.getSms_notifications_header(), "HappyBirthdayBonus", "eBA")));
							else {
								if(request.isSuccessfully()) new JdbcHappyBirthDayBonusRollBackDao(dao).saveOneRollBack(new RollBack(0, 1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
								else new JdbcHappyBirthDayBonusRollBackDao(dao).saveOneRollBack(new RollBack(0, -1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
							}
						}
						else {
							new JdbcHappyBirthDayBonusRollBackDao(dao).saveOneRollBack(new RollBack(0, -2, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
						}
					}
				}
				// rollback
				else {
					if(request.isSuccessfully()) new JdbcHappyBirthDayBonusRollBackDao(dao).saveOneRollBack(new RollBack(0, 1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
					else new JdbcHappyBirthDayBonusRollBackDao(dao).saveOneRollBack(new RollBack(0, -1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
				}
			}

		} catch(Throwable th) {

		} finally {
			if(responseCode >= 0) {
				// unlock
				((new JdbcHappyBirthDayBonusSubscriberDao(dao))).locking(birthdayBonusSubscriber, false);

				if(request.isWaitingForResponse()) {
					if(request.isSuccessfully()) ;
					else {
						productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host())) ;
						throw new AirAvailabilityException() ;
					}
				}
			}
		}

		return responseCode;
	}

	public Object [] getStatus(MessageSource i18n, DAO dao, String msisdn, int language) {
		int statusCode = -1; String message = null;
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

		int offer = productProperties.getHappy_birthday_bonus_offer_id();
		HashSet<OfferInformation> offers = (offer > 0) ? request.getOffers(msisdn, new int[][]{{offer,offer}}, false, null, false) : null;

		if((offer > 0) && ((offers == null) || offers.size() == 0)) {
			Object [] requestStatus = (new PricePlanCurrent()).getStatus(productProperties, i18n, dao, msisdn, language, false);
			statusCode = ((int)(requestStatus[0]) == 0) ? 1 : ((int)(requestStatus[0]) == 1) ? 1 : -1;

			if((int)(requestStatus[0]) == 0) message = i18n.getMessage("happy.birthday.bonus.status.failed", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
			else if((int)(requestStatus[0]) == 1) message = (String)(requestStatus[1]);
			else message = i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
		}
		else {
			BalanceAndDate balanceData = request.getBalanceAndDate(msisdn, productProperties.getHappy_birthday_bonus_data_da());
			BalanceAndDate balanceVoice = request.getBalanceAndDate(msisdn, productProperties.getHappy_birthday_bonus_voice_da());

			if((balanceVoice == null) || (balanceData == null)) {
				message = i18n.getMessage("service.internal.error", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
			}
			else {
				statusCode = 0;
				long volumeData = balanceData.getAccountValue()/(10*100);
				long volumeVoice = balanceVoice.getAccountValue()/100;

				if(volumeData >= 1024) {
					message = i18n.getMessage("happy.birthday.bonus.status", new Object [] {((volumeVoice/60) + ""), ((volumeVoice%60) + ""), new Formatter().format("%.2f", ((double)volumeData/1024)), (language == 2) ? "GB" : "Go", (language == 2) ? (new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm")).format(balanceData.getExpiryDate()) : (new SimpleDateFormat("dd/MM/yyyy 'a' HH:mm")).format(balanceVoice.getExpiryDate())}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
				}
				else {
					message = i18n.getMessage("happy.birthday.bonus.status", new Object [] {((volumeVoice/60) + ""), ((volumeVoice%60) + ""), (volumeData + ""), (language == 2) ? "MB" : "Mo", (language == 2) ? (new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm")).format(balanceData.getExpiryDate()) : (new SimpleDateFormat("dd/MM/yyyy 'a' HH:mm")).format(balanceVoice.getExpiryDate())}, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
				}
			}
		}

		return new Object [] {statusCode, message};
	}

}
