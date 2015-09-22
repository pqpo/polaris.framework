package org.polaris.framework.hadoop.hdfs.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.polaris.framework.common.console.ConsoleSupport;
import org.polaris.framework.hadoop.CompressionType;
import org.polaris.framework.hadoop.HadoopContext;
import org.polaris.framework.hadoop.hdfs.HdfsTemplate;
import org.polaris.framework.hadoop.hdfs.RowConverter;
import org.polaris.framework.hadoop.hdfs.StorageSession;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

/**
 * HdfsTemplate的实现类
 * 
 * @author wang.sheng
 * 
 */
@Service
public class HdfsTemplateImpl implements HdfsTemplate, ConsoleSupport
{
	Log log = LogFactory.getLog(getClass());

	@Resource
	private HadoopContext hadoopContext;
	@Resource
	private ApplicationContext applicationContext;

	private CompressionCodecFactory compressionCodecFactory;

	@PostConstruct
	protected void init()
	{
		compressionCodecFactory = new CompressionCodecFactory(hadoopContext.createConfiguration());
	}

	@Override
	public OutputStream getOutputStream(String uri) throws IOException
	{
		return this.getOutputStream(uri, true);
	}

	@Override
	public OutputStream getOutputStream(String uri, boolean append) throws IOException
	{
		FileSystem fs = FileSystem.get(hadoopContext.createConfiguration());
		Path path = new Path(uri);
		if (append && fs.exists(path))
		{
			// 如果文件存在则进行追加
			return fs.append(path);
		}
		else
		{
			// 如果文件不存在则创建
			return fs.create(new Path(uri));
		}
	}

	@Override
	public void delete(Path path)
	{
		FileSystem fs = null;
		try
		{
			fs = FileSystem.get(hadoopContext.createConfiguration());
			fs.delete(path, false);
			log.info("hdfs file deleted successful! path: " + path);
		}
		catch (Exception e)
		{
			log.error("deleted failed! uri=" + path, e);
		}
		finally
		{
			IOUtils.closeQuietly(fs);
		}
	}

	@Override
	public void delete(String uri)
	{
		FileSystem fs = null;
		try
		{
			fs = FileSystem.get(hadoopContext.createConfiguration());
			fs.delete(new Path(uri), false);
			log.info("hdfs file deleted successful! uri: " + uri);
		}
		catch (Exception e)
		{
			log.error("deleted failed! uri=" + uri, e);
		}
		finally
		{
			IOUtils.closeQuietly(fs);
		}
	}

	private CompressionCodec getCompressionCodec(CompressionType compressType)
	{
		if (compressType == null)
		{
			return null;
		}
		else if (compressType == CompressionType.GZ)
		{
			return compressionCodecFactory.getCodecByName("gzip");
		}
		else if (compressType == CompressionType.BZ2)
		{
			return compressionCodecFactory.getCodecByName("bzip2");
		}
		else if (compressType == CompressionType.LZO)
		{
			return compressionCodecFactory.getCodecByName("lzo");
		}
		else if (compressType == CompressionType.LZ4)
		{
			return compressionCodecFactory.getCodecByName("lz4");
		}
		else if (compressType == CompressionType.SNAPPY)
		{
			return compressionCodecFactory.getCodecByName("snappy");
		}
		log.warn("UnSupported CompressionType: " + compressType);
		return null;
	}

	@Override
	public void write(File file, String uri, CompressionType compressType)
	{
		this.write(file, uri, compressType, true);
	}

