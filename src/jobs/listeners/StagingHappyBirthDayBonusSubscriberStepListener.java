package jobs.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcBirthDayBonusSubscriberDao;
import dao.queries.JdbcScheduledTaskDao;
import dao.queries.JdbcSubscriberDao;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.BirthDayBonusSubscriber;
import domain.models.ScheduledTask;
import domain.models.Subscriber;
import domain.models.USSDService;
import product.PricePlanCurrentActions;
import product.ProductProperties;
import tools.SMPPConnector;

public class StagingHappyBirthDayBonusSubscriberStepListener implements StepExecutionListener {

	private DAO dao;

	private ProductProperties productProperties;

	public StagingHappyBirthDayBonusSubscriberStepListener() {

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

	@SuppressWarnings("deprecation")
	@Override
	// action avant l'exécution de l'étape
	public void beforeStep(StepExecution stepExecution) {
		try {
			Date now = new Date();
			now = stepExecution.getStartTime();
			USSDService service = new JdbcUSSDServiceDao(dao).getOneUSSDService(productProperties.getSc());

			// Stopping a job from a tasklet : Setting the stop flag in a tasklet is straightforward;
			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				stepExecution.setTerminateOnly(); // Sets stop flag if necessary
		        // stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
				stepExecution.setExitStatus(new ExitStatus("STOPPED WITH DATE OUT OF RANGE", "Job should not be run right now."));
			}
			else if((new JdbcBirthDayBonusSubscriberDao(dao)).isBirthDayReported()) {
				stepExecution.setTerminateOnly(); // Sets stop flag if necessary
		        // stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
		        stepExecution.setExitStatus(new ExitStatus("STOPPED WITH DATE EXCLUDED", "Job should not be run right now."));
			}
			else {
				// TODO Auto-generated method stub
				/*Before the step begins, you tag all the records in a way that identifies them as the records to be processed in the current batch run (or JobInstance) using a StepListener
				The tagging can be by either updating a special column or columns on the database field or copying the records into a staging table.
				Then, the ItemReader reads the records that were tagged at the beginning of the step normally.
				As each chunk completes, you use an ItemWriteListener to update the records you just processed as having been processed.

				To apply this concept to the step, you begin by adding two columns to the table: jobId and processed. The jobId stores the run.id of the current run of the statement job.
				The second column is a boolean with the value true if the record has been processed and false if it hasn’t*/

				/*updates all the records you identify with the job id you pass in to be processed by your step*/
				/*update("update " + tableName + SQL + whereClause, new Object [] {jobId});*/

				// StepExecution: id=2, version=1, name=runningPAM, status=STARTED, exitStatus=EXECUTING, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=0, rollbackCount=0, exitDescription=
				// System.out.println(stepExecution);
				String StepExecutionDescription = stepExecution.toString();
				String stepName = StepExecutionDescription.substring(StepExecutionDescription.indexOf("name=") + 5, StepExecutionDescription.indexOf(", status=", StepExecutionDescription.indexOf("name="))).trim();
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

					// set today HAPPY BIRTHDAY BONUS MTNKIF+ Subscribers
					Connection connexion = null;
					PreparedStatement ps = null;
					ResultSet rs = null;

					HashSet <BirthDayBonusSubscriber> allMSISDN_Today_Is_BIRTHDATE = new HashSet <BirthDayBonusSubscriber>((new JdbcBirthDayBonusSubscriberDao(dao)).getOneBirthdayBonusSubscribers(true));
					HashSet <BirthDayBonusSubscriber> allMSISDN_With_ASPU_ReachedFlag = new HashSet <BirthDayBonusSubscriber>();

					try {
						Class.forName("oracle.jdbc.driver.OracleDriver"); // chargement du pilote JDBC
						// connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/vmdg", "abutu", "kT60#bTh03#18"); // ouverture connexion
						connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/itbidg2", "ebauser", "bBt0518#taBut"); // ouverture connexion
						connexion.setAutoCommit(false); // début transaction
						connexion.setReadOnly(true); // en mode lecture seule

						// Kif+ Subscribers with ASPU >= 3000 XOF
						Date previous_month = new Date(); previous_month.setMonth(previous_month.getMonth() - 1); // consider previous month table
						ps = connexion.prepareStatement(productProperties.getDatabase_aspu_filter().trim().replace("[monthnameYY]", (new SimpleDateFormat("MMMyy")).format(previous_month)).replace("<%= VALUE>", productProperties.getHappy_birthday_bonus_aspu_minimum() + ""));
						rs = ps.executeQuery();
						// Liste des elements
						while (rs.next()) {
							String msisdn = rs.getString("MSISDN").trim();
							BirthDayBonusSubscriber birthdayBonusSubscriber = new BirthDayBonusSubscriber(0, (msisdn.length() == productProperties.getMsisdn_length()) ? productProperties.getMcc() + msisdn : msisdn, null, 0, null);
							birthdayBonusSubscriber.setAspu(Long.parseLong(rs.getString("ASPU").trim()));
							allMSISDN_With_ASPU_ReachedFlag.add(birthdayBonusSubscriber);
						}

						connexion.commit(); // commit transaction

					} catch (ClassNotFoundException|SQLException ex) {
						// on traite l'exception

					} finally {
						// fermer la connexion
						if (connexion != null) {
							try {
								connexion.close();

							} catch (SQLException ex) {
								// traiter l'exception
							}
						}
					}

					// croiser today_is_birthday and aspu reached
					allMSISDN_Today_Is_BIRTHDATE.retainAll(allMSISDN_With_ASPU_ReachedFlag);
					int air_error_count = 0;

					for(BirthDayBonusSubscriber birthdayBonusSubscriber : allMSISDN_Today_Is_BIRTHDATE) {
						try {
							// store birthdayBonusSubscriber : verify again msisdn is still mtnkif subscriber
							// (éviter d'inscrire les numéros en bd) pour gagner du temps : juste vérifier lee statut de chaque subscriber
							int status = checkPricePlanCurrent(productProperties, dao, birthdayBonusSubscriber.getValue());

							if(status == 0) {
								(new JdbcBirthDayBonusSubscriberDao(dao)).saveOneBirthdayBonusSubscriber(birthdayBonusSubscriber);
							}
							else if(status == -1) {
								++air_error_count;
								if(air_error_count >= 5) break;
							}

						} catch(Throwable th) {

						}
					}

					if(air_error_count >= 5) {
						stepExecution.setTerminateOnly(); // Sets stop flag if necessary
				        // stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
				        stepExecution.setExitStatus(new ExitStatus("STOPPED WITH AIR UNAVAILABILITY", "Job should not be run right now."));
					}
				}
			}

		} catch(Throwable th) {

		}
	}

	public int checkPricePlanCurrent(ProductProperties productProperties, DAO dao, String msisdn) {
		// attempts
		int retry = 0;

		while(productProperties.getAir_preferred_host() == -1) {
			if(retry >= 3) return -1;

			productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
			retry++;
		}

		Subscriber subscriber = new JdbcSubscriberDao(dao).getOneSubscriber(msisdn);

		 if((subscriber != null) && ((subscriber.isLocked()) || (!subscriber.isFlag()))) return 1;
		 else {
			 int status = (new PricePlanCurrentActions()).isActivated(productProperties, dao, msisdn);

			// re-check air connection
			if(status == -1) productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));

			return status;
		 }
	}

}
