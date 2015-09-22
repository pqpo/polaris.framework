package org.polaris.framework.report.excel.items;

import org.polaris.framework.report.excel.style.Style;

/**
 * 单元格
 * 
 * @author C4ISR
 * 
 */
public class TagTd
{
	private String styleName;
	private int rowspan = 1;
	private int colspan = 1;
	/**
	 * 单元格的内容:可能是字符串/图片/静态字符串
	 */
	private Object content;

	/**
	 * 横向位置
	 */
	private String align = "left";
	/**
	 * 纵向位置
	 */
	private String valign = "center";
	private int width = 10;
	private int height = 10;

	/**
	 * 单元格的样式
	 */
	private Style style;

	public Style getStyle()
	{
		return style;
	}

	public void setStyle(Style style)
	{
		this.style = style;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getColspan()
	{
		return colspan;
	}

	public void setColspan(int colspan)
	{
		this.colspan = colspan;
	}

	public Object getContent()
	{
		return content;
	}

	public void setContent(Object content)
	{
		this.content = content;
	}

	public int getRowspan()
	{
		return rowspan;
	}

	public void setRowspan(int rowspan)
	{
		this.rowspan = rowspan;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}

	public String getAlign()
	{
		return align;
	}

	public void setAlign(String align)
	{
		this.align = align;
	}

	public String getValign()
	{
		return valign;
	}

	public void setValign(String valign)
	{
		this.valign = valign;
	}
}
