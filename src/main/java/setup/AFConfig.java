package setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;

import utils.AFFileUtils;
import utils.Trace;

/**
 * Configuration du framework
 *
 */
public class AFConfig
{
	/**
	 * Les propriétés du fichier properties
	 */
	private Properties propertiesFileConf;

	/**
	 * Clé indiquant si on fonctionne en mode headless ou pas
	 */
	private static final String KEY_HEADLESS = "headless";

	public boolean modeHeadless;

	/**
	 * Taille de l'écran en mode headless
	 */
	private static final String KEY_HEADLESS_WIDTH = "headless.width";

	public int headlessWidth = 1920;

	/**
	 * Taille de l'écran en mode headless
	 */
	private static final String KEY_HEADLESS_HEIGHT = "headless.height";

	public int headlessHeight = 1080;

	/**
	 * URL de l'environnement
	 */
	private static final String KEY_URL = "url";

	public String envUrl = "";

	/**
	 * URL de déconnexion
	 */
	private static final String KEY_LOGOUT_URL = "logouturl";

	public String envLogoutUrl = "";

	/**
	 * Navigateur
	 */
	private static final String KEY_BROWSER = "browser";

	public String browser = "";

	/**
	 * Langue
	 */
	private static final String KEY_LANG = "lang";

	public String lang = "";

	/**
	 * Path complet vers le driver chrome (ex: D://SeleniumDrivers//chromedriver.exe )
	 */
	protected static final String KEY_WEBDRIVER_CHROME_PATH = "webdriver.chrome.driver";

	public String driverChrome = "";

	/**
	 * Path complet vers le driver IE
	 */
	protected static final String KEY_WEBDRIVER_IE_PATH = "webdriver.ie.driver";

	public String driverIe = "";

	/**
	 * Path complet vers le driver FF
	 */
	protected static final String KEY_WEBDRIVER_FIREFOX_PATH = "webdriver.firefox.driver";

	public String driverGecko = "";

	/**
	 * Path complet vers l'exe FF (ex: C://Program Files//Mozilla Firefox//firefox.exe )
	 */
	protected static final String KEY_EXEC_FIREFOX_PATH = "webdriver.firefox.bin";

	@SuppressWarnings("unused")
	private String binFirefox = "";

	/**
	 * Path complet vers le driver EDGE chromium
	 */
	private static final String KEY_WEBDRIVER_EDGECH_PATH = "webdriver.edgechromium.driver";

	public String driverEdgeCH = "";

	/**
	 * Path complet vers l'exe EDGE chromium (ex: C://Program Files (x86)//Microsoft//Edge Beta//Application//msedge.exe )
	 */
	private static final String KEY_EXEC_EDGECH_PATH = "webdriver.edgechromium.bin";

	public String binEdgeCH = "";

	/**
	 * Activation de la bordure rouge autour des éléments
	 */
	private static final String KEY_ENABLE_HIGHLIGHTING = "highlighting.enabled";

	public boolean highlighting;

	/**
	 * Temps d'attente
	 */
	private static final String KEY_SYNC_WAIT = "sync.wait.timesec";

	public int syncWaitTime = 30;

	/**
	 * Timeout page
	 */
	private static final String KEY_SYNC_TIMEOUT = "sync.page.timeout";

	public int syncPageTimeout = 60;

	/**
	 * Nullsync : nb de boucles
	 */
	private static final String KEY_NULLSYNC_RETRIES = "nullsync.retries";

	public int nullSyncRetries = 1;

	/**
	 * Nullsync : temps d'attente après chaque itération
	 */
	private static final String KEY_NULLSYNC_WAIT = "nullsync.wait.msec";

	public int nullSyncTime = 200;

	/**
	 * URL du RemoteWebDriver pour lancement via Docker
	 */
	private static final String KEY_REMOTEWEBDRIVER_URL = "remotewebdriverurl";

	public URL remoteWebDriverUrl = null;

