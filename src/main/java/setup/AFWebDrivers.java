package setup;

import java.net.HttpURLConnection;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import hooks.AFStartup;
import utils.AFFileUtils;
import utils.Trace;

/**
 * Initialisation des web drivers
 *
 */
public class AFWebDrivers
{
	private AFWebDrivers()
	{
		// do not use
	}


	/**
	 * Création d'un RemoteWebDriver (en mode Docker)
	 * 
	 * @param remoteURL
	 * @param options
	 * @return le WebDriver ou null si echec
	 * @throws InterruptedException
	 */
	private static WebDriver createRemoteWebDriver(	URL remoteURL,
													Capabilities options)
		throws InterruptedException
	{
		WebDriver wdToReturn = null;

		int currentTry = 0;
		while (currentTry < 100)
		{
			currentTry++;
			Trace.debug("RemoteWebDriver : Tentative [" + currentTry + "] de récupération du driver via : " + remoteURL);
			try
			{
				// On essaye d'abord d'accéder à l'URL
				HttpURLConnection connection = (HttpURLConnection) remoteURL.openConnection();
				connection.setConnectTimeout(10000);
				connection.connect();

				// Si ça n'a pas planté on va tenter d'instancier le driver
				wdToReturn = new RemoteWebDriver(remoteURL, options);

				// Et si ça a marché on sort
				break;
			}
			catch (Exception e)
			{
				Trace.debug("RemoteWebDriver : Tentative [" + currentTry + "] en echec, pause et retry");
				Thread.sleep(3000);
			}
		}

		return wdToReturn;
	}


	/**
	 * Init driver chrome
	 * 
	 * @param config
	 * @param browserWidth
	 * @param browserHeight
	 * @return
	 * @throws InterruptedException
	 */
	public static WebDriver initChromeDriver(	AFConfig config,
												int browserWidth,
												int browserHeight)
		throws InterruptedException
	{
		WebDriver wdToReturn = null;
		ChromeOptions options = configureChromeOptions(config, browserWidth, browserHeight);

		// Mode RemoteWebDriver/Docker
		if (config.remoteWebDriverUrl != null)
		{
			String driverPath = System.getProperty("user.dir") + "/exe/chromedriver";
			System.setProperty(AFConfig.KEY_WEBDRIVER_CHROME_PATH, driverPath);

			wdToReturn = createRemoteWebDriver(config.remoteWebDriverUrl, options);
		}
		else
		{
			wdToReturn = new ChromeDriver(options);
		}
		return wdToReturn;
	}


	/**
	 * Récupération des options chrome (utilisé pour edge chromium)
	 * 
	 * @param config
	 * @param browserWidth
	 * @param browserHeight
	 * @return
	 */
	private static ChromeOptions configureChromeOptions(AFConfig config,
														int browserWidth,
														int browserHeight)
	{
		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-extensions");
		options.addArguments("--lang=" + config.lang.toLowerCase());
		if (config.modeHeadless)
		{
			options.addArguments("--window-size=" + browserWidth + "," + browserHeight);
			options.addArguments("headless");

			// Screenshots KO en chrome headless
			// https://stackoverflow.com/questions/55364056/timed-out-receiving-message-from-renderer-10-000-while-capturing-screenshot-usi
			// https://bugs.chromium.org/p/chromium/issues/detail?id=942023
			options.addArguments("--disable-features=VizDisplayCompositor");

			// PAS-DE-CACHE : navigation privée
			options.addArguments("--incognito");
		}

		// Gestion HTTPS
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

		options.setCapability(ChromeOptions.CAPABILITY, options);
		options.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);

		// Lancement de chrome avec/sans ouverture du "enregistrer sous"
		java.util.Map<String, Object> preferences = new java.util.HashMap<>();
		// preferences.put("profile.default_content_settings.popups", 0);
		preferences.put("download.prompt_for_download", false);
		// preferences.put("download.default_directory", AFFileUtils.DEFAULT_DOWNLOAD_DIR_WIN);
		options.setExperimentalOption("prefs", preferences);

