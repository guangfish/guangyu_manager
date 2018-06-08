<@model.webheadsearchv2 />
<!-- 头部 -->
	<div class=" mui-bar mui-bar-nav mui-search-box">
	    <a class="backToIndex"><span>&lt;</span>返回</a>
		<a href="searchv2" class="index-logo"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
		<div class="mui-input-row mui-search">
			<input type="search" class="mui-input-clear" placeholder="请粘贴从淘宝或京东复制的商品链接">
		</div>
		<a class=" mui-icon mui-icon-search mui-self-search"></a>
	</div>
	<div class="mui-content mui-content-box">
		
		<!-- 热门活动 -->
		<div class="mui-cont-box">
			<div class="mui-tit">
				<p>热门活动</p>
				<a href="event_index.html">更多>></a>
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
		
		<!-- 好券直播 -->
		<div class="mui-cont-box ">
			<div class="mui-tit">
				<p>好券直播</p>
				<a href="/api/more">更多>></a>
			</div>
			
			<div class="mui-new-list">
				<ul class="mui-table-view ">
				    <#if (productInfoList?exists && productInfoList?size > 0)>
			        <#list productInfoList as productInfo>
					<li class="mui-table-view-cell mui-media pos">
						<a target="_blank" onclick="<#if ifWeixinBrower=="yes">jsCopy('${productInfo.tklquan?if_exists}')<#else>drump('${productInfo.couponPromoLink?if_exists}')</#if>" href="javascript:void(0);">
							<img class="mui-media-object mui-pull-left" src="${productInfo.productImgUrl?if_exists}">
							<div class="mui-media-body">
								<h2 class="mui-body-tit">${productInfo.productName?if_exists}</h2>
								<p>商店名:<span class="mui-inventory">${productInfo.shopName?if_exists}</span></p>
								<p>现价:<span class="mui-inventory">￥${productInfo.price?if_exists}</span>月销量:<span class="mui-adorn">${productInfo.monthSales?if_exists}件</span></p>
								<p>券:<span class="mui-inventory">${productInfo.couponMiane?if_exists}</span><span class="mui-inventory">余${productInfo.couponRest?if_exists}张</span></p>
								<div>
									<span>预估返现:<em class="mui-first-payment">￥<#if (productInfo.commission?exists)>${productInfo.commission*rate}</#if></em></span>
								</div>
								<p class="mui-buy-gift">购买该商品预估可额外获得${productInfo.fanli?if_exists}倍返现奖励</p>
							</div>
						</a>
					</li>
					</#list>
			        </#if>														
				</ul>
			</div>
		</div>
		
		<!-- 底部内容 -->
		<div class="mui-cont-box">
			<div class="f_content">
				<!--<a><img style="color: red;width:60px;height:60px" src="/static/frontv2/img/guangfish/favicon.png" alt=""></a>-->
				<div class="text-center">
					<p>一个购物拿返利的神器，花钱还能赚钱，支持淘宝、京东。</p>
					<p>90%的商品背后商家都设置了返现，返现比例最高可达80%以上，只是大家平时购物的时候不知道这个信息，逛鱼搜索不仅是一个搜返利的工具，也是一个拿返现的工具。</p>
					<p>工具非常适用于喜欢网上购物、希望获得优惠的朋友。</p>
				</div>
			</div>
			<div class="mui-footer-tel">
				<p><img style="width:120px;height:120px" src="http://help.guangfish.com/imgs/gzh.jpg"></p>	
				<span>©2018 杭州特为网络科技有限公司 版权所有</span><br/>
				<span>浙ICP备13014457号-2号</span>	
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
  function isWeiXin(){
          //window.navigator.userAgent属性包含了浏览器类型、版本、操作系统类型、浏览器引擎类型等信息，这个属性可以用来判断浏览器类型
          var ua = window.navigator.userAgent.toLowerCase();
          //通过正则表达式匹配ua中是否含有MicroMessenger字符串
          if(ua.match(/MicroMessenger/i) == 'micromessenger'){
            return true;
          }else{
            return false;
          }
        }

  function drump(link) {
	window.open(link);
  }
  
  function jsCopy(tkl){
	$('#copy').attr('data-clipboard-text', tkl);
	var clipboard = new Clipboard('#copy');
    clipboard.on('success', function (e) {
       Core.Dialog.msg('淘口令复制成功，去打开手机淘宝完成商品购买，【付款时不要用红包抵扣】，完成购买后记得回来录入订单号拿返利哦！',9000);
       $('#copy').removeAttr('data-clipboard-text');
    });
    clipboard.on('error', function (e) {
      alert('复制失败');
    });
  }
