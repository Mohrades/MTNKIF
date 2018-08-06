package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.PAMRunningReportingRowMapper;
import domain.models.PAMRunningReporting;

public class JdbcPAMRunningReportingDao {

	private DAO dao;

	public JdbcPAMRunningReportingDao(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOnePAMRunningReporting(PAMRunningReporting reporting, boolean flag) {
		Date now = new Date();
		String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();

		if(flag) getJdbcTemplate().update("INSERT INTO " + tableName + " (SUBSCRIBER,FLAG,CREATED_DATE_TIME,CREATED_DATE_TIME_INDEX,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'," + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ",'" + reporting.getOriginOperatorID().replace("'", "''") + "')");
		else getJdbcTemplate().update("INSERT INTO " + tableName + " (SUBSCRIBER,CREATED_DATE_TIME,CREATED_DATE_TIME_INDEX,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'," + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ",'" + reporting.getOriginOperatorID().replace("'", "''") + "')");
	}

	public List<PAMRunningReporting> getPAMRunningReporting(int subscriber) {
		Date now = new Date();
		String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();
		return getJdbcTemplate().query("SELECT ID,SUBSCRIBER,FLAG,CREATED_DATE_TIME,ORIGIN_OPERATOR_ID FROM " + tableName + " WHERE (SUBSCRIBER = " + subscriber + ") ORDER BY CREATED_DATE_TIME DESC", new PAMRunningReportingRowMapper());
	}

	public void notifyNightAdvantages(PAMRunningReporting pamRunningReporting, int subscriberId, boolean flag, boolean sms) {
		Date now = new Date();
		String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();

		// FAILED, COMPLETED
		if(sms) {
			if(pamRunningReporting.getId() > 0) getJdbcTemplate().update("UPDATE " + tableName + " SET " + (flag ? ("FLAG = " + ((pamRunningReporting.isFlag()) ? "1" : "0") + ", ") : "") + "SMS = 1 WHERE (ID = " + pamRunningReporting.getId() + ")");
			else if (subscriberId > 0) getJdbcTemplate().update("UPDATE " + tableName + " SET " + (flag ? ("FLAG = " + ((pamRunningReporting.isFlag()) ? "1" : "0") + ", ") : "") + "SMS = 1 WHERE ((CREATED_DATE_TIME_INDEX = " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (SUBSCRIBER = " + subscriberId + "))");
		}
		else {
			if(pamRunningReporting.getId() > 0) if(pamRunningReporting.getId() > 0) getJdbcTemplate().update("UPDATE " + tableName + " SET " + (flag ? ("FLAG = " + ((pamRunningReporting.isFlag()) ? "1" : "0") + ", ") : "") + "SMS = 0 WHERE (ID = " + pamRunningReporting.getId() + ")");
			else if (subscriberId > 0) getJdbcTemplate().update("UPDATE " + tableName + " SET " + (flag ? ("FLAG = " + ((pamRunningReporting.isFlag()) ? "1" : "0") + ", ") : "") + "SMS = 0 WHERE ((CREATED_DATE_TIME_INDEX = " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (SUBSCRIBER = " + subscriberId + "))");
		}
	}
}
