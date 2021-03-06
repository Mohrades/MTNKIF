package jobs.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import com.tools.SMPPConnector;

import dao.DAO;
import dao.queries.JdbcHappyBirthDayBonusSubscriberDao;
import dao.queries.JdbcScheduledTaskDao;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.HappyBirthDayBonusSubscriber;
import domain.models.ScheduledTask;
import domain.models.USSDService;
import jobs.HappyBirthDayBonusSubscriberValidator;
import product.ProductProperties;

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
	// action avant l'ex�cution de l'�tape
	public void beforeStep(StepExecution stepExecution) {
		try {
			// Date now = (stepExecution.getStartTime() == null) ? new Date() : (Date) stepExecution.getStartTime().clone();
			Date now = (stepExecution.getJobExecution().getStartTime() == null) ? new Date() : (Date) stepExecution.getJobExecution().getStartTime().clone();
			USSDService service = new JdbcUSSDServiceDao(dao).getOneUSSDService(productProperties.getSc());

			// Stopping a job from a tasklet : Setting the stop flag in a tasklet is straightforward;
			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				stepExecution.setTerminateOnly(); // Sets stop flag if necessary
		        // stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
				stepExecution.setExitStatus(new ExitStatus("STOPPED WITH DATE OUT OF RANGE", "Job should not be run right now."));
			}
			else if((new JdbcHappyBirthDayBonusSubscriberDao(dao)).isBirthDayReported()) {
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
				The second column is a boolean with the value true if the record has been processed and false if it hasn�t*/

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

					List<HappyBirthDayBonusSubscriber> allSubscribers = (new JdbcHappyBirthDayBonusSubscriberDao(dao)).getOneBirthdayBonusSubscribers(true);
					HashSet<HappyBirthDayBonusSubscriber> allMSISDN_Today_Is_BIRTHDATE = new HashSet <HappyBirthDayBonusSubscriber>(allSubscribers);

					if(allMSISDN_Today_Is_BIRTHDATE.isEmpty()) {

					}
					else {
						boolean SQLSyntaxErrorException = false;
						HashSet <HappyBirthDayBonusSubscriber> allMSISDN_With_ASPU_ReachedFlag = new HashSet <HappyBirthDayBonusSubscriber>();
						String SQLQuery = null;

						try {
							Class.forName("oracle.jdbc.driver.OracleDriver"); // chargement du pilote JDBC
							// connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/vmdg", "abutu", "kT60#bTh03#18"); // ouverture connexion
							connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/itbidg2", "ebauser", "bBt0518#taBut"); // ouverture connexion
							connexion.setAutoCommit(false); // d�but transaction
							connexion.setReadOnly(true); // en mode lecture seule

							// Kif+ Subscribers with ASPU >= 3000 XOF
							// Date previous_month = (stepExecution.getStartTime() == null) ? new Date() : (Date) stepExecution.getStartTime().clone();
							Date previous_month = (stepExecution.getJobExecution().getStartTime() == null) ? new Date() : (Date) stepExecution.getJobExecution().getStartTime().clone();
							// consider previous month table
							previous_month.setMonth(previous_month.getMonth() - 1); // consider previous month table

							SQLQuery = productProperties.getDatabase_aspu_filter().trim().replace("[monthnameYY]", ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(previous_month)).toUpperCase()).replace("<%= VALUE>", productProperties.getHappy_birthday_bonus_aspu_minimum() + "");
							ps = connexion.prepareStatement(productProperties.getDatabase_aspu_filter().trim().replace("[monthnameYY]", ((new SimpleDateFormat("MMMyy", Locale.ENGLISH)).format(previous_month)).toUpperCase()).replace("<%= VALUE>", productProperties.getHappy_birthday_bonus_aspu_minimum() + ""));
							rs = ps.executeQuery();
							// Liste des elements
							while (rs.next()) {
								String msisdn = rs.getString("MSISDN").trim();
								HappyBirthDayBonusSubscriber birthdayBonusSubscriber = new HappyBirthDayBonusSubscriber(0, (msisdn.length() == productProperties.getMsisdn_length()) ? productProperties.getMcc() + msisdn : msisdn, null, 0, null);
								// birthdayBonusSubscriber.setAspu(Long.parseLong(rs.getString("ASPU").trim()));
								// birthdayBonusSubscriber.setAspu(Double.parseDouble(rs.getString("ASPU").trim()));
								birthdayBonusSubscriber.setAspu(rs.getDouble("ASPU"));
								allMSISDN_With_ASPU_ReachedFlag.add(birthdayBonusSubscriber);
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
							log = (new SimpleDateFormat("MMM dd', 'yyyy HH:mm:ss' '")).format(new Date()).toUpperCase() + "MTNKIF happyBirthDayBonusJob failed with the following status: [SQLSyntaxErrorException]";
							new SMPPConnector().submitSm("APP SERV", productProperties.getAir_test_connection_msisdn(), log);

							logger = LogManager.getLogger("logging.log4j.DataAvailabilityLogger");
							logger.error("HOST = ga-exa-scan.mtn.bj,   PORT = 1521,   DATABASE = itbidg2,   SQLSyntaxErrorException = " + SQLQuery);

							logger = LogManager.getLogger("logging.log4j.JobExecutionLogger");
							logger.log(Level.INFO, "HOST = ga-exa-scan.mtn.bj,   PORT = 1521,   DATABASE = itbidg2,   SQLSyntaxErrorException = " + SQLQuery + ",   JobExecution = MTNKIF happyBirthDayBonusJob failed with the following status: [SQLSyntaxErrorException]");

							stepExecution.setTerminateOnly(); // Sets stop flag if necessary
					        stepExecution.setExitStatus(new ExitStatus("FAILED", "MTNKIF happyBirthDayBonusJob failed with the following status: [SQLSyntaxErrorException]"));
						}
						else {
							// croiser today_is_birthday and duplicated subscribers
							/*if(allSubscribers.size() != allMSISDN_Today_Is_BIRTHDATE.size()) {*/
							if(allSubscribers.size() > allMSISDN_Today_Is_BIRTHDATE.size()) {
								next : for(HappyBirthDayBonusSubscriber happyBirthDayBonusSubscriber : allSubscribers) {
									for(HappyBirthDayBonusSubscriber duplicatedSubscriber : allMSISDN_Today_Is_BIRTHDATE) {
										if(duplicatedSubscriber.getId() == happyBirthDayBonusSubscriber.getId()) continue next;
									}

									// stage duplicated record as already completely processed
									(new JdbcHappyBirthDayBonusSubscriberDao(dao)).saveOneBirthdayBonusSubscriber(happyBirthDayBonusSubscriber, false);
								}
							}

							// croiser today_is_birthday and aspu not reached
							HashSet<HappyBirthDayBonusSubscriber> allMSISDN_Today_Is_BIRTHDATE_COPY = new HashSet<HappyBirthDayBonusSubscriber>(allMSISDN_Today_Is_BIRTHDATE);
							allMSISDN_Today_Is_BIRTHDATE_COPY.removeAll(allMSISDN_With_ASPU_ReachedFlag);
							for(HappyBirthDayBonusSubscriber happyBirthDayBonusSubscriber : allMSISDN_Today_Is_BIRTHDATE_COPY) {
								// stage record as completely processed
								(new JdbcHappyBirthDayBonusSubscriberDao(dao)).saveOneBirthdayBonusSubscriber(happyBirthDayBonusSubscriber, true);
							}

							// croiser today_is_birthday and aspu reached
							allMSISDN_Today_Is_BIRTHDATE.retainAll(allMSISDN_With_ASPU_ReachedFlag);
							int air_error_count = 0;

							for(HappyBirthDayBonusSubscriber happyBirthDayBonusSubscriber : allMSISDN_Today_Is_BIRTHDATE) {
								try {
									// store birthdayBonusSubscriber : verify again msisdn is still mtnkif subscriber
									// (�viter d'inscrire les num�ros en bd) pour gagner du temps : juste v�rifier lee statut de chaque subscriber
									// int status = checkPricePlanCurrent(productProperties, dao, happyBirthDayBonusSubscriber.getValue());
									int status = (new HappyBirthDayBonusSubscriberValidator(productProperties, dao)).checkPricePlanCurrent(happyBirthDayBonusSubscriber.getValue());

									if(status == 0) {
										// stage record as partially processed : batch will complete processing with happy birthday bonus granting
										(new JdbcHappyBirthDayBonusSubscriberDao(dao)).saveOneBirthdayBonusSubscriber(happyBirthDayBonusSubscriber, true);
									}
									else if(status == 1) {
										// stage record as completely processed
										happyBirthDayBonusSubscriber.setAspu(0);
										(new JdbcHappyBirthDayBonusSubscriberDao(dao)).saveOneBirthdayBonusSubscriber(happyBirthDayBonusSubscriber, true);
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
				}
			}

		} catch(Throwable th) {

		}
	}

	/*public int checkPricePlanCurrent(ProductProperties productProperties, DAO dao, String msisdn) {
		// attempts
		int retry = 0;

		while(productProperties.getAir_preferred_host() == -1) {
			if(retry >= 3) return -1;

			productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
			retry++;
		}

		Subscriber subscriber = new JdbcSubscriberDao(dao).getOneSubscriber(msisdn);

		 if((subscriber != null) && ((subscriber.isLocked()) || ((!subscriber.isFlag()) && (subscriber.getLast_update_time() != null)))) return 1;
		 else {
			 int status = (new PricePlanCurrentActions()).isActivated(productProperties, dao, msisdn);

			// re-check air connection
			if(status == -1) productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));

			return status;
		 }
	}*/

}
