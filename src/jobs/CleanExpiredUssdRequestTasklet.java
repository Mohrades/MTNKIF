package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dao.DAO;

@Component("cleanExpiredUssdRequestTasklet")
public class CleanExpiredUssdRequestTasklet implements Tasklet {

	@Autowired
	private DAO dao;

	@SuppressWarnings("deprecation")
	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		// TODO Auto-generated method stub

		try {
			Date now = new Date();
			now.setMinutes(now.getMinutes() - 5);

			dao.getJdbcTemplate().update("DELETE FROM MTN_KIF_USSD_EBA WHERE LAST_UPDATE_TIME < TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'");

			stepContribution.setExitStatus(ExitStatus.COMPLETED);
			return RepeatStatus.FINISHED;

		} catch(Throwable th) {
			
		}
		
		return null;
	}

}
