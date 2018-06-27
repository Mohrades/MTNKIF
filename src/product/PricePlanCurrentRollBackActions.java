package product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.RollBackDAOJdbc;
import domain.models.RollBack;
import util.BalanceAndDate;
import util.DedicatedAccount;
import util.PamInformation;
import util.PamInformationList;
import util.ServiceOfferings;

public class PricePlanCurrentRollBackActions {

	public PricePlanCurrentRollBackActions() {

	}

	@SuppressWarnings("deprecation")
	public int activation(int step, ProductProperties productProperties, DAO dao, String msisdn, boolean charged) {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

		// remove PAM
		PamInformationList pamInformationList = new PamInformationList();
		pamInformationList.add(new PamInformation(productProperties.getPamServiceID(), productProperties.getPamClassID(), productProperties.getScheduleID()));

		if((step < 5) || (productProperties.getPamServiceID() > 99) || (productProperties.getPamServiceID() < 0) || (request.deletePeriodicAccountManagementData(msisdn, pamInformationList, "eBA", true))) {
			step = (step < 5) ? step : (step-1);

			// community
			int[] communityInformationCurrent = null;
			int[] communityInformationNew = null;

			try {
				communityInformationCurrent = ((step < 4) || (productProperties.getCommunity_id() == 0)) ? null : request.getAccountDetails(msisdn).getCommunityInformationCurrent();
				if((step < 4) || (productProperties.getCommunity_id() == 0) || (communityInformationCurrent == null) || (communityInformationCurrent.length == 0)) ;
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
				if((step >= 4) && (productProperties.getCommunity_id() != 0)) {
					// save rollback
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -4, 1, msisdn, msisdn, null));

					if(request.isSuccessfully()) ;
					else {
						// re-test connection
						productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
					}

					return -1;
				}
			}

