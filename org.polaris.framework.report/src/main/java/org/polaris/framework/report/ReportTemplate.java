package org.polaris.framework.report;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Resource;

import org.polaris.framework.report.barcode.BarcodeService;
import org.polaris.framework.report.excel.ExcelReportService;
import org.polaris.framework.report.qrcode.QRCodeService;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;

/**
 * 报表类的模板
 * 
 * @author wang.sheng
 * 
 */
@Service
public class ReportTemplate
{
	@Resource
	private ExcelReportService excelReportService;
	@Resource
	private BarcodeService barcodeService;
	@Resource
	private QRCodeService qrCodeService;

	/**
	 * 根据传入的FTL文件路径,以及数据,向os输出Excel文件的二进制流
	 * 
	 * @param path
	 * @param data
	 * @param os
	 */
	public void writeExcel(String path, Object data, OutputStream os)
	{
		this.excelReportService.report(path, data, os);
	}

	/**
	 * 生成指定类型二维码,并输出到OutputStream
	 * 
	 * @param type
	 * @param value
	 * @param os
	 */
	public void writeBarcode(String type, String value, OutputStream os)
	{
		this.barcodeService.generateBarcode(type, value, os);
	}

	/**
	 * 生成指定类型的二维码, 并获得图片的二进制数组
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public byte[] generateBarcode(String type, String value)
	{
		return this.barcodeService.generateBarcode(type, value);
	}

	/**
	 * 输出QR二维码到OutputStream
	 * 
	 * @param text
	 * @param width
	 * @param height
	 * @param os
	 * @throws IOException
	 * @throws WriterException
	 */
	public void writeQRCode(String text, int width, int height, OutputStream os) throws IOException, WriterException
	{
		this.qrCodeService.writeQRCode(text, width, height, os);
	}
}
