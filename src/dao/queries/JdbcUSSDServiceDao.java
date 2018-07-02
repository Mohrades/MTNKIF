package dao.queries;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.USSDServiceRowMapper;
import domain.models.USSDService;

public class JdbcUSSDServiceDao {

	private DAO dao;

	public JdbcUSSDServiceDao(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public USSDService getOneUSSDService(int sc) {
		List<USSDService> services = getJdbcTemplate().query("SELECT ID,CODE,URL,START_DATE,STOP_DATE,REQUESTS_COUNT FROM SERVICE_CODE_EBA WHERE (CODE = " + sc + ")", new USSDServiceRowMapper());
		return services.isEmpty() ? null : services.get(0);
	}

	public void setActiveUSSDRequest(boolean increment, int sc) {
		if(increment) getJdbcTemplate().update("UPDATE SERVICE_CODE_EBA SET REQUESTS_COUNT = REQUESTS_COUNT + 1 WHERE (CODE = " + sc + ")");
		else getJdbcTemplate().update("UPDATE SERVICE_CODE_EBA SET REQUESTS_COUNT = REQUESTS_COUNT - 1 WHERE (CODE = " + sc + ")");
	}

}
