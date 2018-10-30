//package com.trade.biz.domain.tradejob.mocktrading;
//
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.*;
//import com.google.common.base.Strings;
//import com.trade.common.infrastructure.util.logger.LogInfoUtils;
//import com.trade.common.infrastructure.util.phantomjs.WebClientUtils;
//import com.trade.common.infrastructure.util.security.SecurityUtils;
//import com.trade.common.infrastructure.util.string.CustomStringUtils;
//import com.trade.common.infrastructure.util.yundama.YundamaUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@Slf4j
//public class MockTradingManagerBak {
//
//	private static final String US_TRADE_URL = "https://www.futunn.com/trade/us-trade";
//
//	public void execute() {
//		long totalStartMills = System.currentTimeMillis();
//		String proxyServer = ""; // String.format("8^%s:%s", "118.252.71.239", "7524"); // proxyServer_LocalCacheProxy.getDefaultProxyServer(ProxyServerTypeEnum.TOP);
//
//		try (final WebClient webClient = new WebClient(WebClientUtils.buildChromeBrowser(false))) {
//			// 设置 webClient 必要参数
//			WebClientUtils.setWebClientParameters(webClient, WebClientUtils.WEBCLIENT_DEFAULT_REQUEST_TIME_OUT, proxyServer);
//
//			// 获取用户注册首页代码、用户输入组件、下一步按钮
//			long startMills = System.currentTimeMillis();
//			HtmlPage loginPage = webClient.getPage("https://passport.futu5.com/?target=https%3A%2F%2Fwww.futunn.com%2Ftrade%2Fus-trade");
//			long spendTimeLoginPage = System.currentTimeMillis() - startMills;
//
//			startMills = System.currentTimeMillis();
//			HtmlTextInput emailInput = loginPage.getElementByName("email");
//			List<DomElement> passwordInputs = loginPage.getElementsByName("password");
//			HtmlSubmitInput submitInput = loginPage.querySelector(".loginFormWrapper .ui-form-submit");
//			long spendTimeFindInput = System.currentTimeMillis() - startMills;
//
//			startMills = System.currentTimeMillis();
//			if (emailInput == null || submitInput == null) {
//				log.info("loginPage mobileInput OR validCodeSendButton is EMPTY! proxyServer={}", proxyServer);
//				return;
//			}
//
//			emailInput.type("18601018270");
//			for (DomElement passwordInput : passwordInputs) {
//				((HtmlPasswordInput) passwordInput).type("2384Wish");
//			}
//			long spendTimeTypeAll = System.currentTimeMillis() - startMills;
//
//			startMills = System.currentTimeMillis();
//			performTypeImgValidCode(loginPage);
//			long spendTimeValidCode = System.currentTimeMillis() - startMills;
//
//			startMills = System.currentTimeMillis();
//			submitInput.click();
//			long spendTimeSubmit = System.currentTimeMillis() - startMills;
//
//			HtmlPage stockPage = webClient.getPage(String.format("https://www.futunn.com/trade?security_id=%s", "210182"));
//			HtmlTextInput stockCodeInput = (HtmlTextInput) stockPage.getElementById("stockCodeInput");
//			HtmlTextInput priceInput = stockPage.getElementByName("price");
//			stockCodeInput.type("BABA");
//			stockPage = priceInput.click();
//			stockPage = webClient.getPage("https://www.futunn.com/trade/us-trade#us/BABA");
//
//			priceInput = stockPage.getElementByName("price");
//			HtmlTextInput qtyInput = stockPage.getElementByName("qty_str");
//			HtmlButton buyButton = stockPage.querySelector(".btnLi01 .btn01");
//			HtmlButton sellButton = stockPage.querySelector(".btnLi01 .btn02");
//			if (stockCodeInput == null || priceInput == null || qtyInput == null || buyButton == null || sellButton == null) {
//				log.error("stockPage Element ERROR!");
//			}
//
//			priceInput.type("134.76");
//			qtyInput.type("20");
//			HtmlPage buyStockPage = buyButton.click();
//			HtmlButton submitButton = buyStockPage.querySelector("#confirmDialog .btn01");
//			HtmlPage successPage = submitButton.click();
//
//			boolean success = true;
//
//		} catch (Exception ex) {
//			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//			String logData = String.format("proxyServer=%s", proxyServer);
//			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
//		}
//	}
//
//	/**
//	 * 处理注册过程中的验证码输入
//	 *
//	 * @param loginPage
//	 * @return
//	 */
//	private boolean performTypeImgValidCode(HtmlPage loginPage) {
//		try {
//			// 处理注册过程中的验证码输入
//			String loginPageCode = loginPage.asXml();
////			String usefulCode = CustomStringUtils.substringBetween(loginPageCode, "id=\"loginFormWrapper\"", "/form>", "ui-content-captcha", "/li>");
////			if (usefulCode.contains("style=\"display:none\"") || usefulCode.contains("style=\"display: none\"")) {
////				return true;
////			} else {
//			String validCodeImgUrl = CustomStringUtils.substringBetween(loginPageCode, "class=\"ui-captcha u-captcha\"", ">", "src=\"", "\"");
//			if (!Strings.isNullOrEmpty(validCodeImgUrl)) {
//				String validCode = YundamaUtils.getIdentifyCodeValue(validCodeImgUrl);
//				if (!Strings.isNullOrEmpty(validCode)) {
//					HtmlTextInput captchaInput = loginPage.getElementByName("captcha");
//					if (captchaInput == null) {
//						log.info("performImgValidCode FAIL! captchaInput is NULL");
//						return false;
//					} else {
//						if (validCode.contains("\\u")) {
//							validCode = SecurityUtils.decode_unescapeJava(validCode);
//						}
//						captchaInput.type(validCode);
//						return true;
//					}
//				}
//
//				// 处理验证码错误问题
//				loginPageCode = loginPage.asXml();
//				if (loginPageCode.contains("验证码错误")) {
//					log.info("performImgValidCode FAIL! validCode is ERROR");
//					return false;
//				}
//			} else {
//				return true;
//			}
////			}
//		} catch (Exception ex) {
//			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
//		}
//
//		return false;
//	}
//}
