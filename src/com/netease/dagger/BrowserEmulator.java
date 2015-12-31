/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.netease.dagger;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import com.thoughtworks.selenium.webdriven.*;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.Reporter;
import org.openqa.selenium.internal.*;

import com.thoughtworks.selenium.Wait;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;

import com.anteambulo.SeleniumJQuery.*;

/**
 * BrowserEmulator is based on Selenium2 and adds some enhancements
 * 
 * @author ChenKan
 */
public class BrowserEmulator {
	private final class WaitExtension extends Wait {
		private final String text;

		private WaitExtension(String text) {
			this.text = text;
		}

		public boolean until() {
			return isTextPresent(text, -1);
		}
	}

	RemoteWebDriver browserCore;
	WebDriverBackedSelenium browser;
	ChromeDriverService chromeServer;
	JavascriptExecutor javaScriptExecutor;

	int stepInterval = Integer.parseInt(GlobalSettings.stepInterval);
	int timeout = Integer.parseInt(GlobalSettings.timeout);

	private static Logger logger = Logger.getLogger(BrowserEmulator.class
			.getName());

	public BrowserEmulator() {
//		BasicConfigurator.configure();
		PropertyConfigurator.configure(GlobalSettings.baseStorageUrl
				+ "\\src\\log4j.properties");
		setupBrowserCoreType(GlobalSettings.browserCoreType);
		browser = new WebDriverBackedSelenium(browserCore, "http://");
		javaScriptExecutor = (JavascriptExecutor) browserCore;
		logger.info("Started BrowserEmulator");
	}

	private void setupBrowserCoreType(int type) {
		if (type == 1) {
			browserCore = new FirefoxDriver();
			logger.info("Using Firefox");
			return;
		}
		if (type == 2) {
			chromeServer = new ChromeDriverService.Builder()
					.usingDriverExecutable(
							new File(GlobalSettings.chromeDriverPath))
					.usingAnyFreePort().build();
			try {
				chromeServer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches",
					Arrays.asList("--start-maximized"));
			browserCore = new RemoteWebDriver(chromeServer.getUrl(),
					capabilities);
			logger.info("Using Chrome");
			return;
		}
		if (type == 3) {
			System.setProperty("webdriver.ie.driver",
					GlobalSettings.ieDriverPath);
			DesiredCapabilities capabilities = DesiredCapabilities
					.internetExplorer();
			capabilities
					.setCapability(
							InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
							true);
			browserCore = new InternetExplorerDriver(capabilities);
			logger.info("Using IE");
			return;
		}
		if (type == 4) {
			browserCore = new SafariDriver();
			logger.info("Using Safari");
			return;
		}

		Assert.fail("Incorrect browser type");
	}

	/**
	 * Get the WebDriver instance embedded in BrowserEmulator
	 * 
	 * @return a WebDriver instance
	 */
	public RemoteWebDriver getBrowserCore() {
		return browserCore;
	}

	/**
	 * Get the WebDriverBackedSelenium instance embedded in BrowserEmulator
	 * 
	 * @return a WebDriverBackedSelenium instance
	 */
	public WebDriverBackedSelenium getBrowser() {
		return browser;
	}

	/**
	 * Get the JavascriptExecutor instance embedded in BrowserEmulator
	 * 
	 * @return a JavascriptExecutor instance
	 */
	public JavascriptExecutor getJavaScriptExecutor() {
		return javaScriptExecutor;
	}

	/**
	 * Open the URL
	 * 
	 * @param url
	 *            the target URL
	 */
	@SuppressWarnings("deprecation")
	public void open(String url) {
		pause(stepInterval);
		try {
			browser.open(url);
			browserCore.manage().window().maximize();
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to open url " + url);
		}
		logger.info("Opened url " + url);
	}

	/**
	 * Switch to frame by iframe xpath
	 * 
	 * @param frameXpath
	 *            target iframe xpath
	 */
	@SuppressWarnings("deprecation")
	public void selectFrame(String frameXpath) {
		pause(stepInterval);
		try {
			WebElement iframe = browserCore.findElementByXPath(frameXpath);
			browserCore.switchTo().frame(iframe);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to find frame: " + frameXpath);
		}
		logger.info("Find frame: " + frameXpath);
	}

