package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.CRBTReportingRowMapper;
import domain.models.CRBTReporting;

public class CRBTReportingDAOJdbc {

	private DAO dao;

	public CRBTReportingDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOneCRBTReporting(CRBTReporting reporting) {
		Date now = new Date();
		getJdbcTemplate().update("INSERT INTO MTN_KIF_CRBT_REPORT_EBA (SUBSCRIBER,FLAG,CREATED_DATE_TIME,CREATED_DATE_TIME_INDEX,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'," + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ",'" + reporting.getOriginOperatorID().replace("'", "''") + "')");
	}

	public List<CRBTReporting> getCRBTReporting(int subscriber) {
		return getJdbcTemplate().query("SELECT ID,SUBSCRIBER,FLAG,CREATED_DATE_TIME,ORIGIN_OPERATOR_ID FROM MTN_KIF_CRBT_REPORT_EBA WHERE (SUBSCRIBER = " + subscriber + ") ORDER BY CREATED_DATE_TIME DESC", new CRBTReportingRowMapper());
	}

}
