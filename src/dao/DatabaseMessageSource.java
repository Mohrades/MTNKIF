package dao;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@SuppressWarnings("unused")
public class DatabaseMessageSource {
	
	private DAO dao;

	public DatabaseMessageSource(DAO dao) {
		setDao(dao);
	}

	private DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	/**
	 *
	Spring prend en charge deux ensembles d'annotations pouvant être utilisés pour implémenter la mise en cache.
	Vous avez les annotations Spring originales et les annotations JSR-107.

	JSR-107 (JCache) Annotations
	Si vous êtes familier avec Spring, vous savez qu'il fournit des annotations pour vous aider à développer des applications.
	En ce qui concerne la mise en cache, Spring prend en charge deux ensembles d'annotations pouvant être utilisés pour implémenter la mise en cache.
	Vous avez les annotations Spring originales et les nouvelles annotations JSR-107. Les annotations Spring d'origine peuvent être utilisées avec les versions 3.1 et ultérieures de Spring, tandis que les annotations JSR-107 ne sont disponibles qu'au Spring 4.1+.
	Dans cet exemple, nous allons utiliser les annotations JSR-107. Ci-dessous, j'ai répertorié les annotations JSR-107 (JCache) les plus couramment utilisées, accompagnées de brèves descriptions et de liens vers leurs API.

	@CacheDefaults - permet la configuration des valeurs par défaut au niveau de la classe. Par exemple, vous pouvez définir un nom de cache au niveau de la classe et cela sera utilisé par défaut.

	@CacheResult - Cache la valeur de retour de la méthode. La première fois que la méthode est appelée avec une clé particulière, elle sera exécutée et la valeur sera mise en cache. Lors d'appels ultérieurs avec la même clé si la valeur est toujours mise en cache, elle sera extraite du cache au lieu d'exécuter la méthode.

	@CachePut - Met en cache la valeur spécifiée comme @CacheValue. Ceci est similaire à @CacheResult mais la différence est qu'il mettra en cache @CacheValue chaque fois que la méthode est appelée.

	@CacheRemove - supprime les entrées du cache spécifié correspondant à la clé fournie / générée

	@CacheRemoveAll - supprime tous les éléments du cache spécifié

	 */

	/**
	 *
	method name is not included in cache key to work with @TriggersRemove
	 */

