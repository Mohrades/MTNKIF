package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.USSDService;

public class USSDServiceRowMapper implements RowMapper<USSDService>{

	@Override
	public USSDService mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new USSDService(rs.getInt("ID"), rs.getInt("CODE"), rs.getTimestamp("START_DATE"), rs.getTimestamp("STOP_DATE"), rs.getInt("REQUESTS_COUNT"), rs.getString("URL"));
	}

}
