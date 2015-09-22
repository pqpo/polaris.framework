package org.polaris.framework.debug.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.webapp.extjs.Node;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单控制器
 * 
 * @author wang.sheng
 * 
 */
@RestController
@RequestMapping("/menu")
public class MenuController
{
	Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Node[] load(@PathVariable String id)
	{
		log.info("load node: " + id);
		Node node1 = new Node();
		node1.setId("node1");
		node1.setText("测试页面");
		node1.setParentId(id);
		node1.setLeaf(true);
		Node node2 = new Node();
		node2.setId("node2");
		node2.setText("百度");
		node2.setHref("http://www.baidu.com");
		node2.setHrefTarget("_blank");
		node2.setLeaf(true);
		node2.setParentId(id);
		return new Node[] { node1, node2 };
	}
}
