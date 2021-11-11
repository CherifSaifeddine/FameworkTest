package dom;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Méthodes utilitaires liées aux DOM<br>
 * Ici ces méthodes sont en rapport avec les écritures à l'intérieur
 * d'éléments web
 *
 */
public class DOMSet {

	private DOMSet() {
		// do not use
	}

	/**
	 * Méthode permettant d'écrire dans un webElement (input/textarea)
	 * 
	 * @param sValue valeur
	 * @param we     WebElement
	 */
	public static void setValueOfWebElement(String sValue, WebElement we) {
		DOMWait.waitForElementVisibleOnce(we);
		DOMWait.waitForElementClickable(we);

		if (sValue == null || sValue.isEmpty()) {
			// we.clear() ne déclenche pas d'évènement et c'est volontaire d'après la
			// javadoc

			we.clear();
			// we.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			we.sendKeys(Keys.BACK_SPACE);

			we.click();
			DOMJS.triggerJavascriptEvent(we, "change");
			return;
		}

//		sValue = AFStartup.getLang().getLabelValue(sValue); // LANG
		we.sendKeys(Keys.chord(Keys.CONTROL, "a"));

		// Cas du textarea ou les '\n' des .feature feront des ENTER
		if (sValue.contains("\\n") && "textarea".equalsIgnoreCase(we.getTagName())) {
			String[] lines = sValue.split("\\\\n", -1);
			for (int i = 0; i < lines.length; i++) {
				if (!lines[i].isEmpty())
					we.sendKeys(lines[i]);

				if (i != (lines.length - 1))
					we.sendKeys(Keys.ENTER);
			}
		}
		// Cas input standard ou textarea sans \n
		else {
			we.sendKeys(sValue);
		}
	}

}
