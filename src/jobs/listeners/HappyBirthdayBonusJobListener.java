package jobs.listeners;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class HappyBirthdayBonusJobListener implements JobExecutionListener {

	@Override
	public void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub

		// Called when job ends successfully
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

		}
		// Called when job ends in failure
        else if (jobExecution.getStatus() == BatchStatus.FAILED) {

        }
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		// Called when job starts

	}

}
