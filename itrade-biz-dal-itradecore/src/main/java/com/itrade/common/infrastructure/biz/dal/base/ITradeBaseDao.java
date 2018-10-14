package com.itrade.common.infrastructure.biz.dal.base;

import com.itrade.common.infrastructure.util.dao.DynamicSqlSessionTemplate;

public class ITradeBaseDao extends DynamicSqlSessionTemplate {

	@Override
	protected String getSqlSessionTemplateName() {
		return "sqlSessionTemplate_itradecore";
	}
}

