package org.polaris.framework.debug.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.common.rest.FormResult;
import org.polaris.framework.common.rest.PagingResult;
import org.polaris.framework.debug.service.StationService;
import org.polaris.framework.debug.vo.Station;
import org.polaris.framework.report.excel.ExcelReportService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/station")
public class StationController
{
	private static final int MAX_LENGTH = 500;

	Log log = LogFactory.getLog(getClass());

	@Resource
	private StationService stationService;
	@Resource
	private ExcelReportService excelReportService;

	@RequestMapping(method = RequestMethod.POST)
	public FormResult save(@RequestBody Station station)
	{
		FormResult formResult = new FormResult();
		try
		{
			if (StringUtils.isEmpty(station.getId()))
			{
				stationService.insert(station);
			}
			else
			{
				stationService.update(station);
			}
			formResult.setSuccess(true);
			formResult.setMessage("基站信息保存成功!");
		}
		catch (ConstraintViolationException e)
		{
			formResult.copyErrors(e);
			formResult.setMessage("基站信息校验失败!");
			formResult.setSuccess(false);
		}
		catch (Exception e)
		{
			log.error("Station save failed!", e);
			formResult.setSuccess(false);
			formResult.setMessage(e.getMessage());
		}
		return formResult;
	}

	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public void report(HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("date", new Date());
		PagingResult<Station> pagingResult = stationService.getStations(0, 1000);
		map.put("stations", pagingResult.getResults());
		try
		{
			response.setContentType("APPLICATION/OCTET-STREAM");
			String fileName = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".xls";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			excelReportService.report("report/excel/table.ftl", map, response.getOutputStream());
		}
		catch (IOException e)
		{
			log.error("report failed!", e);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public FormResult getStation(@PathVariable String id)
	{
		FormResult formResult = new FormResult();
		try
		{
			Station station = stationService.getStation(id);
			formResult.setData(station);
			formResult.setSuccess(true);
		}
		catch (Exception e)
		{
			log.error("getStation failed!", e);
			formResult.setSuccess(false);
			formResult.setMessage(e.getMessage());
		}
		return formResult;
	}

	@RequestMapping(value = "/grid", method = RequestMethod.GET)
	public PagingResult<Station> getStations(Integer start, Integer limit)
	{
		if (limit == null || limit > MAX_LENGTH)
		{
			limit = MAX_LENGTH;
		}
		if (start == null)
		{
			start = 0;
		}
		return stationService.getStations(start, limit);
	}

	@RequestMapping(value = "/grid", method = RequestMethod.DELETE)
	public FormResult deleteStations(@RequestBody Station[] stations)
	{
		FormResult formResult = new FormResult();
		try
		{
			for (Station station : stations)
			{
				stationService.delete(station.getId());
			}
			formResult.setSuccess(true);
		}
		catch (Exception e)
		{
			log.error("deleteStations failed!", e);
			formResult.setSuccess(false);
			formResult.setMessage(e.getMessage());
		}
		return formResult;
	}

	@RequestMapping(value = "/grid", method = RequestMethod.PUT)
	public FormResult updateStations(@RequestBody Station[] stations)
	{
		FormResult formResult = new FormResult();
		try
		{
			for (Station station : stations)
			{
				stationService.update(station);
			}
			formResult.setSuccess(true);
		}
		catch (Exception e)
		{
			log.error("updateStation failed!", e);
			formResult.setSuccess(false);
			formResult.setMessage(e.getMessage());
		}
		return formResult;
	}

	@RequestMapping(value = "/grid", method = RequestMethod.POST)
	public FormResult insertStations(@RequestBody Station[] stations)
	{
		FormResult formResult = new FormResult();
		try
		{
			for (Station station : stations)
			{
				stationService.insert(station);
			}
			formResult.setSuccess(true);
		}
		catch (Exception e)
		{
			log.error("updateStation failed!", e);
			formResult.setSuccess(false);
			formResult.setMessage(e.getMessage());
		}
		return formResult;
	}

}