			if((step < 4) || ((productProperties.getCommunity_id() == 0) || (communityInformationNew == null) || (communityInformationCurrent == null) || (communityInformationCurrent.length == 0) ||  (request.updateCommunityList(msisdn, communityInformationCurrent, communityInformationNew, "eBA")))) {
				step = (step < 4) ? step : (step-1);

				if((step < 3) || ((productProperties.getOffer_id() == 0) || (request.deleteOffer(msisdn, productProperties.getOffer_id(), "eBA", true)))) {
					step = (step < 3) ? step : (step-1);

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

					if((step < 2) || ((serviceOfferings == null) || (request.updateSubscriberSegmentation(msisdn, null, serviceOfferings, "eBA")))) {
						step = (step < 2) ? step : (step-1);

						if(charged && productProperties.getActivation_chargingAmount() == 0) charged = false;

						HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
						if(productProperties.getChargingDA() == 0) balances.add(new BalanceAndDate(0, productProperties.getActivation_chargingAmount(), null));
						else balances.add(new DedicatedAccount(productProperties.getChargingDA(), productProperties.getActivation_chargingAmount(), null));

						if((step < 1) || ((!charged) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "ACTIVATION", "eBA")))) {
							step = (step < 1) ? step : (step-1);
							
							return 1;
						}
						else {
							if(request.isSuccessfully()) {
								new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 1, 1, msisdn, msisdn, null));
							}
							else {
								new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -1, 1, msisdn, msisdn, null));
							}

							return request.isSuccessfully() ? 1 : -1;					
						}
					}
					else {
						if(request.isSuccessfully()) {
							new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 2, 1, msisdn, msisdn, null));
						}
						else {
							new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -2, 1, msisdn, msisdn, null));
						}

						return request.isSuccessfully() ? 1 : -1;				
					}
				}
				else {
					if(request.isSuccessfully()) {
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 3, 1, msisdn, msisdn, null));
					}
					else {
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -3, 1, msisdn, msisdn, null));
					}

					return request.isSuccessfully() ? 1 : -1;
				}
			}
			else {
				if(request.isSuccessfully()) {
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 4, 1, msisdn, msisdn, null));
				}
				else {
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -4, 1, msisdn, msisdn, null));
				}

				return request.isSuccessfully() ? 1 : -1;
			}
		}
		else {
			if(request.isSuccessfully()) {
				new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 5, 1, msisdn, msisdn, null));
			}
			else {
				new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -5, 1, msisdn, msisdn, null));
			}

			return request.isSuccessfully() ? 1 : -1;
		}

	}

	@SuppressWarnings("deprecation")
	public int deactivation(int step, ProductProperties productProperties, DAO dao, String msisdn, boolean charged) {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());

		// set PAM
		PamInformationList pamInformationList = new PamInformationList();
		pamInformationList.add(new PamInformation(productProperties.getPamServiceID(), productProperties.getPamClassID(), productProperties.getScheduleID()));

		if((step < 5) || ((productProperties.getPamServiceID() > 99) || (productProperties.getPamServiceID() < 0) || (request.addPeriodicAccountManagementData(msisdn, pamInformationList, true, "eBA")))) {
			step = (step < 5) ? step : (step-1);

			// community
			int[] communityInformationCurrent = null;
			int[] communityInformationNew = null;

			try {
				communityInformationCurrent = ((step < 4) || (productProperties.getCommunity_id() == 0)) ? null : request.getAccountDetails(msisdn).getCommunityInformationCurrent();
				if((step < 4) || (productProperties.getCommunity_id() == 0)) ;
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
				if((step >= 4) && (productProperties.getCommunity_id() != 0)) {
					// save rollback
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -4, 2, msisdn, msisdn, null));

					if(request.isSuccessfully()) ;
					else {
						// re-test connection
						productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
					}

					return -1;
				}
			}

			if((step < 4) || ((productProperties.getCommunity_id() == 0) || (communityInformationNew == null) || (communityInformationNew.length == 0) || (request.updateCommunityList(msisdn, communityInformationCurrent, communityInformationNew, "eBA")))) {
				step = (step < 4) ? step : (step-1);

				if((step < 3) || ((productProperties.getOffer_id() == 0) || (request.updateOffer(msisdn, productProperties.getOffer_id(), null, null, null, "eBA")))) {
					step = (step < 3) ? step : (step-1);

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

					if((step < 2) || ((serviceOfferings == null) || (request.updateSubscriberSegmentation(msisdn, null, serviceOfferings, "eBA")))) {
						step = (step < 2) ? step : (step-1);

						if(charged && productProperties.getDeactivation_chargingAmount() == 0) charged = false;

						HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
						if(productProperties.getChargingDA() == 0) balances.add(new BalanceAndDate(0, productProperties.getDeactivation_chargingAmount(), null));
						else balances.add(new DedicatedAccount(productProperties.getChargingDA(), productProperties.getDeactivation_chargingAmount(), null));

						if((step < 1) || ((!charged) || (request.updateBalanceAndDate(msisdn, balances, productProperties.getSms_notifications_header(), "DEACTIVATION", "eBA")))) {
							step = (step < 1) ? step : (step-1);

							return 1;
						}
						else {
							if(request.isSuccessfully()) {
								new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 1, 2, msisdn, msisdn, null));
							}
							else {
								new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -1, 2, msisdn, msisdn, null));
							}

							return request.isSuccessfully() ? 1 : -1;					
						}
					}
					else {
						if(request.isSuccessfully()) {
							new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 2, 2, msisdn, msisdn, null));
						}
						else {
							new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -2, 2, msisdn, msisdn, null));
						}

						return request.isSuccessfully() ? 1 : -1;				
					}
				}
				else {
					if(request.isSuccessfully()) {
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 3, 2, msisdn, msisdn, null));
					}
					else {
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -3, 2, msisdn, msisdn, null));
					}

					return request.isSuccessfully() ? 1 : -1;
				}
			}
			else {
				if(request.isSuccessfully()) {
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 4, 2, msisdn, msisdn, null));
				}
				else {
					new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -4, 2, msisdn, msisdn, null));
				}

				return request.isSuccessfully() ? 1 : -1;
			}
		}
		else {
			if(request.isSuccessfully()) {
				new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 5, 2, msisdn, msisdn, null));
			}
			else {
				new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, -5, 2, msisdn, msisdn, null));
			}

			return request.isSuccessfully() ? 1 : -1;
		}
	}

}