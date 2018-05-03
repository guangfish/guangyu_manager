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
      <div class="row pre-scrollable">
      
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
	                <label>时间:</label>
	                <div class="input-group">
	                  <div class="input-group-addon">
	                    <i class="fa fa-clock-o"></i>
	                  </div>
	                  <input type="text" class="form-control pull-right" id="reservationtime" name="dateTime">
	                </div>
                   </div>
                 </div>
                 
                <div class="col-md-2">
                <label for="type">订单状态：</label>
                 <select class="form-control select2" id="orderStatus" name="orderStatus">
                    <option selected="selected" value="">请选择</option>
					<option value="1" textVal="未审核">未审核</option>
					<option value="2" textVal="已审核">已审核</option>
					<option value="3" textVal="无效订单">无效订单</option>
                 </select>
                </div>
                
                <div class="col-md-2">
                 <label for="type">提现状态：</label>
                 <select class="form-control select2" id="putForwardStatus" name="putForwardStatus">
                    <option selected="selected" value="">请选择</option>
                    <option value="1" textVal="未提现">未提现</option>
					<option value="2" textVal="提现申请中">提现申请中</option>
					<option value="3" textVal="已提现">已提现</option>
                 </select>
                </div>
                
                <div class="col-md-2">
                <label for="type">支付状态：</label>
                 <select class="form-control select2" id="paymentStatus " name="paymentStatus">
                   <option selected="selected" value="">请选择</option>
                   <option value="1"  textVal="未支付">未支付</option>
				   <option value="2"  textVal="已支付">已支付</option>
                 </select>
                </div>
                </div>
                
                <div class="col-md-10" >
                  <button type="submit" class="btn btn-info pull-right">查询</button>
                </div>
            </form>
            <hr>
            <br> <br>
              <div class="table-responsive">
               <table class="table table-striped table-bordered table-hover table-condensed" id="orderList">
               </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    <!-- /.content -->
  </div>
  <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">管理员</h4>
      </div>
      <div class="modal-body">
         <form id="adminForm">
         <div id="msg" class="alert alert-danger" role="alert" style="display:none"></div>
          <input type="hidden" id="id" name="id">
          
         <div class="row">
              <div class="form-group col-md-6">
                 <label for="type">订单状态：</label>
                 <div class="radio">
                    <label>
                      <input type="radio" name="orderStatus" value="1">未审核
                    </label>
                    <label>
                      <input type="radio" name="orderStatus" value="2">已审核
                    </label>
                     <label>
                      <input type="radio" name="orderStatus" value="3">无效订单
                    </label>
                 </div>
             </div>
         </div>

         <div class="row">
              <div class="form-group col-md-6">
                 <label for="type">提现状态：</label>
                 <div class="radio">
                   <label>
                      <input type="radio" name="putForwardStatus" value="1">未提现
                    </label>
                    <label>
                      <input type="radio" name="putForwardStatus" value="2">提现申请中
                    </label>
                     <label>
                      <input type="radio" name="putForwardStatus" value="3">已提现
                    </label>
                 </div>
             </div>
         </div>

         <div class="row">
              <div class="form-group col-md-6">
                 <label for="type">支付状态：</label>
                 <div class="radio">
                    <label>
                      <input type="radio" name="paymentStatus" value="1">未支付
                    </label>
                    <label>
                      <input type="radio" name="paymentStatus" value="2">已支付
                    </label>
                 </div>
             </div>
         </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" onclick="save()">保存</button>
      </div>
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


$('#reservationtime').daterangepicker({
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
$('#reservationtime').val("");
</script>
<!-- --------------------------------时间范围选择器 end---------------------------------------------- -->

    <script type="text/javascript">
    //定义查询变量
	var searchData = new Object();
	searchData.pageNum = 1;
	//定义分页相关数据
	var pageUrl ='/load/order/list';
    
    $("#searchForm").submit(function(){
		search();
		return false;
	});
     var rowData=null;

     var dt=null;
	 $(function() {
		 dt = fangs.initDataTable('orderList',{
				ajax:{
		               url:pageUrl,
		               data:function(d){
						    d.startDate = $.trim($("#reservationtime").val().split('-')[0]);
						    d.endDate = $.trim($("#reservationtime").val().split('-')[1]);
							d.mobile = $("#searchForm input[name='mobile']").val();
							d.putForwardStatus = $("#searchForm select[name='putForwardStatus'] option:selected").val();
							d.paymentStatus = $("#searchForm select[name='paymentStatus'] option:selected").val();
							d.orderStatus = $("#searchForm select[name='orderStatus'] option:selected").val();
                            console.log(d.paymentStatus);
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
		            { title: "商品名称",render:function(d,t,r){
                  return '<div title="' +r.product_info+ '" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;width:200px;"> ' +r.product_info+ ' </div>';
                }},
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
		            }},
		            {title:"操作",render:function(d,t,r){
                        var text = "";
                        text+='<div><a href="javascript:void(0)" onclick="showUpdateModal(\''+r.id+'\')" >修改</a> | ';
                        text+='<a href="javascript:void(0)" onclick="deleteOrder(\''+r.id+'\')" >删除</a>';
                     return text+='</div>';
                    }}
		        ]
			});
	   
	 });
	  
	function search(){
		 dt.ajax.reload(null,true);
	}

     function showUpdateModal(id){
    	 $('#msg').hide();
    	  var data = dt.data();
          for(var i = 0;i!=data.length;i++){
            if(data[i].id==id){
            rowData = data[i];
            break;
            }
          }
    	 if(rowData!=null){
          $("#adminForm input[name='id']").val(rowData.id);
          $("#adminForm input[name='orderStatus'][value=" +rowData.status1 +"]").attr("checked",true);
          $("#adminForm input[name='putForwardStatus'][value=" +rowData.status2 +"]").attr("checked",true);
          $("#adminForm input[name='paymentStatus'][value=" +rowData.status3 +"]").attr("checked",true);
          $('#myModal').modal('show');
 		  }
     }

     function save(){
        var id = $("#adminForm input[name='id']").val();
        var putForwardStatus = $("#adminForm input[name='putForwardStatus']:checked").val();
        var paymentStatus = $("#adminForm input[name='paymentStatus']:checked").val();
        var orderStatus = $("#adminForm input[name='orderStatus']:checked").val();
        $.ajax({
            url:"/order/status/upt",
            dataType:"json",
            type:"get",
            data:{
                "id":id,
                "putForwardStatus":putForwardStatus,
                "paymentStatus":paymentStatus,
                "orderStatus":orderStatus
            },
            success:function(data){
              $('#myModal').modal('hide');
              search();
            }
        });
     }
     
     function deleteOrder(id) {
         var url = "/delete/order";
         var type = "是否删除？";
         fangs.confirm({
            text:type,
            ok:function(){
                fangs.get(url,{id:id},function(){
                    dt.ajax.reload(null,true);
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