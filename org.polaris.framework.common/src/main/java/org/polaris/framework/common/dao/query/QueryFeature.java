package org.polaris.framework.common.dao.query;

import java.util.List;

public class QueryFeature
{
	/**
	 * 查询语句
	 */
	private String[] qls;
	/**
	 * 对应的参数
	 */
	private List<Object> paramList;

	public String[] getQls()
	{
		return qls;
	}

	public void setQls(String[] qls)
	{
		this.qls = qls;
	}

	public Object[] getParams()
	{
		if (paramList != null)
		{
			return paramList.toArray();
		}
		return new Object[0];
	}

	public List<Object> getParamList()
	{
		return paramList;
	}

	public void setParams(List<Object> paramList)
	{
		this.paramList = paramList;
	}

	public boolean isEmpty()
	{
		return qls == null || qls.length == 0;
	}

}
