package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.MSISDN;

public class MSISDNRowMapper implements RowMapper<MSISDN>{

	@Override
	public MSISDN mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new MSISDN(rs.getInt("ID"), rs.getString("MSISDN"));

	}

}
