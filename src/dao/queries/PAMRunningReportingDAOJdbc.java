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
		getJdbcTemplate().update("INSERT INTO MTN_KIF_PAM_RUN_REPORT_EBA (SUBSCRIBER,FLAG,CREATED_DATE_TIME,CREATED_DATE_TIME_INDEX,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'," + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ",'" + reporting.getOriginOperatorID().replace("'", "''") + "')");
	}

	public List<PAMRunningReporting> getPAMRunningReporting(int subscriber) {
		return getJdbcTemplate().query("SELECT ID,SUBSCRIBER,FLAG,CREATED_DATE_TIME FROM MTN_KIF_PAM_RUN_REPORT_EBA WHERE (SUBSCRIBER = " + subscriber + ") ORDER BY CREATED_DATE_TIME DESC", new PAMRunningReportingRowMapper());
	}

}
