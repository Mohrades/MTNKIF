package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
/*import org.springframework.scheduling.annotation.Scheduled;*/
import org.springframework.stereotype.Service;

@Service("jobs")
public class ScheduledTasks {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job cleanExpiredUssdRequestJob;

	@Autowired
	private Job crbtRenewalJob;

	public ScheduledTasks() {

	}

	@Scheduled(cron="0 0 0 ? * SUN")
	public void analyze() {
		try {

		} catch(Throwable e) {

		}
	}

	public void clear_ussd() {
		execute(cleanExpiredUssdRequestJob);
	}

	public void renew_crbt() {
		execute(crbtRenewalJob);
	}

	public void execute(Job job) {
		try {
			SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

			Map<String, JobParameter> map = new HashMap<String, JobParameter>();
			map.put("date.lancement", new JobParameter(timestampFormat.format(new Date())));
			JobParameters params = new JobParameters(map);
			JobExecution jobExecution = jobLauncher.run(job, params);

			jobExecution.getStatus();

		} catch(Throwable th) {

		}
	}
}
