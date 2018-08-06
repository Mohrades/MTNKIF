package jobs;

import java.util.List;
import org.springframework.batch.item.ItemWriter;
import dao.DAO;
import dao.queries.JdbcPAMRunningReportingDao;
import domain.models.PAMRunningReporting;
import domain.models.Subscriber;

public class RunningPAMWriter implements ItemWriter<Subscriber> {

	private DAO dao;

	private boolean waitingForResponse;

	public RunningPAMWriter() {

	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public boolean isWaitingForResponse() {
		return waitingForResponse;
	}

	public void setWaitingForResponse(boolean waitingForResponse) {
		this.waitingForResponse = waitingForResponse;
	}

	@Override
	public void write(List<? extends Subscriber> subscribers) {
		// TODO Auto-generated method stub

		try {
			for(Subscriber subscriber : subscribers) {
				if(subscriber != null) {
					try {
						// save reporting
						(new JdbcPAMRunningReportingDao(dao)).saveOnePAMRunningReporting(new PAMRunningReporting(0, subscriber.getId(), subscriber.isFlag(), null, "eBA"), waitingForResponse);

					} catch(NullPointerException ex) {

					} catch(Throwable th) {

					}
				}
			}

		} catch(Throwable th) {

		}
	}

}
