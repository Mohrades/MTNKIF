package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.USSDRequestRowMapper;
import domain.models.USSDRequest;

public class USSDRequestDAOJdbc {

	private DAO dao;

	public USSDRequestDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOneUSSD(USSDRequest ussd) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if(ussd.getId() == 0) {
				getJdbcTemplate().update("INSERT INTO MTN_KIF_USSD_EBA (SESSIONID,MSISDN,INPUT,STEP,LAST_UPDATE_TIME) VALUES(" + ussd.getSessionId() + ",'" + ussd.getMsisdn() + "','" + ussd.getInput() + "'," + ussd.getStep() + ",TIMESTAMP '" + dateFormat.format(new Date()) + "')");			
			}
			else if(ussd.getId() > 0) {
				getJdbcTemplate().update("UPDATE MTN_KIF_USSD_EBA SET STEP = " + ussd.getStep() + ", INPUT = '" + ussd.getInput() + "', LAST_UPDATE_TIME = TIMESTAMP '" + dateFormat.format(new Date()) + "' WHERE ((ID = " + ussd.getId() + ") AND (SESSIONID = " + ussd.getSessionId() + ") AND (MSISDN = '" + ussd.getMsisdn() + "'))");
			}

		} catch(EmptyResultDataAccessException emptyEx) {

		} catch(Throwable th) {

		}
	}

	public USSDRequest getOneUSSD(int id) {
		List<USSDRequest> ussds = getJdbcTemplate().query("SELECT ID,SESSIONID,MSISDN,STEP,INPUT,LAST_UPDATE_TIME FROM MTN_KIF_USSD_EBA WHERE ID = " + id, new USSDRequestRowMapper());
		return ussds.isEmpty() ? null : ussds.get(0);
	}

	public USSDRequest getOneUSSD(long sessionId, String msisdn)  {
		List<USSDRequest> ussds = getJdbcTemplate().query("SELECT ID,SESSIONID,MSISDN,STEP,INPUT,LAST_UPDATE_TIME FROM MTN_KIF_USSD_EBA WHERE ((SESSIONID = " + sessionId + ") AND (MSISDN = '" + msisdn + "'))", new USSDRequestRowMapper());
		return ussds.isEmpty() ? null : ussds.get(0);
	}

	public void deleteOneUSSD(int id) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_USSD_EBA WHERE ID = " + id);
	}

	public void deleteOneUSSD(long sessionId) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_USSD_EBA WHERE SESSIONID = " + sessionId + "");
	}

}
