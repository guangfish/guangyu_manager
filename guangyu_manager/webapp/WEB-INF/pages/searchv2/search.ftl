<@model.webheadsearchv2 />

<!-- 头部 -->
	<div class=" mui-bar mui-bar-nav mui-search-box">
	    <a style="text-decoration: none;" class="backToIndex mui-icon mui-icon-back"></a>
		<a href="/v2/search" class="index-logo"><img src="/static/frontv2/img/guangfish/logo-cn.png" class="mui-logo2"></a>
		<div class="mui-input-row mui-search">
			<input type="search" class="mui-input-clear" placeholder="粘贴宝贝标题，先领券再购物">
		</div>
		<a style="text-decoration: none;" class=" mui-icon mui-icon-search mui-self-search"></a>
	</div>
	<div class="mui-content mui-content-box">
		
		<div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption"><font color="red">今日已为用户节省${saveMoney?if_exists}元</font></div></div>
		
		<!-- 热门活动 -->
		<div class="mui-cont-box">
			<div class="mui-tit">
				<p>热门活动</p>
				<a style="text-decoration: none;" href="/v2/campaign">更多>></a>
			</div>
			<div class="mui-swiper">
				<div class="swiper-container">
				    <div class="swiper-wrapper">
				      <#list campaignList as banner>
				      <div class="swiper-slide"><a href="${banner.link?if_exists}" <#if (banner.target==2)>target="_blank"</#if>><img src="${banner.imgUrl?if_exists}" alt=""></a></div>
				      </#list>
				    </div>
				  </div>
			</div>
		</div>
		
		<!-- 好券直播 -->
		<div class="mui-cont-box ">
			<div class="mui-tit">
				<p>好券直播</p>
				<a style="text-decoration: none;" class="mui-self-search">更多>></a>
			</div>
			
			<div class="mui-new-list">
				<ul class="mui-table-view ">
				    <#if (productInfoList?exists && productInfoList?size > 0)>
			        <#list productInfoList as productInfo>
					<li class="mui-table-view-cell mui-media pos">
						<a target="_blank" id="copy${productInfo.productId?if_exists}" onclick="<#if (ifWeixinBrower=="yes")>jsCopyId('${productInfo.tkl?if_exists}','${productInfo.productId?if_exists}')<#else>drump('${productInfo.couponPromoLink?if_exists}')</#if>" href="javascript:void(0);">
							<img class="mui-media-object mui-pull-left" src="${productInfo.productImgUrl?if_exists}">
							<div class="mui-media-body">
								<h2 style="white-space: pre-wrap;display:-webkit-box;-webkit-box-orient:vertical;-webkit-line-clamp:2;" class="mui-body-tit"><#if (productInfo.platformType?exists)><img style="width:20px;align:center;" src="/static/frontv2/img/guangfish/<#if (productInfo.platformType=="淘宝")>taobao.png<#else>tmall.png</#if>"/>&nbsp;</#if>${productInfo.productName?if_exists}</h2>
								<p style="margin-top: 3px">商店名:&nbsp;<span class="mui-inventory">${productInfo.shopName?if_exists}</span></p>
								<p><span class="mui-inventory">现价:&nbsp;${productInfo.price?if_exists}元</span><span style="position: absolute;right: 0;text-align: center;" class="mui-adorn">月销量:&nbsp;${productInfo.monthSales?if_exists}件&nbsp;&nbsp;</span></p>
								<p><span class="mui-inventory">券:&nbsp;${productInfo.couponMiane?if_exists}</span><span style="position: absolute;right: 0;text-align: center;" class="mui-inventory">余${productInfo.couponRest?if_exists}张&nbsp;&nbsp;</span></p>
								
								<div style="margin-top: 12px">
									<span>领券省:&nbsp;<em class="mui-first-payment">${productInfo.couponQuan?if_exists}元</em></span><span style="position: absolute;right: 0;text-align: center;">再返现:&nbsp;<em  class="mui-first-payment"><#if (productInfo.commission?exists)>${productInfo.commission*rate}元</#if>&nbsp;&nbsp;</em></span>
								</div>								
								<!--<p class="mui-buy-gift">购买该商品预估可额外获得${productInfo.fanli?if_exists}倍返现奖励</p>-->
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
				<span>浙ICP备33011002013316号</span>	
			</div>	
		</div>
				
		<div class="backTop">
			<a href="javascript:void(0)"></a>
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
	
