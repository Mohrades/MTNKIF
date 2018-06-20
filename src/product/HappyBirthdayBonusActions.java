package product;

import java.util.Date;
import java.util.HashSet;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.BirthdayBonusSubscriberDAOJdbc;
import dao.queries.RollBackDAOJdbc;
import domain.models.BirthDayBonusSubscriber;
import domain.models.RollBack;
import exceptions.AirAvailabilityException;
import util.BalanceAndDate;
import util.DedicatedAccount;

public class HappyBirthdayBonusActions {

	ProductProperties productProperties;

	public HappyBirthdayBonusActions(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	@SuppressWarnings("deprecation")
	public int doActions(DAO dao, BirthDayBonusSubscriber birthdayBonusSubscriber) throws AirAvailabilityException {
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

		if((productProperties.getAir_preferred_host() != -1) && ((request.getBalanceAndDate(birthdayBonusSubscriber.getValue(), 0)) != null)) {
			try {
				Date expires = new Date();
				expires.setDate(expires.getDate() + 0);
				expires.setSeconds(59);expires.setMinutes(59);expires.setHours(23);
				// set bonus expiry date
				birthdayBonusSubscriber.setBonus_expires_in(expires);

				if((new BirthdayBonusSubscriberDAOJdbc(dao)).locking(birthdayBonusSubscriber, true) > 0) {
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
							if((new BirthdayBonusSubscriberDAOJdbc(dao)).saveOneBirthdayBonusSubscriber(birthdayBonusSubscriber) > 0) {
								responseCode = 0;
							}
						}
						// rollback
						else {
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
								if(request.isSuccessfully()) new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
								else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
							}
						}
					}
					// rollback
					else {
						if(request.isSuccessfully()) new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
						else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -1, 0, birthdayBonusSubscriber.getValue(), birthdayBonusSubscriber.getValue(), null));
					}
				}

			} catch(Throwable th) {

			} finally {
				if(responseCode >= 0) {
					// unlock
					((new BirthdayBonusSubscriberDAOJdbc(dao))).locking(birthdayBonusSubscriber, false);

					if(request.isWaitingForResponse()) {
						if(request.isSuccessfully());
						else {
							productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
							throw new AirAvailabilityException();
						}
					}
				}
			}
		}
		else {
			if((productProperties.getAir_preferred_host() != -1) && (request.isWaitingForResponse())) {
				if(request.isSuccessfully());
				else {
					productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
					throw new AirAvailabilityException();
				}
			}
		}

		return responseCode;
	}

}
