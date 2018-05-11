package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.MSISDNRedirection;

public class MSISDNRedirectionRowMapper implements RowMapper<MSISDNRedirection>{

	@Override
	public MSISDNRedirection mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new MSISDNRedirection(rs.getInt("ID"), rs.getInt("SERVICE_CODE"), rs.getString("TYPE"), rs.getString("EXPRESSION"));
	}

}
