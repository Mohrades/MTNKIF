package dao.queries;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.MSISDNRedirectionRowMapper;
import domain.models.MSISDNRedirection;

public class MSISDNRedirectionDAOJdbc {

	private DAO dao;

	public MSISDNRedirectionDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public MSISDNRedirection getOneMSISDNRedirection(int sc, String msisdn)  {
		List<MSISDNRedirection> redirections = getJdbcTemplate().query("SELECT ID,SERVICE_CODE,TYPE,EXPRESSION FROM MSISDN_REDIRECTION_EBA WHERE ((SERVICE_CODE = " + sc + ") AND ((TYPE = 'ALL') OR ((TYPE = 'MSISDN') AND (EXPRESSION = '" + msisdn + "'))))", new MSISDNRedirectionRowMapper());
		return redirections.isEmpty() ? null : redirections.get(0);
	}

	public void deleteOneMSISDNRedirection(int sc) {
		getJdbcTemplate().update("DELETE FROM MSISDN_REDIRECTION_EBA WHERE SERVICE_CODE = " + sc);
	}

	public void deleteOneMSISDNRedirection(int sc, String msisdn) {
		getJdbcTemplate().update("DELETE FROM MSISDN_REDIRECTION_EBA WHERE ((SERVICE_CODE = " + sc + ") AND (TYPE = 'MSISDN') AND (EXPRESSION = '" + msisdn + "'))");
	}

}