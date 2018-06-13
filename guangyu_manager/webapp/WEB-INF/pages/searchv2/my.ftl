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
						<p>${user.mobile?if_exists}</p>
					</div>
				</div>
				
				<div class="self-center">
					<ul class="mui-table-view">
						<li class="mui-table-view-cell">
							<a class="mui-navigate-right" href="/v2/myinvitation">
								<i class="ic_mycollect"></i>我的邀请
							</a>
						</li>					
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
	<script>
	  function logout() {
	    $.cookie('mobile', '', { expires: -1, path: '/'}); 
	    location.href="/v2/search";
	  }
	</script>
<@model.webendsearchv2 />
