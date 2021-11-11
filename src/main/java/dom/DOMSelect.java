package dom;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import hooks.AFStartup;
import utils.Trace;
/**
 * Méthodes utilitaires liées aux DOM<br>
 * Ici ces méthodes sont en rapport avec les sélections d'éléments web
 *
 */
public class DOMSelect {
	private DOMSelect() {
		// do not use
	}

	/**
	 * Action sur un select pour sélectionner par texte puis par valeur si ça ne
	 * fonctionne pas
	 * 
	 * @param sValue valeur
	 * @param we     WebElement
	 */
	public static void selectByTextOrByValue(String sValue, WebElement we) {
		Select select = convertWebElementToSelect(we);

//		sValue = AFStartup.getLang().getLabelValue(sValue); // LANG
		try {
			select.selectByVisibleText(sValue);
		} catch (NoSuchElementException e) {
			select.selectByValue(sValue);
		}
	}

	/**
	 * Action sur un select pour sélectionner par texte
	 * 
	 * @param sValue valeur
	 * @param we     WebElement
	 */
	public static void selectByText(String sValue, WebElement we) {
		Select select = convertWebElementToSelect(we);

//		sValue = AFStartup.getLang().getLabelValue(sValue); // LANG
		select.selectByVisibleText(sValue);
	}

	/**
	 * Action sur un select pour sélectionner par valeur
	 * 
	 * @param sValue valeur
	 * @param we     WebElement
	 */
	public static void selectByValue(String sValue, WebElement we) {
		Select select = convertWebElementToSelect(we);

//		sValue = AFStartup.getLang().getLabelValue(sValue); // LANG
		select.selectByValue(sValue);
	}

	/**
	 * Action sur un select pour choisir une valeur/texte selon une opérande de
	 * string
	 * 
	 * @param valueOrText value ou text
	 * @param operand     starts-with/contains/ends-with
	 * @param text        le texte à appliquer
	 * @param we          WebElement
	 * 
	 * @throws NoSuchElementException Si aucune option correspondante
	 */
	public static void selectWithOperand(String valueOrText, String operand, String text, WebElement we)
			throws NoSuchElementException {
		Select select = convertWebElementToSelect(we);
		List<WebElement> selectOptions = select.getOptions();
		String textToCompare;
		boolean isValidOption = false;
		for (WebElement anOption : selectOptions) {
			if ("value".equals(valueOrText))
				textToCompare = anOption.getAttribute("value");
			else
				textToCompare = anOption.getText();

			switch (operand) {
			case "starts-with":
				isValidOption = textToCompare.startsWith(text);
				break;
			case "contains":
				isValidOption = textToCompare.contains(text);
				break;
			case "ends-with":
				isValidOption = textToCompare.endsWith(text);
				break;
			default:
				Trace.error("DOMSelect.selectWithOperand:: opérande inconnue : '" + operand + "'");
			}

			if (isValidOption) {
				Trace.debug("DOMSelect.selectWithOperand:: sélection de l'option '" + textToCompare + "'");
				if ("value".equals(valueOrText))
					DOMSelect.selectByValue(textToCompare, we);
				else
					DOMSelect.selectByText(textToCompare, we);

				// On a trouvé la valeur donc on arrête de boucler parmi les options !
				break;
			}
		}

		// Si on a trouvé aucune option, il faut planter !!
		if (!isValidOption) {
			throw new NoSuchElementException(
					"DOMSelect.selectWithOperand:: aucune option correspondante pour le critère '" + valueOrText
							+ "' '" + operand + "' '" + text + "' sur l'élément '" + we + "'.");
		}
	}

	/**
	 * Prend en paramètre un WebElement et renvoie un Select
	 * 
	 * @param we
	 * @return
	 */
	private static Select convertWebElementToSelect(WebElement we) {
		Select select = new Select(we);

		// Mécanisme pour attendre que le Select contienne des options (expérimental,
		// désactivé par défaut)
		if ("true".equals(AFStartup.getConfig().getKey("selectsync.wait.enabled"))) {
			int nbRetries = AFStartup.getConfig().nullSyncRetries;
			List<WebElement> optionsList = select.getOptions();
			int optionsCount = optionsList.size();
			Trace.debug("RETRY-SELECT mécanisme actif : " + optionsCount + " options / " + nbRetries + " essais max.");

			if (optionsCount <= 1 && nbRetries > 0) {
				int waitTimeMs = AFStartup.getConfig().nullSyncTime;
				for (int i = 1; i <= nbRetries; i++) {
					// On commence d'abord par attendre
					try {
						DOMWaitSynchro.wait_X_millisecondes(waitTimeMs);
					} catch (Throwable e) {
					}

					// On réessaye de le récupérer
					int selectSize = select.getOptions().size();
					Trace.debug("RETRY-SELECT-" + i + " : Après une attente de " + waitTimeMs
							+ "ms le select contient " + selectSize + " élément(s).");
					if (selectSize > 1) {
						break;
					}
				}
				if (select.getOptions().size() <= 1) {
					Trace.debug("RETRY-SELECT : Select toujours vide après " + nbRetries + " essais.");
				}
			}
		}
		return select;
	}
}
