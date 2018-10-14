package com.itrade.common.infrastructure.util.sql;

import com.google.common.collect.Lists;
import com.itrade.common.infrastructure.util.logger.LogInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBSQLQueryUtils {

	// Logger
	private static final Logger _logger = LoggerFactory.getLogger(DBSQLQueryUtils.class);

	/**
	 * 执行SQL语句
	 *
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> executeQueryBySql(Connection conn, String sql) throws SQLException {
		List<Map<String, Object>> result = Lists.newArrayList();

		Statement stmt = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rSet = stmt.executeQuery(sql);
			ResultSetMetaData md = rSet.getMetaData();
			int num = md.getColumnCount();
			while (rSet.next()) {
				Map<String, Object> mapOfColValues = new HashMap<String, Object>(num);
				for (int i = 1; i <= num; i++) {
					mapOfColValues.put(md.getColumnName(i), rSet.getObject(i));
				}
				result.add(mapOfColValues);
			}
			conn.commit();
		} catch (Exception ex) {
			_logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName()), ex);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return result;
	}

	/**
	 * 执行存储过程
	 *
	 * @param conn
	 * @param storedProcedureName
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> executeQueryByStoredProcedure(Connection conn, String storedProcedureName) throws SQLException {
		List<Map<String, Object>> result = Lists.newArrayList();

		CallableStatement stmt = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.prepareCall(String.format("{call %s()}", storedProcedureName));
			ResultSet rSet = stmt.executeQuery();
			ResultSetMetaData md = rSet.getMetaData();
			int num = md.getColumnCount();
			while (rSet.next()) {
				Map<String, Object> mapOfColValues = new HashMap<String, Object>(num);
				for (int i = 1; i <= num; i++) {
					mapOfColValues.put(md.getColumnName(i), rSet.getObject(i));
				}

				result.add(mapOfColValues);
			}
			conn.commit();
		} catch (Exception ex) {
			_logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName()), ex);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

		return result;
	}
}
