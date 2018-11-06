package com.trade.common.infrastructure.util.phantomjs;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.business.conf.PropertiesUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class WebDriverUtils {

	// Logger
	protected static final Logger s_logger = LoggerFactory.getLogger(WebDriverUtils.class);

	// 相关变量
	public static final int WEBCLIENT_DEFAULT_TIME_OUT_SECONDS = 30;
	public static final int WEBCLIENT_SHORT_TIME_OUT_SECONDS = 3;
	public static ArrayList<String> cliArgsCap = new ArrayList<String>();
	public static String SELENIUM_URL_TEMP = "http://%s:4444/wd/hub";
	private static final Map<String, String> hotelRealtimeHubMap = new HashMap<String, String>() {
		{
			// cache 1-16
			put("10.81.11.235", "10.163.145.43");
			put("10.81.10.211", "10.163.144.149");
			put("10.81.9.22", "10.81.10.238");
			put("10.81.11.232", "10.163.145.55");

			// crawl 1-4
			put("10.163.145.43", "10.163.145.43");
			put("10.163.144.149", "10.163.144.149");
			put("10.81.10.238", "10.81.10.238");
			put("10.163.145.55", "10.163.145.55");
		}
	};

	static {
		cliArgsCap.add("--web-security=false");
		cliArgsCap.add("--ssl-protocol=any");
		cliArgsCap.add("--ignore-ssl-errors=true");
	}

	/**
	 * 获取本地或远程的 chromeDriver 实例（默认配置）
	 *
	 * @param useLocalChromeDriver
	 * @return
	 */
	public static WebDriver getChromeWebDriver(boolean useLocalChromeDriver) {
		return getChromeWebDriver(useLocalChromeDriver, WEBCLIENT_DEFAULT_TIME_OUT_SECONDS, "", false);
	}

	/**
	 * 获取本地或远程的 chromeDriver 实例（参数化配置）
	 *
	 * @param useLocalChromeDriver
	 * @param timeOut
	 * @param proxyServer
	 * @param isMobileBrowser
	 * @return
	 */
	public static WebDriver getChromeWebDriver(boolean useLocalChromeDriver, int timeOut, String proxyServer, boolean isMobileBrowser) {
		if (useLocalChromeDriver) {
			return getLocalChromeWebDriver(timeOut, proxyServer, isMobileBrowser);
		} else {
			return getRemoteChromeWebDriver(WebDriverUtils.getRemoteChromeIP(), timeOut, proxyServer, isMobileBrowser);
		}
	}

	/**
	 * 获取远程 chromeDriver 实例（WEB浏览器）
	 *
	 * @param seleniumIP
	 * @param timeOut
	 * @param proxyServer
	 * @return
	 */
	public static WebDriver getRemoteChromeWebDriver(String seleniumIP, int timeOut, String proxyServer) {
		return getRemoteChromeWebDriver(seleniumIP, timeOut, proxyServer, false);
	}

	/**
	 * 获取远程 chromeDriver 实例（WEB浏览器 / APP浏览器）
	 *
	 * @param seleniumIP
	 * @param timeOut
	 * @param proxyServer
	 * @param isMobileBrowser
	 * @return
	 */
	public static WebDriver getRemoteChromeWebDriver(String seleniumIP, int timeOut, String proxyServer, boolean isMobileBrowser) {
		if (!seleniumIP.equals("localhost") && hotelRealtimeHubMap.containsKey(seleniumIP)) {
			seleniumIP = hotelRealtimeHubMap.get(seleniumIP);
		}

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox");
		if (isMobileBrowser) {
			chromeOptions.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
		} else {
			chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
		}
		chromeOptions.addArguments("--web-security=false");
		chromeOptions.addArguments("--ssl-protocol=any");
		chromeOptions.addArguments("--ignore-ssl-errors=true");
		chromeOptions.addArguments("start-maximized");
		chromeOptions.setHeadless(true);
		chromeOptions.setAcceptInsecureCerts(true);

		//设置代理
		setProxy(proxyServer, chromeOptions);

		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL(String.format(SELENIUM_URL_TEMP, seleniumIP)), chromeOptions);
		} catch (Exception ex) {
			s_logger.error(String.format("MalformedURLException:timeOut=%s ,proxy=%s ,exString=%s", timeOut, proxyServer, ex.toString()));
		}

		//设置超时时间
		driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		//设置窗口大小,防止某些按钮点不到
		driver.manage().window().setSize(new Dimension(1920, 1080));

		return driver;
	}

	/**
	 * 获取本地 chromeDriver 实例（WEB浏览器）
	 *
	 * @param timeOut
	 * @param proxyServer
	 * @return
	 */
	public static WebDriver getLocalChromeWebDriver(int timeOut, String proxyServer) {
		return getLocalChromeWebDriver(timeOut, proxyServer, false);
	}

	/**
	 * 获取本地 chromeDriver 实例（WEB浏览器 / APP浏览器）
	 *
	 * @param timeOut
	 * @param proxyServer
	 * @param isMobileBrowser
	 * @return
	 */
	public static WebDriver getLocalChromeWebDriver(int timeOut, String proxyServer, boolean isMobileBrowser) {
		System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox");
		if (isMobileBrowser) {
			chromeOptions.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
		} else {
			chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
		}
		chromeOptions.addArguments("--web-security=false");
		chromeOptions.addArguments("--ssl-protocol=any");
		chromeOptions.addArguments("--ignore-ssl-errors=true");
		chromeOptions.addArguments("start-maximized");
		chromeOptions.setHeadless(false);
		chromeOptions.setAcceptInsecureCerts(true);

		//设置代理
		setProxy(proxyServer, chromeOptions);

		WebDriver driver = null;
		try {
			driver = new ChromeDriver(chromeOptions);
		} catch (Exception ex) {
			s_logger.error(String.format("MalformedURLException:timeOut=%s ,proxy=%s ,exString=%s", timeOut, proxyServer, ex.toString()));
		}

		//设置超时时间
		driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		//设置窗口大小,防止某些按钮点不到
		driver.manage().window().setSize(new Dimension(1920, 1080));

		return driver;
	}

	/**
	 * 设置代理服务器参数
	 *
	 * @param proxyServer
	 * @param chromeOptions
	 */
	private static void setProxy(String proxyServer, ChromeOptions chromeOptions) {
		if (!Strings.isNullOrEmpty(proxyServer)) {
			Proxy proxy = new Proxy();
			if (proxyServer.contains("^")) {
				proxyServer = proxyServer.substring(proxyServer.indexOf("^") + 1);
			}

			proxy.setHttpProxy(proxyServer);
			chromeOptions.setProxy(proxy);
		}
	}

	/**
	 * 等待页面某个元素加载完成（使用默认超时时间）
	 *
	 * @param webDriver
	 * @param by
	 * @return
	 */
	public static boolean waitForPageElementLoaded(WebDriver webDriver, By by) {
		return waitForPageElementLoaded(webDriver, WEBCLIENT_DEFAULT_TIME_OUT_SECONDS, by);
	}

	/**
	 * 等待页面某个元素加载完成（使用指定超时时间）
	 *
	 * @param webDriver
	 * @param timeOut
	 * @param by
	 * @return
	 */
	public static boolean waitForPageElementLoaded(WebDriver webDriver, int timeOut, By by) {
		final boolean[] result = {false};

		try {
			WebDriverWait waitForData = new WebDriverWait(webDriver, timeOut);
			waitForData.until(new Function<WebDriver, Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					List<WebElement> webElements = webDriver.findElements(by);
					if (webElements != null && webElements.size() > 0) {
						result[0] = true;
						return true;
					}

					return false;
				}
			});
		} catch (Exception ex) {//如果timeOut秒还没出现结果,则会抛出异常
			s_logger.error(String.format("waitForPageElementLoaded FAIL!!! by=%s, ex=%s", by.toString(), ex.toString()));
		}

		return result[0];
	}

	/**
	 * 等待页面某个元素加载完成，并包含特定的属性（使用默认超时时间）
	 *
	 * @param webDriver
	 * @param by
	 * @param attrName
	 * @param attrValue
	 * @return
	 */
	public static boolean waitForPageElementAttribute(WebDriver webDriver, By by, String attrName, String attrValue) {
		return waitForPageElementAttribute(webDriver, WEBCLIENT_DEFAULT_TIME_OUT_SECONDS, by, attrName, attrValue);
	}

	/**
	 * 等待页面某个元素加载完成，并包含特定的属性（使用指定超时时间）
	 *
	 * @param webDriver
	 * @param timeOut
	 * @param by
	 * @return
	 */
	public static boolean waitForPageElementAttribute(WebDriver webDriver, int timeOut, By by, String attrName, String attrValue) {
		final boolean[] result = {false};

		try {
			WebDriverWait waitForData = new WebDriverWait(webDriver, timeOut);
			waitForData.until(new Function<WebDriver, Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					List<WebElement> webElements = webDriver.findElements(by);
					if (webElements != null && webElements.size() > 0) {
						String attributeValue = webElements.get(0).getAttribute(attrName);
						if (!Strings.isNullOrEmpty(attributeValue) && attributeValue.replace(" ", "").contains(attrValue)) {
							result[0] = true;
							return true;
						}
					}

					return false;
				}
			});
		} catch (Exception ex) {//如果timeOut秒还没出现结果,则会抛出异常
			s_logger.error(String.format("waitForPageElementLoaded FAIL!!! by=%s, ex=%s", by.toString(), ex.toString()));
		}

		return result[0];
	}

	/**
	 * 等待 转向到某个 URL 是否完成
	 *
	 * @param webDriver
	 * @param timeOut
	 * @param pageUrl
	 * @return
	 */
	public static boolean waitForPageUrl(WebDriver webDriver, int timeOut, String pageUrl) {
		final boolean[] result = {false};

		try {
			WebDriverWait waitForData = new WebDriverWait(webDriver, timeOut);
			waitForData.until(new Function<WebDriver, Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					result[0] = webDriver.getCurrentUrl().contains(pageUrl);
					return result[0];
				}
			});
		} catch (Exception ex) {//如果timeOut秒还没出现结果,则会抛出异常
			s_logger.error(String.format("waitForPageElementLoaded failed!!! pageUrl=%s, ex=%s", pageUrl, ex.toString()));
		}

		return result[0];
	}

	/**
	 * 等待 某个元素加载完成 或者等待5秒
	 *
	 * @param timeOut
	 * @param driver
	 * @param css
	 * @param attribute
	 * @param value
	 */
	public static void waitForLoading(int timeOut, WebDriver driver, String css, String attribute, String value) {
		long start = System.currentTimeMillis();
		while (true) {
			synchronized (driver) {
				try {
					driver.wait(100);
				} catch (InterruptedException e) {
					s_logger.error(String.format("driver.wait Exception,exString=%s", e.toString()));
				}
			}
			long now = System.currentTimeMillis();
			List<WebElement> divList_arrow = driver.findElements(By.cssSelector(css));
			if ((now - start > timeOut) || (divList_arrow.size() > 0 && divList_arrow.get(0).getAttribute(attribute).equals(value))) {
				s_logger.info(String.format("driver.wait time=%s,size=%s", now - start, divList_arrow.size()));
				break;
			}
		}
	}

	/**
	 * 等待 某个元素加载完成 或者等待5秒
	 *
	 * @param timeOut
	 * @param driver
	 * @param css
	 */
	public static void waitForLoadingElementAppear(int timeOut, WebDriver driver, String css) {
		long start = System.currentTimeMillis();
		while (true) {
			synchronized (driver) {
				try {
					driver.wait(100);
				} catch (InterruptedException e) {
					s_logger.error(String.format("driver.wait Exception,exString=%s", e.toString()));
				}
			}
			long now = System.currentTimeMillis();
			List<WebElement> divList_arrow = driver.findElements(By.cssSelector(css));
			if ((now - start > timeOut) || (divList_arrow.size() > 0)) {
				s_logger.info(String.format("driver.wait time=%s,size=%s", now - start, divList_arrow.size()));
				break;
			}
		}
	}

	/**
	 * 获取页面上具有唯一标识的 webElement 节点
	 *
	 * @param webDriver
	 * @param by
	 * @return
	 */
	public static WebElement getSingleWebElement(WebDriver webDriver, By by) {
		List<WebElement> webElements = webDriver.findElements(by);
		if (webElements != null && webElements.size() > 0) {
			return webElements.get(0);
		}

		return null;
	}

	public static boolean tooManyNoPrice(List<Boolean> nopriceLists) {
		if (nopriceLists.size() > 5) {
			for (int i = nopriceLists.size() - 1; i > nopriceLists.size() - 6; i--) {
				if (!nopriceLists.get(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static boolean checkPriceLoaded(WebDriver driver) {
		String pageCode = driver.getPageSource();

		if (pageCode.contains("最低报价")) {
			return false;
		}
		return true;
	}

	public static void clickNextPage(WebDriver driver) {
		WebElement page = driver.findElement(By.id("J_page"));

		List<WebElement> pageWebElement = page.findElements(By.tagName("a"));
		for (WebElement webElement : pageWebElement) {
			if (webElement.getText().equals("下一页")) {
				String url = webElement.getAttribute("href");
				System.out.println(url);
				webElement.click();
			}
		}
	}

	public static boolean hasNextPage(String pageCode) {
		boolean result = false;

		if (pageCode.contains("下一页")) {
			result = true;
		}

		return result;
	}

	/**
	 * 等待 某个元素加载完成 或者等待5秒
	 *
	 * @param driver
	 */
	public static void waitForLoading(int timeOut, WebDriver driver) {
		long start = System.currentTimeMillis();
		while (true) {
			synchronized (driver) {
				try {
					driver.wait(5000);
				} catch (InterruptedException e) {
					s_logger.error(String.format("driver.wait Exception,exString=%s", e.toString()));
				}
			}
			long now = System.currentTimeMillis();

			if ((now - start > timeOut) || (checkPriceLoaded(driver))) {
				s_logger.info(String.format("driver.wait time=%s", now - start));
				break;
			}
		}
	}

	/**
	 * webDriver 退出
	 *
	 * @param webDriver
	 */
	public static void webDriverQuit(WebDriver webDriver) {
		try {
			if (webDriver != null) {
				for (String handle : webDriver.getWindowHandles()) {
					webDriver.switchTo().window(handle);
					webDriver.close();
				}
				webDriver.quit();
			}
		} catch (Exception e) {
			s_logger.error(String.format("webDriverQuit Exception,exString=%s", e.toString()));
		}
	}

	/**
	 * 获取远程 chromeDriver IP
	 *
	 * @return
	 */
	public static String getRemoteChromeIP() {
		String remoteChromeIP = "10.26.171.131";
		if (PropertiesUtils.getBooleanValue("isDebug")) {
			remoteChromeIP = "localhost";
		}
		return remoteChromeIP;
	}
}