	/**
	 * Configuration du proxy des browsers
	 */
	public AFProxy afProxy = null;

	/**
	 * Faut-il continuer les scénarios suivants en cas d'erreur dans la feature courante ?
	 */
	private static final String KEY_SCENARIO_ONERROR_CONTINUE = "scenario.onerror.continue";

	public boolean continueScenarioOnError;

	/**
	 * Faut-il kill le webdriver courant avant de lancer les tests ?
	 */
	private static final String KEY_WEBDRIVER_ONBOOT_KILL = "webdriver.onboot.kill";

	public boolean killCurrentWebDriverOnBoot = true;

	/**
	 * Constructeur vide uniquement utilisé pour les tests unitaires
	 */
	protected AFConfig()
	{

	}


	/**
	 * Constructeur et initialisation de la configuration
	 * 
	 * @param sPropFileName chemin et nom du properties (ex: ./src/test/resources/conf/a.properties)
	 */
	public AFConfig(String sPropFileName)
	{
		Trace.info("Instanciation " + this.getClass().getSimpleName());
		try
		{
			loadProperties(sPropFileName);
			setConfiguration();
			afProxy = new AFProxy(this);
		}
		catch (IOException e)
		{
			Trace.error("Erreur durant la lecture de la configuration : " + e.getMessage());
			Assert.fail();
		}

	}


	public Properties getPropertiesFileConf()
	{
		return (Properties) propertiesFileConf.clone();
	}


	/**
	 * Lecture et chargement du properties
	 * 
	 * @param sPropFileName
	 * @throws IOException
	 */
	private void loadProperties(String sPropFileName) throws IOException
	{
		this.propertiesFileConf = new Properties();

		File file = new File(sPropFileName);
		if (file.exists() && file.isFile())
		{
			Trace.info("Lecture du fichier de configuration : " + file.getAbsolutePath());
			try (Reader targetReader = new FileReader(file))
			{
				propertiesFileConf.load(AFConfig.handleDataForParsing(targetReader));
			}

		}
		else
		{
			throw new FileNotFoundException("Fichier de configuration manquant : " + sPropFileName);
		}
	}


	/**
	 * Lit le reader passé en paramètre, effectue des traitements sur les données..<br>
	 * ..puis reconstruit un reader pour qu'il soit chargé par {@link Properties}
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static Reader handleDataForParsing(Reader reader) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		try (BufferedReader lr = new BufferedReader(reader))
		{
			String cur = lr.readLine();
			while (cur != null)
			{
				sb.append(AFConfig.parseLineToReplaceByEnvVar(cur));
				sb.append(System.lineSeparator());
				cur = lr.readLine();
			}
		}

		StringReader ret = new StringReader(sb.toString());

		return ret;
	}


	/**
	 * Lit une chaine pour la remplacer en partie par une variable d'environnement<br>
	 * Exemple : str-%{toto} ira chercher la variable d'env 'toto' et fera le remplacement si elle existe
	 * 
	 * @param line
	 * @return
	 */
	protected static String parseLineToReplaceByEnvVar(String line)
	{
		boolean replace = true;

		while (replace)
		{
			replace = false;
			int start = line.indexOf("%{");
			int end = line.indexOf("}");
			if (start > -1 && end > -1)
			{
				// variable name identification
				String variableName = line.substring(start + 2, end);
				String value = System.getenv(variableName);
				// replace only if env variable has a relevant value.
				if (value != null && value.length() > 1)
				{
					// there is - at least - a variable to replace
					line = line.replaceAll("\\%\\{" + variableName + "\\}", value);

					// continue only if there is a replacement.
					replace = true;
				}
			}
		}
		return line;
	}


