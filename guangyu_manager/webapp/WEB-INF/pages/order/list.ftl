<#assign webTitle="首页" in model>
<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<#assign webHead in model>
</#assign>
<@model.webhead />
    <!-- 头部 -->
    <@model.webMenu current="order" child="orderSearch" />
			<div class="m09876ain-container" style="height: auto;">
				zADSFGHY
				<!-- 筛选 -->
				<div class="ty-effect bor-box">
				<form id="searchForm">
					<div class="hd">
		                <div class="select-box plan-select-box">
							<label>手机号：</label>
							<input type="text" name="mobile"/>
						</div>
		
					    <div class="select-box plan-select-box">
							<label>开始时间：</label>
							<input type="text" name="datepicker" id="startdate"/>
						</div>
						<div class="select-box plan-select-box">
							<label>结束时间：</label>
							<input type="text" name="datepicker" id="enddate"/>
						</div>
						<div class="select-box plan-select-box">
							<label>订单状态：</label>
							<select class="form-control" style="height:37px;width:95px;" name="orderStatus">
								<option value="">全部</option>
								<option value="1">未审核</option>
								<option value="2">已审核</option>
								<option value="3">无效订单</option>
							</select>
						</div>
						
						<div class="select-box plan-select-box">
							<label>提现状态：</label>
							<select class="form-control" style="height:37px;width:120px;" name="putForwardStatus">
								<option value="">全部</option>
								<option value="1">未提现</option>
								<option value="2">提现申请中</option>
								<option value="3">已提现</option>
							</select>
						</div>
						
						<div class="select-box plan-select-box">
							<label>支付状态：</label>
							<select class="form-control" style="height:37px;width:95px;" name="paymentStatus">
								<option value="">全部</option>
								<option value="1">未支付</option>
								<option value="2">已支付</option>
							</select>
						</div>

						<a href="javascript:void(0);" class="btn btn-success" id="queryBtn">查询</a>
                    </form>
					</div>
					
					<!-- 数据报表 -->
					<div class="data-report">
						<div class="bd">
							<table width="100%" cellpadding="0" cellspacing="0" border="0" class="setsorttable" id="plan">
								<thead>
									<tr>
										<th style="width:30px">编号</th>
										<th >订单号</th>
										<th >手机号</th>
										<th >创建时间</th>
										<th >更新时间</th>
										<th >商品名称</th>
										<th >商品价格</th>
										<th >返现比例</th>
										<th >真实佣金</th>
										<th >客户佣金</th>
										<th >订单状态</th>
										<th >提现状态</th>
										<th >是否已支付</th>
										<th >操作</th>
									</tr>
								</thead>
								<tbody>
								<#list bizObj.list as order>
								<tr>
								    <td>${order.id?if_exists}</td>
								    <td>${order.order_id?if_exists}</td>
								    <td>${order.mobile?if_exists}</td>
								    <td>${order.create_time?datetime}</td>
								    <td>${order.update_time?datetime}</td>
								    <td>${order.product_info?if_exists}</td>
								    <td>${order.price?if_exists}</td>
								    <td>${order.rate?if_exists}</td>
								    <td>${order.commission1?if_exists}</td>
								    <td>${order.commission3?if_exists}</td>
								    <td>${order.status1?if_exists}</td>
								    <td>${order.status2?if_exists}</td>
								    <td>${order.status3?if_exists}</td>
								    <td>
								      <a href="#">编辑</a> | 
								      <a href="#">删除</a>
								    </td>
								 </tr>
								</#list>
								</tbody>
								<@model.showPage url=vm.getUrlByRemoveKey(thisUrl, ["start", "size"]) p=bizObj.page parEnd="" colsnum=14 />
								<!-- 翻页 -->
							</table>
						</div>
					</div>
				</div>
				<!--resolve E-->
				<div class="hold-bottom" style="height:30px"></div>
			</div>
		</div>
	</div>

	<style type="text/css">
		.data-report .bd tbody tr td{ border-right: 1px solid #ddd;}
		.unit-tab{ border:1px solid #bbb; margin-top: -5px;}
		.data-report .bd .unit-tab thead th{ background-color: #fafafa; }
		.data-report .bd .unit-tab tr td:last-child{ border-right: 0;}
		.data-report .bd .unit-tab tfoot td{ padding: 3px 10px;}
		.data-report .bd .unit-tab tfoot td .btn{ color:#fff;padding: 1px 5px;float:left; font-size: 12px;}
		.data-report .bd .unit-tab tfoot td .page a{ border:0; padding:0 5px; background: none;}
	</style>
    
    <script type ="text/javascript" src="/static/jquery/jquery.js"></script>
    <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
    <script type ="text/javascript" src="/static/jquery/jquery.datetimepicker.js"></script>
	
	<script type="text/javascript">
	
    $(function(){
        $("input[name='datepicker']").datetimepicker({
            lang:'ch'
        
        });
        
            // 查询
			$("#queryBtn").on("click", function() {
			    var strParam = "";
				var startDate = $("#startdate").val();
				var endDate = $("#enddate").val();
				var mobile = $("input[name='mobile']").val();
				var putForwardStatus = $("select[name='putForwardStatus'] option:selected").val();
				var paymentStatus = $("select[name='paymentStatus'] option:selected").val();
				var orderStatus = $("select[name='orderStatus'] option:selected").val();
				if (startDate != "") {
					if (strParam != "") {
						strParam = strParam + "&startDate=" + startDate;
					} else {
						strParam = strParam + "?startDate=" + startDate;
					}
				}
				if (endDate != "") {
					if (strParam != "") {
						strParam = strParam + "&endDate=" + endDate;
					} else {
						strParam = strParam + "?endDate=" + endDate;
					}
				}
				if (mobile != "") {
					if (strParam != "") {
						strParam = strParam + "&mobile=" + mobile;
					} else {
						strParam = strParam + "?mobile=" + mobile;
					}
				}
				if (putForwardStatus != "") {
					if (strParam != "") {
						strParam = strParam + "&putForwardStatus=" + putForwardStatus;
					} else {
						strParam = strParam + "?putForwardStatus=" + putForwardStatus;
					}
				}
				if (paymentStatus != "") {
					if (strParam != "") {
						strParam = strParam + "&paymentStatus=" + paymentStatus;
					} else {
						strParam = strParam + "?paymentStatus=" + paymentStatus;
					}
				}
				if (orderStatus != "") {
					if (strParam != "") {
						strParam = strParam + "&orderStatus=" + orderStatus;
					} else {
						strParam = strParam + "?orderStatus=" + orderStatus;
					}
				}	
				
				console.log(startDate,endDate, putForwardStatus, paymentStatus, orderStatus);
				window.location.href = "/order/list"+strParam;
			});
        
        
        
        
    });

	</script>
<@model.webend />