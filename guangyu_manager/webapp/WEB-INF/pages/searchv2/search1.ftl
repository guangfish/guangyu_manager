<@model.webheadsearchv2 />
<!-- 头部 -->
	<div class=" mui-bar mui-bar-nav mui-search-box">
	    <a class="backToIndex"><span>&lt;</span>返回</a>
		<a href="searchv2" class="index-logo"><img src="/static/frontv2/img/logo.png" class="mui-logo2"></a>
		<div class="mui-input-row mui-search">
			<input type="search" class="mui-input-clear" placeholder="请粘贴从淘宝或京东复制的商品链接">
		</div>
		<a class=" mui-icon mui-icon-search mui-self-search"></a>
	</div>
	<div class="mui-content mui-content-box">
		<div id="slider" class="mui-slider" >
			<div class="mui-slider-group mui-slider-loop">				
				<#if (bannerList?exists && bannerList?size > 0)>
				<#assign listsize = bannerList?size>
				<!-- 额外增加的一个节点(循环轮播：第一个节点是最后一张轮播) -->
				<div class="mui-slider-item mui-slider-item-duplicate">
					<a href="${bannerList[1].link}">
						<img src="${bannerList[1].imgUrl}">
					</a>
				</div>
			    <#list bannerList as banner>
			    <div class="mui-slider-item">
					<a href="${banner.link?if_exists}">
						<img src="${banner.imgUrl?if_exists}">
					</a>
				</div>
			    </#list>
			    <!-- 额外增加的一个节点(循环轮播：最后一个节点是第一张轮播) -->
				<div class="mui-slider-item mui-slider-item-duplicate">
					<a href="${bannerList[listsize-1].link}">
						<img src="${bannerList[listsize-1].imgUrl}">
					</a>
				</div>
			    </#if>								
			</div>
			<div class="mui-slider-indicator">
			<#if (bannerList?exists && bannerList?size > 0)>
			<#list bannerList as banner>
			   <div class="mui-indicator <#if banner_index==0>mui-active</#if>"></div>
			</#list>
			</#if>
			</div>
		</div>
		
		<!-- 热门活动 -->
		<div class="mui-cont-box">
			<div class="mui-tit">
				<p>热门活动</p>
				<a href="javascript:void(0);">更多>></a>
			</div>
			<div class="mui-swiper">
				<div class="swiper-container">
				    <div class="swiper-wrapper">
				      <div class="swiper-slide"><img src="/static/frontv2/img/s_slide1.jpg" alt=""></div>
				      <div class="swiper-slide"><img src="/static/frontv2/img/s_slide2.jpg" alt=""></div>
				      <div class="swiper-slide"><img src="/static/frontv2/img/s_slide1.jpg" alt=""></div>
				      <div class="swiper-slide"><img src="/static/frontv2/img/s_slide2.jpg" alt=""></div>
				    </div>
				  </div>
			</div>
		</div>
		
				
		<div class="backTop">
			<a href="javascript:void(0)"></a>
		</div>
	</div>	
	
	
<script type="text/html" id="searchResult">
<div id="search-page" class="search-page">
	<!-- 热销新车 -->
	<div class="mui-cont-box ">
		<div class="mui-new-list">
			<ul class="mui-table-view ">
				
			</ul>
		</div>
	</div>	
</div>
</script>

<script type='text/javascript' src='/static/front/js/clipboard.min.js' charset='utf-8'></script>

