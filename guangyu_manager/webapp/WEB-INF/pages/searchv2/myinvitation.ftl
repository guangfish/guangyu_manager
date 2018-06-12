<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/answer/answer.css">

    <header class="mui-bar mui-bar-nav">
		<button class="mui-action-back mui-btn mui-btn-blue mui-btn-link mui-btn-nav mui-pull-left"><span class="mui-icon mui-icon-left-nav"></span>我的邀请</button>
	</header>
	<div class="mui-content mui-answer">
		<div class="mui-main-answer">
			<div class="mui-scroll-wrapper ans_mt">
				<div class="mui-scoll">
					<div class="main-answer-cont">
					  <#if (invitationList?exists && invitationList?size > 0)>
			          <#list invitationList as invitation>
						<div class="main-answer-item">
							<p>${invitation.beInviterMobile?if_exists}<span>${(invitation.createTime)?string('yyyy-MM-dd')}</i></span></p>
							<p><#if (invitation.status==1)>未激活<#else>已激活，奖励金额${invitation.money?if_exists}，<#if (invitation.reward==1)>未领取奖励<#else>已领取奖励</#if></#if></p>
						</div>	
					  </#list>
			          </#if>					
					</div>
				</div>
			</div>			
		</div>
	</div>
	
<@model.webendsearchv2 />
