package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.PAMRunningReportingRowMapper;
import domain.models.PAMRunningReporting;

public class PAMRunningReportingDAOJdbc {

	private DAO dao;

	public PAMRunningReportingDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOnePAMRunningReporting(PAMRunningReporting reporting) {
		Date now = new Date();
		String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy")).format(now)).toUpperCase();
		getJdbcTemplate().update("INSERT INTO " + tableName + " (SUBSCRIBER,FLAG,CREATED_DATE_TIME,CREATED_DATE_TIME_INDEX,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'," + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ",'" + reporting.getOriginOperatorID().replace("'", "''") + "')");
	}

	public List<PAMRunningReporting> getPAMRunningReporting(int subscriber) {
		Date now = new Date();
		String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy")).format(now)).toUpperCase();
		return getJdbcTemplate().query("SELECT ID,SUBSCRIBER,FLAG,CREATED_DATE_TIME FROM " + tableName + " WHERE (SUBSCRIBER = " + subscriber + ") ORDER BY CREATED_DATE_TIME DESC", new PAMRunningReportingRowMapper());
	}

}
