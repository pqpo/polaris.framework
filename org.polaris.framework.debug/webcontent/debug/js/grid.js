Ext.require([ "*.*" ]);

Ext.onReady(function() {

	var stationController = new StationController();

	Ext.define("Station", {
		extend : "Ext.data.Model",
		fields : [ "id", "mcc", "mnc", "lac", "ci", "lng", "lat", "info" ]
	});
	var store = Ext.create("Ext.data.Store", {
		model : "Station",
		pageSize : 30,
		proxy : {
			type : "rest",
			url : "/api/station/grid",
			appendId : false,
			batchActions : true,
			reader : {
				type : "json",
				root : "results",
				totalProperty : "total"
			},
			writer : {
				type : "json",
				writeAllFields : true,
				allowSingle : false
			}
		},
		listeners : {
			write : function(store, operation, eOpts) {
				store.load();
			}
		}
	});

	var grid = Ext.create("Ext.grid.Panel", {
		region : 'center',
		store : store,
		plugins : {
			ptype : "cellediting",
			clicksToEdit : 2
		},
		selModel : "checkboxmodel",
		// selModel: "rowmodel",
		loadMask : true,
		columns : [ {
			xtype : "rownumberer",
			width : 40
		}, {
			text : "国家号",
			width : 80,
			dataIndex : "mcc",
			align : "right",
			editor : {
				xtype : 'numberfield',
				allowBlank : false
			},
			sortable : false
		}, {
			text : "运营商号",
			width : 80,
			dataIndex : "mnc",
			align : "right",
			editor : {
				xtype : 'numberfield',
				allowBlank : false
			},
			sortable : false
		}, {
			text : "位置号",
			width : 80,
			dataIndex : "lac",
			align : "right",
			editor : {
				xtype : 'numberfield',
				allowBlank : false
			},
			sortable : true
		}, {
			text : "小区号",
			width : 80,
			dataIndex : "ci",
			align : "right",
			editor : {
				xtype : 'numberfield',
				allowBlank : false
			},
			sortable : true
		}, {
			text : "经度",
			width : 120,
			dataIndex : "lng",
			sortable : true,
			align : "right",
			editor : {
				xtype : 'numberfield',
				decimalPrecision : 6,
				allowBlank : false
			},
			renderer : Ext.util.Format.numberRenderer("0.000000")
		}, {
			text : "纬度",
			width : 120,
			dataIndex : "lat",
			sortable : true,
			editor : {
				xtype : 'numberfield',
				decimalPrecision : 6,
				allowBlank : false
			},
			renderer : Ext.util.Format.numberRenderer("0.000000"),
			align : "right"
		}, {
			text : "信息",
			flex : 1,
			dataIndex : "info",
			editor : {
				xtype : 'textfield',
				allowBlank : false
			},
			sortable : true
		} ],
		autoScroll : true,
		region : "center",
		dockedItems : [ {
			dock : "top",
			xtype : "toolbar",
			items : [ {
				text : "刷新",
				handler : function() {
					store.load();
				}
			}, "-", {
				text : "添加",
				handler : function() {
					var record = new Station();
					store.insert(0, record);
				}
			}, {
				text : "添加2",
				handler : function() {
					stationController.open(null);
				}
			}, {
				text : "修改",
				handler : function() {
					var selection = grid.getView().getSelectionModel().getSelection();
					if (selection.length < 1) {
						Ext.Msg.show({
							title : "注意",
							msg : "请选择需要修改的基站!",
							buttons : Ext.MessageBox.OK,
							icon : Ext.MessageBox.WARNING
						});
						return;
					}
					stationController.open(selection[0].getId());
				}
			}, {
				text : "保存",
				handler : function() {
					store.sync();
				}
			}, "-", {
				text : "删除",
				handler : function() {
					var selection = grid.getView().getSelectionModel().getSelection();
					if (selection.length < 1) {
						Ext.Msg.show({
							title : "注意",
							msg : "请选择需要删除的记录!",
							buttons : Ext.MessageBox.OK,
							icon : Ext.MessageBox.WARNING
						});
						return;
					}
					Ext.Msg.confirm("确认", "确定删除选中的记录?", function(btn) {
						if (btn == "yes") {
							store.remove(selection);
							store.sync();
						}
					});
				}
			} ]
		}, {
			dock : "bottom",
			xtype : "pagingtoolbar",
			store : store,
			displayInfo : true,
			items : [ "-", {
				xtype : "combobox",
				store : Ext.create("Ext.data.ArrayStore", {
					data : [ [ 10 ], [ 20 ], [ 30 ], [ 40 ], [ 50 ] ],
					fields : [ {
						name : "value",
						type : "int"
					} ]
				}),
				displayField : "value",
				valueField : "value",
				labelWidth : 65,
				fieldLabel : "每页显示",
				width : 125,
				value : "30",
				listeners : {
					select : function(combo, records, eOpts) {
						var number = combo.getValue() * 1.0;
						if (number > 0) {
							store.pageSize = number;
							store.loadPage(1);
						}
					}
				}
			} ],
			dock : "bottom"
		} ],
	});

	store.load();

	Ext.create('Ext.container.Viewport', {
		layout : 'border',
		items : [ grid ]
	});

	// 当基站控制器保存成功时刷新列表
	stationController.setActionListener({
		onSaved : function() {
			stationController.close();
			store.load();
		}
	});
});