   /*@Cacheable(value = "messageCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = @Property( name="includeMethod", value="false" )))*/
   @Cacheable(value = "messageCache", key = "#serviceShortCode + '_' + #code + '_' + #language + '_' + #country")
   /*@CacheResult(cacheName = "messageCache")*/
   public String findByCode(int serviceShortCode, String code, Locale locale, String language, String country, boolean fallbackToSystemLocale) {
	   /**
	   	 *
	   	This table would have three columns, key, locale, and value, with the key and locale being the primary keys of the table. The Sql to retrieve the required value would be something as follows:

	   		"SELECT value FROM i18nstrings WHERE key = … AND locale = …"
	   	*/

   		// That way the statement returns up to three texts, the first one being the most specific, the second one being language-specific and the last one for the default-locale.
	   	/**
	   	 *
			Writting a database-based MessageSource isn't as hard as it looks.
			First we modelled Language, Country and Locale in the database. These tables are more or less static, so we filled them with a script.
			The table containing all the messages is called Message. It consists of the attributes msg_key, msg_locale_id and msg_text.
			In order to find a localized text, the sql looks something like this:
	   		select msg_text from message
	   		join locale loc on loc.loc_id = message.msg_locale_id
	   		join language lan on lan.lan_id = loc.loc_language_id and lan.lan_shorthand = :lan_shorthand
	   		join country con on con.con_id = loc.loc_country_id and con.con_shorthand = :con_shorthand
	   		union all
	   		select msg_text from message
	   		join locale loc on loc.loc_id = message.msg_locale_id and loc.loc_country_id is null
	   		join language lan on lan.lan_id = loc.loc_language_id and lan.lan_shorthand = :lan_shorthand
	   		union all
	   		select msg_text from message
	   		join locale loc on loc.loc_id = message.msg_locale_id and loc.loc_language_id is null andloc.loc_country_id is null

	   		That way the statement returns up to three texts, the first one being the most specific, the second one being language-specific and the last one for the default-locale.

	   	*/

   		// String query = ((locale.getCountry() == null) || locale.getCountry().isEmpty()) ? ("select msg_text from message join locale loc on ((loc.loc_id = message.msg_locale_id) and (loc.loc_country_id is null)) join language lan on ((lan.lan_id = loc.loc_language_id) and (lan.lan_shorthand = '" + locale.getLanguage() + "')) where ((message.service_code = " + serviceShortCode + ") and (message.msg_key = '" + code.replace("'", "''") + "'))") : ("select msg_text from message join locale loc on (loc.loc_id = message.msg_locale_id) join language lan on ((lan.lan_id = loc.loc_language_id) and (lan.lan_shorthand = '" + locale.getLanguage() + "')) join country con on ((con.con_id = loc.loc_country_id) and (con.con_shorthand = '" + locale.getCountry() + "')) where ((message.service_code = " + serviceShortCode + ") and (message.msg_key = '" + code.replace("'", "''") + "'))" + " union all select msg_text from message join locale loc on ((loc.loc_id = message.msg_locale_id) and (loc.loc_country_id is null)) join language lan on ((lan.lan_id = loc.loc_language_id) and (lan.lan_shorthand = '" + locale.getLanguage() + "')) where ((message.service_code = " + serviceShortCode + ") and (message.msg_key = '" + code.replace("'", "''") + "'))");
		String query = ((locale.getCountry() == null) || locale.getCountry().isEmpty()) ? ("SELECT MSG_TEXT FROM SERVICE_RESOURCE_MESSAGE_EBA JOIN SERVICE_RESOURCE_BUNDLE_EBA LOC ON ((LOC.ID = SERVICE_RESOURCE_MESSAGE_EBA.BUNDLE) AND (LOC.COUNTRY IS NULL)) JOIN SERVICE_LANGUAGE_EBA LAN ON ((LAN.ID = LOC.LANGUAGE) AND (LAN.SHORTHAND = '" + locale.getLanguage() + "')) WHERE ((SERVICE_RESOURCE_MESSAGE_EBA.SERVICE_CODE = " + serviceShortCode + ") AND (SERVICE_RESOURCE_MESSAGE_EBA.MSG_KEY = '" + code.replace("'", "''") + "'))") : ("SELECT MSG_TEXT FROM SERVICE_RESOURCE_MESSAGE_EBA JOIN SERVICE_RESOURCE_BUNDLE_EBA LOC ON (LOC.ID = SERVICE_RESOURCE_MESSAGE_EBA.BUNDLE) JOIN SERVICE_LANGUAGE_EBA LAN ON ((LAN.ID = LOC.LANGUAGE) AND (LAN.SHORTHAND = '" + locale.getLanguage() + "')) JOIN SERVICE_COUNTRY_EBA CON ON ((CON.ID = LOC.COUNTRY) AND (CON.SHORTHAND = '" + locale.getCountry() + "')) WHERE ((SERVICE_RESOURCE_MESSAGE_EBA.SERVICE_CODE = " + serviceShortCode + ") AND (SERVICE_RESOURCE_MESSAGE_EBA.MSG_KEY = '" + code.replace("'", "''") + "'))" + " UNION ALL SELECT MSG_TEXT FROM SERVICE_RESOURCE_MESSAGE_EBA JOIN SERVICE_RESOURCE_BUNDLE_EBA LOC ON ((LOC.ID = SERVICE_RESOURCE_MESSAGE_EBA.BUNDLE) AND (LOC.COUNTRY IS NULL)) JOIN SERVICE_LANGUAGE_EBA LAN ON ((LAN.ID = LOC.LANGUAGE) AND (LAN.SHORTHAND = '" + locale.getLanguage() + "')) WHERE ((SERVICE_RESOURCE_MESSAGE_EBA.SERVICE_CODE = " + serviceShortCode + ") AND (SERVICE_RESOURCE_MESSAGE_EBA.MSG_KEY = '" + code.replace("'", "''") + "'))");

	   	if(locale.equals(Locale.getDefault()) || (!fallbackToSystemLocale)) {

	   	}
	   	// check fallbackToSystemLocale status : 
	   	// If true, msg not found for locale => then check for the system locale, => and at last for the default-locale.
	   	else {
	   		// query += ((Locale.getDefault().getCountry() == null) || Locale.getDefault().getCountry().isEmpty()) ? (" union all select msg_text from message join locale loc on ((loc.loc_id = message.msg_locale_id) and (loc.loc_country_id is null)) join language lan on ((lan.lan_id = loc.loc_language_id) and (lan.lan_shorthand = '" + Locale.getDefault().getLanguage() + "')) where ((message.service_code = " + serviceShortCode + ") and (message.msg_key = '" + code.replace("'", "''") + "'))") : (" union all select msg_text from message join locale loc on (loc.loc_id = message.msg_locale_id) join language lan on ((lan.lan_id = loc.loc_language_id) and (lan.lan_shorthand = '" + Locale.getDefault().getLanguage() + "')) join country con on ((con.con_id = loc.loc_country_id) and (con.con_shorthand = '" + Locale.getDefault().getCountry() + "')) where ((message.service_code = " + serviceShortCode + ") and (message.msg_key = '" + code.replace("'", "''") + "'))" + " union all select msg_text from message join locale loc on ((loc.loc_id = message.msg_locale_id) and (loc.loc_country_id is null)) join language lan on ((lan.lan_id = loc.loc_language_id) and (lan.lan_shorthand = '" + Locale.getDefault().getLanguage() + "')) where ((message.service_code = " + serviceShortCode + ") and (message.msg_key = '" + code.replace("'", "''") + "'))");
	   		query += ((Locale.getDefault().getCountry() == null) || Locale.getDefault().getCountry().isEmpty()) ? (" UNION ALL SELECT MSG_TEXT FROM SERVICE_RESOURCE_MESSAGE_EBA JOIN SERVICE_RESOURCE_BUNDLE_EBA LOC ON ((LOC.ID = SERVICE_RESOURCE_MESSAGE_EBA.BUNDLE) AND (LOC.COUNTRY IS NULL)) JOIN SERVICE_LANGUAGE_EBA LAN ON ((LAN.ID = LOC.LANGUAGE) AND (LAN.SHORTHAND = '" + Locale.getDefault().getLanguage() + "')) WHERE ((SERVICE_RESOURCE_MESSAGE_EBA.SERVICE_CODE = " + serviceShortCode + ") AND (SERVICE_RESOURCE_MESSAGE_EBA.MSG_KEY = '" + code.replace("'", "''") + "'))") : (" UNION ALL SELECT MSG_TEXT FROM SERVICE_RESOURCE_MESSAGE_EBA JOIN SERVICE_RESOURCE_BUNDLE_EBA LOC ON (LOC.ID = SERVICE_RESOURCE_MESSAGE_EBA.BUNDLE) JOIN SERVICE_LANGUAGE_EBA LAN ON ((LAN.ID = LOC.LANGUAGE) AND (LAN.SHORTHAND = '" + Locale.getDefault().getLanguage() + "')) JOIN SERVICE_COUNTRY_EBA CON ON ((CON.ID = LOC.COUNTRY) AND (CON.SHORTHAND = '" + Locale.getDefault().getCountry() + "')) WHERE ((SERVICE_RESOURCE_MESSAGE_EBA.SERVICE_CODE = " + serviceShortCode + ") AND (SERVICE_RESOURCE_MESSAGE_EBA.MSG_KEY = '" + code.replace("'", "''") + "'))" + " UNION ALL SELECT MSG_TEXT FROM SERVICE_RESOURCE_MESSAGE_EBA JOIN SERVICE_RESOURCE_BUNDLE_EBA LOC ON ((LOC.ID = SERVICE_RESOURCE_MESSAGE_EBA.BUNDLE) AND (LOC.COUNTRY IS NULL)) JOIN SERVICE_LANGUAGE_EBA LAN ON ((LAN.ID = LOC.LANGUAGE) AND (LAN.SHORTHAND = '" + Locale.getDefault().getLanguage() + "')) WHERE ((SERVICE_RESOURCE_MESSAGE_EBA.SERVICE_CODE = " + serviceShortCode + ") AND (SERVICE_RESOURCE_MESSAGE_EBA.MSG_KEY = '" + code.replace("'", "''") + "'))");
	   	}

	   	// query += " union all select msg_text from message join locale loc on ((loc.loc_id = message.msg_locale_id) and (loc.loc_language_id is null) and (loc.loc_country_id is null)) where ((message.service_code = " + serviceShortCode + ") and (message.msg_key = '" + code.replace("'", "''") + "'))";
	   	query += " UNION ALL SELECT MSG_TEXT FROM SERVICE_RESOURCE_MESSAGE_EBA JOIN SERVICE_RESOURCE_BUNDLE_EBA LOC ON ((LOC.ID = SERVICE_RESOURCE_MESSAGE_EBA.BUNDLE) AND (LOC.LANGUAGE IS NULL) AND (LOC.COUNTRY IS NULL)) WHERE ((SERVICE_RESOURCE_MESSAGE_EBA.SERVICE_CODE = " + serviceShortCode + ") AND (SERVICE_RESOURCE_MESSAGE_EBA.MSG_KEY = '" + code.replace("'", "''") + "'))";

	   	String textForCurrentLanguage = null; // String msg_text = null;

	   	List<Map<String, Object>> configs = getDao().getJdbcTemplate().queryForList(query);
		for (Map<String, Object> config : configs) {
			// That way the statement returns up to three texts, the first one being the most specific, the second one being language-specific and the last one for the default-locale.
			// consider the first text not null : it is the most suitable.
			if(textForCurrentLanguage != null) {
				break;
			}

			/*textForCurrentLanguage = (config.get("msg_text") == null) ? null : (config.get("msg_text")).toString().trim();*/
			textForCurrentLanguage = (config.get("MSG_TEXT") == null) ? null : (config.get("MSG_TEXT")).toString().trim();
		}

		return (textForCurrentLanguage == null) ? "" : textForCurrentLanguage;
   }

	/**
	 *
	update applications messages from database

	*/
   /*@TriggersRemove(cacheName = "messageCache", keyGenerator = @KeyGenerator (name = "HashCodeCacheKeyGenerator", properties = @Property( name="includeMethod", value="false" )))*/
   /*private void reloadDatabaseMessages(@PartialCacheKey Integer id) {*/
   /*@CacheEvict(value = "messageCache", key = "CACHE_SUMMARY_FIELDS + #accountId + '_' + #formType")*/
   @CacheRemoveAll(cacheName = "messageCache")
   public void clearCache() {
   	
   }

}
