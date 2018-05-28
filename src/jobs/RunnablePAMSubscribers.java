package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.item.database.JdbcCursorItemReader;

@SuppressWarnings("rawtypes")
public class RunnablePAMSubscribers extends JdbcCursorItemReader {

	@SuppressWarnings("deprecation")
	public RunnablePAMSubscribers(int type) {
		if(type == 0) {
			setSql("SELECT ID,MSISDN,FLAG,CRBT,LAST_UPDATE_TIME,CRBT_NEXT_RENEWAL_DATE,LOCKED FROM MTN_KIF_MSISDN_EBA WHERE ((FLAG = 1) AND (LOCKED = 0))");
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
