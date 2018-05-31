var Core = (function(){
	var Sys = {};
	var ua = navigator.userAgent.toLowerCase();
	var s;
	(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
	(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
	(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
	(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
	(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
	if(Sys.ie) document.documentElement.addBehavior("#default#userdata");
	try{$.fn.combobox.defaults.editable = false;$.fn.datebox.defaults.editable = false;}catch(e){}

	var Util = {
		Sys : {},
		submit : function(o){if(typeof(o)!='object') o = $(":submit");o.attr('disabled','disabled');return true;},
		rule : function(method,a){
			switch(method){
				case '*':var r = /[\w\W]+/;break;
				case 'key':var r = /^([a-zA-Z0-9]|[._-]){2,32}$/;break;
				case 'mobile':var r = /^1[3-9]{1}[\d]{9}$/;break;
				case 'phone':return (/^1[3-9]{1}[\d]{9}$/.test(a) || /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(a) || /^([0-9]{3,4})?[0-9]{7,8}$/.test(a));break;
				case 'datetime':
					var d = new Date(Date.parse(a.toString().replace(/-/g, "/")));//"2005-12-15 09:41:30"
					return /^[0-9]*[1-9][0-9]*$/.test(d.getFullYear());		
				break;case 'int':var r = /^-?\d+$/;break;//正-负整数+0
				case 'intp':var r = /^[0-9]*[1-9][0-9]*$/;break;//正整数
				case 'intps':var r = /^\d+$/;break;//正整数+0
				case 'intn':var r = /^-[0-9]*[1-9][0-9]*$/;break;//负整数
				case 'intns':var r = /^((-\d+)|(0+))$/;break;//负整数+0
				case 'dbl':case 'float':var r = /^(-?\d+)(\.\d+)?$/;break;//浮点数
				case 'dblp':var r = /^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;break;//正浮点数
				case 'dblps':var r = /^\d+(\.\d+)?$/;break;//正浮点数 + 0
				case 'dbln':var r = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/;break;//负浮点数
				case 'dbls':var r = /^((-\d+(\.\d+)?)|(0+(\.0+)?))$/;break;//负浮点数 + 0
				case 'boolean':var r = /^[0-1]{1}$/;break;//0|1 布尔值
				case 'booleans':var r = /^[1-2]{1}$/;break;//1|2 布尔值
				case 'numeric':var r = /^[0-9]*$/;break;
				case 'money':var r = /^([1-9][\d]{0,9}|0)(\.[\d]{1,2})?$/;break;
				default:var r = /^[+\-]?\d+(\.\d+)?$/;break;
			}
			return r.test(a);
		},
		dformat : function(d,format){//时间戳转date
			if (!Util.rule('intp',d)) return false;
			var d = new Date(parseInt(d)*1000);
			var date = {"m+": d.getMonth() + 1,"d+": d.getDate(),"h+": d.getHours(),"H+": d.getHours(),"i+": d.getMinutes(),"s+": d.getSeconds(),"q+": Math.floor((d.getMonth() + 3) / 3),"S+": d.getMilliseconds()};
			if (/(y+)/i.test(format)) {
				format = format.replace(RegExp.$1, (d.getFullYear() + '').substr(4 - RegExp.$1.length));
			}
			for (var k in date) {
				if (new RegExp("(" + k + ")").test(format)) {
					format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
				}
			}
			return format;
		},
		isDefined: function(str){
			return ((typeof str!='undefined')||$.trim(str)!='')?str:'';
		},
		saveUserdata : function(name,data){
			/**
			 * set本地存储的变量
			 */
			if(Sys.ie){
			  if(data.length < 54889) {
				  with(document.documentElement){setAttribute("value",data);save('Dv__'+name);}
			  }
			} else if(window.localStorage){
			  localStorage.setItem('Dv__'+name, data);
			} else if(window.sessionStorage){
			  sessionStorage.setItem('Dv__'+name, data);
			}
		},
		loadUserdata : function(name){
			/**
			 * get本地存储的变量
			 */
			if(Sys.ie){
				with(document.documentElement){load('Dv__' + name);return getAttribute("value");}
			} else if(window.localStorage){
				return localStorage.getItem('Dv__'+name);
			} else if(window.sessionStorage){
				return sessionStorage.getItem('Dv__'+name);
			}
		},
		delUserdata : function(name){
			/**
			 * 删除本地存储的变量
			 */
			if(Sys.ie){
				with(document.documentElement){removeAttribute('value');save('Dv__'+name);}
			} else if(window.localStorage){
				localStorage.removeItem('Dv__'+name);
			} else if(window.sessionStorage){
				sessionStorage.removeItem('Dv__'+name);
			}
			return true;
		},
		Easyui:{
			'W':0,'W':0,
			default: {
				'table':null,
				'config':{},
				'columns':[],
				'hiddenColumn':[],
				'maxsize': true,
				'state':false
			},
			init: function(obj,colum,conf){
				/**
				 * [obj datagrid表格]
				 * [colum datagrid数据列]
				 * [conf datagrid配置]
				 * @type {[type]}
				 */
				Util.Easyui.dom = $(obj.grid) || $('#datagrid');
				Util.Easyui.searchbar = $(obj.sbar) || $('.search_form');

				if(Util.Easyui.W === 0 || Util.Easyui.H === 0){
					if(Util.Easyui.dom.parent('.table_box').length>0){
						Util.Easyui.default['table'] = Util.Easyui.dom.parent('.table_box').length>0? Util.Easyui.dom.parent('.table_box'):Util.Easyui.dom;
						Util.Easyui.W = Util.Easyui.default['table'].width();
						Util.Easyui.H = $(window).height() - Util.Easyui.default['table'].offset().top - 2;
					}
				}
				
				var columnsKey = Util.Easyui.dom.attr('data-columnskey');
				if(Util.isDefined(columnsKey)!='' && Util.loadUserdata(columnsKey+':hidden')){
					Util.Easyui.default['hiddenColumn'] = Util.loadUserdata(columnsKey+':hidden').split(',');
				}
				// Util.Easyui.default['hiddenColumn'] = ['cz','afid','steptime'];
				// console.log(Util.Easyui.default['hiddenColumn']);
				Util.Easyui.setColumns(colum); //所有列
				try{
					Util.Easyui.setColumns(conf['frozenColumns'][0],'frozen');
				}catch(e){

				}
				// console.log(Util.Easyui.default['columns']);
				// console.log(Util.Easyui.default['frozenColumns']);
				Util.Easyui.config(conf);

				if(!Util.Easyui.searchbar || Util.isDefined(Util.Easyui.searchbar) == '') { return;}
				Util.Easyui.searchbar.find('.search_btn').off('click').on('click',function(){
					Util.Easyui.search();
				})
				Util.filterTool(Util.Easyui.searchbar);
			},
			config: function(option){
				if(Util.isDefined(option)!==''){
					var opt = option;
					if((opt.width && Util.isDefined(opt.width)!='') || (opt.height && Util.isDefined(opt.height)!='')){
						Util.Easyui.default['maxsize'] = false;
					}
				}

				Util.Easyui.default['config'] = {
					method       : 'get', //数据方式
					width        : Util.Easyui.W, //宽度
					height       : Util.Easyui.H, //高度
					striped      : true,//条纹行
					fitColumns   : true, //宽度自适横向滚动条
					singleSelect : false, //选中单行
					rownumbers   : true,
					collapsible  : true,
					autoRowHeight: true, //自动行高
					pagination   : true, //是否分页
					remoteSort   : true, //是否允许排序等待,false本页排序,true后台排序
					multiSort    : false, //多个排序
					//toolbar      :'#Toolb', //出现工具栏
					pageSize     : 100, //初始化页内行数
					pageList     : [50,100,200],
					loadMsg      : '数据加载中，请稍候...',
					checkOnSelect: true,
					columns      : [Util.Easyui.default['columns']],
					onBeforeLoad: function() {
	                    // $(this).datagrid('clearChecked');
	                    // $(this).datagrid('clearSelections');
	                },
	                onClickRow: function(index, row) {
	                    // var _table = $(this);
	                    // _table.datagrid('clearChecked');
	                    // _table.datagrid('clearSelections');
	                    // _table.datagrid('checkRow', index);
	                }
				};
	            Util.Easyui.default['config'] = Util.isDefined(opt)!==''? Util.Easyui.params(opt,Util.Easyui.default['config']): Util.Easyui.default['config'];
				// console.log(Util.Easyui.default['config']);
			},
			params: function(option,params){
				for(var key in params){
					option[key] = Util.isDefined(option[key])!==''? option[key]:params[key];
				}
				// console.log(option);
				return option;
			},
			setColumns: function(column,type){
				var _colm = 'columns';
				if(type === 'frozen'){
					_colm = 'frozenColumns';
				}
				Util.Easyui.default[_colm] = (Util.isDefined(column)!=='' && column.length>0)?column:[];
				if(!Util.Easyui.default[_colm].length){ return;}
				var w = Util.Easyui.W - 45, box = [];
				for(var i=0;i<Util.Easyui.default[_colm].length;i++){
					if(Util.rule('intp', Util.Easyui.default[_colm][i]['width'])){
						w -= Util.Easyui.default[_colm][i]['width'];
					}else{
						box.push(i);
					}
					if($.isArray(Util.Easyui.default['hiddenColumn']) && $.inArray(Util.Easyui.default[_colm][i]['field'],Util.Easyui.default['hiddenColumn'])>-1){
						Util.Easyui.default[_colm][i]['hidden'] = true;
					}
				}
				if(!box.length){ return;}
				for(var j=0;j<box.length;j++){
					Util.Easyui.default[_colm][box[j]]['width'] = w/box.length;
				}
			},
			load : function(params){
				Util.Easyui.dom.datagrid('load',params);
			},
			reload : function(){
				if(!Util.Easyui.default['state']) return;
				Util.Easyui.dom.datagrid('reload');
			},
			get: function(url){
				easyloader.locale = 'zh_CN';
				easyloader.load('datagrid', function(){
					Util.Easyui.default['config']['url'] = url;
					Util.Easyui.default['state'] = true;
					// console.log(Util.Easyui.default['config']);
					Util.Easyui.dom.datagrid(Util.Easyui.default['config']);
				});
			}, 
			resize: function(){
				if(!Util.Easyui.default['state']){return;}
				var box = Util.Easyui.default['table'] || Util.Easyui.dom.parents('.table_box');
				// console.log(box.parents(window).height() - box.offset().top);
				Util.Easyui.dom.datagrid('resize', {
					width: (Util.Easyui.default['maxsize'])?box.width():Util.Easyui.default['config']['width'],
					height: (Util.Easyui.default['maxsize'])?(box.parents(window).height() - box.offset().top):Util.Easyui.default['config']['height']
				});
			},
			diyColumns: {
				destory: function(cover){
					cover.slideUp(150, function() {
		                cover.empty().remove();
		            });
				},
				loader: function(o){
					/**
					 * [o 加载自定义列]
					 */
					if(!Util.Easyui.default['state']){return;}
					var fieldList = $('.field_list'); 

			        if(fieldList.length){ Util.Easyui.diyColumns.destory(fieldList); return; }

					var _href = $.trim($(o).attr('href'));
					if(Util.isDefined(_href)==''){return;}

					if(_href && _href.indexOf('#') === 0) {
						var _table = $(_href);
						var fields = _table.datagrid('getColumnFields',true).concat(_table.datagrid('getColumnFields'));
						// console.log(fields);
						$('body').append('<div class="field_list" data-table="' + _href + '"></div>');
		                var _ftml = '<ul>';
		                var isCkAll = true;
		                $.each(fields, function(index, field) {
		                    var _opt = _table.datagrid('getColumnOption', field);
		                    var _title = $.trim(_opt.title);
		                   	var check =  (_title && _opt.hidden)? "":"checked='checked'";
		                   	if(_opt.hidden){ isCkAll = false; }
		                    if(_title) {
								_ftml += ('<li><label class="checkbox"><input type="checkbox" name="fields" '+ check +' value="' + field + '"/><span>' + _title + '</span></label></li>');
		                    }
		                });
		                _ftml += '</ul>';
		                _ftml += ('<div class="reset_fields"><label class="checkbox"><input ' + (isCkAll ? 'checked="checked"' : '') + ' type="checkbox" class="check-all" data-name="fields"/><span>全选</span></label>');
		                _ftml +=('<button class="btn btn-small">确定</button></div>');
		                
		                fieldList = $('.field_list');
		                fieldList.html(_ftml);
		                fieldList.offset({
		                    // left: $(o).offset().left - 439,
		                    top: $(o).offset().top + 30
		                });
		                fieldList.slideDown(150);
		                try{
		                	$('.field_list').find('button').on("click",function(){
		                		Util.Easyui.diyColumns.hide(fieldList);
		                	})
		                }catch(e){console.log(e)}
					}
				},
				hide: function(c){
					/**
					 * [关闭自定义列]
					 */
					if(!Util.Easyui.default['state']){return};
					Util.Easyui.default['hiddenColumn'] = [];
					
					$(c).find('li :checkbox[name="fields"]').each(function(index, field) {
						if(field.checked){
							Util.Easyui.dom.datagrid('showColumn',field.value);
						}else{
							Util.Easyui.dom.datagrid('hideColumn',field.value);
							Util.Easyui.default['hiddenColumn'].push(field.value);
						}
					});

					Util.Easyui.diyColumns.destory($(c));
					Util.Easyui.dom.datagrid("resize");
					var columnsKey = Util.Easyui.dom.attr('data-columnskey');
					if(Util.isDefined(columnsKey)!=''){
						Util.saveUserdata(columnsKey+':hidden',Util.Easyui.default['hiddenColumn'].join(','));
					}
				}
			},
			search: function(){
				/**
				 * [datagrid search]
				 * @param [search field需要查找的键值对]
				 */
		        if(Util.Easyui.dom.length) {
		            Util.Easyui.dom.datagrid('clearChecked');
		            Util.Easyui.dom.datagrid('clearSelections');
		            var parmAry = Util.Easyui.searchbar.serializeArray();
		            var parm = {};
		            $.each(parmAry, function(index, item) {
		                parm[item.name] = item.value;
		            });
		            
		            Util.Easyui.dom.trigger('search-start', parm);
		            if(Util.Easyui.dom.attr('data-preve-load') !== 'yes') {
		                Util.Easyui.dom.datagrid('load', parm);
		            }
		        }
			}
		},
		getCheckedRowId: function(table,id){
			var ids = '';
			if(table){
				var checkedItems = table.datagrid('getChecked');
					ids = checkedItems.length? $.map(checkedItems,function(item, index){ return item[id];}).join(','):'';
			}
			return ids;
		},
		getCheckedRow:function(table,id){
			var checkedItems='';
			if(table){
				checkedItems = table.datagrid('getChecked');
			}
			return checkedItems;
		},
		filterTool: function(scbar,number){
			// console.log(number);
			return (function(_f,count){
				if(!_f.find('.add_req') || !_f.find('.clear_all')){ return;}
				// console.log(count);
				if(!count || typeof count == 'undefined'){ count = 5;}

				//特殊筛选条件的元素
				var specFts = $('a[special-filter]');

				var adn = _f.find('.add_req'),
					dln = _f.find('.clear_all');
				var d = {
					list: [],
					tmp: [],
					tmplist: []
				}
				var Ft = {
					bindv: function(btn,evt,cbk){
						/**
						 * [description] 绑定条件筛选的按钮事件
						 * @param  {[type]} (按钮,事件,回调函数)
						 * @return {[type]} 执行回调
						 */
						btn.off(evt).on(evt,function(e){
							e.preventDefault();
							cbk && cbk(this);
						})
					},
					settmp: function(f,n){
						/**
						 * [vt description] 当前添加的tmp条件
						 * @type {Object}
						 */
						var vt = {};
							vt.name = n;
						// debugger;
						if(f.find('.search_write_box').length>0 && !f.find('.search_write_box').hasClass('disn')){
				            //输入框
				            vt.value = $.trim(f.find('.search_write').val());
				            vt.text = vt.value+'';
				        }else if(f.find('.search_select_box').length>0 && !f.find('.search_select_box').hasClass('disn')){
				            //下拉框
				            vt.value = f.find('.search_select').getZsValue();
				            vt.text = f.find('.search_select').zsGetBox().find('.zs-item.current').text();
				        }else if(!f.find('.search-province-city').hasClass('disn')||!f.find('.search-brand-series-spec').hasClass('disn')){
				            var comb_box = !f.find('.search-province-city').hasClass('disn')? f.find('.search-province-city'): f.find('.search-brand-series-spec');
				            comb_box.find('.z-select').each(function(){
				                var _t = $(this);
				                var fv = _t.getZsValue();
				                var fn = _t.attr('data-fname');
				                Ft.deltmp(fn);
				                if(fv) {
				                    vt.value = fv;
				                    vt.text = _t.zsGetBox().find('.zs-item.current').text();
				                    vt.name = fn;
				                }
				            })
				        }
				        // console.log(vt);
				        return vt;
					},
					deltmp: function(n){
						/**
						 * [description] 删除条件
						 */
				        d.tmplist = [];
				        for(var i in d.list) {
				            if(d.list[i].name != n) {
				                d.tmplist.push(d.list[i]);
				            }
				        }
				        d.list = d.tmplist;
					},
					render: function(f){
						/**
						 * [f description] 重新渲染search_form
						 * @type {[type]}
						 */
						var _sl = f.find('.search_list').addClass('disn');
				        var _ul = _sl.find('.req_list ul').empty();
				        if(d.list.length) {
				            _sl.removeClass('disn');
				            for(var i in d.list) {
				                _ul.append('<li title="' + d.list[i].text + '"><span>' + d.list[i].text + '</span><input type="hidden" name="' + d.list[i].name + '" value="' + d.list[i].value + '"/><i class="clear_req"></i></li>');
				            }
				            Ft.bindv(_ul.find('.clear_req'),'click',function(ts){
				            	// console.log(ts,'click');
				            	var _n = $(ts).parent('li').find('input:hidden').attr('name');
						        Ft.deltmp(_n);
						        Ft.render(f);
				            })
				        }
				        if(f.find(".search_list").hasClass("disn")){
				            f.removeClass("t_filter");
				        }else{
				            f.addClass("t_filter");
				        }
				        if(Util.Easyui.default['state']){
				        	Util.Easyui.resize();
				        }
					},
					init: function(count, specfts){
						function myaddfun(d){
							if(d.tmp['name'] && d.tmp['value'] && d.tmp['text']){
								var isAdded = false;
					            for(var i in d.list) {
					                if(d.list[i].name == d.tmp['name']) {
					                    isAdded = true;
					                    d.list[i].value = d.tmp['value'];
					                    d.list[i].text = d.tmp['text'];
					                    break;
					                }
					            }
					            if(!isAdded) {
					                if(d.list.length < count) {
					                    d.list.push({
					                        name: d.tmp['name'],
					                        value: d.tmp['value'],
					                        text: d.tmp['text'] || d.tmp['value']
					                    });
					                } else {
					                    layer.msg('最多只能增加'+count+'个筛选条件', {
					                        time: 3000
					                    });
					                }
					            }
					            Ft.render(_f);
							}
						}
						if(specfts){
							specfts.each(function(_ks){
								Ft.bindv(specfts.eq(_ks),'click.specfts',function(ts){
									var myftdata = $(ts).data('setfilters');
									// console.log(myftdata);
									if(myftdata.length>0){
										$('.search_form').slideDown(300);
										$.each(myftdata, function(_kv){
											d.tmp = myftdata[_kv];
											myaddfun(d);
										})
									}
								})
							})
						}

						Ft.bindv(adn,'click',function(ts){
							/*add*/
							var k = (_f.find('.search_key').length)? _f.find('.search_key').getZsValue():'';
							if(k==''){return;}
							d.tmp = Ft.settmp(_f,k);
							myaddfun(d);
						});

						Ft.bindv(dln,'click',function(ts){
							/*clear all*/
							var _index = layer.confirm("您确定要清除筛选条件吗?", { title: '清除筛选', icon: 3 },function(){
					            d.list = [];
					            Ft.render(_f);
					            layer.close(_index);
					        })
						});
					}
				}
				if(specFts.length>0){
					Ft.init(count, specFts);
				}else{
					Ft.init(count);
				}
				
			})(scbar,number);
		},
		Treegrid:{
			'W':0,'W':0,
			default: {
				'table':null,
				'config':{},
				'columns':[],
				'hiddenColumn':[],
				'maxsize': true,
				'state':false
			},
			init: function(obj,colum,conf){
				/**
				 * [obj datagrid表格]
				 * [colum datagrid数据列]
				 * [conf datagrid配置]
				 * @type {[type]}
				 */
				Util.Treegrid.dom = $(obj.grid) || $('#treegrid');
				Util.Treegrid.searchbar = $(obj.sbar) || $('.search_form');

				if(Util.Treegrid.W === 0 || Util.Treegrid.H === 0){
					if(Util.Treegrid.dom.parent('.table_box').length>0){
						Util.Treegrid.default['table'] = Util.Treegrid.dom.parent('.table_box').length>0? Util.Treegrid.dom.parent('.table_box'):Util.Treegrid.dom;
						Util.Treegrid.W = Util.Treegrid.default['table'].width();
						Util.Treegrid.H = $(window).height() - Util.Treegrid.default['table'].offset().top - 2;
					}
				}

				Util.Treegrid.setColumns(colum); //所有列
				Util.Treegrid.config(conf);
				//绑定搜索工具栏按钮
				if(!Util.Treegrid.searchbar || Util.isDefined(Util.Treegrid.searchbar) == '') { return;}
				Util.Treegrid.searchbar.find('.search_btn').off('click').on('click',function(){
					Util.Treegrid.search();
				})
			},
			config: function(option){
				if(Util.isDefined(option)!==''){
					var opt = option;
					if((opt.width && Util.isDefined(opt.width)!='') || (opt.height && Util.isDefined(opt.height)!='')){
						Util.Treegrid.default['maxsize'] = false;
					}
				}

				Util.Treegrid.default['config'] = {
					method       : 'get', //数据方式
					width        : Util.Treegrid.W, //宽度
					height       : Util.Treegrid.H, //高度
					striped      : false,//条纹行
					fitColumns   : true, //宽度自适横向滚动条
					singleSelect : false, //选中单行
					rownumbers   : true,
					collapsible  : true,
					autoRowHeight: true, //自动行高
					pagination   : true, //是否分页
					remoteSort   : true, //是否允许排序等待,false本页排序,true后台排序
					multiSort    : false, //多个排序
					//toolbar      :'#Toolb', //出现工具栏
					pageSize     : 100, //初始化页内行数
					pageList     : [50,100,200],
					loadMsg      : '数据加载中，请稍候...',
					checkOnSelect: true,
					columns      : [Util.Treegrid.default['columns']],
					onBeforeLoad: function() {

	                },
	                onClickRow: function(index, row) {

	                }
				};
	            Util.Treegrid.default['config'] = Util.isDefined(opt)!==''? Util.Treegrid.params(opt,Util.Treegrid.default['config']): Util.Treegrid.default['config'];
	            console.log(Util.Treegrid.default['config']);
			},
			params: function(option,params){
				for(var key in params){
					option[key] = Util.isDefined(option[key])!==''? option[key]:params[key];
				}
				return option;
			},
			setColumns: function(column){
				Util.Treegrid.default['columns'] = (Util.isDefined(column)!=='' && column.length>0)?column:[];
			},
			load : function(params){
				Util.Treegrid.dom.treegrid('load',params);
			},
			reload : function(){
				if(!Util.Treegrid.default['state']) return;
				Util.Treegrid.dom.treegrid('reload');
			},
			get: function(url){
				easyloader.locale = 'zh_CN';
				easyloader.load('treegrid', function(){
					Util.Treegrid.default['config']['url'] = url;
					Util.Treegrid.default['state'] = true;
					Util.Treegrid.dom.treegrid(Util.Treegrid.default['config']);
				});
			}, 
			resize: function(){
				if(!Util.Treegrid.default['state']){return;}
				var box = Util.Treegrid.default['table'] || Util.Treegrid.dom.parents('.table_box');
				Util.Treegrid.dom.treegrid('resize', {
					width: (Util.Treegrid.default['maxsize'])?box.width():Util.Treegrid.default['config']['width'],
					height: (Util.Treegrid.default['maxsize'])?(box.parents(window).height() - box.offset().top):Util.Treegrid.default['config']['height']
				});
			},
			search: function(){
				/**
				 * [datagrid search]
				 * @param [search field需要查找的键值对]
				 */
		        if(Util.Treegrid.dom.length) {
		            Util.Treegrid.dom.datagrid('clearChecked');
		            Util.Treegrid.dom.datagrid('clearSelections');
		            var parmAry = Util.Treegrid.searchbar.serializeArray();
		            var parm = {};
		            $.each(parmAry, function(index, item) {
		                parm[item.name] = item.value;
		            });
		            
		            Util.Treegrid.dom.trigger('search-start', parm);
		            if(Util.Treegrid.dom.attr('data-preve-load') !== 'yes') {
		                Util.Treegrid.dom.treegrid('load', parm);
		            }
		        }
			}
		},
		Tbox:{
			load: function(tab){
				if(!$(tab).hasClass('tab_links')){return;}
				var t = this;
					t.isTabAuto = false;
				var auto = $(tab).attr('attr-tabload'); //是否预加载
				var tba = $(tab).find('a');

				if(Util.isDefined(auto)=='auto'){
					//加载所有当前tab下的标签
					t.isTabAuto = true;
					tba.each(function(i,item){
						var x = $($(item).attr('href'));
						var _url = x.attr('data-url');
						if(!x || !_url){ return;}
						t.getTml(x, _url);
					});
				}

				tba.off('click').on('click',function(e){
					e.preventDefault();
					t.setShow($(this));
				});
				if($(tab).find('.active').length>0){ 
					t.setShow($(tab).find('.active'));
					return; 
				}
				//默认加载第一个标签
				tba.eq(0).addClass('active');
				t.setShow(tba.eq(0));
			},
			getTml: function(box,url){
				var t = this;
				if(box.attr('tab-load') == 'done'){return;}
				$.get(url, function(html) {
                    box.html(html);
                    box.attr('tab-load','done');
                    box.find('.z-select').zSelect();
                });
			},
			setShow: function(active){
				var t = this;
				var xid = $(active).attr('href');
				if(!t.isTabAuto){
					t.getTml($(xid),$(xid).attr('data-url'));
				}
				// console.log(xid);
				$(active).addClass('active').siblings('a').removeClass('active');
				if(!$(xid).hasClass('tbox_inner')){
					$(xid).siblings('.tbox').hide();
					$(xid).show();
					return;
				}
				$(xid).siblings('.tbox').removeClass('visib');
				$(xid).addClass('visib');
			}
		},
		Dialog:{
			config: {
				shade: 0.2,
				area: ['40%','30%']
			},
			confirm: function(opts){
				var tit = opts.title || '询问框', content = opts.content || '',_btns = opts.btn || ['确定'];
				var cbk = opts.callback || function(){};
				layer.ready(function(){
					var c_layer = layer.confirm('<div style="font-size:12px;">'+content+'</div>', {
						title: tit,
						btn: _btns //按钮
					}, function(){
						layer.close(c_layer);
						cbk && cbk();
						//opts.ok && opts.ok(function(){
							//layer.close(c_layer);
						//});
					}, function(){
						opts.cancel && opts.cancel();
					});
				});
			},
			note: function(opts){
				var tit = opts.title || '提示', content = opts.content || '', _btns = opts.btn || ['确定'];
				var cbk = opts.callback || function(){};
				layer.ready(function(){
					var l_tip = layer.alert('<div style="font-size:12px;">'+content+'</div>', {
						title: tit,
						btn: _btns,
						shade: 0.2
					}, function(){
						layer.close(l_tip);
						cbk && cbk();
					});
				});
			},
			msg: function(str, _time){
				layer.ready(function(){
					layer.msg(str,{time: _time || 1500});
				});
			},
			open: function(opts, backfun){
				var t = this;
				var tit = opts.title || '信息', url = opts.url || false;
				var w = opts.width || '480px', h = opts.height || '450px';
				var cont = opts.content || '', _btns = opts.btn || ['确定'];
				var cbk = opts.callback || function(){};

				if(!url && cont==''){return};

				var isOpenInParent = (opts.father == 'top')? true: false;
				layer.ready(function(){
					function openly(htmls){
						if(isOpenInParent){
							window.parent.layer.open({
								type: 1,
								title: tit,
								area: [w, h],
								btn: _btns,
								shade: 0.2,
								content: htmls
							});
						}else{
							layer.open({
								type: 1,
								title: tit,
								area: [w, h],
								btn: _btns,
								btnAlign: 'c',
								moveType: 1,
								shade: 0.2,
								content: htmls,
								yes:function(index){
									layer.close(index);
									cbk && cbk();
								}
							});
						}
						if(backfun instanceof Function){
							backfun();
						}
					}
					if(eval(opts.framer) && isOpenInParent){
						//iframe in parent window
						window.parent.layer.open({
							type: 2,
							title: tit,
							area: [w, h],
							shade: 0.2,
							content: url //iframe的url
						});
						if(backfun instanceof Function){
							backfun();
						}
					}
					if(eval(opts.framer) && !isOpenInParent){
						//iframe not in parent window
						layer.open({
							type: 2,
							title: tit,
							area: [w, h],
							shade: 0.2,
							content: url //iframe的url
						});
						if(backfun instanceof Function){
							backfun();
						}
					}
					if(!eval(opts.framer)){
						if(!url){
							openly(cont);
							return;
						}
						//normal
						$.get(url,function(_html){
							//normal
							openly(_html);
						})
					}
				});
			},
			Datagrid: function(grid,opt){
				var option = {
	                idField: 'id',
	                rownumbers: true, //是否显示行号
	                method: 'get', //数据方式
	                striped: true, //条纹行
	                fitColumns: true, //宽度自适横向滚动条
	                singleSelect: false, //选中单行
	                collapsible: true,
	                autoRowHeight: true, //自动行高
	                pagination: true, //是否分页
	                multiSort: false, //多个排序
	                pageSize: 30, //初始化页内行数
	                pageList: [30, 50, 100],
	                loadMsg: '正拼命加载，请稍候...',
	                checkOnSelect: true,
	                onBeforeLoad: function() {
	                    $(this).datagrid('clearChecked');
	                    $(this).datagrid('clearSelections');
	                },
	                onClickRow: function(index, row) {
	                    var _table = $(this);
	                    _table.datagrid('clearChecked');
	                    _table.datagrid('clearSelections');
	                    // console.log(row);
	                    _table.datagrid('checkRow', index);
	                },
	                onLoadSuccess: function(){
	                }
	            };
	            for(var i in opt) {
	                option[i] = opt[i];
	            }
	            
	            easyloader.locale = 'zh_CN';
				easyloader.load('datagrid', function(){
	            	grid.datagrid(option);
	            })
			}
		},
		Form:{
			submit: function(opts,callback){
				if(!opts) return;
				if(!opts.form || Util.isDefined(opts.form) == '' || !opts.url || Util.isDefined(opts.url) == '') return;
				if(!opts.form.is('form')) return;
				var _f = opts.form;
				var _u = opts.url;
				_f.on('submit',function(e){
					var target = e.target || e.srcElement;
					var subbtn = $(target).find("[type='submit']");
					if(subbtn.hasClass('disabled')) return;

					if(callback && callback($(target))){
						var ary = _f.serializeArray();
						var _p = {};
						$.each(ary,function(_i,_t){
							_p[_t.name] = _t.value;
						})
						console.log(_p);
						
						subbtn.addClass('disabled');
						$.ajax({
		                    url: _u,
		                    type: 'post',
		                    data: _p,
		                    dataType: 'json',
		                    success: function(result) {
		                  		var sout = setTimeout(function(){ 
		                  			subbtn.removeClass('disabled'); 
		                  			clearTimeout(sout);
		                  		}, 1500);
		                    	if(opts.successfun){
		                    		opts.successfun(result);
		                    		return;
		                    	}
		                    	console.log(result);
		                    },
		                    error: function(err){
		                    	var sout = setTimeout(function(){ 
		                  			subbtn.removeClass('disabled'); 
		                  			clearTimeout(sout);
		                  		}, 1500);
		                    	if(opts.errorfun){
		                    		opts.errorfun(err);
		                    		return;
		                    	}
		                    	console.log(err);
		                    }
		                })
					}
				})
			},
			tips: {
	            clear: function(_f){
	                _f.find('.required_tip').remove();
	            },
	            set: function(_input){
	                _input.parents('.row')
	                .append('<p class="colortip required_tip">'+ (_input.attr('placeholder')||'必填项不能为空') +'</p>');
	            }
	        }
		},
		InitSelect: function(d){
			d.find('.z-select').zSelect();
		},
		CombDown:{
	        init: function(_arr,_opts){
	        	/*
	        		_arr->doms type:array 要初始的下拉框及初始值
					_opts type:object 参数配置及url
	        	 */
	            if(!_arr.length>0) {return;}
	            var vid = _opts.valueField || 'id';
	            var vtext = _opts.textField || 'name';
	            var optArr = [];
	            for(var r in _arr){
	                optArr.push({
	                    elem: _arr[r][0],
	                    setValue: (_arr[r][1] && _arr[r][1]!='')?_arr[r][1]:'',
	                    valueField: vid,
	                    textField: vtext,
	                    url: _opts.url +'?'+(r>0?'pid=@parentValue':'')
	                })
	            }
	            // console.log(optArr);
	            $.zSelectGroup(optArr);
	        }
	    },
	    ListEval: {
	    	bindClear: function(jqdom,list){
	    		var t = this;
	    		jqdom.data('markdata',list);
	    		jqdom.off('click').on('click','.clear_req',function(){
		    		var _t = $(this).parent('li');
		    		Util.Dialog.confirm({
		    			'title':'确认层',
		    			'content':'确认执行删除操作？',
		    			'ok':function(cbk){
		    				t.removeval({'key': _t.attr('data-key'),'title': _t.attr('title')},list);
		    				t.setlist(jqdom,list);
		    				cbk();
		    			}
		    		})
		    	})
	    	},
	    	checkval: function(prm,list){
		    	var index = -1;
		    	$.each(list,function(_i,_k){
		    		if(prm.key == _k.key){
		    			index = _i;
		    		}
		    	})
		    	return index;
		    },
		    setlist: function(jqdom,list){
		    	var t = this;
		    	var mhtml = '';
		    	$.each(list,function(_k,_item){
		    		mhtml += '<li title="'+_item.title+'" data-key="'+_item.key+'" class="mark">';
		    		mhtml += '<span>'+_item.title+'</span><i class="clear_req"></i></li>';
		    	})
		    	jqdom.html(mhtml);
		    	t.bindClear(jqdom,list);
		    },
		    removeval: function(prm,list){
		    	var t = this;
		    	var index = t.checkval(prm,list);
				if (index>-1) {
					list.splice(index, 1);
				}
		    }
	    },
	    playRecode: function(recPath){
            Util.Dialog.open({

            	title: '播放录音',
            	width: '400px',
            	height: '170px',
                content: '<div class="rec-player" style="margin-top:20px;text-align:center;"><audio style="margin: 20px" src="' + recPath + '" controls="controls" autoplay="autoplay"></audio></div>'
            });
	    },
	    Time: {
	    	setTime: function(d,_f){
				var self = this;
					var endtime = begintime = self.getBeforeDate(0);
					var d = (!d||d===''||(typeof(d)=="undefined"))? 0 : d;
						begintime = self.getBeforeDate(d);
					_f.find("input[name='starttime']").val(begintime);
					_f.find("input[name='endtime']").val(endtime);
	    	},
	    	getBeforeDate: function(n){
	    		var d = new Date();
					d.setDate(d.getDate()-n);
				var y = d.getFullYear();
				var m=d.getMonth()+1;
				var d=d.getDate();
					s = y+"-"+(m<10?('0'+m):m)+"-"+(d<10?('0'+d):d);
					return s;
	    	},
	    	getNowTime: function(){
	    		var d = new Date(); 
				var y=d.getYear(); 
				var m=d.getMonth()+1; 
				var d=d.getDate(); 
				var hour=d.getHours(); 
				var minute=d.getMinutes(); 
				var second=d.getSeconds(); 
				return "20"+y+"-"+m+"-"+d+" "+hour+":"+minute+":"+second; 
	    	}
	    }
	    
	}
	return Util;
})();

  $.extend({
      date2YmdHms: function(dt) { //date转为YYYY-MM-DD HH:mm:ss
        function numZero(num, len) {
	        var res = num + '';
	        if(res.length < len) {
	            res = ('0' + res);
	            return numZero(res, len);
	        } else {
	            return res;
	        }
	    }
        dt = dt ? (new Date(dt * 1)) : (new Date());
        var y = numZero(dt.getFullYear(), 4);
        var M = numZero(dt.getMonth() + 1, 2);
        var d = numZero(dt.getDate(), 2);
        var h = numZero(dt.getHours(), 2);
        var m = numZero(dt.getMinutes(), 2);
        var s = numZero(dt.getSeconds(), 2);
        return y + '-' + M + '-' + d + ' ' + h + ':' + m + ':' + s;
      },
      ymdhms2date: function(ymdhms) { //'2022-10-10 10:11:12'
          function dateClear() { //得到 0000-01-01 00:00:00对应的date
              var dt = new Date();
              dt.setFullYear(0);
              dt.setMonth(0);
              dt.setDate(1);
              dt.setHours(0);
              dt.setMinutes(0);
              dt.setSeconds(0);
              dt.setMilliseconds(0);
              return dt;
          }
          function ymd2date(ymd) { //ymd---'2022-10-10'
              var dt = dateClear();
              var _ymd = ymd.split('-'); //['2022','10','10']
              dt.setFullYear(_ymd[0] * 1);
              dt.setMonth(_ymd[1] * 1 - 1);
              dt.setDate(_ymd[2] * 1);
              return dt;
          };
          var dt = ymd2date(ymdhms.split(' ')[0]);
          if(ymdhms.split(' ')[1]) {
              var _hms = ymdhms.split(' ')[1].split(':'); //['10','11','12']
              dt.setHours((_hms[0] * 1) || 0);
              dt.setMinutes((_hms[1] * 1) || 0);
              dt.setSeconds((_hms[2] * 1) || 0);
          }
          return dt;
      },
      getUrlOpt: function(url) { //url---xxxx.html?x=1&y=2&z=3&xx%5B%5D=1&xx%5B%5D=2&xx%5B%5D=3
          var _search = decodeURIComponent(url.split('#')[0].split('?')[1] || ''); //x=1&y=2&z=3&xx[]=1&xx[]=2&xx[]=3
          var kvs = _search.split('&'); //['x=1','y=2','z=3','xx[]=1','xx[]=2','xx[]=3'];
          var opts = {};
          for(var i in kvs) {
              var _kv = kvs[i].split('='); //['x','1']    ['y','2']    ['z','3']    ['xx[]','1']    ['xx[]','2']    ['xx[]','3']
              if(/\[\]$/.test(_kv[0])) {
                  var _k = _kv[0].replace(/\[\]$/, '');
                  if(opts[_k]) {
                      opts[_k].push(_kv[1])
                  } else {
                      opts[_k] = [_kv[1]];
                  }
              } else {
                  opts[_kv[0]] = _kv[1];
              }
          }
          return opts; //{x:1,y:2,z:3,xx:['1','2','3']}
      }
  });
