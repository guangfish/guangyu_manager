$.ajaxSetup({
	   dataType:"json",
	    timeout:10000,
		statusCode:{
			401:function(data){
					  location.href="/admin/login";
				  },
			403:function(data){
				  alert("对不起，您的权限不足");
			  }
		}
	}
);

var fangs = {
		exportExcel:function(url,data){
			if(url.indexOf("?")==-1){
				url+="?";
			}
			var form = $("#downLoanFrm");
			form.html("");
			if(data){
				$.each(data,function(i,n){
					form.append('<input type="hidden" name="'+i+'" value="'+n+'"/>');
				});
			}
			form.attr("action",url+new Date().getTime());
			form.trigger('submit');
		},
		formatDate:function(dt, fmt) {
			var o = {
		        "M+": dt.getMonth() + 1, //月份 
		        "d+": dt.getDate(), //日 
		        "h+": dt.getHours(), //小时 
		        "m+": dt.getMinutes(), //分 
		        "s+": dt.getSeconds(), //秒 
		        "q+": Math.floor((dt.getMonth() + 3) / 3), //季度 
		        "S": dt.getMilliseconds() //毫秒 
		    };
		    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (dt.getFullYear() + "").substr(4 - RegExp.$1.length));
		    for (var k in o)
		    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		    return fmt;
		},
		initDataTable:function(id,options){
			$.fn.dataTable.ext.errMode = 'none';
			 var initOptions = {
			            paging: true,
			            lengthChange: true,
			            searching: false,
			            ordering:false,
			            info: true,
			            autoWidth: false,
						processing:true,
						order: [],
			            lengthMenu: [10, 20, 30 ],
			            columnDefs:[ 
                            {targets:'_all',defaultContent:''}
							],
			            language:{
			               info:"当前显示第 _START_ 至 _END_ 条,共 _TOTAL_ 条",
			               infoEmpty: "",
			               emptyTable:'没有查询到数据',
			               zeroRecords:'没有查询到数据',
			               lengthMenu:"每页显示 _MENU_ 条",
			               paginate: {
			        				first:      "第一页",
			       				 last:      "最后一页",
			        				next:       "下一页",
			        				previous:   "上一页"
			   				 }
			            },
			             serverSide:true
					};
			 var settings = $.extend(true,initOptions,options);
			 settings.ajax.data = function(d){
				delete d["columns"];
				delete d["search"];
				delete d["order"];
				if(options&&options.ajax&&options.ajax.data){
					options.ajax.data(d);
				}
			 }
			 var dt =  $("#"+id).DataTable(settings);
			 dt.on("preXhr.dt",function(e, settings, data ){
				var order = dt.order();
				var orderStr = "";
				$.each(order,function(i,n){
					if(n.length>1&&n[0]&&[1]){
						orderStr = orderStr+","+dt.column(n[0]).dataSrc()+" "+n[1];
					}
				})
				if(orderStr.length>0){
				  orderStr = orderStr.substring(1);
				  data.orderStr = orderStr;
				}
			  });
			 return dt;
		},
		confirm:function(options){
			var initOpt = {
					title:'确认',
					text:'您是否确认?',
					ok:function(){
						
					},
					cancel:function(){
						
					}
			}
			$.extend(initOpt,options);
			$('#confirmModal_btn_cancel').on('click',function(){
				if($.isFunction(initOpt.cancel)){
					initOpt.cancel();
					$("#confirmModal").modal('hide');
				}
			});
			$('#confirmModal_btn_ok').on('click',function(){
				if($.isFunction(initOpt.ok)){
					initOpt.ok();
					$("#confirmModal").modal('hide');	
				}
			});
			$("#confirmModalLabel").html(initOpt.title);
			$("#confirmModalText").html(initOpt.text);
			$("#confirmModal").modal();
		},
		post:function(url,data,success,error,timeout,before,compelete){
			fangs.ajax('POST',url,data,success,error,timeout,before,compelete);
		},
		get:function(url,data,success,error,timeout,before,compelete){
			fangs.ajax('GET',url,data,success,error,timeout,before,compelete);
		},
		ajax:function(type,url,data,success,error,timeout,before,compelete){
			if(!timeout){
				timeout=10000;
			}
			$.ajax({
				timeout:timeout,
                url:url,				
				type:type,
				 data:data,
				dataType:'json',
				success:function(data){
					if(data){
						switch(data.code){
						case 200:
							if($.isFunction(success)){
							success(data);
						}
						break;
						case 400:
							if($.isFunction(error)){
								error(data);
							}else{
								if(data.msg){
									alert(data.msg);
								}
							}
							break;
						default:
							if($.isFunction(error)){
								error(data);
							}else{
								if(data.msg){
									alert(data.msg);
								}
							}
							break;
						}
					}
				},
				error:function(){
					if($.isFunction(error)){
						error();
						return;
					}
				}
			});
		}
}