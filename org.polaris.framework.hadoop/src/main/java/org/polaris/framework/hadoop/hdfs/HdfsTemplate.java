package org.polaris.framework.hadoop.hdfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sql.DataSource;

import org.apache.hadoop.fs.Path;
import org.polaris.framework.hadoop.CompressionType;


/**
 * HDFS文件系统的模板类
 * 
 * @author wang.sheng
 * 
 */
public interface HdfsTemplate
{
	/**
	 * 开启一个OutputStream用于写入内容到HDFS
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	OutputStream getOutputStream(String uri) throws IOException;

	/**
	 * 开启一个OutputStream用于写入文件
	 * 
	 * @param uri
	 * @param append
	 *            是否追加
	 * @return
	 * @throws IOException
	 */
	OutputStream getOutputStream(String uri, boolean append) throws IOException;

	/**
	 * 开启一个InputStream用于读出HDFS文件内容
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream(String uri) throws IOException;

	/**
	 * 获取指定路径下的所有文件Path集合
	 * 
	 * @param path
	 * @return
	 */
	Path[] listPaths(Path path);

	/**
	 * 删除指定文件
	 * 
	 * @param uri
	 */
	void delete(String uri);

	/**
	 * 删除指定文件
	 * 
	 * @param path
	 */
	void delete(Path path);

	/**
	 * 将本地文件写入HDFS
	 * 
	 * @param file
	 * @param uri
	 */
	void write(File file, String uri);

	/**
	 * 将本地文件按照指定的压缩格式写入HDFS
	 * 
	 * @param file
	 * @param uri
	 * @param compressType
	 */
	void write(File file, String uri, CompressionType compressType);

	/**
	 * 将本地文件写入HDFS
	 * 
	 * @param file
	 * @param uri
	 * @param append
	 *            是否追加
	 */
	void write(File file, String uri, boolean append);

	/**
	 * 将本地文件写入HDFS
	 * 
	 * @param file
	 * @param uri
	 * @param compressType
	 * @param append
	 *            是否追加
	 */
	void write(File file, String uri, CompressionType compressType, boolean append);

	/**
	 * 将指定SQL的结果集以CSV格式写入HDFS<br>
	 * rowConverter如果为null,则按照默认的方式,写入全部列到CSV中<br>
	 * 
	 * @param dataSource
	 * @param sql
	 * @param uri
	 * @param separator
	 * @param rowConverter
	 */
	void write(DataSource dataSource, String sql, String uri, char separator, RowConverter rowConverter);

	/**
	 * 建立多线程存储会话
	 * 
	 * @param uri
	 * @param poolSize
	 * @param separator
	 * @return
	 */
	StorageSession openStorageSession(String uri, int poolSize, char separator);

}
