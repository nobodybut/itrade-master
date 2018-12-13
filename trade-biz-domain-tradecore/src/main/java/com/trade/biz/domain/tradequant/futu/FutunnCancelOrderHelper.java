package com.trade.biz.domain.tradequant.futu;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.phantomjs.WebDriverUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import com.trade.model.tradecore.enums.TradeSideEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FutunnCancelOrderHelper {

	// 依赖注入
	@Resource
	private FutunnLoginHelper futunnLoginHelper;

	/**
	 * 处理富途股票模拟交易（模拟交易目前只支持买入/卖出）
	 *
	 * @param stockID
	 * @param stockCode
	 * @param orderID
	 * @return
	 */
	public boolean cancelQuantOrder(long stockID, String stockCode, String orderID) {
		WebDriver webDriver = null;

		try {
			// 获取 chromeDriver 实例
			webDriver = WebDriverUtils.getChromeWebDriver(FutunnConsts.USE_LOCAL_CHROME_DRIVER);

			// 处理用户登录
			boolean loginSuccess = futunnLoginHelper.loginFutunn(webDriver);
			if (!loginSuccess) {
				log.error("futunn login FAIL!");
				return false;
			}

			// 打开股票模拟交易页面
			webDriver.get(FutunnConsts.FUTUNN_US_TRADE_URL_TMPL + stockCode);
			WebDriverUtils.waitForPageElementLoaded(webDriver, By.id("orderPart"));

			// 查找模拟交易页面上的输入控件
			List<WebElement> trElements = webDriver.findElements(By.cssSelector("#orderPart .tr01"));
			for (WebElement trElement : trElements) {
				boolean isToSellOrder = false;
				List<WebElement> tdElements = trElement.findElements(By.tagName("td"));
				for (WebElement tdElement : tdElements) {
					String dataOrderID = tdElement.getAttribute("data-order-id");
					if (dataOrderID.equals(orderID)) {
						isToSellOrder = true;
						break;
					}
				}

				if (isToSellOrder) {

				}
			}

			WebElement priceInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("price"));
			WebElement qtyInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("qty_str"));
			WebElement buyButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".btnLi01 .btn01"));
			WebElement sellButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".btnLi01 .btn02"));
			if (priceInput == null || qtyInput == null || buyButton == null || sellButton == null) {
				log.error("tradeInputs Elements ERROR!");
				return false;
			}

			// 输入股票代码、买入/卖出价格、数量
//			TimeUnit.MILLISECONDS.sleep(1000);
//			priceInput.click();
//			priceInput.clear();
//			priceInput.sendKeys(String.valueOf(price));
//			TimeUnit.MILLISECONDS.sleep(1000);
//			qtyInput.click();
//			qtyInput.clear();
//			qtyInput.sendKeys(String.valueOf(volume));

//			// 根据交易类型，点击对应的买入/卖出按钮
//			if (tradeSide == TradeSideEnum.BUY) {
//				buyButton.click();
//			} else if (tradeSide == TradeSideEnum.SELL) {
//				sellButton.click();
//			}

			// 处理购买确认框
			WebElement submitButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector("#confirmDialog .btn01"));
			if (submitButton == null) {
				log.error("submitButtonElement ERROR!");
				return false;
			}
			TimeUnit.MILLISECONDS.sleep(500);
			submitButton.click();

			// 查看当前交易提示信息是否成功
			WebDriverUtils.waitForPageElementLoaded(webDriver, By.cssSelector(".ui-dialog-messageTitle"));
			WebElement messageTitleElement = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".ui-dialog-messageTitle"));
			WebElement messageBodyElement = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".ui-dialog-messageBody"));
			if (messageTitleElement == null || messageBodyElement == null) {
				log.error("dialog messageElement ERROR!");
				return false;
			}
			String messageTitle = messageTitleElement.getText();
			if (messageTitle.contains("下单失败") || messageTitle.contains("失败")
					|| !(messageTitle.contains("下单成功") || messageTitle.contains("成功"))) {
				log.error("trade FAIL! message is " + messageBodyElement.getText());
				return false;
			}

			// 循环检查交易是否真实成功
			boolean tradingIsSuccess = true; // checkTradingIsSuccess(webDriver, stockID, tradeSide, price, volume, refException);

			// 返回是否交易成功
			return tradingIsSuccess;
		} catch (Exception ex) {
//			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//			String logData = String.format("stockID=%s, stockCode=%s, tradeSide=%s, price=%s, volume=%s", stockID, stockCode, tradeSide.ordinal(), price, volume);
//			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		} finally {
			WebDriverUtils.webDriverQuit(webDriver);
		}

		return false;
	}

	/**
	 * 循环检查交易是否真实成功（只有真实成功才返回结果）
	 *
	 * @param webDriver
	 * @param stockID
	 * @param tradeSide
	 * @param price
	 * @param volume
	 * @return
	 */
	private boolean checkTradingIsSuccess(WebDriver webDriver, long stockID, TradeSideEnum tradeSide, float price, int volume) {
		try {
			for (int i = 0; i < 3600; i++) {
				// 打开成交记录页面
				String tradeRecordUrl = String.format(FutunnConsts.FUTUNN_TRADE_RECORD_URL_TMPL, String.valueOf(System.currentTimeMillis()));
				webDriver.get(tradeRecordUrl);
				String pageCode = webDriver.getPageSource();
				if (Strings.isNullOrEmpty(pageCode)) {
					log.error("checkTradingIsSuccess EXCEPTION! tradeRecord is EMPTY!");
					return false;
				}

//				// 根据成交记录页面代码，计算当前交易是否已真实成交
//				pageCode = pageCode.replace(" ", "");
//				List<String> jsons = CustomStringUtils.substringsBetweenToList(pageCode, "{\"id\"", "}");
//				for (String json : jsons) {
//					long actualStockID = CustomNumberUtils.toLong(CustomStringUtils.substringBetween(json, "\"security_id\":\"", "\""));
//					if (stockID == actualStockID) {
//						TradeSideEnum actualTradeSide = calcTradeSideFromJson(json);
//						float actualPrice = CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(json, "\"price\":", ","));
//						int actualVolume = CustomNumberUtils.toInt(CustomStringUtils.substringBetween(json, "\"quantity\":", ","));
//
//						if (actualTradeSide == tradeSide && actualPrice <= price && actualVolume == volume) {
//							return true;
//						}
//					}
//				}

				// 暂停一段时间
				TimeUnit.MILLISECONDS.sleep(500);
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stockID=%s, tradeSide=%s, price=%s, volume=%s", stockID, tradeSide.ordinal(), price, volume);
			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		log.error("checkTradingIsSuccess EXCEPTION! loop end but trading FAIL!");
		return false;
	}
}
