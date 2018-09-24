package product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.integration.HuaweiCrbtServer;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcCRBTReportingDao;
import dao.queries.JdbcRollBackDao;
import domain.models.CRBTReporting;
import domain.models.RollBack;
import domain.models.Subscriber;
import ema.CAI_For_HLR_EMARequest;
import jobs.listeners.CustomRetryOperationsListener;
import util.AccountDetails;
import util.BalanceAndDate;
import util.DedicatedAccount;
import util.PamInformation;
import util.PamInformationList;
import util.ServiceOfferings;

public class PricePlanCurrentActions {

	public PricePlanCurrentActions() {

	}

	public String getInfo(MessageSource i18n, ProductProperties productProperties, String msisdn) {
		AccountDetails accountDetails = getAccountDetails(new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host()), msisdn);
		int language = (accountDetails == null) ? 1 : accountDetails.getLanguageIDCurrent();

		return i18n.getMessage("info", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
	}

	public int isActivated(ProductProperties productProperties, DAO dao, String msisdn) {
		try {
			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

			if((productProperties.getOffer_id() == 0) || (!(request.getOffers(msisdn, new int[][] {{productProperties.getOffer_id(), productProperties.getOffer_id()}}, false, null, false).isEmpty()))) {
				if(productProperties.getServiceOfferings_IDs() != null) {
					AccountDetails accountDetails = getAccountDetails(request, msisdn);

					if(accountDetails == null) {
						if(request.isSuccessfully()) return 1;
						else return -1;
					}
					else {
						ServiceOfferings serviceOfferings = accountDetails.getServiceOfferings();

						serviceOfferings = accountDetails.getServiceOfferings();
						int size = productProperties.getServiceOfferings_IDs().size();

						for(int index = 0; index < size; index++) {
							int serviceOfferingID = Integer.parseInt(productProperties.getServiceOfferings_IDs().get(index));
							boolean activeFlag = (Integer.parseInt(productProperties.getServiceOfferings_activeFlags().get(index)) == 1) ? true : false;

							if((activeFlag && !serviceOfferings.isActiveFlag(serviceOfferingID)) || (!activeFlag && serviceOfferings.isActiveFlag(serviceOfferingID))) {
								return 1;
							}
						}

						return 0;
					}
				}
				else {
					return 0;
				}
			}
			else {
				if(request.isSuccessfully()) return 1;
				else return -1;
			}

		} catch(NullPointerException ex) {

		} catch(NumberFormatException ex) {

		} catch(Exception ex) {

		} catch(Throwable th) {

		}

		return -1;
	}

