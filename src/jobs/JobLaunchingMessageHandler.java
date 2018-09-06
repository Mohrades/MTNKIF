package jobs;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

/**
 * 
You now need to write the code to launch a Spring Batch job from a JobLaunchRequest.
The following listing shows the Java class in charge of adapting a JobLaunchRequest instance to the Spring Batch launching API.

The JobLaunchingMessageHandler class works with JobParameters and JobLauncher objects, familiar Spring Batch classes by now.
It also uses a less commonly used Spring Batch type that deserves some explanation: the JobRegistry interface. A job registry allows looking up a Job object by name.
It’s used here because the JobLaunchRequest class contains only the name of the job, and you want to keep this class independent from the Spring Batch API.
The following snippet declares a jobRegistry in Spring:
<bean id="jobRegistry" class="org.springframework.batch.core.configuration.support.MapJobRegistry" />
<bean class="org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor">
  <property name="jobRegistry" ref="jobRegistry" />
</bean>

Whereas the JobLaunchRequest is a plain old Java object (POJO), the JobLaunchingMessageHandler class relies on Spring Batch components.
This makes sense: the JobLaunchingMessageHandler is a bridge between the POJO-based messaging world and the Spring Batch launch API.
Note that you use a JobLaunchingMessageHandler with  Spring  Integration, but it doesn’t depend on the Spring Integration API.
The Spring Integration framework uses the JobLaunchingMessageHandler on its message bus, but the class remains independent from any messaging infrastructure.
Later, we’ll send messages containing JobLaunchRequest objects and see Spring Integration handle routing, extracting JobLaunchRequests, and calling the JobLaunchingMessageHandler launch method.

You now have the necessary classes to launch Spring Batch jobs in a generic manner. Let’s create a simple job to illustrate our quick-start with Spring Integration.

*/
public class JobLaunchingMessageHandler {

	private JobRegistry jobRegistry;
	
	private JobLauncher jobLauncher;
	
	public JobLaunchingMessageHandler(JobRegistry jobRegistry, JobLauncher jobLauncher) {
		super();
		this.jobRegistry = jobRegistry;
		this.jobLauncher = jobLauncher;
	}
	
	public JobExecution launch(JobLaunchRequest request) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException,NoSuchJobException {
		/**
		 * 
		Looks up job object
		 */
		Job job = jobRegistry.getJob(request.getJobName());

		/**
		 * 
		Converts job parameters
		 */
		JobParametersBuilder builder = new JobParametersBuilder();
		for(Map.Entry<String,String> entry : request.getJobParameters().entrySet()) {
			builder.addString(entry.getKey(), entry.getValue());
		}

		/**
		 * 
		Launches job
		 */
		return jobLauncher.run(job, builder.toJobParameters());
	}
}