<script>
    var swiper = new Swiper('.swiper-container', {
      slidesPerView: 'auto',
      spaceBetween:9,
      pagination: {
        el: '.swiper-pagination',
        clickable: true,
      },
    });
  </script>
 <script>
 	var _val;
 	var curPage = 0;
 	//打开搜索页面
 	$('.mui-self-search').on('tap',function(){
 		var start = 0;
 		_val=$(this).parents('.mui-search-box').find('.mui-input-clear').val();
 		resetsch();
 		$(".search-page").off("scroll").on("scroll", function() {
			scrollFun();
		})
 		if(start == 0){
 			curPage = 0;
 			start = 1;
 			wrapLoad(_val);	
 		}
 	});
 	//返回关闭搜索页面
 	$('.backToIndex').on('tap',function(){
 		curPage = 0;
 		$('.mui-search-box').removeClass('search-open');
 		$('.mui-content-box').removeClass('disn');
 		$('.mui-bar-tab').removeClass('disn');
 		$(".search-page").off("scroll").remove();
 	})
 	function resetsch(){
 		$('.mui-search-box').addClass('search-open');
 		$('.mui-content-box').addClass('disn');
 		$('.mui-bar-tab').addClass('disn');
 		$('#search-page').remove();
 		$('body').append($('#searchResult').html());
 		var pageHeight=$(window).height()-$('.mui-search-box').outerHeight();
 		$('.search-page').css('height',pageHeight);
 	}
 	
	var setTimer = null;
	var isListLoading = false; //是否正在加载
	function scrollFun() {
		clearTimeout(setTimer);
		setTimer = setTimeout(function() {
			if(!isListLoading) {
				var scrollTop = $(".search-page").scrollTop();
				var windowHeight = $(".search-page").height();
				var pageHeight = $(".search-page").find("ul").height();
				// scrollTop + windowHeight=>屏幕底部距离页面顶部的距离   pageHeight=>页面高度
				if(scrollTop + windowHeight >= pageHeight - 150) { // 滚动到距离底部还有150px---快到底部了
					isListLoading = true;
					wrapLoad(_val);
				}
			}
		}, 50)

	}

	//上拉拉刷新加载更多资源	
	var setLoading = null;
	var maxPage = 2;
	
	function wrapLoad(v) {
		var _v = v;
		curPage++;
		if($("#pullTips").length > 0) {
			$("#pullTips").remove();
		}
		if(curPage>maxPage) {
			var loadOverTips = '<div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption" style="font-weight: bold;">没有更多数据了</div></div>';
			$(".search-page").append(loadOverTips);
			isListLoading = false;
			return false;
		}
		var onloadTips = '<div id="pullTips" class="mui-pull-tips"><div class="mui-pull-loading mui-icon mui-spinner"></div><div class="mui-pull-caption">正在加载...</div></div>';
		$(".search-page").append(onloadTips);
		//加载内容
		clearTimeout(setLoading);
		setLoading = setTimeout(function() {
			mui.ajax('/static/frontv2/data/search.json',{
				data:{keys:_v,pageNo:curPage},
				dataType:'json',//服务器返回json格式数据
				type:'get',//HTTP请求类型
				timeout:10000,//超时时间设置为10秒；
				success:function(data,textStatus){
					if(document.getElementById("search-page")){
						var table = document.getElementById("search-page").querySelector('.mui-table-view');
						var o = data.list;
						for (var i = 0; i<o.length; i++) {
							var li = document.createElement('li');
							li.className = 'mui-table-view-cell mui-media pos';
							var myInner = '<a href="new_detail.html">\
								<img class="mui-media-object mui-pull-left" src="img/img1.jpg">\
								<div class="mui-media-body">\
									<h2 class="mui-body-tit">全新JEEP自由侠 180T 智能版</h2>\
									<p>车身:<span class="mui-bodycolor">天蓝色</span>内饰:<span class="mui-adorn">黑色</span>库存:<span class="mui-inventory">3567</span></p>\
									<div>\
										<span>首付:<em class="mui-first-payment">2.28万</em></span>\
										<span>参考价:<del>93600元</del></span>\
										<span>月供:<em class="mui-month-price">2368元</em></span>\
									</div>\
									<p class="mui-buy-gift">购车送全车贴膜、送第一年车辆保险</p>\
								</div>\
							</a>';
							li.innerHTML = myInner;
							table.appendChild(li);
						}
						isListLoading = false;
					}
				},
				error:function(xhr,type,errorThrown){
					//异常处理；
					//console.log(type);
				}
			});
		}, 800);
	}

 	

 </script>
<@model.webendsearchv2 />
