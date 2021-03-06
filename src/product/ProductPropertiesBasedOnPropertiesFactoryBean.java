package product;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import com.google.common.base.Splitter;

/*@Component(value="productProperties")*/
public class ProductPropertiesBasedOnPropertiesFactoryBean implements ProductProperties {

	@Value("#{appConfig['gsm.mcc']}")
	private short mcc;

	@Value("#{appConfig['gsm.name']}")
	private String gsm_name;

	@Value("#{appConfig['gsm.short_code']}")
	private short sc;

	@Value("#{appConfig['sms.notifications.header']}")
	private String sms_notifications_header;

	@Value("#{appConfig['happy.birthday.sms.notifications.header']}")
	private String happy_birthday_sms_notifications_header;

	private List<String> mnc;

	@Value("#{appConfig['msisdn.length']}")
	private byte msisdn_length;
	
	@Value("#{appConfig['chargingDA']}")
	private int chargingDA;

	@Value("#{appConfig['activation.chargingAmount']}")
	private long activation_chargingAmount;

	@Value("#{appConfig['default.price.plan']}")
	private String default_price_plan;

	@Value("#{appConfig['default.price.plan.deactivated']}")
	private boolean default_price_plan_deactivated;

	@Value("#{appConfig['default.price.plan.url']}")
	private String default_price_plan_url;

	@Value("#{appConfig['advantages.always']}")
	private boolean advantages_always;

	@Value("#{appConfig['advantages.sms.da']}")
	private int advantages_sms_da;

	@Value("#{appConfig['advantages.sms.value']}")
	private long advantages_sms_value;

	@Value("#{appConfig['advantages.data.da']}")
	private int advantages_data_da;

	@Value("#{appConfig['advantages.data.value']}")
	private long advantages_data_value;

	@Value("#{appConfig['deactivation.freeCharging.days']}")
	private short deactivation_freeCharging_days;
	
	@Value("#{appConfig['deactivation.chargingAmount']}")
	private long deactivation_chargingAmount;

	@Value("#{appConfig['happy.birthday.bonus.voice.da']}")
	private int happy_birthday_bonus_voice_da;

	@Value("#{appConfig['happy.birthday.bonus.voice.volume']}")
	private long happy_birthday_bonus_voice_volume;

	@Value("#{appConfig['happy.birthday.bonus.data.da']}")
	private int happy_birthday_bonus_data_da;

	@Value("#{appConfig['happy.birthday.bonus.data.volume']}")
	private long happy_birthday_bonus_data_volume;

	@Value("#{appConfig['happy.birthday.bonus.expires_in_days']}")
	private short happy_birthday_bonus_expires_in_days;

	@Value("#{appConfig['happy.birthday.bonus.offer.id']}")
	private int happy_birthday_bonus_offer_id;

	private List<String> xtra_serviceOfferings_IDs;
	private List<String> xtra_serviceOfferings_activeFlags;
	private List<String> xtra_removal_offer_IDs;

	private List<String> serviceOfferings_IDs;
	private List<String> serviceOfferings_activeFlags;

	@Value("#{appConfig['community.id']}")
	private int community_id;

	@Value("#{appConfig['offer.id']}")
	private int offer_id;

	@Value("#{appConfig['scheduleID']}")
	private short scheduleID;

	@Value("#{appConfig['pamServiceID']}")
	private byte pamServiceID;

	@Value("#{appConfig['pamClassID']}")
	private short pamClassID;

	@Value("#{appConfig['productID']}")
	private short productID;

	@Value("#{appConfig['crbt.renewal.aspu.minimum']}")
	private long crbt_renewal_aspu_minimum;

	@Value("#{appConfig['crbt.renewal.days']}")
	private short crbt_renewal_days;

	@Value("#{appConfig['happy.birthday.bonus.aspu.minimum']}")
	private long happy_birthday_bonus_aspu_minimum;

	@Value("#{appConfig['database.aspu.filter']}")
	private String database_aspu_filter;

	@Value("#{appConfig['song.rbt.code']}")
	private String song_rbt_code;

	@Value("#{appConfig['song.rbt.id']}")
	private String song_rbt_id;

	private List<String> hbd_serviceClass_include_filter;
	private List<String> hbd_db_include_filter;
	private List<String> hbd_serviceClass_exclude_filter;
	private List<String> hbd_db_exclude_filter;

	private List<String> Anumber_serviceClass_include_filter;
	private List<String> Anumber_db_include_filter;
	private List<String> Anumber_serviceClass_exclude_filter;
	private List<String> Anumber_db_exclude_filter;

