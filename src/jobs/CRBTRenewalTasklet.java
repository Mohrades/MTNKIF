package jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dao.DAO;
import dao.queries.CRBTReportingDAOJdbc;
import dao.queries.SubscriberDAOJdbc;
import dao.queries.USSDServiceDAOJdbc;
import domain.models.CRBTReporting;
import domain.models.Subscriber;
import domain.models.USSDService;
import product.ProductProperties;

@Component("crbtRenewalTasklet")
public class CRBTRenewalTasklet implements Tasklet {

	@Autowired
	private DAO dao;

	@Autowired
	private ProductProperties productProperties;

	@SuppressWarnings("deprecation")
	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		// TODO Auto-generated method stub

		try {
			USSDService service = new USSDServiceDAOJdbc(dao).getOneUSSDService(productProperties.getSc());
			Date now = new Date();

			// service.unavailable
			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {

			}
			else {
				// set today HVC
				Connection connexion = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				HashSet <Subscriber> allMSISDN_Today_Is_CRBTRENEWABLE = new HashSet <Subscriber>((new SubscriberDAOJdbc(dao)).getAllRenewableCRBTSubscribers(now));
				HashSet <Subscriber> allMSISDN_With_ASPU_ReachedFlag = new HashSet <Subscriber>();

				try {
					Class.forName("oracle.jdbc.driver.OracleDriver"); // chargement du pilote JDBC
					// connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/vmdg", "abutu", "kT60#bTh03#18"); // ouverture connexion
					connexion = DriverManager.getConnection("jdbc:oracle:thin:@ga-exa-scan.mtn.bj:1521/itbidg2", "ebauser", "bBt0518#taBut"); // ouverture connexion
					connexion.setAutoCommit(false); // début transaction
					connexion.setReadOnly(true); // en mode lecture seule

					// on lit la table PRICEPLAN.VALUE_BAND_LIST [MSISDN, CUSTOMER_SEGMENT]
					// ps = connexion.prepareStatement(productProperties.getCrbt_renewal_aspu_filter());
					ps = connexion.prepareStatement(productProperties.getCrbt_renewal_aspu_filter().replace("[monthnameYY]", (new SimpleDateFormat("MMMyy")).format(now)));
					rs = ps.executeQuery();
					// Liste des elements
					while (rs.next()) {
						String msisdn = rs.getString("MSISDN").trim();
						allMSISDN_With_ASPU_ReachedFlag.add(new Subscriber(0, (msisdn.length() == productProperties.getMsisdn_length()) ? productProperties.getMcc() + msisdn : msisdn, false, false, null, null, true));
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

				// croiser subscriber with today is crbt renewal date and aspu reached
				HashSet <Subscriber> allMSISDN_Today_Is_CRBTRENEWABLE_COPY = new HashSet <Subscriber>(allMSISDN_Today_Is_CRBTRENEWABLE);
				allMSISDN_Today_Is_CRBTRENEWABLE.retainAll(allMSISDN_With_ASPU_ReachedFlag);

				// croiser subscriber with today is crbt renewal date and aspu not reached
				allMSISDN_Today_Is_CRBTRENEWABLE_COPY.removeAll(allMSISDN_With_ASPU_ReachedFlag);

				now.setDate(now.getDate() + 30);

				// crbt renewal failed
				for(Subscriber subscriber : allMSISDN_Today_Is_CRBTRENEWABLE_COPY) {
					try {
						// remove crbt song


						subscriber.setCrbt(false);
						subscriber.setCrbtNextRenewalDate(now);
						// store subscriber
						new SubscriberDAOJdbc(dao).setCRBTFlag(subscriber);
						// store reporting
						new CRBTReportingDAOJdbc(dao).saveOneCRBTReporting(new CRBTReporting(0, subscriber.getId(), false, new Date(), "eBA"));

					} catch(Throwable th) {

					}
				}

				// crbt renewal succeeded
				for(Subscriber subscriber : allMSISDN_Today_Is_CRBTRENEWABLE) {
					try {
						// set crbt song


						subscriber.setCrbt(true);
						subscriber.setCrbtNextRenewalDate(now);
						// store subscriber
						new SubscriberDAOJdbc(dao).setCRBTFlag(subscriber);
						// store reporting
						new CRBTReportingDAOJdbc(dao).saveOneCRBTReporting(new CRBTReporting(0, subscriber.getId(), true, new Date(), "eBA"));

					} catch(Throwable th) {

				}
			}

			stepContribution.setExitStatus(ExitStatus.COMPLETED);
			return RepeatStatus.FINISHED;
		}

		} catch(Throwable th) {

		}

		return null;
	}

}
