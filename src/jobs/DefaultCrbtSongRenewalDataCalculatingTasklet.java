package jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import com.tools.SMPPConnector;

import dao.DAO;
import dao.queries.JdbcSubscriberDao;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.Subscriber;
import domain.models.USSDService;
import product.ProductProperties;

public class DefaultCrbtSongRenewalDataCalculatingTasklet implements Tasklet {

	private DAO dao;

	private ProductProperties productProperties;

	public DefaultCrbtSongRenewalDataCalculatingTasklet() {
		
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

	@SuppressWarnings("deprecation")
	@Override
	/**
	 * 
	In a Spring Batch job, each step has its own task to fulfill. To know what it must do, a step can use job parameters.
	Sometimes, you can’t know the inputs for a step when the job starts because a previous step must compute these inputs.
	In such circumstances, the calculating step and the receiving step must find a way to share data.

	You can share data in batch applications in many ways, and you can implement them all using Spring Batch.
	
	Using the execution context to share data :
	What exactly is the execution context? The class ExecutionContext represents the execution  context,  which  acts  as  a  map  (of  key-value  pairs)  but  with  an  API  better suited to batch applications.
	An execution context exists only as part of a job execution, and there are different kinds of execution contexts.
	
	JOBS AND STEPS HAVE THEIR OWN EXECUTION CONTEXT
	Spring Batch provides two kinds of execution contexts: the job execution context and the step execution context. They’re both of the same type—ExecutionContext—but they don’t have the same scope.

	How do you gain access to an execution context? You need a reference to the corresponding execution: a JobExecution if you want to access a job execution context, or a StepExecution if want to access a step execution context.
	Nearly all Spring Batch artifacts can easily access either a JobExecution or a StepExecution.
	The unlucky artifacts without this access are the item reader, processor, and writer. If you need access to an execution context from an item reader, processor, or writer, you can implement a listener interface that provides more insight into the execution, such as an ItemStream.

	*/
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub

		try {
			/*Although Spring Batch stores the job parameters in an instance of the JobParameter class, when you 
			obtain the parameters this way getJobParameters() returns a Map<String, Object>. Because of this, the cast is required.*/
			// String param = (String) chunkContext.getStepContext().getJobParameters().get("paramName");

			// Date now = (chunkContext.getStepContext().getStepExecution().getStartTime() == null) ? new Date() : (Date) chunkContext.getStepContext().getStepExecution().getStartTime().clone();
			Date now = (chunkContext.getStepContext().getStepExecution().getJobExecution().getStartTime() == null) ? new Date() : (Date) chunkContext.getStepContext().getStepExecution().getJobExecution().getStartTime().clone();
			USSDService service = new JdbcUSSDServiceDao(dao).getOneUSSDService(productProperties.getSc());

			/*The first way to stop execution is to throw an exception. This works all the time, unless you configured the job to skip some exceptions in a chunk-oriented step!*/
			// Stopping a job from a tasklet : Setting the stop flag in a tasklet is straightforward;
			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				// Sets stop flag
			    chunkContext.getStepContext().getStepExecution().setTerminateOnly();
				stepContribution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
			}
			else {
				// set today CRBT RENEWABLE MTNKIF+ Subscribers
				Connection connexion = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				HashSet <Subscriber> allMSISDN_Today_Is_CRBTRENEWABLE = new HashSet <Subscriber>((new JdbcSubscriberDao(dao)).getAllRenewableCRBTSubscribers());

				if(allMSISDN_Today_Is_CRBTRENEWABLE.isEmpty()) {
					/**
					 * 
					Gets job execution context
					Gets step execution context
					Writes Data in execution context

					*/
					ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
					// Writes Data in execution context
					jobExecutionContext.put("allMSISDN_With_ASPU_ReachedFlag", new HashSet <Subscriber>());

					ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
					// Writes Data in execution context
					stepExecutionContext.put("allMSISDN_With_ASPU_NotReachedFlag", new HashSet <Subscriber>());

					stepContribution.setExitStatus(new ExitStatus("COMPLETED WITH NO DATA"));
					return RepeatStatus.FINISHED;
				}
				else {
					boolean SQLSyntaxErrorException = false;
					HashSet <Subscriber> allMSISDN_With_ASPU_ReachedFlag = new HashSet <Subscriber>();
					String SQLQuery = null;

					try {
						Class.forName("oracle.jdbc.driver.OracleDriver"); // chargement du pilote JDBC
						// connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/vmdg", "abutu", "kT60#bTh03#18"); // ouverture connexion
						connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/itbidg2", "ebauser", "bBt0518#taBut"); // ouverture connexion
						connexion.setAutoCommit(false); // début transaction
						connexion.setReadOnly(true); // en mode lecture seule

						// on lit la table PRICEPLAN.VALUE_BAND_LIST [MSISDN, CUSTOMER_SEGMENT]
						// ps = connexion.prepareStatement(productProperties.getCrbt_renewal_aspu_filter());
						Date previous_month = new Date(); previous_month.setMonth(previous_month.getMonth() - 1); // consider previous month table
						SQLQuery = productProperties.getDatabase_aspu_filter().trim().replace("[monthnameYY]", ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(previous_month).toUpperCase())).replace("<%= VALUE>", productProperties.getCrbt_renewal_aspu_minimum() + "");
						ps = connexion.prepareStatement(productProperties.getDatabase_aspu_filter().trim().replace("[monthnameYY]", ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(previous_month)).toUpperCase()).replace("<%= VALUE>", productProperties.getCrbt_renewal_aspu_minimum() + ""));
						rs = ps.executeQuery();
						// Liste des elements
						while (rs.next()) {
							String msisdn = rs.getString("MSISDN").trim();
							allMSISDN_With_ASPU_ReachedFlag.add(new Subscriber(0, (msisdn.length() == productProperties.getMsisdn_length()) ? productProperties.getMcc() + msisdn : msisdn, false, false, null, null, true));
						}

						connexion.commit(); // commit transaction

					} catch (SQLSyntaxErrorException ex) {
						// on traite l'exception : ORA-00942: table or view does not exist
						SQLSyntaxErrorException = true;
					} catch (ClassNotFoundException|SQLException ex) {
						// on traite l'exception
						SQLSyntaxErrorException = true;
					} catch (Throwable th) {
						// on traite l'exception

					} finally {
						// fermer la connexion
						if (connexion != null) {
							try {
								connexion.close();

							} catch (SQLException ex) {
								// traiter l'exception
							} catch (Throwable th) {
								// traiter l'exception
							}
						}
					}

					if(SQLSyntaxErrorException) {
						String log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format(new Date()).toUpperCase() + "CRBTRenewalTasklet failed with the following status: [SQLSyntaxErrorException]";
						new SMPPConnector().submitSm("APP SERV", productProperties.getAir_test_connection_msisdn(), log);

						Logger logger = LogManager.getLogger("logging.log4j.DataAvailabilityLogger");
						logger.log(Level.ERROR, "HOST = ga-exa-scan.mtn.bj,   PORT = 1521,   DATABASE = itbidg2,   SQLSyntaxErrorException = " + SQLQuery);

						logger = LogManager.getLogger("logging.log4j.JobExecutionLogger");
						logger.log(Level.INFO, "HOST = ga-exa-scan.mtn.bj,   PORT = 1521,   DATABASE = itbidg2,   SQLSyntaxErrorException = " + SQLQuery + ",   JobExecution = CRBTRenewalTasklet failed with the following status: [SQLSyntaxErrorException]");

						/**
						 * 
						Gets job execution context
						Gets step execution context
						Writes Data in execution context

						*/
						ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
						// Writes Data in execution context
						jobExecutionContext.put("allMSISDN_With_ASPU_ReachedFlag", new HashSet <Subscriber>());

						ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
						// Writes Data in execution context
						stepExecutionContext.put("allMSISDN_With_ASPU_NotReachedFlag", new HashSet <Subscriber>());

						stepContribution.setExitStatus(ExitStatus.FAILED);
						return RepeatStatus.FINISHED;
					}
					else {
						// allMSISDN_With_ASPU_ReachedFlag.add(new Subscriber(0, "22961437066", false, false, null, null, true));

						// croiser subscriber with today is crbt renewal date and aspu not reached
						HashSet <Subscriber> allMSISDN_Today_Is_CRBTRENEWABLE_COPY = new HashSet <Subscriber>(allMSISDN_Today_Is_CRBTRENEWABLE);
						allMSISDN_Today_Is_CRBTRENEWABLE_COPY.removeAll(allMSISDN_With_ASPU_ReachedFlag);

						// croiser subscriber with today is crbt renewal date and aspu reached
						allMSISDN_Today_Is_CRBTRENEWABLE.retainAll(allMSISDN_With_ASPU_ReachedFlag);

						/**
						 * 
						Gets job execution context
						Gets step execution context
						Writes Data in execution context

						*/
						ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
						// Writes Data in execution context
						// crbt renewal failed
						stepExecutionContext.put("allMSISDN_With_ASPU_NotReachedFlag", allMSISDN_Today_Is_CRBTRENEWABLE_COPY);

						ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();               
						// Writes Data in execution context
						// crbt renewal succeeded
						jobExecutionContext.put("allMSISDN_With_ASPU_ReachedFlag", allMSISDN_Today_Is_CRBTRENEWABLE);

						stepContribution.setExitStatus(ExitStatus.COMPLETED);
						return RepeatStatus.FINISHED;
					}
				}
			}

		} catch(Throwable th) {

		}

		return null;
	}

}
