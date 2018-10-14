package com.trade.common.infrastructure.util.web;

import com.trade.common.infrastructure.util.consts.DomainConsts;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebException {

	/**
	 * 转到404错误页面
	 *
	 * @param response
	 */
	public static void redirectTo404(HttpServletResponse response) {
		try {
			response.sendRedirect(DomainConsts.WWW_ITRADE_DOMAIN + "/404.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