		// ================= Résoudre les pb de timeout sur Chrome
		// https://stackoverflow.com/questions/48450594/selenium-timed-out-receiving-message-from-renderer
		// AGRESSIVE: options.setPageLoadStrategy(PageLoadStrategy.NONE); // https://www.skptricks.com/2018/08/timed-out-receiving-message-from-renderer-selenium.html
		// NOSONAR options.addArguments("start-maximized"); // https://stackoverflow.com/a/26283818/1689770
		options.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
		// NOSONAR options.addArguments("--headless"); // only if you are ACTUALLY running headless
		options.addArguments("--no-sandbox"); // https://stackoverflow.com/a/50725918/1689770
		options.addArguments("--disable-infobars"); // https://stackoverflow.com/a/43840128/1689770
		options.addArguments("--disable-dev-shm-usage"); // https://stackoverflow.com/a/50725918/1689770
		options.addArguments("--disable-browser-side-navigation"); // https://stackoverflow.com/a/49123152/1689770
		options.addArguments("--disable-gpu"); // https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc
		// =================

		if (config.afProxy.getSeleniumProxyObject() != null)
		{
			options.setCapability("proxy", config.afProxy.getSeleniumProxyObject());
		}
		return options;
	}


	/**
	 * Init driver FF
	 * 
	 * @param config
	 * @param browserWidth
	 * @param browserHeight
	 * @return
	 * @throws InterruptedException
	 */
	public static WebDriver initFirefoxDriver(	AFConfig config,
												int browserWidth,
												int browserHeight)
		throws InterruptedException
	{
		WebDriver wdToReturn = null;

		FirefoxOptions options = configureFirefoxOptions(config, browserWidth, browserHeight);
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "false"); // important

		// Mode RemoteWebDriver/Docker
		if (config.remoteWebDriverUrl != null)
		{
			wdToReturn = createRemoteWebDriver(config.remoteWebDriverUrl, options);
		}
		else
		{
			System.setProperty("webdriver.gecko.driver", config.driverGecko);
			wdToReturn = new FirefoxDriver(options);
		}
		return wdToReturn;
	}


	/**
	 * Récupération des options firefox
	 * 
	 * @param config
	 * @param browserWidth
	 * @param browserHeight
	 * @return
	 */
	private static FirefoxOptions configureFirefoxOptions(	AFConfig config,
															int browserWidth,
															int browserHeight)
	{
		FirefoxOptions options = new FirefoxOptions();
		options.setCapability("marionette", true);
		options.addPreference("browser.popups.showPopupBlocker", false);

		if (config.modeHeadless)
		{
			options.addArguments("--width=" + browserWidth);
			options.addArguments("--height=" + browserHeight);

			if (config.remoteWebDriverUrl != null)
			{
				// En mode Docker, il ne faut pas instancier de FirefoxBinary sinon plantage
			}
			else
			{
				FirefoxBinary firefoxBinary = new FirefoxBinary();
				firefoxBinary.addCommandLineOptions("--headless");

				// CES LIGNES NE MARCHENT PAS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				// NOSONAR firefoxBinary.addCommandLineOptions("--window-size=1920," + height);
				// NOSONAR options.addArguments("--window-size=1920," + height);
				// NOSONAR firefoxBinary.addCommandLineOptions("--width=1920 --height=" + height);
				// NOSONAR options.addArguments("--width=1920 --height=" + height);
				// NOSONAR options.addArguments("--window-size").addArguments("1920," + height);
				// NOSONAR System.setProperty("MOZ_HEADLESS_HEIGHT", height);
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

				options.setBinary(firefoxBinary);
			}
		}

		// PAS-DE-CACHE : profile
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.cache.disk.enable", false);
		profile.setPreference("browser.cache.memory.enable", false);
		profile.setPreference("browser.cache.offline.enable", false);
		profile.setPreference("network.http.use-cache", false);

		// Gestion HTTPS
		profile.setAcceptUntrustedCertificates(true);
		profile.setAssumeUntrustedCertificateIssuer(false);

		// Empecher les popup de téléchargement de fichier
		profile.setPreference("browser.download.dir", AFFileUtils.DEFAULT_DOWNLOAD_DIR_WIN);  // folder
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", AFFileUtils.DOWNLOAD_ACCEPTED_MIMETYPES);  // MIME type
		profile.setPreference("pdfjs.disabled", true);  // disable the built-in PDF viewer
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.panel.shown", false);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.download.manager.focusWhenStarting", false);
		profile.setPreference("browser.download.manager.useWindow", false);
		profile.setPreference("browser.download.manager.showAlertOnComplete", false);

		options.setProfile(profile);
		options.setLogLevel(FirefoxDriverLogLevel.FATAL);

		if (config.afProxy.getSeleniumProxyObject() != null)
		{
			options.setCapability("proxy", config.afProxy.getSeleniumProxyObject());
		}
		return options;
	}


	/**
	 * Init driver IE
	 * 
	 * @param config
	 * @param browserWidth
	 * @param browserHeight
	 * @return
	 */
	public static WebDriver initIEDriver(	AFConfig config,
											int browserWidth,
											int browserHeight)
	{
		WebDriver wdToReturn;
		InternetExplorerOptions options = new InternetExplorerOptions();

		options.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

		// PAS-DE-CACHE : IE
		options.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		options.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);

		options.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
		options.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		// NOSONAR options.setCapability("nativeEvents", false);

		// Gestion HTTPS
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

		setRequireWindowFocusCapabilityForIE(config, options);

