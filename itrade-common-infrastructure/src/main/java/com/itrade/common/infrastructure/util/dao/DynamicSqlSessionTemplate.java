package com.itrade.common.infrastructure.util.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public abstract class DynamicSqlSessionTemplate implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	private Map<String, SqlSessionTemplate> targetSqlSessionTemplates;
	private SqlSessionTemplate defaultTargetSqlSessionTemplate;

	/**
	 * 根据服务器类型读取 sqlSessionTemplate 对象
	 *
	 * @return
	 */
	public SqlSessionTemplate getSqlSessionTemplate() {
		SqlSessionTemplate result = null;

		String sqlSessionTemplateName = getSqlSessionTemplateName();
		result = targetSqlSessionTemplates.get(sqlSessionTemplateName);
		if (result == null) {
			if (defaultTargetSqlSessionTemplate != null) {
				result = defaultTargetSqlSessionTemplate;
			} else {
				result = (SqlSessionTemplate) applicationContext.getBean(sqlSessionTemplateName);
			}
		}

		return result;
	}

	/**
	 * 设置 targetSqlSessionTemplates 数据
	 *
	 * @param targetSqlSessionTemplates
	 */
	public void setTargetSqlSessionTemplates(Map<String, SqlSessionTemplate> targetSqlSessionTemplates) {
		this.targetSqlSessionTemplates = targetSqlSessionTemplates;
	}

	/**
	 * 设置 defaultTargetSqlSessionTemplate 数据
	 *
	 * @param defaultTargetSqlSessionTemplate
	 */
	public void setDefaultTargetSqlSessionTemplate(SqlSessionTemplate defaultTargetSqlSessionTemplate) {
		this.defaultTargetSqlSessionTemplate = defaultTargetSqlSessionTemplate;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 根据服务器所在环境计算 sqlSessionTemplateName 的值
	 *
	 * @return
	 */
	protected abstract String getSqlSessionTemplateName();
}
