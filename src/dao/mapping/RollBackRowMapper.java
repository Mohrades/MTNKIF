package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.RollBack;

public class RollBackRowMapper implements RowMapper<RollBack>{

	@Override
	public RollBack mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return  new RollBack(rs.getInt("ID"), rs.getInt("STEP"), rs.getInt("VALUE"), rs.getString("ANUMBER"), rs.getString("BNUMBER"), rs.getTimestamp("ERROR_TIME"));
	}

}
