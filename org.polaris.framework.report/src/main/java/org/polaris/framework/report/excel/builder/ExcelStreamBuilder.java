package org.polaris.framework.report.excel.builder;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.report.excel.items.TagExcel;
import org.polaris.framework.report.excel.items.TagSheet;
import org.springframework.stereotype.Service;

@Service
public class ExcelStreamBuilder
{
	Log log = LogFactory.getLog(getClass());

	@Resource
	private SheetBuilder sheetBuilder;

	public void builder(WritableWorkbook wb, TagExcel excel, Object data)
	{
		log.info("---> start create excel file!");
		Iterator<TagSheet> it = excel.getSheetList().iterator();
		int count = 0;
		while (it.hasNext())
		{
			TagSheet ts = it.next();
			log.info("--->add sheet: text=" + ts.getText());
			WritableSheet sheet = wb.createSheet(ts.getText(), count++);
			sheetBuilder.builder(ts, sheet, data);
		}
		log.info("<--- excel file created successful!");
	}

}
