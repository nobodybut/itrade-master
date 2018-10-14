package com.itrade.common.infrastructure.util.mybatis;

/**
 * Created by axlwish on 8/31/16.
 */
public class MybatisUtils {

	/**
	 * 根据传入数据库序号, 计算分库分表类型数据库集合的单个数据库名称
	 *
	 * @param dbOrdinal
	 * @return
	 */
	public static String calDRDSDBName(int dbOrdinal) {
		if (dbOrdinal < 10) {
			return "0" + dbOrdinal;
		}

		return String.valueOf(dbOrdinal);
	}
}
