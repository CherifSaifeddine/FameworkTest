package hooks;

import java.io.File;
import java.util.concurrent.TimeUnit;

//import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import setup.AFConfig;
import setup.AFWebDrivers;
import utils.AFScreenshotUtils;
import utils.AFWinUtils;
import utils.Trace;

/**
 * D�marrage et initialisation du framework
 *
 */
public class AFStartup
{

	private static WebDriver wdDriver;

	public static String failedScenarName = null;

//	/**
//	 * Chargeur des OR
//	 */
//	private static List<AFORLoader> orLoaderList = new ArrayList<>();
//
//	/**
//	 * Chargeur des beans
//	 */
//	private static AFBeansLoader beansLoader;

	/**
	 * Configuration du framework
	 */
	private static AFConfig config;


	/**
	 * Utilitaire de capture d'�cran
	 */
	private static AFScreenshotUtils screenshotUtils;

	/**
	 * Activation/D�sactivation des steps (glues)
	 */
	private static boolean stepsEnabled = true;

	private AFStartup()
	{
		// do not use
	}


	/**
	 * D�marrage du framework et initialisations
	 * 
	 * @param properties chemin vers le fichier de configuration
	 * @param xmlRootForBeanLoader chemin vers les beans
	 * @param projectOverrides
	 * 
	 * @throws Exception
	 */
	public static void startFramework(String properties)
		throws Exception
	{
		try
		{
			Trace.info("Initialisation du framework...");
//			beansLoader = new AFBeansLoader(xmlRootForBeanLoader);
			AFStartup.setConfig(new AFConfig(properties));
//			highlight = new AFHighlight(AFStartup.getConfig().highlighting);

			killPreviousDriver(AFStartup.getConfig().browser);
			try
			{
				AFStartup.setWebDriver(createWebDriver(AFStartup.getConfig().browser));
			}
			catch (Exception e)
			{
				Trace.error("Erreur durant la cr�ation du driver : " + e.getMessage());
				throw e;
			}
			// On envoie le driver � l'utilitaire de capture d'�cran
//			AFStartup.setScreenshotUtils(new AFScreenshotUtils(AFStartup.getWebDriver()));

//			createORLoaders(packageName);
			Trace.info("Initialisation du framework...OK");
		}
		catch (Exception e)
		{
			Trace.error("Erreur durant le d�marrage du FRAMEWORK : " + e.getMessage(), e);
			throw e;
		}
	}


	/**
	 * Initialisation des OR pour les N packages demand�s
	 * 
	 * @param packageName String avec soit 'com.package' soit 'com.package1;com.packageN;'
	 */
//	private static void createORLoaders(String packageName)
//	{
//		// Multiples OR � charger
//		if (packageName.contains(";"))
//		{
//			String[] packages = packageName.split(";");
//			for (String p : packages)
//			{
//				if (p.isEmpty())
//					continue;
//				orLoaderList.add(new AFORLoader(AFStartup.getWebDriver(), p));
//			}
//		}
//		// Un unique OR � charger
//		else
//		{
//			orLoaderList.add(new AFORLoader(AFStartup.getWebDriver(), packageName));
//		}
//	}


	/**
	 * Arr�t / d�chargements du framework
	 * 
	 */
	public static void stopFramework()
	{
		Trace.info("Arr�t du framework...");

		if (AFStartup.getWebDriver() != null)
		{
			AFStartup.getWebDriver().quit();
		}
		Trace.info("Arr�t du framework OK");
	}


	/**
	 * Nettoyage des instances de webdriver en suspens pour un navigateur donn�
	 * 
	 * @param browserName le nom du navigateur courant
	 */
	protected static boolean killPreviousDriver(String browserName)
	{
		if (AFStartup.getConfig().killCurrentWebDriverOnBoot == false)
		{
			Trace.info("La configuration du lanceur fait que le driver '" + browserName + "' ne sera pas tu�.");
			return false;
		}

		// TODO �quivalent unix
//		if (!SystemUtils.IS_OS_WINDOWS)
//			return false;

		Trace.info("Nettoyage des instances de webdriver pour le navigateur '" + browserName + "'.");

		String aWebDriverPath = AFWebDrivers.getWebDriverPathFromBrowserName(browserName);
		if (aWebDriverPath == null)
		{
			Trace.error("Impossible de trouver l'emplacement du webdriver pour le nettoyage.");
			return false;
		}

		File f = new File(aWebDriverPath);
		if (!f.exists() || !f.isFile())
		{
			Trace.error("Impossible de trouver l'emplacement du webdriver pour le nettoyage.");
			return false;
		}

		// R�cup�rer le nom de l'executable
		String execName = f.getName();
		AFWinUtils.killWebDriverProcess(execName);
		Trace.info("Nettoyage termin�.");
		return true;
	}


