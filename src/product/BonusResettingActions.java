package product;

import java.util.HashSet;

import connexions.AIRRequest;
import util.AccumulatorInformation;
import util.DedicatedAccount;

public class BonusResettingActions {

	public BonusResettingActions() {
		
	}

	public void execute(String msisdn, ProductProperties productProperties) {
		HashSet<DedicatedAccount> dedicatedAccounts = new HashSet<DedicatedAccount>();
		DedicatedAccount dedicatedAccount = null;

		// sms advantages
		if(productProperties.getAdvantages_sms_da() == 0) ;
		else {
			dedicatedAccount = new DedicatedAccount(productProperties.getAdvantages_sms_da(), 0, null);
			dedicatedAccount.setRelative(false);
			dedicatedAccounts.add(dedicatedAccount);
		}

		// data advantages
		if(productProperties.getAdvantages_data_da() == 0) ;
		else {
			dedicatedAccount = new DedicatedAccount(productProperties.getAdvantages_data_da(), 0, null);
			dedicatedAccount.setRelative(false);
			dedicatedAccounts.add(dedicatedAccount);
		}

		// night advantages call
		if(productProperties.getNight_advantages_call_da() == 0) ;
		else {
			dedicatedAccount = new DedicatedAccount(productProperties.getNight_advantages_call_da(), 0, null);
			dedicatedAccount.setRelative(false);
			dedicatedAccounts.add(dedicatedAccount);
		}

		// night advantages data
		if(productProperties.getNight_advantages_data_da() == 0) ;
		else {
			dedicatedAccount = new DedicatedAccount(productProperties.getNight_advantages_data_da(), 0, null);
			dedicatedAccount.setRelative(false);
			dedicatedAccounts.add(dedicatedAccount);
		}



		// reset bonus sms accumulator
		HashSet<AccumulatorInformation> accumulatorIDs = new HashSet<AccumulatorInformation>();
		AccumulatorInformation accumulatorInformation = null;
		// bonus sms onNet
		if(productProperties.getBonus_sms_onNet_accumulator() == 0) ;
		else {
			accumulatorInformation = new AccumulatorInformation(productProperties.getBonus_sms_onNet_accumulator(), 0, null, null);
			accumulatorInformation.setAccumulatorValueRelative(false);
			accumulatorIDs.add(accumulatorInformation);
		}

        // bonus sms offNet
		if(productProperties.getBonus_sms_offNet_accumulator() == 0) ;
		else {
			accumulatorInformation = new AccumulatorInformation(productProperties.getBonus_sms_offNet_accumulator(), 0, null, null);
			accumulatorInformation.setAccumulatorValueRelative(false);
			accumulatorIDs.add(accumulatorInformation);
		}

        // bonus sms remaining
		if(productProperties.getBonus_sms_remaining_accumulator() == 0) ;
		else {
			accumulatorInformation = new AccumulatorInformation(productProperties.getBonus_sms_remaining_accumulator(), 0, null, null);
			accumulatorInformation.setAccumulatorValueRelative(false);
			accumulatorIDs.add(accumulatorInformation);
		}



        // don't waiting for the response : set waitingForResponse false
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
		request.setWaitingForResponse(false);

		// delete DAs
        if(dedicatedAccounts.size() > 0) request.deleteDedicatedAccounts(msisdn, null, dedicatedAccounts, "eBA");
		// delete Accumulators
        // if(accumulatorIDs.size() > 0) request.deleteAccumulators(msisdn, null, accumulatorIDs, "eBA");
		// update Accumulators
        if(accumulatorIDs.size() > 0) request.updateAccumulators(msisdn, accumulatorIDs, "eBA");

        // release waiting for the response : set waitingForResponse true
        request.setWaitingForResponse(true); request.setSuccessfully(true);
	}

}
