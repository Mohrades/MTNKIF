package com.util;

import java.io.Serializable;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * nous d�finirons notre classe de processeur de propri�t� personnalis�e qui �tendra le PropertyPlaceholderConfigurer de Spring et chargera les propri�t�s de la base de donn�es
 */
public class PropertiesUtils extends PropertyPlaceholderConfigurer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3486410984537696454L;

	private static CustomProperties customProps;

	private String dataSourceName;

	private int serviceShortCode;

	public PropertiesUtils() {
		
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public int getServiceShortCode() {
		return serviceShortCode;
	}

	public void setServiceShortCode(int serviceShortCode) {
		this.serviceShortCode = serviceShortCode;
	}

	@Override
	// nous d�finirons notre classe de processeur de propri�t� personnalis�e qui �tendra le PropertyPlaceholderConfigurer de Spring et chargera les propri�t�s de la base de donn�es
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		DataSource dataSource = (DataSource) beanFactory.getBean(getDataSourceName());
		customProps = new CustomProperties(dataSource, getServiceShortCode());
		setProperties(customProps);
		super.postProcessBeanFactory(beanFactory);
	}

	// You can access these properties directly using @Value annotation or you can call utility method provided along with this class
	public static String getProperty(String name) {
		return (null == customProps.get(name)) ? "" : customProps.get(name).toString().trim();
	}

}
