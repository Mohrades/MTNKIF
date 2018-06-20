package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.BirthDayBonusSubscriber;

public class BirthDayBonusSubscriberRowMapper implements RowMapper<BirthDayBonusSubscriber> {

	@Override
	public BirthDayBonusSubscriber mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		BirthDayBonusSubscriber birthdayBonusSubscriber = new BirthDayBonusSubscriber(rs.getInt("ID"), rs.getString("MSISDN"), rs.getString("NAME"), rs.getInt("LANGUAGE"), rs.getDate("BIRTH_DATE"));
		birthdayBonusSubscriber.setBonus(rs.getInt("BONUS"));
		birthdayBonusSubscriber.setAspu(rs.getLong("ASPU"));
		birthdayBonusSubscriber.setLast_update_time(rs.getTimestamp("LAST_UPDATE_TIME"));
		birthdayBonusSubscriber.setBonus_expires_in(rs.getTimestamp("BONUS_EXPIRES_IN"));

		return birthdayBonusSubscriber;
	}

}