<?xml version="1.0" encoding="UTF-8"?>
<excel style="report/excel/tablestyle.xml">
	<sheet text="第一页">
		<table leftmargin="1">
			<tr class="title">
				<td colspan="6" rowspan="4">${date?string("yyyy年MM月dd日 HH时mm分ss秒")}基站信息</td>
			</tr>
			<tr class="title2">
				<td colspan="2" height="21">日期:${date?date}</td>
				<td colspan="1"></td>
				<td colspan="3">单位：平方米</td>
			</tr>
			<tr class="head">
				<td>序号</td>
				<td>LAC</td>
				<td>CI</td>
				<td width="20">经度</td>
				<td width="20">维度</td>
				<td width="60">信息</td>
			</tr>
			<#list stations as vo>
				<tr class="content">
					<td align="left">${vo_index+1}</td>
					<td align="right">${vo.lac?string("0")}</td>
					<td align="right">${vo.ci?string("0")}</td>
					<td align="right" width="20">${vo.lng?string("0.000000")}</td>
					<td align="right" width="20">${vo.lat?string("0.000000")}</td>
					<td width="60">${vo.info}</td>
				</tr>
			</#list>
		</table>
	</sheet>
</excel>
