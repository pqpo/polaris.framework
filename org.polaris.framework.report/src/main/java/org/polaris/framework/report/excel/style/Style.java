package org.polaris.framework.report.excel.style;

/**
 * 样式
 * 
 * @author C4ISR
 * 
 */
public class Style implements Cloneable
{
	private Font font = new Font();
	private String bgColor = "white";
	private String align = "center";
	private String valign = "center";
	private int border = 1;
	private int outborder = 0;// 只使用于table

	@Override
	public Object clone()
	{
		Style style = new Style();
		style.font = (Font) font.clone();
		style.bgColor = this.bgColor;
		style.align = this.align;
		style.valign = this.valign;
		style.border = this.border;
		style.outborder = this.outborder;
		return style;
	}

	public int getOutborder()
	{
		return outborder;
	}

	public void setOutborder(int outborder)
	{
		this.outborder = outborder;
	}

	public String getAlign()
	{
		return align;
	}

	public void setAlign(String align)
	{
		this.align = align;
	}

	public int getBorder()
	{
		return border;
	}

	public void setBorder(int border)
	{
		this.border = border;
	}

	public String getValign()
	{
		return valign;
	}

	public void setValign(String valign)
	{
		this.valign = valign;
	}

	public String getBgColor()
	{
		return bgColor;
	}

	public void setBgColor(String bgColor)
	{
		this.bgColor = bgColor;
	}

	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		this.font = font;
	}

}
