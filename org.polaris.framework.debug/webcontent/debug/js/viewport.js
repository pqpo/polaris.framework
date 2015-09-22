Ext.require([ "*.*" ]);

Ext.onReady(function() {

	var gridHtml = "<iframe src='grid.html' width='100%' height='100%' frameborder='no' border='0' marginwidth='0' marginheight='0' scrolling='no' ></iframe>";

	var menuTree = new MenuTree();

	var viewport = Ext.create('Ext.container.Viewport', {
		layout : 'border',
		items : [ {
			region : 'west',
			collapsible : true,
			title : '导航',
			split : true,
			width : 200,
			items : menuTree.getTreePanel()
		}, {
			region : 'center',
			xtype : 'tabpanel',
			activeTab : 1,
			items : [ {
				title : '首页'
			}, {
				id : "tab1",
				title : "基站列表",
				html : gridHtml,
				closable : true
			} ]
		} ]
	});
});