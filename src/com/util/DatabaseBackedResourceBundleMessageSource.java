package com.util;

import java.text.MessageFormat;
import java.util.Locale;
import org.springframework.context.support.ResourceBundleMessageSource;

import dao.DatabaseMessageSource;

public class DatabaseBackedResourceBundleMessageSource extends ResourceBundleMessageSource {

	private DatabaseMessageSource databaseMessageSource;

	private int serviceShortCode;

	private boolean fallbackToSystemLocale;

	public DatabaseBackedResourceBundleMessageSource(DatabaseMessageSource databaseMessageSource) {
		setDatabaseMessageSource(databaseMessageSource);
		setFallbackToSystemLocale(true);
	}

	private DatabaseMessageSource getDatabaseMessageSource() {
		return databaseMessageSource;
	}

	public void setDatabaseMessageSource(DatabaseMessageSource databaseMessageSource) {
		this.databaseMessageSource = databaseMessageSource;
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

		// String msgForCurrentLanguage = getDatabaseMessageSource().findByCode(getServiceShortCode(), code, ((locale == null) ? (isFallbackToSystemLocale() ? Locale.getDefault() : null) : locale), isFallbackToSystemLocale());
		String msgForCurrentLanguage = getDatabaseMessageSource().findByCode(getServiceShortCode(), code, locale, locale.getLanguage(), locale.getCountry(), isFallbackToSystemLocale());

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
    	// String msgForCurrentLanguage = getDatabaseMessageSource().findByCode(getServiceShortCode(), code, ((locale == null) ? (isFallbackToSystemLocale() ? Locale.getDefault() : null) : locale), isFallbackToSystemLocale());
    	String msgForCurrentLanguage = getDatabaseMessageSource().findByCode(getServiceShortCode(), code, locale, locale.getLanguage(), locale.getCountry(), isFallbackToSystemLocale());

        if ((msgForCurrentLanguage != null) && (!msgForCurrentLanguage.isEmpty())) ;
        else {
        	msgForCurrentLanguage = super.resolveCodeWithoutArguments(code, locale);
        }

        return msgForCurrentLanguage;
    }
}
