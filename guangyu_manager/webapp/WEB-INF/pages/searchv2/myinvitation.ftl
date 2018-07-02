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
					  <#if (invitationList?exists && invitationList?size > 0)>
			  <div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption">新激活<font color="red">${activeFriend?if_exists}</font>个朋友，可提奖励金额<font color="red">￥${reward?if_exists}</font></div></div>
			<#else>
			  <div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption">去邀请朋友成为你的会员吧！<a href="/v2/invitation"><font color="red">点我邀请</font></a></div></div>
			</#if>				  
					  <#if (invitationList?exists && invitationList?size > 0)>
					  <div class="main-answer-item">
							<p>我的会员&nbsp;<font color="red">(激活状态)</font><span>邀请奖励&nbsp;<font color="red">(奖励状态)</font></i></span></p>						
					  </div>
			          <#list invitationList as invitation>
						<div class="main-answer-item">
							<p>${invitation.beInviterMobile?if_exists}&nbsp;<font color="red">(<#if (invitation.status==1)>未激活<#else>已激活</#if>)</font><span><#if (invitation.status==1)>￥${invitation.money?if_exists}&nbsp;<font color="red">(可领取)<#else>￥${invitation.money?if_exists}&nbsp;(<#if (invitation.reward==1)>未领取<#else>已领取</#if>)</#if></font></i></span></p>							
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
		<a class="mui-tab-item mui-active" href="<#if (reward>0)>/v2/rewarddraw<#else>javascript:void(0);</#if>">
			<span class="mui-icon mui-icon-draw"></span>
			<span class="mui-tab-label">提取邀请奖励</span>
		</a>
	</nav>
	
<@model.webendsearchv2 />
