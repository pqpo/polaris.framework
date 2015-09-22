package org.polaris.framework.webapp.extjs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * TreeStore中的模型对象
 * 
 * @author wang.sheng
 */
@JsonInclude(value = Include.NON_NULL)
public class Node
{
	private Boolean checked;
	private List<Node> children;
	private Boolean expandable;
	private Boolean expanded;
	private String href;
	private String hrefTarget;
	private String icon;
	private Integer index;
	private Boolean leaf;
	private String parentId;
	private Boolean root;
	private String text;
	private String id;

	public Boolean getChecked()
	{
		return checked;
	}

	public List<Node> getChildren()
	{
		return children;
	}

	public Boolean getExpandable()
	{
		return expandable;
	}

	public Boolean getExpanded()
	{
		return expanded;
	}

	public String getHref()
	{
		return href;
	}

	public String getHrefTarget()
	{
		return hrefTarget;
	}

	public String getIcon()
	{
		return icon;
	}

	public Integer getIndex()
	{
		return index;
	}

	public Boolean getLeaf()
	{
		return leaf;
	}

	public String getParentId()
	{
		return parentId;
	}

	public Boolean getRoot()
	{
		return root;
	}

	public String getText()
	{
		return text;
	}

	public String getId()
	{
		return id;
	}

	public void addChild(Node child)
	{
		if (this.children == null)
		{
			this.children = new ArrayList<Node>();
		}
		this.children.add(child);
	}

	public void setChecked(Boolean checked)
	{
		this.checked = checked;
	}

	public void setExpandable(Boolean expandable)
	{
		this.expandable = expandable;
	}

	public void setExpanded(Boolean expanded)
	{
		this.expanded = expanded;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public void setHrefTarget(String hrefTarget)
	{
		this.hrefTarget = hrefTarget;
	}

	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}

	public void setLeaf(Boolean leaf)
	{
		this.leaf = leaf;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public void setRoot(Boolean root)
	{
		this.root = root;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setId(String id)
	{
		this.id = id;
	}

}
