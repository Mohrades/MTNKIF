package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.CRBTReportingRowMapper;
import domain.models.CRBTReporting;

public class JdbcCRBTReportingDao {

	private DAO dao;

	public JdbcCRBTReportingDao(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOneCRBTReporting(CRBTReporting reporting) {
		Date now = new Date();
		String tableName = "MTN_KIF_CRBT_REPORT_EBA_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();

		if(reporting.getToneBoxID() != null) getJdbcTemplate().update("INSERT INTO " + tableName + " (SUBSCRIBER,TONEBOXID,FLAG,AUTO,CREATED_DATE_TIME,CREATED_DATE_TIME_INDEX,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + ",'" + reporting.getToneBoxID() + "'," + (reporting.isFlag() ? 1 : 0) + "," + (reporting.isAuto() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'," + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ",'" + reporting.getOriginOperatorID().replace("'", "''") + "')");
		else getJdbcTemplate().update("INSERT INTO " + tableName + " (SUBSCRIBER,FLAG,AUTO,CREATED_DATE_TIME,CREATED_DATE_TIME_INDEX,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 : 0) + "," + (reporting.isAuto() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'," + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ",'" + reporting.getOriginOperatorID().replace("'", "''") + "')");
	}

	public List<CRBTReporting> getCRBTReporting(int subscriber) {
		Date now = new Date();
		String tableName = "MTN_KIF_CRBT_REPORT_EBA_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();

		return getJdbcTemplate().query("SELECT ID,SUBSCRIBER,FLAG,AUTO,CREATED_DATE_TIME,ORIGIN_OPERATOR_ID,TONEBOXID FROM " + tableName + " WHERE (SUBSCRIBER = " + subscriber + ") ORDER BY CREATED_DATE_TIME DESC", new CRBTReportingRowMapper());
	}

}
