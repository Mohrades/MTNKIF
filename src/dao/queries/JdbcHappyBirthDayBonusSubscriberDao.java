package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import dao.DAO;
import dao.mapping.HappyBirthDayBonusSubscriberRowMapper;
import domain.models.HappyBirthDayBonusSubscriber;

public class JdbcHappyBirthDayBonusSubscriberDao {

	private DAO dao;

	public JdbcHappyBirthDayBonusSubscriberDao(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public int saveOneBirthdayBonusSubscriber(HappyBirthDayBonusSubscriber birthdayBonusSubscriber, boolean staged) {
		try {
			Date now = new Date();

			if(birthdayBonusSubscriber.getId() == 0) {
				// Tout autre caractère suivi d'un antislash est pris littéralement. Du coup, pour inclure un caractère antislash, écrivez deux antislashs (\\).
				// De plus, un guillemet simple peut être inclus dans une chaîne d'échappement en écrivant \', en plus de la façon normale ''.
				getJdbcTemplate().update("INSERT INTO MTN_KIF_BIRTHDAY_BONUS_EBA (MSISDN,NAME,LANGUAGE,BIRTH_DATE,PROCESSED) VALUES('" + birthdayBonusSubscriber.getValue() + "','" + birthdayBonusSubscriber.getName().replace("'", "''") + "'," + birthdayBonusSubscriber.getLanguage() + ",'" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(now) + "',0)");
			}
			else if(birthdayBonusSubscriber.getId() > 0) {
				if(staged) {
					if(birthdayBonusSubscriber.getAspu() > 0) return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET " + (((new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(now).equals((new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(birthdayBonusSubscriber.getBirth_date()))) ? "" : ("BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(now) + "', ")) + "ASPU = " + birthdayBonusSubscriber.getAspu() + ", LAST_UPDATE_TIME = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "' WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (ASPU IS NULL) AND (BONUS IS NULL))");
					else return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET " + (((new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(now).equals((new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(birthdayBonusSubscriber.getBirth_date()))) ? "" : ("BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(now) + "', ")) + "LAST_UPDATE_TIME = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "', PROCESSED = 1 WHERE ID = " + birthdayBonusSubscriber.getId());
				}
				else {
					if((birthdayBonusSubscriber.getBonus() > 0) && (birthdayBonusSubscriber.getAspu() > 0)) return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET BONUS = " + birthdayBonusSubscriber.getBonus() + ", BONUS_EXPIRES_IN = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(birthdayBonusSubscriber.getBonus_expires_in()) + "', LAST_UPDATE_TIME = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "', PROCESSED = 1 WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (ASPU IS NOT NULL) AND (BONUS IS NULL) AND (PROCESSED = 0))");
					else return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET LAST_UPDATE_TIME = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "', PROCESSED = 1 WHERE ID = " + birthdayBonusSubscriber.getId());
				}
			}
			else return -1;

		} catch(EmptyResultDataAccessException emptyEx) {
			return -1;

		} catch(Throwable th) {
			return -1;
		}

		return 0;
	}

	public int locking(HappyBirthDayBonusSubscriber birthdayBonusSubscriber, boolean locked) {
		try {
			if(locked) {
				return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET LOCKED = 1 WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (ASPU IS NOT NULL) AND (BONUS IS NULL) AND (PROCESSED = 0) AND (LOCKED = 0))");
			}
			else {
				return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET LOCKED = 0 WHERE (ID = " + birthdayBonusSubscriber.getId() + ")");
			}

		} catch(EmptyResultDataAccessException emptyEx) {

		} catch(Throwable th) {

		}

		return 0;
	}

	public HappyBirthDayBonusSubscriber getOneBirthdayBonusSubscriber(int id) {
		List<HappyBirthDayBonusSubscriber> birthdayBonusSubscribers = getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ID = " + id, new HappyBirthDayBonusSubscriberRowMapper());
		return birthdayBonusSubscribers.isEmpty() ? null : birthdayBonusSubscribers.get(0);
	}

	public HappyBirthDayBonusSubscriber getOneBirthdayBonusSubscriber(String msisdn, boolean bonusRequired) {
		List<HappyBirthDayBonusSubscriber> birthdayBonusSubscribers = null;

		if(bonusRequired) {
			birthdayBonusSubscribers = getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "') AND (MSISDN = '" + msisdn + "') AND (ASPU IS NOT NULL) AND (BONUS IS NOT NULL) AND (PROCESSED = 1))", new HappyBirthDayBonusSubscriberRowMapper());
		}
		else {
			birthdayBonusSubscribers = getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "') AND (MSISDN = '" + msisdn + "') AND (ASPU IS NOT NULL))", new HappyBirthDayBonusSubscriberRowMapper());
		}

		return birthdayBonusSubscribers.isEmpty() ? null : birthdayBonusSubscribers.get(0);
	}

	public List<HappyBirthDayBonusSubscriber> getOneBirthdayBonusSubscribers(boolean unStagedRecords) {
		if(unStagedRecords) {
			// return getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "') AND (PROCESSED = 0) AND (BONUS IS NULL) AND (ASPU IS NULL))", new HappyBirthDayBonusSubscriberRowMapper());
			return getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((PROCESSED = 0) AND (ASPU IS NULL) AND (BONUS IS NULL))", new HappyBirthDayBonusSubscriberRowMapper());
		}
		else {
			return getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE (BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "')", new HappyBirthDayBonusSubscriberRowMapper());
		}
	}

	public void deleteOneBirthdayBonusSubscriber(int id) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ID = " + id);
	}

	public void deleteOneBirthdayBonusSubscriber(String msisdn) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "') AND (MSISDN = '" + msisdn + "'))");
	}

	public boolean isBirthDayReported() {
		try {
			List<Map<String, Object>> result = getJdbcTemplate().queryForList("SELECT BIRTH_DATE FROM HVC_BIRTHDAY_BONUS_DATES_EBA Aa WHERE (Aa.BIRTH_DATE = TO_DATE('" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "', 'DD-MON-YY')) AND ((Aa.REPORTED_TO IS NULL) OR (Aa.REPORTED_TO != Aa.BIRTH_DATE))");
			return result.isEmpty() ? false : true ;

		} catch(EmptyResultDataAccessException empty) {

		} catch(Exception ex) {

		} catch(Throwable th) {

		}

		return true;
	}

}
