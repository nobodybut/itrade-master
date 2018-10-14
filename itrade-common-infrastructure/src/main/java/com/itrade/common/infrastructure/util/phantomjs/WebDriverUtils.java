package com.itrade.common.infrastructure.util.phantomjs;

import com.google.common.base.Strings;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WebDriverUtils {

	// Logger
	protected static final Logger s_logger = LoggerFactory.getLogger(WebDriverUtils.class);

	// 相关变量
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

	public static WebDriver getRemoteChromeWebDriver(String seleniumIP, int timeOut, String proxyServer) {
		if (!seleniumIP.equals("localhost") && hotelRealtimeHubMap.containsKey(seleniumIP)) {
			seleniumIP = hotelRealtimeHubMap.get(seleniumIP);
		}

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
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
		driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		//设置窗口大小,防止某些按钮点不到
		driver.manage().window().setSize(new Dimension(1920, 1080));

		return driver;
	}

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

	public static WebDriver getLocalChromeWebDriver(int timeOut, String proxyServer) {
		System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
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
			driver = new ChromeDriver(chromeOptions);
		} catch (Exception ex) {
			s_logger.error(String.format("MalformedURLException:timeOut=%s ,proxy=%s ,exString=%s", timeOut, proxyServer, ex.toString()));
		}

		//设置超时时间
		driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		//设置窗口大小,防止某些按钮点不到
		driver.manage().window().setSize(new Dimension(1920, 1080));

		return driver;
	}

//	public static WebDriver getLocalPhantomJsDriver(int timeOut, String proxyStr) {
//		DesiredCapabilities sCaps = new DesiredCapabilities();
//		sCaps.setJavascriptEnabled(true);
//		sCaps.setCapability("takesScreenshot", false);
//		sCaps.setCapability("load-images", false);
//		//设置代理
//		if (!Strings.isNullOrEmpty(proxyStr)) {
//			Proxy proxy = new Proxy();
//			proxy.setHttpProxy(proxyStr);
//			sCaps.setCapability(CapabilityType.PROXY, proxy);
//		}
//
//		sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/opt/phantomjs/phantomjs");
//		sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
//
//		WebDriver driver = null;
//		try {
//			driver = new PhantomJSDriver(sCaps);
//		} catch (Exception ex) {
//			s_logger.error(String.format("MalformedURLException:timeOut=%s ,proxy=%s ,exString=%s", timeOut, proxyStr, ex.toString()));
//		}
//
//		//设置超时时间
//		driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
//
//		return driver;
//	}

//	public static WebDriver getRemoteWebDriver(String seleniumIP, int timeOut, String proxyServer) {
//		if (!seleniumIP.equals("localhost") && hotelRealtimeHubMap.containsKey(seleniumIP)) {
//			seleniumIP = hotelRealtimeHubMap.get(seleniumIP);
//		}
//
//		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
//		capabilities.setBrowserName("phantomjs");
//
//		//设置代理
//		if (!Strings.isNullOrEmpty(proxyServer)) {
//			KeyValuePair<ProxyServerSupplierEnum, String> proxyServerKV = ProxyServerSupplierUtils.parseProxyServerKV(proxyServer);
//			ProxyServerSupplierEnum proxyServerSupplierEnum = proxyServerKV.getKey();
//			String curProxyServer = proxyServerKV.getValue();
//
//			Proxy proxy = new Proxy();
//			proxy.setHttpProxy(curProxyServer);
//			capabilities.setCapability(CapabilityType.PROXY, proxy);
//		}
//		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
//
//		//启用javascript
//		capabilities.setJavascriptEnabled(true);
//		capabilities.setCapability("takesScreenshot", false);
//		//禁用截屏
//		//capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
//		//禁止加载图片
//		//capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", false);
//		capabilities.setCapability("load-images", false);
//
//		WebDriver driver = null;
//		try {
//			driver = new RemoteWebDriver(new URL(String.format(SELENIUM_URL_TEMP, seleniumIP)), capabilities);
//		} catch (MalformedURLException ex) {
//			s_logger.error(String.format("MalformedURLException:timeOut=%s ,proxy=%s ,exString=%s", timeOut, proxyServer, ex.toString()));
//		}
//
//		//设置超时时间 .implicitlyWait(timeOut, TimeUnit.MILLISECONDS)
//		driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
//
//		//设置窗口大小,防止某些按钮点不到
//		driver.manage().window().setSize(new Dimension(1920, 1080));
//
//		return driver;
//	}

	/**
	 * 等待 某个元素加载完成 或者等待5秒
	 *
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
			for (String handle : webDriver.getWindowHandles()) {
				webDriver.switchTo().window(handle);
				webDriver.close();
			}

			webDriver.quit();
		} catch (Exception e) {
			s_logger.error(String.format("webDriverQuit Exception,exString=%s", e.toString()));
		}
	}
}