package dom;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import hooks.AFStartup;
import utils.Trace;

public class DOMKeyboard {
	private static WebDriver driver = AFStartup.getWebDriver();

	/**
	 * Appui sur la touche passée en paramètre (WEB)
	 */
	public static void webPressKey(Keys akey) {
		driver.findElement(By.xpath("//html")).sendKeys(akey);
	}

	/**
	 * Appui sur la touche passée en paramètre (java graphique)
	 */
	public static void awtPressKey(int akey) {
		try {
			Robot robot = new Robot();
			robot.keyPress(akey);
			robot.keyRelease(akey);
		} catch (AWTException e) {
			Trace.error("Erreur durant la saisie des touches AWT : " + e.getMessage());
		}
	}

	/**
	 * Appui sur entrée (java graphique)
	 */
	public static void awt_ENTER() {
		awtPressKey(KeyEvent.VK_ENTER);
	}

	/**
	 * Appui sur entrée (web)
	 */
	public static void web_ENTER() {
		webPressKey(Keys.ENTER);
	}

	/**
	 * Appui sur TAB (java graphique)
	 */
	public static void awt_TAB() {
		awtPressKey(KeyEvent.VK_TAB);
	}

	/**
	 * Appui sur TAB (web)
	 */
	public static void web_TAB() {
		webPressKey(Keys.TAB);
	}

	/**
	 * @deprecated use awt_PAGEDOWN()
	 */
	@Deprecated
	public static void awt_SCROLL() {
		awt_PAGEDOWN();
	}

	/**
	 * Page down (java graphique)
	 */
	public static void awt_PAGEDOWN() {
		awtPressKey(KeyEvent.VK_PAGE_DOWN);
	}

	/**
	 * Page up (java graphique)
	 */
	public static void awt_PAGEUP() {
		awtPressKey(KeyEvent.VK_PAGE_UP);
	}

}
