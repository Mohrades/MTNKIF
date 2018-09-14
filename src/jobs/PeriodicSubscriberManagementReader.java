package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.batch.item.database.JdbcCursorItemReader;

@SuppressWarnings("rawtypes")
public class PeriodicSubscriberManagementReader extends JdbcCursorItemReader {

	public PeriodicSubscriberManagementReader() {
		Date now = new Date();
		String tableName = "MTN_KIF_CRBT_REPORT_EBA_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();

		setSql("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA Aa WHERE ((Aa.CRBT_NEXT_RENEWAL_DATE_INDEX <= " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (Aa.FLAG = 1) AND (NOT EXISTS (SELECT B.CREATED_DATE_TIME FROM " + tableName + " B WHERE ((B.CREATED_DATE_TIME_INDEX = " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (B.SUBSCRIBER = Aa.ID)))))");
	}

}
