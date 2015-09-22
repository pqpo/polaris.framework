package org.polaris.framework.hadoop.mapreduce;

/**
 * 基于Yarn的MapReduce模板接口
 * 
 * @author wang.sheng
 * 
 */
public interface MapReduceTemplate
{
	/**
	 * 启动MapReduce任务,并同步等待运行结束<br>
	 * beanId为对应实现了JobSetter接口的Bean ID<br>
	 * 
	 * @param beanId
	 */
	void start(String beanId);

	/**
	 * 启动MapReduce任务,带参数
	 * 
	 * @param beanId
	 * @param param
	 */
	void start(String beanId, String param);
}
