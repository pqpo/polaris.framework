package org.polaris.framework.hadoop.mapreduce;

/**
 * MapReduce的拦截器
 * 
 * @author wang.sheng
 * 
 */
public interface MapReduceInterceptor
{
	/**
	 * 在任务启动之前
	 */
	void before();

	/**
	 * 在任务启动之后
	 */
	void after();

	/**
	 * 出现异常状况
	 * 
	 * @param e
	 */
	void exception(Exception e);
}
