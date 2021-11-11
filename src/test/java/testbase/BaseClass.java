package testbase;

import org.openqa.selenium.WebDriver;

import hooks.AFStartup;

public class BaseClass {

	public static WebDriver driver;

	/**
	 * This method will create a driver and return it
	 * 
	 * @return WebDriver driver
	 */
	public static WebDriver setUp() {

		driver= AFStartup.getWebDriver();
		// we initialize all the page elements of the classes in package com.neotech.pages
		PageInitializer.initialize();

		return driver;
	}

	/**
	 * This method will quit the browser
	 */
	public static void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

}
