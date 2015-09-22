package org.polaris.framework.hadoop.hbase;

import java.io.File;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.apache.hadoop.hbase.client.Result;
import org.polaris.framework.hadoop.HadoopContext;
import org.polaris.framework.hadoop.hbase.storage.PutConverter;
import org.polaris.framework.hadoop.hbase.storage.StorageSession;


/**
 * HBase的各项操作模板
 * 
 * @author wang.sheng
 * 
 */
public interface HBaseTemplate
{
	/**
	 * 为指定表创建写入管道
	 * 
	 * @param name
	 * @return
	 */
	OutputChannel createOutputChannel(String name);

	/**
	 * 为指定表创建写入管道
	 * 
	 * @param name
	 * @return
	 */
	OutputChannel createOutputChannel(byte[] name);

	/**
	 * 获取hadoopContext对象
	 * 
	 * @return
	 */
	HadoopContext getHadoopContext();

	/**
	 * 向指定表中导入文本文件.一般支持CSV和TSV格式
	 * 
	 * @param tableName
	 * @param textFile
	 * @param separator
	 * @param putConverter
	 */
	void storage(String tableName, File textFile, char separator, PutConverter<String[]> putConverter);

	/**
	 * 向指定表中导入文本文件.一般支持CSV和TSV格式
	 * 
	 * @param tableName
	 * @param textFile
	 * @param separator
	 * @param putConverter
	 */
	void storage(byte[] tableName, File textFile, char separator, PutConverter<String[]> putConverter);

	/**
	 * 根据SQL导入关系数据库的数据到HBase中
	 * 
	 * @param tableName
	 * @param dataSource
	 * @param sql
	 * @param putConverter
	 */
	void storage(String tableName, DataSource dataSource, String sql, PutConverter<ResultSet> putConverter);

	/**
	 * 根据SQL导入关系数据库的数据到HBase中
	 * 
	 * @param tableName
	 * @param dataSource
	 * @param sql
	 * @param putConverter
	 */
	void storage(byte[] tableName, DataSource dataSource, String sql, PutConverter<ResultSet> putConverter);

	/**
	 * 通过GET的方式查询一条记录
	 * 
	 * @param name
	 * @param key
	 * @return
	 */
	Result get(String name, String key);

	/**
	 * 通过GET的方式查询一条记录
	 * 
	 * @param name
	 * @param key
	 * @param rowMapper
	 * @return
	 */
	<T> T get(String name, String key, RowMapper<T> rowMapper);

	/**
	 * 查询结果,返回List. 适用于数据量较少的查询
	 * 
	 * @param name
	 * @param startKey
	 * @param endKey
	 * @param rowMapper
	 * @return
	 */
	<T> List<T> queryForList(String name, String startKey, String endKey, RowMapper<T> rowMapper);

	/**
	 * 查询结果,返回List. 适用于数据量较少的查询
	 * 
	 * @param name
	 * @param rowMapper
	 * @return
	 */
	<T> List<T> queryForList(String name, RowMapper<T> rowMapper);

	/**
	 * 查询和遍历表记录
	 * 
	 * @param name
	 * @param startKey
	 * @param endKey
	 * @param handler
	 */
	void query(String name, String startKey, String endKey, QueryCallbackHandler handler);

	/**
	 * 查询和遍历表记录
	 * 
	 * @param name
	 * @param handler
	 */
	void query(String name, QueryCallbackHandler handler);

	/**
	 * 删除指定表
	 * 
	 * @param name
	 */
	void deleteTable(String name);

	/**
	 * 创建表.rebuild表示一旦表已存在,是否重新创建该表
	 * 
	 * @param tableMeta
	 * @param rebuild
	 */
	void createTable(TableMeta tableMeta, boolean rebuild);

	/**
	 * 开启存储会话
	 * 
	 * @param tableName
	 * @param poolSize
	 */
	StorageSession openStorageSession(String tableName, int poolSize);

	/**
	 * 开启存储会话
	 * 
	 * @param tableName
	 * @param poolSize
	 * @return
	 */
	StorageSession openStorageSession(byte[] tableName, int poolSize);
}
