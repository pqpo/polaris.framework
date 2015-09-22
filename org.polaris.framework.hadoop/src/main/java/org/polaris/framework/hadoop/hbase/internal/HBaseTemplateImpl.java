package org.polaris.framework.hadoop.hbase.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.polaris.framework.common.console.ConsoleSupport;
import org.polaris.framework.common.utils.FileCharsetUtils;
import org.polaris.framework.hadoop.HadoopContext;
import org.polaris.framework.hadoop.hbase.FamilyMeta;
import org.polaris.framework.hadoop.hbase.HBaseTemplate;
import org.polaris.framework.hadoop.hbase.OutputChannel;
import org.polaris.framework.hadoop.hbase.QueryCallbackHandler;
import org.polaris.framework.hadoop.hbase.RowMapper;
import org.polaris.framework.hadoop.hbase.RowkeySplitService;
import org.polaris.framework.hadoop.hbase.TableMeta;
import org.polaris.framework.hadoop.hbase.storage.PutConverter;
import org.polaris.framework.hadoop.hbase.storage.StorageSession;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;

/**
 * HBase的各项操作模板
 * 
 * @author wang.sheng
 * 
 */
@Service
class HBaseTemplateImpl implements HBaseTemplate, ConsoleSupport
{
	private static final long WRITE_BUFFER_SIZE = 8 * 1024 * 1024L;

	Log log = LogFactory.getLog(getClass());

	@Resource
	private HadoopContext hadoopContext;
	@Resource
	private ApplicationContext applicationContext;

	@Override
	public OutputChannel createOutputChannel(String name)
	{
		return this.createOutputChannel(Bytes.toBytes(name));
	}

	@Override
	public HadoopContext getHadoopContext()
	{
		return this.hadoopContext;
	}

	@Override
	public OutputChannel createOutputChannel(final byte[] name)
	{
		return new OutputChannel()
		{
			private Connection connection = null;
			private boolean isOpen = false;
			private BufferedMutator bufferedMutator = null;

			@Override
			public void open()
			{
				if (isOpen)
				{
					return;
				}
				try
				{
					connection = hadoopContext.createConnection();
					TableName tableName = TableName.valueOf(name);
					BufferedMutatorParams bufferedMutatorParams = new BufferedMutatorParams(tableName);
					bufferedMutatorParams.writeBufferSize(WRITE_BUFFER_SIZE);
					bufferedMutator = connection.getBufferedMutator(bufferedMutatorParams);
					isOpen = true;
				}
				catch (Exception e)
				{
					log.error("OutputChannel open failed!", e);
					isOpen = false;
				}
			}

			@Override
			public void write(Put put)
			{
				if (!isOpen)
				{
					throw new RuntimeException("OutputChannel is not open! or open failed!");
				}
				try
				{
					bufferedMutator.mutate(put);
				}
				catch (IOException e)
				{
					log.warn("table put failed!", e);
				}
			}

			@Override
			public void write(List<Put> putList)
			{
				if (!isOpen)
				{
					throw new RuntimeException("OutputChannel is not open! or open failed!");
				}
				try
				{
					bufferedMutator.mutate(putList);
				}
				catch (IOException e)
				{
					log.warn("table put failed!", e);
				}
			}

			@Override
			public void close()
			{
				IOUtils.closeQuietly(bufferedMutator);
				IOUtils.closeQuietly(connection);
				isOpen = false;
			}

		};
	}

	@Override
	public Result get(String name, String key)
	{
		if (StringUtils.isBlank(name))
		{
			throw new IllegalArgumentException("name cannot empty!");
		}
		if (StringUtils.isEmpty(key))
		{
			throw new IllegalArgumentException("key cannot empty!");
		}
		Connection connection = null;
		Table table = null;
		ResultScanner resultScanner = null;
		try
		{
			connection = hadoopContext.createConnection();
			TableName tableName = TableName.valueOf(name);
			table = connection.getTable(tableName);
			Get get = new Get(Bytes.toBytes(key));
			Result result = table.get(get);
			if (result == null || result.isEmpty())
			{
				return null;
			}
			else
			{
				return result;
			}
		}
		catch (Exception e)
		{
			log.error("query failed! table: " + name, e);
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(resultScanner);
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(connection);
		}
	}

