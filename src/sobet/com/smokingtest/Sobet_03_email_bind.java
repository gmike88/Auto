package sobet.com.smokingtest;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import com.netease.dagger.BrowserEmulator;
import com.netease.datadriver.DBOperator;
import com.netease.datadriver.ExcelDataProvider;

/**
 * 
 * @author Kris
 */

public class Sobet_03_email_bind {
	
	BrowserEmulator sobet; 
	
  @Test(dataProvider = "dp")
  public void testCase(Map<String, String> data){
	  sobet.open(data.get("url").trim());
	  sobet.click(data.get("login_label").trim());
	  sobet.type(data.get("user_name").trim(), data.get("user_name1").trim());
	  sobet.type(data.get("password_Box").trim(), data.get("password").trim());
	  sobet.click(data.get("login_button").trim());
	  sobet.isTextPresent(data.get("expect").trim(), 2000);
	  sobet.click(data.get("bind_email").trim());
	  sobet.switchToNewPage();
	  sobet.type(data.get("email_Address").trim(), data.get("email_Address1").trim());
	  sobet.pressKeyboard(9);//simulation of typing TAB to skip auto-fill
	  sobet.pause(1000);
	  sobet.click(data.get("submit").trim());
	  sobet.pause(5000);
	  sobet.scroll("down", 200);
	  sobet.expectTextExistOrNot(true, "邮箱发送了一封验证邮件，请进入邮箱完成验证。", 5000);
	  sobet.click(data.get("logout_button").trim());
	  sobet.click(data.get("confirm_OK").trim());
	  sobet.isElementPresent(data.get("login_label").trim(), 2000);
  }

  @DataProvider(name = "dp")
  public Iterator<Object[]> dataForTestMethod(Method method)
  throws IOException{
	  return new ExcelDataProvider(this.getClass().getName(), method.getName());
  }
  
  @BeforeClass
  public void setUp() throws Exception {
	  sobet = new BrowserEmulator();
//	  DBOperator.testDataPreparation(this.getClass().getSimpleName().toString());
  }

  @AfterClass(alwaysRun = true)
  public void tearDown() {
	  sobet.quit();
  }

}
