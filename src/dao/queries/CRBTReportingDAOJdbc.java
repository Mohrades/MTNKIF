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
		getJdbcTemplate().update("INSERT INTO MTN_KIF_CRBT_REPORT_EBA (SUBSCRIBER,FLAG,CREATED_DATE_TIME) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 : 0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "')");
	}

	public List<CRBTReporting> getCRBTReporting(int subscriber) {
		return getJdbcTemplate().query("SELECT ID,SUBSCRIBER,FLAG,CREATED_DATE_TIME FROM MTN_KIF_CRBT_REPORT_EBA WHERE SUBSCRIBER = " + subscriber, new CRBTReportingRowMapper());
	}

}
