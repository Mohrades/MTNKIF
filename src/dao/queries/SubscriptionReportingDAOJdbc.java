package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.SubscriptionReportingRowMapper;
import domain.models.SubscriptionReporting;

public class SubscriptionReportingDAOJdbc {

	private DAO dao;

	public SubscriptionReportingDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOneSubscriptionReporting(SubscriptionReporting reporting) {
		if(reporting.getChargingAmount() == 0) {
			getJdbcTemplate().update("INSERT INTO MTN_KIF_SUBSCRIPTION_REPORT_EB (SUBSCRIBER,FLAG,CREATED_DATE_TIME,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 :0) + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "','" + reporting.getOriginOperatorID().replace("'", "''") + "')");
		}
		else if(reporting.getChargingAmount() > 0) {
			getJdbcTemplate().update("INSERT INTO MTN_KIF_SUBSCRIPTION_REPORT_EB (SUBSCRIBER,FLAG,CHARGING_AMOUNT,CREATED_DATE_TIME,ORIGIN_OPERATOR_ID) VALUES(" + reporting.getSubscriber() + "," + (reporting.isFlag() ? 1 : 0) + "," + reporting.getChargingAmount() + ",TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "','" + reporting.getOriginOperatorID().replace("'", "''") + "')");
		}
	}

	public List<SubscriptionReporting> getSubscriptionReporting(int subscriber) {
		return getJdbcTemplate().query("SELECT ID,SUBSCRIBER,FLAG,CHARGING_AMOUNT,CREATED_DATE_TIME,ORIGIN_OPERATOR_ID FROM MTN_KIF_SUBSCRIPTION_REPORT_EB WHERE SUBSCRIBER = " + subscriber, new SubscriptionReportingRowMapper());
	}

}
