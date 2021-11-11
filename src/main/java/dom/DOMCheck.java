package dom;

import org.junit.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import utils.Trace;

/**
 * Méthodes utilitaires liées aux DOM<br>
 * Ici ces méthodes sont en rapport avec les vérifications de contenu
 * d'éléments web
 *
 */
public class DOMCheck {
	private DOMCheck() {
		// do not use
	}

	/**
	 * Permet de vérifier la valeur à l'intérieur d'un WebElement<br>
	 * Les input (attribut value) sont bien pris en compte
	 * 
	 * @param sValue   Valeur à vérifier
	 * @param sElement Nom du WebElement (pour les traces)
	 * @param we       WebElement
	 */
	public static void checkTheValueInTheWebElement(String sValue, WebElement we) {
		DOMWait.waitForElementVisibleOnce(we);
		//sValue = AFStartup.getLang().getLabelValue(sValue); // LANG

		String tagName = we.getTagName();
		String sTextValue = null;
		if ("input".equalsIgnoreCase(tagName)) {
			String inputType = we.getAttribute("type");
			if ("checkbox".equalsIgnoreCase(inputType)) {
				sTextValue = Boolean.toString(we.isSelected());
			} else {
				sTextValue = we.getAttribute("value");
			}
		} else if ("select".equalsIgnoreCase(tagName)) {
			Select select = new Select(we);
			WebElement selectedOption = select.getFirstSelectedOption();
			sTextValue = selectedOption == null ? "" : selectedOption.getAttribute("value");
			if (!(sValue.equals(sTextValue)))
				sTextValue = selectedOption == null ? "" : selectedOption.getText();
		} else {
			sTextValue = we.getText();
			try {
				DOMWait.waitForElementPresentOfText(we, sValue);
			} catch (TimeoutException e) {
			}

		}

		if (!(sValue.equals(sTextValue))) {
			Trace.error("Texte attendu : " + sValue);
			Trace.error("Texte obtenu : " + sTextValue);
			Assert.fail("Impossible de trouver le texte : " + sValue + ""
					+ "\nLe texte présent dans l'élément est : " + sTextValue);
//			Assert.fail("Impossible de trouver dans l'élément '" + sElement + "' le texte : " + sValue + ""
//					+ "\nLe texte présent dans l'élément est : " + sTextValue);
		}
	}

	/**
	 * Action sur un webelement pour vérifier son texte selon une opérande de
	 * string
	 * 
	 * @param operand  starts-with/contains/end-with
	 * @param text     le texte à appliquer
	 * @param sElement le label du WebElement
	 * @param we       WebElement
	 */
	public static void checkWithOperand(String operand, String text, String sElement, WebElement we) {
		String tagName = we.getTagName();
		String textToCompare;
		String alternativeText = null; // fallback pour le select value/text
		if ("input".equalsIgnoreCase(tagName)) {
			textToCompare = we.getAttribute("value");
		} else if ("select".equalsIgnoreCase(tagName)) {
			Select select = new Select(we);
			WebElement selectedOption = select.getFirstSelectedOption();
			textToCompare = selectedOption == null ? "" : selectedOption.getAttribute("value");
			alternativeText = selectedOption == null ? null : selectedOption.getText();
		} else {
			textToCompare = we.getText();
		}

		boolean isValid = false;
		if (textToCompare != null) {
			switch (operand) {
			case "starts-with":
				isValid = textToCompare.startsWith(text);
				if (!isValid && alternativeText != null)
					isValid = alternativeText.startsWith(text);
				break;
			case "contains":
				isValid = textToCompare.contains(text);
				if (!isValid && alternativeText != null)
					isValid = alternativeText.contains(text);
				break;
			case "ends-with":
				isValid = textToCompare.endsWith(text);
				if (!isValid && alternativeText != null)
					isValid = alternativeText.endsWith(text);
				break;
			default:
				Trace.error("DOMCheck.checkWithOperand:: opérande inconnue : '" + operand + "'");
			}
		}
		if (!isValid) {
			Trace.error("Texte attendu : '" + operand + "'(" + text + ")");
			Trace.error("Texte obtenu : " + textToCompare);
			Assert.fail("Impossible d'appliquer dans l'élément '" + sElement + "' l'opérande " + operand + "(" + text
					+ ")" + "\nLe texte présent dans l'élément est : " + textToCompare);
		}
	}
}
