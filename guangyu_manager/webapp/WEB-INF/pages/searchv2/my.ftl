<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/self_index/self_index.css">

<header class="mui-bar mui-bar-nav mui-setting-tit">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h1 class="mui-title">个人中心</h1>
		<a class=" mui-infor mui-pull-right"><span class="infor-num">0</span></a>

	</header>
	<div class="mui-scroll-wrapper">
		<div class="mui-scroll">
			<div class="mui-content self-content-box">
				<div class="mui-self-top">
					<a class="mui-head-img"><img src="/static/frontv2/img/icon_head.png" alt=""></a>
					<div class="mui-self-infor">
					    <h2><#if (user.accountType == 1)>逛鱼会员<#else>超级会员</#if></h2>
						<p>${user.mobile?if_exists}</p>
					</div>
				</div>
				
				<div class="self-center">
				    <ul class="mui-table-view">						
						<li class="mui-table-view-cell">
							<a class="" href="javascript:void(0);">
								<i class="ic_compare"></i>累计购物已省<font color="red">￥${cash?if_exists}</font>
							</a>
						</li>
						<#if (user.accountType == 2)>
						<li class="mui-table-view-cell">
							<a class="" href="javascript:void(0);" id='copy' onclick="copyInviteCode()">
							    <input type="hidden" name="myInviteCode" id="myInviteCode" value="${user.myInviteCode?if_exists}"/>
								<i class="ic_mybrowse"></i>我的邀请码
							</a>
						</li>
						</#if>
					</ul>
					<ul class="mui-table-view">
						<li class="mui-table-view-cell">
							<a class="mui-navigate-right" href="/v2/myinvitation">
								<i class="ic_mycollect"></i><#if (user.accountType == 1)>我的邀请<#else>我的会员</#if>
							</a>
						</li>
						<#if (user.accountType == 2)>
						<li class="mui-table-view-cell">
							<a class="mui-navigate-right" href="/v2/agencyreward">
								<i class="ic_mycollect"></i>订单奖励
							</a>
						</li>
						</#if>
					</ul>	
								
					<ul class="mui-table-view">
						<li class="mui-table-view-cell">
							<div class="mui-login"><a href="javascript:void(0);" onclick="logout()">退出登录</a></div>
						</li>
					</ul>

				</div>
			</div>
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
		<a class="mui-tab-item" href="/v2/help">
			<span class="mui-icon mui-icon-help1"></span>
			<span class="mui-tab-label">帮助</span>
		</a>
		<a class="mui-tab-item  mui-active" href="/v2/my">
			<span class="mui-icon mui-icon-self"></span>
			<span class="mui-tab-label">我的</span>
		</a>
	</nav>
	<script type='text/javascript' src='/static/front/js/clipboard.min.js' charset='utf-8'></script>
	
	<script>
	  function logout() {
	    $.cookie('mobile', '', { expires: -1, path: '/'}); 
	    location.href="/v2/search";
	  }
	  
	  function copyInviteCode() {
	    var value = $('#myInviteCode').val();
		  $('#copy').attr('data-clipboard-text', '邀请您加入逛鱼搜索，搜索淘宝、京东优惠券，拿返利！先领券，再购物，更划算！\r-------------\r访问链接：https://www.guangfish.com\r-------------\r邀请码【'+value+'】');
		  var clipboard = new Clipboard('#copy');
          clipboard.on('success', function (e) {
            Core.Dialog.msg('我的邀请码复制成功，赶紧去微信粘贴邀请好友吧！',9000);
            $('#copy').removeAttr('data-clipboard-text');
          });
          clipboard.on('error', function (e) {
            Core.Dialog.msg('复制失败');
          });
	  }
	</script>
<@model.webendsearchv2 />
