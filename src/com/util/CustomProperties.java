package com.util;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * custom properties class
 */
public class CustomProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7149552055602431893L;
	
	public CustomProperties() {
		
	}
	
	// This will make sure that you will have properties loaded from database as well as any additional configuration that you can safely put in application.
	public CustomProperties(DataSource dataSource, int serviceShortCode) {
		super();

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		/*List<Map<String, Object>> configs = jdbcTemplate.queryForList("select config_key, config_value from config_params");*/
		List<Map<String, Object>> configs = jdbcTemplate.queryForList("SELECT CONFIG_KEY, CONFIG_VALUE FROM SERVICE_CONFIG_PARAMS_EBA WHERE CODE = " + serviceShortCode);

		/*Logger logger = LogManager.getRootLogger();
		logger.info("Loading properties from Database");*/

		for (Map<String, Object> config : configs) {
			/*setProperty((config.get("config_key")).toString().trim(), ((config.get("config_value") == null) ? "" : (config.get("config_value")).toString().trim()));*/
			setProperty((config.get("CONFIG_KEY")).toString().trim(), ((config.get("CONFIG_VALUE") == null) ? "" : (config.get("CONFIG_VALUE")).toString().trim()));
		}
	}

}
