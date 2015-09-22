package org.polaris.framework.hadoop.hbase.meta;

import java.util.ArrayList;
import java.util.List;

import org.polaris.framework.common.xml.annotation.XmlAttribute;
import org.polaris.framework.common.xml.annotation.XmlList;
import org.polaris.framework.hadoop.CompressionType;


public class Family
{
	@XmlAttribute
	private String name;
	@XmlList(name = "column", itemClass = Column.class)
	private List<Column> columnList;
	@XmlAttribute
	private String description;
	@XmlAttribute
	private CompressionType compressionType;
	@XmlAttribute
	private Integer timeToLive;

	public String getDescription()
	{
		return description;
	}

	public String getName()
	{
		return name;
	}

	public List<Column> getColumnList()
	{
		if (columnList == null)
		{
			columnList = new ArrayList<Column>();
		}
		return columnList;
	}

	public CompressionType getCompressionType()
	{
		return compressionType;
	}

	public Integer getTimeToLive()
	{
		return timeToLive;
	}

}
