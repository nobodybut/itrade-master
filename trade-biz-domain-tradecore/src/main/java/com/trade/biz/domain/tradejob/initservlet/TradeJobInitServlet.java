package com.trade.biz.domain.tradejob.initservlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class TradeJobInitServlet extends HttpServlet {

	private static final Logger s_logger = LoggerFactory.getLogger(TradeJobInitServlet.class);

	@Override
	public void init() throws ServletException {
		try {
			JobManager.getInstance().run();
			s_logger.info("加载完成...");
		} catch (Exception ex) {
			s_logger.error("加载出现错误", ex);
		}
	}
}
