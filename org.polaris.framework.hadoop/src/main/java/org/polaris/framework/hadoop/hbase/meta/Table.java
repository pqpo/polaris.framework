package org.polaris.framework.hadoop.hbase.meta;

import java.util.List;

import org.polaris.framework.common.xml.annotation.XmlAttribute;
import org.polaris.framework.common.xml.annotation.XmlList;

public class Table
{
	@XmlAttribute
	private String name;
	@XmlList(name = "family", itemClass = Family.class)
	private List<Family> familyList;
	@XmlAttribute
	private String description;
	@XmlAttribute
	private String splitclass;

	public String getDescription()
	{
		return description;
	}

	public String getName()
	{
		return name;
	}

	public String getSplitclass()
	{
		return splitclass;
	}

	public List<Family> getFamilyList()
	{
		return familyList;
	}

}
