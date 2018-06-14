package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.CRBTReporting;

public class CRBTReportingRowMapper implements RowMapper<CRBTReporting> {

	@Override
	public CRBTReporting mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		CRBTReporting reporting =  new CRBTReporting(rs.getInt("ID"), rs.getInt("SUBSCRIBER"), ((rs.getInt("FLAG") == 1) ? true : false), rs.getTimestamp("CREATED_DATE_TIME"), rs.getString("ORIGIN_OPERATOR_ID"));
		reporting.setAuto((rs.getInt("AUTO") == 1) ? true : false);
		reporting.setToneBoxID(rs.getString("TONEBOXID"));

		return reporting;
	}

}