package com.trade.common.infrastructure.util.mybatis;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.collection.CustomListUtils;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class LocalDateListTypeHandler extends BaseTypeHandler<List<LocalDate>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<LocalDate> parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, CustomJSONUtils.toJSONString(parameter));
	}

	@Override
	public List<LocalDate> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getResult(rs.getString(columnName));
	}

	@Override
	public List<LocalDate> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(rs.getString(columnIndex));
	}

	@Override
	public List<LocalDate> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getResult(cs.getString(columnIndex));
	}

	private List<LocalDate> getResult(String value) {
		if (!Strings.isNullOrEmpty(value)) {
			return CustomListUtils.arrayToList(JSON.parseObject(value, LocalDate[].class));
		}

		return Lists.newArrayList();
	}
}
