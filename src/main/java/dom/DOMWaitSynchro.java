package dom;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import hooks.AFStartup;
import utils.Trace;
/**
 * Méthodes utilitaires liées aux DOM<br>
 * Ici ces méthodes sont en rapport avec les attentes (synchro) d'éléments
 * web
 *
 */
public class DOMWaitSynchro {

	private static WebDriver wdDriver = AFStartup.getWebDriver();

	private DOMWaitSynchro() {
		// do not use
	}

	/**
	 * Attendre que toute la page ait fini de charger (JS/JQuery/Rosace)
	 */
	public static void waitForAll() {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		try {
			ExpectedCondition<Boolean> loadingFinished = new ExpectedCondition<Boolean>() {

				@Override
				public Boolean apply(final WebDriver driver) {

					boolean javascriptFinished = ((JavascriptExecutor) driver)
							.executeScript("return document.readyState").equals("complete");

					Object jQueryJS = ((JavascriptExecutor) driver)
							.executeScript("return (typeof(jQuery) ==='undefined' ? true : (jQuery.active == 0));");
					if (jQueryJS == null) {
						jQueryJS = "true";
					}
					boolean jQueryFinished = Boolean.valueOf(jQueryJS.toString()); // NOSONAR

					// TODO améliorer et rendre personnalisable
					// Object rosaceVisible = ((JavascriptExecutor) driver)
					// .executeScript(
					// "return (document.getElementById(\"Div-Overlay\") != null &&
					// (document.getElementById(\"Div-Overlay\").offsetWidth > 0 ||
					// document.getElementById(\"Div-Overlay\").offsetHeight > 0))");
					// if (rosaceVisible == null)
					// {
					// rosaceVisible = "false";
					// }

					// boolean rosaceVisibleB = Boolean.valueOf(rosaceVisible.toString()); //
					// NOSONAR

					return javascriptFinished && jQueryFinished; // && !rosaceVisibleB;
				}
			};
			wait.ignoring(WebDriverException.class).until(loadingFinished);
		} catch (Exception e) {
			Trace.error("waitForAll()::" + e.getMessage());
		}
	}

	/**
	 * Attendre X secondes
	 * 
	 * @param _iSeconds
	 * 
	 * @deprecated EVITER AU MAXIMUM D'UTILISER CECI !
	 */
	@Deprecated
	public static void wait_X_secondes(int _iSeconds) {
		/*---------------------------------------- Static waits ---------------------------------------------------------
		    *** WARNING ***
		    As best practice you should always use dynamic waits provided by Selenium,
		    This is definitely the right way to have resilient tests, and as fast as the web application allows depending on the context.
		    So you should reserve the usage of static wait for particular situations where after trying everything
		    your still get synchronisation issues or unexpected behaviors.
		----------------------------------------------------------------------------------------------------------------*/
		Object monitor = new Object();
		synchronized (monitor) {
			Trace.debug("... wait for " + _iSeconds + " secondes.");
			try {
				monitor.wait(_iSeconds * 1000L); // NOSONAR
			} catch (InterruptedException e) {
				Trace.error("Erreur durant wait: " + e.getMessage());
			}
		}
	}

	/**
	 * Attendre X ms
	 * 
	 * @param _iMilliSeconds
	 * 
	 * @deprecated EVITER AU MAXIMUM D'UTILISER CECI !
	 */
	@Deprecated
	public static void wait_X_millisecondes(int _iMilliSeconds) throws Throwable {
		/*---------------------------------------- Static waits ---------------------------------------------------------
		    *** WARNING ***
		    As best practice you should always use dynamic waits provided by Selenium,
		    This is definitely the right way to have resilient tests, and as fast as the web application allows depending on the context.
		    So you should reserve the usage of static wait for particular situations where after trying everything
		    your still get synchronisation issues or unexpected behaviors.
		----------------------------------------------------------------------------------------------------------------*/
		Object monitor = new Object();
		synchronized (monitor) {
			try {
				monitor.wait(_iMilliSeconds);
			} catch (InterruptedException e) {
				Trace.error("Erreur durant wait ms: " + e.getMessage());
			}
		}
		if (_iMilliSeconds > 999) {
			Trace.debug("... wait_X_millisecondes(" + _iMilliSeconds + ") : OK");
		}
	}
}
