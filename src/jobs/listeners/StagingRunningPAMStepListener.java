package jobs.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import dao.DAO;
import product.ProductProperties;

public class StagingRunningPAMStepListener implements StepExecutionListener {

	private DAO dao;

	private ProductProperties productProperties;

	public StagingRunningPAMStepListener() {

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
	}

}