</script>

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
 	    var producturl = $('.mui-input-clear').val();
 	    if (!producturl) {
 	      Core.Dialog.msg("请粘贴从淘宝或京东复制的商品链接",5000);
 	      return;
 	    }
 	    
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
			mui.ajax('/api/productInfo',{
				//data:{product_url:_v,pageNo:curPage},
				data : JSON.stringify({
								"product_url" : _v,
								"pageNo" : curPage
							}),
				dataType:'json',//服务器返回json格式数据
				type:'post',//HTTP请求类型
				timeout:10000,//超时时间设置为10秒；
				success:function(data,textStatus){
					var table = document.getElementById("search-page").querySelector('.mui-table-view');
					var li = document.createElement('li');
					li.className = 'mui-table-view-cell mui-media pos';
					if (JSON.stringify(data) == "{}") {					  
					  var myInner = '<div class="mui-media-body"><h2 class="mui-body-tit">该商品无返利</h2></div>';
					}else{
					  var status=data.ret.result.status;
					  if(status=="1"){
					    Core.Dialog.msg("系统繁忙，请稍后再试",5000);
					  }else if(status=="2"){
						Core.Dialog.msg("请输入商品链接地址",5000);
					  }else if(status=="3"){
						Core.Dialog.msg("商品链接地址不正确",5000);
					  }else if(status=="4"){
						Core.Dialog.msg("系统只支持手机淘宝、京东的商品地址",5000);						
					  }
					  if(status!="0"){
					    $("#pullTips").remove();
					    return;
					  }
					  var url;
					  var goodUrl=data.ret.result.map.goodUrl;
					  var quanUrl=data.ret.result.map.quanUrl;
					  var img=data.ret.result.map.img;
					  var title=data.ret.result.map.title;
					  var shop=data.ret.result.map.shop;
					  var price=data.ret.result.map.price;
					  var sellNum=data.ret.result.map.sellNum;
					  var money=data.ret.result.map.money;
					  var per=data.ret.result.map.per;
					  var tkl=data.ret.result.map.tkl;
					  var tklquan=data.ret.result.map.tklquan;
					  var tkl0;
					  var fanliMultiple=data.ret.result.map.fanliMultiple;
					  if(quanUrl!=""){
					    url=quanUrl;
					    tkl0=tklquan;
					  }else{
					    url=goodUrl;
					    tkl0=tkl;
					  }
					  
					  var func;
					  if(isWeiXin()){
					    func="jsCopy('"+tkl0+"')";
					  }else{
					    func="drump('"+url+"')";
					  }
					  var myInner = '<a id="copy" onclick="'+func+'" href="javascript:void(0);">\
							<img class="mui-media-object mui-pull-left" src="'+img+'">\
							<div class="mui-media-body">\
								<h2 class="mui-body-tit">'+title+'</h2>\
								<p>商店名:<span class="mui-bodycolor">'+shop+'</span></p>\
								<p>价格:<span class="mui-bodycolor">'+price+'</span>月销量:<span class="mui-adorn">'+sellNum+'</span></p>\
								<div>\
									<span>预估返现:<em class="mui-first-payment">'+money+'('+per+')</em></span>\
								</div>\
								<p class="mui-buy-gift">购买该商品预估可额外获得'+fanliMultiple+'倍返现奖励</p>\
							</div>\
						</a>';
					}
					li.innerHTML = myInner;
					table.appendChild(li);
										
					isListLoading = false;
					$("#pullTips").remove();
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