	/**
	 * Chargement des données de configuration<br>
	 * On va lire les propriétés système et ensuite le properties local pour remplir les champs de cette classe
	 * 
	 * @throws MalformedURLException
	 */
	private void setConfiguration() throws MalformedURLException
	{
		Trace.info("Configuration utilisée pour cette exécution :");

		String remoteWDurl = getStringProperty(KEY_REMOTEWEBDRIVER_URL, null, "URL RemoteWebDriver (mode docker)");
		if (remoteWDurl != null && !remoteWDurl.isEmpty())
			remoteWebDriverUrl = new URL(remoteWDurl);

		if (remoteWebDriverUrl != null)
		{
			modeHeadless = true;
			Trace.info("> Lancement RemoteWebDriver/Docker donc mode headless forcé");
		}
		else
		{
			modeHeadless = Boolean.valueOf(getStringProperty(KEY_HEADLESS, "false", "Lancement via Jenkins (mode headless)"));
		}
		headlessWidth = getIntegerProperty(KEY_HEADLESS_WIDTH, headlessWidth, "Longueur de l'écran (mode headless)");
		headlessHeight = getIntegerProperty(KEY_HEADLESS_HEIGHT, headlessHeight, "Hauteur de l'écran (mode headless)");

		highlighting = Boolean.valueOf(getStringProperty(KEY_ENABLE_HIGHLIGHTING, "false", "Encadrement des éléments cliqués (hors headless)"));

		envUrl = getStringProperty(KEY_URL, "", "Url de la page de login");

		envLogoutUrl = getStringProperty(KEY_LOGOUT_URL, "", "Url de la page de logout");
		if (envLogoutUrl != null && envLogoutUrl.contains("${url}"))
		{
			envLogoutUrl = envLogoutUrl.replace("${url}", envUrl);
			Trace.info("> Réelle url de la page de logout   = " + envLogoutUrl);
		}

		browser = getStringProperty(KEY_BROWSER, "", "Browser");
		lang = getStringProperty(KEY_LANG, "FR", "Langue");

		if (remoteWebDriverUrl != null)
		{
			Trace.info("> Lancement RemoteWebDriver/Docker donc les paths de drivers ne seront pas chargés via la configuration");
		}
		else
		{
			driverChrome = getStringProperty(KEY_WEBDRIVER_CHROME_PATH, "", "Path utilisé pour le driver Chrome");
			driverIe = getStringProperty(KEY_WEBDRIVER_IE_PATH, "", "Path utilisé pour le driver IE");
			driverGecko = getStringProperty(KEY_WEBDRIVER_FIREFOX_PATH, "", "Path utilisé pour le driver FF");
			binFirefox = getStringProperty(KEY_EXEC_FIREFOX_PATH, "", "Path utilisé pour le bin FF");
			driverEdgeCH = getStringProperty(KEY_WEBDRIVER_EDGECH_PATH, "", "Path utilisé pour le driver EDGE (chromium)");
			binEdgeCH = getStringProperty(KEY_EXEC_EDGECH_PATH, "", "Path utilisé pour le bin EDGE (chromium)");
		}

		syncWaitTime = getIntegerProperty(KEY_SYNC_WAIT, syncWaitTime, "Temps de synchronisation");
		syncPageTimeout = getIntegerProperty(KEY_SYNC_TIMEOUT, syncPageTimeout, "Temps de timeout de la page");
		nullSyncRetries = getIntegerProperty(KEY_NULLSYNC_RETRIES, nullSyncRetries, "Nombre d'essai de getWebElement si retour null");
		nullSyncTime = getIntegerProperty(KEY_NULLSYNC_WAIT, nullSyncTime, "Temps d'attente entre chaque essai de getWebElement si retour null");

		continueScenarioOnError = Boolean.valueOf(getStringProperty(KEY_SCENARIO_ONERROR_CONTINUE,
				"true",
				"Continuer les scénarios suivants en cas d'erreur dans la feature courante"));

		killCurrentWebDriverOnBoot = Boolean.valueOf(getStringProperty(KEY_WEBDRIVER_ONBOOT_KILL,
				"true",
				"Kill du webdriver courant avant de démarrer"));

		Trace.info(" > Les fichiers téléchargés seront stockés dans : " + AFFileUtils.DEFAULT_DOWNLOAD_DIR_WIN);
	}


