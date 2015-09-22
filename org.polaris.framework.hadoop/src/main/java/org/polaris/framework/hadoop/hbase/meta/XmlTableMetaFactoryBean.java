package org.polaris.framework.hadoop.hbase.meta;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.common.xml.XmlParseService;
import org.polaris.framework.hadoop.hbase.FamilyMeta;
import org.polaris.framework.hadoop.hbase.TableMeta;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;


/**
 * 利用XML进行配置的TableMeta的工厂Bean
 * 
 * @author wang.sheng
 * 
 */
public class XmlTableMetaFactoryBean implements FactoryBean<TableMeta>
{
	Log log = LogFactory.getLog(getClass());

	private Resource resource;
	@javax.annotation.Resource
	private XmlParseService xmlParseService;

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param resource
	 */
	public void setResource(Resource resource)
	{
		this.resource = resource;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param xmlParseService
	 */
	public void setXmlParseService(XmlParseService xmlParseService)
	{
		this.xmlParseService = xmlParseService;
	}

	@Override
	public TableMeta getObject() throws Exception
	{
		InputStream is = null;
		try
		{
			is = resource.getInputStream();
			Table table = xmlParseService.parseObject(Table.class, is);
			List<FamilyMeta> familyMetaList = new ArrayList<FamilyMeta>();
			for (Family family : table.getFamilyList())
			{
				List<String> columnList = new ArrayList<String>();
				for (Column column : family.getColumnList())
				{
					columnList.add(column.getName());
				}
				FamilyMeta familyMeta = new FamilyMeta(family.getName(), columnList.toArray(new String[0]), family.getCompressionType(), family.getTimeToLive());
				familyMetaList.add(familyMeta);
			}
			return new TableMeta(table.getName(), table.getSplitclass(), familyMetaList.toArray(new FamilyMeta[0]));
		}
		catch (Exception e)
		{
			log.error("TableMeta getObject failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		return null;
	}

	@Override
	public Class<?> getObjectType()
	{
		return TableMeta.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

}
