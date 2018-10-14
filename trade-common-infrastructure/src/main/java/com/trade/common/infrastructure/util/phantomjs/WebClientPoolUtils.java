package com.trade.common.infrastructure.util.phantomjs;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * WebClient 工具类
 */
@Slf4j
public class WebClientPoolUtils {

	//基于Apache的commons-pool池
	private WebClientPool clientPool;

	private WebClientPoolUtils() {
		WebClientFactory orderFactory = new WebClientFactory();

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(15);
		//设置获取连接超时时间
		config.setMaxWaitMillis(10000);

		clientPool = new WebClientPool(orderFactory, config);
	}

	public WebClient getClient() {
		try {
			return (WebClient) this.clientPool.borrowObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void returnClient(WebClient client) {
		try {
			this.clientPool.returnObject(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
	 */
	private static class WebClientPoolUtilsHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static WebClientPoolUtils instance = new WebClientPoolUtils();
	}

	/**
	 * 当getInstance方法第一次被调用的时候，它第一次读取 WebClientUtilHolder.instance，导致WebClientUtilHolder类得到初始化；<BR />
	 * 而这个类在装载并被初始化的时候，会初始化它的静 态域，从而创建WebClientUtil的实例，由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并由虚拟机来保证它的线程安全性。<BR />
	 * 这个模式的优势在于，getInstance方法并没有被同步，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。<BR />
	 */
	public static WebClientPoolUtils getInstance() {
		return WebClientPoolUtilsHolder.instance;
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 7; i++) {
			WebClient webClient = WebClientPoolUtils.getInstance().getClient();
			HtmlPage htmlPage = webClient.getPage("http://2018.ip138.com/ic.asp");
			WebResponse response = htmlPage.getWebResponse();
			String result = response.getContentAsString();
			System.out.println("brrow a connection: " + webClient + " active connection:" + WebClientPoolUtils.getInstance().clientPool.getNumActive() + "html:" + result);
		}
	}
}