	@SuppressWarnings("deprecation")
	public int activation(ProductProperties productProperties, DAO dao, Subscriber subscriber, boolean charged, boolean advantages, String originOperatorID) {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
		String msisdn = subscriber.getValue();

		if(charged && productProperties.getActivation_chargingAmount() == 0) charged = false;
		HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
		if(productProperties.getChargingDA() == 0) balances.add(new BalanceAndDate(0, -productProperties.getActivation_chargingAmount(), null));
		else balances.add(new DedicatedAccount(productProperties.getChargingDA(), -productProperties.getActivation_chargingAmount(), null));

		// update Anumber Balance
		if((!charged) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "ACTIVATION", "eBA"))) {
			// update Anumber serviceOfferings
			ServiceOfferings serviceOfferings = null;
			if(productProperties.getServiceOfferings_IDs() != null) {
				serviceOfferings = new ServiceOfferings();
				int size = productProperties.getServiceOfferings_IDs().size();

				for(int index = 0; index < size; index++) {
					int serviceOfferingID = Integer.parseInt(productProperties.getServiceOfferings_IDs().get(index));
					boolean activeFlag = (Integer.parseInt(productProperties.getServiceOfferings_activeFlags().get(index)) == 1) ? true : false;
					serviceOfferings.SetActiveFlag(serviceOfferingID, activeFlag);
				}
			}

			if((serviceOfferings == null) || (request.updateSubscriberSegmentation(msisdn, null, serviceOfferings, "eBA"))) {
				// update Anumber Offer
				if((productProperties.getOffer_id() == 0) || (request.updateOffer(msisdn, productProperties.getOffer_id(), null, null, null, "eBA"))) {
					// community
					int[] communityInformationCurrent = null;
					int[] communityInformationNew = null;

					try {
						communityInformationCurrent = (productProperties.getCommunity_id() == 0) ? null : request.getAccountDetails(msisdn).getCommunityInformationCurrent();
						if(productProperties.getCommunity_id() == 0);
						else {
							if(communityInformationCurrent == null) communityInformationCurrent = new int[]{};

							// Java 8, convert array to List, primitive int[] to List<Integer>
							// List<Integer> list21 =  Arrays.asList(integers); // Cannot modify returned list
							List<Integer> communityInformationCurrentasList = new ArrayList<>(Arrays.stream(communityInformationCurrent).boxed().collect(Collectors.toList()));  // good : can modify returned list
							if(communityInformationCurrentasList.contains(new Integer(productProperties.getCommunity_id())));
							else {
								communityInformationCurrentasList.add(new Integer(productProperties.getCommunity_id()));
								communityInformationNew = communityInformationCurrentasList.stream().mapToInt(Integer::intValue).toArray();
							}
						}

					} catch(Throwable th) {
						if(productProperties.getCommunity_id() != 0) {
							// save rollback
							new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -4, 1, msisdn, msisdn, null));

							if(request.isSuccessfully()) ;
							else {
								// re-test connection
								productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
							}

							return -1;
						}
					}

					if((productProperties.getCommunity_id() == 0) || (communityInformationNew == null) || (communityInformationNew.length == 0) || (request.updateCommunityList(msisdn, communityInformationCurrent, communityInformationNew, "eBA"))) {
						// set PAM
						PamInformationList pamInformationList = new PamInformationList();
						pamInformationList.add(new PamInformation(productProperties.getPamServiceID(), productProperties.getPamClassID(), productProperties.getScheduleID()));
						if((productProperties.getPamServiceID() > 99) || (productProperties.getPamServiceID() < 0) || (request.addPeriodicAccountManagementData(msisdn, pamInformationList, true, "eBA"))) {
							// set Product id to subscriber through EMA interface
							HashSet<Integer> allResp = new HashSet<Integer>(); allResp.add(0);
							HashSet<Integer> successResp = new HashSet<Integer>(); successResp.add(0);
							CAI_For_HLR_EMARequest cai = new CAI_For_HLR_EMARequest(productProperties.getEma_hosts(), productProperties.getEma_io_sleep(), productProperties.getEma_io_timeout());

							if((productProperties.getProductID() == 0) || (cai.execute("SET:PCRFSUB:MSISDN," + msisdn + ":ACTIONTYPE,SUBSCRIBEPRODUCT:CHANNELID,99:PAYTYPE,0:PRODUCTID," + productProperties.getProductID() + ";", allResp, successResp))) {
								// Welcome gift : ADVANTAGES
								if(advantages) {
									Date expires = new Date();
									expires.setSeconds(59);expires.setMinutes(59);expires.setHours(23);

									balances = new HashSet<BalanceAndDate>();
									// sms advantages
									if(productProperties.getAdvantages_sms_value() != 0) {
										if(productProperties.getAdvantages_sms_da() == 0) balances.add(new BalanceAndDate(0, productProperties.getAdvantages_sms_value(), null));
										else balances.add(new DedicatedAccount(productProperties.getAdvantages_sms_da(), productProperties.getAdvantages_sms_value(), expires));
									}

									// data advantages
									if(productProperties.getAdvantages_data_value() != 0) {
										if(productProperties.getAdvantages_data_da() == 0) balances.add(new BalanceAndDate(0, productProperties.getAdvantages_data_value(), null));
										else balances.add(new DedicatedAccount(productProperties.getAdvantages_data_da(), productProperties.getAdvantages_data_value(), expires));
									}

									// update Anumber Balance
									if((balances.isEmpty()) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "ACTIVATIONADVANTAGES", "eBA"))) ;
									else {
										// save rollback
										new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 6 : -6, 1, msisdn, msisdn, null));
									}

									// crbt advantages
									try {
										/*if((true || advantages) && (productProperties.getSong_rbt_code() != null)) {*/
										if((!subscriber.isCrbt()) && (productProperties.getSong_rbt_code() != null)) {
											/**
											 * 
											Configures RetryTemplate
											*/
										    RetryTemplate retryTemplate = new RetryTemplate();
										    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
										    retryPolicy.setMaxAttempts(3);
										    retryTemplate.setRetryPolicy(retryPolicy);
										    retryTemplate.setListeners(new RetryListenerSupport[] {new CustomRetryOperationsListener()});

											// set mtnkif+ crbt song
											String national = msisdn.substring((productProperties.getMcc() + "").length());
											HuaweiCrbtServer huaweiCrbtServer = new HuaweiCrbtServer(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout());

											// first step : subscribe
											/**
											 * 
											Calls web service with retry
											*/
											HashMap<String, String> multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {

												@Override
												public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
													// TODO Auto-generated method stub

													return huaweiCrbtServer.subscribe("1", "000000", "1", national, national);
												}
										    });

											if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("301009"))) {
												 // step two : order  tone
												/**
												 * 
												Calls web service with retry
												*/
												multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {

													@Override
													public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
														// TODO Auto-generated method stub

														// return huaweiCrbtServer.orderTone("1", "000000", "1", national, national, national, productProperties.getSong_rbt_code(), "1", "0", "0", null);
														return huaweiCrbtServer.orderTone("1", "000000", "1", national, national, national, productProperties.getSong_rbt_code(), "1", "0", null, null);
													}
											    });

												if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302011"))) {
													// step three : add tone
													/**
													 * 
													Calls web service with retry
													*/
													multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {

														@Override
														public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
															// TODO Auto-generated method stub

															return huaweiCrbtServer.addToneBox("1", "000000", "1", national, "2", "mtnkif", null, new String[] {productProperties.getSong_rbt_code()}, null, null, "2", "1", "000000000", national);
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

															@Override
															public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
																// TODO Auto-generated method stub

																return huaweiCrbtServer.setTone("1", "000000", "1", national, national, national, null, null, null, null, "1", "1", "2", null, null, toneBoxID, "1");
															}
													    });

														// reporting
														if((multiRef != null) && (multiRef.containsKey("returnCode")) && multiRef.get("returnCode").equals("000000")) {
															subscriber.setCrbt(true); // update status
															CRBTReporting CRBTReporting = new CRBTReporting(0, subscriber.getId(), true, new Date(), originOperatorID);
															CRBTReporting.setToneBoxID(toneBoxID);
															new JdbcCRBTReportingDao(dao).saveOneCRBTReporting(CRBTReporting);										
														}
													}
												}
											}
										}

									} catch(Throwable th) {

									}
								}

								// delete others settings
								// delete serviceOfferings
								if(productProperties.getXtra_serviceOfferings_IDs() != null) {
									serviceOfferings = new ServiceOfferings();
									int size = productProperties.getXtra_serviceOfferings_IDs().size();

									for(int index = 0; index < size; index++) {
										int serviceOfferingID = Integer.parseInt(productProperties.getXtra_serviceOfferings_IDs().get(index));
										boolean activeFlag = (Integer.parseInt(productProperties.getXtra_serviceOfferings_activeFlags().get(index)) == 1) ? true : false;
										serviceOfferings.SetActiveFlag(serviceOfferingID, activeFlag);
									}

									if(request.updateSubscriberSegmentation(msisdn, null, serviceOfferings, "eBA")) ;
									else {
										// save rollback
										new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 7 : -7, 1, msisdn, msisdn, null));
									}
								}

								// delete offers
								if(productProperties.getXtra_removal_offer_IDs() != null) {
									int[] offerIDs = new int[productProperties.getXtra_removal_offer_IDs().size()];

									for(int index = 0; index < offerIDs.length; index++) {
										offerIDs[index] = Integer.parseInt(productProperties.getXtra_removal_offer_IDs().get(index));
									}

									for(int offerID : offerIDs) {
										if(request.deleteOffer(msisdn, offerID, "eBA", true));
										else {
											// save rollback
											new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 8 : -8, 1, msisdn, msisdn, null));
											if(request.isSuccessfully()) ;
											else break;
										}
									}
								}

								return 0;
							}
							else {
								// do rollback for AIR ACTIONS
								int rollbackStatus = new PricePlanCurrentRollBackActions().activation(5, productProperties, dao, msisdn, charged);

								if(cai.isSuccessfully()) return rollbackStatus;
								else {
									// save rollback
									new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, cai.isSuccessfully() ? 9 : -9, 1, msisdn, msisdn, null));
									return -1;
								}
							}
						}
						else {
							if(request.isSuccessfully()) {
								return new PricePlanCurrentRollBackActions().activation(4, productProperties, dao, msisdn, charged);
							}
							else {
								// save rollback
								new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -5, 1, msisdn, msisdn, null));
								return -1;
							}
						}
					}
					else {
						if(request.isSuccessfully()) {
							return new PricePlanCurrentRollBackActions().activation(3, productProperties, dao, msisdn, charged);
						}
						else {
							// save rollback
							new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -4, 1, msisdn, msisdn, null));
							return -1;
						}
					}
				}
				else {
					if(request.isSuccessfully()) {
						return new PricePlanCurrentRollBackActions().activation(2, productProperties, dao, msisdn, charged);
					}
					else {
						new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -3, 1, msisdn, msisdn, null));
						return -1;
					}
				}
			}
			else {
				if(request.isSuccessfully()) {
					return new PricePlanCurrentRollBackActions().activation(1, productProperties, dao, msisdn, charged);
				}
				else {
					new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -2, 1, msisdn, msisdn, null));
					return -1;
				}

			}
		}
		else {
			if(request.isSuccessfully()) {
				return 1;
			}
			else {
				new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -1, 1, msisdn, msisdn, null));
				return -1;
			}
		}
	}

	public int deactivation(ProductProperties productProperties, DAO dao, Subscriber subscriber, boolean charged, String originOperatorID) {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
		String msisdn = subscriber.getValue();

		if(charged && productProperties.getActivation_chargingAmount() == 0) charged = false;
		HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
		if(productProperties.getChargingDA() == 0) balances.add(new BalanceAndDate(0, -productProperties.getDeactivation_chargingAmount(), null));
		else balances.add(new DedicatedAccount((int) productProperties.getChargingDA(), -productProperties.getDeactivation_chargingAmount(), null));

		// update Anumber Balance
		if((!charged) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "DEACTIVATION", "eBA"))) {
			// update Anumber serviceOfferings
			ServiceOfferings serviceOfferings = null;
			if(productProperties.getServiceOfferings_IDs() != null) {
				serviceOfferings = new ServiceOfferings();
				int size = productProperties.getServiceOfferings_IDs().size();

				for(int index = 0; index < size; index++) {
					int serviceOfferingID = Integer.parseInt(productProperties.getServiceOfferings_IDs().get(index));
					boolean activeFlag = (Integer.parseInt(productProperties.getServiceOfferings_activeFlags().get(index)) == 1) ? false : true;
					serviceOfferings.SetActiveFlag(serviceOfferingID, activeFlag);
				}
			}

			if((serviceOfferings == null) || (request.updateSubscriberSegmentation(msisdn, null, serviceOfferings, "eBA"))) {
				// update Anumber Offer
				if((productProperties.getOffer_id() == 0) || (request.deleteOffer(msisdn, productProperties.getOffer_id(), "eBA", true))) {
					// community
					int[] communityInformationCurrent = null;
					int[] communityInformationNew = null;

					try {
						communityInformationCurrent = (productProperties.getCommunity_id() == 0) ? null : request.getAccountDetails(msisdn).getCommunityInformationCurrent();
						if((productProperties.getCommunity_id() == 0) || (communityInformationCurrent == null) || (communityInformationCurrent.length == 0)) ;
						else {
							// Java 8, convert array to List, primitive int[] to List<Integer>
							// List<Integer> list21 =  Arrays.asList(integers); // Cannot modify returned list
							List<Integer> communityInformationCurrentasList = new ArrayList<>(Arrays.stream(communityInformationCurrent).boxed().collect(Collectors.toList()));  // good : can modify returned list
							if(communityInformationCurrentasList.contains(new Integer(productProperties.getCommunity_id()))) {
								communityInformationCurrentasList.remove(new Integer(productProperties.getCommunity_id()));
								communityInformationNew = communityInformationCurrentasList.stream().mapToInt(Integer::intValue).toArray();
							}
						}

					} catch(Throwable th) {
						if(productProperties.getCommunity_id() != 0) {
							// save rollback
							new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -4, 2, msisdn, msisdn, null));

							if(request.isSuccessfully()) ;
							else {
								// re-test connection
								productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
							}

							return -1;
						}
					}

					if((productProperties.getCommunity_id() == 0) || (communityInformationNew == null) || (communityInformationCurrent == null) || (communityInformationCurrent.length == 0) ||  (request.updateCommunityList(msisdn, communityInformationCurrent, communityInformationNew, "eBA"))) {
						// remove PAM
						PamInformationList pamInformationList = new PamInformationList();
						pamInformationList.add(new PamInformation(productProperties.getPamServiceID(), productProperties.getPamClassID(), productProperties.getScheduleID()));
						if((productProperties.getPamServiceID() > 99) || (productProperties.getPamServiceID() < 0) || (request.deletePeriodicAccountManagementData(msisdn, pamInformationList, "eBA", true))) {

							// remove Product id from subscriber through EMA interface
							HashSet<Integer> allResp = new HashSet<Integer>(); allResp.add(0);
							HashSet<Integer> successResp = new HashSet<Integer>(); successResp.add(0);
							CAI_For_HLR_EMARequest cai = new CAI_For_HLR_EMARequest(productProperties.getEma_hosts(), productProperties.getEma_io_sleep(), productProperties.getEma_io_timeout());

							if((productProperties.getProductID() == 0) || (cai.execute("SET:PCRFSUB:MSISDN," + msisdn + ":ACTIONTYPE,UNSUBSCRIBEPRODUCT:CHANNELID,99:PAYTYPE,0:PRODUCTID," + productProperties.getProductID() + ";", allResp, successResp))) {
								// remove mtnkif+ crbt song
								try {
									if(subscriber.isCrbt() && (productProperties.getSong_rbt_code() != null)) {
										/**
										 * 
										Configures RetryTemplate
										*/
									    RetryTemplate retryTemplate = new RetryTemplate();
									    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
									    retryPolicy.setMaxAttempts(3);
									    retryTemplate.setRetryPolicy(retryPolicy);
									    retryTemplate.setListeners(new RetryListenerSupport[] {new CustomRetryOperationsListener()});

										String national = msisdn.substring((productProperties.getMcc() + "").length());
										HuaweiCrbtServer huaweiCrbtServer = new HuaweiCrbtServer(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout());

										/**
										 * 
										Calls web service with retry
										*/
										HashMap<String, String> multiRef = retryTemplate.execute(new RetryCallback<HashMap<String, String>, Throwable>() {

											@Override
											public HashMap<String, String> doWithRetry(RetryContext context) throws Throwable {
												// TODO Auto-generated method stub

												return huaweiCrbtServer.delInboxTone("1", "000000", "1", national, national, null, null, null, productProperties.getSong_rbt_code(), null, "1");
											}
									    });

										// reporting
										if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302073"))) {
											subscriber.setCrbt(false); // update status
											CRBTReporting CRBTReporting = new CRBTReporting(0, subscriber.getId(), false, new Date(), originOperatorID);
											new JdbcCRBTReportingDao(dao).saveOneCRBTReporting(CRBTReporting);
										}
									}

								} catch(Throwable th) {

								}

								return 0;
							}
							else {
								// do rollback for AIR ACTIONS
								int rollbackStatus = new PricePlanCurrentRollBackActions().deactivation(5, productProperties, dao, msisdn, charged);

								if(cai.isSuccessfully()) return rollbackStatus;
								else {
									// save rollback
									new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, cai.isSuccessfully() ? 6 : -6, 2, msisdn, msisdn, null));
									return -1;
								}
							}
						}
						else {
							if(request.isSuccessfully()) {
								return new PricePlanCurrentRollBackActions().deactivation(4, productProperties, dao, msisdn, charged);
							}
							else {
								// save rollback
								new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -5, 2, msisdn, msisdn, null));
								return -1;
							}
						}
					}
					else {
						if(request.isSuccessfully()) {
							return new PricePlanCurrentRollBackActions().deactivation(3, productProperties, dao, msisdn, charged);
						}
						else {
							// save rollback
							new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -4, 2, msisdn, msisdn, null));
							return -1;
						}
					}
				}
				else {
					if(request.isSuccessfully()) {
						return new PricePlanCurrentRollBackActions().deactivation(2, productProperties, dao, msisdn, charged);
					}
					else {
						new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -3, 2, msisdn, msisdn, null));
						return -1;
					}
				}
			}
			else {
				if(request.isSuccessfully()) {
					return new PricePlanCurrentRollBackActions().deactivation(1, productProperties, dao, msisdn, charged);
				}
				else {
					new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -2, 2, msisdn, msisdn, null));
					return -1;
				}

			}
		}
		else {
			if(request.isSuccessfully()) {
				return 1;
			}
			else {
				new JdbcRollBackDao(dao).saveOneRollBack(new RollBack(0, -1, 2, msisdn, msisdn, null));
				return -1;
			}
		}
	}

	public AccountDetails getAccountDetails(AIRRequest request, String msisdn) {
		return request.getAccountDetails(msisdn);
	}

}
