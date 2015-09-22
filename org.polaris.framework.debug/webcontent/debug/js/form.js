var StationController = function() {

	var actionListener = null;

	var stationWindow = Ext.create("Ext.window.Window", {
		constrain : true,
		modal : true,
		title : "基站位置",
		frame : true,
		layout : "fit",
		items : [ Ext.create("Ext.form.Panel", {
			id : "stationForm",
			jsonSubmit : true,
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 80,
				anchor : '100%',
				margin : "8"
			},
			preventHeader : true,
			items : [ {
				xtype : "hidden",
				name : "id",
				value : ""
			}, {
				xtype : "numberfield",
				allowBlank : false,
				blankText : "国家号不可以为空!",
				name : "mcc",
				fieldLabel : "国家号"
			}, {
				xtype : "numberfield",
				allowBlank : false,
				blankText : "运营商号不可以为空!",
				name : "mnc",
				fieldLabel : "运营商号"
			}, {
				xtype : "numberfield",
				allowBlank : false,
				blankText : "基站号不可以为空!",
				name : "lac",
				fieldLabel : "基站号"
			}, {
				xtype : "numberfield",
				allowBlank : false,
				blankText : "小区号不可以为空!",
				name : "ci",
				fieldLabel : "小区号"
			}, {
				xtype : "numberfield",
				allowBlank : false,
				blankText : "经度不可以为空!",
				decimalPrecision : 6,
				name : "lat",
				fieldLabel : "经度"
			}, {
				xtype : "numberfield",
				allowBlank : false,
				blankText : "纬度不可以为空!",
				decimalPrecision : 6,
				name : "lng",
				fieldLabel : "纬度"
			}, {
				xtype : "textareafield",
				name : "info",
				fieldLabel : "信息"
			} ],
			autoScroll : true,
			frame : true
		}) ],
		width : 500,
		height : 350,
		closeAction : "hide",
		buttonAlign : "center",
		buttons : [ {
			xtype : "button",
			text : "保存",
			handler : function() {
				var form = Ext.getCmp("stationForm").getForm();
				if (form.isValid()) {
					form.submit({
						url : "/api/station",
						method : "POST",
						success : function(form, action) {
							Ext.MessageBox.show({
								title : '信息',
								msg : action.result.message,
								buttons : Ext.Msg.OK,
								icon : Ext.Msg.INFO,
								fn : function(btn) {
									if (actionListener != null) {
										// 保存成功后的回调方法
										actionListener.onSaved();
									}
								}
							});
						},
						failure : function(form, action) {
							Ext.MessageBox.show({
								title : '错误',
								msg : action.result.message,
								buttons : Ext.Msg.OK,
								icon : Ext.Msg.ERROR
							});
						}
					});
				}
			}
		}, {
			xtype : "button",
			text : "关闭",
			handler : function() {
				stationWindow.setVisible(false);
			}
		} ]
	});

	/**
	 * 打开对话框
	 */
	this.open = function(stationId) {
		var form = Ext.getCmp("stationForm");
		if (stationId == null) {
			// 进入添加页面
			stationWindow.setTitle("添加基站");
			form.getForm().reset();
		} else {
			// 进入修改页面
			stationWindow.setTitle("修改基站");
			form.getForm().load({
				url : "/api/station/" + stationId,
				method : "GET",
				failure : function(form, action) {
					Ext.Msg.show({
						title : "加载失败",
						msg : action.result.message,
						buttons : Ext.MessageBox.OK,
						icon : Ext.MessageBox.ERROR
					});
				}
			});
		}
		stationWindow.setVisible(true);
	};

	this.setActionListener = function(listener) {
		actionListener = listener;
	};

	/**
	 * 关闭对话框
	 */
	this.close = function() {
		stationWindow.setVisible(false);
	};

};
