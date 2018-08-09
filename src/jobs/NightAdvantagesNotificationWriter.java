package jobs;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.MessageSource;
import connexions.AIRRequest;
import domain.models.Subscriber;
import product.ProductProperties;
import tools.SMPPConnector;
import util.AccountDetails;

public class NightAdvantagesNotificationWriter implements ItemWriter<Subscriber> {

	private MessageSource i18n;

	private ProductProperties productProperties;

	public NightAdvantagesNotificationWriter() {

	}

	public MessageSource getI18n() {
		return i18n;
	}

	public void setI18n(MessageSource i18n) {
		this.i18n = i18n;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void write(List<? extends Subscriber> subscribers) {
		// TODO Auto-generated method stub

		try {
			/*if(new Date().getHours() <= 5) {*/
			if(new Date().getHours() <= 4) {
				Logger logger = LogManager.getLogger("logging.log4j.SubmitSMLogger");

				for(Subscriber subscriber : subscribers) {
					if(subscriber != null) {
						try {
							AccountDetails accountDetails = (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).getAccountDetails(subscriber.getValue());
							String message = i18n.getMessage("night.advantages.notification.message", null, null, (accountDetails == null) ? Locale.FRENCH : (accountDetails.getLanguageIDCurrent() == 2) ? Locale.ENGLISH : Locale.FRENCH);

							/*new SMPPConnector().submitSm("HVC", hvc.getValue(), message);*/
							new SMPPConnector().submitSm(productProperties.getSms_notifications_header(), subscriber.getValue(), message);
							logger.log(Level.TRACE, "[" + subscriber.getValue() + "] " + message);

						} catch(NullPointerException ex) {

						} catch(Throwable th) {

						}
					}
				}
			}

		} catch(Throwable th) {

		}
	}

}
