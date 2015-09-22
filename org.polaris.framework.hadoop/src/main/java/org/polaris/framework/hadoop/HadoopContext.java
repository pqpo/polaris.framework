package org.polaris.framework.hadoop;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.polaris.framework.common.config.Config;
import org.polaris.framework.common.config.Property;

/**
 * HBase的上下文
 * 
 * @author wang.sheng
 * 
 */
public final class HadoopContext
{
	Log log = LogFactory.getLog(getClass());

	/**
	 * 连接参数配置
	 */
	private Config config;

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param config
	 */
	public void setConfig(Config config)
	{
		this.config = config;
	}

	/**
	 * 创建Configuration对象
	 * 
	 * @return
	 */
	public Configuration createConfiguration()
	{
		Configuration configuration = new Configuration();
		List<Property> list = config.getPropertyList();
		for (Property property : list)
		{
			configuration.set(property.getName(), property.getValue());
		}
		return HBaseConfiguration.create(configuration);
	}

	/**
	 * 创建连接
	 * 
	 * @return
	 * @throws IOException
	 */
	public Connection createConnection() throws IOException
	{
		return ConnectionFactory.createConnection(createConfiguration());
	}

}
