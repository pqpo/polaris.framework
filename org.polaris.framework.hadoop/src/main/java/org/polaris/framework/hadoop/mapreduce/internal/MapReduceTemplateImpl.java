package org.polaris.framework.hadoop.mapreduce.internal;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapreduce.Job;
import org.polaris.framework.common.console.ConsoleSupport;
import org.polaris.framework.hadoop.HadoopContext;
import org.polaris.framework.hadoop.mapreduce.MapReduceInterceptor;
import org.polaris.framework.hadoop.mapreduce.MapReduceJobSetter;
import org.polaris.framework.hadoop.mapreduce.MapReduceTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


/**
 * MapReduce模板接口的实现
 * 
 * @author wang.sheng
 * 
 */
@Service
public class MapReduceTemplateImpl implements MapReduceTemplate, ConsoleSupport
{
	Log log = LogFactory.getLog(getClass());

	private static final String PARAM_KEY = "MAPREDUCE_TASK_PARAM";

	@Resource
	private HadoopContext hadoopContext;
	@Resource
	private ApplicationContext applicationContext;

	@Override
	public void start(String beanId, String param)
	{
		MapReduceJobSetter jobSetter = applicationContext.getBean(beanId, MapReduceJobSetter.class);
		MapReduceInterceptor interceptor = null;
		if (jobSetter instanceof MapReduceInterceptor)
		{
			interceptor = (MapReduceInterceptor) jobSetter;
			interceptor.before();
		}
		String taskName = beanId + "-" + DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss");
		try
		{
			long t1 = System.currentTimeMillis();
			log.info("MapReduce: " + taskName + " is started!");
			Job job = Job.getInstance(hadoopContext.createConfiguration(), taskName);
			jobSetter.set(job);
			if (!StringUtils.isEmpty(param))
			{
				log.info("mapreduce task param: " + param);
				job.getConfiguration().set(PARAM_KEY, param);
			}
			job.waitForCompletion(true);
			long t2 = System.currentTimeMillis();
			log.info("MapReduce: " + taskName + " is Completed! use time:" + (t2 - t1));
			if (interceptor != null)
			{
				interceptor.after();
			}
		}
		catch (Exception e)
		{
			log.error("MapReduce: " + taskName + " is failed!", e);
			if (interceptor != null)
			{
				interceptor.exception(e);
			}
		}
	}

	@Override
	public String commandRegex()
	{
		return "^start mapreduce (\\w+) *(\\w*)$";
	}

	@Override
	public void execute(String[] args)
	{
		String beanId = args[0];
		String param = null;
		if (args.length > 1)
		{
			param = args[1];
		}
		if (!applicationContext.containsBean(beanId))
		{
			// bean不存在
			log.error("MapReduceJobSetter Bean: " + beanId + " not found!");
			String[] beanIds = applicationContext.getBeanNamesForType(MapReduceJobSetter.class);
			log.warn("MapReduceJobSetter Beans: " + StringUtils.join(beanIds, ","));
			return;
		}
		this.start(beanId, param);
	}

	@Override
	public void start(String beanId)
	{
		this.start(beanId, null);
	}
}
