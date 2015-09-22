package org.polaris.framework.hadoop.hbase;

import org.apache.hadoop.hbase.client.Result;

/**
 * 行映射器
 * 
 * @author wang.sheng
 * 
 */
public interface RowMapper<T>
{
	/**
	 * 将Result转换为自定义对象
	 * 
	 * @param result
	 * @return
	 */
	T mapRow(Result result);
}
