package org.polaris.framework.report.excel.parse;

import java.io.Reader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.polaris.framework.report.excel.items.TagBarCode;
import org.polaris.framework.report.excel.items.TagBr;
import org.polaris.framework.report.excel.items.TagExcel;
import org.polaris.framework.report.excel.items.TagImage;
import org.polaris.framework.report.excel.items.TagSheet;
import org.polaris.framework.report.excel.items.TagTable;
import org.polaris.framework.report.excel.items.TagTd;
import org.polaris.framework.report.excel.items.TagTr;
import org.polaris.framework.report.excel.style.Style;
import org.polaris.framework.report.excel.style.StyleCollect;
import org.springframework.stereotype.Service;

import java.util.*;

import javax.annotation.Resource;

/**
 * IExcelConfigParse接口实现
 * 
 * @author Administrator
 * 
 */
@Service
public class ExcelConfigParser
{
	@Resource
	private StyleParser styleParser;

	Log log = LogFactory.getLog(getClass());

	public TagExcel parse(Reader reader) throws Exception
	{
		SAXReader sb = new SAXReader();
		Document doc = sb.read(reader);
		Element root = doc.getRootElement();
		TagExcel excel = new TagExcel();
		String style = root.attributeValue("style");
		StyleCollect styleCollect = styleParser.parseStyleCollect(style);
		excel.setStyle(style);
		Iterator<?> it = root.elementIterator();
		while (it.hasNext())
		{
			Element element = (Element) it.next();
			TagSheet sheet = this.parseSheet(element, styleCollect);
			excel.addSheet(sheet);
		}
		return excel;
	}

	/**
	 * 解析sheet
	 * 
	 * @param sheetElement
	 * @return
	 */
	private TagSheet parseSheet(Element sheetElement, final StyleCollect styleCollect)
	{
		TagSheet sheet = new TagSheet();
		String text = sheetElement.attributeValue("text");
		if (text == null)
		{
			text = "Unknown Title";
		}
		sheet.setText(text);
		Iterator<?> it = sheetElement.elementIterator();
		while (it.hasNext())
		{
			Element element = (Element) it.next();
			String name = element.getName();
			if (name.equalsIgnoreCase("br"))
			{
				// 解析<br/>
				TagBr br = new TagBr();
				sheet.addBr(br);
			}
			else if (name.equalsIgnoreCase("table"))
			{
				TagTable table = parseTable(element, styleCollect);
				sheet.addTable(table);
			}
			else
			{
				log.warn("Unknown Tag: " + name);
			}
		}
		return sheet;
	}

	/**
	 * 解析表格部分
	 * 
	 * @param tableElement
	 * @return
	 */
	private TagTable parseTable(Element tableElement, final StyleCollect styleCollect)
	{
		TagTable table = new TagTable();
		String leftmargin = tableElement.attributeValue("leftmargin");
		String styleName = tableElement.attributeValue("class");
		String border = tableElement.attributeValue("border");
		String outborder = tableElement.attributeValue("outborder");

		if (leftmargin != null)
		{
			table.setLeftmargin(Integer.parseInt(leftmargin));
		}
		if (border != null)
		{
			table.setBorder(Integer.parseInt(border));
		}
		if (outborder != null)
		{
			table.setOutborder(Integer.parseInt(outborder));
		}
		table.setStyleName(styleName);
		Iterator<?> it = tableElement.elementIterator();
		while (it.hasNext())
		{
			Element element = (Element) it.next();
			String name = element.getName();
			if (name.equalsIgnoreCase("tr"))
			{
				TagTr tr = this.parseTr(element, styleCollect, styleName);
				table.addTr(tr);
			}
			else
			{
				log.warn("Unknown Tag: " + name);
			}
		}
		return table;
	}

	/**
	 * 解析表格行
	 * 
	 * @param trElement
	 * @return
	 */
	private TagTr parseTr(Element trElement, final StyleCollect styleCollect, String parentStyle)
	{
		TagTr tr = new TagTr();
		String styleName = trElement.attributeValue("class");
		if (styleName == null)
		{
			// 如果为空,则设置为TABLE的样式
			styleName = parentStyle;
		}
		tr.setStyleName(styleName);
		Iterator<?> it = trElement.elementIterator();
		while (it.hasNext())
		{
			Element element = (Element) it.next();
			TagTd td = this.parseTd(element, styleCollect, styleName);
			tr.addTd(td);
		}
		return tr;
	}

	/**
	 * 解析表格列
	 * 
	 * @param element
	 * @return
	 */
	private TagTd parseTd(Element tdElement, final StyleCollect styleCollect, String parentStyle)
	{
		TagTd td = new TagTd();
		String styleName = tdElement.attributeValue("class");
		String rowspan = tdElement.attributeValue("rowspan");
		String colspan = tdElement.attributeValue("colspan");
		String align = tdElement.attributeValue("align");
		String valign = tdElement.attributeValue("valign");
		String width = tdElement.attributeValue("width");
		String height = tdElement.attributeValue("height");
		if (styleName == null)
		{
			styleName = parentStyle;
		}
		td.setStyleName(styleName);
		Style style = null;
		if (styleName != null)
		{
			style = styleCollect.getStyle(styleName);
		}
		else
		{
			style = new Style();
		}
		if (rowspan != null)
		{
			td.setRowspan(Integer.parseInt(rowspan));
		}
		if (colspan != null)
		{
			td.setColspan(Integer.parseInt(colspan));
		}
		if (align != null)
		{
			td.setAlign(align);
			style.setAlign(align);
		}
		if (valign != null)
		{
			td.setValign(valign);
			style.setValign(valign);
		}
		if (width != null)
		{
			td.setWidth(Integer.parseInt(width));
		}
		if (height != null)
		{
			td.setHeight(Integer.parseInt(height));
		}
		td.setStyle(style);// 设置单元格的样式
		Iterator<?> it = tdElement.elementIterator();
		if (it.hasNext())
		{
			Element element = (Element) it.next();
			String tagName = element.getName();
			if (tagName.equalsIgnoreCase("image"))
			{
				// 输出图片
				String imageWidth = element.attributeValue("width");
				String imageHeight = element.attributeValue("height");
				TagImage image = new TagImage();
				String content = StringUtils.trim(element.getText());
				byte[] bytes = Base64.decodeBase64(content);
				image.setValue(bytes);
				if (imageWidth != null)
				{
					image.setWidth(Integer.parseInt(imageWidth));
				}
				if (imageHeight != null)
				{
					image.setHeight(Integer.parseInt(imageHeight));
				}
				td.setContent(image);
			}
			else if (tagName.equalsIgnoreCase("barcode"))
			{
				// 输出条形码
				String value = element.attributeValue("value");
				String type = element.attributeValue("type");
				TagBarCode bar = new TagBarCode();
				bar.setType(type);
				bar.setValue(value);
				td.setContent(bar);
			}
			else
			{
				log.warn("UnKnown Tag: " + tagName);
			}
		}
		else
		{
			// 是静态字符串
			String value = tdElement.getText();
			td.setContent(value);
		}
		return td;
	}

}
