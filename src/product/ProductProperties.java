package product;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface ProductProperties extends InitializingBean, DisposableBean {

	public void setMnc(final String gsmmnc) ;

	public void setAir_hosts(final String air_hosts) ;

	public void setEma_hosts(final String ema_hosts) ;

	public void setXtra_serviceOfferings_IDs(final String xtra_serviceOfferings_IDs) ;

	public void setXtra_serviceOfferings_activeFlags(final String xtra_serviceOfferings_activeFlags) ;

	public void setServiceOfferings_IDs(final String serviceOfferings_IDs) ;

	public void setServiceOfferings_activeFlags(final String serviceOfferings_activeFlags) ;

	public void setXtra_removal_offer_IDs(final String xtra_removal_offer_IDs) ;

	public void setAnumber_serviceClass_include_filter(final String anumber_serviceClass_include_filter) ;

	public void setAnumber_db_include_filter(final String anumber_db_include_filter) ;

	public void setAnumber_serviceClass_exclude_filter(final String anumber_serviceClass_exclude_filter) ;

	public void setAnumber_db_exclude_filter(final String anumber_db_exclude_filter) ;

	public void setBnumber_serviceClass_include_filter(final String bnumber_serviceClass_include_filter) ;

	public void setBnumber_db_include_filter(final String bnumber_db_include_filter) ;

	public void setBnumber_serviceClass_exclude_filter(final String bnumber_serviceClass_exclude_filter) ;

	public void setBnumber_db_exclude_filter(final String bnumber_db_exclude_filter) ;

	/*public void setOriginOperatorIDs_list(final String originOperatorIDs_list) ;*/

	public short getMcc() ;

	public String getGsm_name() ;

	public short getSc() ;

	public String getSms_notifications_header() ;

	public List<String> getMnc() ;

	public byte getMsisdn_length() ;

	public List<String> getXtra_serviceOfferings_IDs() ;

	public List<String> getXtra_serviceOfferings_activeFlags() ;

	public String getDefault_price_plan() ;

	public boolean isDefault_price_plan_deactivated() ;

	public String getDefault_price_plan_url() ;

	public String getHappy_birthday_sms_notifications_header() ;

	public long getHappy_birthday_bonus_aspu_minimum() ;

	public int getHappy_birthday_bonus_offer_id() ;

	public int getHappy_birthday_bonus_data_da() ;

	public long getHappy_birthday_bonus_data_volume() ;

	public int getHappy_birthday_bonus_voice_da() ;

	public long getHappy_birthday_bonus_voice_volume() ;

	public int getBonus_sms_onNet_accumulator() ;

	public int getBonus_sms_offNet_accumulator() ;

	public int getBonus_sms_remaining_accumulator() ;

	public int getBonus_sms_offNet_threshold() ;

	public int getBonus_sms_threshold() ;

	public int getNight_advantages_call_da() ;

	public int getNight_advantages_data_da() ;

	public boolean isAdvantages_always() ;

	public int getAdvantages_sms_da() ;

	public long getAdvantages_sms_value() ;

	public int getAdvantages_data_da() ;

	public long getAdvantages_data_value() ;

	public int getChargingDA() ;

	public long getActivation_chargingAmount() ;

	public String getSong_rbt_code() ;

	public String getSong_rbt_id() ;

	public long getCrbt_renewal_aspu_minimum() ;

	public String getDatabase_aspu_filter() ;

	public short getDeactivation_freeCharging_days() ;

	public long getDeactivation_chargingAmount() ;

	public List<String> getServiceOfferings_IDs() ;

	public List<String> getServiceOfferings_activeFlags() ;

	public int getCommunity_id() ;

	public int getOffer_id() ;

	public short getScheduleID() ;

	public byte getPamServiceID() ;

	public short getPamClassID() ;

	public short getProductID() ;

	public List<String> getXtra_removal_offer_IDs() ;

	public List<String> getAnumber_serviceClass_include_filter() ;

	public List<String> getAnumber_db_include_filter() ;

	public List<String> getAnumber_serviceClass_exclude_filter() ;

	public List<String> getAnumber_db_exclude_filter() ;

	public List<String> getBnumber_serviceClass_include_filter() ;

	public List<String> getBnumber_db_include_filter() ;

	public List<String> getBnumber_serviceClass_exclude_filter() ;

	public List<String> getBnumber_db_exclude_filter() ;

	/*public List<String> getOriginOperatorIDs_list() ;*/

	public List<String> getAir_hosts() ;

	public int getAir_io_sleep() ;

	public int getAir_io_timeout() ;

	public int getAir_io_threshold() ;

	public String getAir_test_connection_msisdn() ;

	public byte getAir_preferred_host() ;

	public void setAir_preferred_host(byte air_preferred_host) ;

	public List<String> getEma_hosts() ;

	public int getEma_io_sleep() ;

	public int getEma_io_timeout() ;

	public String getCrbt_server_host() ;

	public int getCrbt_server_io_sleep() ;

	public int getCrbt_server_io_timeout() ;

}
