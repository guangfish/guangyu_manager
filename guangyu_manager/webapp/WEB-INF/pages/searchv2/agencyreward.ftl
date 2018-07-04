<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/answer/answer.css">

    <header class="mui-bar mui-bar-nav">
		<button class="mui-action-back mui-btn mui-btn-blue mui-btn-link mui-btn-nav mui-pull-left"><span class="mui-icon mui-icon-left-nav"></span>个人中心</button>
	</header>
	<div class="mui-content mui-answer">
		<div class="mui-main-answer">
			<div class="mui-scroll-wrapper ans_mt">
			
				<div class="mui-scoll">
					<div class="main-answer-cont">	
					<#if (userOrderList?exists && userOrderList?size > 0)>
			  <div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption">共<font color="red">${orderNum?if_exists}</font>个订单，可提奖励金额<font color="red">￥${reward?if_exists}</font></div></div>
			<#else>
			  <div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption">还没邀请会员或我的会员还没有订单！</div></div>
			</#if>				    
					  <#if (userOrderList?exists && userOrderList?size > 0)>
					    <div class="main-answer-item">
							<p>会员订单返现<span>平台奖励&nbsp;<font color="red">(20%至100%)</font></i></span></p>						
						</div>
			          <#list userOrderList as userOrder>
						<div class="main-answer-item">
							<p>${userOrder.mobile?if_exists}&nbsp;<font color="red">(￥${userOrder.commission3?if_exists})</font><span>￥${userOrder.commissionReward?if_exists}&nbsp;<font color="red">(${userOrder.commissionRewardRate?if_exists}%)</font></i></span></p>						
						</div>	
					  </#list>
			          </#if>					
					</div>
				</div>
			</div>			
		</div>
	</div>
	<!-- 底部菜单栏 -->
	<nav class="mui-bar mui-bar-tab new-bar">
		<a style="text-decoration: none;" class="mui-tab-item mui-active" href="<#if (userOrderList?exists && userOrderList?size > 0)>/v2/agencyrewarddraw<#else>javascript:void(0);</#if>">
			<span class="mui-icon mui-icon-draw"></span>
			<span class="mui-tab-label">提取平台奖励</span>
		</a>
	</nav>
	
<@model.webendsearchv2 />
