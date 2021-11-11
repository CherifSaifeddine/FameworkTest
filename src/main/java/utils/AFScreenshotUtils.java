package utils;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;


import io.cucumber.java.Scenario;

/**
 * Utilitaire dédié à la capture d'écran
 *
 */
public class AFScreenshotUtils {
	/**
	 * Liste des captures courantes
	 */
	private List<byte[]> screenshotDataList = null;

	/**
	 * Driver de capture
	 */
	private TakesScreenshot driver;

	/**
	 * Constructeur
	 * 
	 * @param driver un WebDriver prenant en charger la capture d'écran
	 */
	public AFScreenshotUtils(WebDriver driver) {
		try {
			this.driver = (TakesScreenshot) driver;
		} catch (Exception e) {
			Trace.error("Erreur durant la création de l'utilitaire de screenshot : " + e.getMessage(), e);
			Trace.error("Ce driver ne prend probablement pas en charge la capture d'écran.");
			this.driver = null;
		}

		if (this.driver != null) {
			screenshotDataList = new ArrayList<>(5);
		}
	}

	/**
	 * Prendre une capture d'écran
	 * 
	 * @return true si la capture a été prise, false sinon
	 */
	public boolean takeScreenshot() {
		boolean captureSuccessful = false;
		if (this.driver == null) {
			Trace.error(
					"L'utilitaire de screenshot a eu une erreur lors du démarrage, impossible de capturer l'écran.");
		} else {
			try {
				byte[] screenshotData = this.driver.getScreenshotAs(OutputType.BYTES);
				getScreenshotDataList().add(screenshotData);
				Trace.info("Une capture d'écran a été réalisée.");
				captureSuccessful = true;
			} catch (Exception e) {
				Trace.error("Une erreur a eu lieu durant la capture d'écran : " + e.getMessage(), e);
			}
		}
		return captureSuccessful;
	}

	/**
	 * Ajout des captures d'écran courantes aux scénario
	 * 
	 * @param scenario Le scénario passé en paramètre
	 */
	public void embedScreenshotsToScenario(Scenario scenario) {
		// Si erreur durant l'initialisation
		if (this.driver == null) {
			return;
		}

		if (getScreenshotDataList() != null && getScreenshotDataList().size() > 0) {
			Trace.debug("Ajout de " + getScreenshotDataList().size()
					+ " capture(s) d'écran en pièce jointe du scénario.");
			for (byte[] screenshotData : getScreenshotDataList()) {
				scenario.attach(screenshotData, "image/png", scenario.getName());
			}

			// Nettoyage
			getScreenshotDataList().clear();
			screenshotDataList = new ArrayList<>(5);
		}
	}

	/**
	 * @return the screenshotDataList
	 */
	protected List<byte[]> getScreenshotDataList() {
		return screenshotDataList;
	}
}
