package jobs;

import java.util.Collections;
import java.util.Map;

/**
 * 
To launch a Spring Batch job, you need the name of the job and some (optional) job parameters.
Let’s gather this information in a Java class. The following listing shows our custom Java representation of a job launch request.

The JobLaunchRequest class is a wrapper. It doesn’t depend on the Spring Batch API: no one using the JobLaunchRequest class will know that you’re using Spring Batch at the end of the processing chain.

You now need to write the code to launch a Spring Batch job from a JobLaunchRequest.
*/
public class JobLaunchRequest {

	private String jobName;
	
	private Map<String,String> jobParameters;
	
	public JobLaunchRequest(String jobName) {
		// this(jobName, Collections.EMPTY_MAP);
		this(jobName, Collections.emptyMap());
	}
	
	public JobLaunchRequest(String jobName, Map<String,String> jobParams) {
		super();
		this.jobName = jobName;
		this.jobParameters = jobParams;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public Map<String,String> getJobParameters() {
		// return jobParameters == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(jobParameters);
		return jobParameters == null ? Collections.emptyMap() : Collections.unmodifiableMap(jobParameters);
	}
}
