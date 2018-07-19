package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.ScheduledTask;

public class ScheduledTaskRowMapper implements RowMapper<ScheduledTask> {

	@Override
	public ScheduledTask mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new ScheduledTask(rs.getInt("ID"), rs.getInt("CODE"), rs.getString("STEP_EXECUTION"), rs.getInt("HOUR"), rs.getInt("MINUTE"), ((rs.getInt("FLAG") == 1) ? true : false));
	}

}
