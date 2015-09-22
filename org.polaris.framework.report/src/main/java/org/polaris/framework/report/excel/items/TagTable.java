package org.polaris.framework.report.excel.items;

import java.util.*;

/**
 * 表格
 * 
 * @author C4ISR
 * 
 */
public class TagTable
{
	private int leftmargin = 0;
	private String styleName;
	/**
	 * 行元素,可能是TagTr也有可能是TagIterator
	 */
	private List<Object> rowList;

	/**
	 * 表格的线宽
	 */
	private int border = 0;
	/**
	 * 表格外框的线宽
	 */
	private int outborder = 0;

	public TagTable()
	{
		rowList = new ArrayList<Object>();
	}

	public void addTr(TagTr tr)
	{
		rowList.add(tr);
	}

	public int getLeftmargin()
	{
		return leftmargin;
	}

	public void setLeftmargin(int leftmargin)
	{
		this.leftmargin = leftmargin;
	}

	public List<Object> getRowList()
	{
		return rowList;
	}

	public void setRowList(List<Object> rowList)
	{
		this.rowList = rowList;
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
		rowList.clear();
	}

	public int getBorder()
	{
		return border;
	}

	public void setBorder(int border)
	{
		this.border = border;
	}

	public int getOutborder()
	{
		return outborder;
	}

	public void setOutborder(int outborder)
	{
		this.outborder = outborder;
	}
}
