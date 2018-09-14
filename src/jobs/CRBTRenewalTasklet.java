package jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.MessageSource;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.tools.SMPPConnector;

import connexions.AIRRequest;
import crbt.AddToneBox;
import crbt.DelInboxTone;
import crbt.OrderTone;
import crbt.SetTone;
import crbt.Subscribe;
import dao.DAO;
import dao.queries.JdbcCRBTReportingDao;
import dao.queries.JdbcSubscriberDao;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.CRBTReporting;
import domain.models.Subscriber;
import domain.models.USSDService;
import jobs.listeners.CustomRetryOperationsListener;
import product.ProductProperties;
import util.AccountDetails;

/*@Component("crbtRenewalTasklet")*/
public class CRBTRenewalTasklet implements Tasklet {

	/*@Autowired*/
	private DAO dao;

	/*@Autowired*/
	private ProductProperties productProperties;

	/*@Autowired*/
	private MessageSource i18n;

	public CRBTRenewalTasklet() {
		
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

	public MessageSource getI18n() {
		return i18n;
	}

	public void setI18n(MessageSource i18n) {
		this.i18n = i18n;
	}

	@SuppressWarnings("deprecation")
	@Override
	/**
	 * 
	Imagine you use a web service in a custom tasklet to retrieve data that a subsequent step will then use.
	A call to a web service can cause transient failures, so being able to retry this call would make the tasklet more robust.
	You can benefit from Spring Batch’s retry feature in a tasklet, with the RetryOperations interface and its RetryTemplate implementation.
	The RetryTemplate allows for programmatic retry in application code.
	
 	The online store uses a tasklet to retrieve the latest discounts from a web service. The discount data is small enough to keep in memory for later use in the next step.
	The DiscountService interface hides the call to the web service. The following listing shows the tasklet that retrieves the discounts (the setter methods are omitted for brevity). The tasklet uses a RetryTemplate to retry in case of failure.

	*/
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		// TODO Auto-generated method stub

		try {
			/*Although Spring Batch stores the job parameters in an instance of the JobParameter class, when you 
			obtain the parameters this way getJobParameters() returns a Map<String, Object>. Because of this, the cast is required.*/
			// String param = (String) chunkContext.getStepContext().getJobParameters().get("paramName");


			USSDService service = new JdbcUSSDServiceDao(dao).getOneUSSDService(productProperties.getSc());
			Date now = new Date();

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
					stepContribution.setExitStatus(ExitStatus.COMPLETED);
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

						stepContribution.setExitStatus(ExitStatus.FAILED);
						return RepeatStatus.FINISHED;
					}
					else {
						/**
						 * 
						Configures RetryTemplate
						*/
					    RetryTemplate retryTemplate = new RetryTemplate();
					    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();                        
					    retryPolicy.setMaxAttempts(3);                    
					    retryTemplate.setRetryPolicy(retryPolicy);
					    retryTemplate.setListeners(new RetryListenerSupport[] {new CustomRetryOperationsListener()});

						// allMSISDN_With_ASPU_ReachedFlag.add(new Subscriber(0, "22961437066", false, false, null, null, true));

						// croiser subscriber with today is crbt renewal date and aspu not reached
						HashSet <Subscriber> allMSISDN_Today_Is_CRBTRENEWABLE_COPY = new HashSet <Subscriber>(allMSISDN_Today_Is_CRBTRENEWABLE);
						allMSISDN_Today_Is_CRBTRENEWABLE_COPY.removeAll(allMSISDN_With_ASPU_ReachedFlag);

						// croiser subscriber with today is crbt renewal date and aspu reached
						allMSISDN_Today_Is_CRBTRENEWABLE.retainAll(allMSISDN_With_ASPU_ReachedFlag);

						now.setDate(now.getDate() + productProperties.getCrbt_renewal_days());
						AIRRequest AIRRequest = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

						// crbt renewal failed
						for(Subscriber subscriber : allMSISDN_Today_Is_CRBTRENEWABLE_COPY) {
							AccountDetails accountDetails = AIRRequest.getAccountDetails(subscriber.getValue());

							try {
								// remove mtnkif+ crbt song
								if(productProperties.getSong_rbt_code() != null) {
									String national = subscriber.getValue().substring((productProperties.getMcc() + "").length());

									/**
									 * 
									Calls web service with retry
									*/
									HashMap<String, String> multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {
										HashMap<String, String> results = null;

										@Override
										public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
											// TODO Auto-generated method stub

											results = new DelInboxTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, null, null, null, productProperties.getSong_rbt_code(), null, "1", true);

											/*if(results == null) throw (new Exception("DelInboxTone failed on subscriber " + national));*/
											if(results == null) throw (new Throwable("DelInboxTone failed on subscriber " + national));
											else return results;

											//return null;
										}
								    });

									// reporting
									if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302073"))) {
										subscriber.setCrbt(false); // update status

										// calculate CrbtNextRenewalDate
										Date currentCrbtNextRenewalDate = subscriber.getCrbtNextRenewalDate();
										if(currentCrbtNextRenewalDate == null) currentCrbtNextRenewalDate = now;
										else currentCrbtNextRenewalDate.setDate(currentCrbtNextRenewalDate.getDate() + productProperties.getCrbt_renewal_days());
										subscriber.setCrbtNextRenewalDate(currentCrbtNextRenewalDate);

										// store subscriber
										new JdbcSubscriberDao(dao).setCRBTFlag(subscriber);

										// store reporting
										CRBTReporting reporting = new CRBTReporting(0, subscriber.getId(), false, new Date(), "eBA");
										reporting.setAuto(true);
										new JdbcCRBTReportingDao(dao).saveOneCRBTReporting(reporting);
									}
								}

							} catch(Throwable th) {

							}

							// send monthly reminder notification sms
							String notification_message = i18n.getMessage("monthly.reminder", null, null, (accountDetails == null) ? Locale.FRENCH : (accountDetails.getLanguageIDCurrent() == 2) ? Locale.ENGLISH : Locale.FRENCH);
							requestSubmitSmToSmppConnector(notification_message, subscriber.getValue(), productProperties.getSms_notifications_header());
						}

						// crbt renewal succeeded
						for(Subscriber subscriber : allMSISDN_Today_Is_CRBTRENEWABLE) {
							AccountDetails accountDetails = AIRRequest.getAccountDetails(subscriber.getValue());

							try {
								// // set mtnkif+ crbt song
								if(productProperties.getSong_rbt_code() != null) {
									String national = subscriber.getValue().substring((productProperties.getMcc() + "").length());

									// first step : subscribe
									/**
									 * 
									Calls web service with retry
									*/
									HashMap<String, String> multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {
										HashMap<String, String> results = null;

										@Override
										public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
											// TODO Auto-generated method stub

											results = new Subscribe(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, true);

											/*if(results == null) throw (new Exception("Subscribe failed on subscriber " + national));*/
											if(results == null) throw (new Throwable("Subscribe failed on subscriber " + national));
											else return results;

											//return null;
										}
								    });

									if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("301009"))) {
										// delete tone first : precaution to avoid crbt system auto renewal and charge subscribers
										/**
										 * 
										Calls web service with retry
										*/
										multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {
											HashMap<String, String> results = null;

											@Override
											public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
												// TODO Auto-generated method stub

												results = new DelInboxTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, null, null, null, productProperties.getSong_rbt_code(), null, "1", true);

												if(results == null) throw (new Throwable("DelInboxTone failed on subscriber " + national));
												else return results;
											}
									    });

										if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302073"))) {
											// step two : order  tone
											/**
											 * 
											Calls web service with retry
											*/
											multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {
												HashMap<String, String> results = null;

												@Override
												public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
													// TODO Auto-generated method stub

													// results = new OrderTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, national, productProperties.getSong_rbt_code(), "1", "0", "0", null, true);
													results = new OrderTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, national, productProperties.getSong_rbt_code(), "1", "0", null, null, true);

													if(results == null) throw (new Throwable("OrderTone failed on subscriber " + national));
													else return results;
												}
										    });

											if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302011"))) {
											/*if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000"))) { // when tone already exists, do nothing
		*/										// step three : add tone
												/**
												 * 
												Calls web service with retry
												*/
												multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {
													HashMap<String, String> results = null;

													@Override
													public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
														// TODO Auto-generated method stub

														results = new AddToneBox(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, "2", "mtnkif", null, new String[] {productProperties.getSong_rbt_code()}, null, null, "2", "1", "000000000", national, true);

														if(results == null) throw (new Throwable("AddToneBox failed on subscriber " + national));
														else return results;
													}
											    });
												
												if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") && multiRef.containsKey("toneBoxID"))) {
													// set tone
													String toneBoxID = multiRef.get("toneBoxID");

													/**
													 * 
													Calls web service with retry
													*/
													multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {
														HashMap<String, String> results = null;

														@Override
														public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
															// TODO Auto-generated method stub

															results = new SetTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, national, null, null, null, null, "1", "1", "2", null, null, toneBoxID, "1", true);

															if(results == null) throw (new Throwable("SetTone failed on subscriber " + national));
															else return results;
														}
												    });

													// reporting
													if((multiRef != null) && (multiRef.containsKey("returnCode")) && multiRef.get("returnCode").equals("000000")) {
														subscriber.setCrbt(true); // update status

														// calculate CrbtNextRenewalDate
														Date currentCrbtNextRenewalDate = subscriber.getCrbtNextRenewalDate();
														if(currentCrbtNextRenewalDate == null) currentCrbtNextRenewalDate = now;
														else currentCrbtNextRenewalDate.setDate(currentCrbtNextRenewalDate.getDate() + productProperties.getCrbt_renewal_days());
														subscriber.setCrbtNextRenewalDate(currentCrbtNextRenewalDate);

														// store subscriber
														new JdbcSubscriberDao(dao).setCRBTFlag(subscriber);

														// store reporting
														CRBTReporting reporting = new CRBTReporting(0, subscriber.getId(), true, new Date(), "eBA");
														reporting.setAuto(true);
														reporting.setToneBoxID(toneBoxID);
														new JdbcCRBTReportingDao(dao).saveOneCRBTReporting(reporting);

														// send notification sms
														String notification_message = i18n.getMessage("crbt.renewal.successful", null, null, (accountDetails == null) ? Locale.FRENCH : (accountDetails.getLanguageIDCurrent() == 2) ? Locale.ENGLISH : Locale.FRENCH);
														requestSubmitSmToSmppConnector(notification_message, subscriber.getValue(), productProperties.getSms_notifications_header());
													}
												}
											}
											else if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("302011"))) {
												// send notification sms
												String notification_message = i18n.getMessage("crbt.renewal.successful", null, null, (accountDetails == null) ? Locale.FRENCH : (accountDetails.getLanguageIDCurrent() == 2) ? Locale.ENGLISH : Locale.FRENCH);
												requestSubmitSmToSmppConnector(notification_message, subscriber.getValue(), productProperties.getSms_notifications_header());
											}
										}
									}
								}

							} catch(Throwable th) {

							}

							// send monthly reminder notification sms
							String notification_message = i18n.getMessage("monthly.reminder", null, null, (accountDetails == null) ? Locale.FRENCH : (accountDetails.getLanguageIDCurrent() == 2) ? Locale.ENGLISH : Locale.FRENCH);
							requestSubmitSmToSmppConnector(notification_message, subscriber.getValue(), productProperties.getSms_notifications_header());
						}

						stepContribution.setExitStatus(ExitStatus.COMPLETED);
						return RepeatStatus.FINISHED;
					}
				}
			}

		} catch(Throwable th) {

		}

		return null;
	}

	public void requestSubmitSmToSmppConnector(String message, String msisdn, String senderName) {
		if(senderName != null) {
			Logger logger = LogManager.getLogger("logging.log4j.SubmitSMLogger");

			if(msisdn != null) {
				new SMPPConnector().submitSm(senderName, msisdn, message);
				logger.log(Level.TRACE, "[" + msisdn + "] " + message);
			}
			else {
				new SMPPConnector().submitSm(senderName, msisdn, message);
				logger.trace("[" + msisdn + "] " + message);
			}
		}
	}

}
