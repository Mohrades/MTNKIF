package product;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;

@Component(value="productProperties")
public class ProductProperties implements InitializingBean, DisposableBean {

	@Value("#{appConfig['gsm.mcc']}")
	private short mcc;

	@Value("#{appConfig['gsm.name']}")
	private String gsm_name;

	@Value("#{appConfig['gsm.short_code']}")
	private short sc;

	@Value("#{appConfig['sms.notifications.header']}")
	private String sms_notifications_header;

	private List<String> mnc;

	@Value("#{appConfig['msisdn.length']}")
	private byte msisdn_length;
	
	@Value("#{appConfig['charging.da']}")
	private int chargingDA;

	@Value("#{appConfig['activation.chargingAmount']}")
	private long activation_chargingAmount;
	
	@Value("#{appConfig['default.price.plan.deactivated']}")
	private boolean default_price_plan_deactivated;

	@Value("#{appConfig['default.price.plan.url']}")
	private String default_price_plan_url;

	@Value("#{appConfig['advantages.sms.da']}")
	private int advantages_sms_da;

	@Value("#{appConfig['advantages.sms.value']}")
	private long advantages_sms_value;

	@Value("#{appConfig['advantages.data.da']}")
	private int advantages_data_da;

	@Value("#{appConfig['advantages.data.value']}")
	private long advantages_data_value;

	@Value("#{appConfig['deactivation.freeCharging.startDate']}")
	private short deactivation_freeCharging_startDate;
	
	@Value("#{appConfig['deactivation.chargingAmount']}")
	private long deactivation_chargingAmount;

	private List<String> xtra_serviceOfferings_IDs;
	private List<String> xtra_serviceOfferings_activeFlags;
	private List<String> xtra_removal_offer_IDs;

	private List<String> serviceOfferings_IDs;
	private List<String> serviceOfferings_activeFlags;
	
	@Value("#{appConfig['offer.id']}")
	private int offer_id;

	@Value("#{appConfig['crbt.renewal.aspu.minimum']}")
	private long crbt_renewal_aspu_minimum;

	@Value("#{appConfig['crbt.default.song']}")
	private short crbt_default_song;

	private List<String> Anumber_serviceClass_include_filter;
	private List<String> Anumber_db_include_filter;
	private List<String> Anumber_serviceClass_exclude_filter;
	private List<String> Anumber_db_exclude_filter;

	private List<String> Bnumber_serviceClass_include_filter;
	private List<String> Bnumber_db_include_filter;
	private List<String> Bnumber_serviceClass_exclude_filter;
	private List<String> Bnumber_db_exclude_filter;

	@Value("#{appConfig['gsm.mnc']}")
	public void setMnc(final String gsmmnc) {
		if(isSet(gsmmnc)) {
			mnc = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(gsmmnc);
		}
	 }

	@Value("#{appConfig['xtra.serviceOfferings.IDs']}")
	public void setXtra_serviceOfferings_IDs(final String xtra_serviceOfferings_IDs) {
		if(isSet(xtra_serviceOfferings_IDs)) {
			this.xtra_serviceOfferings_IDs = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(xtra_serviceOfferings_IDs);
		}
	}

	@Value("#{appConfig['xtra.serviceOfferings.activeFlags']}")
	public void setXtra_serviceOfferings_activeFlags(final String xtra_serviceOfferings_activeFlags) {
		if(isSet(xtra_serviceOfferings_activeFlags)) {
			this.xtra_serviceOfferings_activeFlags = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(xtra_serviceOfferings_activeFlags);
		}
	}

	@Value("#{appConfig['serviceOfferings.IDs']}")
	public void setServiceOfferings_IDs(final String serviceOfferings_IDs) {
		if(isSet(serviceOfferings_IDs)) {
			this.serviceOfferings_IDs = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(serviceOfferings_IDs);
		}
	}

	@Value("#{appConfig['serviceOfferings.activeFlags']}")
	public void setServiceOfferings_activeFlags(final String serviceOfferings_activeFlags) {
		if(isSet(serviceOfferings_activeFlags)) {
			this.serviceOfferings_activeFlags = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(serviceOfferings_activeFlags);
		}
	}

	@Value("#{appConfig['xtra.removal.offer.IDs']}")
	public void setXtra_removal_offer_IDs(final String xtra_removal_offer_IDs) {
		if(isSet(xtra_removal_offer_IDs)) {
			this.xtra_removal_offer_IDs = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(xtra_removal_offer_IDs);
		}
	}

