package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import dao.DAO;
import dao.mapping.BirthDayBonusSubscriberRowMapper;
import domain.models.BirthDayBonusSubscriber;

public class BirthDayBonusSubscriberDAOJdbc {

	private DAO dao;

	public BirthDayBonusSubscriberDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public int saveOneBirthdayBonusSubscriber(BirthDayBonusSubscriber birthdayBonusSubscriber) {
		try {
			if(birthdayBonusSubscriber.getId() == 0) {
				// Tout autre caractère suivi d'un antislash est pris littéralement. Du coup, pour inclure un caractère antislash, écrivez deux antislashs (\\).
				// De plus, un guillemet simple peut être inclus dans une chaîne d'échappement en écrivant \', en plus de la façon normale ''.
				getJdbcTemplate().update("INSERT INTO MTN_KIF_BIRTHDAY_BONUS_EBA (MSISDN,NAME,LANGUAGE,BIRTH_DATE) VALUES('" + birthdayBonusSubscriber.getValue() + "','" + birthdayBonusSubscriber.getName().replace("'", "''") + "'," + birthdayBonusSubscriber.getLanguage() + ",'" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "')");
			}
			else if(birthdayBonusSubscriber.getId() > 0) {
				if(birthdayBonusSubscriber.getBonus() > 0) {
					return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET ASPU = " + birthdayBonusSubscriber.getAspu() + ", BONUS = " + birthdayBonusSubscriber.getBonus() + ", BONUS_EXPIRES_IN = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(birthdayBonusSubscriber.getBonus_expires_in()) + "', LAST_UPDATE_TIME = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "' WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (BONUS IS NULL))");
				}
				else if(birthdayBonusSubscriber.getAspu() > 0) {
					return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET ASPU = " + birthdayBonusSubscriber.getAspu() + " WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (BONUS IS NULL))");
				}
			}
			else {
				return -1;
			}

		} catch(EmptyResultDataAccessException emptyEx) {
			return -1;

		} catch(Throwable th) {
			return -1;
		}

		return 0;
	}

	public int locking(BirthDayBonusSubscriber birthdayBonusSubscriber, boolean locked) {
		try {
			if(locked) {
				return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET LOCKED = 1 WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (BONUS IS NULL) AND (LOCKED = 0))");
			}
			else {
				return getJdbcTemplate().update("UPDATE MTN_KIF_BIRTHDAY_BONUS_EBA SET LOCKED = 0 WHERE (ID = " + birthdayBonusSubscriber.getId() + ")");
			}

		} catch(EmptyResultDataAccessException emptyEx) {

		} catch(Throwable th) {

		}

		return 0;
	}

	public BirthDayBonusSubscriber getOneBirthdayBonusSubscriber(int id) {
		List<BirthDayBonusSubscriber> birthdayBonusSubscribers = getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ID = " + id, new BirthDayBonusSubscriberRowMapper());
		return birthdayBonusSubscribers.isEmpty() ? null : birthdayBonusSubscribers.get(0);
	}

	public List<BirthDayBonusSubscriber> getOneBirthdayBonusSubscribers() {
		return getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE (BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "')", new BirthDayBonusSubscriberRowMapper());
	}

	public void deleteOneBirthdayBonusSubscriber(int id) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ID = " + id);
	}

	public void deleteOneBirthdayBonusSubscriber(String msisdn) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "') AND (MSISDN = '" + msisdn + "'))");
	}

	public boolean isBirthDayReported() {
		try {
			List<Map<String, Object>> result = getJdbcTemplate().queryForList("SELECT BIRTH_DATE FROM HVC_BIRTHDAY_BONUS_DATES_EBA Aa WHERE (Aa.BIRTH_DATE = TO_DATE('" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "', 'DD-MON-YY')) AND ((Aa.REPORTED_TO IS NULL) OR (Aa.REPORTED_TO != Aa.BIRTH_DATE))");
			return result.isEmpty() ? false : true ;

		} catch(EmptyResultDataAccessException empty) {
			
		} catch(Exception ex) {
			
		} catch(Throwable th) {
			
		}

		return true;
	}

}
