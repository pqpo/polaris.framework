var MenuTree = function() {

	var treePanel = Ext.create('Ext.tree.Panel', {
		preventHeader : true,
		width : 200,
		height : 150,
		store : Ext.create('Ext.data.TreeStore', {
			autoLoad : true,
			proxy : {
				type : "rest",
				url : "/api/menu"
			}
		}),
		rootVisible : false
	});

	this.getTreePanel = function() {
		return treePanel;
	};

};