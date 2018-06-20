package jobs.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import dao.DAO;
import dao.queries.BirthdayBonusSubscriberDAOJdbc;
import dao.queries.USSDServiceDAOJdbc;
import domain.models.BirthDayBonusSubscriber;
import domain.models.USSDService;
import product.PricePlanCurrent;
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

        return stepExecution.getExitStatus();
		// return null;
	}

	@Override
	// action avant l'exécution de l'étape
	public void beforeStep(StepExecution stepExecution) {
		try {
			USSDService service = new USSDServiceDAOJdbc(dao).getOneUSSDService(productProperties.getSc());
			Date now = new Date();

			// Stopping a job from a tasklet : Setting the stop flag in a tasklet is straightforward;
			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				stepExecution.setTerminateOnly(); // Sets stop flag if necessary
		        // stepExecution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
				stepExecution.setExitStatus(new ExitStatus("STOPPED WITH DATE OUT OF RANGE", "Job should not be run right now."));
			}
			else if((new BirthdayBonusSubscriberDAOJdbc(dao)).isBirthDayReported()) {
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

				// set today HAPPY BIRTHDAY BONUS MTNKIF+ Subscribers
				Connection connexion = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				HashSet <BirthDayBonusSubscriber> allMSISDN_Today_Is_BIRTHDATE = new HashSet <BirthDayBonusSubscriber>((new BirthdayBonusSubscriberDAOJdbc(dao)).getOneBirthdayBonusSubscribers());
				HashSet <BirthDayBonusSubscriber> allMSISDN_With_ASPU_ReachedFlag = new HashSet <BirthDayBonusSubscriber>();

				try {
					Class.forName("oracle.jdbc.driver.OracleDriver"); // chargement du pilote JDBC
					// connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/vmdg", "abutu", "kT60#bTh03#18"); // ouverture connexion
					connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/itbidg2", "ebauser", "bBt0518#taBut"); // ouverture connexion
					connexion.setAutoCommit(false); // début transaction
					connexion.setReadOnly(true); // en mode lecture seule

					// Kif+ Subscribers with ASPU >= 3000 XOF
					ps = connexion.prepareStatement(productProperties.getDatabase_aspu_filter().replace("[monthnameYY]", (new SimpleDateFormat("MMMyy")).format(now)).replace("<%= VALUE>", productProperties.getHappy_birthday_bonus_aspu_minimum() + ""));
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

				for(BirthDayBonusSubscriber birthdayBonusSubscriber : allMSISDN_Today_Is_BIRTHDATE) {
					try {
						// store birthdayBonusSubscriber : verify again msisdn is mtnkif subscriber
						Object [] requestStatus = (new PricePlanCurrent()).getStatus(productProperties, null, dao, birthdayBonusSubscriber.getValue(), 0);
						if((int)(requestStatus[0]) == 0) {
							(new BirthdayBonusSubscriberDAOJdbc(dao)).saveOneBirthdayBonusSubscriber(birthdayBonusSubscriber);
						}

					} catch(Throwable th) {

					}
				}
			}

		} catch(Throwable th) {

		}
	}

}
