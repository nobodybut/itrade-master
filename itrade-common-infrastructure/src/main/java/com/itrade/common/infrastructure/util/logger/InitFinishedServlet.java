package com.itrade.common.infrastructure.util.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitFinishedServlet extends HttpServlet {

	private final Logger _logger = LoggerFactory.getLogger(getClass());

	@Override
	public void init() throws ServletException {
		_logger.info("==================== HttpServlet 加载完成 ====================");
	}
}
