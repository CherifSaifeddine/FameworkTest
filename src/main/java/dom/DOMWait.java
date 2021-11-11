package dom;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import hooks.AFStartup;
import utils.Trace;
/**
 * Méthodes utilitaires liées aux DOM<br>
 * Ici ces méthodes sont en rapport avec les attentes (synchro) d'éléments
 * web
 *
 */
public class DOMWait {

	private static WebDriver wdDriver = AFStartup.getWebDriver();

	private DOMWait() {
		// do not use
	}

	/**
	 * Attendre qu'un élément soit visible (donc présent ET visible)
	 *
	 * @param _we WebElement
	 * @return
	 * @throws RuntimeException
	 */
	public static WebElement waitForElementVisibleOnce(WebElement _we) throws RuntimeException {
		return waitForElementVisibleOnce(_we, AFStartup.getConfig().syncWaitTime);
	}

	/**
	 * Attendre qu'un élément soit visible (donc présent ET visible)
	 *
	 * @param _we WebElement
	 * @return
	 * @throws RuntimeException
	 */
	public static WebElement waitForElementVisibleOnce(WebElement _we, int time) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, time);
		return wait.until(ExpectedConditions.visibilityOf(_we));
	}

	/**
	 * Attendre qu'un élément soit visible (donc présent ET visible)
	 *
	 * @param selector By.xpath / By.id / etc...
	 * @param _iTime   temps d'attente (secondes)
	 * @return
	 * @throws RuntimeException
	 */
	public static WebElement waitForElementVisibleOnce(By selector, int _iTime) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, _iTime);
		return wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
	}

	/**
	 * Attendre qu'un élément soit visible (donc présent ET visible)
	 *
	 * @param selector By.xpath / By.id / etc...
	 * @return
	 * @throws RuntimeException
	 */
	public static WebElement waitForElementVisibleOnce(By selector) throws RuntimeException {
		return waitForElementVisibleOnce(selector, AFStartup.getConfig().syncWaitTime);
	}

	/**
	 * Attendre qu'un élément soit cliquable
	 *
	 * @param selector
	 * @return
	 * @throws RuntimeException
	 */
	public static WebElement waitForElementClickable(By selector) throws RuntimeException {
		return waitForElementClickable(selector, AFStartup.getConfig().syncWaitTime);
	}

	public static WebElement waitForElementClickable(By selector, int time) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, time);
		return wait.until(ExpectedConditions.elementToBeClickable(selector));
	}

	/**
	 * Attendre qu'un élément soit cliquable
	 *
	 * @param _we
	 * @return
	 * @throws RuntimeException
	 */
	public static WebElement waitForElementClickable(WebElement _we) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.elementToBeClickable(_we));
	}

	/**
	 * Attendre qu'un élément soit présent sur la page (pas forcément visible)
	 *
	 * @param selector
	 * @return
	 * @throws RuntimeException
	 */
	public static WebElement waitForElementPresentOnce(By selector) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.presenceOfElementLocated(selector));
	}

	public static boolean waitForElementPresentOfText(By selector, String sTextToBe) throws RuntimeException {
		return waitForElementPresentOfText(selector, sTextToBe, AFStartup.getConfig().syncWaitTime);
	}

	public static boolean waitForElementPresentOfText(WebElement _we, String sTextToBe) throws RuntimeException {
		return waitForElementPresentOfText(_we, sTextToBe, AFStartup.getConfig().syncWaitTime);
	}

	public static boolean waitForElementPresentOfText(WebElement _we, String sTextToBe, int time)
			throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, time);
		Trace.debug("waitForElementPresentOfText => Texte attendu : " + sTextToBe);
		boolean b = wait.until(ExpectedConditions.textToBePresentInElement(_we, sTextToBe));
		Trace.debug("waitForElementPresentOfText => Texte WebElem : " + _we.getText());
		return b;
	}

	public static boolean waitForElementPresentOfText(By selector, String sTextToBe, int _iTime)
			throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, _iTime);
		return wait.until(ExpectedConditions.textToBe(selector, sTextToBe));
	}

	public static WebElement waitFluentForElementVisibleOnce(int _iTimeInSeconds, By selector) {
		WebElement weToReturn = null;
		try {
			weToReturn = new FluentWait<>(wdDriver).withTimeout(Duration.ofSeconds(_iTimeInSeconds))
					.pollingEvery(Duration.ofMillis(250)).ignoring(NotFoundException.class)
					.ignoring(NoSuchElementException.class).ignoring(WebDriverException.class)
					.until(visibilityOfElementLocated(selector));
		} catch (TimeoutException ex) {
			try {
				DOMUtils.reloadPage();
			} catch (Throwable ex1) {
				Logger.getLogger(DOMWait.class.getName()).log(Level.SEVERE, null, ex1);
			}
		}
		return weToReturn;
	}

	public static boolean waitForPageTitle(String sTitle) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.titleIs(sTitle));
	}

	public static boolean waitForElementUntilNotExist(By selector) throws RuntimeException {
		return waitForElementUntilNotExist(selector, AFStartup.getConfig().syncWaitTime);
	}

	public static boolean waitForElementUntilNotExist(By selector, int time) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, time);
		return wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
	}

	// !!! L'implementation est incorrecte (à supprimer quand rework OR finalisé)
	// ==> voir waitForElementUntilNotExist2
	public static boolean waitForElementUntilNotExist(WebElement _we) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.invisibilityOf(_we));
	}

	public static boolean waitForElementUntilNotExist2(WebElement _we) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.stalenessOf(_we));
	}

	public static boolean waitForElementUntilNotExist2(WebElement _we, int time) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, time);
		return wait.until(ExpectedConditions.stalenessOf(_we));
	}

	public static boolean waitForElementUntilAllInvisible(WebElement _we) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.invisibilityOfAllElements(_we));
	}

	public static boolean waitForElementUntilInvisible(WebElement _we) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.invisibilityOf(_we));
	}

	public static boolean waitForElementUntilInvisible(By selector) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
	}

	/**
	 * Attendre qu'un WebElement soit absent
	 *
	 * @param we
	 */
	public static void waitForElementAbsence(WebElement we) {
		try {
			if (we != null) {
				DOMWait.waitForElementUntilNotExist(we);
			}
		} catch (TimeoutException e) {
			if (e.getCause() instanceof NoSuchElementException) {
				Trace.debug(
						"Le wait absence reçoit un NoSuchElementException, l'élément n'existe pas c'est ce qu'on veut.");
			} else {
				if (e.getMessage() != null
						&& e.getMessage().startsWith("Expected condition failed: waiting for invisibility of ")) {
					// L'élément est toujours présent, le wait absence n'a pas fonctionné !
					throw new TimeoutException(
							"waitForElementAbsence n'a pas fonctionné, l'élément est toujours présent", e);
				}
			}
		} catch (Exception e) {
			Trace.debug("Erreur ignorée car on attend une absence d'élément : " + e.getMessage());
		}

		// En général du JS travaille à ce moment donc on attend la fin
		DOMWaitSynchro.waitForAll();
	}

	/**
	 * Attendre qu'un WebElement soit détaché du dom (Stale)
	 *
	 * @param _we
	 * @param ms
	 * @return
	 * @throws RuntimeException
	 */
	public static boolean waitForElementDetachedFromDom(WebElement _we, long ms) throws RuntimeException {
		return new FluentWait<>(wdDriver).withTimeout(Duration.ofMillis(ms)).pollingEvery(Duration.ofMillis(ms / 4))
				.ignoring(NotFoundException.class).ignoring(NoSuchElementException.class)
				.ignoring(WebDriverException.class).until(ExpectedConditions.stalenessOf(_we));
	}

	public static boolean waitForElementAttributeToBe(WebElement _we, String attribute, String value)
			throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.attributeToBe(_we, attribute, value));
	}

	public static boolean waitForElementAttributeToBe(By selector, String attribute, String value)
			throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.attributeToBe(selector, attribute, value));
	}

	public static void waitInvisibleChampsByClassname(final String classename) {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(classename)));
	}
}
