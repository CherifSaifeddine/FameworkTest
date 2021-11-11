package setup;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;

import utils.Trace;

/**
 * Classe dédiée à la gestion du proxy des browsers
 *
 */
public class AFProxy
{

	/**
	 * Objet qui sera utilisé par Selenium par les WebDrivers
	 */
	private Proxy seleniumProxyObject = null;

	// Paramétrages
	protected static final String BROWSER_PROXY_TYPE = "browser.proxy.type";

	protected static final String BROWSER_PROXY_URL_VALUE = "browser.proxy.url.value";

	protected static final String BROWSER_PROXY_MANUAL_HTTP = "browser.proxy.manual.http";

	protected static final String BROWSER_PROXY_MANUAL_SSL = "browser.proxy.manual.ssl";

	protected static final String BROWSER_PROXY_MANUAL_SOCKS = "browser.proxy.manual.socks";

	protected static final String BROWSER_PROXY_MANUAL_SOCKS_VERSION = "browser.proxy.manual.socks.version";

	protected static final String BROWSER_PROXY_MANUAL_SOCKS_USER = "browser.proxy.manual.socks.user";

	protected static final String BROWSER_PROXY_MANUAL_SOCKS_PASS = "browser.proxy.manual.socks.pass";


	/**
	 * Constructeur qui initialise l'objet de Proxy en fonction de la configuration
	 * 
	 * @param config
	 */
	public AFProxy(AFConfig config)
	{
		String proxyTypeFromConf = config.getKey(BROWSER_PROXY_TYPE);
		if (proxyTypeFromConf != null)
		{
			proxyTypeFromConf = proxyTypeFromConf.toUpperCase();
			if ("URL".equals(proxyTypeFromConf))
				proxyTypeFromConf = "PAC"; // URL est plus parlant que PAC, on garde PAC en interne
			else if ("NOPROXY".equals(proxyTypeFromConf))
				proxyTypeFromConf = "DIRECT"; // même chose

			ProxyType proxyTypeEnumValue = null;
			try
			{
				proxyTypeEnumValue = ProxyType.valueOf(proxyTypeFromConf);
			}
			catch (IllegalArgumentException e)
			{
				Trace.error("AFProxy : Valeur invalide pour la clé " + BROWSER_PROXY_TYPE + " (" + proxyTypeFromConf + ")");
			}

			if (proxyTypeEnumValue != null)
			{
				this.seleniumProxyObject = new Proxy();
				this.seleniumProxyObject.setProxyType(proxyTypeEnumValue);

				switch (proxyTypeEnumValue)
				{
					// AUTO CONFIGURATION VIA URL
					case PAC:
						setPacProxy(config);
						break;

					// MANUEL HTTP/SSL/SOCKS
					case MANUAL:
						setHttpProxy(config);
						setSslProxy(config);
						setSocksProxy(config);
						break;

					default:
					{
						// AUTODETECT/DIRECT/SYSTEM/UNSPECIFIED
					}
				}
			}
		}

		if (this.seleniumProxyObject != null)
		{
			Trace.info("AFProxy : Proxy configuré en mode : " + this.seleniumProxyObject.getProxyType());
		}
		else
		{
			Trace.info("AFProxy : Aucune configuration de proxy trouvée.");
		}
	}


	/**
	 * Réglage ou non de l'URL d'autoconfiguration du proxy selon la configuration
	 * 
	 * @param config
	 */
	private void setPacProxy(AFConfig config)
	{
		String pacURL = config.getKey(BROWSER_PROXY_URL_VALUE);
		if (pacURL == null)
		{
			Trace.error(
					"AFProxy : Le type du proxy a été fixé sur URL mais l'URL n'est pas renseignée via la clé : " + BROWSER_PROXY_URL_VALUE);
		}
		else
		{
			this.seleniumProxyObject.setProxyAutoconfigUrl(pacURL);
		}
	}


	/**
	 * Réglage ou non du proxy SOCKS selon la configuration
	 * 
	 * @param config
	 */
	private void setSocksProxy(AFConfig config)
	{
		String proxySocks = config.getKey(BROWSER_PROXY_MANUAL_SOCKS);
		if (proxySocks == null)
		{
			Trace.warn("AFProxy : Le type du proxy a été fixé sur MANUAL mais il n'y a pas de proxy SOCKS.");
			return;
		}

		this.seleniumProxyObject.setSocksProxy(proxySocks);

		String proxySocksVersion = config.getKey(BROWSER_PROXY_MANUAL_SOCKS_VERSION);
		if (proxySocksVersion == null)
		{
			Trace.warn("AFProxy : Aucune version indiquée pour le proxy SOCKS.");
			return;
		}

		if ("4".equals(proxySocksVersion) || "5".equals(proxySocksVersion))
		{
			this.seleniumProxyObject.setSocksVersion(Integer.valueOf(proxySocksVersion));

			String proxySocksUser = config.getKey(BROWSER_PROXY_MANUAL_SOCKS_USER);
			if (proxySocksUser != null)
			{
				if ("4".equals(proxySocksVersion))
				{
					Trace.error("AFProxy : Le proxy SOCKS est configuré en version 4, le nom d'utilisateur sera ignoré.");
				}
				else
				{
					this.seleniumProxyObject.setSocksUsername(proxySocksUser);
				}
			}

			String proxySocksPass = config.getKey(BROWSER_PROXY_MANUAL_SOCKS_PASS);
			if (proxySocksPass != null)
			{
				if ("4".equals(proxySocksVersion))
				{
					Trace.error("AFProxy : Le proxy SOCKS est configuré en version 4, le mot de passe sera ignoré.");
				}
				else
				{
					this.seleniumProxyObject.setSocksPassword(proxySocksPass);
				}
			}
		}
		else
		{
			Trace.error("AFProxy : Version incorrecte de proxy SOCKS (4 ou 5) : " + proxySocksVersion);
		}
	}


	/**
	 * Réglage ou non du proxy SSL selon la configuration
	 * 
	 * @param config
	 */
	private void setSslProxy(AFConfig config)
	{
		String proxySSL = config.getKey(BROWSER_PROXY_MANUAL_SSL);
		if (proxySSL == null)
		{
			Trace.warn("AFProxy : Le type du proxy a été fixé sur MANUAL mais il n'y a pas de proxy SSL.");
		}
		else
		{
			this.seleniumProxyObject.setSslProxy(proxySSL);
		}
	}


	/**
	 * Réglage ou non du proxy HTTP selon la configuration
	 * 
	 * @param config
	 */
	private void setHttpProxy(AFConfig config)
	{
		String proxyHttp = config.getKey(BROWSER_PROXY_MANUAL_HTTP);
		if (proxyHttp == null)
		{
			Trace.warn("AFProxy : Le type du proxy a été fixé sur MANUAL mais il n'y a pas de proxy HTTP.");
		}
		else
		{
			this.seleniumProxyObject.setHttpProxy(proxyHttp);
		}
	}


	public Proxy getSeleniumProxyObject()
	{
		return seleniumProxyObject;
	}
}
