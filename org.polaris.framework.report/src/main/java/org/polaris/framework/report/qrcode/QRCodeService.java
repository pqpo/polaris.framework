package org.polaris.framework.report.qrcode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 生成QR二维码
 * 
 * @author wang.sheng
 * 
 */
@Service
public class QRCodeService
{
	private static final String IMAGE_FORMAT = "png";
	private static final Map<EncodeHintType, String> hintMap;

	static
	{
		hintMap = new HashMap<EncodeHintType, String>();
		hintMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
	}

	/**
	 * 生成QR二维码
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
		BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);
		QRCodeUtils.writeToStream(bitMatrix, IMAGE_FORMAT, os);
	}
}
