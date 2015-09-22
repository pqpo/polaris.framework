package org.polaris.framework.report.excel.builder;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.report.barcode.BarcodeService;
import org.polaris.framework.report.excel.items.TagBarCode;
import org.polaris.framework.report.excel.items.TagImage;
import org.polaris.framework.report.excel.items.TagTd;
import org.polaris.framework.report.excel.style.Style;
import org.polaris.framework.report.excel.style.WriterableCellStyleSetter;
import org.springframework.stereotype.Service;

import jxl.CellView;
import jxl.write.Label;
import jxl.write.WritableSheet;

@Service
public class CellBuilder
{
	Log log = LogFactory.getLog(getClass());

	@Resource
	private WriterableCellStyleSetter writerableCellStyleSetter;
	@Resource
	private BarcodeService barcodeService;

	public void build(WritableSheet sheet, Object data, TagTd tagTd, int row, int col, Style style)
	{
		Object obj = tagTd.getContent();
		int colspan = tagTd.getColspan();
		int rowspan = tagTd.getRowspan();
		try
		{
			// 首先进行合并单元格
			if (rowspan > 1 || colspan > 1)
			{
				sheet.mergeCells(col, row, col + colspan - 1, row + rowspan - 1);
			}
			if (obj instanceof String)
			{
				String value = (String) obj;
				Label label = new Label(col, row, value);
				writerableCellStyleSetter.setStyle(label, style);
				CellView cellView = sheet.getColumnView(col);
				int width = cellView.getSize();
				if (width < tagTd.getWidth() || width > 1000)
				{
					sheet.setColumnView(col, tagTd.getWidth());
					cellView.setAutosize(true);
				}
				sheet.addCell(label);
			}
			else if (obj instanceof TagImage)
			{
				// 添加一个图片
				TagImage ti = (TagImage) obj;
				jxl.write.WritableImage image = new jxl.write.WritableImage(col, row, colspan, rowspan, ti.getValue());
				sheet.addImage(image);
			}
			else if (obj instanceof TagBarCode)
			{
				// 添加一个条形码
				TagBarCode bar = (TagBarCode) obj;
				byte[] b = barcodeService.generateBarcode(bar.getType(), bar.getValue());
				jxl.write.WritableImage image = new jxl.write.WritableImage(col, row, colspan, rowspan, b);
				sheet.addImage(image);
			}
			else
			{
				log.warn("UnKnown Tag: " + obj.getClass());
			}
		}
		catch (Exception e)
		{
			log.error("build failed!", e);
		}
	}
}
