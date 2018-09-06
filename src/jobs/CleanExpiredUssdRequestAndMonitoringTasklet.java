package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import connexions.AIRRequest;
import dao.DAO;
import product.ProductProperties;

/*@Component("cleanExpiredUssdRequestTasklet")*/
public class CleanExpiredUssdRequestAndMonitoringTasklet implements Tasklet {

	/*@Autowired*/
	private DAO dao;

	/*@Autowired*/
	private ProductProperties productProperties;

	public CleanExpiredUssdRequestAndMonitoringTasklet() {
		
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
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		// TODO Auto-generated method stub

		try {
			checkAirConnectivity();
			cleanExpiredUssdRequests();

			stepContribution.setExitStatus(ExitStatus.COMPLETED);
			return RepeatStatus.FINISHED;

		} catch(Throwable th) {
			
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public void cleanExpiredUssdRequests() {
		try {
			// delete expired ussd
			Date now = new Date();
			now.setMinutes(now.getMinutes() - 5);

			dao.getJdbcTemplate().update("DELETE FROM MTN_KIF_USSD_EBA WHERE LAST_UPDATE_TIME < TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'");

		} catch(Throwable th) {
			
		}
	}
	
	public void checkAirConnectivity() {
		try {
			// test AIR Connection
			productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));
			if(productProperties.getAir_preferred_host() == -1) {
				Logger logger = LogManager.getLogger("logging.log4j.AirAvailabilityLogger");
				logger.error("ALL AIR NODES ARE DOWN !");
			}

		} catch(Throwable th) {
			
		}
	}

}
