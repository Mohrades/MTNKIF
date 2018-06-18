package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.BirthdayBonusSubscriberRowMapper;
import domain.models.BirthdayBonusSubscriber;

public class BirthdayBonusSubscriberDAOJdbc {

	private DAO dao;

	public BirthdayBonusSubscriberDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public int saveOneBirthdayBonusSubscriber(BirthdayBonusSubscriber birthdayBonusSubscriber) {
		try {
			if(birthdayBonusSubscriber.getId() == 0) {
				// Tout autre caract�re suivi d'un antislash est pris litt�ralement. Du coup, pour inclure un caract�re antislash, �crivez deux antislashs (\\).
				// De plus, un guillemet simple peut �tre inclus dans une cha�ne d'�chappement en �crivant \', en plus de la fa�on normale ''.
				getJdbcTemplate().update("INSERT INTO MTNKIF_BIRTHDAY_BONUS_MSISDN_E (MSISDN,NAME,LANGUAGE,BIRTH_DATE) VALUES('" + birthdayBonusSubscriber.getValue() + "','" + birthdayBonusSubscriber.getName().replace("'", "''") + "'," + birthdayBonusSubscriber.getLanguage() + ",'" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "')");
			}
			else if(birthdayBonusSubscriber.getId() > 0) {
				if(birthdayBonusSubscriber.getBonus() > 0) {
					return getJdbcTemplate().update("UPDATE MTNKIF_BIRTHDAY_BONUS_MSISDN_E SET ASPU = " + birthdayBonusSubscriber.getAspu() + ", BONUS = " + birthdayBonusSubscriber.getBonus() + ", BONUS_EXPIRES_IN = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(birthdayBonusSubscriber.getBonus_expires_in()) + "', LAST_UPDATE_TIME = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "' WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (BONUS IS NULL))");
				}
				else if(birthdayBonusSubscriber.getAspu() > 0) {
					return getJdbcTemplate().update("UPDATE MTNKIF_BIRTHDAY_BONUS_MSISDN_E SET ASPU = " + birthdayBonusSubscriber.getAspu() + " WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (BONUS IS NULL))");
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

	public int locking(BirthdayBonusSubscriber birthdayBonusSubscriber, boolean locked) {
		try {
			if(locked) {
				return getJdbcTemplate().update("UPDATE MTNKIF_BIRTHDAY_BONUS_MSISDN_E SET LOCKED = 1 WHERE ((ID = " + birthdayBonusSubscriber.getId() + ") AND (BONUS IS NULL) AND (LOCKED = 0))");
			}
			else {
				return getJdbcTemplate().update("UPDATE MTNKIF_BIRTHDAY_BONUS_MSISDN_E SET LOCKED = 0 WHERE (ID = " + birthdayBonusSubscriber.getId() + ")");
			}

		} catch(EmptyResultDataAccessException emptyEx) {

		} catch(Throwable th) {

		}

		return 0;
	}

	public BirthdayBonusSubscriber getOneBirthdayBonusSubscriber(int id) {
		List<BirthdayBonusSubscriber> birthdayBonusSubscribers = getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTNKIF_BIRTHDAY_BONUS_MSISDN_E WHERE ID = " + id, new BirthdayBonusSubscriberRowMapper());
		return birthdayBonusSubscribers.isEmpty() ? null : birthdayBonusSubscribers.get(0);
	}

	public List<BirthdayBonusSubscriber> getOneBirthdayBonusSubscribers() {
		return getJdbcTemplate().query("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTNKIF_BIRTHDAY_BONUS_MSISDN_E WHERE (BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "')", new BirthdayBonusSubscriberRowMapper());
	}

	public void deleteOneBirthdayBonusSubscriber(int id) {
		getJdbcTemplate().update("DELETE FROM MTNKIF_BIRTHDAY_BONUS_MSISDN_E WHERE ID = " + id);
	}

	public void deleteOneBirthdayBonusSubscriber(String msisdn) {
		getJdbcTemplate().update("DELETE FROM MTNKIF_BIRTHDAY_BONUS_MSISDN_E WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "') AND (MSISDN = '" + msisdn + "'))");
	}

}