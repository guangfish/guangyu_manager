<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/login/login.css">
<script type='text/javascript' src='/static/front/js/jquery.cookie.js' charset='utf-8'></script>

<div class=" mui-bar mui-bar-nav mui-search-box">
			<a href="index.html"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
			<h1 class="mui-title" style="top:3px">订单查询</h1>
		</div>
		<div class="main">
			<div class="bkfff"></div>
			<form>
				<div>
					<input type="text" placeholder="请输入您的手机号" name="phone" id="mobile"/>
				</div>				
			</form>
			<span class="btn btn-submit">订单查询</span>
			<div class="login_p">
				<a href="orderdrawv2">提现申请</a>
			</div>
		</div>
    <!-- 底部菜单栏 -->
	<nav class="mui-bar mui-bar-tab new-bar">
		<a class="mui-tab-item" href="searchv2">
			<span class="mui-icon mui-icon-index "></span>
			<span class="mui-tab-label">首页</span>
		</a>
		<a class="mui-tab-item" href="orderv2">
			<span class="mui-icon mui-icon-new"></span>
			<span class="mui-tab-label">订单</span>
		</a>
		<a class="mui-tab-item mui-active" href="searchorderv2">
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
                  //ajax
				}
			})
		</script>

<@model.webendsearchv2 />
