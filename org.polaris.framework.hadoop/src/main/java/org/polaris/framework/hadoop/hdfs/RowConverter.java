package org.polaris.framework.hadoop.hdfs;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 将数据库行记录转换为CSV的行记录
 * 
 * @author wang.sheng
 * 
 */
public interface RowConverter
{
	/**
	 * 将数据库行记录转换为CSV的行记录
	 * 
	 * @param row
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	String[] convert(int row, ResultSet resultSet) throws SQLException;
}
