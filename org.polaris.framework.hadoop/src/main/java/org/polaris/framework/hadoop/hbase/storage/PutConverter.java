package org.polaris.framework.hadoop.hbase.storage;

import org.apache.hadoop.hbase.client.Put;

/**
 * 将指定数据源转换为Put实例
 * 
 * @author wang.sheng
 * 
 * @param <T>
 */
public interface PutConverter<T>
{
	Put convert(int row, T source) throws Exception;
}
