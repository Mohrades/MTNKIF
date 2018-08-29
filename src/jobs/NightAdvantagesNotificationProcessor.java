package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.springframework.batch.item.ItemProcessor;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.JdbcPAMRunningReportingDao;
import dao.queries.JdbcSubscriberDao;
import domain.models.PAMRunningReporting;
import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import product.ProductProperties;
import util.BalanceAndDate;

public class NightAdvantagesNotificationProcessor implements ItemProcessor<PAMRunningReporting, Subscriber> {

	private DAO dao;

	private ProductProperties productProperties;

	private boolean waitingForResponse;

	public NightAdvantagesNotificationProcessor() {

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

	public boolean isWaitingForResponse() {
		return waitingForResponse;
	}

	public void setWaitingForResponse(boolean waitingForResponse) {
		this.waitingForResponse = waitingForResponse;
	}

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
	public Subscriber process(PAMRunningReporting pamRunningReporting) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		try {
			Subscriber subscriber = (new JdbcSubscriberDao(dao)).getOneSubscriber(pamRunningReporting.getSubscriber(), false);

			// check night advantages
			if(getNightAdvantages(productProperties, subscriber.getValue())) {
				// save notification
				if(!waitingForResponse) pamRunningReporting.setFlag(true);
				(new JdbcPAMRunningReportingDao(dao)).notifyNightAdvantages(pamRunningReporting, subscriber.getId(), !waitingForResponse, true);

				return subscriber;
			}
			else {
				if(!waitingForResponse) pamRunningReporting.setFlag(false);
				(new JdbcPAMRunningReportingDao(dao)).notifyNightAdvantages(pamRunningReporting, subscriber.getId(), !waitingForResponse, false);
			}

		} catch(AirAvailabilityException ex) {
			throw ex;

		} catch(Exception ex) {
			if(ex instanceof AirAvailabilityException) throw ex;

		} catch(Throwable th) {
			if(th instanceof AirAvailabilityException) throw th;
		}

		return null;
	}

	public boolean getNightAdvantages(ProductProperties productProperties, String msisdn) throws AirAvailabilityException {
		if(((productProperties.getNight_advantages_call_da() == 0) && (productProperties.getNight_advantages_data_da() == 0))) return false;

		try {
			// attempts
			int retry = 0;

			while(productProperties.getAir_preferred_host() == -1) {
				if(retry >= 3) throw new AirAvailabilityException();

				productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
				retry++;
			}

			retry = 0;

			AIRRequest request = new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host());
			HashSet<BalanceAndDate> balancesBonus = null;

			if((productProperties.getNight_advantages_call_da() > 0) && (productProperties.getNight_advantages_data_da() > 0)) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}, {productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});
			else if(productProperties.getNight_advantages_call_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_call_da(), productProperties.getNight_advantages_call_da()}});
			else if(productProperties.getNight_advantages_data_da() > 0) balancesBonus = request.getDedicatedAccounts(msisdn, new int[][] {{productProperties.getNight_advantages_data_da(), productProperties.getNight_advantages_data_da()}});

			if(((productProperties.getNight_advantages_call_da() > 0) || (productProperties.getNight_advantages_data_da() > 0)) && (!request.isSuccessfully())) {
				productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host())) ;
				throw new AirAvailabilityException() ;
			}

			if((balancesBonus != null) && (!balancesBonus.isEmpty())) {
				BalanceAndDate balanceBonusCall = null;
				BalanceAndDate balanceBonusData = null;

				for(BalanceAndDate balanceAndDate : balancesBonus) {
					if(balanceAndDate.getExpiryDate() != null) {
						Date expiryDate = (Date)balanceAndDate.getExpiryDate();

						if((expiryDate == null) || ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(expiryDate).equals("9999-12-30 13:00:00")));
						else {
							if(((Date)balanceAndDate.getExpiryDate()).after(new Date())) {
								if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_call_da())) balanceBonusCall = balanceAndDate;
								else if((balanceAndDate.getAccountID() > 0) && (balanceAndDate.getAccountID() == productProperties.getNight_advantages_data_da())) balanceBonusData = balanceAndDate;
							}
						}
					}
				}

				if((balanceBonusCall != null) || (balanceBonusData != null)) {
					return true;
				}
			}

		} catch(Throwable th) {
			
		}

		return false;
	}
}
