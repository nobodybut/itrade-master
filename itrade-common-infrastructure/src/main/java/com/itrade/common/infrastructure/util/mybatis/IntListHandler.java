package com.itrade.common.infrastructure.util.mybatis;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.itrade.common.infrastructure.util.collection.CustomListUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class IntListHandler extends BaseTypeHandler<List<Integer>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, CustomListUtils.listToString(parameter, "|"));
	}

	@Override
	public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getResult(rs.getString(columnName));
	}

	@Override
	public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(rs.getString(columnIndex));
	}

	@Override
	public List<Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getResult(cs.getString(columnIndex));
	}

	private List<Integer> getResult(String value) {
		if (!Strings.isNullOrEmpty(value)) {
			return CustomListUtils.stringToListInteger(value, "|");
		}

		return Lists.newArrayList();
	}
}