//		 TODO voir si le proxy est nécessaire/réglable sur IE
		 if (config.afProxy.getSeleniumProxyObject() != null)
		 {
		 options.setCapability("proxy", config.afProxy.getSeleniumProxyObject());
		 }

		wdToReturn = new InternetExplorerDriver(options);

		// Sur IE pas de headless mais on sait qu'on est en mode jenkins
		if (config.modeHeadless)
		{
			wdToReturn.manage().window().setSize(new Dimension(browserWidth, browserHeight));
			wdToReturn.manage().window().maximize();
		}

		// On force le zoom à 100% sur IE local+jenkins via CTRL zéro
		wdToReturn.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, "0"));
		return wdToReturn;
	}


	/**
	 * Réglage de InternetExplorerDriver.REQUIRE_WINDOW_FOCUS en fonction du paramétrage
	 * 
	 * @param options
	 */
	private static void setRequireWindowFocusCapabilityForIE(	AFConfig config,
																InternetExplorerOptions options)
	{
		// https://stackoverflow.com/questions/22338609/webdriver-how-do-i-prevent-internetexplorerdriver-from-taking-control-of-the-mo
		// Introduced the "requireWindowFocus" capability into the IE driver.
		// When used in conjunction with the "nativeEvents" capability,
		// the driver will attempt to bring the current IE window to the foreground before executing a mouse or keyboard event.
		//
		// Also, when the requireWindowFocus capability is set to true,
		// advanced user interactions will now use the Windows SendInput() API to execute the interactions.
		// To enable this behavior, set the value of the requiresWindowFocus capability to "true" when creating an instance of the IE driver.
		// The default for this new capability is "false".
		// This functionality is currently considered extremely experimental; use at your own risk.
		boolean requireWindowFocusIE = false;
		if ("true".equalsIgnoreCase(config.getKey("browser.ie.requireWindowFocus")))
		{
			requireWindowFocusIE = true;
		}
		options.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, requireWindowFocusIE);
	}


	/**
	 * Initialisation du driver EDGE (chromium)
	 * 
	 * @param config
	 * @param browserWidth
	 * @param browserHeight
	 * @return
	 */
	public static WebDriver initEdgeChromium(	AFConfig config,
												int browserWidth,
												int browserHeight)
	{
		WebDriver wdToReturn;
		ChromeOptions options = configureChromeOptions(config, browserWidth, browserHeight);
		options.setBinary(config.binEdgeCH); // important d'indiquer le path
		Trace.info("Emplacement du binaire EDGE : " + config.binEdgeCH);

		EdgeOptions edgeOptions = new EdgeOptions().merge(options);
		edgeOptions.setCapability("browserName", "MicrosoftEdge");

		System.setProperty("webdriver.edge.driver", config.driverEdgeCH);

		wdToReturn = new EdgeDriver(edgeOptions);

		return wdToReturn;
	}


	/**
	 * Récupérer le path d'un webdriver en fonction du nom du navigateur
	 * 
	 * @param browserName nom du navigateur
	 * @return le nom du path du webdriver ou null si non trouvé
	 */
	public static String getWebDriverPathFromBrowserName(String browserName)
	{
		String aWebDriverPath = null;
		if (browserName != null)
		{
			AFConfig conf = AFStartup.getConfig();
			switch (browserName)
			{
				case "firefox":
					aWebDriverPath = conf.driverGecko;
					break;
				case "chrome":
					aWebDriverPath = conf.driverChrome;
					break;
				case "edgechromium":
					aWebDriverPath = conf.driverEdgeCH;
					break;
				case "ie":
					aWebDriverPath = conf.driverIe;
					break;
			}
		}
		return aWebDriverPath;
	}
}
