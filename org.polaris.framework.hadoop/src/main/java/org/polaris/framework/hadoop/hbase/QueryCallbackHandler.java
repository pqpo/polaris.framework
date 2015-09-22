package org.polaris.framework.hadoop.hbase;

import java.util.Iterator;

import org.apache.hadoop.hbase.client.Result;

/**
 * 查询结果处理
 * 
 * @author wang.sheng
 * 
 */
public interface QueryCallbackHandler
{
	void process(Iterator<Result> iterator);
}
