<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/answer/answer.css">

    <header class="mui-bar mui-bar-nav">
		<button class="mui-action-back mui-btn mui-btn-blue mui-btn-link mui-btn-nav mui-pull-left"><span class="mui-icon mui-icon-left-nav"></span>个人中心</button>
	</header>
	<div class="mui-content mui-answer">
		<div class="mui-main-answer">
			<div class="mui-scroll-wrapper ans_mt">
			<div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption">共<font color="red">${orderNum?if_exists}</font>个订单，可提奖励金额<font color="red">￥${reward?if_exists}</font></div></div>
				<div class="mui-scoll">
					<div class="main-answer-cont">
					    <div class="main-answer-item">
							<p>我的会员<span>奖励金额</i></span></p>						
						</div>
					  <#if (userOrderList?exists && userOrderList?size > 0)>
			          <#list userOrderList as userOrder>
						<div class="main-answer-item">
							<p><font color="red">${userOrder.mobile?if_exists}</font><span>￥${userOrder.commissionReward?if_exists}</i></span></p>						
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
		<a class="mui-tab-item mui-active" href="<#if (userOrderList?exists && userOrderList?size > 0)>/v2/agencyrewarddraw<#else>javascript:void(0);</#if>">
			<span class="mui-icon mui-icon-draw"></span>
			<span class="mui-tab-label">提取平台奖励</span>
		</a>
	</nav>
	
<@model.webendsearchv2 />
