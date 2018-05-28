package dao.queries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.RollBackRowMapper;
import domain.models.RollBack;

public class RollBackDAOJdbc {

	private DAO dao;

	public RollBackDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOneRollBack(RollBack rollBack) {
		try {
			if(rollBack.getId() == 0) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date now = new Date();

				getJdbcTemplate().update("INSERT INTO MTN_KIF_ROLLBACK_EBA (STEP,VALUE,ANUMBER,BNUMBER,ERROR_TIME,ERROR_TIME_INDEX) VALUES(" + rollBack.getStep() + "," + rollBack.getValue() + ",'" + rollBack.getAnumber() + "','" + rollBack.getBnumber() + "',TIMESTAMP '" + dateFormat.format(now) + "'," + Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(now)) + ")");			
			}

		} catch(EmptyResultDataAccessException emptyEx) {

		} catch(Throwable th) {

		}
	}

	public RollBack getOneRollBack(int id) {
		List<RollBack> rollbacks = getJdbcTemplate().query("SELECT ID,STEP,VALUE,ANUMBER,BNUMBER,ERROR_TIME FROM MTN_KIF_ROLLBACK_EBA WHERE ID = " + id, new RollBackRowMapper());
		return rollbacks.isEmpty() ? null : rollbacks.get(0);
	}

	public List<RollBack> getAllRollBacks()  {
		return getJdbcTemplate().query("SELECT ID,STEP,VALUE,ANUMBER,BNUMBER,ERROR_TIME FROM MTN_KIF_ROLLBACK_EBA", new RollBackRowMapper());
	}

	public void deleteOneRollBack(int id) {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_ROLLBACK_EBA WHERE ID = " + id);
	}

	public void deleteAllRollBacks() {
		getJdbcTemplate().update("DELETE FROM MTN_KIF_ROLLBACK_EBA");
	}

}