	private List<String> Bnumber_serviceClass_include_filter;
	private List<String> Bnumber_db_include_filter;
	private List<String> Bnumber_serviceClass_exclude_filter;
	private List<String> Bnumber_db_exclude_filter;

	/*private List<String> originOperatorIDs_list;*/

	private List<String> air_hosts;
	@Value("#{appConfig['air.io.sleep']}")
	private int air_io_sleep;
	@Value("#{appConfig['air.io.timeout']}")
	private int air_io_timeout;
	@Value("#{appConfig['air.io.threshold']}")
	private int air_io_threshold;
	@Value("#{appConfig['air.test.connection.msisdn']}")
	private String air_test_connection_msisdn;
	@Value("#{appConfig['air.preferred.host']}")
	private byte air_preferred_host;

	private List<String> ema_hosts;
	@Value("#{appConfig['ema.io.sleep']}")
	private int ema_io_sleep;
	@Value("#{appConfig['ema.io.timeout']}")
	private int ema_io_timeout;

	@Value("#{appConfig['crbt.server.host']}")
	private String crbt_server_host;
	@Value("#{appConfig['crbt.server.io.sleep']}")
	private int crbt_server_io_sleep;
	@Value("#{appConfig['crbt.server.io.timeout']}")
	private int crbt_server_io_timeout;

	@Value("#{appConfig['bonus.reset.required']}")
	private boolean bonus_reset_required;
	@Value("#{appConfig['bonus.sms.onNet.accumulator']}")
	private int bonus_sms_onNet_accumulator;
	@Value("#{appConfig['bonus.sms.offNet.accumulator']}")
	private int bonus_sms_offNet_accumulator;
	@Value("#{appConfig['bonus.sms.remaining.accumulator']}")
	private int bonus_sms_remaining_accumulator;
	@Value("#{appConfig['bonus.sms.offNet.threshold']}")
	private int bonus_sms_offNet_threshold;
	@Value("#{appConfig['bonus.sms.threshold']}")
	private int bonus_sms_threshold;
	@Value("#{appConfig['night.advantages.call.da']}")
	private int night_advantages_call_da;
	@Value("#{appConfig['night.advantages.data.da']}")
	private int night_advantages_data_da;
	@Value("#{appConfig['night.advantages.expires_in']}")
	private String night_advantages_expires_in;

	@Value("#{appConfig['gsm.mnc']}")
	public void setMnc(final String gsmmnc) {
		if(isSet(gsmmnc)) {
			mnc = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(gsmmnc);
		}
	 }

	@Value("#{appConfig['air.hosts']}")
	public void setAir_hosts(final String air_hosts) {
		if(isSet(air_hosts)) {
			this.air_hosts = Splitter.onPattern("[;]").trimResults().omitEmptyStrings().splitToList(air_hosts);
		}
	}

