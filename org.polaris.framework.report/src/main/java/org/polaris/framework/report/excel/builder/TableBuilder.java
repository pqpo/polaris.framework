package org.polaris.framework.report.excel.builder;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.report.excel.items.TagTable;
import org.polaris.framework.report.excel.items.TagTr;
import org.springframework.stereotype.Service;

import jxl.write.WritableSheet;

@Service
public class TableBuilder
{
	Log log = LogFactory.getLog(getClass().getName());

	@Resource
	private TrBuilder trBuilder;

	/**
	 * 返回所生成的行数
	 */
	public int build(WritableSheet sheet, Object data, TagTable tagTable, int row)
	{
		int leftMargin = tagTable.getLeftmargin();
		int[] rowArray = null;// 定义一个行数组,初始化为空
		int currentCol = leftMargin;// 设置当前列
		int currentRow = row;// 设置当前行
		Iterator<Object> it = tagTable.getRowList().iterator();
		int count = 0;
		while (it.hasNext())
		{
			Object obj = it.next();
			if (obj instanceof TagTr)
			{
				TagTr tr = (TagTr) obj;
				// 处理这个tr
				TrParam result = trBuilder.build(sheet, data, currentRow, currentCol, tr, rowArray);
				if (result.getArray() != null)
				{
					rowArray = result.getArray();
				}
				for (int i = 0; i < rowArray.length; i++)
				{
					rowArray[i] -= result.getDr();
				}
				currentRow += result.getDr();
				count += result.getDr();
			}
			else
			{
				log.warn("UnKnown tag: " + obj.getClass());
			}
		}
		return count;
	}
}
