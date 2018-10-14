package com.itrade.common.infrastructure.util.mybatis;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.itrade.common.infrastructure.util.collection.CustomListUtils;
import com.itrade.common.infrastructure.util.math.CustomNumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class IntIntsMapHandler extends BaseTypeHandler<Map<Integer, List<Integer>>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Map<Integer, List<Integer>> parameter, JdbcType jdbcType) throws SQLException {
		String resultValue = "";

		if (parameter.size() > 0) {
			StringBuilder sBuilder = new StringBuilder();

			for (Map.Entry<Integer, List<Integer>> entry : parameter.entrySet()) {
				sBuilder.append(entry.getKey());
				sBuilder.append(":");

				for (int index = 0; index < entry.getValue().size(); index++) {
					sBuilder.append(entry.getValue().get(index));

					if (index != entry.getValue().size() - 1) {
						sBuilder.append("-");
					}
				}

				sBuilder.append("|");
			}

			resultValue = sBuilder.substring(0, sBuilder.length() - 1);
		}

		ps.setString(i, resultValue);
	}

	@Override
	public Map<Integer, List<Integer>> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getResult(rs.getString(columnName));
	}

	@Override
	public Map<Integer, List<Integer>> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(rs.getString(columnIndex));
	}

	@Override
	public Map<Integer, List<Integer>> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getResult(cs.getString(columnIndex));
	}

	private Map<Integer, List<Integer>> getResult(String value) {
		Map<Integer, List<Integer>> result = Maps.newHashMap();

		if (!Strings.isNullOrEmpty(value)) {
			String[] bigItems = StringUtils.split(value, "|");
			for (String bigItem : bigItems) {
				String[] kvItems = StringUtils.splitPreserveAllTokens(bigItem, ":");
				if (kvItems.length == 2) {
					int key = CustomNumberUtils.toInt(kvItems[0]);
					List<Integer> values = CustomListUtils.stringToListInteger(kvItems[1], "-");

					if (!result.containsKey(key)) {
						result.put(key, values);
					}
				}
			}
		}

		return result;
	}
}
