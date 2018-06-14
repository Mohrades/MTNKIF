package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.ServiceAccess;

public class ServiceAccessRowMapper implements RowMapper<ServiceAccess>{

	@Override
	public ServiceAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new ServiceAccess(rs.getInt("ID"), rs.getInt("CODE"), rs.getString("USERNAME"), rs.getString("PASSWORD"), ((rs.getInt("FLAG") == 1) ? true : false));
	}

}
