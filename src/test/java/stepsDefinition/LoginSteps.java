package stepsDefinition;

import org.openqa.selenium.WebDriver;

import dom.DOMCheck;
import dom.DOMClick;
import dom.DOMSet;
import dom.DOMWait;
import dom.DOMWaitSynchro;
import hooks.AFStartup;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.Trace;
import utilsProject.CommonMethods;

public class LoginSteps extends CommonMethods{
	
	static WebDriver wdDriver = AFStartup.getWebDriver();

	@Given("go to the url page")
	public static void go_to_X_page(){
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
	        return;
		
        wdDriver.manage().deleteAllCookies();
        String  url = AFStartup.getConfig().envUrl;
        if (url == null)
        	org.junit.Assert.fail(">>> go_to_X_page : Page name '" + url + "' undefined");
        
        wdDriver.get(url);
        DOMWaitSynchro.waitForAll();
        
        Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName());      
    }
	
	@When("user enters valid username")
	public void user_enters_valid_username() throws Throwable {
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
            return;
		String username=AFStartup.getConfig().getKey("user");
		DOMSet.setValueOfWebElement(username,loginPage.userName);
		Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName());  
	}
	
	@When("user enters valid password")
	public void user_enters_valid_password() {
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
	        return;
		DOMSet.setValueOfWebElement(AFStartup.getConfig().getKey("password"),loginPage.password);
		Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName()); 
	}

	@When("click on login button")
	public void click_on_login_button() throws Throwable {
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
	        return;
		DOMClick.click("normal", loginPage.loginBtn);
		Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName()); 
	}

	@Then("I validate that user is logged in")
	public void i_validate_that_user_is_logged_in() {
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
	        return;
		DOMWait.waitForElementVisibleOnce(mainPage.appLogo);
		Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName()); 
//		Assert.assertTrue(mainPage.appLogo.isDisplayed());
	}

	@When("user leaves password empty")
	public void user_leaves_password_empty() {
//		sendText(loginPage.password, "");
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
	        return;
		DOMSet.setValueOfWebElement("",loginPage.password);
		Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName()); 
	}

	@Then("I validate that {string} message is displayed")
	public void i_validate_that_message_is_displayed(String expectedMsg) {
//		String actualMsg = loginPage.errorMsg.getText();
		DOMCheck.checkTheValueInTheWebElement(expectedMsg, loginPage.errorMsg);
//		Assert.assertEquals("The welcome message is different!!!", expectedMsg, actualMsg);
	}

	@When("user enters invalid username as {string}")
	public void user_enters_invalid_username_as(String username) {
//		sendText(loginPage.userName, username);
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
            return;
		DOMSet.setValueOfWebElement(username,loginPage.userName);
		Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName());  
	}

	@When("user enters invalid password as {string}")
	public void user_enters_invalid_password_as(String password) {
//		sendText(loginPage.password, password);
		if (!Trace.methodStart(new Throwable().getStackTrace()[0].getMethodName()))
	        return;
		DOMSet.setValueOfWebElement(password,loginPage.password);
		Trace.methodStop(new Throwable().getStackTrace()[0].getMethodName()); 
	}

}
