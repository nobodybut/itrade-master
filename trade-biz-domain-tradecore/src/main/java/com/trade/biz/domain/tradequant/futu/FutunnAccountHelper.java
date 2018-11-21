package com.trade.biz.domain.tradequant.futu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class FutunnAccountHelper {

	// 依赖注入
	@Resource
	private FutunnLoginHelper futunnLoginHelper;

	/**
	 * 获取富途账户剩余资金金额
	 *
	 * @return
	 */
	public int getAccountTotalAmount() {
		return 90000000;

//		WebDriver webDriver = null;
//
//		try {
//			// 获取 chromeDriver 实例
//			webDriver = WebDriverUtils.getChromeWebDriver(FutunnConsts.USE_LOCAL_CHROME_DRIVER);
//
//			// 处理用户登录
//			boolean loginSuccess = futunnLoginHelper.loginFutunn(webDriver);
//			if (!loginSuccess) {
//				log.error("futunn login FAIL!");
//				return 0;
//			}
//
//			// 打开股票模拟交易页面
////			webDriver.get(FutunnConsts.FUTUNN_US_TRADE_URL_TMPL + stock.getCode());
////			WebDriverUtils.waitForPageElementLoaded(webDriver, By.id("stockCodeInput"));
////			WebDriverUtils.waitForPageElementAttribute(webDriver, By.cssSelector(".tradeForm01 .selectFloatBox"), "style", "display:none");
//
//			return 90000;
//
//		} catch (Exception ex) {
//			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
//		} finally {
//			WebDriverUtils.webDriverQuit(webDriver);
//		}
//
//		return 0;
	}
}