	@Value("#{appConfig['ema.hosts']}")
	public void setEma_hosts(final String ema_hosts) {
		if(isSet(ema_hosts)) {
			this.ema_hosts = Splitter.onPattern("[;]").trimResults().omitEmptyStrings().splitToList(ema_hosts);
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

	@Value("#{appConfig['hbd.serviceClass.include_filter']}")
	public void setHbd_serviceClass_include_filter(final String hbd_serviceClass_include_filter) {
		if(isSet(hbd_serviceClass_include_filter)) {
			this.hbd_serviceClass_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(hbd_serviceClass_include_filter);
		}
	}

	@Value("#{appConfig['hbd.db.include_filter']}")
	public void setHbd_db_include_filter(final String hbd_db_include_filter) {
		if(isSet(hbd_db_include_filter)) {
			this.hbd_db_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(hbd_db_include_filter);
		}
	}

	@Value("#{appConfig['hbd.serviceClass.exclude_filter']}")
	public void setHbd_serviceClass_exclude_filter(final String hbd_serviceClass_exclude_filter) {
		if(isSet(hbd_serviceClass_exclude_filter)) {
			this.hbd_serviceClass_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(hbd_serviceClass_exclude_filter);
		}
	}

	@Value("#{appConfig['hbd.db.exclude_filter']}")
	public void setHbd_db_exclude_filter(final String hbd_db_exclude_filter) {
		if(isSet(hbd_db_exclude_filter)) {
			this.hbd_db_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(hbd_db_exclude_filter);
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

	/*@Value("#{appConfig['originOperatorIDs.list']}")
	public void setOriginOperatorIDs_list(final String originOperatorIDs_list) {
		if(isSet(originOperatorIDs_list)) {
			this.originOperatorIDs_list = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(originOperatorIDs_list);
		}
	}*/

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

	public String getDefault_price_plan() {
		return default_price_plan;
	}

	public boolean isDefault_price_plan_deactivated() {
		return default_price_plan_deactivated;
	}

	public String getDefault_price_plan_url() {
		return default_price_plan_url;
	}

	public String getHappy_birthday_sms_notifications_header() {
		return happy_birthday_sms_notifications_header;
	}

	public long getHappy_birthday_bonus_aspu_minimum() {
		return happy_birthday_bonus_aspu_minimum;
	}

	public int getHappy_birthday_bonus_offer_id() {
		return happy_birthday_bonus_offer_id;
	}

	public int getHappy_birthday_bonus_data_da() {
		return happy_birthday_bonus_data_da;
	}

	public long getHappy_birthday_bonus_data_volume() {
		return happy_birthday_bonus_data_volume;
	}

	public int getHappy_birthday_bonus_voice_da() {
		return happy_birthday_bonus_voice_da;
	}

	public long getHappy_birthday_bonus_voice_volume() {
		return happy_birthday_bonus_voice_volume;
	}

	public short getHappy_birthday_bonus_expires_in_days() {
		return happy_birthday_bonus_expires_in_days;
	}

	public boolean isBonus_reset_required() {
		return bonus_reset_required;
	}

	public int getBonus_sms_onNet_accumulator() {
		return bonus_sms_onNet_accumulator;
	}

	public int getBonus_sms_offNet_accumulator() {
		return bonus_sms_offNet_accumulator;
	}

	public int getBonus_sms_remaining_accumulator() {
		return bonus_sms_remaining_accumulator;
	}

	public int getBonus_sms_offNet_threshold() {
		return bonus_sms_offNet_threshold;
	}

	public int getBonus_sms_threshold() {
		return bonus_sms_threshold;
	}

	public int getNight_advantages_call_da() {
		return night_advantages_call_da;
	}

	public int getNight_advantages_data_da() {
		return night_advantages_data_da;
	}

	public String getNight_advantages_expires_in() {
		return night_advantages_expires_in;
	}

	public boolean isAdvantages_always() {
		return advantages_always;
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

	public String getSong_rbt_code() {
		return song_rbt_code;
	}

	public String getSong_rbt_id() {
		return song_rbt_id;
	}

	public long getCrbt_renewal_aspu_minimum() {
		return crbt_renewal_aspu_minimum;
	}

	public short getCrbt_renewal_days() {
		return crbt_renewal_days;
	}

	public String getDatabase_aspu_filter() {
		return database_aspu_filter;
	}

	public short getDeactivation_freeCharging_days() {
		return deactivation_freeCharging_days;
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

	public int getCommunity_id() {
		return community_id;
	}

	public int getOffer_id() {
		return offer_id;
	}

	public short getScheduleID() {
		return scheduleID;
	}

	public byte getPamServiceID() {
		return pamServiceID;
	}

	public short getPamClassID() {
		return pamClassID;
	}

	public short getProductID() {
		return productID;
	}

	public List<String> getXtra_removal_offer_IDs() {
		return xtra_removal_offer_IDs;
	}

	public List<String> getHbd_serviceClass_include_filter() {
		return hbd_serviceClass_include_filter;
	}

	public List<String> getHbd_db_include_filter() {
		return hbd_db_include_filter;
	}

	public List<String> getHbd_serviceClass_exclude_filter() {
		return hbd_serviceClass_exclude_filter;
	}

	public List<String> getHbd_db_exclude_filter() {
		return hbd_db_exclude_filter;
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

	/*public List<String> getOriginOperatorIDs_list() {
		return originOperatorIDs_list;
	}*/

	public List<String> getAir_hosts() {
		return air_hosts;
	}

	public int getAir_io_sleep() {
		return air_io_sleep;
	}

	public int getAir_io_timeout() {
		return air_io_timeout;
	}

	public int getAir_io_threshold() {
		return air_io_threshold;
	}

	public String getAir_test_connection_msisdn() {
		return air_test_connection_msisdn;
	}

	public byte getAir_preferred_host() {
		return air_preferred_host;
	}

	public void setAir_preferred_host(byte air_preferred_host) {
		this.air_preferred_host = air_preferred_host;
	}

	public List<String> getEma_hosts() {
		return ema_hosts;
	}

	public int getEma_io_sleep() {
		return ema_io_sleep;
	}

	public int getEma_io_timeout() {
		return ema_io_timeout;
	}

	public String getCrbt_server_host() {
		return crbt_server_host;
	}

	public int getCrbt_server_io_sleep() {
		return crbt_server_io_sleep;
	}

	public int getCrbt_server_io_timeout() {
		return crbt_server_io_timeout;
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
