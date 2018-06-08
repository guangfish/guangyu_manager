<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/login/login.css">
<script type='text/javascript' src='/static/front/js/jquery.cookie.js' charset='utf-8'></script>

<div class=" mui-bar mui-bar-nav mui-search-box">
			<a href="index.html"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
			<h1 class="mui-title" style="top:3px">提现申请</h1>
		</div>
		<div class="main">
			<div class="bkfff"></div>
			<form>
				<div>
					<input type="text" placeholder="请输入您的手机号" name="phone" id="mobile"/>
				</div>
				<div>
					<input type="text" placeholder="请输入您的支付宝账号" name="alipay" id="alipay"/>
				</div>		
				<div class="code">
					<input type="text" placeholder="请输入手机验证码" name="code"/>
					<div>
						获取验证码
					</div>
				</div>
			</form>
			<span class="btn btn-submit">提现</span>
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
		    var countdown = 60;
			var timer;
			function invokeSettime(obj) {
				clearTimeout(timer);
				settime(obj);
				function settime(obj) {
					if(countdown == 0) {
						$(obj).attr("disabled", false);
						$(obj).text("获取验证码");
						countdown = 60;
						return;
					} else {
						$(obj).attr("disabled", true);
						$(obj).text("(" + countdown + ") s 后重新发送");
						countdown--;
					}
					timer=setTimeout(function() {
						settime(obj)
					}, 1000)
				}
			}
			$('.code').on('click', 'div', function() {
				if(countdown == 60){					
					invokeSettime(this)
				}
			})
			
			$('.btn-submit').on('click',function(){
				var status=1;
				var _k=$('form').serializeArray();
				var _p={};
				for(var i in _k){
					_p[_k[i].name]=_k[i].value;
				}console.log(this,counter.rule('*',_p.phone))
				if(!counter.rule('*',_p.phone)||!counter.rule('*',_p.code)||!counter.rule('*',_p.alipay)){
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
