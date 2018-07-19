package dao.queries;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.ScheduledTaskRowMapper;
import domain.models.ScheduledTask;

public class JdbcScheduledTaskDao {

	private DAO dao;

	public JdbcScheduledTaskDao(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public ScheduledTask getOneScheduledTask(int sc, String stepExecution, int hour, int minute) {
		List<ScheduledTask> scheduledTasks = getJdbcTemplate().query("SELECT ID,CODE,STEP_EXECUTION,HOUR,MINUTE,FLAG FROM SCHEDULED_TASK_EBA WHERE ((CODE = " + sc + ") AND (UPPER(TRIM(STEP_EXECUTION)) = '" + stepExecution.toUpperCase().replace("'", "''") + "') AND (HOUR = " + hour + ") AND (MINUTE = " + minute + ") AND (FLAG = 1))", new ScheduledTaskRowMapper());
		return scheduledTasks.isEmpty() ? null : scheduledTasks.get(0);
	}

}
