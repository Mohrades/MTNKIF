package product;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import org.springframework.context.MessageSource;

import connexions.AIRRequest;
import crbt.AddToneBox;
import crbt.DelInboxTone;
import crbt.OrderTone;
import crbt.SetTone;
import crbt.Subscribe;
import dao.DAO;
import dao.queries.CRBTReportingDAOJdbc;
import dao.queries.RollBackDAOJdbc;
import domain.models.CRBTReporting;
import domain.models.RollBack;
import domain.models.Subscriber;
import ema.CAI_For_HLR_EMARequest;
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
		AccountDetails accountDetails = getAccountDetails(new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold()), msisdn);
		int language = (accountDetails == null) ? 1 : accountDetails.getLanguageIDCurrent();

		return i18n.getMessage("info", null, null, (language == 2) ? Locale.ENGLISH : Locale.FRENCH);
	}

	public int isActivated(ProductProperties productProperties, DAO dao, String msisdn) {
		try {
			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold());

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
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold());
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
					// set PAM
					PamInformationList pamInformationList = new PamInformationList();
					pamInformationList.add(new PamInformation(productProperties.getPamServiceID(), productProperties.getPamClassID(), productProperties.getScheduleID()));
					if(request.addPeriodicAccountManagementData(msisdn, pamInformationList, true, "eBA")) ;
					else {
						// save rollback
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 8 : -8, 1, msisdn, msisdn, null));
					}

					// set Product id to subscriber through EMA interface
					HashSet<Integer> allResp = new HashSet<Integer>(); allResp.add(0);
					HashSet<Integer> successResp = new HashSet<Integer>(); successResp.add(0);
					if((new CAI_For_HLR_EMARequest(productProperties.getEma_hosts(), productProperties.getEma_io_sleep(), productProperties.getEma_io_timeout())).execute("SET:PCRFSUB:MSISDN," + msisdn + ":ACTIONTYPE,SUBSCRIBEPRODUCT:CHANNELID,99:PAYTYPE,0:PRODUCTID," + productProperties.getProductID() + ";", allResp, successResp)) ;
					else {
						// save rollback
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 9, 1, msisdn, msisdn, null));
					}

					Date expires = new Date();
					expires.setSeconds(59);expires.setMinutes(59);expires.setHours(23);

					// sms advantages
					balances = new HashSet<BalanceAndDate>();
					if(productProperties.getAdvantages_sms_da() == 0) balances.add(new BalanceAndDate(0, productProperties.getAdvantages_sms_value(), expires));
					else balances.add(new DedicatedAccount(productProperties.getAdvantages_sms_da(), productProperties.getAdvantages_sms_value(), expires));

					// update Anumber Balance
					if((!advantages) || ((productProperties.getAdvantages_sms_value() == 0) || request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "ACTIVATION", "eBA"))) {

						// data advantages
						balances = new HashSet<BalanceAndDate>();
						if(productProperties.getAdvantages_data_da() == 0) balances.add(new BalanceAndDate(0, productProperties.getAdvantages_data_value(), expires));
						else balances.add(new DedicatedAccount(productProperties.getAdvantages_data_da(), productProperties.getAdvantages_data_value(), expires));

						// update Anumber Balance
						if((!advantages) || ((productProperties.getAdvantages_data_value() == 0) || request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "ACTIVATION", "eBA"))) {

						}
						else {
							// save rollback
							new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 5 : -5, 1, msisdn, msisdn, null));
						}
					}
					else {
						// save rollback
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 4 : -4, 1, msisdn, msisdn, null));
					}

					// crbt advantages
					/*if((true || advantages) && (productProperties.getSong_rbt_code() != null)) {*/
					if(productProperties.getSong_rbt_code() != null) {
						// set mtnkif+ crbt song
						String national = msisdn.substring((productProperties.getMcc() + "").length());

						// first step : subscribe
						HashMap<String, String> multiRef = new Subscribe(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, true);
						if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("301009"))) {
							 // step two : order  tone
							multiRef = new OrderTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, national, productProperties.getSong_rbt_code(), "1", "0", null, true);
							if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302011"))) {
								// step three : add tone
								multiRef = new AddToneBox(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, "2", "mtnkif", null, new String[] {productProperties.getSong_rbt_code()}, null, null, "2", "1", "000000000", national, true);
								if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") && multiRef.containsKey("toneBoxID"))) {
									// set tone
									String toneBoxID = multiRef.get("toneBoxID");
									multiRef = new SetTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, national, null, null, null, null, "1", "1", "2", null, null, toneBoxID, "1", true);
									// reporting
									if((multiRef != null) && (multiRef.containsKey("returnCode")) && multiRef.get("returnCode").equals("000000")) {
										subscriber.setCrbt(true); // update status
										CRBTReporting CRBTReporting = new CRBTReporting(0, subscriber.getId(), true, new Date(), originOperatorID);
										CRBTReporting.setToneBoxID(toneBoxID);
										new CRBTReportingDAOJdbc(dao).saveOneCRBTReporting(CRBTReporting);										
									}
								}
							}
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
							new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 6 : -6, 1, msisdn, msisdn, null));
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
								new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 7 : -7, 1, msisdn, msisdn, null));
								if(request.isSuccessfully()) ;
								else break;
							}
						}
					}

					return 0;
				}
				else {
					if(request.isSuccessfully()) {
						return new PricePlanCurrentRollBackActions().activation(2, productProperties, dao, msisdn, charged);
					}
					else {
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -3, 1, msisdn, msisdn, null));
						return -1;
					}
				}
			}
			else {
				if(request.isSuccessfully()) {
					return new PricePlanCurrentRollBackActions().activation(1, productProperties, dao, msisdn, charged);
				}
				else {
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -2, 1, msisdn, msisdn, null));
					return -1;
				}

			}
		}
		else {
			if(request.isSuccessfully()) {
				return 1;
			}
			else {
				new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -1, 1, msisdn, msisdn, null));
				return -1;
			}
		}
	}

	public int deactivation(ProductProperties productProperties, DAO dao, Subscriber subscriber, boolean charged, String originOperatorID) {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold());
		String msisdn =subscriber.getValue();

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
					// remove PAM
					PamInformationList pamInformationList = new PamInformationList();
					pamInformationList.add(new PamInformation(productProperties.getPamServiceID(), productProperties.getPamClassID(), productProperties.getScheduleID()));
					if(request.deletePeriodicAccountManagementData(msisdn, pamInformationList, "eBA", true)) {

					}
					else {
						// save rollback
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, request.isSuccessfully() ? 4 : -4, 2, msisdn, msisdn, null));
					}

					// remove Product id from subscriber through EMA interface
					HashSet<Integer> allResp = new HashSet<Integer>(); allResp.add(0);
					HashSet<Integer> successResp = new HashSet<Integer>(); successResp.add(0);
					if((new CAI_For_HLR_EMARequest(productProperties.getEma_hosts(), productProperties.getEma_io_sleep(), productProperties.getEma_io_timeout())).execute("SET:PCRFSUB:MSISDN," + msisdn + ":ACTIONTYPE,UNSUBSCRIBEPRODUCT:CHANNELID,99:PAYTYPE,0:PRODUCTID," + productProperties.getProductID() + ";", allResp, successResp)) ;
					else {
						// save rollback
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 5, 2, msisdn, msisdn, null));
					}

					// remove mtnkif+ crbt song
					if(productProperties.getSong_rbt_code() != null) {
						String national = msisdn.substring((productProperties.getMcc() + "").length());

						HashMap<String, String> multiRef = new DelInboxTone(productProperties.getCrbt_server_host(), productProperties.getCrbt_server_io_sleep(), productProperties.getCrbt_server_io_timeout()).execute("1", "000000", "1", national, national, null, null, null, productProperties.getSong_rbt_code(), null, "1", true);
						// reporting
						if((multiRef != null) && (multiRef.containsKey("returnCode")) && (multiRef.get("returnCode").equals("000000") || multiRef.get("returnCode").equals("302073"))) {
							subscriber.setCrbt(false); // update status
							CRBTReporting CRBTReporting = new CRBTReporting(0, subscriber.getId(), false, new Date(), originOperatorID);
							new CRBTReportingDAOJdbc(dao).saveOneCRBTReporting(CRBTReporting);
						}
					}

					return 0;
				}
				else {
					if(request.isSuccessfully()) {
						return new PricePlanCurrentRollBackActions().deactivation(2, productProperties, dao, msisdn, charged);
					}
					else {
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -3, 2, msisdn, msisdn, null));
						return -1;
					}
				}
			}
			else {
				if(request.isSuccessfully()) {
					return new PricePlanCurrentRollBackActions().deactivation(1, productProperties, dao, msisdn, charged);
				}
				else {
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -2, 2, msisdn, msisdn, null));
					return -1;
				}

			}
		}
		else {
			if(request.isSuccessfully()) {
				return 1;
			}
			else {
				new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -1, 2, msisdn, msisdn, null));
				return -1;
			}
		}
	}

	public AccountDetails getAccountDetails(AIRRequest request, String msisdn) {
		return request.getAccountDetails(msisdn);
	}

}
