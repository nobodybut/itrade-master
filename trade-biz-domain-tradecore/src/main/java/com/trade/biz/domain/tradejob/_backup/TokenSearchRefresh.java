//package com.jdy.solution.biz.domain.solutionjob.robotpost.toutiao;
//
//import com.google.common.collect.Lists;
//import com.jdy.basic.common.basiccache.cacheproxy_local_proxyserver.ProxyServer_LocalCacheProxy;
//import com.jdy.basic.model.basiccore.enums.ProxyServerTypeEnum;
//import com.jdy.infrastructure.business.conf.PropertiesUtils;
//import com.jdy.infrastructure.util.date.CustomDateFormatUtils;
//import com.jdy.infrastructure.util.logger.LogInfoUtils;
//import com.jdy.infrastructure.util.math.CustomMathUtils;
//import com.jdy.infrastructure.util.phantomjs.WebDriverUtils;
//import com.jdy.solution.common.solutionutil.consts.RobotPostConsts;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@Slf4j
//public class TokenSearchRefresh {
//
//	// 线程池
//	private static final ExecutorService EXECUTOR_POOL = Executors.newCachedThreadPool();
//
//	// 相关常量
//	private static final boolean REFRESH_USE_MULTITHREAD = false;
//	private static final int REFRESH_LOOP_COUNT = 500;
//	private static final int REFRESH_THREAD_SIZE = 2;
//	private static final String REFRESH_TOKEN_CODE = "RVN";
//	private static final String REFRESH_TOKEN_ID = "821689972";
//
//	// 依赖注入
//	@Resource
//	private ProxyServer_LocalCacheProxy proxyServer_LocalCacheProxy;
//
//	public void execute() {
//		try {
//			final List<Integer> executedSizes = Lists.newArrayList();
//			long totalStartMills = System.currentTimeMillis();
//
//			for (int i = 0; i < REFRESH_LOOP_COUNT; i++) {
//				if (REFRESH_USE_MULTITHREAD) {
//					long partStartMills = System.currentTimeMillis();
//
//					// 计算 Callable 线程列表
//					List<Callable<Boolean>> tasks = Lists.newArrayList();
//					for (int threadCount = 0; threadCount < REFRESH_THREAD_SIZE; threadCount++) {
//						tasks.add(() -> {
//							return doRefreshToken(executedSizes);
//						});
//					}
//
//					// 多线程执行
//					EXECUTOR_POOL.invokeAll(tasks);
//
//					// 记录分批完成日志
//					log.info("part ({}) task execute SUCCESS! spendTime={}", i, (System.currentTimeMillis() - partStartMills) / 1000);
//				} else {
//					for (int threadCount = 0; threadCount < REFRESH_THREAD_SIZE; threadCount++) {
//						doRefreshToken(executedSizes);
//					}
//				}
//			}
//
//			// 记录全部完成日志
//			log.info("all task execute SUCCESS! spendTime={}", (System.currentTimeMillis() - totalStartMills) / 1000);
//		} catch (Exception ex) {
//			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
//		}
//	}
//
//	/**
//	 * 真实执行 token 搜索刷新操作
//	 *
//	 * @param executedSizes
//	 * @return
//	 */
//	private boolean doRefreshToken(List<Integer> executedSizes) {
//		long startMills = System.currentTimeMillis();
//		String proxyServer = proxyServer_LocalCacheProxy.getDefaultProxyServer(ProxyServerTypeEnum.NORMAL);
//		WebDriver webDriver = null;
//
//		try {
//			// 获取 chromeDriver 实例
//			// webDriver = WebDriverUtils.getLocalChromeWebDriver(RobotPostConsts.WEBCLIENT_DEFAULT_TIME_OUT_MILLS * 4, proxyServer);
//			webDriver = WebDriverUtils.getRemoteChromeWebDriver(getRemoteChromeIP(), RobotPostConsts.WEBCLIENT_DEFAULT_TIME_OUT_MILLS * 4, proxyServer);
//
//			// 打开网站首页
//			webDriver.get("https://www.mytoken.io/");
//			WebElement searchInput = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".search-bar input"));
//			WebElement searchButton = WebDriverUtils.getSingleWebElement(webDriver, By.cssSelector(".search-btn"));
//			searchInput.click();
//			searchInput.clear();
//			searchInput.sendKeys(REFRESH_TOKEN_CODE);
//			searchButton.click();
//
//			// 打开搜索结果页
//			webDriver.get("https://www.mytoken.io/search?keyword=" + REFRESH_TOKEN_CODE);
//
//			// 打开产品详情页
//			TimeUnit.SECONDS.sleep(2);
//			webDriver.get("https://www.mytoken.io/currency/" + REFRESH_TOKEN_ID);
//
//			// 模拟点击产品简介标签
//			TimeUnit.SECONDS.sleep(CustomMathUtils.calRandomInteger(2, 3));
//			List<WebElement> domNodes = webDriver.findElements(By.cssSelector(".main .nav .nav-item"));
//			for (int i = 0; i < domNodes.size(); i++) {
//				domNodes.get(i).click();
//
//				if (i == 1) {
//					break;
//				} else {
//					TimeUnit.SECONDS.sleep(CustomMathUtils.calRandomInteger(1, 2));
//				}
//			}
//
//			executedSizes.add(0);
//			log.info("index={}, accessTime={}, proxyServer={}, spendTime={}", executedSizes.size(), CustomDateFormatUtils.formatDateTime(LocalDateTime.now()), proxyServer, (System.currentTimeMillis() - startMills) / 1000);
//		} catch (Exception ex) {
//			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
//		} finally {
//			WebDriverUtils.webDriverQuit(webDriver);
//		}
//
//		return false;
//	}
//
//	/**
//	 * 获取本地或远程 chromeDriver 实例
//	 *
//	 * @return
//	 */
//	private String getRemoteChromeIP() {
//		String remoteChromeIP = "10.26.171.131";
//		if (PropertiesUtils.getBooleanValue("isDebug")) {
//			remoteChromeIP = "localhost";
//		}
//		return remoteChromeIP;
//	}
//}
