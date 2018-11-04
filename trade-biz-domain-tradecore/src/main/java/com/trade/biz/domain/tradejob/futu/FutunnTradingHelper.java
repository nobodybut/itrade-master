package com.trade.biz.domain.tradejob.futu;

import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.phantomjs.WebDriverUtils;
import com.trade.model.tradecore.enums.TrdSideEnum;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FutunnTradingHelper {

	// 相关常量
	private static boolean USE_LOCAL_CHROME_DRIVER = false;

	// 依赖注入
	@Resource
	private FutunnLoginHelper futunnLoginHelper;

	/**
	 * 处理富途股票模拟交易
	 *
	 * @param stock
	 * @param trdSide
	 * @param price
	 * @param quantity
	 * @return
	 */
	public boolean stockTrading(Stock stock, TrdSideEnum trdSide, float price, int quantity) {
		boolean isSuccess = false;
		WebDriver webDriver = null;

		try {
			// 获取 chromeDriver 实例
			webDriver = WebDriverUtils.getChromeWebDriver(USE_LOCAL_CHROME_DRIVER);

			// 处理用户登录
			boolean loginSuccess = futunnLoginHelper.loginFutunn(webDriver);
			if (!loginSuccess) {
				log.error("futunn login FAIL!");
				return false;
			}

			// 打开股票模拟交易页面
			webDriver.get("https://www.futunn.com/trade/us-trade#us/" + stock.getCode());
			WebDriverUtils.waitForPageElementLoaded(webDriver, By.id("stockCodeInput"));
			WebDriverUtils.waitForPageElementAttribute(webDriver, By.cssSelector(".tradeForm01 .selectFloatBox"), "style", "display:none");

			// 查找模拟交易页面上的输入控件
			WebElement priceInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("price"));
			WebElement qtyInput = WebDriverUtils.getSingleWebElement(webDriver, By.name("qty_str"));
			WebElement buyButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".btnLi01 .btn01"));
			WebElement sellButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".btnLi01 .btn02"));
			if (priceInput == null || qtyInput == null || buyButton == null || sellButton == null) {
				log.error("tradeInputs Elements ERROR!");
				return false;
			}

			// 输入股票代码、买入/卖出价格、数量
			TimeUnit.MILLISECONDS.sleep(400);
			priceInput.click();
			priceInput.clear();
			priceInput.sendKeys(String.valueOf(price));
			TimeUnit.MILLISECONDS.sleep(400);
			qtyInput.click();
			qtyInput.clear();
			qtyInput.sendKeys(String.valueOf(quantity));

			// 根据交易类型，点击对应的买入/卖出按钮
			if (trdSide == TrdSideEnum.BUY) {
				buyButton.click();
			} else if (trdSide == TrdSideEnum.SELL) {
				sellButton.click();
			}

			// 处理购买确认框
			WebElement submitButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector("#confirmDialog .btn01"));
			if (submitButton == null) {
				log.error("submitButtonElement ERROR!");
				return false;
			}
			TimeUnit.MILLISECONDS.sleep(200);
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
			if (messageTitle.contains("下单失败") || messageTitle.contains("失败")) {
				log.error("trade FAIL! message is " + messageBodyElement.getText());
				isSuccess = false;
			} else if (messageTitle.contains("下单成功") || messageTitle.contains("成功")) {
				isSuccess = true;
			}

			// 写入买入/卖出记录到数据库

		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stock=%s, trdSide=%s, price=%s, quantity=%s", stock.getCode(), trdSide.ordinal(), price, quantity);
			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		} finally {
			if (webDriver != null) {
				webDriver.quit();
			}
		}

		return isSuccess;
	}
}
