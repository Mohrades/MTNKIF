package jobs.listeners;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import product.ProductProperties;
import tools.SMPPConnector;

public class JobPhaseEventListener implements JobExecutionListener {
	
	private ProductProperties productProperties;

	public JobPhaseEventListener() {
		
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub

		try {
			// Called when job ends successfully
			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

			}
			// Called when job ends in failure
	        else if (jobExecution.getStatus() == BatchStatus.FAILED) {

	        }

			// JobExecution: id=1, version=1, startTime=Mon Jun 25 21:58:30 CAT 2018, endTime=Mon Jun 25 21:58:39 CAT 2018, lastUpdated=Mon Jun 25 21:58:30 CAT 2018, status=FAILED, exitStatus=exitCode=FAILED;exitDescription=, job=[JobInstance: id=1, version=0, Job=[defaultBonusJob]], jobParameters=[{date.lancement=2018-06-25 21:58:30+0100}]
			// System.out.println(jobExecution);
			String JobExecutionDescription = jobExecution.toString();
			String jobName = JobExecutionDescription.substring(JobExecutionDescription.indexOf("Job=["), JobExecutionDescription.indexOf("]]", JobExecutionDescription.indexOf("Job=[")) + 1);

			// String log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format(jobExecution.getEndTime()).toUpperCase() + "Job: [FlowJob: [name=" + jobExecution.getJobConfigurationName() + "]] completed with the following status: [" + jobExecution.getStatus() + "]";
			String log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format(jobExecution.getEndTime()).toUpperCase() + jobName + " completed with the following status: [" + jobExecution.getStatus() + "]";
			new SMPPConnector().submitSm("APP SERV", productProperties.getAir_test_connection_msisdn(), log);

			Logger logger = LogManager.getLogger("logging.log4j.JobExecutionLogger");
			logger.log(Level.INFO, log);

		} catch(Throwable th) {
			
		}
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		// Called when job starts

		try {
			// JobExecution: id=1, version=1, startTime=Mon Jun 25 21:48:00 CAT 2018, endTime=null, lastUpdated=Mon Jun 25 21:48:00 CAT 2018, status=STARTED, exitStatus=exitCode=UNKNOWN;exitDescription=, job=[JobInstance: id=1, version=0, Job=[defaultBonusJob]], jobParameters=[{date.lancement=2018-06-25 21:48:00+0100}]
			// System.out.println(jobExecution);
			String JobExecutionDescription = jobExecution.toString();
			String jobName = JobExecutionDescription.substring(JobExecutionDescription.indexOf("Job=["), JobExecutionDescription.indexOf("]]", JobExecutionDescription.indexOf("Job=[")) + 1);

			// String log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format(jobExecution.getStartTime()).toUpperCase() + "Job: [FlowJob: [name=" + jobExecution.getJobConfigurationName() + "]] launched with the following parameters: [{date.lancement=" + jobExecution.getJobParameters().getString("date.lancement") + "}]";
			String log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format(jobExecution.getStartTime()).toUpperCase() + jobName + " launched with the following parameters: [{date.lancement=" + jobExecution.getJobParameters().getString("date.lancement") + "}]";
			new SMPPConnector().submitSm("APP SERV", productProperties.getAir_test_connection_msisdn(), log);

			Logger logger = LogManager.getLogger("logging.log4j.JobExecutionLogger");
			logger.info(log);

		} catch(Throwable th) {

		}
	}
}
