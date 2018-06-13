<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/login/login.css">

<div class=" mui-bar mui-bar-nav mui-search-box">
			<a href="/v2/search"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
			<h1 class="mui-title" style="top:3px">邀请好友</h1>
		</div>
		<div class="main">
			<div class="bkfff"></div>
			<form>
				<div>
					<input type="text" placeholder="请输入好友手机号" name="phone" id="phone"/>
				</div>							
			</form>
			<span class="btn btn-submit">提交</span>
			<div class="login_p">
				每邀请一个朋友并通过逛鱼搜索完成一单购买任务后即可得到<font color="red">${reward?if_exists}</font>元奖励
			</div>
		</div>
		<!-- 底部菜单栏 -->
		<nav class="mui-bar mui-bar-tab new-bar">
		<a class="mui-tab-item" href="/v2/search">
			<span class="mui-icon mui-icon-index "></span>
			<span class="mui-tab-label">首页</span>
		</a>
		<a class="mui-tab-item" href="/v2/order">
			<span class="mui-icon mui-icon-order"></span>
			<span class="mui-tab-label">订单</span>
		</a>
		<a class="mui-tab-item" href="/v2/searchorder">
			<span class="mui-icon mui-icon-draw"></span>
			<span class="mui-tab-label">提现</span>
		</a>
		<a class="mui-tab-item" href="javascript:void(0);">
			<span class="mui-icon mui-icon-help1"></span>
			<span class="mui-tab-label">帮助</span>
		</a>
		<a class="mui-tab-item" href="/v2/my">
			<span class="mui-icon mui-icon-self"></span>
			<span class="mui-tab-label">我的</span>
		</a>
	    </nav>
	    
		<script>
			$('.btn-submit').on('click',function(){
				var status=1;
				var _k=$('form').serializeArray();
				var _p={};
				for(var i in _k){
					_p[_k[i].name]=_k[i].value;
				}console.log(this,counter.rule('*',_p.phone))
				if(!counter.rule('*',_p.phone)){
					mui.toast('请将信息填写完整');
					status=0;
				}else if(!counter.rule('phone',_p.phone)){
					mui.toast('请填写正确的手机号码');
					status=0;
				}
				if(status){
				    var mobile_me = $.cookie('mobile');
				    var mobile_friend = $('#phone').val();

					$
						.ajax({
							type : "post",
							url : "/api/saveinvitation",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
								"mobileme" : ""+mobile_me,
								"mobilefriend" : ""+mobile_friend
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(JSON.stringify(data) != "{}"){								  		  
                                   if(data.ret.result == "3"){
                                     Core.Dialog.msg("邀请的用户已经在使用逛鱼搜索或已被邀请或已邀请过别人",5000);
                                   }                               
                                   if(data.ret.result == "0"){
                                     Core.Dialog.msg("邀请好友成功，请细心帮助好友使用逛鱼搜索，当好友通过逛鱼搜索完成购买任务时，您才可以得到奖励哦！",6000);
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
