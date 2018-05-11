package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.USSDRequest;

public class USSDRequestRowMapper implements RowMapper<USSDRequest>{

	@Override
	public USSDRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new USSDRequest(rs.getInt("ID"), rs.getLong("SESSIONID"), rs.getString("MSISDN"), rs.getString("INPUT"), rs.getInt("STEP"), rs.getTimestamp("LAST_UPDATE_TIME"));
	}

}
