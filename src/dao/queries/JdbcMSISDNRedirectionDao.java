package dao.queries;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.MSISDNRedirectionRowMapper;
import domain.models.MSISDNRedirection;

public class JdbcMSISDNRedirectionDao {

	private DAO dao;

	public JdbcMSISDNRedirectionDao(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public MSISDNRedirection getOneMSISDNRedirection(int sc, String msisdn, int serviceClass)  {
		List<MSISDNRedirection> redirections = getJdbcTemplate().query("SELECT ID,SERVICE_CODE,TYPE,EXPRESSION,REDIRECTION_URL FROM MSISDN_REDIRECTION_EBA WHERE ((SERVICE_CODE = " + sc + ") AND (((TYPE = 'ServiceClass') AND ((EXPRESSION = 'ALL') OR (EXPRESSION = '" + serviceClass + "'))) OR ((TYPE = 'MSISDN') AND ((EXPRESSION = 'ALL') OR (EXPRESSION = '" + msisdn + "')))))", new MSISDNRedirectionRowMapper());
		return redirections.isEmpty() ? null : redirections.get(0);
	}

	public void deleteOneMSISDNRedirection(int sc) {
		getJdbcTemplate().update("DELETE FROM MSISDN_REDIRECTION_EBA WHERE SERVICE_CODE = " + sc);
	}

	public void deleteOneMSISDNRedirection(int sc, String msisdn, int serviceClass) {
		getJdbcTemplate().update("DELETE FROM MSISDN_REDIRECTION_EBA WHERE ((SERVICE_CODE = " + sc + ") AND (((TYPE = 'MSISDN') AND (EXPRESSION = '" + msisdn + "')) OR ((TYPE = 'ServiceClass') AND (EXPRESSION = '" + serviceClass + "'))))");
	}

}