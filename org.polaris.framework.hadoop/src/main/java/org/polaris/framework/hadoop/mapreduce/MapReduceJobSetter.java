package org.polaris.framework.hadoop.mapreduce;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;

/**
 * Job的设置
 * 
 * @author wang.sheng
 * 
 */
public interface MapReduceJobSetter
{
	/**
	 * 设置Job启动前的各项属性
	 * 
	 * @param job
	 * @throws IOException
	 */
	void set(Job job) throws IOException;
}
