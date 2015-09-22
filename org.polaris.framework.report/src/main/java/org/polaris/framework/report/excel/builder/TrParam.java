package org.polaris.framework.report.excel.builder;

public class TrParam
{
	private int[] array;
	private int dr;
	
	public TrParam(int[] array,int dr)
	{
		this.array=array;
		this.dr=dr;
	}

	public int[] getArray()
	{
		return array;
	}

	public void setArray(int[] array)
	{
		this.array = array;
	}

	public int getDr()
	{
		return dr;
	}

	public void setDr(int dr)
	{
		this.dr = dr;
	}
}
