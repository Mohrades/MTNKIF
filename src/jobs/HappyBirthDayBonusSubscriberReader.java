package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.batch.item.database.JdbcCursorItemReader;

@SuppressWarnings("rawtypes")
public class HappyBirthDayBonusSubscriberReader extends JdbcCursorItemReader {

	public HappyBirthDayBonusSubscriberReader(int type) {
		if(type == 0) {
			setSql("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "') AND (ASPU IS NOT NULL))");
		}
		else if(type == 1) {
			setSql("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "') AND (ASPU IS NOT NULL) AND (BONUS IS NOT NULL) AND (PROCESSED = 1))");
		}
		else if(type == 2) {
			setSql("SELECT ID,MSISDN,NAME,LANGUAGE,BIRTH_DATE,ASPU,BONUS,BONUS_EXPIRES_IN,LAST_UPDATE_TIME FROM MTN_KIF_BIRTHDAY_BONUS_EBA WHERE ((BIRTH_DATE = '" + (new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)).format(new Date()) + "') AND (ASPU IS NOT NULL) AND (BONUS IS NULL) AND (PROCESSED = 0) AND (LOCKED = 0))");
		}
	}

}
