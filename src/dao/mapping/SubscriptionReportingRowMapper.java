package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.SubscriptionReporting;

public class SubscriptionReportingRowMapper implements RowMapper<SubscriptionReporting> {

	@Override
	public SubscriptionReporting mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new SubscriptionReporting(rs.getInt("ID"), rs.getInt("SUBSCRIBER"), ((rs.getInt("FLAG") == 1) ? true : false), rs.getLong("CHARGING_AMOUNT"), rs.getTimestamp("CREATED_DATE_TIME"), rs.getString("ORIGIN_OPERATOR_ID"));
	}

}