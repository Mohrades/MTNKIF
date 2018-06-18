package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import connexions.AIRRequest;
import dao.DAO;
import product.ProductProperties;

@Component("cleanExpiredUssdRequestTasklet")
public class CleanExpiredUssdRequestTasklet implements Tasklet {

	@Autowired
	private DAO dao;

	@Autowired
	private ProductProperties productProperties;

	@SuppressWarnings("deprecation")
	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		// TODO Auto-generated method stub

		try {
			// test AIR Connection
			productProperties.setAir_preferred_host((byte) (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).testConnection(productProperties.getAir_test_connection_msisdn(), productProperties.getAir_preferred_host()));

			// delete expired ussd
			Date now = new Date();
			now.setMinutes(now.getMinutes() - 5);

			dao.getJdbcTemplate().update("DELETE FROM MTN_KIF_USSD_EBA WHERE LAST_UPDATE_TIME < TIMESTAMP '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(now) + "'");

			stepContribution.setExitStatus(ExitStatus.COMPLETED);
			return RepeatStatus.FINISHED;

		} catch(Throwable th) {
			
		}
		
		return null;
	}

}
