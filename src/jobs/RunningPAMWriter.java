package jobs;

import java.util.List;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dao.DAO;
import dao.queries.PAMRunningReportingDAOJdbc;
import domain.models.PAMRunningReporting;
import domain.models.Subscriber;

@Component("runningPAMWriter")
public class RunningPAMWriter implements ItemWriter<Subscriber> {

	@Autowired
	private DAO dao;

	@Override
	public void write(List<? extends Subscriber> subscribers) {
		// TODO Auto-generated method stub

		try {
			for(Subscriber subscriber : subscribers) {
				if(subscriber != null) {
					try {
						// save reporting
						(new PAMRunningReportingDAOJdbc(dao)).saveOnePAMRunningReporting(new PAMRunningReporting(0, subscriber.getId(), subscriber.isFlag(), null, "eBA"));

					} catch(NullPointerException ex) {

					} catch(Throwable th) {

					}
				}
			}

		} catch(Throwable th) {

		}
	}

}
