package com.trade.common.tradeutil.consts;

import com.trade.common.infrastructure.business.conf.DebugConfigUtils;

public class FutunnConsts {

	// 相关常量
	public static boolean USE_LOCAL_CHROME_DRIVER = DebugConfigUtils.isDebug() ? true : false;
	public static final String FUTUNN_LOGIN_PAGE_URL = "https://passport.futu5.com/";
	public static final String FUTUNN_USER_NAME = "18601018270";
	public static final String FUTUNN_PASS_WORD = "2384Wish";
	public static final String FUTUNN_ACCOUNT_ID = "8444896";
	public static final String FUTUNN_QUOTE_BASIC_URL_TMPL = "https://www.futunn.com/trade/quote-basic-v3?security_id=%s&_=%s";
	public static final String FUTUNN_QUOTE_MINUTE_URL_TMPL = "https://www.futunn.com/trade/quote-minute-v2?security_id=%s&_=%s";
	public static final String FUTUNN_TOP_LIST_URL_TMPL = "https://www.futunn.com/stock/top-list?plate_id=%s&page=%s&_=%s";
	public static final String FUTUNN_DAY_KLINE_URL_TMPL = "https://www.futunn.com/quote/kline-v2?security_id=%s&type=2&from=&_=%s";
	public static final String FUTUNN_US_TRADE_URL_TMPL = "https://www.futunn.com/trade/us-trade#us/";
	public static final String FUTUNN_ACCOUNT_URL_TMPL = "https://www.futunn.com/trade/account?market=2&account_id=&_=%s";
	public static final String FUTUNN_TRADE_RECORD_URL_TMPL = "https://www.futunn.com/trade/trade-record?accountId=" + FUTUNN_ACCOUNT_ID + "&_=%s";
}
