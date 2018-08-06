package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.batch.item.database.JdbcCursorItemReader;

@SuppressWarnings("rawtypes")
public class RunningPAMSubscriberReader extends JdbcCursorItemReader {

	@SuppressWarnings("deprecation")
	public RunningPAMSubscriberReader(int type) {
		if(type == 0) {
			Date now = new Date();

			String tableName = "MTN_KIF_PAM_RUN_REPORT_E_" + ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(now)).toUpperCase();
			setSql("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA Aa WHERE ((Aa.FLAG = 1) AND (Aa.LOCKED = 0) AND (NOT EXISTS (SELECT B.CREATED_DATE_TIME FROM " + tableName + " B WHERE ((B.CREATED_DATE_TIME_INDEX = " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (B.SUBSCRIBER = Aa.ID)))))");
			// setSql("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE ((FLAG = 1) AND (LOCKED = 0))");
		}
		else {
			Date now = new Date();
			now.setDate(now.getDate() - 1);
			now.setHours(23);
			now.setMinutes(59);
			now.setSeconds(59);

			setSql("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE ((LAST_UPDATE_TIME_INDEX = " + Integer.parseInt((new SimpleDateFormat("yyyyMMdd")).format(now)) + ") AND (FLAG = 1) AND (LOCKED = 0))");
			// setSql("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE ((FLAG = 1) AND (LOCKED = 0) AND (TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "' >= LAST_UPDATE_TIME))");
		}
	}

}
