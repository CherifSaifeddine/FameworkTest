package dom;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import hooks.AFStartup;
import utils.Trace;

/**
 * Méthodes utilitaires liées aux DOM<br>
 * Ici ces méthodes sont en rapport avec les clic sur des éléments web
 *
 */
public class DOMClick {
	private static WebDriver wdDriver = AFStartup.getWebDriver();

	private DOMClick() {
		// do not use
	}

	/**
	 * Méthode permettant de cliquer sur un webelement<br>
	 * Dans le cas d'un input type submit, le submit sera appelé<br>
	 * Il est préférable d'utiliser la méthode
	 * {@link #click(String, WebElement, String)}<br>
	 * En cas d'erreur potentielle de synchro, le WebElement sera récupéré à
	 * nouveau pour pouvoir cliquer dessus
	 * 
	 * @param sClickType normal/perform/javascript/right/double
	 * @param we         WebElement
	 * 
	 * @throws Throwable
	 */
	public static void click(String sClickType, WebElement we) throws Throwable {
		click(sClickType, we, null);
	}

	/**
	 * Méthode permettant de cliquer sur un webelement<br>
	 * Dans le cas d'un input type submit, le submit sera appelé<br>
	 * 
	 * @param sClickType normal/perform/javascript/right/double
	 * @param we         WebElement
	 * @param weXpath    le xpath correspondant au WebElement pour pouvoir
	 *                   réessayer de cliquer en cas de problème de synchro
	 * 
	 * @throws Throwable
	 */
	@SuppressWarnings("deprecation")
	public static void click(String sClickType, WebElement we, String weXpath) throws Throwable {
		// null signifie que cette trace ne sera pas visible (allègement des traces)
		if (!Trace.methodStart(null))
			return;

		sClickType = sClickType.toLowerCase();
		DOMWaitSynchro.waitForAll();

		String tagName = null;
		try {
			tagName = we.getTagName();
		} catch (WebDriverException e) {
			if (weXpath != null && !weXpath.isEmpty()) {
				Trace.warn("Exception durant la récupération du tag d'un webelement :" + e.getMessage());
				DOMWaitSynchro.waitForAll();
//				we = ORUtils.getWebElement(weXpath);
			} else
				throw e;
		}

		// Dans le cas d'un input type submit, on préfère appeler submit() pluôt que
		// click()
		if ("input".equalsIgnoreCase(tagName)) {
			if ("submit".equalsIgnoreCase(we.getAttribute("type"))) {
				if (we.getAttribute("onclick") == null) {
					DOMWait.waitForElementVisibleOnce(we);
					we.submit();
					Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName(), sClickType, we.toString());

					DOMWaitSynchro.waitForAll();
					return;
				}
			}
		}

		switch (sClickType) {
//		case "normal":
//			try {
//				DOMWait.waitForElementClickable(DOMWait.waitForElementVisibleOnce(we)).click();
//			} catch (WebDriverException e) {
//				if (weXpath != null && !weXpath.isEmpty()) {
//					Trace.warn("Un clic normal a déclenché une possible erreur de synchro, on retente : "
//							+ e.getMessage());
//					DOMWaitSynchro.waitForAll();
//					we = ORUtils.getWebElement(weXpath);
//					DOMWait.waitForElementClickable(DOMWait.waitForElementVisibleOnce(we)).click();
//				} else
//					throw e;
//			}
//			break;
		case "perform":
			DOMWait.waitForElementVisibleOnce(we);
			DOMWait.waitForElementClickable(we);
			Actions action = new Actions(wdDriver);
			action.moveToElement(we).click().perform();
			break;
		case "slowperform":
			DOMWait.waitForElementVisibleOnce(we);
			DOMWait.waitForElementClickable(we);
			Actions ac = new Actions(wdDriver);
			ac.moveToElement(we).perform();
			DOMWaitSynchro.wait_X_millisecondes(500);
			we.click();
			DOMWaitSynchro.wait_X_millisecondes(500);
			break;
		case "javascript":
			DOMWait.waitForElementVisibleOnce(we);
			DOMWait.waitForElementClickable(we);
			JavascriptExecutor executor = (JavascriptExecutor) wdDriver;
			executor.executeScript("arguments[0].click();", we);
			break;
		case "right":
			DOMWait.waitForElementVisibleOnce(we);
			DOMWait.waitForElementClickable(we);
			Actions action1 = new Actions(wdDriver);
			action1.moveToElement(we).contextClick(we).perform();
			break;
		case "double":
			DOMWait.waitForElementVisibleOnce(we);
			DOMWait.waitForElementClickable(we);
			Actions action2 = new Actions(wdDriver);
			action2.moveToElement(we).doubleClick().perform();
			break;

		default:
			org.junit.Assert.fail("Type de clic inconnu : '" + sClickType + "'");
			break;
		}

		if (DOMAlert.isAlertPresent()) {
			Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName(), sClickType,
					"Impossible d'afficher le webelement car une popup alert est en cours.");
		} else {
			Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName(), sClickType, we.toString());
		}
	}

	/**
	 * Cliquer et attendre un autre web element
	 * 
	 * @param sClickType
	 * @param sElement
	 * @param elementToWaitFor
	 * @throws Throwable
	 */
	public static void clickAndWaitForElement(String sClickType, String sElement, String elementToWaitFor)
			throws Throwable {
//		DOMClick.click(sClickType, ORUtils.getWebElement(sElement));

		if (!elementToWaitFor.startsWith("//")) {
//			DOMWait.waitForElementVisibleOnce(ORUtils.getWebElement(elementToWaitFor));
		} else {
			DOMWait.waitForElementVisibleOnce(By.xpath(elementToWaitFor));
		}
	}
}
