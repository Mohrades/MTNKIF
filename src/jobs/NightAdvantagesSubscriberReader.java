package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.batch.item.database.JdbcCursorItemReader;

@SuppressWarnings("rawtypes")
public class NightAdvantagesSubscriberReader extends JdbcCursorItemReader {

	public NightAdvantagesSubscriberReader() {
		Date now = new Date();
		String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();
		setSql("SELECT ID,SUBSCRIBER,FLAG,CREATED_DATE_TIME,ORIGIN_OPERATOR_ID FROM " + tableName + " WHERE ((CREATED_DATE_TIME_INDEX = " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (SMS IS NULL))");
	}

}
