package admin.com.smokingtest;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.netease.dagger.BrowserEmulator;
import com.netease.datadriver.ExcelDataProvider;

public class Admin_05_user_state_setting {
	
	BrowserEmulator sobet;

	@Test(dataProvider = "dp")
	public void testCase(Map<String, String> data) {
		sobet.open(data.get("url").trim());
		sobet.type(data.get("user_name").trim(), data.get("user_name1").trim());
		sobet.type(data.get("password_Box").trim(), data.get("password").trim());
		sobet.click(data.get("login_button").trim());
		sobet.isTextPresent(data.get("expect").trim(), 2000);
		sobet.click(data.get("user_state_setting_link").trim());
		sobet.selectFrameByIndex(0);
		sobet.type(data.get("cn_search").trim(), data.get("cn_search1").trim());
		sobet.click(data.get("search_button").trim());
		sobet.quitFrame();
	}

	@DataProvider(name = "dp")
	public Iterator<Object[]> dataForTestMethod(Method method)
			throws IOException {
		return new ExcelDataProvider(this.getClass().getName(),
				method.getName());
	}

	@BeforeClass
	public void setUp() throws Exception {
		sobet = new BrowserEmulator();
		// DBOperator.testDataPreparation(this.getClass().getSimpleName().toString());
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		sobet.quit();
	}

}
