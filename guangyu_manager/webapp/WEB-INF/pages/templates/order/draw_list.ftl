<!DOCTYPE html>
<html>
<head>
   <@model.webheadTmp/>
</head>

<body class="hold-transition skin-blue fixed sidebar-mini">
<div class="wrapper">
 
  <!-- Main Header -->
 	<#include "../header.ftl" />

  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <ol class="breadcrumb">
      </ol>
    </section>

    <!-- Main content -->
    <section class="content container-fluid">
      <div class="row">
      
        <div class="col-md-12">
          <div class="box">
            <div class="box-header with-border">
              <h3 class="box-title"></h3>
            </div>
            <div class="box-body">
            
              <form id="searchForm" >
              <div class="row">
              <div class="col-md-2">
                  <div class="form-group">
                    <label>手机号：</label>
                    <input class="form-control" type="text"  name="mobile"/>
                  </div>
                </div>
                
                <div class="col-md-2">
                  <div class="form-group">
	                <label>申请时间:</label>
	                <div class="input-group">
	                  <div class="input-group-addon">
	                    <i class="fa fa-clock-o"></i>
	                  </div>
	                  <input type="text" class="form-control pull-right" id="applyTime" name="applyTime">
	                </div>
                   </div>
                 </div>
                 
                 <div class="col-md-2">
                  <div class="form-group">
	                <label>打款时间:</label>
	                <div class="input-group">
	                  <div class="input-group-addon">
	                    <i class="fa fa-clock-o"></i>
	                  </div>
	                  <input type="text" class="form-control pull-right" id="payTime" name="payTime">
	                </div>
                   </div>
                 </div>
                 
                <div class="col-md-2">
                <label for="type">打款状态：</label>
                 <select class="form-control select2" id="status" name="status">
                  <option value="" textVal="">请选择</option>
                  <option value="1" textVal="未打款">未打款</option>
                  <option value="2" textVal="已打款">已打款</option>
                 </select>
                </div>
                
                 <div class="col-md-2">
                  <div class="form-group">
                    <label>支付宝账号：</label>
                    <input class="form-control" type="text"  name="alipayAccount"/>
                  </div>
                </div>
                </div>
                
                <div class="col-md-10" >
                  <button type="submit" class="btn btn-info pull-right">查询</button>
                </div>
            </form>
            <hr>
            <br> <br>
            <div class="table-responsive">
              <table class="table table-striped table-bordered table-hover table-condensed" id="drawCacheList">
              </table>
            </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</div>

  <div class="modal fade" id="userOrderModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" >
  <div class="modal-dialog" role="document">
    <div class="modal-content" style="width: 1200px;margin-left:-179px;">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">管理员</h4>
      </div>
      <div class="modal-body">
         <form id="adminForm">
         <div id="msg" class="alert alert-danger" role="alert" style="display:none"></div>
          <input type="hidden" id="id" name="id">
          

        </form>
            
        <div class="table-responsive">
          <table class="table table-striped table-bordered table-hover table-condensed" id="userOrderList">
          </table>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
      </div>
    </div>
  </div>
</div>
  <!-- Main Footer -->
  <#include "../footer.ftl" />
</div>
<!-- REQUIRED JS SCRIPTS -->
 <@model.webendTmp/>
<!-- --------------------------------时间范围选择器 begin---------------------------------------------- -->
<link rel="stylesheet" type="text/css" href="${request.contextPath}/static/adminLTE/datetime/bootstrap-timepicker.css">

<script type="text/javascript" src="${request.contextPath}/static/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="${request.contextPath}/static/adminLTE/datetime/moment.min.js"></script>
<script type="text/javascript" src="${request.contextPath}/static/adminLTE/datetime/daterangepicker.js"></script>
<script type="text/javascript" src="${model.basePath}/static/adminLTE/datetime/bootstrap-datepicker.min.js"></script>
<script type="text/javascript">


