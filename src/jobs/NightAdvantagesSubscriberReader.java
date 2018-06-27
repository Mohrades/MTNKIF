package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.item.database.JdbcCursorItemReader;

@SuppressWarnings("rawtypes")
public class NightAdvantagesSubscriberReader extends JdbcCursorItemReader {

	public NightAdvantagesSubscriberReader() {
		Date now = new Date();
		String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy")).format(now)).toUpperCase();
		setSql("SELECT ID,SUBSCRIBER,FLAG,CREATED_DATE_TIME FROM " + tableName + " WHERE ((CREATED_DATE_TIME_INDEX = " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (FLAG = 1) AND (SMS IS NULL))");
	}

}
