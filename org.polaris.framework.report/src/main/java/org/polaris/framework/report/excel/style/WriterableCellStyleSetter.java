package org.polaris.framework.report.excel.style;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

/**
 * 设置一个单元格的样式
 * 
 * @author C4ISR
 * 
 */
@Service
public class WriterableCellStyleSetter
{
	Log log = LogFactory.getLog(getClass());

	public void setStyle(WritableCell cell, Style style)
	{
		if (style == null)
		{
			style = new Style();
		}
		WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
		Font font = style.getFont();
		// 创建字体对象
		WritableFont wf = new WritableFont(WritableFont.createFont(font.getFontName()), font.getSize(), font.isBold() ? WritableFont.BOLD
				: WritableFont.NO_BOLD, font.isItalic(), font.isUnderline() ? jxl.format.UnderlineStyle.SINGLE : jxl.format.UnderlineStyle.NO_UNDERLINE,
				getColour(font.getColor()));
		try
		{
			format.setFont(wf);// 设置字体
			// 设置背景色
			format.setBackground(getColour(style.getBgColor()));
			// 设置align
			format.setAlignment(getAlignment(style.getAlign()));
			// 设置valign
			format.setVerticalAlignment(getVerticalAlignment(style.getValign()));
			// 设置线宽
			format.setBorder(jxl.format.Border.ALL, getBorderLineStyle(style.getBorder()));
			format.setWrap(true);
		}
		catch (WriteException e)
		{
			log.error("styleSetter failed!", e);
		}
		finally
		{
			cell.setCellFormat(format);
		}
	}

	public void drawOutBorder(int x, int y, int width, int height, int outborder, WritableSheet sheet, String outColor)
	{
		// log.info("开始设置表格的边框");
		try
		{
			for (int i = 0; i < width; i++)
			{
				for (int j = 0; j < height; j++)
				{
					WritableCell cell = (WritableCell) sheet.getCell(x + i, y + j);
					if (cell == null)
						continue;

					WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
					if (i == 0)
					{
						// 左侧面
						format.setBorder(jxl.format.Border.LEFT, getBorderLineStyle(outborder), getColour(outColor));
					}
					if (j == 0)
					{
						// 上侧面
						format.setBorder(jxl.format.Border.TOP, getBorderLineStyle(outborder), getColour(outColor));
					}
					if (i == width - 1)
					{
						// 右侧面
						format.setBorder(jxl.format.Border.RIGHT, getBorderLineStyle(outborder), getColour(outColor));
					}
					if (j == height - 1)
					{
						// 底面
						format.setBorder(jxl.format.Border.BOTTOM, getBorderLineStyle(outborder), getColour(outColor));
					}
					cell.setCellFormat(format);
				}
			}
		}
		catch (Exception e)
		{
			log.error("drawOutBorder failed!", e);
		}
	}

	/**
	 * 获取线宽
	 * 
	 * @param border
	 * @return
	 */
	private BorderLineStyle getBorderLineStyle(int border)
	{
		if (border == 0)
		{
			return BorderLineStyle.NONE;
		}
		else if (border == 1)
		{
			return BorderLineStyle.THIN;
		}
		else
		{
			return BorderLineStyle.THICK;
		}
	}

	/**
	 * 获取valign
	 * 
	 * @param valign
	 * @return
	 */
	private VerticalAlignment getVerticalAlignment(String valign)
	{
		if (valign == null || valign.equals(""))
		{
			return VerticalAlignment.TOP;
		}
		else if (valign.equalsIgnoreCase("top"))
		{
			return VerticalAlignment.TOP;
		}
		else if (valign.equalsIgnoreCase("bottom"))
		{
			return VerticalAlignment.BOTTOM;
		}
		else if (valign.equalsIgnoreCase("center"))
		{
			return VerticalAlignment.CENTRE;
		}
		else
		{
			return VerticalAlignment.TOP;
		}
	}

	/**
	 * 获取align
	 * 
	 * @param align
	 * @return
	 */
	private Alignment getAlignment(String align)
	{
		if (align == null || align.equals(""))
		{
			return Alignment.LEFT;
		}
		else if (align.equalsIgnoreCase("left"))
		{
			return Alignment.LEFT;
		}
		else if (align.equalsIgnoreCase("center"))
		{
			return Alignment.CENTRE;
		}
		else if (align.equalsIgnoreCase("right"))
		{
			return Alignment.RIGHT;
		}
		else
		{
			return Alignment.LEFT;
		}
	}

	/**
	 * 获取指定的颜色
	 * 
	 * @param colour
	 * @return
	 */
	private Colour getColour(String colour)
	{
		if (colour == null || colour.equals(""))
		{
			return Colour.BLACK;
		}
		else if (colour.equalsIgnoreCase("white"))
		{
			return Colour.WHITE;
		}
		else if (colour.equalsIgnoreCase("red"))
		{
			return Colour.RED;
		}
		else if (colour.equalsIgnoreCase("green"))
		{
			return Colour.GREEN;
		}
		else if (colour.equalsIgnoreCase("blue"))
		{
			return Colour.BLUE;
		}
		else if (colour.equalsIgnoreCase("yellow"))
		{
			return Colour.YELLOW;
		}
		else if (colour.equalsIgnoreCase("blue grey"))
		{
			return Colour.BLUE_GREY;
		}
		else if (colour.equalsIgnoreCase("grey 25%"))
		{
			return Colour.GREY_25_PERCENT;
		}
		else if (colour.equalsIgnoreCase("grey 40%"))
		{
			return Colour.GREY_40_PERCENT;
		}
		else if (colour.equalsIgnoreCase("grey 50%"))
		{
			return Colour.GREY_50_PERCENT;
		}
		else if (colour.equalsIgnoreCase("grey 80%"))
		{
			return Colour.GREY_80_PERCENT;
		}
		else if (colour.equalsIgnoreCase("pink"))
		{
			return Colour.PINK;
		}
		else if (colour.equalsIgnoreCase("light orange"))
		{
			return Colour.LIGHT_ORANGE;
		}
		else if (colour.equalsIgnoreCase("ice blue"))
		{
			return Colour.ICE_BLUE;
		}
		else
		{
			return Colour.GRAY_80;
		}
	}

}