$('#applyTime').daterangepicker({
    "timePicker": true,
    "timePicker24Hour": true,
     timePickerIncrement: 30, 
    "locale": {
                    format: 'YYYY/MM/DD',
                    applyLabel: "应用",
                    cancelLabel: "取消",
                    resetLabel: "重置",
              }
});
$('#payTime').daterangepicker({
    "timePicker": true,
    "timePicker24Hour": true,
     timePickerIncrement: 30, 
    "locale": {
                    format: 'YYYY/MM/DD',
                    applyLabel: "应用",
                    cancelLabel: "取消",
                    resetLabel: "重置",
              }
});
$('#applyTime').val("");
$('#payTime').val("");
</script>
<!-- --------------------------------时间范围选择器 end---------------------------------------------- -->

    <script type="text/javascript">
  //定义查询变量
	var searchData = new Object();
	searchData.pageNum = 1;
	//定义分页相关数据
	var pageUrl ='/load/drawcache/list';
    
  $("#searchForm").submit(function(){
		search();
		return false;
	});
     var rowData=null;

     var dt=null;
	 $(function() {
		 dt = fangs.initDataTable('drawCacheList',{
				ajax:{
                url:pageUrl,
                data:function(d){
                  d.applyTimeStart = $.trim($("#applyTime").val().split('-')[0]);
                  d.applyTimeEnd = $.trim($("#applyTime").val().split('-')[1]);
                  d.payTimeStart = $.trim($("#payTime").val().split('-')[0]);
                  d.payTimeEnd = $.trim($("#payTime").val().split('-')[1]);
                  d.mobile = $("#searchForm input[name='mobile']").val();
                  d.status = $("#searchForm select[name='status'] option:selected").val();
                  d.alipayAccount = $("#searchForm input[name='alipayAccount']").val();
		            },
		        },
		         columns: [
		            { title: "手机号",data:'mobile' },
	                { title: "支付宝账号",data:'alipayAccount' },
	                { title: "提现金额",data:'cash' },
	                { title: "邀请奖励",data:'reward' },
	                { title: "支付状态",render:function(d,t,r){
		                switch(r.status){
                            case 1:return '未打款';
                            case 2:return '已打款';
                        }
		            }},
		            { title: "申请提现时间",render:function(d,t,r){
		                return getMyDate(r.createTime);
		            }},
		            { title: "打款时间",render:function(d,t,r){
		                if(r.status == 1) {
		                    return '';
		                }
                        return getMyDate(r.payTime);
                }},
		            {title:"操作",render:function(d,t,r){
                    var text = "";
                    text+='<div><a href="javascript:void(0)" onclick="showDrawOrderList(\''+r.id+'\')" >查看</a> | ';
                    text+='<div><a href="javascript:void(0)" onclick="deleteDrawOrder(\''+r.id+'\')" >删除</a> | ';
                    if (r.status == 1) {
                      text+='<a href="javascript:void(0)" onclick="confirmPayment(\''+r.id+'\')" >确认打款</a>';
                    }
                    return text+='</div>';
                }}
		        ]
			});
	 });
	  
	function search(){
		 dt.ajax.reload(null,true);
	}

     function showDrawOrderList(id){
    	 fangs.initDataTable('userOrderList',{
				ajax:{
                url:"/load/draworder/list",
                data:function(d){
                  d.id = id;
		            },
		        },
		         columns: [
		            { title: "编号",data:"id" },
		            { title: "订单号" ,data:"order_id"},
		            { title: "手机号",data:'mobile' },
		            { title: "创建时间",render:function(d,t,r){
		                return getMyDate(r.create_time);
		            }},
		            { title: "更新时间",render:function(d,t,r){
                       return getMyDate(r.update_time);
                    }},
		            { title: "商品名称",data:'product_info' },
		            { title: "商品价格",data:'price' },
		            { title: "返现比例",data:'rate' },
		            { title: "真实佣金",data:'commission1' },
		            { title: "客户佣金",data:'commission3' },
		            { title: "订单状态",render:function(d,t,r){
		                switch(r.status1){
                            case 1:return '未审核';
                            case 2:return '已审核';
                            case 3:return '无效订单';
                        }
		            }},
		            { title: "提现状态",render:function(d,t,r){
		                switch(r.status2){
                            case 1:return '未提现';
                            case 2:return '提现申请中';
                            case 3:return '已提现';
                        }
		            }},
		            { title: "是否已支付",render:function(d,t,r){
		                switch(r.status3){
                            case 1:return '未支付';
                            case 2:return '已支付';
                        }
		            }}
		        ]
			});

 			 $('#userOrderModal').modal('show');
 		}

     function deleteDrawOrder(id) {
         var url = "/delete/drwaorder";
         var msg = "是否删除？";
         fangs.confirm({
            text:msg,
            ok:function(){
                fangs.get(url,{id:id},function(data){
                    dt.ajax.reload(null,false);
                });
            }
         });
     }
    
     function confirmPayment(id) {
         var url = "/payment/confirm";
         var msg = "是否确认打款？";
         fangs.confirm({
            text:msg,
            ok:function(){
                fangs.get(url,{id:id},function(data){
                    dt.ajax.reload(null,false);
                });
            }
         });
     } 
	
    function getMyDate(str){
         var oDate = new Date(str),
         oYear = oDate.getFullYear(),
         oMonth = oDate.getMonth()+1,
         oDay = oDate.getDate(),
         oHour = oDate.getHours(),
         oMin = oDate.getMinutes(),
         oSen = oDate.getSeconds(),
         oTime = oYear +'-'+ getzf(oMonth) +'-'+ getzf(oDay) +' '+ getzf(oHour) +':'+ getzf(oMin) +':'+getzf(oSen);//最后拼接时间
         return oTime;
     };

       //补0操作
       function getzf(num){
           if(parseInt(num) < 10){
               num = '0'+num;
           }
           return num;
       }
</script>
</body>
</html>