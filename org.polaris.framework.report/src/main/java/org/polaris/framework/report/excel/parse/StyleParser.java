package org.polaris.framework.report.excel.parse;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.polaris.framework.report.excel.style.Font;
import org.polaris.framework.report.excel.style.Style;
import org.polaris.framework.report.excel.style.StyleCollect;
import org.springframework.stereotype.Service;

/**
 * IStyleParse接口实现
 * 
 * @author C4ISR
 * 
 */
@Service
public class StyleParser
{
	/**
	 * 缓存
	 */
	// private HashMap<String, StyleCollect> map = new HashMap<String,
	// StyleCollect>();

	Log log = LogFactory.getLog(getClass());

	public StyleCollect parseStyleCollect(String filePath)
	{
		log.info("parse style config file: " + filePath);
		// StyleCollect sc = map.get(filePath);
		// if (sc != null)
		// {
		// return sc;
		// }
		// 开始进行解析
		StyleCollect sc = new StyleCollect();
		InputStream is = null;
		try
		{
			is = new FileInputStream(filePath);
			SAXReader sb = new SAXReader();
			Document doc = sb.read(is);
			Element root = doc.getRootElement();
			Iterator<?> it = root.elementIterator();
			while (it.hasNext())
			{
				Element element = (Element) it.next();
				String name = element.attributeValue("name");
				String bgColor = element.attributeValue("bgcolor");
				String align = element.attributeValue("align");
				String valign = element.attributeValue("valign");
				String border = element.attributeValue("border");
				String outborder = element.attributeValue("outborder");
				Style style = new Style();
				if (bgColor != null)
				{
					style.setBgColor(bgColor);
				}
				if (align != null)
				{
					style.setAlign(align);
				}
				if (valign != null)
				{
					style.setValign(valign);
				}
				if (border != null)
				{
					style.setBorder(Integer.parseInt(border));
				}
				if (outborder != null)
				{
					style.setOutborder(Integer.parseInt(outborder));
				}
				Element child = element.element("font");
				if (child != null)
				{
					style.setFont(parseFont(child));
				}
				sc.put(name, style);
			}
			// map.put(filePath, sc);
			log.info("style config file: " + filePath + " parsed successful!");
			return sc;
		}
		catch (Exception e)
		{
			log.error("parseStyleCollect failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		return null;
	}

	private Font parseFont(Element element)
	{
		Font font = new Font();
		String name = element.attributeValue("name");
		String size = element.attributeValue("size");
		String bold = element.attributeValue("bold");
		String italic = element.attributeValue("italic");
		String color = element.attributeValue("color");
		String underline = element.attributeValue("underline");
		if (name != null)
		{
			font.setFontName(name);
		}
		if (size != null)
		{
			font.setSize(Integer.parseInt(size));
		}
		if (bold != null)
		{
			font.setBold(Boolean.parseBoolean(bold));
		}
		if (italic != null)
		{
			font.setItalic(Boolean.parseBoolean(italic));
		}
		if (color != null)
		{
			font.setColor(color);
		}
		if (underline != null)
		{
			font.setUnderline(Boolean.parseBoolean(underline));
		}
		return font;
	}

}
