package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import crbt.AddToneBox;
import crbt.DelInboxTone;
import crbt.OrderTone;
import crbt.SetTone;
import crbt.Subscribe;
import dao.DAO;
import dao.queries.JdbcCRBTReportingDao;
import dao.queries.JdbcSubscriberDao;
import domain.models.CRBTReporting;
import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import jobs.listeners.CustomRetryOperationsListener;
import product.ProductProperties;

public class CRBTRenewalProcessor implements ItemProcessor<Subscriber, Subscriber>, InitializingBean {

	private DAO dao;

	private ProductProperties productProperties;
	
	private HashSet <Subscriber> allMSISDN_With_ASPU_ReachedFlag, allMSISDN_With_ASPU_NotReachedFlag;
	
	private RetryTemplate retryTemplate;
	
	private Date crbtNextRenewalDefaultDate;

	public CRBTRenewalProcessor() {

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

	public HashSet<Subscriber> getAllMSISDN_With_ASPU_ReachedFlag() {
		return allMSISDN_With_ASPU_ReachedFlag;
	}

	public void setAllMSISDN_With_ASPU_ReachedFlag(HashSet<Subscriber> allMSISDN_With_ASPU_ReachedFlag) {
		this.allMSISDN_With_ASPU_ReachedFlag = allMSISDN_With_ASPU_ReachedFlag;
	}

	public HashSet<Subscriber> getAllMSISDN_With_ASPU_NotReachedFlag() {
		return allMSISDN_With_ASPU_NotReachedFlag;
	}

	public void setAllMSISDN_With_ASPU_NotReachedFlag(HashSet<Subscriber> allMSISDN_With_ASPU_NotReachedFlag) {
		this.allMSISDN_With_ASPU_NotReachedFlag = allMSISDN_With_ASPU_NotReachedFlag;
	}

	@SuppressWarnings("deprecation")
	@Override
	/**
	 * 
	Filtering items : It is actually very easy to tell Spring Batch not to continue processing an item.  To do so, instead of the ItemProcessor returning an item, it returns null.
	
	let’s look at the filtering rules for item processors:
		If  the  process  method  returns  null,  Spring  Batch  filters  out  the  item  and  it won’t go to the item writer.
		Filtering is different from skipping.
		An exception thrown by an item processor results in a skip (if you configured the skip strategy accordingly).
	
	The basic contract for filtering is clear, but we must point out the distinction between filtering and skipping:
		Filtering means that Spring Batch shouldn’t write a given record. For example, the item writer can’t handle a record.
		Skipping  means that a given record is invalid. For example, the format of a phone number is invalid.
	*/
	/* Filtering items : It is actually very easy to tell Spring Batch not to continue processing an item.  To do so, instead of the ItemProcessor returning an item, it returns null.*/
	public Subscriber process(Subscriber subscriber) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		try {
			// set mtnkif+ crbt song
			if(allMSISDN_With_ASPU_ReachedFlag.contains(subscriber)) {
				int id = subscriber.getId();
				subscriber.setId(0);

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
										if(currentCrbtNextRenewalDate == null) currentCrbtNextRenewalDate = crbtNextRenewalDefaultDate;
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
										subscriber.setId(id);
									}
								}
							}
							else if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("302011"))) {
								// send notification sms
								subscriber.setId(id);
							}
						}
					}
				}

				return subscriber;
			}

			// remove mtnkif+ crbt song
			else if(allMSISDN_With_ASPU_NotReachedFlag.contains(subscriber)) {
				int id = subscriber.getId();
				subscriber.setId(0);

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
						if(currentCrbtNextRenewalDate == null) currentCrbtNextRenewalDate = crbtNextRenewalDefaultDate;
						else currentCrbtNextRenewalDate.setDate(currentCrbtNextRenewalDate.getDate() + productProperties.getCrbt_renewal_days());
						subscriber.setCrbtNextRenewalDate(currentCrbtNextRenewalDate);

						// store subscriber
						new JdbcSubscriberDao(dao).setCRBTFlag(subscriber);

						// store reporting
						CRBTReporting reporting = new CRBTReporting(0, subscriber.getId(), false, new Date(), "eBA");
						reporting.setAuto(true);
						new JdbcCRBTReportingDao(dao).saveOneCRBTReporting(reporting);

						// send notification sms
						subscriber.setId(-id);
					}
				}

				return subscriber;
			}

		} catch(AirAvailabilityException ex) {
			throw ex;

		} catch(Exception ex) {

		} catch(Throwable th) {
			
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		/**
		 * 
		Configures RetryTemplate
		*/
	    retryTemplate = new RetryTemplate();
	    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();                        
	    retryPolicy.setMaxAttempts(3);                    
	    retryTemplate.setRetryPolicy(retryPolicy);
	    retryTemplate.setListeners(new RetryListenerSupport[] {new CustomRetryOperationsListener()});

	    // calculate crbt next renewal default date
	    crbtNextRenewalDefaultDate = new Date();
	    crbtNextRenewalDefaultDate.setDate(crbtNextRenewalDefaultDate.getDate() + productProperties.getCrbt_renewal_days());
	}

}
