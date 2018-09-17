package jobs;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.MessageSource;

import com.tools.SMPPConnector;

import connexions.AIRRequest;
import domain.models.Subscriber;
import product.ProductProperties;
import util.AccountDetails;

public class PeriodicSubscriberManagementWriter implements ItemWriter<Subscriber> {

	private MessageSource i18n;

	private ProductProperties productProperties;

	public PeriodicSubscriberManagementWriter() {

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

	@Override
	public void write(List<? extends Subscriber> subscribers) {
		// TODO Auto-generated method stub

		try {
			for(Subscriber subscriber : subscribers) {
				if(subscriber != null) {
					try {
						AccountDetails accountDetails = (new AIRRequest(productProperties.getAir_hosts(), productProperties.getAir_io_sleep(), productProperties.getAir_io_timeout(), productProperties.getAir_io_threshold(), productProperties.getAir_preferred_host())).getAccountDetails(subscriber.getValue());
						int lang = (accountDetails == null) ? 1 : accountDetails.getLanguageIDCurrent();

						// default crbt song rolled over successfully : send notification sms
						if(subscriber.getId() > 0) {
							String notification_message = i18n.getMessage("crbt.renewal.successful", null, null, (lang == 2) ? Locale.ENGLISH : Locale.FRENCH);
							requestSubmitSmToSmppConnector(notification_message, subscriber.getValue(), productProperties.getSms_notifications_header());
						}
						//  default crbt song deleted successfully
						else if(subscriber.getId() < 0) {
							
						}
						// crbt renewal processing throws error
						else {
							
						}

						// send monthly reminder notification sms
						String notification_message = i18n.getMessage("monthly.reminder", null, null, (lang == 2) ? Locale.ENGLISH : Locale.FRENCH);
						requestSubmitSmToSmppConnector(notification_message, subscriber.getValue(), productProperties.getSms_notifications_header());

					} catch(NullPointerException ex) {

					} catch(Throwable th) {

					}
				}
			}

		} catch(Throwable th) {

		}
	}

	public void requestSubmitSmToSmppConnector(String message, String msisdn, String senderName) {
		if(senderName != null) {
			Logger logger = LogManager.getLogger("logging.log4j.SubmitSMLogger");

			if(msisdn != null) {
				new SMPPConnector().submitSm(senderName, msisdn, message);
				logger.log(Level.TRACE, "[" + msisdn + "] " + message);
			}
			else {
				new SMPPConnector().submitSm(senderName, msisdn, message);
				logger.trace("[" + msisdn + "] " + message);
			}
		}
	}

}