	@Value("#{appConfig['Anumber.serviceClass.include_filter']}")
	public void setAnumber_serviceClass_include_filter(final String anumber_serviceClass_include_filter) {
		if(isSet(anumber_serviceClass_include_filter)) {
			Anumber_serviceClass_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_serviceClass_include_filter);
		}
	}

	@Value("#{appConfig['Anumber.db.include_filter']}")
	public void setAnumber_db_include_filter(final String anumber_db_include_filter) {
		if(isSet(anumber_db_include_filter)) {
			Anumber_db_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_db_include_filter);
		}
	}

	@Value("#{appConfig['Anumber.serviceClass.exclude_filter']}")
	public void setAnumber_serviceClass_exclude_filter(final String anumber_serviceClass_exclude_filter) {
		if(isSet(anumber_serviceClass_exclude_filter)) {
			Anumber_serviceClass_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_serviceClass_exclude_filter);
		}
	}

	@Value("#{appConfig['Anumber.db.exclude_filter']}")
	public void setAnumber_db_exclude_filter(final String anumber_db_exclude_filter) {
		if(isSet(anumber_db_exclude_filter)) {
			Anumber_db_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_db_exclude_filter);
		}
	}

	@Value("#{appConfig['Bnumber.serviceClass.include_filter']}")
	public void setBnumber_serviceClass_include_filter(final String bnumber_serviceClass_include_filter) {
		if(isSet(bnumber_serviceClass_include_filter)) {
			Bnumber_serviceClass_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_serviceClass_include_filter);
		}
	}

	@Value("#{appConfig['Bnumber.db.include_filter']}")
	public void setBnumber_db_include_filter(final String bnumber_db_include_filter) {
		if(isSet(bnumber_db_include_filter)) {
			Bnumber_db_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_db_include_filter);
		}
	}

	@Value("#{appConfig['Bnumber.serviceClass.exclude_filter']}")
	public void setBnumber_serviceClass_exclude_filter(final String bnumber_serviceClass_exclude_filter) {
		if(isSet(bnumber_serviceClass_exclude_filter)) {
			Bnumber_serviceClass_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_serviceClass_exclude_filter);
		}
	}

	@Value("#{appConfig['Bnumber.db.exclude_filter']}")
	public void setBnumber_db_exclude_filter(final String bnumber_db_exclude_filter) {
		if(isSet(bnumber_db_exclude_filter)) {
			Bnumber_db_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_db_exclude_filter);
		}
	}

	public short getMcc() {
		return mcc;
	}

	public String getGsm_name() {
		return gsm_name;
	}

	public short getSc() {
		return sc;
	}

	public String getSms_notifications_header() {
		return sms_notifications_header;
	}

	public List<String> getMnc() {
		return mnc;
	}

	public byte getMsisdn_length() {
		return msisdn_length;
	}

	public List<String> getXtra_serviceOfferings_IDs() {
		return xtra_serviceOfferings_IDs;
	}

	public List<String> getXtra_serviceOfferings_activeFlags() {
		return xtra_serviceOfferings_activeFlags;
	}

	public boolean isDefault_price_plan_deactivated() {
		return default_price_plan_deactivated;
	}

	public String getDefault_price_plan_url() {
		return default_price_plan_url;
	}

	public int getAdvantages_sms_da() {
		return advantages_sms_da;
	}

	public long getAdvantages_sms_value() {
		return advantages_sms_value;
	}

	public int getAdvantages_data_da() {
		return advantages_data_da;
	}

	public long getAdvantages_data_value() {
		return advantages_data_value;
	}

	public int getChargingDA() {
		return chargingDA;
	}

	public long getActivation_chargingAmount() {
		return activation_chargingAmount;
	}

	public short getCrbt_default_song() {
		return crbt_default_song;
	}

	public long getCrbt_renewal_aspu_minimum() {
		return crbt_renewal_aspu_minimum;
	}

	public short getDeactivation_freeCharging_startDate() {
		return deactivation_freeCharging_startDate;
	}

	public long getDeactivation_chargingAmount() {
		return deactivation_chargingAmount;
	}

	public List<String> getServiceOfferings_IDs() {
		return serviceOfferings_IDs;
	}

	public List<String> getServiceOfferings_activeFlags() {
		return serviceOfferings_activeFlags;
	}

	public int getOffer_id() {
		return offer_id;
	}

	public List<String> getXtra_removal_offer_IDs() {
		return xtra_removal_offer_IDs;
	}

	public List<String> getAnumber_serviceClass_include_filter() {
		return Anumber_serviceClass_include_filter;
	}

	public List<String> getAnumber_db_include_filter() {
		return Anumber_db_include_filter;
	}

	public List<String> getAnumber_serviceClass_exclude_filter() {
		return Anumber_serviceClass_exclude_filter;
	}

	public List<String> getAnumber_db_exclude_filter() {
		return Anumber_db_exclude_filter;
	}

	public List<String> getBnumber_serviceClass_include_filter() {
		return Bnumber_serviceClass_include_filter;
	}

	public List<String> getBnumber_db_include_filter() {
		return Bnumber_db_include_filter;
	}

	public List<String> getBnumber_serviceClass_exclude_filter() {
		return Bnumber_serviceClass_exclude_filter;
	}

	public List<String> getBnumber_db_exclude_filter() {
		return Bnumber_db_exclude_filter;
	}

	public boolean isSet(String property_value) {
		if((property_value == null) || (property_value.trim().length() == 0)) return false;
		else return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
