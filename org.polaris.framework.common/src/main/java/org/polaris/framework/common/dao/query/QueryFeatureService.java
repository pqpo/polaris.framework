package org.polaris.framework.common.dao.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.common.utils.ReflectUtils;
import org.springframework.stereotype.Service;

/**
 * 查询转换服务
 * 
 * @author wang.sheng
 * 
 */
@Service
public class QueryFeatureService
{
	Log log = LogFactory.getLog(getClass());

	/**
	 * 将查询对象中按照Annotation的规则生成QL语句和参数集合
	 * 
	 * @param object
	 * @param qlPrefix
	 * @return
	 */
	public QueryFeature build(Object object, String qlPrefix)
	{
		if (object == null)
		{
			throw new IllegalArgumentException("input param cannot null!");
		}
		QueryFeature queryFeature = new QueryFeature();
		Field[] fields = ReflectUtils.getAllFields(object.getClass());
		List<String> qlList = new ArrayList<String>();
		List<Object> paramList = new ArrayList<Object>();
		for (Field field : fields)
		{
			Query query = field.getAnnotation(Query.class);
			if (query == null)
			{
				// 未设置注解
				continue;
			}
			Object value = ReflectUtils.getFieldValue(object, field);
			if (value == null)
			{
				// 取值为空
				continue;
			}
			if (value instanceof String)
			{
				String str = (String) value;
				if (StringUtils.isBlank(str))
				{
					// 空字符串也被认为取值为空
					continue;
				}
			}
			String fieldName = query.value();
			if (StringUtils.isEmpty(fieldName))
			{
				fieldName = field.getName();
			}
			if (!StringUtils.isEmpty(qlPrefix))
			{
				// ql前缀不为空
				fieldName = qlPrefix + fieldName;
			}
			Compare compare = query.compare();
			if (compare == Compare.Eq)
			{
				// 等于
				qlList.add(fieldName + "=?");
				paramList.add(value);
			}
			else if (compare == Compare.Gt)
			{
				// 大于
				qlList.add(fieldName + ">?");
				paramList.add(value);
			}
			else if (compare == Compare.GtEq)
			{
				// 大于等于
				qlList.add(fieldName + ">=?");
				paramList.add(value);
			}
			else if (compare == Compare.Like)
			{
				// 相似
				qlList.add(fieldName + " like ?");
				paramList.add("%" + value + "%");
			}
			else if (compare == Compare.Lt)
			{
				// 小于
				qlList.add(fieldName + "<?");
				paramList.add(value);
			}
			else if (compare == Compare.LtEq)
			{
				// 小于等于
				qlList.add(fieldName + "<=?");
				paramList.add(value);
			}
			else if (compare == Compare.NoEq)
			{
				// 不等于
				qlList.add(fieldName + "<>?");
				paramList.add(value);
			}
			else if (compare == Compare.NoLike)
			{
				// 不相似
				qlList.add(fieldName + " not like ?");
				paramList.add("%" + value + "%");
			}
		}
		queryFeature.setQls(qlList.toArray(new String[0]));
		queryFeature.setParams(paramList);
		return queryFeature;
	}
}
