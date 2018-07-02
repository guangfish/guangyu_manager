<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/event_index/event_index.css">
<!-- 头部 -->
	<div class=" mui-bar mui-bar-nav mui-search-box">
		<a href="/v2/search"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
	</div>
	<!-- 主要内容 -->
	<div class="mui-content mui-event-box">
		<div class="mui-main-event">
			<div class="mui-event-title tab_links">
				<a style="text-decoration: none;" data-id="#allEvent" class="mui-event-tap active">全部活动</a>
				<a style="text-decoration: none;" data-id="#notStarted" class="mui-event-tap">即将开始</a>
				<a style="text-decoration: none;" data-id="#started" class="mui-event-tap">已开始</a>
				<a style="text-decoration: none;" data-id="#finished" class="mui-event-tap">已结束</a>
			</div>
			<div id="pullrefresh" class="mui-scroll-wrapper scroll-refresh">
				<div class="mui-scroll">
					<div id="allEvent" class="mui-event-cont">
					    <#if (campaignAllList?exists && campaignAllList?size > 0)>
					    <#list campaignAllList as banner>
						<div class="mui-cont-box">
							<a style="text-decoration: none;" href="${banner.link?if_exists}" <#if (banner.target==2)>target="_blank"</#if>>
								<div class="eventImg"><img class="eventImg" src="${banner.imgUrl?if_exists}" alt=""></div>	
								<div class="mui-cont-tit ">
									<h2>${banner.title?if_exists}</h2>
									<span>${banner.desc?if_exists}</span>
								</div>
								<div class="mui-cont-text limit_text">
									有效时间：${banner.fromTime?string('yyyy-MM-dd')}至${banner.toTime?string('yyyy-MM-dd')}
								</div>						
							</a>
						</div>	
						</#list>
			            </#if>																	
					</div>
					<div id="notStarted" class="mui-event-cont disn">
						<#if (campaignBeginInAMinuteList?exists && campaignBeginInAMinuteList?size > 0)>
					    <#list campaignBeginInAMinuteList as banner>
						<div class="mui-cont-box">
							<a style="text-decoration: none;" href="${banner.link?if_exists}" <#if (banner.target==2)>target="_blank"</#if>>
								<div class="eventImg"><img class="eventImg" src="${banner.imgUrl?if_exists}" alt=""></div>	
								<div class="mui-cont-tit ">
									<h2>${banner.title?if_exists}</h2>
									<span>${banner.desc?if_exists}</span>
								</div>	
								<div class="mui-cont-text limit_text">
									有效时间：${banner.fromTime?string('yyyy-MM-dd')}至${banner.toTime?string('yyyy-MM-dd')}
								</div>					
							</a>
						</div>	
						</#list>
			            </#if>							
					</div>
					<div id="started" class="mui-event-cont disn">
						<#if (campaignAlreadyStartedList?exists && campaignAlreadyStartedList?size > 0)>
					    <#list campaignAlreadyStartedList as banner>
						<div class="mui-cont-box">
							<a style="text-decoration: none;" href="${banner.link?if_exists}" <#if (banner.target==2)>target="_blank"</#if>>
								<div class="eventImg"><img class="eventImg" src="${banner.imgUrl?if_exists}" alt=""></div>	
								<div class="mui-cont-tit ">
									<h2>${banner.title?if_exists}</h2>
									<span>${banner.desc?if_exists}</span>
								</div>	
								<div class="mui-cont-text limit_text">
									有效时间：${banner.fromTime?string('yyyy-MM-dd')}至${banner.toTime?string('yyyy-MM-dd')}
								</div>					
							</a>
						</div>	
						</#list>
			            </#if>																	
					</div>
					<div id="finished" class="mui-event-cont disn">
						<#if (campaignAlreadyStopedList?exists && campaignAlreadyStopedList?size > 0)>
					    <#list campaignAlreadyStopedList as banner>
						<div class="mui-cont-box">
							<a style="text-decoration: none;" href="${banner.link?if_exists}" <#if (banner.target==2)>target="_blank"</#if>>
								<div class="eventImg"><img class="eventImg" src="${banner.imgUrl?if_exists}" alt=""></div>	
								<div class="mui-cont-tit ">
									<h2>${banner.title?if_exists}</h2>
									<span>${banner.desc?if_exists}</span>
								</div>	
								<div class="mui-cont-text limit_text">
									有效时间：${banner.fromTime?string('yyyy-MM-dd')}至${banner.toTime?string('yyyy-MM-dd')}
								</div>					
							</a>
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
		<a style="text-decoration: none;" class="mui-tab-item mui-active" href="/v2/search">
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
	
<@model.webendsearchv2 />
