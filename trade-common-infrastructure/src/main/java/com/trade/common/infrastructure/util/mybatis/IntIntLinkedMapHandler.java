package com.trade.common.infrastructure.util.mybatis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class IntIntLinkedMapHandler extends BaseTypeHandler<LinkedHashMap<Integer, Integer>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LinkedHashMap<Integer, Integer> parameter, JdbcType jdbcType) throws SQLException {
		String resultValue = "";

		if (parameter.size() > 0) {
			resultValue = CustomJSONUtils.toJSONString(parameter);
		}

		ps.setString(i, resultValue);
	}

	@Override
	public LinkedHashMap<Integer, Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getResult(rs.getString(columnName));
	}

	@Override
	public LinkedHashMap<Integer, Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(rs.getString(columnIndex));
	}

	@Override
	public LinkedHashMap<Integer, Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getResult(cs.getString(columnIndex));
	}

	private LinkedHashMap<Integer, Integer> getResult(String value) {
		LinkedHashMap<Integer, Integer> result = Maps.newLinkedHashMap();

		if (!Strings.isNullOrEmpty(value)) {
			result = JSON.parseObject(value, new TypeReference<LinkedHashMap<Integer, Integer>>() {
			});
		}

		return result;
	}
}
