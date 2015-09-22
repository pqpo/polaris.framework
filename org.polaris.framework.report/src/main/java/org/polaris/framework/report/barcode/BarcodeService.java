package org.polaris.framework.report.barcode;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Service;

/**
 * 条形码,二维码工具类
 * 
 * @author wang.sheng
 * 
 */
@Service
public class BarcodeService
{
	Log log = LogFactory.getLog(getClass());

	/**
	 * 生成条形码,二维码
	 * 
	 * @param type
	 * @param value
	 * @param os
	 */
	public void generateBarcode(String type, String value, OutputStream os)
	{
		BitmapCanvasProvider canvas = null;
		try
		{
			BarcodeGenerator gen = BarcodeUtil.getInstance().createBarcodeGenerator(buildCfg(type));
			canvas = new BitmapCanvasProvider(os, "image/jpeg", 200, BufferedImage.TYPE_BYTE_BINARY, false, 0);
			gen.generateBarcode(canvas, value);
		}
		catch (Exception e)
		{
			log.error("generateBarcode failed!", e);
		}
		finally
		{
			try
			{
				canvas.finish();
			}
			catch (IOException e)
			{
				log.warn("BitmapCanvasProvider close failed!", e);
			}
		}
	}

	/**
	 * 生成条形码, 并返回图像的二进制数组
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public byte[] generateBarcode(String type, String value)
	{
		ByteArrayOutputStream baos = null;
		try
		{
			baos = new ByteArrayOutputStream();
			generateBarcode(type, value, baos);
			baos.flush();
			return baos.toByteArray();
		}
		catch (Exception e)
		{
			log.error("generateBarcode failed!", e);
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(baos);
		}
	}

	private static Configuration buildCfg(String type)
	{
		DefaultConfiguration cfg = new DefaultConfiguration("barcode");
		// 条码类型
		DefaultConfiguration child = new DefaultConfiguration(type);
		cfg.addChild(child);
		// 条形码位置
		DefaultConfiguration attr = new DefaultConfiguration("human-readable");
		DefaultConfiguration subAttr = new DefaultConfiguration("placement");
		subAttr.setValue("bottom");
		attr.addChild(subAttr);
		child.addChild(attr);
		return cfg;
	}
}