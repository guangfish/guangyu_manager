<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/login/login.css">

<div class=" mui-bar mui-bar-nav mui-search-box">
			<a href="/v2/search"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
			<h1 class="mui-title" style="top:3px">用户注册</h1>
		</div>
		<div class="main">
			<div class="bkfff"></div>
			<form>
			    <div>
					<input type="text" placeholder="请输入邀请码(非必填项)" name="invitecode" id="invitecode"/>
				</div>
				<div>
					<input type="text" placeholder="请输入手机号" name="phone" id="phone"/>
				</div>
				<div class="code" id="copyalipay">
					<input type="text" placeholder="请输入支付宝账号" name="alipay" id="alipay"/>
					<div>
						复制手机号
					</div>
				</div>
				<div class="code" id="copyweixin">
					<input type="text" placeholder="请输入微信账号" name="weixin" id="weixin"/>
					<div>
						复制手机号
					</div>
				</div>
				<!--
				<div class="imgCode">
					<input type="text" placeholder="请输入图形验证码" name="imgCode" id="imgcode"/>
					<div class="btnImg">
						<span class="imged">							
							<img src="/getCode?%27+(new%20Date()).getTime()" alt=""/>
						</span>
						<div>
							<b>看不清换一张</b>
						</div>
					</div>
				</div>
				-->
				<div class="code" id="codesend">
					<input type="text" placeholder="请输入手机验证码" name="code" id="code"/>
					<div>
						<a href="javascript:void(0);" onclick="send()" id="aaa">获取验证码</a>
					</div>
				</div>
				<!--
				<div>
					<input type="password" placeholder="请输入新密码" name="newpass" id="newpass"/>
				</div>
				<div>
					<input type="password" placeholder="请确认新密码"  name="newpassagain"/>
				</div>
				-->
				<!--
				<div class="login-lable agreement">
					<span><input type="checkbox" class="input_check norm-input" id="check3"><label for="check3"></label></span>
					<span>我已经看过并同意<a href="#">《网络服务条款》</a></span>
					<p class="state check-text"></p>
				</div>
				-->
			</form>
			<span class="btn btn-submit">注册</span>
			<div class="login_p">
				<a href="/v2/login?toUrl=${toUrl?if_exists}" class="btn">短信登录</a>
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
			
//			$('#codesend').on('click', 'div', function() {
//			    if(sendsmscode()==1){
//			      if(countdown == 120){					
//					invokeSettime(this)
//				  }	
//			    }else{				  	
//				}		
//			})
			
			$('#copyalipay').on('click', 'div', function() {
			    var mobile = $('#phone').val();
			    $('#alipay').val(mobile);	
			})
			$('#copyweixin').on('click', 'div', function() {
			    var mobile = $('#phone').val();
			    $('#weixin').val(mobile);		
			})
			
			$('.imgCode').on('click', '.btnImg', function() {
				$(this).find('.imged').html('<img src="/getCode?'+(new Date()).getTime()+'"/>');
			})
			$('.btn-submit').on('click',function(){
				var status=1;
				var _k=$('form').serializeArray();
				var _p={};
				for(var i in _k){
					_p[_k[i].name]=_k[i].value;
				}console.log(this,counter.rule('*',_p.phone))
				if(!counter.rule('*',_p.phone)||!counter.rule('*',_p.alipay)||!counter.rule('*',_p.weixin)||!counter.rule('*',_p.code)){
					mui.toast('请将信息填写完整');
					status=0;
				}else if(!counter.rule('phone',_p.phone)){
					mui.toast('请填写正确的手机号码');
					status=0;
				}
				if(status){
				    var invitecode = $('#invitecode').val();
					var mobile = $('#phone').val();
					var alipay = $('#alipay').val();
					var weixin = $('#weixin').val();
					//var imgcode = $('#imgcode').val();
					var code = $('#code').val();
					
					$
						.ajax({
							type : "post",
							url : "/v2/api/register",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
							    "inviteCode" : invitecode,
								"mobile" : mobile,
								"alipay" : alipay,
								"weixin" : weixin,
								"code" : code
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(data.response.status=="0"){
								   mui.toast(data.response.desc);
								   $.cookie('mobile', mobile, { expires: 365, path: '/',domain:'${cookieDomain?if_exists}'});
								   location.href="${toUrl?if_exists}";
								}else {
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
