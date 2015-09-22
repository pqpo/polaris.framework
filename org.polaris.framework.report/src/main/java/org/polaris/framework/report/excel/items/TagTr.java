package org.polaris.framework.report.excel.items;

import java.util.*;

/**
 * 单元格的一行表格
 * 
 * @author C4ISR
 * 
 */
public class TagTr
{
	/**
	 * 一行单元格的样式
	 */
	private String styleName;
	private List<Object> tagTdList;

	public TagTr()
	{
		tagTdList = new ArrayList<Object>();
	}

	public void addTd(TagTd td)
	{
		tagTdList.add(td);
	}

	public List<Object> getTagTdList()
	{
		return tagTdList;
	}

	public void setTagTdList(List<Object> tagTdList)
	{
		this.tagTdList = tagTdList;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}

	public void clear()
	{
		tagTdList.clear();
	}
}
