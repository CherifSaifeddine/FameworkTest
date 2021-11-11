package dom;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import hooks.AFStartup;
import utils.Trace;

public class DOMAlert {
	protected static WebDriver wdDriver = AFStartup.getWebDriver();

	private DOMAlert() {
		// do not use
	}

	/**
	 * Y'a t'il une popup d'alerte ?
	 * 
	 * @return
	 */
	public static boolean isAlertPresent() {
		return (ExpectedConditions.alertIsPresent().apply(wdDriver) != null);

		// Backup si le dessus ne marche pas
		// try
		// {
		// wdDriver.switchTo().alert();
		// return true;
		// }
		// catch (NoAlertPresentException Ex)
		// {
		// return false;
		// }
	}

	/**
	 * Accepter l'alerte
	 */
	public static boolean acceptAlert() {
		if (!isAlertPresent()) {
			Trace.warn("Aucune popup alert sur la page alors qu'on cherche à en accepter une.");
			return false;
		}

		wdDriver.switchTo().alert().accept();
		return true;
	}

	/**
	 * Refuser l'alerte
	 */
	public static boolean dismissAlert() {
		if (!isAlertPresent()) {
			Trace.warn("Aucune popup alert sur la page alors qu'on cherche à en refuser une.");
			return false;
		}

		wdDriver.switchTo().alert().dismiss();
		return true;
	}

	/**
	 * Ecrire du texte sur l'alerte
	 */
	public static boolean sendKeyToAlert(String value) {
		if (!isAlertPresent()) {
			Trace.warn("Aucune popup alert sur la page alors qu'on cherche à écrire dans une.");
			return false;
		}

		//value = AFStartup.getLang().getLabelValue(value); // LANG
		wdDriver.switchTo().alert().sendKeys(value);
		return true;
	}
}
