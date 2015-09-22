package org.polaris.framework.report.excel;

import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.common.template.FreemarkerTemplate;
import org.polaris.framework.report.excel.builder.ExcelStreamBuilder;
import org.polaris.framework.report.excel.items.TagExcel;
import org.polaris.framework.report.excel.parse.ExcelConfigParser;
import org.springframework.stereotype.Service;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

/**
 * Excel报表服务
 * 
 * @author wang.sheng
 * 
 */
@Service
public class ExcelReportService
{
	@Resource
	private ExcelConfigParser excelConfigParser;
	@Resource
	private ExcelStreamBuilder excelStreamBuilder;
	@Resource
	private FreemarkerTemplate freemarkerTemplate;

	Log log = LogFactory.getLog(getClass());

	/**
	 * 根据传入的FTL文件路径,以及数据,向os输出Excel文件的二进制流
	 * 
	 * @param path
	 * @param data
	 * @param os
	 */
	public void report(String path, Object data, OutputStream os)
	{
		StringWriter writer = null;
		String result = null;
		try
		{
			writer = new StringWriter();
			freemarkerTemplate.execute(writer, path, data);
			writer.flush();
			result = writer.getBuffer().toString();
		}
		catch (Exception e)
		{
			log.error("report failed! path: " + path, e);
			return;
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}
		Reader reader = null;
		TagExcel excel = null;
		try
		{
			reader = new StringReader(result);
			excel = excelConfigParser.parse(reader);
		}
		catch (Exception e)
		{
			log.error("report failed! path: " + path, e);
			return;
		}
		finally
		{
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(reader);
		}
		WritableWorkbook wb = null;
		try
		{
			wb = Workbook.createWorkbook(os);
			excelStreamBuilder.builder(wb, excel, data);
			wb.write();
		}
		catch (Exception e)
		{
			log.error("report failed! path: " + path, e);
		}
		finally
		{
			try
			{
				wb.close();
			}
			catch (Exception e)
			{
				log.warn("WritableWorkbook close failed!", e);
			}
		}

	}
}
