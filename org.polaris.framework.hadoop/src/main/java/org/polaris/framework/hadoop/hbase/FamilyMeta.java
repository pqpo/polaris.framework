package org.polaris.framework.hadoop.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;
import org.polaris.framework.hadoop.CompressionType;


/**
 * HBase表的Family元数据
 * 
 * @author wang.sheng
 * 
 */
public class FamilyMeta
{
	private byte[] name;
	private byte[][] columnNames;
	private Algorithm algorithm;
	private Integer timeToLive;

	public FamilyMeta(String name, String[] columnNames, CompressionType compressionType, Integer timeToLive)
	{
		if (StringUtils.isEmpty(name))
		{
			throw new IllegalArgumentException("Family Name cannot empty!");
		}
		this.name = Bytes.toBytes(name);
		this.columnNames = new byte[columnNames.length][];
		for (int i = 0; i < columnNames.length; i++)
		{
			this.columnNames[i] = Bytes.toBytes(columnNames[i]);
		}
		this.algorithm = toAlgorithm(compressionType);
		this.timeToLive = timeToLive;
	}

	/**
	 * 将枚举CompressionType转换为Algorithm
	 * 
	 * @param compressionType
	 * @return
	 */
	public static Algorithm toAlgorithm(CompressionType compressionType)
	{
		if (compressionType == null)
		{
			return Algorithm.NONE;
		}
		else if (compressionType == CompressionType.GZ)
		{
			return Algorithm.GZ;
		}
		else if (compressionType == CompressionType.LZO)
		{
			return Algorithm.LZO;
		}
		else if (compressionType == CompressionType.LZ4)
		{
			return Algorithm.LZ4;
		}
		else if (compressionType == CompressionType.SNAPPY)
		{
			return Algorithm.SNAPPY;
		}
		return Algorithm.NONE;
	}

	public byte[] getName()
	{
		return name;
	}

	public byte[][] getColumnNames()
	{
		return columnNames;
	}

	public int getColumnNumber()
	{
		return columnNames.length;
	}

	public Algorithm getAlgorithm()
	{
		return algorithm;
	}

	/**
	 * 获取记录存活时间
	 * 
	 * @return
	 */
	public Integer getTimeToLive()
	{
		return timeToLive;
	}

}
