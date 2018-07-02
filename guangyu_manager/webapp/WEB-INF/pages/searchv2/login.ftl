<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/login/login.css">

<div class=" mui-bar mui-bar-nav mui-search-box">
			<a href="/v2/search"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
			<h1 class="mui-title" style="top:3px">手机登录</h1>
		</div>
		<div class="main">
			<div class="bkfff"></div>
			<form>
				<div>
					<input type="text" placeholder="请输入手机号" name="phone" id="phone"/>
				</div>				
				<div class="code">
					<input type="text" placeholder="请输入手机验证码" name="code" id="code"/>
					<div>
						<a href="javascript:void(0);" onclick="send()" id="aaa">获取验证码</a>
					</div>
				</div>

			</form>
			<span class="btn btn-submit">登录</span>
			<div class="login_p">
				<a href="/v2/login?toUrl=${toUrl?if_exists}" class="btn tel_login">短信登录</a>
				<a href="/v2/register?toUrl=${toUrl?if_exists}" class="btn">轻松注册</a>
			</div>
		</div>
		<!-- 底部菜单栏 -->
		<nav class="mui-bar mui-bar-tab new-bar">
		<a style="text-decoration: none;" class="mui-tab-item" href="/v2/search">
			<span class="mui-icon mui-icon-index "></span>
			<span class="mui-tab-label">首页</span>
		</a>
		<a style="text-decoration: none;" class="mui-tab-item" href="/v2/order">
			<span class="mui-icon mui-icon-order"></span>
			<span class="mui-tab-label">订单</span>
		</a>
		<a style="text-decoration: none;" class="mui-tab-item" href="/v2/searchorder">
			<span class="mui-icon mui-icon-draw"></span>
			<span class="mui-tab-label">提现</span>
		</a>
		<a style="text-decoration: none;" class="mui-tab-item" href="/v2/help">
			<span class="mui-icon mui-icon-help1"></span>
			<span class="mui-tab-label">帮助</span>
		</a>
		<a style="text-decoration: none;" class="mui-tab-item" href="/v2/my">
			<span class="mui-icon mui-icon-self"></span>
			<span class="mui-tab-label">我的</span>
		</a>
	    </nav>
	    
		<script>
			var countdown = 120;
			var timer;
			function invokeSettime(obj) {
				clearTimeout(timer);
				settime(obj);
				function settime(obj) {
					if(countdown == 0) {
						$(obj).attr("disabled", false);
						$(obj).text("获取验证码");
						countdown = 120;
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
			function send() {
			  if(sendsmscode()==1){
			      if(countdown == 120){					
					invokeSettime($('#aaa'))
				  }	
			    }else{				  	
				}	
			}
			
//			$('.code').on('click', 'div', function() {
//				if(sendsmscode()==1){
//			      if(countdown == 120){					
//					invokeSettime(this)
//				  }	
//			    }else{				  	
//				}	
//			})

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
				    var mobile = $('#phone').val();
					var code = $('#code').val();

					$
						.ajax({
							type : "post",
							url : "/v2/api/login",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
								"mobile" : mobile,
								"code" : code
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(data.response.status=="0"){
								   mui.toast(data.response.desc);
								   $.cookie('mobile', mobile, { expires: 365, path: '/',domain:'${cookieDomain?if_exists}'});
								   location.href="${toUrl?if_exists}";
								}else{
								   mui.toast(data.response.desc);
								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
				}
			})
			
			function sendsmscode() {
			  var ret=0;
			  var mobile = $('#phone').val();
			  if (!mobile) {
				mui.toast('请填写正确的手机号码');				
			  }
			
				$
						.ajax({
							type : "post",
							url : "/api/getSmsCode",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							async:false,
							data : JSON.stringify({
								"mobile" : mobile
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data);
								if(data.ret.result.status=="4"){
								    mui.toast('请等待2分钟后再次发送短信验证码');			
								    ret=0;		    
								}
								if(data.ret.result.status=="0"){	
								  ret=1;							  	    
								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
			return ret;
		}
		</script>

<@model.webendsearchv2 />
