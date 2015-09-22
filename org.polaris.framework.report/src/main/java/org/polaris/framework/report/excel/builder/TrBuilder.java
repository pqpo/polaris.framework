package org.polaris.framework.report.excel.builder;

import java.util.Iterator;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.report.excel.items.TagTd;
import org.polaris.framework.report.excel.items.TagTr;
import org.springframework.stereotype.Service;

import jxl.write.WritableSheet;

@Service
public class TrBuilder
{
	Log log = LogFactory.getLog(getClass());

	@Resource
	private CellBuilder cellBuilder;

	/**
	 * 获取最小单元格的行数
	 * 
	 * @param tr
	 * @return
	 */
	private int getCols(TagTr tr)
	{
		int count = 0;
		Iterator<?> it = tr.getTagTdList().iterator();
		while (it.hasNext())
		{
			TagTd td = (TagTd) it.next();
			count += td.getColspan();// colspan默认为1
		}
		return count;
	}

	public TrParam build(WritableSheet sheet, Object data, int row, int col, TagTr tagTr, int[] array)
	{
		// 当array为null的时候,创建数组并返回,否则直接修改array并返回null
		Iterator<?> it = tagTr.getTagTdList().iterator();
		boolean isFirst = false;
		int minRow = 8;
		while (it.hasNext())
		{
			TagTd tagTd = (TagTd) it.next();
			if (array == null)
			{
				// 说明是表格的第一行
				int size = getCols(tagTr);
				array = new int[size];
				for (int i = 0; i < array.length; i++)
				{
					array[i] = 0;
				}
				isFirst = true;
			}
			int colspan = tagTd.getColspan();
			int rowspan = tagTd.getRowspan();
			int index = -1;
			for (int i = 0; i < array.length && index < 0; i++)
			{
				if (array[i] == 0)
				{
					index = i;
				}
			}
			if (index < 0)
			{
				// 没有适合的行输出
				log.warn("cannot print row!");
				continue;
			}
			for (int i = 0; i < colspan; i++)
			{
				array[index + i] = rowspan;
			}
			// 输出到sheet中
			cellBuilder.build(sheet, data, tagTd, row, col + index, tagTd.getStyle());
			if (minRow > rowspan)
			{
				// 设置最大的行数
				minRow = rowspan;
			}
		}
		return new TrParam(isFirst ? array : null, minRow);
	}

}
