package jobs.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import com.tools.SMPPConnector;

import dao.DAO;
import dao.queries.JdbcScheduledTaskDao;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.ScheduledTask;
import domain.models.USSDService;
import product.ProductProperties;

public class JobRunListener implements StepExecutionListener {

	private DAO dao;

	private ProductProperties productProperties;

	public JobRunListener() {
		
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub

		try {
			// intercept exit code to return a custom one
			/*The above code is a StepExecutionListener that first checks to make sure the Step was successful, and next if the skip count on the StepExecution is higher than 0.
			If both conditions are met, a new ExitStatus with an exit code of "COMPLETED WITH SKIPS" is returned.*/

	        String exitCode = stepExecution.getExitStatus().getExitCode();
	        /*if (!exitCode.equals(ExitStatus.FAILED.getExitCode()) && (stepExecution.getSkipCount() > 0)) {
	            return new ExitStatus("COMPLETED WITH SKIPS");
	        }
	        else {
	            return null;
	        }*/

			// StepExecution: id=2, version=1, name=runningPAM, status=FAILED, exitStatus=FAILED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=0, rollbackCount=0, exitDescription=org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is java.sql.SQLException: Connections could not be acquired from the underlying database!
			String StepExecutionDescription = stepExecution.toString();
			String stepName = "Step=[" + StepExecutionDescription.substring(StepExecutionDescription.indexOf("name=") + 5, StepExecutionDescription.indexOf(", status=", StepExecutionDescription.indexOf("name="))).trim() + "]";
			String stepStatus = StepExecutionDescription.substring(StepExecutionDescription.indexOf("status="), StepExecutionDescription.indexOf(", readCount", StepExecutionDescription.indexOf("status="))).trim();

			String log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format((stepExecution.getEndTime() == null) ? new Date() : stepExecution.getEndTime()).toUpperCase() + stepName + " completed with the following status: [" + stepStatus + "]";

			if (exitCode.equals(ExitStatus.STOPPED.getExitCode())) ;
			else new SMPPConnector().submitSm("APP SERV", productProperties.getAir_test_connection_msisdn(), log);

			Logger logger = LogManager.getLogger("logging.log4j.JobExecutionLogger");
			logger.log(Level.INFO, log);

		} catch(Throwable th) {

		}

		/**
		 *
		Choosing the exit status for a step with a step execution listener
		*/

		/**
		 * 
		// Returns custom status for skipped items
	    if(!ExitStatus.FAILED.equals(stepExecution.getExitStatus()) && stepExecution.getSkipCount() > 0) {            
	       return new ExitStatus("COMPLETED WITH SKIPS");   
	     }
	    // Returns default status
	    else {
	       return stepExecution.getExitStatus();        
	     }
		*/
        return stepExecution.getExitStatus();
		// return null;
	}

	@Override
	// action avant l'exécution de l'étape
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub

		try {
			/*Before the step begins, you tag all the records in a way that identifies them as the records to be processed in the current batch run (or JobInstance) using a StepListener
			The tagging can be by either updating a special column or columns on the database field or copying the records into a staging table.
			Then, the ItemReader reads the records that were tagged at the beginning of the step normally.
			As each chunk completes, you use an ItemWriteListener to update the records you just processed as having been processed.

			To apply this concept to the step, you begin by adding two columns to the table: jobId and processed. The jobId stores the run.id of the current run of the statement job.
			The second column is a boolean with the value true if the record has been processed and false if it hasn’t*/

			/*updates all the records you identify with the job id you pass in to be processed by your step*/
			/*update("update " + tableName + SQL + whereClause, new Object [] {jobId});*/

			/*The first way to stop execution is to throw an exception. This works all the time, unless you configured the job to skip some exceptions in a chunk-oriented step!*/
			// STOPPING A JOB FROM A CHUNK-ORIENTED STEP
			/*If you look at the ItemReader, ItemProcessor, and ItemWriter interfaces, you won’t see a StepExecution.
			You access the StepExecution to stop the execution using listeners.
			Not dealing with stopping a job in item readers, processors, and writers is a good thing. These components should focus on their processing to enforce separation of concerns.*/
	        // listeners will still work, but any other step logic (reader, processor, writer) will not happen

			Date now = stepExecution.getStartTime();
			if(now == null) now = new Date();
			USSDService service = new JdbcUSSDServiceDao(dao).getOneUSSDService(productProperties.getSc());

			// Stopping a job from a tasklet : Setting the stop flag in a tasklet is straightforward;
			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				stepExecution.setTerminateOnly(); // Sets stop flag if necessary
		        // stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
				stepExecution.setExitStatus(new ExitStatus("STOPPED WITH DATE OUT OF RANGE", "Job should not be run right now."));
			}
			else {
				// StepExecution: id=2, version=1, name=runningPAM, status=STARTED, exitStatus=EXECUTING, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=0, rollbackCount=0, exitDescription=
				String StepExecutionDescription = stepExecution.toString();

				if(StepExecutionDescription.contains("name=nightAdvantagesNotificationThroughSms")) ;
				else if(StepExecutionDescription.contains("name=periodicSubscriberManagement")) ;
				else {
					String stepName = StepExecutionDescription.substring(StepExecutionDescription.indexOf("name=") + 5, StepExecutionDescription.indexOf(", status=", StepExecutionDescription.indexOf("name="))).trim();
					@SuppressWarnings("deprecation")
					ScheduledTask task = (new JdbcScheduledTaskDao(dao)).getOneScheduledTask(productProperties.getSc(), stepName, now.getHours(), now.getMinutes());

					if(task == null) {
						stepExecution.setTerminateOnly(); // Sets stop flag if necessary
				        stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
					}
					else {
						stepName = "Step=[" + stepName + "]";
						String stepStatus = StepExecutionDescription.substring(StepExecutionDescription.indexOf("status="), StepExecutionDescription.indexOf(", readCount", StepExecutionDescription.indexOf("status="))).trim();

						String log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format(stepExecution.getStartTime()).toUpperCase() + stepName + " launched with the following status: [" + stepStatus + "]";
						new SMPPConnector().submitSm("APP SERV", productProperties.getAir_test_connection_msisdn(), log);

						Logger logger = LogManager.getLogger("logging.log4j.JobExecutionLogger");
						logger.log(Level.INFO, log);
					}
				}
			}

		} catch(Throwable th) {

		}
	}
}
