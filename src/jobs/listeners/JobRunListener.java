package jobs.listeners;

import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import dao.DAO;
import dao.queries.USSDServiceDAOJdbc;
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

		// intercept exit code to return a custom one
		/*The above code is a StepExecutionListener that first checks to make sure the Step was successful, and next if the skip count on the StepExecution is higher than 0.
		If both conditions are met, a new ExitStatus with an exit code of "COMPLETED WITH SKIPS" is returned.*/

        /*String exitCode = stepExecution.getExitStatus().getExitCode();
        if (!exitCode.equals(ExitStatus.FAILED.getExitCode()) && (stepExecution.getSkipCount() > 0)) {
            return new ExitStatus("COMPLETED WITH SKIPS");
        }
        else {
            return null;
        }*/

        return stepExecution.getExitStatus();
		// return null;
	}

	@Override
	// action avant l'exécution de l'étape
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
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

		USSDService service = new USSDServiceDAOJdbc(dao).getOneUSSDService(productProperties.getSc());
		Date now = new Date();

		// Stopping a job from a tasklet : Setting the stop flag in a tasklet is straightforward;
		if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
			stepExecution.setTerminateOnly(); // Sets stop flag if necessary
	        // stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
			stepExecution.setExitStatus(new ExitStatus("STOPPED WITH DATE OUT OF RANGE", "Job should not be run right now."));
		}
	}

}
