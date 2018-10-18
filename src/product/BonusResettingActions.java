package product;

import java.util.HashSet;

import connexions.AIRRequest;
import util.AccumulatorInformation;
import util.DedicatedAccount;

public class BonusResettingActions {

	public BonusResettingActions() {
		
	}

	public void execute(String msisdn, ProductProperties productProperties) {
		AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
		HashSet<DedicatedAccount> dedicatedAccounts = new HashSet<DedicatedAccount>();
        HashSet<AccumulatorInformation> accumulatorIDs = new HashSet<AccumulatorInformation>();

		// sms advantages
		if(productProperties.getAdvantages_sms_da() == 0) ;
		else dedicatedAccounts.add(new DedicatedAccount(productProperties.getAdvantages_sms_da(), 0, null));

		// data advantages
		if(productProperties.getAdvantages_data_da() == 0) ;
		else dedicatedAccounts.add(new DedicatedAccount(productProperties.getAdvantages_data_da(), 0, null));

		// night advantages call
		if(productProperties.getNight_advantages_call_da() == 0) ;
		else dedicatedAccounts.add(new DedicatedAccount(productProperties.getNight_advantages_call_da(), 0, null));

		// night advantages data
		if(productProperties.getNight_advantages_data_da() == 0) ;
		else dedicatedAccounts.add(new DedicatedAccount(productProperties.getNight_advantages_data_da(), 0, null));


		// bonus sms onNet
		if(productProperties.getBonus_sms_onNet_accumulator() == 0) ;
		else accumulatorIDs.add(new AccumulatorInformation(productProperties.getBonus_sms_onNet_accumulator(), 0, null, null));

        // bonus sms offNet
		if(productProperties.getBonus_sms_offNet_accumulator() == 0) ;
		else accumulatorIDs.add(new AccumulatorInformation(productProperties.getBonus_sms_offNet_accumulator(), 0, null, null));

        // bonus sms remaining
		if(productProperties.getBonus_sms_remaining_accumulator() == 0) ;
		else accumulatorIDs.add(new AccumulatorInformation(productProperties.getBonus_sms_remaining_accumulator(), 0, null, null));

        // don't waiting for the response : set waitingForResponse false
        request.setWaitingForResponse(false);

		// delete DAs
        if(dedicatedAccounts.size() > 0) request.deleteDedicatedAccounts(msisdn, null, dedicatedAccounts, "eBA");
		// delete Accumulators
        if(accumulatorIDs.size() > 0) request.deleteAccumulators(msisdn, null, accumulatorIDs, "eBA");

        // release waiting for the response : set waitingForResponse true
        request.setWaitingForResponse(true); request.setSuccessfully(true);
	}

}