	@Override
	public void query(String name, String startKey, String endKey, QueryCallbackHandler handler)
	{
		if (StringUtils.isBlank(name))
		{
			throw new IllegalArgumentException("name cannot empty!");
		}
		if (handler == null)
		{
			throw new IllegalArgumentException("handler cannot null!");
		}
		Connection connection = null;
		Table table = null;
		ResultScanner resultScanner = null;
		try
		{
			connection = hadoopContext.createConnection();
			TableName tableName = TableName.valueOf(name);
			table = connection.getTable(tableName);
			Scan scan = new Scan();
			scan.setCacheBlocks(false);
			scan.setBatch(500);
			if (!StringUtils.isEmpty(startKey))
			{
				scan.setStartRow(Bytes.toBytes(startKey));
			}
			if (!StringUtils.isEmpty(endKey))
			{
				scan.setStopRow(Bytes.toBytes(endKey));
			}
			resultScanner = table.getScanner(scan);
			handler.process(resultScanner.iterator());
		}
		catch (Exception e)
		{
			log.error("query failed! table: " + name, e);
		}
		finally
		{
			IOUtils.closeQuietly(resultScanner);
			IOUtils.closeQuietly(table);
			IOUtils.closeQuietly(connection);
		}
	}

	@Override
	public void query(String name, QueryCallbackHandler handler)
	{
		this.query(name, null, null, handler);
	}

