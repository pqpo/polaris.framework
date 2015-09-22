package org.polaris.framework.report.excel.items;

import java.util.*;

public class TagSheet
{
	/**
	 * sheet的名称
	 */
	private String text;
	/**
	 * 可能是<br/>
	 * 也有可能是
	 * <table/>
	 */
	private List<Object> contentList;

	public TagSheet()
	{
		contentList = new ArrayList<Object>();
	}

	public void addBr(TagBr br)
	{
		contentList.add(br);
	}

	public void addTable(TagTable table)
	{
		contentList.add(table);
	}

	public List<Object> getContentList()
	{
		return contentList;
	}

	public void setContentList(List<Object> contentList)
	{
		this.contentList = contentList;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void clear()
	{
		contentList.clear();
	}
}
