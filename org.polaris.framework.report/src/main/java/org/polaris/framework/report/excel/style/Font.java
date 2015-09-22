package org.polaris.framework.report.excel.style;

/**
 * 字体
 * 
 * @author C4ISR
 * 
 */
public class Font implements Cloneable
{
	private String fontName = "Times New Roman";
	private int size = 10;
	private boolean bold = false;
	private boolean italic = false;
	private String color = "black";
	private boolean underline = false;

	@Override
	public Object clone()
	{
		Font font = new Font();
		font.setFontName(this.fontName);
		font.setSize(this.size);
		font.setBold(this.bold);
		font.setItalic(this.italic);
		font.setColor(this.color);
		font.setUnderline(underline);
		return font;
	}

	public boolean isUnderline()
	{
		return underline;
	}

	public void setUnderline(boolean underline)
	{
		this.underline = underline;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public boolean isBold()
	{
		return bold;
	}

	public void setBold(boolean bold)
	{
		this.bold = bold;
	}

	public String getFontName()
	{
		return fontName;
	}

	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}

	public boolean isItalic()
	{
		return italic;
	}

	public void setItalic(boolean italic)
	{
		this.italic = italic;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

}
