package org.polaris.framework.hadoop;

/**
 * 压缩类型
 * 
 * @author wang.sheng
 * 
 */
public enum CompressionType
{
	GZ, BZ2, LZ4, LZO, SNAPPY;

	/**
	 * 获取对应压缩文件的后缀
	 * 
	 * @return
	 */
	public static String suffix(CompressionType type)
	{
		if (type == null)
		{
			return "";
		}
		else if (type == GZ)
		{
			return ".zip";
		}
		else if (type == BZ2)
		{
			return ".bz2";
		}
		else if (type == LZ4)
		{
			return ".lz4";
		}
		else if (type == LZO)
		{
			return ".lzo";
		}
		else if (type == SNAPPY)
		{
			return ".snappy";
		}
		return "";
	}
}
