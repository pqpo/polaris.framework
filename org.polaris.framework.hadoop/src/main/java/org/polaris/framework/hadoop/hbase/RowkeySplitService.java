package org.polaris.framework.hadoop.hbase;

/**
 * 行key的划分服务
 * 
 * @author wang.sheng
 * 
 */
public interface RowkeySplitService
{
	String[] split();
}
