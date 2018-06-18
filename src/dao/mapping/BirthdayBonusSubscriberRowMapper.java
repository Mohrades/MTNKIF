package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import domain.models.BirthdayBonusSubscriber;

public class BirthdayBonusSubscriberRowMapper implements RowMapper<BirthdayBonusSubscriber> {

	@Override
	public BirthdayBonusSubscriber mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		BirthdayBonusSubscriber birthdayBonusSubscriber = new BirthdayBonusSubscriber(rs.getInt("ID"), rs.getString("MSISDN"), rs.getString("NAME"), rs.getInt("LANGUAGE"), rs.getDate("BIRTH_DATE"));
		birthdayBonusSubscriber.setBonus(rs.getInt("BONUS"));
		birthdayBonusSubscriber.setAspu(rs.getLong("ASPU"));
		birthdayBonusSubscriber.setLast_update_time(rs.getTimestamp("LAST_UPDATE_TIME"));
		birthdayBonusSubscriber.setBonus_expires_in(rs.getTimestamp("BONUS_EXPIRES_IN"));

		return birthdayBonusSubscriber;
	}

}