<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/self_index/self_index.css">

<header class="mui-bar mui-bar-nav mui-setting-tit">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h1 class="mui-title">我的订单</h1>

	</header>
	<div class="mui-content self-content-box">
		<div class="mui-main-common">
			<div class="mui-common-tit tab_links">
				<a data-id="#mnewcar" class="mui-event-tap active">可提现</a>
				<a data-id="#moldcar" class="mui-event-tap">不可提现</a>
			</div>
			<div id="pullrefresh" class="mui-scroll-wrapper scroll-refresh">			    
				<div class="mui-scroll">
					<div id="mnewcar" class="mui-common-cont">
						<!-- 热销新车 -->
						<div class="mui-cont-box ">
							<div class="mui-new-list">
							  <br/><div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption"><font color="red">${canDrawOrderNum?if_exists}</font>条可提现，可提现金额<font color="red">￥${tCommission?if_exists}</font>，共邀请<font color="red">${friendNum?if_exists}</font>个好友，尚有激活邀请<font color="red">${friendNumValid?if_exists}</font>个，未激活邀请<font color="red">${friendNumNoValid?if_exists}</font>个，可提现奖励<font color="red">￥${reward?if_exists}</font>，未达提现要求奖励<font color="red">￥${rewardAll?if_exists}</font></div></div><br/><br/>
								<ul class="mui-table-view ">	
								  <#if (userOrderCanDrawList?exists && userOrderCanDrawList?size > 0)>	
								  <#list userOrderCanDrawList as userOrder>																																											
									<li class="mui-table-view-cell mui-media pos">
										<a href="new_detail.html">
											<img class="mui-media-object mui-pull-left" src="${userOrder.productImgUrl?if_exists}">
											<div class="mui-media-body">
												<h2 class="mui-body-tit">${userOrder.productInfo?if_exists}</h2>
												<p>订单状态:&nbsp;&nbsp;&nbsp;<span class="mui-bodycolor">${userOrder.orderStatus?if_exists}</span></p>
												<p>订单时间:&nbsp;&nbsp;&nbsp;<span class="mui-bodycolor">${userOrder.createTime?string('yyyy-MM-dd')}</span></p>
												<div>
													<span>返现:&nbsp;&nbsp;&nbsp;<em class="mui-first-payment">￥${userOrder.commission3?if_exists}</em></span>
												</div>
												<p class="mui-buy-gift">购买该商品可额外获得${userOrder.fanliMultiple?if_exists}倍返现奖励</p>
											</div>											
										</a>
									</li>
								  </#list>	
								  </#if>								  	
								</ul>
							</div>
						</div>
					</div>
					<div id="moldcar" class="mui-common-cont disn">
						<div class="mui-cont-box">
							<div class="mui-new-list">
							  <br/><div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption"><font color="red">${uncanDrawOrderNum?if_exists}</font>条暂不可提现，预估暂不可提现金额<font color="red">￥${tUCommission?if_exists}</font></div></div><br/>
								<ul class="mui-table-view">		
								  <#if (userOrderNotCanDrawList?exists && userOrderNotCanDrawList?size > 0)>	
								  <#list userOrderNotCanDrawList as userOrder>																										
									<li class="mui-table-view-cell mui-media pos">
										<a href="new_detail.html">
											<img class="mui-media-object mui-pull-left" src="${userOrder.productImgUrl?if_exists}">
											<div class="mui-media-body">
												<h2 class="mui-body-tit">${userOrder.productInfo?if_exists}</h2>
												<p>订单状态:&nbsp;&nbsp;&nbsp;<span class="mui-bodycolor">${userOrder.orderStatus?if_exists}</span></p>
												<p>订单时间:&nbsp;&nbsp;&nbsp;<span class="mui-bodycolor">${userOrder.createTime?string('yyyy-MM-dd')}</span></p>
												<div>
													<span>返现:&nbsp;&nbsp;&nbsp;<em class="mui-first-payment">￥${userOrder.commission3?if_exists}</em></span>
												</div>
												<p class="mui-buy-gift">购买该商品可额外获得${userOrder.fanliMultiple?if_exists}倍返现奖励</p>
											</div>											
										</a>
									</li>
								  </#list>	
								  </#if>	
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 底部菜单栏 -->
	<nav class="mui-bar mui-bar-tab new-bar">
		<a class="mui-tab-item" href="<#if (userOrderCanDrawList?exists && userOrderCanDrawList?size > 0)>/v2/orderdraw<#else>javascript:void(0);</#if>">
			<span class="mui-icon mui-icon-index "></span>
			<span class="mui-tab-label">申请提现</span>
		</a>
	</nav>

<@model.webendsearchv2 />
