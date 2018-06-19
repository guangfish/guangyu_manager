<@model.webheadsearchv2 />
<link rel="stylesheet" type="text/css" href="/static/frontv2/css/self_index/self_index.css">

<header class="mui-bar mui-bar-nav mui-setting-tit">
		<a class="mui-icon mui-icon-left-nav mui-pull-left" href="/v2/search"></a>
		<h1 class="mui-title">我的订单</h1>

	</header>
	<div class="mui-content self-content-box">
		<div class="mui-main-common">
			<div class="mui-common-tit tab_links">
			    <a data-id="#all" class="mui-event-tap active">全部</a>
				<a data-id="#complete" class="mui-event-tap">订单结算</a>
				<a data-id="#pay" class="mui-event-tap">订单付款</a>
				<a data-id="#novalid" class="mui-event-tap">订单失效</a>
			</div>
			<div id="pullrefresh" class="mui-scroll-wrapper scroll-refresh">			    
				<div class="mui-scroll">
				    <div id="all" class="mui-common-cont">
						<div class="mui-cont-box ">
							<div class="mui-new-list">								  
								<ul class="mui-table-view ">	
								  <#if (userOrderList?exists && userOrderList?size > 0)>	
								  <#list userOrderList as userOrder>																																											
									<li class="mui-table-view-cell mui-media pos">
										<a href="javascript:void(0);">
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
					<div id="complete" class="mui-common-cont disn">
						<div class="mui-cont-box ">
							<div class="mui-new-list">
							  <#if (canDrawOrderNum>0)>
							    <br/><div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption"><font color="red">${canDrawOrderNum?if_exists}</font>件已结算，可提现金额<font color="red">￥${tCommission?if_exists}</font>
							    </div></div><br/>						      
							  </#if>
							  
								<ul class="mui-table-view ">	
								  <#if (userOrderCanDrawList?exists && userOrderCanDrawList?size > 0)>	
								  <#list userOrderCanDrawList as userOrder>																																											
									<li class="mui-table-view-cell mui-media pos">
										<a href="javascript:void(0);">
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
					<div id="pay" class="mui-common-cont disn">
						<div class="mui-cont-box">
							<div class="mui-new-list">
							  <#if (uncanDrawOrderNum>0)><br/><div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption"><font color="red">${uncanDrawOrderNum?if_exists}</font>件已付款，预估可提现金额<font color="red">￥${tUCommission?if_exists}</font></div></div><br/></#if>
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
					<div id="novalid" class="mui-common-cont disn">
						<div class="mui-cont-box">
							<div class="mui-new-list">						
								<ul class="mui-table-view">		
								  <#if (userOrderNoValidDrawList?exists && userOrderNoValidDrawList?size > 0)>	
								  <#list userOrderNoValidDrawList as userOrder>																										
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
		<a class="mui-tab-item mui-active" href="<#if (userOrderCanDrawList?exists && userOrderCanDrawList?size > 0)>/v2/orderdraw<#else>javascript:void(0);</#if>">
			<span class="mui-icon mui-icon-draw"></span>
			<span class="mui-tab-label">申请提现</span>
		</a>
	</nav>
	

	

<@model.webendsearchv2 />
