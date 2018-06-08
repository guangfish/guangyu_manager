<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/login/login.css">
<script type='text/javascript' src='/static/front/js/jquery.cookie.js' charset='utf-8'></script>

<div class=" mui-bar mui-bar-nav mui-search-box">
			<a href="index.html"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
			<h1 class="mui-title" style="top:3px">录入订单号</h1>
		</div>
		<div class="main">
			<div class="bkfff"></div>
			<form>
				<div>
					<input type="text" placeholder="请输入您的手机号" name="phone" id="mobile"/>
				</div>				
				<div class="code">
					<input type="text" placeholder="请录入订单号" name="code" id="orderid"/>
				</div>
			</form>
			<span class="btn btn-submit">提交</span>
			<div class="login_p">
				<a href="javascript:void(0);" style="color:red;">刚录入的订单号，最快1分钟、最迟次日可查</a>
			</div>
		</div>
		<!-- 底部菜单栏 -->
	<nav class="mui-bar mui-bar-tab new-bar">
		<a class="mui-tab-item" href="searchv2">
			<span class="mui-icon mui-icon-index "></span>
			<span class="mui-tab-label">首页</span>
		</a>
		<a class="mui-tab-item mui-active" href="orderv2">
			<span class="mui-icon mui-icon-new"></span>
			<span class="mui-tab-label">订单</span>
		</a>
		<a class="mui-tab-item" href="searchorderv2">
			<span class="mui-icon mui-icon-old"></span>
			<span class="mui-tab-label">提现</span>
		</a>
	</nav>
	
		<script>
	  	  var mobile = $.cookie('guangfishmobile');
	  	  if(mobile){
	  	    $("#mobile").val(mobile);
	  	  }
	    </script>
		
		<script>
			$('.btn-submit').on('click',function(){
				var status=1;
				var _k=$('form').serializeArray();
				var _p={};
				for(var i in _k){
					_p[_k[i].name]=_k[i].value;
				}console.log(this,counter.rule('*',_p.phone))
				if(!counter.rule('*',_p.phone)||!counter.rule('*',_p.code)){
					mui.toast('请将信息填写完整');
					status=0;
				}else if(!counter.rule('phone',_p.phone)){
					mui.toast('请填写正确的手机号码');
					status=0;
				}
				if(status){
				  var orderid = $('#orderid').val();
	              var mobile = $('#mobile').val();
					$
						.ajax({
							type : "post",
							url : "/api/ordersave",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
								"orderid" : ""+orderid,
								"mobile" : ""+mobile,
								"vcode" : ""
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(JSON.stringify(data) != "{}"){
								  if(data.ret.result=="0"){
								    mui.toast("订单号提交成功，请收货后，去淘宝/京东【确认收货】后方可提现");								    
								    var orderSubmitNotice = $.cookie('ordersubmitnotice');
								    if(!orderSubmitNotice){
								      mui.toast("订单号提交成功，刚录入的订单号，最快1分钟、最迟次日可查");
								      //$.cookie('ordersubmitnotice', 'ordersubmitnotice', { expires: 7, path: '/',domain:'${cookieDomain?if_exists}'});
								    }
								    $("#orderid").val("");							    
								  }
								  
								  if(data.ret.result=="-1"){
								    mui.toast("该订单号已存在，请勿重复提交!");
								    $("#orderid").val("");
								  }								  
								}						
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
				}
			})
		</script>

<@model.webendsearchv2 />
