package org.polaris.framework.report.excel.items;

/**
 * 图片对象标签
 * 
 * @author C4ISR
 * 
 */
public class TagImage
{
	/**
	 * 图片宽度,默认为一个单元格
	 */
	private int width = 1;
	/**
	 * 图片高度,默认为一个单元格
	 */
	private int height = 1;
	private byte[] value;

	public byte[] getValue()
	{
		return value;
	}

	public void setValue(byte[] value)
	{
		this.value = value;
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
}
