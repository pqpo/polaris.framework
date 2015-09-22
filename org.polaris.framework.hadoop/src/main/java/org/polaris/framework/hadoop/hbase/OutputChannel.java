package org.polaris.framework.hadoop.hbase;

import java.io.Closeable;
import java.util.List;

import org.apache.hadoop.hbase.client.Put;

/**
 * 向HBase库表中写入内容的管道
 * 
 * @author wang.sheng
 * 
 */
public interface OutputChannel extends Closeable
{
	/**
	 * 打开管道
	 */
	void open();

	/**
	 * 写入数据
	 * 
	 * @param put
	 */
	void write(Put put);

	/**
	 * 写入批量数据
	 * 
	 * @param putList
	 */
	void write(List<Put> putList);

}
