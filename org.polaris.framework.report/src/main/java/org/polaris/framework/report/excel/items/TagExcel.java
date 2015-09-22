package org.polaris.framework.report.excel.items;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel的总的对象
 * 
 * @author C4ISR
 * 
 */
public class TagExcel
{
	private String style;
	private List<TagSheet> sheetList;

	public TagExcel()
	{
		sheetList = new ArrayList<TagSheet>();
	}

	public void addSheet(TagSheet sheet)
	{
		sheetList.add(sheet);
	}

	public List<TagSheet> getSheetList()
	{
		return sheetList;
	}

	public void setSheetList(List<TagSheet> sheetList)
	{
		this.sheetList = sheetList;
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	public void clear()
	{
		sheetList.clear();
	}
}
