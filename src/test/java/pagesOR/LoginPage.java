package pagesOR;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import testbase.BaseClass;

public class LoginPage extends BaseClass{

	@FindBy(id = "user-name")
	public WebElement userName;

	@FindBy(id = "password")
	public WebElement password;

	@FindBy(xpath = "//input [@value='Login']")
	public WebElement loginBtn;

	@FindBy(xpath = "//h3 [@data-test='error']")
	public WebElement errorMsg;

	public LoginPage() {
		PageFactory.initElements(driver, this);
	}

}
