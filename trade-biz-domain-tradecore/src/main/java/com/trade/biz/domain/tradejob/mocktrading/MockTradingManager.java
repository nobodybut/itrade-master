package com.trade.biz.domain.tradejob.mocktrading;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.business.conf.PropertiesUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.phantomjs.WebDriverUtils;
import com.trade.common.infrastructure.util.security.SecurityUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.infrastructure.util.yundama.YundamaUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import static com.trade.common.infrastructure.util.httpclient.HttpClientUtils.calErrorLogInfo;

@Component
@Slf4j
public class MockTradingManager {

	private static final String LOGIN_PAGE_URL = "https://passport.futu5.com/?target=https%3A%2F%2Fwww.futunn.com%2Ftrade%2Fus-trade";
//	private static final String US_TRADE_URL = "https://www.futunn.com/trade/us-trade";

	public void execute() {
		long startMills = System.currentTimeMillis();
		String proxyServer = ""; // String.format("8^%s:%s", "118.252.71.239", "7524"); // proxyServer_LocalCacheProxy.getDefaultProxyServer(ProxyServerTypeEnum.TOP);

		WebDriver webDriver = null;
		String remoteChromeIP = getRemoteChromeIP();

		try {
			// 获取用户登录首页代码、用户输入组件、下一步按钮
			webDriver = WebDriverUtils.getRemoteChromeWebDriver(remoteChromeIP, WebDriverUtils.WEBCLIENT_DEFAULT_TIME_OUT_MILLS, proxyServer);
			webDriver.get(LOGIN_PAGE_URL);
			WebDriverUtils.waitForPageLoaded(webDriver, WebDriverUtils.WEBCLIENT_DEFAULT_TIME_OUT_SECONDS, true, "loginFormWrapper");

			// 获取用户登录首页代码、用户输入组件、下一步按钮
			WebElement emailInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("email"));
			WebElement passwordInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("password"));
			WebElement submitInput = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".loginFormWrapper .ui-form-submit"));
			if (emailInput == null || passwordInput == null || submitInput == null) {
				log.info("loginPage mobileInput OR validCodeSendButton is EMPTY! proxyServer={}", proxyServer);
				return;
			}

			emailInput.click();
			emailInput.clear();
			emailInput.sendKeys("18601018270");
			passwordInput.click();
			passwordInput.clear();
			passwordInput.sendKeys("2384Wish");
			performTypeImgValidCode(webDriver);
			submitInput.click();

			webDriver.get("https://www.futunn.com/trade/us-trade#us/BABA");
			WebDriverUtils.waitForPageLoaded(webDriver, WebDriverUtils.WEBCLIENT_DEFAULT_TIME_OUT_SECONDS, true, "stockCodeInput");

			WebElement stockCodeInput = WebDriverUtils.getSingleWebElement(webDriver, By.id("stockCodeInput"));
			WebElement priceInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("price"));
			WebElement qtyInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("qty_str"));
			WebElement buyButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".btnLi01 .btn01"));
			WebElement sellButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".btnLi01 .btn02"));
			if (stockCodeInput == null || priceInput == null || qtyInput == null || buyButton == null || sellButton == null) {
				log.error("stockPage Element ERROR!");
			}

			stockCodeInput.click();
			stockCodeInput.sendKeys("BABA");

			priceInput.click();
			priceInput.clear();
			priceInput.sendKeys("128.50");

			qtyInput.click();
			qtyInput.clear();
			qtyInput.sendKeys("20");

			buyButton.click();

			WebElement submitButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector("#confirmDialog .btn01"));
			submitButton.click();

			boolean success = true;
		} catch (Exception ex) {
			String errorLogInfo = calErrorLogInfo(LOGIN_PAGE_URL, WebDriverUtils.WEBCLIENT_DEFAULT_TIME_OUT_MILLS, proxyServer, ex.toString());
			if (!Strings.isNullOrEmpty(errorLogInfo)) {
				log.error("Exception:" + errorLogInfo);
			}
		} finally {
			if (webDriver != null) {
				webDriver.quit();
			}
		}
	}

	private String getRemoteChromeIP() {
		String remoteChromeIP = "10.26.171.131";
		if (PropertiesUtils.getBooleanValue("isDebug")) {
			remoteChromeIP = "60.205.197.205";
		}
		return remoteChromeIP;
	}


	/**
	 * 解析处理登录过程中的验证码
	 *
	 * @param webDriver
	 * @return
	 */
	private boolean performTypeImgValidCode(WebDriver webDriver) {
		try {
			// 处理登录过程中的验证码输入
			String loginPageCode = webDriver.getPageSource();
			String usefulCode = CustomStringUtils.substringBetween(loginPageCode, "class=\"ui-input-wrapper ui-content-captcha\"", "/div>");
			if (usefulCode.contains("style=\"display:none\"")) {
				return true;
			}
			String validCodeImgUrl = CustomStringUtils.substringBetween(usefulCode, "class=\"ui-captcha u-captcha\"", ">", "src=\"", "\"");
			if (Strings.isNullOrEmpty(validCodeImgUrl)) {
				return true;
			}

			String validCode = YundamaUtils.getIdentifyCodeValue(validCodeImgUrl);
			if (!Strings.isNullOrEmpty(validCode)) {
				WebElement captchaInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("captcha"));
				if (captchaInput == null) {
					log.info("performImgValidCode FAIL! captchaInput is NULL");
					return false;
				} else {
					if (validCode.contains("\\u")) {
						validCode = SecurityUtils.decode_unescapeJava(validCode);
					}
					captchaInput.sendKeys(validCode);
					return true;
				}
			}

//				// 处理验证码错误问题
//				loginPageCode = loginPage.asXml();
//				if (loginPageCode.contains("验证码错误")) {
//					log.info("performImgValidCode FAIL! validCode is ERROR");
//					return false;
//				}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return false;
	}
}
