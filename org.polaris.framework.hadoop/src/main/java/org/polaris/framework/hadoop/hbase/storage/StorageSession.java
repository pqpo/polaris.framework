package org.polaris.framework.hadoop.hbase.storage;

import java.io.File;
import java.sql.ResultSet;

import javax.sql.DataSource;

/**
 * 存储会话<br>
 * 一般用于解决较大数据量的导入操作,通过线程池并发的方式提高导入速度
 * 
 * @author wang.sheng
 * 
 */
public interface StorageSession
{
	/**
	 * 同步等待存储会话执行完成
	 */
	void waitForCompletion();

	/**
	 * 添加存储请求
	 * 
	 * @param title
	 * @param dataSource
	 * @param sql
	 * @param putConverter
	 */
	void addRequest(String title, DataSource dataSource, String sql, PutConverter<ResultSet> putConverter);

	/**
	 * 添加存储请求
	 * 
	 * @param textFile
	 * @param separator
	 * @param putConverter
	 */
	void addRequest(File textFile, char separator, PutConverter<String[]> putConverter);
}
