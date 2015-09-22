package org.polaris.framework.hadoop.hbase.storage;

import java.sql.ResultSet;
import java.sql.Types;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * 抽象的数据库记录到Put的转换器,适用于关系数据库导入HBase
 * 
 * @author wang.sheng
 * 
 */
public abstract class AbstractResultSetPutConverter implements PutConverter<ResultSet>
{
	private Map<String, ColumnMeta> columnMetaMap;
	private static final byte[] family = Bytes.toBytes("table");

	/**
	 * 抽象方法,根据ResultSet构建key
	 * 
	 * @param row
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected abstract String generateKey(int row, ResultSet rs) throws SQLException;

	/**
	 * 初始化字段元数据集合
	 * 
	 * @param metaData
	 * @throws SQLException
	 */
	private void initColumnMetaMap(ResultSetMetaData metaData) throws SQLException
	{
		columnMetaMap = new HashMap<String, ColumnMeta>();
		int columnNumber = metaData.getColumnCount();
		for (int i = 0; i < columnNumber; i++)
		{
			String columnName = metaData.getColumnName(i);
			int columnType = metaData.getColumnType(i);
			columnMetaMap.put(columnName, new ColumnMeta(columnType, Bytes.toBytes(columnName)));
		}
	}

	@Override
	public final Put convert(int row, ResultSet rs) throws SQLException
	{
		if (columnMetaMap == null)
		{
			initColumnMetaMap(rs.getMetaData());
		}
		String key = generateKey(row, rs);
		Put put = new Put(Bytes.toBytes(key));
		for (Map.Entry<String, ColumnMeta> entry : columnMetaMap.entrySet())
		{
			byte[] columnValue = this.getColumnValue(entry, rs);
			if (columnValue != null)
			{
				put.addColumn(family, entry.getValue().getName(), columnValue);
			}
		}
		return put;
	}

	/**
	 * 获取字段值对象
	 * 
	 * @param entry
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private byte[] getColumnValue(Map.Entry<String, ColumnMeta> entry, ResultSet rs) throws SQLException
	{
		String columnName = entry.getKey();
		int columnType = entry.getValue().getType();
		byte[] value = null;
		switch (columnType)
		{
		case Types.BIGINT:
			value = Bytes.toBytes(rs.getLong(columnName));
			break;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			value = Bytes.toBytes(rs.getTimestamp(columnName).getTime());
			break;
		case Types.INTEGER:
		case Types.TINYINT:
			value = Bytes.toBytes(rs.getInt(columnName));
			break;
		case Types.DECIMAL:
		case Types.FLOAT:
		case Types.DOUBLE:
			value = Bytes.toBytes(rs.getDouble(columnName));
			break;
		case Types.CHAR:
		case Types.CLOB:
		case Types.NCHAR:
		case Types.NCLOB:
		case Types.VARCHAR:
		default:
			value = Bytes.toBytes(rs.getString(columnName));
			break;
		}
		return value;
	}

	/**
	 * 列元数据
	 * 
	 * @author wang.sheng
	 * 
	 */
	private static class ColumnMeta
	{
		private int type;
		private byte[] name;

		public ColumnMeta(int type, byte[] name)
		{
			this.type = type;
			this.name = name;
		}

		public int getType()
		{
			return type;
		}

		public byte[] getName()
		{
			return name;
		}

	}
}
