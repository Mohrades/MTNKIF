package product;

import java.util.HashSet;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.RollBackDAOJdbc;
import domain.models.RollBack;
import util.BalanceAndDate;
import util.DedicatedAccount;
import util.ServiceOfferings;

public class PricePlanCurrentRollBackActions {

	public PricePlanCurrentRollBackActions() {

	}

	public int activation(int step, ProductProperties productProperties, DAO dao, String msisdn, boolean charged) {
		AIRRequest request = new AIRRequest();

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

	public int deactivation(int step, ProductProperties productProperties, DAO dao, String msisdn, boolean charged) {
		AIRRequest request = new AIRRequest();

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


}