	@Override
	public void deleteTable(String name)
	{
		Connection connection = null;
		Admin admin = null;
		try
		{
			connection = hadoopContext.createConnection();
			admin = connection.getAdmin();
			TableName tableName = TableName.valueOf(name);
			if (admin.tableExists(tableName))
			{
				admin.disableTable(tableName);
				admin.deleteTable(tableName);
				log.info("HBase table: " + name + " deleted successful!");
			}
			else
			{
				log.info("HBase table:" + name + " not exist!");
			}
		}
		catch (Exception e)
		{
			log.error("deleteTable failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(admin);
			IOUtils.closeQuietly(connection);
		}

	}

	@Override
	public void createTable(TableMeta tableMeta, boolean rebuild)
	{
		Connection connection = null;
		Admin admin = null;
		try
		{
			connection = hadoopContext.createConnection();
			admin = connection.getAdmin();
			String name = Bytes.toString(tableMeta.getName());
			TableName tableName = TableName.valueOf(tableMeta.getName());
			if (admin.tableExists(tableName))
			{
				// 表已经存在
				log.info("HBase Table:" + name + " is already exist!");
				if (rebuild)
				{
					// 删除已经存在的表
					admin.disableTable(tableName);
					admin.deleteTable(tableName);
					log.info("HBase Table:" + name + " deleted successful!");
				}
				else
				{
					// 不需要重新创建,则返回
					return;
				}
			}
			// 表不存在,需要创建表
			HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
			for (FamilyMeta family : tableMeta.getFamilys())
			{
				HColumnDescriptor columnDescriptor = new HColumnDescriptor(family.getName());
				if (family.getAlgorithm() != null)
				{
					// 设置列族压缩算法
					columnDescriptor.setCompressionType(family.getAlgorithm());
				}
				if (family.getTimeToLive() != null)
				{
					// 设置列族的持久化存活时间
					columnDescriptor.setTimeToLive(family.getTimeToLive());
				}
				tableDescriptor.addFamily(columnDescriptor);
			}
			Class<? extends RowkeySplitService> splitClass = tableMeta.getSplitClass();
			if (splitClass != null)
			{
				RowkeySplitService splitService = (RowkeySplitService) splitClass.newInstance();
				String[] splits = splitService.split();
				if (splits != null && splits.length > 0)
				{
					byte[][] keySplits = new byte[splits.length][];
					for (int i = 0; i < splits.length; i++)
					{
						keySplits[i] = Bytes.toBytes(splits[i]);
					}
					// 进行预分区的表创建
					admin.createTable(tableDescriptor, keySplits);
				}
				else
				{
					// 无分区信息,则直接建表
					admin.createTable(tableDescriptor);
				}
			}
			else
			{
				// 不分区的表创建
				admin.createTable(tableDescriptor);
			}
			log.info("HBase Table:" + name + " created successful!");
		}
		catch (Exception e)
		{
			log.error("createTable failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(admin);
			IOUtils.closeQuietly(connection);
		}
	}

	@Override
	public void storage(byte[] tableName, DataSource dataSource, String sql, PutConverter<ResultSet> putConverter)
	{
		if (putConverter == null)
		{
			throw new IllegalArgumentException("ResultSetPutConverter cannot null!");
		}
		OutputChannel channel = null;
		java.sql.Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			log.info("start importDatabase! sql: " + sql);
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery();
			channel = this.createOutputChannel(tableName);
			channel.open();
			int row = 0;
			while (rs.next())
			{
				Put put = putConverter.convert(row, rs);
				if (put != null)
				{
					channel.write(put);
				}
				row++;
				if (row % 100000 == 0)
				{
					// 每导入10万条记录输出一行日志
					log.info("importDatabase sql: " + sql + ", imported row: " + row);
				}
			}
			log.info("importDatabase sql: " + sql + ", imported successful! row: " + row);
		}
		catch (Exception e)
		{
			log.error("importDatabase sql: " + sql + ", imported failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(channel);
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}
	}

	@Override
	public void storage(byte[] tableName, File textFile, char separator, PutConverter<String[]> putConverter)
	{
		if (putConverter == null)
		{
			throw new IllegalArgumentException("StringArrayPutConverter cannot null!");
		}
		CSVReader csvReader = null;
		InputStream is = null;
		InputStreamReader inputStreamReader = null;
		OutputChannel outputChannel = null;
		try
		{
			String charset = FileCharsetUtils.getCharset(textFile);
			log.info("start import TextFile: " + textFile.getName() + ", charset=" + charset);
			is = new FileInputStream(textFile);
			inputStreamReader = new InputStreamReader(is, charset);
			csvReader = new CSVReader(inputStreamReader, separator);
			outputChannel = this.createOutputChannel(tableName);
			outputChannel.open();
			int row = 0;
			int putCount = 0;
			while (true)
			{
				String[] datas = csvReader.readNext();
				if (datas == null)
				{
					break;
				}
				Put put = putConverter.convert(row, datas);
				if (put != null)
				{
					outputChannel.write(put);
					putCount++;
				}
				row++;
				if (row % 100000 == 0)
				{
					// 每导入10万条记录输出一行日志
					log.info("TextFile: " + textFile.getName() + " imported row: " + row);
				}
			}
			log.info("TextFile: " + textFile.getName() + " imported successful! File row: " + row + ", Table row: " + putCount);
		}
		catch (Exception e)
		{
			log.error("TextFile: " + textFile.getName() + "import failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(outputChannel);
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(inputStreamReader);
			IOUtils.closeQuietly(is);
		}
	}

	@Override
	public <T> T get(String name, String key, RowMapper<T> rowMapper)
	{
		if (rowMapper == null)
		{
			throw new IllegalArgumentException("RowMapper cannot null!");
		}
		Result result = this.get(name, key);
		if (result == null)
		{
			return null;
		}
		return rowMapper.mapRow(result);
	}

	@Override
	public <T> List<T> queryForList(String name, String startKey, String endKey, final RowMapper<T> rowMapper)
	{
		if (rowMapper == null)
		{
			throw new IllegalArgumentException("RowMapper cannot null!");
		}
		final List<T> list = new ArrayList<T>();
		this.query(name, startKey, endKey, new QueryCallbackHandler()
		{
			@Override
			public void process(Iterator<Result> iterator)
			{
				while (iterator.hasNext())
				{
					Result result = iterator.next();
					T object = rowMapper.mapRow(result);
					if (object != null)
					{
						list.add(object);
					}
				}
			}
		});
		return list;
	}

	@Override
	public <T> List<T> queryForList(String name, RowMapper<T> rowMapper)
	{
		return this.queryForList(name, null, null, rowMapper);
	}

	@Override
	public StorageSession openStorageSession(String tableName, int poolSize)
	{
		return this.openStorageSession(Bytes.toBytes(tableName), poolSize);
	}

	@Override
	public StorageSession openStorageSession(byte[] tableName, int poolSize)
	{
		return new StorageSessionImpl(tableName, poolSize);
	}

	/**
	 * 存储会话的实现类
	 * 
	 * @author wang.sheng
	 * 
	 */
	class StorageSessionImpl implements StorageSession
	{
		/**
		 * 任务执行器
		 */
		private ThreadPoolTaskExecutor taskExecutor;
		private boolean[] syncObject = new boolean[0];
		private OutputChannel outputChannel;

		public StorageSessionImpl(byte[] tableName, int poolSize)
		{
			taskExecutor = applicationContext.getBean("threadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
			taskExecutor.setCorePoolSize(poolSize);
			taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
			outputChannel = createOutputChannel(tableName);
			outputChannel.open();
		}

		private synchronized void save(List<Put> putList)
		{
			outputChannel.write(putList);
		}

		@Override
		public void waitForCompletion()
		{
			while (taskExecutor.getActiveCount() > 0)
			{
				synchronized (syncObject)
				{
					try
					{
						syncObject.wait(5000L);
					}
					catch (InterruptedException e)
					{
						log.warn("InterruptedException", e);
					}
				}
			}
			// 任务全部执行完成后,关闭写数据通道
			IOUtils.closeQuietly(outputChannel);
			// 关闭任务池
			taskExecutor.shutdown();
		}

		@Override
		public void addRequest(final String title, final DataSource dataSource, final String sql, final PutConverter<ResultSet> putConverter)
		{
			if (putConverter == null)
			{
				throw new IllegalArgumentException("PutConverter cannot null!");
			}
			taskExecutor.execute(new Runnable()
			{
				@Override
				public void run()
				{
					java.sql.Connection connection = null;
					PreparedStatement statement = null;
					ResultSet rs = null;
					List<Put> putList = new ArrayList<Put>(10000);// 创建缓存
					int putCount = 0;
					try
					{
						log.info("start importDatabase! sql: " + sql);
						connection = dataSource.getConnection();
						statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
						statement.setFetchSize(Integer.MIN_VALUE);
						rs = statement.executeQuery();
						int row = 0;
						while (rs.next())
						{
							Put put = putConverter.convert(row, rs);
							if (put != null)
							{
								putList.add(put);
								if (putList.size() >= 10000)
								{
									// 保存入库
									save(putList);
									putList.clear();
								}
								putCount++;
							}
							row++;
							if (row % 100000 == 0)
							{
								// 每导入10万条记录输出一行日志
								log.info("importDatabase " + title + ", imported row: " + row);
							}
						}
						if (!putList.isEmpty())
						{
							// 保存入库
							save(putList);
							putList.clear();
						}
						putList = null;
						log.info("importDatabase " + title + ", imported successful! row: " + row + ", Table row: " + putCount);
					}
					catch (Exception e)
					{
						log.error("importDatabase " + title + ", imported failed!", e);
					}
					finally
					{
						JdbcUtils.closeResultSet(rs);
						JdbcUtils.closeStatement(statement);
						JdbcUtils.closeConnection(connection);
					}
				}
			});
		}

		@Override
		public void addRequest(final File textFile, final char separator, final PutConverter<String[]> putConverter)
		{
			if (putConverter == null)
			{
				throw new IllegalArgumentException("PutConverter cannot null!");
			}
			taskExecutor.execute(new Runnable()
			{
				@Override
				public void run()
				{
					CSVReader csvReader = null;
					InputStream is = null;
					InputStreamReader inputStreamReader = null;
					List<Put> putList = new ArrayList<Put>(10000);// 创建缓存
					try
					{
						String charset = FileCharsetUtils.getCharset(textFile);
						log.info("start import TextFile: " + textFile.getName() + ", charset=" + charset);
						is = new FileInputStream(textFile);
						inputStreamReader = new InputStreamReader(is, charset);
						csvReader = new CSVReader(inputStreamReader, separator);
						int row = 0;
						int putCount = 0;
						while (true)
						{
							String[] datas = csvReader.readNext();
							if (datas == null)
							{
								break;
							}
							Put put = putConverter.convert(row, datas);
							if (put != null)
							{
								putList.add(put);
								if (putList.size() >= 10000)
								{
									save(putList);
									putList.clear();
								}
								putCount++;
							}
							row++;
							if (row % 100000 == 0)
							{
								// 每导入10万条记录输出一行日志
								log.info("TextFile: " + textFile.getName() + " imported row: " + row);
							}
						}
						if (!putList.isEmpty())
						{
							// 保存入库
							save(putList);
							putList.clear();
						}
						putList = null;
						log.info("TextFile: " + textFile.getName() + " imported successful! File row: " + row + ", Table row: " + putCount);
					}
					catch (Exception e)
					{
						log.error("TextFile: " + textFile.getName() + "import failed!", e);
					}
					finally
					{
						IOUtils.closeQuietly(csvReader);
						IOUtils.closeQuietly(inputStreamReader);
						IOUtils.closeQuietly(is);
					}
				}
			});
		}
	}

	@Override
	public void storage(String tableName, File textFile, char separator, PutConverter<String[]> putConverter)
	{
		this.storage(Bytes.toBytes(tableName), textFile, separator, putConverter);
	}

	@Override
	public void storage(String tableName, DataSource dataSource, String sql, PutConverter<ResultSet> putConverter)
	{
		this.storage(Bytes.toBytes(tableName), dataSource, sql, putConverter);
	}

	@Override
	public String commandRegex()
	{
		return "^delete table (\\w+)$";
	}

	@Override
	public void execute(String[] args)
	{
		this.deleteTable(args[0]);
	}

}
