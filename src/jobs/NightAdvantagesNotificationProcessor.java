package jobs;

import org.springframework.batch.item.ItemProcessor;

import connexions.AIRRequest;
import dao.DAO;
import dao.queries.PAMRunningReportingDAOJdbc;
import dao.queries.SubscriberDAOJdbc;
import domain.models.PAMRunningReporting;
import domain.models.Subscriber;
import exceptions.AirAvailabilityException;
import product.ProductProperties;
import util.BalanceAndDate;

public class NightAdvantagesNotificationProcessor implements ItemProcessor<PAMRunningReporting, Subscriber> {

	private DAO dao;

	private ProductProperties productProperties;

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

	@Override
	public Subscriber process(PAMRunningReporting pamRunningReporting) throws AirAvailabilityException {
		// TODO Auto-generated method stub

		try {
			Subscriber subscriber = (new SubscriberDAOJdbc(dao)).getOneSubscriber(pamRunningReporting.getSubscriber(), true);

			// check night advantages
			if(getNightAdvantages(productProperties, subscriber.getValue())) {
				// save notification
				(new PAMRunningReportingDAOJdbc(dao)).notifyNightAdvantages(pamRunningReporting.getId(), subscriber.getId());

				return subscriber;
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

			BalanceAndDate balanceBonusCall = (productProperties.getNight_advantages_call_da() > 0) ? request.getBalanceAndDate(msisdn, productProperties.getNight_advantages_call_da()) : null;
			BalanceAndDate balanceBonusData = (productProperties.getNight_advantages_data_da() > 0) ? request.getBalanceAndDate(msisdn, productProperties.getNight_advantages_data_da()) : null;

			if(((productProperties.getNight_advantages_call_da() > 0) || (productProperties.getNight_advantages_data_da() > 0)) && (!request.isSuccessfully())) {
				productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host())) ;
				throw new AirAvailabilityException() ;
			}

			if((balanceBonusCall != null) && (balanceBonusData != null)) {
				return true;
			}
			else if(balanceBonusCall != null) {
				return true;
			}
			else if(balanceBonusData != null) {
				return true;
			}

		} catch(Throwable th) {
			
		}

		return false;
	}

}
