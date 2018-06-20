package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.item.database.JdbcCursorItemReader;

@SuppressWarnings("rawtypes")
public class BirthDayBonusSubscribersReader extends JdbcCursorItemReader {

	public BirthDayBonusSubscribersReader(int type) {
		if(type == 0) {
			setSql("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTNKIF_BIRTHDAY_BONUS_MSISDN_E WHERE (BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "')");
		}
		else if(type == 1) {
			setSql("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTNKIF_BIRTHDAY_BONUS_MSISDN_E WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "') AND (BONUS IS NOT NULL))");
		}
		else if(type == 2) {
			setSql("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTNKIF_BIRTHDAY_BONUS_MSISDN_E WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy")).format(new Date()) + "') AND (BONUS IS NULL) AND (ASPU IS NOT NULL) AND (LOCKED = 0))");
		}
	}

}
