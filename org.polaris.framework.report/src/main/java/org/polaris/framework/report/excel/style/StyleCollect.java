package org.polaris.framework.report.excel.style;

import java.util.*;

/**
 * 样式的集合,放在内存中
 * 
 * @author C4ISR
 * 
 */
public class StyleCollect
{
	private final HashMap<String, Style> map = new HashMap<String, Style>();

	public void put(String styleName, Style style)
	{
		map.put(styleName, style);
	}

	public void clear()
	{
		map.clear();
	}

	public Style getStyle(String styleName)
	{
		Style style = map.get(styleName);
		if (style == null)
			return new Style();
		return (Style) style.clone();
	}

	public int size()
	{
		return map.keySet().size();
	}
}
