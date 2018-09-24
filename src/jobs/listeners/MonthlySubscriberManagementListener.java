package jobs.listeners;

import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class MonthlySubscriberManagementListener implements StepExecutionListener {

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub

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

	@SuppressWarnings("deprecation")
	@Override
	/**
	 *
	validate month M and M-1 : dates'months must be different

	Date expires = new Date();
	expires.setSeconds(59);expires.setMinutes(59);expires.setHours(23);expires.setDate(31);expires.setMonth(4);expires.setYear(118);
	System.out.println(expires);

	Date previous_month = (Date) expires.clone(); previous_month.setMonth(previous_month.getMonth() - 1);
	System.out.println(previous_month);
	*/
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub

		try {
			// consider previous month table
			// Date now = (stepExecution.getStartTime() == null) ? new Date() : (Date) stepExecution.getStartTime().clone();
			Date now = (stepExecution.getJobExecution().getStartTime() == null) ? new Date() : (Date) stepExecution.getJobExecution().getStartTime().clone();
			Date previous_month = (Date) now.clone();
			// set M-1
			previous_month.setMonth(previous_month.getMonth() - 1);

			if(previous_month.getMonth() == (now.getMonth())) {
				stepExecution.setTerminateOnly(); // Sets stop flag if necessary
		        stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
			}

		} catch(Throwable th) {

		}
	}

}
