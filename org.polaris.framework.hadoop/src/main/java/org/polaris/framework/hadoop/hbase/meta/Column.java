package org.polaris.framework.hadoop.hbase.meta;

import org.polaris.framework.common.xml.annotation.XmlAttribute;

public class Column
{
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String description;

	public String getDescription()
	{
		return description;
	}

	public String getName()
	{
		return name;
	}

}
