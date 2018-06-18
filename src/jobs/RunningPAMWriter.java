package jobs;

import java.util.List;
import org.springframework.batch.item.ItemWriter;
import dao.DAO;
import dao.queries.PAMRunningReportingDAOJdbc;
import domain.models.PAMRunningReporting;
import domain.models.Subscriber;

public class RunningPAMWriter implements ItemWriter<Subscriber> {

	private DAO dao;

	public RunningPAMWriter() {
		
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

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