package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.SubscriberRowMapper;
import domain.models.Subscriber;

public class SubscriberDAOJdbc {

	private DAO dao;

	public SubscriberDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	@SuppressWarnings("deprecation")
	public int saveOneSubscriber(Subscriber subscriber) {
		try {
			if(subscriber.getId() == 0) {
				Date now = new Date();
				Date next_month = new Date();
				next_month.setYear(now.getYear());
				next_month.setMonth(now.getMonth());
				next_month.setDate(now.getDate() + 30);
				next_month.setHours(now.getHours());
				next_month.setMinutes(now.getMinutes());
				next_month.setSeconds(now.getSeconds());

				getJdbcTemplate().update("INSERT INTO MTN_KIF_MSISDN_EBA (MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED) VALUES('" + subscriber.getValue() + "'," + (subscriber.isFlag() ? 1 : 0) + "," + (subscriber.isCrbt() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "',TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(next_month) + "',0)");
				return 1;
			}
			else if(subscriber.getId() > 0) {
				return getJdbcTemplate().update("UPDATE MTN_KIF_MSISDN_EBA SET FLAG = " + (subscriber.isFlag() ? 1 : 0) + ", LOCKED = 1 WHERE ((ID = " + subscriber.getId() + ") AND (FLAG = " + (subscriber.isFlag() ? 0 : 1) + ") AND (LOCKED = 0))");
			}

		} catch(EmptyResultDataAccessException emptyEx) {
			return -1;

		} catch(Throwable th) {
			return -1;
		}

		return 0;
	}

	public void releaseLock(Subscriber subscriber, boolean rollback) {
		if(rollback) {
			getJdbcTemplate().update("UPDATE MTN_KIF_MSISDN_EBA SET FLAG = (CASE FLAG WHEN 1 THEN 0 ELSE 1 END), LOCKED = 0 WHERE ((ID = " + subscriber.getId() + ") AND (LOCKED = 1))");
		}
		else {
			getJdbcTemplate().update("UPDATE MTN_KIF_MSISDN_EBA SET LAST_UPDATE_TIME = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "', LOCKED = 0 WHERE ((ID = " + subscriber.getId() + ") AND (LOCKED = 1))");
		}
	}

	public int setCRBTFlag(Subscriber subscriber) {
		try {
			if(subscriber.getId() > 0) {
				// return getJdbcTemplate().update("UPDATE MTN_KIF_MSISDN_EBA SET CRBT = " + (subscriber.isCrbt() ? 1 : 0) + ", CRBT_NEXT_RENEWAL_DATE = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "' WHERE ((ID = " + subscriber.getId() + ") AND (FLAG = 'TRUE'))");
				return getJdbcTemplate().update("UPDATE MTN_KIF_MSISDN_EBA SET CRBT = " + (subscriber.isCrbt() ? 1 : 0) + ", CRBT_NEXT_RENEWAL_DATE = TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(subscriber.getCrbtNextRenewalDate()) + "' WHERE (ID = " + subscriber.getId() + ")");
			}

		} catch(EmptyResultDataAccessException emptyEx) {
			return -1;

		} catch(Throwable th) {
			return -1;
		}

		return 0;		
	}

	public Subscriber getOneSubscriber(int id, boolean locked) {
		List<Subscriber> subscribers = getJdbcTemplate().query("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE ((ID = " + id + ") AND (LOCKED = " + (locked ? 1 : 0) + "))", new SubscriberRowMapper());
		return subscribers.isEmpty() ? null : subscribers.get(0);
	}

	public Subscriber getOneSubscriber(int id) {
		List<Subscriber> subscribers = getJdbcTemplate().query("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE ID = " + id, new SubscriberRowMapper());
		return subscribers.isEmpty() ? null : subscribers.get(0);
	}

	public Subscriber getOneSubscriber(String msisdn, boolean locked) {
		List<Subscriber> subscribers = getJdbcTemplate().query("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE ((MSISDN = '" + msisdn + "') AND (LOCKED = " + (locked ? 1 : 0) + "))", new SubscriberRowMapper());
		return subscribers.isEmpty() ? null : subscribers.get(0);
	}

	public Subscriber getOneSubscriber(String msisdn) {
		List<Subscriber> subscribers = getJdbcTemplate().query("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE (MSISDN = '" + msisdn + "')", new SubscriberRowMapper());
		return subscribers.isEmpty() ? null : subscribers.get(0);
	}

	public List<Subscriber> getAllSubscribers(String msisdn, boolean locked) {
		return  getJdbcTemplate().query("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE LOCKED = " + (locked ? 1 : 0), new SubscriberRowMapper());
	}

	public List<Subscriber> getAllSubscribers(String msisdn) {
		return  getJdbcTemplate().query("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA", new SubscriberRowMapper());
	}

	public void deleteOneSubscriber(int id) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_MSISDN_EBA WHERE ((ID = " + id + ") AND (LOCKED = 0))");
	}

	public void deleteOneSubscriber(String msisdn) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_MSISDN_EBA WHERE ((MSISDN = '" + msisdn + "') AND (LOCKED = 0))");
	}
}