	@Override
	public void write(File file, String uri, CompressionType compressType, boolean append)
	{
		CompressionCodec compressionCodec = this.getCompressionCodec(compressType);
		OutputStream os = null;
		InputStream is = null;
		String size = FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file));
		log.info("start write to HDFS local file: " + file.getAbsolutePath() + ", hdfs: " + uri + ", size: " + size);
		try
		{
			if (compressionCodec != null)
			{
				// 需要支持压缩
				uri += compressionCodec.getDefaultExtension();
				os = compressionCodec.createOutputStream(this.getOutputStream(uri, append));
			}
			else
			{
				// 不需要压缩
				os = this.getOutputStream(uri, append);
			}
			is = new FileInputStream(file);
			IOUtils.copy(is, os);
			log.info("HDFS writed successful! local file: " + file.getName());
		}
		catch (Exception e)
		{
			log.error("write to HDFS failed! local file: " + file.getAbsolutePath() + ", hdfs:" + uri, e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
	}

	@Override
	public void write(File file, String uri)
	{
		this.write(file, uri, null, true);
	}

	@Override
	public void write(File file, String uri, boolean append)
	{
		this.write(file, uri, null, append);
	}

	@Override
	public void write(DataSource dataSource, String sql, String uri, char separator, RowConverter rowConverter)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		CSVWriter csvWriter = null;
		Writer writer = null;
		try
		{
			writer = new OutputStreamWriter(this.getOutputStream(uri));
			csvWriter = new CSVWriter(writer, separator);
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			statement.setFetchSize(Integer.MIN_VALUE);
			log.info("write csv to HDFS from DB, sql: " + sql);
			rs = statement.executeQuery();
			int row = 0;
			if (rowConverter == null)
			{
				// 采用默认的行转换器
				rowConverter = new DefaultRowConverter();
			}
			// 按照行转换器进行输出
			while (rs.next())
			{
				String[] array = rowConverter.convert(row, rs);
				if (array != null)
				{
					csvWriter.writeNext(array, false);
					row++;
					if (row % 100000 == 0)
					{
						log.info("write csv from db, row: " + row + ", sql: " + sql);
					}
				}
			}
			log.info("write to HDFS successful! row: " + row + ", sql: " + sql);
		}
		catch (Exception e)
		{
			log.error("write from database failed! uri=" + uri + " sql=" + sql, e);
		}
		finally
		{
			IOUtils.closeQuietly(csvWriter);
			IOUtils.closeQuietly(writer);
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}
	}

	@Override
	public InputStream getInputStream(String uri) throws IOException
	{
		return FileSystem.get(URI.create(uri), hadoopContext.createConfiguration()).open(new Path(uri));
	}

	@Override
	public StorageSession openStorageSession(String uri, int poolSize, char separator)
	{
		return new StorageSessionImpl(uri, poolSize, separator);
	}

	@Override
	public String commandRegex()
	{
		return "^(\\w+) hdfs (.+)$";
	}

	@Override
	public void execute(String[] args)
	{
		String operate = args[0];
		String uri = args[1];
		if (StringUtils.equals(operate, "read"))
		{
			// 读取HDFS文件,前100行
			InputStream is = null;
			BufferedReader reader = null;
			log.info("read HDFS " + uri + " row 0-100");
			try
			{
				is = this.getInputStream(uri);
				reader = new BufferedReader(new InputStreamReader(is));
				int row = 0;
				while (true)
				{
					String line = reader.readLine();
					if (line == null)
					{
						break;
					}
					log.info("[" + row + "] " + line);
					row++;
					if (row > 100)
					{
						break;
					}
				}
			}
			catch (Exception e)
			{
				log.error("read HDFS failed!", e);
			}
			finally
			{
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(is);
			}
		}
		else if (StringUtils.equals(operate, "delete"))
		{
			this.delete(uri);
		}
		else if (StringUtils.equals(operate, "copy"))
		{
			String[] array = uri.split(" ");
			log.info("copy local file: " + array[0] + " to hdfs:" + array[1]);
			File file = new File(array[0]);
			if (!file.exists())
			{
				log.error("local file: " + array[0] + " is not exists!");
			}
			else if (file.isDirectory())
			{
				// 是目录,则拷贝目录下的所有文件
				this.writeAll(file, null, array[1]);
			}
			else if (file.isFile())
			{
				// 拷贝单个文件
				this.write(file, array[1]);
			}
		}
		else
		{
			log.warn("UnSupported HDFS operate: " + operate);
		}
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
		private CSVWriter csvWriter = null;;
		private OutputStream hdfsOutputStream = null;

		public StorageSessionImpl(String uri, int poolSize, char separator)
		{
			taskExecutor = applicationContext.getBean("threadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
			taskExecutor.setCorePoolSize(poolSize);
			taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
			try
			{
				hdfsOutputStream = getOutputStream(uri);
				csvWriter = new CSVWriter(new OutputStreamWriter(hdfsOutputStream), separator);
			}
			catch (IOException e)
			{
				log.error("getOutputStream failed! uri:" + uri, e);
			}

		}

		private synchronized void save(List<String[]> rowList)
		{
			csvWriter.writeAll(rowList, false);
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
			IOUtils.closeQuietly(csvWriter);
			IOUtils.closeQuietly(hdfsOutputStream);
			// 关闭任务池
			taskExecutor.shutdown();
		}

		@Override
		public void addRequest(final String title, final DataSource dataSource, final String sql, final RowConverter rowConverter)
		{
			taskExecutor.execute(new Runnable()
			{
				@Override
				public void run()
				{
					Connection connection = null;
					PreparedStatement statement = null;
					ResultSet rs = null;
					List<String[]> rowList = new ArrayList<String[]>(10000);// 创建缓存
					try
					{
						log.info("start write csv from db! sql: " + sql);
						connection = dataSource.getConnection();
						statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
						statement.setFetchSize(Integer.MIN_VALUE);
						rs = statement.executeQuery();
						int row = 0;
						RowConverter converter = rowConverter == null ? new DefaultRowConverter() : rowConverter;
						while (rs.next())
						{
							String[] array = converter.convert(row, rs);
							if (array != null)
							{
								rowList.add(array);
								if (rowList.size() >= 10000)
								{
									save(rowList);
									rowList.clear();
								}
								row++;
								if (row % 100000 == 0)
								{
									log.info("write csv from db " + title + " row: " + row);
								}
							}
						}
						if (!rowList.isEmpty())
						{
							save(rowList);
							rowList.clear();
						}
						rowList = null;
						log.info("write csv from db " + title + ", imported successful! row: " + row);
					}
					catch (Exception e)
					{
						log.error("write csv from db " + title + ", imported failed!", e);
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
	}

	/**
	 * 默认的行转换器
	 * 
	 * @author wang.sheng
	 * 
	 */
	private static class DefaultRowConverter implements RowConverter
	{
		@Override
		public String[] convert(int row, ResultSet resultSet) throws SQLException
		{
			int columnCount = resultSet.getMetaData().getColumnCount();
			String[] array = new String[columnCount];
			for (int i = 0; i < columnCount; i++)
			{
				array[i] = resultSet.getString(i + 1);
			}
			return array;
		}
	}

	private void writeAll(File dir, FileFilter fileFilter, String uri)
	{
		if (dir == null || !dir.isDirectory())
		{
			throw new IllegalArgumentException("dir:" + dir + " is not Directory!");
		}
		File[] files = dir.listFiles(fileFilter);
		for (File file : files)
		{
			if (file.isDirectory())
			{
				writeAll(file, fileFilter, uri);// 递归调用
			}
			else if (file.isFile())
			{
				this.write(file, uri);
			}
		}
	}

	@Override
	public Path[] listPaths(Path path)
	{
		FileSystem fs = null;
		try
		{
			fs = FileSystem.get(hadoopContext.createConfiguration());
			RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(path, true);
			List<Path> pathList = new ArrayList<Path>();
			while (iterator.hasNext())
			{
				LocatedFileStatus fileStatus = iterator.next();
				if (fileStatus.isFile())
				{
					pathList.add(fileStatus.getPath());
				}
			}
			return pathList.toArray(new Path[0]);
		}
		catch (Exception e)
		{
			log.error("listPaths failed! path=" + path.toString(), e);
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(fs);
		}
	}

}
