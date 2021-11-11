package dom;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import hooks.AFStartup;

public class DOMWaitList {

	private static WebDriver wdDriver = AFStartup.getWebDriver();

	public static List<WebElement> waitForNumberOfElementsToBe(By _selector, int _number) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.numberOfElementsToBe(_selector, _number));
	}

	public static List<WebElement> waitForNumberOfElementsToBeLessThan(By _selector, int _number)
			throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.numberOfElementsToBeLessThan(_selector, _number));
	}

	public static List<WebElement> waitForNumberOfElementsToBeMoreThan(By _selector, int _number)
			throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(_selector, _number));
	}

	public static List<WebElement> waitForVisibilityOfAllElements(List<WebElement> _weList) throws RuntimeException {
		WebDriverWait wait = new WebDriverWait(wdDriver, AFStartup.getConfig().syncWaitTime);
		return wait.until(ExpectedConditions.visibilityOfAllElements(_weList));
	}
}
