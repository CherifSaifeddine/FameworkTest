package dom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import hooks.AFStartup;
import utils.Trace;

/**
 * Méthodes utilitaires liées aux DOM<br>
 * Tout ce qui est ici n'a pas pu être rangé dans les autres classes de DOMxxx
 *
 */
public class DOMUtils {

	private static WebDriver wdDriver = AFStartup.getWebDriver();

	private DOMUtils() {
		// do not use
	}


	/**
	 * Déconnexion via l'URL en paramètre
	 * 
	 */
	public static void disconnect(String disconnectURL) {
		Trace.debug("disconnect() : URL = " + disconnectURL);
		wdDriver.get(disconnectURL);
	}

	/**
	 * Rechargement de la page
	 * 
	 */
	public static void reloadPage() {
		wdDriver.navigate().refresh();
		Trace.info("reload the page");
	}

	public static void displayLogDebug(String xpath) {
		Trace.debug("... displayLogDebug(" + xpath + ") : OK");
		Trace.debug("...... xpath = '" + xpath + "'");
		Trace.debug("...... displayed = '" + wdDriver.findElement(By.xpath(xpath)).isDisplayed() + "'");
		Trace.debug("...... enabled = '" + wdDriver.findElement(By.xpath(xpath)).isEnabled() + "'");
		Trace.debug("...... selected = '" + wdDriver.findElement(By.xpath(xpath)).isSelected() + "'");
		Trace.debug("...... alt = '" + wdDriver.findElement(By.xpath(xpath)).getAttribute("alt") + "'");
		Trace.debug("...... src = '" + wdDriver.findElement(By.xpath(xpath)).getAttribute("src") + "'");
		Trace.debug("...... class = '" + wdDriver.findElement(By.xpath(xpath)).getAttribute("class") + "'");
		Trace.debug("...... css = '" + wdDriver.findElement(By.xpath(xpath)).getCssValue("z-index") + "'");
		Trace.debug("...... css = '" + wdDriver.findElement(By.xpath(xpath)).getCssValue("float") + "'");
		Trace.debug("...... css = '" + wdDriver.findElement(By.xpath(xpath)).getCssValue("overflow") + "'");
		Trace.debug("...... css = '" + wdDriver.findElement(By.xpath(xpath)).getCssValue("margin") + "'");
		Trace.debug("...... css = '" + wdDriver.findElement(By.xpath(xpath)).getCssValue("padding") + "'");
		Trace.debug("...... css = '" + wdDriver.findElement(By.xpath(xpath)).getCssValue("cursor") + "'");
	}

}
