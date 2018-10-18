package com.util;

import java.text.MessageFormat;
import java.util.Locale;
import org.springframework.context.support.ResourceBundleMessageSource;

import dao.queries.JdbcResourceBundleMessageSourceDao;

public class DatabaseBackedResourceBundleMessageSource extends ResourceBundleMessageSource {

	private JdbcResourceBundleMessageSourceDao jdbcResourceBundleMessageSourceDao;

	private int serviceShortCode;

	private boolean fallbackToSystemLocale;

	public DatabaseBackedResourceBundleMessageSource(JdbcResourceBundleMessageSourceDao jdbcResourceBundleMessageSourceDao) {
		setJdbcResourceBundleMessageSourceDao(jdbcResourceBundleMessageSourceDao);
		setFallbackToSystemLocale(true);
	}

	private JdbcResourceBundleMessageSourceDao getJdbcResourceBundleMessageSourceDao() {
		return jdbcResourceBundleMessageSourceDao;
	}

	public void setJdbcResourceBundleMessageSourceDao(JdbcResourceBundleMessageSourceDao jdbcResourceBundleMessageSourceDao) {
		this.jdbcResourceBundleMessageSourceDao = jdbcResourceBundleMessageSourceDao;
	}

	public int getServiceShortCode() {
		return serviceShortCode;
	}

	public void setServiceShortCode(int serviceShortCode) {
		this.serviceShortCode = serviceShortCode;
	}

	protected boolean isFallbackToSystemLocale() {
		return fallbackToSystemLocale;
	}

	public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
		super.setFallbackToSystemLocale(fallbackToSystemLocale);
		this.fallbackToSystemLocale = fallbackToSystemLocale;
	}

	@Override
    protected MessageFormat resolveCode(String code, Locale locale) {
		MessageFormat format;

		// String msgForCurrentLanguage = getJdbcResourceBundleMessageSourceDao().findByCode(getServiceShortCode(), code, ((locale == null) ? (isFallbackToSystemLocale() ? Locale.getDefault() : null) : locale), isFallbackToSystemLocale());
		String msgForCurrentLanguage = getJdbcResourceBundleMessageSourceDao().findByCode(getServiceShortCode(), code, locale, locale.getLanguage(), locale.getCountry(), isFallbackToSystemLocale());

		if((msgForCurrentLanguage != null) && (!msgForCurrentLanguage.isEmpty())) {
			// format = createMessageFormat(msgForCurrentLanguage, locale);
			format = new MessageFormat(msgForCurrentLanguage, locale);
		}
		else {
			format = super.resolveCode(code, locale);
        }

        return format;
	}

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
    	// String msgForCurrentLanguage = getJdbcResourceBundleMessageSourceDao().findByCode(getServiceShortCode(), code, ((locale == null) ? (isFallbackToSystemLocale() ? Locale.getDefault() : null) : locale), isFallbackToSystemLocale());
    	String msgForCurrentLanguage = getJdbcResourceBundleMessageSourceDao().findByCode(getServiceShortCode(), code, locale, locale.getLanguage(), locale.getCountry(), isFallbackToSystemLocale());

        if ((msgForCurrentLanguage != null) && (!msgForCurrentLanguage.isEmpty())) ;
        else {
        	msgForCurrentLanguage = super.resolveCodeWithoutArguments(code, locale);
        }

        return msgForCurrentLanguage;
    }
}
