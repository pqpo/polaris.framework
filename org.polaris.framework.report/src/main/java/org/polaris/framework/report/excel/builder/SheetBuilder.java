package org.polaris.framework.report.excel.builder;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.report.excel.items.TagBr;
import org.polaris.framework.report.excel.items.TagSheet;
import org.polaris.framework.report.excel.items.TagTable;
import org.springframework.stereotype.Service;

import jxl.write.WritableSheet;

@Service
public class SheetBuilder
{
	@Resource
	private TableBuilder tableBuilder;

	Log log = LogFactory.getLog(getClass());

	public void builder(TagSheet tagSheet, WritableSheet sheet, Object data)
	{
		int row = 0;// 设置初始的行号
		Iterator<?> it = tagSheet.getContentList().iterator();
		while (it.hasNext())
		{
			Object obj = it.next();
			if (obj instanceof TagBr)
			{
				// 如果对象为<br/>
				row++;
			}
			else if (obj instanceof TagTable)
			{
				// 如果是table对象
				TagTable table = (TagTable) obj;
				row += tableBuilder.build(sheet, data, table, row);
			}
			else
			{
				log.error("!无法处理的sheet下的对象:" + obj.getClass());
			}
		}
	}

}