	/**
	 * Initialisation du webdriver et configuration browser
	 * 
	 * @param _sBrowserToUse
	 * @return
	 * @throws InterruptedException
	 */
	private static WebDriver createWebDriver(String sBrowserToUse) throws InterruptedException
	{
		int browserWidth = AFStartup.getConfig().headlessWidth;
		int browserHeight = AFStartup.getConfig().headlessHeight;

		Trace.info("Initialisation du webdriver : " + sBrowserToUse + " : DEBUT");

		WebDriver wdToReturn = null;
		switch (sBrowserToUse)
		{
			case "chrome":
				wdToReturn = AFWebDrivers.initChromeDriver(AFStartup.getConfig(), browserWidth, browserHeight);
				break;
			case "firefox":
				wdToReturn = AFWebDrivers.initFirefoxDriver(AFStartup.getConfig(), browserWidth, browserHeight);
				break;
			case "ie":
				wdToReturn = AFWebDrivers.initIEDriver(AFStartup.getConfig(), browserWidth, browserHeight);
				break;
			case "edgechromium":
				wdToReturn = AFWebDrivers.initEdgeChromium(AFStartup.getConfig(), browserWidth, browserHeight);
				break;
			default:
				Trace.error("Invalid browser name : " + sBrowserToUse + " (possible values : chrome, firefox, ie, edgechromium)");
				throw new WebDriverException("Invalid browser name : " + sBrowserToUse);
		}
		if (wdToReturn == null)
		{
			throw new WebDriverException("Error during webdriver setup : " + sBrowserToUse);
		}
		Trace.info("Initialisation du webdriver : " + sBrowserToUse + " : FIN");

		wdToReturn.manage().timeouts().pageLoadTimeout(AFStartup.getConfig().syncPageTimeout, TimeUnit.SECONDS);
		// En non jenkins : maximiser la fen�tre
		if (!AFStartup.getConfig().modeHeadless)
		{
			wdToReturn.manage().window().maximize();
		}

		// Jenkins : clear cookies
		if (AFStartup.getConfig().modeHeadless)
		{
			wdToReturn.manage().deleteAllCookies();
		}

		int width = wdToReturn.manage().window().getSize().getWidth();
		int height = wdToReturn.manage().window().getSize().getHeight();
		Trace.info("========================");
		Trace.info("INFORMATIONS ECRAN DU NAVIGATEUR");
		Trace.info("width=" + width + ", height=" + height);
		Trace.info("========================");
		return wdToReturn;
	}




	//////////////////////////////////////////
	// GETTERS / SETTERS
	//////////////////////////////////////////


	public static WebDriver getWebDriver()
	{
		return wdDriver;
	}


	protected static void setWebDriver(WebDriver wd)
	{
		wdDriver = wd;
	}


//	public static List<AFORLoader> getORLoaders()
//	{
//		return orLoaderList;
//	}
//
//
//	public static AFBeansLoader getBeans()
//	{
//		return beansLoader;
//	}


	public static AFConfig getConfig()
	{
		return config;
	}


	protected static void setConfig(AFConfig aConfig)
	{
		config = aConfig;
	}



	public static boolean areStepsEnabled()
	{
		return stepsEnabled;
	}


	public static void setStepsEnabled(boolean stepsEnabled)
	{
		setStepsEnabled(stepsEnabled, true);
	}


	public static void setStepsEnabled(	boolean stepsEnabled,
										boolean trace)
	{
		if (trace)
		{
			Trace.info("===========");
			if (stepsEnabled)
				Trace.info("=========== ENABLING ALL STEPS  ===========");
			else
				Trace.info("=========== DISABLING ALL STEPS ===========");
			Trace.info("===========");
		}

		AFStartup.stepsEnabled = stepsEnabled;
	}


	public static AFScreenshotUtils getScreenshotUtils()
	{
		return AFStartup.screenshotUtils;
	}


	private static void setScreenshotUtils(AFScreenshotUtils screenshotUtils)
	{
		AFStartup.screenshotUtils = screenshotUtils;
	}
}