	/**
	 * Open new tab in browser
	 */
	public void openNewTAB() {
		pause(stepInterval);
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_T);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_T);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to open new TAB");
		}
		logger.info("Success to open new TAB");
	}

	/**
	 * Paste text from clipboard content
	 */
	public String getClipboardContent() {
		pause(stepInterval);
		try {
			String text = "";
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable data = clip.getContents(null);
			if (data != null) {
				if (data.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					text = (String) data
							.getTransferData(DataFlavor.stringFlavor);
				}
			}
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to get clipboard content");
		}
		logger.info("Success to get clipboard content");
		return null;
	}

	/**
	 * Quit the browser
	 */
	public void quit() {
		pause(stepInterval);
		browserCore.quit();
		if (GlobalSettings.browserCoreType == 2) {
			chromeServer.stop();
		}
		logger.info("Quitted BrowserEmulator");
	}

	/**
	 * Close previous tab in browser
	 */
	public void closePreviousTAB() {
		pause(stepInterval);
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_PAGE_UP);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_PAGE_UP);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_W);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_W);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to close previous TAB");
		}
		logger.info("Success to close previous TAB");
	}
	
	/**
	 * javascript executer
	 * @param js
	 */
	public void jsExecuter(String js) {
		try {
			pause(stepInterval);
			javaScriptExecutor.executeScript(js);
			logger.info("Javascript execution done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get current URL
	 */
	public String getURL() {
		String currentURL = "";
		try {
			pause(stepInterval);
			currentURL = browserCore.getCurrentUrl();
			logger.info("Current URL is " + currentURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentURL;
	}

	/**
	 * Switch to new page
	 */
	public void switchToNewPage() {
		pause(stepInterval);
		Set<String> handles = new HashSet<String>();
		try {
			String currentHandle = browserCore.getWindowHandle();
			handles = browserCore.getWindowHandles();
			Iterator<String> it = handles.iterator();
			while (it.hasNext()) {
				if (currentHandle == it.next())
					continue;
				browserCore.switchTo().window(it.next());
				logger.info("Switch to page: " + browserCore.getCurrentUrl()
						+ " success");
			}
		} catch (Exception e) {
			logger.info("Switch to page failed and current page: "
					+ browserCore.getCurrentUrl() + e.fillInStackTrace());
		}
	}

	/**
	 * Click the page element
	 * 
	 * @param xpath
	 *            the element's xpath
	 */
	public void click(String xpath) {
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);
		try {
			clickTheClickable(xpath, System.currentTimeMillis(), 2500);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to click " + xpath);
		}
		logger.info("Clicked " + xpath);
	}

	/**
	 * Click the page element
	 * 
	 * @param CSS
	 *            the element's CSS
	 */
	public void clickCSS(String CSS) {
		pause(stepInterval);
		expectElementExistOrNotCSS(true, CSS, timeout);
		try {
			clickTheClickableCSS(CSS, System.currentTimeMillis(), 2500);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to click " + CSS);
		}
		logger.info("Clicked " + CSS);
	}

	/**
	 * Click an element until it's clickable or timeout
	 * 
	 * @param xpath
	 * @param startTime
	 * @param timeout
	 *            in millisecond
	 * @throws Exception
	 */
	private void clickTheClickable(String xpath, long startTime, int timeout)
			throws Exception {
		try {
			browserCore.findElementByXPath(xpath).click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeout) {
				logger.info("Element " + xpath + " is unclickable");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				logger.info("Element " + xpath + " is unclickable, try again");
				clickTheClickable(xpath, startTime, timeout);
			}
		}
	}

	/**
	 * Click an element until it's clickableCSS or timeout
	 * @param CSS
	 * @param startTime
	 * @param timeout
	 *            in millisecond
	 * @throws Exception
	 */
	private void clickTheClickableCSS(String CSS, long startTime, int timeout)
			throws Exception {
		try {
			browserCore.findElement(By.cssSelector(CSS)).click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeout) {
				logger.info("Element " + CSS + " is unclickable");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				logger.info("Element " + CSS + " is unclickable, try again");
				clickTheClickableCSS(CSS, startTime, timeout);
			}
		}
	}
	
	/**
	 * Scroll the page
	 * 
	 * @param direction
	 *            = up or down
	 * @param pixels
	 *            = the pixels to be scrolled up or down
	 */
	public void scroll(String direction, int pixels) {
		String symbol;
		switch (direction) {
		case ("down"): {
			symbol = "-";
		}
		case ("up"): {
			symbol = "+";
		}
			try {
				String setscroll = "window.scrollBy(0," + symbol + pixels + ")";
				javaScriptExecutor.executeScript(setscroll);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Fail to scroll");
			}
			logger.info("Scroll successful");
		}
	}

	/**
	 * Accept or dismiss the alert
	 * 
	 * @param handle
	 *            accept = confirm dismiss = cancel
	 */
	private void alertHandle(String handle) {
		pause(stepInterval);
		try {
			switch (handle) {
			case ("accept"): {
				browserCore.switchTo().alert().accept();
				Thread.sleep(500);
				break;
			}
			case ("dismiss"): {
				browserCore.switchTo().alert().dismiss();
				Thread.sleep(500);
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Alert handle failure");
		}
		logger.info("Alert handle success");
	}
	
	/**
	 * Type text at the page element<br>
	 * Before typing, try to clear existed text
	 * 
	 * @param xpath
	 *            the element's xpath
	 * @param text
	 *            the input text
	 */
	public void type(String xpath, String text) {
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);

		WebElement we = browserCore.findElement(By.xpath(xpath));
		try {
			we.clear();
		} catch (Exception e) {
			logger.warn("Failed to clear text at " + xpath);
		}
		try {
			we.sendKeys(text);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to type " + text + " at " + xpath);
		}

		logger.info("Type " + text + " at " + xpath);
	}

	/**
	 * Hover on the page element
	 * 
	 * @param xpath
	 *            the element's xpath
	 */
	public void mouseOver(String xpath) {
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);
		// First make mouse out of browser
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.mouseMove(0, 0);

		// Then hover
		WebElement we = browserCore.findElement(By.xpath(xpath));

		if (GlobalSettings.browserCoreType == 2) {
			try {
				Actions builder = new Actions(browserCore);
				builder.moveToElement(we).build().perform();
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Failed to mouseover " + xpath);
			}

			logger.info("Mouseover " + xpath);
			return;
		}

		// Firefox and IE require multiple cycles, more than twice, to cause a
		// hovering effect
		if (GlobalSettings.browserCoreType == 1
				|| GlobalSettings.browserCoreType == 3) {
			for (int i = 0; i < 5; i++) {
				Actions builder = new Actions(browserCore);
				builder.moveToElement(we).build().perform();
			}
			logger.info("Mouseover " + xpath);
			return;
		}

		// Selenium doesn't support the Safari browser
		if (GlobalSettings.browserCoreType == 4) {
			Assert.fail("Mouseover is not supported for Safari now");
		}
		Assert.fail("Incorrect browser type");
	}

	/**
	 * Switch window/tab
	 * 
	 * @param windowTitle
	 *            the window/tab's title
	 */
	public void selectWindow(String windowTitle) {
		pause(stepInterval);
		browser.selectWindow(windowTitle);
		logger.info("Switched to window " + windowTitle);
	}

	/**
	 * Enter the iframe
	 * 
	 * @param xpath
	 *            the iframe's xpath
	 */
	public void enterFrame(String xpath) {
		pause(stepInterval);
		browserCore.switchTo().frame(browserCore.findElementByXPath(xpath));
		logger.info("Entered iframe " + xpath);
	}

	/**
	 * Leave the iframe
	 */
	public void leaveFrame() {
		pause(stepInterval);
		browserCore.switchTo().defaultContent();
		logger.info("Left the iframe");
	}

	/**
	 * Refresh the browser
	 */
	public void refresh() {
		pause(stepInterval);
		browserCore.navigate().refresh();
		logger.info("Refreshed");
	}

	/**
	 * Mimic system-level keyboard event
	 * 
	 * @param keyCode
	 *            http://www.cnblogs.com/hsapphire/archive/2009/12/16/1625642.html
	 */
	public void pressKeyboard(int keyCode) {
		pause(stepInterval);
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.keyPress(keyCode); // press key
		rb.delay(100); // delay 100ms
		rb.keyRelease(keyCode); // release key
		logger.info("Pressed key with code " + keyCode);
	}

	/**
	 * Mimic system-level keyboard event with String
	 * 
	 * @param text
	 * 
	 */
	public void inputKeyboard(String text) {
		String cmd = System.getProperty("user.dir")
				+ "\\res\\SeleniumCommand.exe" + " sendKeys " + text;

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.destroy();
		}
		logger.info("Pressed key with string " + text);
	}
	
	/**
	 * Type the keyboard
	 * 
	 * @param keys
	 * 
	 */
	public void typeKeyboard(Keys keys) {
		try {
			Actions action = new Actions(browserCore);
			action.sendKeys(keys);
			logger.info("Pressed key with string " + keys);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	// TODO Mimic system-level mouse event

	/**
	 * Expect some text exist or not on the page<br>
	 * Expect text exist, but not found after timeout => Assert fail<br>
	 * Expect text not exist, but found after timeout => Assert fail
	 * 
	 * @param expectExist
	 *            true or false
	 * @param text
	 *            the expected text
	 * @param timeout
	 *            timeout in millisecond
	 */
	@SuppressWarnings("deprecation")
	public void expectTextExistOrNot(boolean expectExist, final String text,
			int timeout) {
		pause(stepInterval);
		if (expectExist) {
			try {
				new WaitExtension(text).wait("Failed to find text " + text,
						timeout);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Failed to find text " + text);
			}
			logger.info("Found desired text " + text);
		} else {
			if (isTextPresent(text, timeout)) {
				handleFailure("Found undesired text " + text);
			} else {
				logger.error("Not found undesired text " + text);
			}
		}
	}

	/**
	 * Expect an element exist or not on the page<br>
	 * Expect element exist, but not found after timeout => Assert fail<br>
	 * Expect element not exist, but found after timeout => Assert fail<br>
	 * Here <b>exist</b> means <b>visible</b>
	 * 
	 * @param expectExist
	 *            true or false
	 * @param xpath
	 *            the expected element's xpath
	 * @param timeout
	 *            timeout in millisecond
	 */
	public void expectElementExistOrNot(boolean expectExist,
			final String xpath, int timeout) {
		if (expectExist) {
			try {
				new Wait() {
					public boolean until() {
						return isElementPresent(xpath, -1);
					}
				}.wait("Failed to find element " + xpath, timeout);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Failed to find element " + xpath);
			}
			logger.info("Found desired element " + xpath);
		} else {
			if (isElementPresent(xpath, timeout)) {
				handleFailure("Found undesired element " + xpath);
			} else {
				logger.info("Not found undesired element " + xpath);
			}
		}
	}

	/**
	 * Expect an element exist or not on the page<br>
	 * Expect element exist, but not found after timeout => Assert fail<br>
	 * Expect element not exist, but found after timeout => Assert fail<br>
	 * Here <b>exist</b> means <b>visible</b>
	 * 
	 * @param expectExist
	 *            true or false
	 * @param CSS
	 *            the expected element's CSS
	 * @param timeout
	 *            timeout in millisecond
	 */
	public void expectElementExistOrNotCSS(boolean expectExist,
			final String CSS, int timeout) {
		if (expectExist) {
			try {
				new Wait() {
					public boolean until() {
						return isElementPresentCSS(CSS, -1);
					}
				}.wait("Failed to find element " + CSS, timeout);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Failed to find element " + CSS);
			}
			logger.info("Found desired element " + CSS);
		} else {
			if (isElementPresentCSS(CSS, timeout)) {
				handleFailure("Found undesired element " + CSS);
			} else {
				logger.info("Not found undesired element " + CSS);
			}
		}
	}
	
	/**
	 * Check expected element has content or not
	 * 
	 * @param elementContent
	 *            string element content
	 * @param xpath
	 *            the expected element's xpath
	 * @param timeout
	 *            timeout in millisecond
	 */
	public void expectedElementHasContent(String elementContent,
			final String xpath, int timeout) {
		try {
			if (browserCore.findElementByXPath(xpath).isDisplayed()) {
				logger.info("Expected element found");
				if (browserCore.findElementByXPath(xpath)
						.equals(elementContent)) {
					logger.info("Expected element has content of "
							+ elementContent);
				} else {
					logger.error("Expected element doesn't has content of "
							+ elementContent);
				}
			} else {
				logger.info("Expected element not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Is the text present on the page
	 * 
	 * @param text
	 *            the expected text
	 * @param time
	 *            wait a moment (in millisecond) before search text on page;<br>
	 *            minus time means search text at once
	 * @return
	 */
	public boolean isTextPresent(String text, int time) {
		pause(time);
		boolean isPresent = browser.isTextPresent(text);
		if (isPresent) {
			logger.info("Found text " + text);
			return true;
		} else {
			logger.error("Not found text " + text);
			return false;
		}
	}

	/**
	 * Is the element present on the page<br>
	 * Here <b>present</b> means <b>visible</b>
	 * 
	 * @param xpath
	 *            the expected element's xpath
	 * @param time
	 *            wait a moment (in millisecond) before search element on page;<br>
	 *            minus time means search element at once
	 * @return
	 */
	public boolean isElementPresent(String xpath, int time) {
		pause(time);
		boolean isPresent = browser.isElementPresent(xpath)
				&& browserCore.findElementByXPath(xpath).isDisplayed();
		if (isPresent) {
			logger.info("Found element " + xpath);
			return true;
		} else {
			logger.error("Not found element" + xpath);
			return false;
		}
	}

	/**
	 * Is the element present on the page<br>
	 * Here <b>present</b> means <b>visible</b>
	 * 
	 * @param CSS
	 *            the expected element's CSS
	 * @param time
	 *            wait a moment (in millisecond) before search element on page;<br>
	 *            minus time means search element at once
	 * @return
	 */
	public boolean isElementPresentCSS(String CSS, int time) {
		pause(time);
		boolean isPresent = browser.isElementPresent(CSS)
				&& browserCore.findElementByCssSelector(CSS).isDisplayed();
		if (isPresent) {
			logger.info("Found element " + CSS);
			return true;
		} else {
			logger.info("Not found element" + CSS);
			return false;
		}
	}
	
	/**
	 * Pause
	 * 
	 * @param time
	 *            in millisecond
	 */
	public void pause(int time) {
		if (time <= 0) {
			return;
		}
		try {
			Thread.sleep(time);
			logger.info("Pause " + time + " ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handleFailure(String notice) {
		String png = LogTools.screenShot(this);
		String log = notice + " >> capture screenshot at " + png;
		logger.error(log);
		if (GlobalSettings.baseStorageUrl.lastIndexOf("/") == GlobalSettings.baseStorageUrl
				.length()) {
			GlobalSettings.baseStorageUrl = GlobalSettings.baseStorageUrl
					.substring(0, GlobalSettings.baseStorageUrl.length() - 1);
		}
		Reporter.log(log + "<br/><img src=\"" + GlobalSettings.baseStorageUrl
				+ "/" + png + "\" />");
		Assert.fail(log);
	}

	/**
	 * Return text from specified web element.
	 * 
	 * @param xpath
	 * @return
	 */
	public String getText(String xpath) {
		WebElement element = this.getBrowserCore().findElement(By.xpath(xpath));
		return element.getText();
	}

	/**
	 * Check whether table cell has expected text
	 * 
	 * @param expectedText
	 * @param tableXpath
	 *               split xpath of table, please just declare xpath to the layer of "/table"("/table/tr" is added in class)
	 */
	public boolean tableHasExpectedText(String expectedText, String tableXpath) {
		try {
			if(!(browserCore.findElements(By.xpath(tableXpath)).isEmpty())) {
				logger.info("Table is found");
				String xpathrows = tableXpath + "/tbody/tr";
				List<WebElement> elements = browserCore.findElements(By.xpath(xpathrows));
				for(int i =2; i<= elements.size(); i++) {
					String xpathcolumns= xpathrows + "[" + i + "]/td";
					List<WebElement> elements1 = browserCore.findElements(By.xpath(xpathcolumns));
					for(int k = 1; k <= elements1.size(); k++){
						String xpath = xpathcolumns + "[" + k + "]";
						String cellText = browserCore.findElements(By.xpath(xpath)).get(0).getText();
						if (cellText.equals(expectedText)){
							logger.info("Expected Text is found!");
							return true;
						}
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		logger.info("Expected Text is not found!");
		return false;
	}
	
	/**
	 * get table cell element
	 * 
	 * @param expectedText
	 * @param tableXpath
	 *               split xpath of table to layer of .../table/tbody/tr(don't add tr[])
	 */
	public WebElement getCellByTableExpectedText(String expectedText, String tableXpath) {
		try {
			if(!(browserCore.findElements(By.xpath(tableXpath)).isEmpty())) {
				logger.info("Table is found");
				String xpathrows = tableXpath + "/tbody/tr";
				List<WebElement> elements = browserCore.findElements(By.xpath(xpathrows));
				for(int i =2; i<= elements.size(); i++) {
					String xpathcolumns= xpathrows + "[" + i + "]/td";
					List<WebElement> elements1 = browserCore.findElements(By.xpath(xpathcolumns));
					for(int k = 1; k <= elements1.size(); k++){
						String xpath = xpathcolumns + "[" + k + "]";
						String cellText = browserCore.findElements(By.xpath(xpath)).get(0).getText();
						WebElement wantedCell = browserCore.findElements(By.xpath(xpath)).get(0);
						if (cellText.equals(expectedText)){
							logger.info("Expected Text is found!");
							return wantedCell;
						}
						else {
							logger.info("Expected webelement not found");
						}
					}
				}
			}
			else {
				logger.info("Table is not found");
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		return null;
	}
		
	/**
	 * Get total pages number of the table after choosing how many rows is displayed in one page 
	 * 
	 * @param xpath
	 *              the path to find exact table
	 * @param onePageDataNumber
	 *              how many rows of data is showed in one page
	 */
	public int getTablePageNumber(String xpath, String onePageDataNumber) {
		try{
			String dataTotalNumber = this.getBrowserCore().findElementByXPath(xpath).getText();
			int k = dataTotalNumber.indexOf("of");
			int dataNumber = Integer.parseInt(dataTotalNumber.substring(k+3));
			int pageDataNumber = Integer.parseInt(onePageDataNumber);
			int pageNumber = dataNumber/pageDataNumber + 1;
			return pageNumber;
		}catch (Exception e){
			logger.error("读取文件内容出错");
			return 0;
		}
	}
	
	/**
	 * The method for reading TXT file
	 * 
	 * @param filepath
	 */
	public void readTextFile(String filePath) {
		try{
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()){
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine())!= null){
					System.out.println(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定文件");
			}
		}catch (Exception e){
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}
	
	/**
	 * Select an option by visible text from &lt;select&gt; web element.
	 * 
	 * @param xpath
	 * @param option
	 */
	public void select(String xpath, String option) {
		WebElement element = this.browserCore.findElement(By.xpath(xpath));
		Select select = new Select(element);
		select.selectByVisibleText(option);
	}
		
	/**
	 * Get cell by xpath/CSS/link two ways of tagname "th" or "td"
	 * automatically judge the tag of table
	 */
	public WebElement getCell(WebElement Row, int cell) {
		List<WebElement> cells;
		WebElement target = null;
		if (Row.findElements(By.tagName("th")).size() > 0) {
			cells = Row.findElements(By.tagName("th"));
			target = cells.get(cell);
		}
		if (Row.findElements(By.tagName("td")).size() > 0) {
			cells = Row.findElements(By.tagName("td"));
			target = cells.get(cell);
		}
		return target;
	}
	
	/**
	 * Get cell webelement by index
	 */
	public WebElement getCellWebElementByIndex(By by, String tableCellAddress) {
		try{
			WebElement table = browserCore.findElement(by);
			int index = tableCellAddress.trim().indexOf('.');
			int row = Integer.parseInt(tableCellAddress.substring(0, index));
			int cell = Integer.parseInt(tableCellAddress.substring(index + 1));
			List<WebElement> rows = table.findElements(By.tagName("tr"));
			WebElement theRow = rows.get(row);
			WebElement cellwb = getCell(theRow, cell);
			return cellwb;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get column webelements
	 */
	public List<WebElement> getColumnElements(By by, int columnAddress) {
		try{
			WebElement table = browserCore.findElement(by);
			int rowNum = table.findElements(By.tagName("tr")).size();
			List<WebElement> rows = table.findElements(By.tagName("tr"));
			List<WebElement> columnElements = new ArrayList<WebElement>();
			for(int i=0; i< rowNum; i++){
				WebElement theRow = rows.get(i);
				WebElement columnElement = getCell(theRow, columnAddress);
				columnElements.add(columnElement);
			}
			return columnElements;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
		
	/**
	 * Get cell webelement text
	 */
	public String getCellText(By by, String tableCellAddress) {
		try{
			WebElement table = browserCore.findElement(by);
			int index = tableCellAddress.trim().indexOf('.');
			int row = Integer.parseInt(tableCellAddress.substring(0, index));
			int cell = Integer.parseInt(tableCellAddress.substring(index + 1));
			List<WebElement> rows = table.findElements(By.tagName("tr"));
			WebElement theRow = rows.get(row);
			String text = getCell(theRow, cell).getText().trim();
			return text;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get cell webelement prepare for image code auto-authentication
	 * combine with Tesseract-OCR
	 */
	public void screenShotForElement(RemoteWebDriver driver, WebElement element, String path) {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try{
			Point p = element.getLocation();
			int width = element.getSize().getWidth();
			int height = element.getSize().getHeight();
			Rectangle rect =  new Rectangle(width, height);
			BufferedImage img = ImageIO.read(scrFile);
			BufferedImage dest = img.getSubimage(p.getX(), p.getY(), rect.width, rect.height);
			ImageIO.write(dest, "png", scrFile);
			Thread.sleep(1000);
			FileUtils.copyFile(scrFile, new File(path));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Select iframe by index
	 * 
	 * @param index
	 */
	public void selectFrameByIndex(int index) {
		try{
			List<WebElement> a = browserCore.findElementsByTagName("iframe");
			browserCore.switchTo().frame(a.get(index));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Quit iframe and switch to default content
	 * 
	 */
	public void quitFrame() {
		try{
			browserCore.switchTo().defaultContent();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * execute JQuery
	 * 
	 */
	public void executerJQuery() {
		try{
			jQueryFactory jq = new jQueryFactory();
//			jq.setJs(browserCore);
			WebElement we = browserCore.findElementByXPath("");
			jq.query(we).val("123").select();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * javascript executer which must contains one object to be affected
	 * 
	 * @param js
	 * @param by
	 * 
	 */
	public void jsExecuter(By by, String js) {
		try{
			WebElement element = browserCore.findElement(by);
			JavascriptExecutor jsExecutor = (JavascriptExecutor) browserCore;
			jsExecutor.executeScript(js, element);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * javascript executer which must contains one object to be affected
	 * 
	 * @param js
	 * @param by
	 * 
	 */
	public void uploadFile(String filepath) {
		try{
			//put file path in a clipboard
			StringSelection strSel = new StringSelection(filepath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strSel, null);
			//imitate mouse event ENTER/COPY/PASTE
			Robot robot = new Robot();
			pause(500);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			logger.info("Success to upload file: " + filepath);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