	/**
	 * Récupération d'une chaine à partir de la configuration ou du système<br>
	 * Méthode uniquement utilisée pour l'initialisation de la configuration<br>
	 * 
	 * Priorité :<br>
	 * - variables d'environnement<br>
	 * - propriétés JVM<br>
	 * - fichier properties
	 * 
	 * @param propertyKeyName nom de la clé
	 * @param defaultValue valeur par défaut
	 * @param string message qui sera affiché dans la trace
	 * 
	 * @return la valeur à prendre en compte
	 */
	protected String getStringProperty(	String propertyKeyName,
										String defaultValue,
										String string)
	{
		String valueToReturn = defaultValue;
		if (System.getenv(propertyKeyName) == null)
		{
			if (System.getProperty(propertyKeyName) == null)
			{
				if (propertiesFileConf.getProperty(propertyKeyName) != null)
				{
					valueToReturn = propertiesFileConf.getProperty(propertyKeyName);
				}
			}
			else
			{
				valueToReturn = System.getProperty(propertyKeyName);
			}
		}
		else
		{
			valueToReturn = System.getenv(propertyKeyName);
		}

		if (valueToReturn != null)
		{
			valueToReturn = valueToReturn.trim();
			System.setProperty(propertyKeyName, valueToReturn);
		}
		Trace.info("> " + string + "          = " + valueToReturn);
		return valueToReturn;
	}


	/**
	 * Récupération d'un entier à partir de la configuration ou du système<br>
	 * Méthode uniquement utilisée pour l'initialisation de la configuration
	 * 
	 * @param propertyKeyName nom de la clé
	 * @param defaultValue valeur par défaut
	 * @param string message qui sera affiché dans la trace
	 * 
	 * @return la valeur à prendre en compte
	 */
	private int getIntegerProperty(	String propertyKeyName,
									int defaultValue,
									String string)
	{
		return Integer.parseInt(getStringProperty(propertyKeyName, String.valueOf(defaultValue), string));
	}


	/**
	 * Récupération d'une clé de propriété autre que celles par défaut<br>
	 * Cette méthode ira d'abord récupérer en propriété système puis dans le properties local
	 * 
	 * @param key nom de la clé
	 * @return la valeur ou null si non trouvé
	 */
	public String getKey(String key)
	{
		return getKey(key, true);
	}


	/**
	 * Récupération d'une clé de propriété autre que celles par défaut
	 * 
	 * 
	 * @param key nom de la clé
	 * @param systemPropertyFirst : doit on passer par le système d'abord puis par le properties ou l'inverse ?
	 * @return la valeur ou null si non trouvé
	 */
	public String getKey(	String key,
							boolean systemPropertyFirst)
	{
		String value = null;
		if (systemPropertyFirst)
		{
			value = System.getenv(key);
			if (value == null)
			{
				value = System.getProperty(key);
				if (value == null)
				{
					value = propertiesFileConf.getProperty(key);
				}
			}
		}
		else
		{
			value = propertiesFileConf.getProperty(key);
			if (value == null)
			{
				value = System.getenv(key);
				if (value == null)
				{
					value = System.getProperty(key);
				}
			}
		}

		if (value != null)
		{
			value = value.trim();
		}
		return value;
	}


	/**
	 * Récupération du nom de la clé en fonction de la valeur
	 * 
	 * @param value
	 * @return
	 */
	public String getKeynameFromValue(String value)
	{
		// Système
		Properties pros = System.getProperties();
		for (Map.Entry<Object, Object> entry : pros.entrySet())
		{
			if (value.equals(entry.getValue()))
				return entry.getKey().toString();
		}

		// Fichier de conf
		for (Map.Entry<Object, Object> dentry : propertiesFileConf.entrySet())
		{
			if (value.equals(dentry.getValue()))
				return dentry.getKey().toString();
		}

		return null;
	}
}
