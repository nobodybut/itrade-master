package com.trade.biz.dal.base;

import com.trade.common.infrastructure.util.dao.DynamicSqlSessionTemplate;

public class TradeDrdsBaseDao extends DynamicSqlSessionTemplate {

	@Override
	protected String getSqlSessionTemplateName() {
		return "sqlSessionTemplate_tradedrds";
	}
}
