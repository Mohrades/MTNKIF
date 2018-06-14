package dao.queries;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.ServiceAccessRowMapper;
import domain.models.ServiceAccess;

public class ServiceAccessDAOJdbc {

	private DAO dao;

	public ServiceAccessDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public ServiceAccess getOneServiceAccess(int sc, String username) {
		List<ServiceAccess> services = getJdbcTemplate().query("SELECT ID,CODE,USERNAME,PASSWORD,FLAG FROM SERVICE_ACCESS_EBA WHERE ((CODE = " + sc + ") AND (USERNAME = '" + username.replace("'", "''") + "') AND (FLAG = 1))", new ServiceAccessRowMapper());
		return services.isEmpty() ? null : services.get(0);
	}

}