<script type="text/html" id="searchResult">
<div id="search-page" class="search-page">
	<!-- 热销新车 -->
	<div class="mui-cont-box ">
		<div class="mui-new-list">
			<ul class="mui-table-view ">
				
			</ul>
		</div>
		<div class="backTop disn">
			<a href="javascript:void(0)"></a>
		</div>
	</div>	
</div>
</script>

<script type='text/javascript' src='/static/front/js/clipboard.min.js' charset='utf-8'></script>

<script>
  setTimeout("clear()", 5000);
  
  function clear(){
    $("#pullTips").remove();
  }

  var B = setInterval(function(){
	    $
						.ajax({
							type : "post",
							url : "/api/notice", 
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							timeout : 30000, 
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(JSON.stringify(data) != "{}"){
								  for(var i = 0; i < data.ret.length; i++){
								    //alert(data.ret[i].type + " " + data.ret[i].content);
								    var notice = $.cookie('guangfishnotice'+data.ret[i].id);
								    if(!notice){
								      if(data.ret[i].type==2){
								        Core.Dialog.note({'title':data.ret[i].title,'content':data.ret[i].content,'btn':['<div style="font-size:12px;">知道了</div>'],'callback':function(){}})
								      }else{
								        Core.Dialog.msg(data.ret[i].content,data.ret[i].noticeTime);
								      }
								      $.cookie('guangfishnotice'+data.ret[i].id, 'notice', { expires: data.ret[i].expires, path: '/',domain:'${cookieDomain?if_exists}'});
								      break;
								    }								    
								  }
								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
	  },15000);
</script>

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
    var producturl = $('.mui-input-clear').val();
    if(producturl.indexOf("jd.com") != -1){
    }else{
      <#if ifWeixinBrower=="yes">
        alert("微信屏蔽淘宝链接，建议用手机浏览器访问逛鱼网！");
      </#if>
    }
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
  
  function jsCopyId(tkl,id){
	$('#copy'+id).attr('data-clipboard-text', tkl);
	var clipboard = new Clipboard('#copy'+id);
    clipboard.on('success', function (e) {
       Core.Dialog.msg('淘口令复制成功，去打开手机淘宝完成商品购买，【付款时不要用红包抵扣】，完成购买后记得回来录入订单号拿返利哦！',9000);
       $('#copy'+id).removeAttr('data-clipboard-text');
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
    var gallery = mui('.mui-slider');
    	gallery.slider({
      	interval:5000//自动轮播周期，若为0则不自动播放，默认为0；
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
 		$('.search-page').scroll(function() {
 			var scrollt = $('.search-page').scrollTop();
 			if(scrollt > 0) {
 				$(".backTop").show();
 			} else {
 				$(".backTop").stop().hide();
 			}
 		});
 		$(".backTop").on('tap','a',function() {
 			console.log('123');
 			 $('.search-page').animate({
 				scrollTop: "0px"
 			}, 200);
 		});
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
	  var mobile = $.cookie('mobile');
	  if(!mobile){
	    location.href="/v2/login?toUrl=/v2/search";
	  }
	  	  
	  var _v = v;
	  if(_v && _v.indexOf("http") != -1){
	    alert("偶尔短暂性不能通过链接、淘口令搜索商品时，请拷贝宝贝标题搜索！");
	    
		curPage++;
		if($("#pullTips").length > 0) {
			$("#pullTips").remove();
		}
		var onloadTips = '<div id="pullTips" class="mui-pull-tips"><div class="mui-pull-loading mui-icon mui-spinner"></div><div class="mui-pull-caption">正在加载...</div></div>';
		$(".search-page").append(onloadTips);
		//加载内容
		clearTimeout(setLoading);
		setLoading = setTimeout(function() {
			mui.ajax('/api/productInfo',{
				data : JSON.stringify({
								"product_url" : _v,
								"pageNo" : curPage
							}),
				dataType:'json',//服务器返回json格式数据
				type:'post',//HTTP请求类型
				timeout:10000,//超时时间设置为10秒；
				success:function(data,textStatus){
				    console.log(data);
					var table = document.getElementById("search-page").querySelector('.mui-table-view');
					var li = document.createElement('li');
					li.className = 'mui-table-view-cell mui-media pos';
					var myInner ="";
					if (JSON.stringify(data) == "{}") {
					  $("#pullTips").remove();
					  myInner = '<div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption">该商品无返利</div></div>';
					  $(".search-page").append(myInner);
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
					  var quanMianzhi=data.ret.result.map.quanMianzhi;
					  if(quanUrl!=""){
					    url=quanUrl;
					    tkl0=tklquan;
					  }else{
					    url=goodUrl;
					    tkl0=tkl;
					  }
					  
					  var func;
					  if(isWeiXin()){
					    if(tkl0){
					      func="jsCopy('"+tkl0+"')";
					    }else{
					      func="drump('"+url+"')";
					    }					    
					  }else{
					    func="drump('"+url+"')";
					  }
					  if(quanUrl==""){
					  myInner = '<a id="copy" onclick="'+func+'" href="javascript:void(0);">\
							<img class="mui-media-object mui-pull-left" src="'+img+'">\
							<div class="mui-media-body">\
								<h2 style="white-space: pre-wrap;display:-webkit-box;-webkit-box-orient:vertical;-webkit-line-clamp:2;" class="mui-body-tit">'+title+'</h2>\
								<p style="margin-top: 10px">商店名:&nbsp;<span class="mui-bodycolor">'+shop+'</span></p>\
								<p style="margin-top: 10px"><span class="mui-bodycolor">价格:&nbsp;'+price+'</span><span style="position: absolute;right: 0;text-align: center;" class="mui-adorn">月销量:&nbsp;<span class="mui-adorn">'+sellNum+'件&nbsp;&nbsp;</span></p>\
								<div style="margin-top: 12px">\
									<span>预估返现:&nbsp;<em class="mui-first-payment">'+money+'</em></span>\
								</div>\
							</div>\
						</a>';
						}else{
						myInner = '<a id="copy" onclick="'+func+'" href="javascript:void(0);">\
							<img class="mui-media-object mui-pull-left" src="'+img+'">\
							<div class="mui-media-body">\
								<h2 style="white-space: pre-wrap;display:-webkit-box;-webkit-box-orient:vertical;-webkit-line-clamp:2;" class="mui-body-tit">'+title+'</h2>\
								<p style="margin-top: 10px">商店名:&nbsp;<span class="mui-bodycolor">'+shop+'</span></p>\
								<p style="margin-top: 10px"><span class="mui-bodycolor">价格:&nbsp;'+price+'</span><span style="position: absolute;right: 0;text-align: center;" class="mui-adorn">月销量:&nbsp;'+sellNum+'件&nbsp;&nbsp;</span></p>\
								<div style="margin-top: 12px">\
									<span>领券省:&nbsp;<em class="mui-first-payment">'+quanMianzhi+'</em></span><span style="position: absolute;right: 0;text-align: center;">再返现:&nbsp;<em class="mui-first-payment">'+money+'&nbsp;&nbsp;</em></span>\
								</div>\
							</div>\
						</a>';
						}
					}
					li.innerHTML = myInner;
					table.appendChild(li);
										
					isListLoading = false;
					$("#pullTips").remove();
				},
				error:function(xhr,type,errorThrown){
					//异常处理；
					//console.log(type);
					$("#pullTips").remove();
				}
			});
		}, 800);
	  }else{
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
		if(_v){
		}else{
		  _v="";
		}
		setLoading = setTimeout(function() {
			mui.ajax('/v2/api/more',{
				data:{product_url:_v,pageNo:curPage},
				dataType:'json',//服务器返回json格式数据
				type:'post',//HTTP请求类型
				timeout:10000,//超时时间设置为10秒；
				success:function(data,textStatus){
				    console.log(data);
					if(document.getElementById("search-page")){
						var table = document.getElementById("search-page").querySelector('.mui-table-view');
						var o = data.list;
						if(!o){
						   $("#pullTips").remove();
					       var myInner = '<div id="pullTips" class="mui-pull-tips"><div class="mui-pull-caption">该商品无返利</div></div>';
					       $(".search-page").append(myInner);
						   return;
						}else{
						maxPage=data.maxPage;
						var list;
						for (var i = 0; i<o.length; i++) {	
						    list=o[i]; 
							var li = document.createElement('li');
							li.className = 'mui-table-view-cell mui-media pos';
							var couponRest=list.couponRest;
							if(!couponRest){
							  couponRest=999;
							}
							
							var func;
					        if(isWeiXin()){
					          if(list.tkl){
					            func="jsCopyId('"+list.tkl+"','"+list.productId+"')";
					          }else{
					            func="drump('"+list.couponPromoLink+"')";
					          }					    
					        }else{
					          func="drump('"+list.couponPromoLink+"')";
					        }
							var zkPrice=list.zkPrice;
							var price=list.price
							if(zkPrice==0){
							  zkPrice=price;
							}
							var platformType=list.platformType
							var mallimg="";
							var imgstr="";
							var px=20;
							if(platformType=="淘宝"){
							  mallimg="taobao.png";		
							  imgstr='<img style="width:20px;align:center;" src="/static/frontv2/img/guangfish/'+mallimg+'">&nbsp;';				
							}else if(platformType=="天猫"){
							  mallimg="tmall.png";
							  imgstr='<img style="width:20px;align:center;" src="/static/frontv2/img/guangfish/'+mallimg+'">&nbsp;';
							}else{
							  imgstr='';
							}
							var del='';
							if(list.price){
							  del='&nbsp<del>'+price+'元</del>';
							}
							if(list.couponMiane){
							  var myInner = '<a id="copy'+list.productId+'" target="_blank" onclick="'+func+'" href="javascript:void(0);">\
								<img class="mui-media-object mui-pull-left" src="'+list.productImgUrl+'">\
								<div class="mui-media-body">\
									<h2 style="white-space: pre-wrap;display:-webkit-box;-webkit-box-orient:vertical;-webkit-line-clamp:2;" class="mui-body-tit">'+imgstr+list.productName+'</h2>\
									<p style="margin-top: 3px">商店名:&nbsp;<span class="mui-inventory">'+list.shopName+'</span></p>\
									<p><span class="mui-bodycolor">现价:&nbsp;'+zkPrice+'元'+del+'</span><span style="position: absolute;right: 0;text-align: center;" class="mui-adorn">月销量:&nbsp;<span class="mui-adorn">'+list.monthSales+'件&nbsp;&nbsp;</span></p>\
									<p><span class="mui-inventory">券:&nbsp;'+list.couponMiane+'</span><span style="position: absolute;right: 0;text-align: center;" class="mui-adorn">余<span class="mui-inventory">'+couponRest+'张&nbsp;&nbsp;</span></p>\
									<div style="margin-top: 12px">\
										<span>领券省:&nbsp;<em class="mui-first-payment">'+list.couponQuan+'元</em></span><span style="position: absolute;right: 0;text-align: center;">再返现:&nbsp;<em class="mui-first-payment">'+list.actualCommission+'元&nbsp;&nbsp;&nbsp;</em></span>\
									</div>\
								</div>\
							  </a>';
							}else{
							  var myInner = '<a id="copy'+list.productId+'" target="_blank" onclick="'+func+'" href="javascript:void(0);">\
								<img class="mui-media-object mui-pull-left" src="'+list.productImgUrl+'">\
								<div class="mui-media-body">\
									<h2 style="white-space: pre-wrap;display:-webkit-box;-webkit-box-orient:vertical;-webkit-line-clamp:2;" class="mui-body-tit">'+imgstr+list.productName+'</h2>\
									<p style="margin-top: 3px">商店名:&nbsp;<span class="mui-inventory">'+list.shopName+'</span></p>\
									<p><span class="mui-bodycolor">现价:&nbsp;'+zkPrice+'元'+del+'</span><span style="position: absolute;right: 0;text-align: center;" class="mui-adorn">月销量:&nbsp;<span class="mui-adorn">'+list.monthSales+'件&nbsp;&nbsp;</span></p>\
									<div style="margin-top: 12px">\
										<span>预估返现:&nbsp;<em class="mui-first-payment">'+list.actualCommission+'元</em></span>\
									</div>\
								</div>\
							  </a>';
							}							
							li.innerHTML = myInner;
							table.appendChild(li);
						}
						isListLoading = false;
						$("#pullTips").remove();
	                  }
					}
				},
				error:function(xhr,type,errorThrown){
					//异常处理；
					//console.log(type);
					$("#pullTips").remove();
				}
			});
		}, 800);
	  }		
	}
 </script>
<@model.webendsearchv2 />
