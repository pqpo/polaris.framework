package org.polaris.framework.hadoop.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Table的元数据
 * 
 * @author wang.sheng
 * 
 */
public class TableMeta
{
	static final Log log = LogFactory.getLog(TableMeta.class);

	private byte[] name;
	private FamilyMeta[] familys;
	private Class<? extends RowkeySplitService> splitClass;

	@SuppressWarnings("unchecked")
	public TableMeta(String name, String splitClassName, FamilyMeta[] familys)
	{
		if (StringUtils.isEmpty(name))
		{
			throw new IllegalArgumentException("Table name cannot empty!");
		}
		if (familys == null || familys.length < 1)
		{
			throw new IllegalArgumentException("FamilyMetas cannot empty!");
		}
		if (!StringUtils.isBlank(splitClassName))
		{
			try
			{
				this.splitClass = (Class<? extends RowkeySplitService>) Class.forName(splitClassName, false, Thread.currentThread().getContextClassLoader());
			}
			catch (ClassNotFoundException e)
			{
				log.error("RowkeySplitService forName failed! className=" + splitClassName, e);
			}
		}
		this.name = Bytes.toBytes(name);
		this.familys = familys;
	}

	public FamilyMeta[] getFamilys()
	{
		return familys;
	}

	public byte[] getName()
	{
		return name;
	}

	public Class<? extends RowkeySplitService> getSplitClass()
	{
		return splitClass;
	}

	public int getColumnNumber()
	{
		int sum = 0;
		for (FamilyMeta family : familys)
		{
			sum += family.getColumnNumber();
		}
		return sum;
	}

}
