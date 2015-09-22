package org.polaris.framework.report.barcode;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/barcode")
public class BarcodeController
{
	@Resource
	private BarcodeService barcodeService;

	Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/{type}/{value}", method = RequestMethod.GET)
	public void generateBarcode(@PathVariable String type, @PathVariable String value, HttpServletResponse response)
	{
		response.setContentType("image/jpeg");
		response.setDateHeader("expries", -1);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		try
		{
			barcodeService.generateBarcode(type, value, response.getOutputStream());
		}
		catch (IOException e)
		{
			log.warn("generateBarcode failed!", e);
		}
	}
}
