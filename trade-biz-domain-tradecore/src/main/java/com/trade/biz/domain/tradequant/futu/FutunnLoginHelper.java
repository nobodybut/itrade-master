package com.trade.biz.domain.tradequant.futu;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.phantomjs.WebDriverUtils;
import com.trade.common.infrastructure.util.security.SecurityUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.infrastructure.util.yundama.YundamaUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import static com.trade.common.infrastructure.util.httpclient.HttpClientUtils.calErrorLogInfo;

@Component
@Slf4j
public class FutunnLoginHelper {

	/**
	 * 处理富途牛牛用户登录
	 *
	 * @param webDriver
	 * @return
	 */
	public boolean loginFutunn(WebDriver webDriver) {
		try {
			// 并打开登录页面
			webDriver.get(FutunnConsts.FUTUNN_LOGIN_PAGE_URL);
			WebDriverUtils.waitForPageElementLoaded(webDriver, By.id("loginFormWrapper"));

			// 获取用户登录页面用户输入组件、下一步按钮
			WebElement emailInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("email"));
			WebElement passwordInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("password"));
			WebElement submitInput = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".loginFormWrapper .ui-form-submit"));
			if (emailInput == null || passwordInput == null || submitInput == null) {
				log.info("loginPage mobileInput OR validCodeSendButton is EMPTY!");
				return false;
			}

			// 输入用户名、密码，点击登录
			emailInput.click();
			emailInput.clear();
			emailInput.sendKeys(FutunnConsts.FUTUNN_USER_NAME);
			passwordInput.click();
			passwordInput.clear();
			passwordInput.sendKeys(FutunnConsts.FUTUNN_PASS_WORD);
			performTypeImgValidCode(webDriver);
			submitInput.click();

			// 判断是否登录成功
			WebDriverUtils.waitForPageElementLoaded(webDriver, WebDriverUtils.WEBCLIENT_SHORT_TIME_OUT_SECONDS, By.id("userAccountPopup"));
			String loginSuccessPageCode = webDriver.getPageSource();
			String titleCode = CustomStringUtils.substringBetween(loginSuccessPageCode, "<title>", "</title>");
			if (!titleCode.contains("登录")) {
				return true;
			}
		} catch (Exception ex) {
			String errorLogInfo = calErrorLogInfo(FutunnConsts.FUTUNN_LOGIN_PAGE_URL, WebDriverUtils.WEBCLIENT_DEFAULT_TIME_OUT_SECONDS, "", ex.toString());
			if (!Strings.isNullOrEmpty(errorLogInfo)) {
				log.error("Exception:" + errorLogInfo);
			}
		}

		return false;
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
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return false;
	}
}
