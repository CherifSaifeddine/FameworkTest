package dom;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import hooks.AFStartup;

public class DOMJS {
	private static WebDriver driver = AFStartup.getWebDriver();

	/**
	 * @deprecated use enterTextByJsByID
	 * 
	 * @param id
	 * @param text
	 */
	@Deprecated
	public static void entertextbyjs(final String sName, String text) {
		enterTextByJsByName(sName, text);
	}

	/**
	 * Utilisation du javascript pour setter la valeur d'un champ en fonction de son
	 * "id" html
	 * 
	 * @param id
	 * @param text
	 */
	public static void enterTextByJsByID(final String id, String text) {
		enterTextByJsByIDWithIndex(id, text, 0);
	}

	/**
	 * Utilisation du javascript pour setter la valeur d'un champ en fonction de son
	 * "id" html
	 * 
	 * @param id
	 * @param text
	 * @param index
	 */
	public static void enterTextByJsByIDWithIndex(final String id, String text, int index) {
		//text = AFStartup.getLang().getLabelValue(text); // LANG

		WebElement elem = driver.findElement(By.id(id));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.getElementById('" + id + "')[" + index + "].value='" + text + "'");
		elem.click();
	}

	/**
	 * Utilisation du javascript pour setter la valeur d'un champ en fonction de son
	 * "name" html
	 * 
	 * @param name
	 * @param text
	 */
	public static void enterTextByJsByName(final String name, String text) {
		enterTextByJsByNameWithIndex(name, text, 0);
	}

	/**
	 * Utilisation du javascript pour setter la valeur d'un champ en fonction de son
	 * "name" html
	 * 
	 * @param name
	 * @param text
	 * @param index
	 */
	public static void enterTextByJsByNameWithIndex(final String name, String text, int index) {
//		text = AFStartup.getLang().getLabelValue(text); // LANG

		// TODO voir l'utilité de ces click
		WebElement we = driver.findElement(By.name(name));
		we.click();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		we.click();
		js.executeScript("document.getElementsByName('" + name + "')[" + index + "].value='" + text + "'");
	}

	/**
	 * Utilisation du javascript pour setter la valeur d'un webelement<br>
	 * TODO voir
	 * 
	 * @param we
	 * @param text
	 */
	public static void entertextbyjs(WebElement we, String text) {
//		text = AFStartup.getLang().getLabelValue(text); // LANG

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].value='" + text + "'", we);
		we.click();
	}

	public static String selectByIndex(WebDriver driver, WebElement select) {
		String javaScript = "var select = arguments[0]; " + "select.options[arguments[1]].selected = true;"
				+ "return select.selectedOptions[0].text";

		return (String) ((JavascriptExecutor) driver).executeScript(javaScript, select);
	}

	/**
	 * @deprecated use {@link #triggerJavascriptEvent(WebElement, String)}
	 */
	@Deprecated
	public static void manualTrigerEvent(WebDriver driver, WebElement element) {
		String javaScript = "arguments[0].dispatchEvent(new Event('change'));";
		((JavascriptExecutor) driver).executeScript(javaScript, element);
	}

	/**
	 * Déclenchement manuel d'un event JS sur un WebElement
	 * 
	 * @param element   WebElement
	 * @param eventName nom de l'event : mouseout/onmouseout par exemple
	 */
	public static void triggerJavascriptEvent(WebElement element, String eventName) {
		eventName = eventName.toLowerCase();
		if (eventName.startsWith("on")) {
			eventName = eventName.substring(2);
		}

		String javaScript = "" + "function FireEvent( Element, EventName ) {" + "  if(Element != null) {"
				+ "    if(Element.fireEvent) {" + "	     Element.fireEvent( 'on' + EventName );" + "    } else {   "
				+ "      var evObj = document.createEvent('Events');"
				+ "      evObj.initEvent( EventName, true, false );" + "      Element.dispatchEvent( evObj );" + "    }"
				+ "  }" + "}" + "FireEvent(arguments[0], '" + eventName + "');";
		((JavascriptExecutor) driver).executeScript(javaScript, element);
	}
}
