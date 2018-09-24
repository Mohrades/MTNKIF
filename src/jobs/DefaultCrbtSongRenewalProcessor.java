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

import com.integration.HuaweiCrbtServer;

import dao.DAO;
import dao.queries.JdbcCRBTReportingDao;
import dao.queries.JdbcSubscriberDao;
import domain.models.CRBTReporting;
import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import exceptions.HuaweiCrbtServerException;
import jobs.listeners.CustomRetryOperationsListener;
import product.ProductProperties;

public class DefaultCrbtSongRenewalProcessor implements ItemProcessor<Subscriber, Subscriber>, InitializingBean {

	private DAO dao;

	private ProductProperties productProperties;
	
	private HashSet <Subscriber> allMSISDN_With_ASPU_ReachedFlag, allMSISDN_With_ASPU_NotReachedFlag;
	
	private RetryTemplate retryTemplate;
	
	private Date crbtNextRenewalDefaultDate;

	public DefaultCrbtSongRenewalProcessor() {

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
	public Subscriber process(Subscriber subscriber) throws AirAvailabilityException, HuaweiCrbtServerException {
		// TODO Auto-generated method stub

		try {
			// set mtnkif+ crbt song
			if(allMSISDN_With_ASPU_ReachedFlag.contains(subscriber)) {
				int id = subscriber.getId();
				subscriber.setId(0);

				if(productProperties.getSong_rbt_code() != null) {
					String national = subscriber.getValue().substring((productProperties.getMcc() + "").length());
					HuaweiCrbtServer huaweiCrbtServer = new HuaweiCrbtServer(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout());

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

							results = huaweiCrbtServer.subscribe("1", "000000", "1", national, national);

							/*if(results == null) throw (new Exception("Subscribe failed on subscriber " + national));*/
							/*if(results == null) throw (new Throwable("Subscribe failed on subscriber " + national));*/
							if(results == null) throw (new HuaweiCrbtServerException("Subscribe failed on subscriber " + national));
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

								results = huaweiCrbtServer.delInboxTone("1", "000000", "1", national, national, null, null, null, productProperties.getSong_rbt_code(), null, "1");

								/*if(results == null) throw (new Throwable("DelInboxTone failed on subscriber " + national));*/
								if(results == null) throw (new HuaweiCrbtServerException("DelInboxTone failed on subscriber " + national));
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

									// results = huaweiCrbtServer.orderTone("1", "000000", "1", national, national, national, productProperties.getSong_rbt_code(), "1", "0", "0", null);
									results = huaweiCrbtServer.orderTone("1", "000000", "1", national, national, national, productProperties.getSong_rbt_code(), "1", "0", null, null);

									/*if(results == null) throw (new Throwable("OrderTone failed on subscriber " + national));*/
									if(results == null) throw (new HuaweiCrbtServerException("OrderTone failed on subscriber " + national));
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

										results = huaweiCrbtServer.addToneBox("1", "000000", "1", national, "2", "mtnkif", null, new String[] {productProperties.getSong_rbt_code()}, null, null, "2", "1", "000000000", national);

										/*if(results == null) throw (new Throwable("AddToneBox failed on subscriber " + national));*/
										if(results == null) throw (new HuaweiCrbtServerException("AddToneBox failed on subscriber " + national));
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

											results = huaweiCrbtServer.setTone("1", "000000", "1", national, national, national, null, null, null, null, "1", "1", "2", null, null, toneBoxID, "1");

											/*if(results == null) throw (new Throwable("SetTone failed on subscriber " + national));*/
											if(results == null) throw (new HuaweiCrbtServerException("SetTone failed on subscriber " + national));
											else return results;
										}
								    });

									// reporting
									if((multiRef != null) && (multiRef.containsKey("returnCode")) && multiRef.get("returnCode").equals("000000")) {
										subscriber.setCrbt(true); // update status

										// calculate CrbtNextRenewalDate
										Date currentCrbtNextRenewalDate = subscriber.getCrbtNextRenewalDate();
										if(currentCrbtNextRenewalDate == null) currentCrbtNextRenewalDate = (Date) crbtNextRenewalDefaultDate.clone();
										else {
											currentCrbtNextRenewalDate.setDate(currentCrbtNextRenewalDate.getDate() + productProperties.getCrbt_renewal_days());
											// validate month M and M-1 : dates'months must be different
											while(currentCrbtNextRenewalDate.getMonth() == (new Date()).getMonth()) {
												currentCrbtNextRenewalDate.setDate(currentCrbtNextRenewalDate.getDate() + 1);
											}
										}

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


			/**
			 * 
			test if subscriber.isCrbt() is false to go faster in processing : bypass delInboxTone() method
			*/
			// remove mtnkif+ crbt song
			else if(allMSISDN_With_ASPU_NotReachedFlag.contains(subscriber)) {
				int id = subscriber.getId();
				subscriber.setId(0);

				if(productProperties.getSong_rbt_code() != null) {
					String national = subscriber.getValue().substring((productProperties.getMcc() + "").length());
					HuaweiCrbtServer huaweiCrbtServer = new HuaweiCrbtServer(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout());

					/**
					 * 
					Calls web service with retry
					*/
					HashMap<String, String> multiRef = (!subscriber.isCrbt()) ? null : retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {
						HashMap<String, String> results = null;

						@Override
						public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
							// TODO Auto-generated method stub

							results = huaweiCrbtServer.delInboxTone("1", "000000", "1", national, national, null, null, null, productProperties.getSong_rbt_code(), null, "1");

							/*if(results == null) throw (new Exception("DelInboxTone failed on subscriber " + national));*/
							/*if(results == null) throw (new Throwable("DelInboxTone failed on subscriber " + national));*/
							if(results == null) throw (new HuaweiCrbtServerException("DelInboxTone failed on subscriber " + national));
							else return results;

							//return null;
						}
				    });

					// reporting
					if((!subscriber.isCrbt()) || ((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302073")))) {
						subscriber.setCrbt(false); // update status

						// calculate CrbtNextRenewalDate
						Date currentCrbtNextRenewalDate = subscriber.getCrbtNextRenewalDate();
						if(currentCrbtNextRenewalDate == null) currentCrbtNextRenewalDate = (Date) crbtNextRenewalDefaultDate.clone();
						else {
							currentCrbtNextRenewalDate.setDate(currentCrbtNextRenewalDate.getDate() + productProperties.getCrbt_renewal_days());
							// validate month M and M-1 : dates'months must be different
							while(currentCrbtNextRenewalDate.getMonth() == (new Date()).getMonth()) {
								currentCrbtNextRenewalDate.setDate(currentCrbtNextRenewalDate.getDate() + 1);
							}
						}

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

		} catch(HuaweiCrbtServerException ex) {
			throw ex;

		} catch(Exception ex) {
			/**
			 *
			On peut s'assurer qu'un objet est bien une instance d'une classe donnée en recourant à l'opérateur instanceOf.
			Par exemple, l'expression p instanceOf Point vaudra true si p est (exactement) de type Point.
			Mais ce test ne reponds pas à nos problématiques ici. Et donc, nous n'utilisons pas cette méthode.

			Les conversions explicites de références:
			Nous avons largement insisté sur la compatibilité qui existe entre référence à un objet d’un type donné et référence à un objet d'un type ascendant.
			Comme on peut s'y attendre, la compatibilité n'a pas lieu dans le sens inverse. Considérons cet exemple, fondé sur nos classes Point et Pointcol habituelles :
			class Point { ..... }
			class Pointcol extends Point { ..... }
			   .....
			Pointcol pc ;
			pc = new Point (...) ;    // erreur de compilation
			Si l'affectation était légale, un simple appel tel que pc.colore(...) conduirait à attribuer une
			couleur à un objet de type Point, ce qui poserait quelques problèmes à l'exécution...
			Mais considérons cette situation :
			Point p ;
			Pointcol pc1 = new Pointcol(...), pc2 ;
			   .....
			p = pc1 ;   // p contient la référence à un objet de type Pointcol
			   .....
			pc2 = p ;   // refusé en compilation
			L'affectation pc2 = p est tout naturellement refusée. Cependant, nous sommes certains que p contient bien ici la référence à un objet de type Pointcol.
			En fait, nous pouvons forcer le compilateur à réaliser la conversion correspondante en utilisant l'opérateur de cast déjà rencontré pour les types primitifs. Ici, nous écrirons simplement :
			pc2 = (Pointcol) p ;   // accepté en compilation
			Toutefois, lors de l'exécution, Java s'assurera que p contient bien une référence à un objet de type Pointcol (ou dérivé) afin de ne pas compromettre la bonne exécution du programme.
			Dans le cas contraire, on obtiendra une exception ClassCastException qui, si elle n'est pas traitée, conduira à un arrêt de l'exécution.

			!! CECI EST JUSTE UNE INFORMATION A SE RAPPELER

			 */

			/**
			 *
			Determines if the class or interface represented by this Class object is either the same as, or is a superclass or superinterface of, the class or interface represented by the specified Class parameter. It returns true if so; otherwise it returns false.
			If this Class object represents a primitive type, this method returns true if the specified Class parameter is exactly this Class object; otherwise it returns false.
			Specifically, this method tests whether the type represented by the specified Class parameter can be converted to the type represented by this Class object via an identity conversion or via a widening reference conversion.

			 */
			/*if(ex instanceof AirAvailabilityException) throw (AirAvailabilityException)ex;*/
			try {
				AirAvailabilityException exceptionClass = (AirAvailabilityException)ex;
				throw exceptionClass;

			} catch(AirAvailabilityException exception) {
				throw exception;

			} catch(NullPointerException exception) {

			} catch(ClassCastException exception) {

			} catch(Exception exception) {

			} catch(Throwable th) {

			}

			/*if(ex instanceof HuaweiCrbtServerException) throw (HuaweiCrbtServerException)ex;*/
			try {
				HuaweiCrbtServerException exceptionClass = (HuaweiCrbtServerException)ex;
				throw exceptionClass;

			} catch(HuaweiCrbtServerException exception) {
				throw exception;

			} catch(NullPointerException exception) {

			} catch(ClassCastException exception) {

			} catch(Exception exception) {

			} catch(Throwable th) {

			}

		} catch(Throwable th) {
			/**
			 *
			On peut s'assurer qu'un objet est bien une instance d'une classe donnée en recourant à l'opérateur instanceOf.
			Par exemple, l'expression p instanceOf Point vaudra true si p est (exactement) de type Point.
			Mais ce test ne reponds pas à nos problématiques ici. Et donc, nous n'utilisons pas cette méthode.

			Les conversions explicites de références:
			Nous avons largement insisté sur la compatibilité qui existe entre référence à un objet d’un type donné et référence à un objet d'un type ascendant.
			Comme on peut s'y attendre, la compatibilité n'a pas lieu dans le sens inverse. Considérons cet exemple, fondé sur nos classes Point et Pointcol habituelles :
			class Point { ..... }
			class Pointcol extends Point { ..... }
			   .....
			Pointcol pc ;
			pc = new Point (...) ;    // erreur de compilation
			Si l'affectation était légale, un simple appel tel que pc.colore(...) conduirait à attribuer une
			couleur à un objet de type Point, ce qui poserait quelques problèmes à l'exécution...
			Mais considérons cette situation :
			Point p ;
			Pointcol pc1 = new Pointcol(...), pc2 ;
			   .....
			p = pc1 ;   // p contient la référence à un objet de type Pointcol
			   .....
			pc2 = p ;   // refusé en compilation
			L'affectation pc2 = p est tout naturellement refusée. Cependant, nous sommes certains que p contient bien ici la référence à un objet de type Pointcol.
			En fait, nous pouvons forcer le compilateur à réaliser la conversion correspondante en utilisant l'opérateur de cast déjà rencontré pour les types primitifs. Ici, nous écrirons simplement :
			pc2 = (Pointcol) p ;   // accepté en compilation
			Toutefois, lors de l'exécution, Java s'assurera que p contient bien une référence à un objet de type Pointcol (ou dérivé) afin de ne pas compromettre la bonne exécution du programme.
			Dans le cas contraire, on obtiendra une exception ClassCastException qui, si elle n'est pas traitée, conduira à un arrêt de l'exécution.

			!! CECI EST JUSTE UNE INFORMATION A SE RAPPELER

			 */

			/**
			 *
			Determines if the class or interface represented by this Class object is either the same as, or is a superclass or superinterface of, the class or interface represented by the specified Class parameter. It returns true if so; otherwise it returns false.
			If this Class object represents a primitive type, this method returns true if the specified Class parameter is exactly this Class object; otherwise it returns false.
			Specifically, this method tests whether the type represented by the specified Class parameter can be converted to the type represented by this Class object via an identity conversion or via a widening reference conversion.

			 */
			/*if(th instanceof AirAvailabilityException) throw (AirAvailabilityException)th;*/
			try {
				AirAvailabilityException exceptionClass = (AirAvailabilityException)th;
				throw exceptionClass;

			} catch(AirAvailabilityException exception) {
				throw exception;

			} catch(NullPointerException exception) {

			} catch(ClassCastException exception) {

			} catch(Exception exception) {

			} catch(Throwable throwable) {

			}

			/*if(th instanceof HuaweiCrbtServerException) throw (HuaweiCrbtServerException)th;*/
			try {
				HuaweiCrbtServerException exceptionClass = (HuaweiCrbtServerException)th;
				throw exceptionClass;

			} catch(HuaweiCrbtServerException exception) {
				throw exception;

			} catch(NullPointerException exception) {

			} catch(ClassCastException exception) {

			} catch(Exception exception) {

			} catch(Throwable throwable) {

			}
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
		// validate month M and M-1 : dates'months must be different
		while(crbtNextRenewalDefaultDate.getMonth() == (new Date()).getMonth()) {
			crbtNextRenewalDefaultDate.setDate(crbtNextRenewalDefaultDate.getDate() + 1);
		}
	}

}
