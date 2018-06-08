var newDetail ={
	init:function(){
		this.detailClick();//点击切换
		this.reportFun();//评估报告点击展开
		this.myQuestion();//问答
	},
	detailSlide:function(){
		// slide滑动获取当前页面index
		var mySwiper = new Swiper('.swiper-container', {
		  slidesPerView: 1,
		  loop: true,

		});
		$('.mui-slide-all').html(mySwiper.slides.length-2);
		if(mySwiper.slides.length<=3){
			mySwiper.destroy();
		}else{
			mySwiper.on('slideChangeTransitionEnd', function () {
			 var imgurl = $('.swiper-slide-active img').attr("src");
			 var txt = $('.swiper-slide-active').html();
			 $('.mui-slide-current').html($('.swiper-slide-active').data('swiper-slide-index')+1);
			});
		}
	},
	detailClick:function(){
		// 点击资质筛选和综合排序
		$('.mui-collapse').off('tap').on('tap','.mui-navigate-right',function(){
			var _t=$(this);
			if($(_t.data('id')).hasClass('disn')){
				$(_t.data('id')).removeClass('disn');
			}else{
				$(_t.data('id')).addClass('disn');
			}
			_t.parent().siblings().find('a').each(function(){
				var _panelbox = $(this).data('id');
				$(_panelbox).addClass('disn');
			});
			// 选中样式 获取 title v
			$('.detail-item').off('tap').on('tap','.view-list',function(){
				var _this = $(this);
				_this.parents('.detail-item').find('a').removeClass('current');
				_this.addClass('current');
			    _this.parents('.detail-item').find('a').not('.current').each(function(){
					$(this).removeClass('current');
			    });
			    var _title = _this.attr('title');
			    var _v = _this.attr('v');
			    _t.html(_title).attr('title',_title);
			    _t.addClass('current-style');
			    _this.parents('.mui-getvalue-box').find('input.apt-input').val(_v);
			    
			});
			$('#viewbox1').find('.detail-item').on('tap',function(){
				detailPost();
			})
			$('.mui-btn-inquire').off('tap').on('tap',function(){
				detailPost();
				$('#viewbox2').addClass('disn');
				_t.parents('.mui-collapse').removeClass('mui-active')
			});
			$('#viewbox3').find('.detail-item').on('tap',function(){
				insPost();
			});
			$('.mui-btn-disn').off('tap').on('tap',function(){
				$(this).parents('#viewbox2').find('input.apt-input').val('');
				$(this).parents('#viewbox2').find('.view-list').removeClass('current');
				_t.removeClass('current-style').html('资质筛选');
			});
		});
		$('.very-easy').prepend('<i></i>');

		// 金融获取数据
		function getInfor(){
			var infor = {};
			//排序
			var sortValue = $('#viewbox1').find('input.apt-input').val();
			var field=$('#viewbox1').find('input.apt-input').attr('name');
			if(sortValue != ''){
				infor[field] = sortValue;
			}

			//资质筛选
			var valueData=[];
			$('.mui-aptitude-box').find('input.apt-input').each(function(){
				var _t = $(this);
				if(_t.val()!=''){
					field = _t.attr('name');
					infor[field]=_t.val();
				}
			})
			return infor;
		}
		
		// 金融提交数据
		function detailPost(){
			var infor = getInfor();
			$.ajax({
				url: '../data/new-cart.json',
				type: 'get',
				dataType: 'json',
				data: {'infor': infor},
				success:function(data){
					var d = data.result;
					var innerHtml = '';
					for(var i=0;i<3;i++){
					 innerHtml += '<li class="mui-view-item">\
									<img class="banklogo" src="img/detail/bk_logo2.png" alt="">\
									<div class="mui-item-cont">\
										<h2>财智金（广东发展银行</h2>\
										<p>0首付,汽车轻松开回家</p>\
										<div class="bank-apply"><span>申请难度</span>\
											<p class="b-icon easy"><i></i><span>容易</span></p>\
										</div>\
										<div class="b-tip"><span>仅需身份证</span><span>仅需社保</span></div>\
									</div>\
									<div class="bank-price">\
										<p><b>3468</b>元/月</p><span>9800元（分期成本）</span>\
									</div>\
									<a class="mui-consult" href="tel:400-0000-688">电话咨询<i></i></a>\
								</li>';
					}
					$('#box1').find('.mui-view-content').find('.mui-view-album').html(innerHtml);
				}
			})
		}
		// 保险获取数据
		function insInfor(){
			var infor = {};
			//排序
			var sortValue = $('#viewbox3').find('input.apt-input').val();
			var field=$('#viewbox3').find('input.apt-input').attr('name');
			if(sortValue != ''){
				infor[field] = sortValue;
			}
			return infor;
		}
		// 保险提交数据
		function insPost(){
			var infor = insInfor();
			$.ajax({
				url: '../data/new-cart.json',
				type: 'get',
				dataType: 'json',
				data: {'infor': infor},
				success:function(data){
					var d = data.result;
					var innerHtml = '';
					for(var i=0;i<3;i++){
					 innerHtml += '<li class="mui-view-item">\
									<img class="banklogo" src="img/detail/bx_logo1.png" alt="">\
									<div class="mui-item-cont">\
										<h2>中国人民保险</h2>\
										<p class="insurance-hint">投保免费享受金牌服务</p>\
									</div>\
									<div class="bank-price">\
										<p>报价<b>3468元</b></p><span>(共优惠 9800元)</span>\
									</div>\
									<a class="mui-consult" href="tel:400-0000-688">电话咨询<i></i></a>\
								</li>';
					}
					$('#box2').find('.mui-view-content').find('.mui-view-album').html(innerHtml);
				}
			})
		}

		// 滚动隐藏 置顶
		function detailScroll(){
			var scroll = mui('.scroll-box').scroll();
			var d_top =0;
		    $('.scroll-box' ).on('scroll', function (e ) { 
		    	d_top=-scroll.y;
		      	if(scroll.y <-450) {
					$('.news-toggle-tit').addClass('select-fixed');
					$('.mui-bar-nav').hide();
					$('.news-toggle-tit').on('tap','a',function(){
						if(-scroll.y>$(".swiper-container").height()){
							scroll.scrollTo(0,-$(".swiper-container").height(),300);
						}
					}); 
				} else {
					$('.news-toggle-tit').removeClass('select-fixed');
					$('.mui-bar-nav').show();
				}
		    });
		}
		detailScroll();
		
	},
	reportFun:function(){
		var flag = true;
		// 报告下拉
		$('.mui-report-list').on('tap','.report-list-tit',function(){
			var t = $(this);
			if(t.find('i').hasClass('rotate')){
				t.next('.report-list-panel').removeClass('disn');
				t.find('i').removeClass('rotate').addClass('rotate1');
			}else{
				t.next('.report-list-panel').addClass('disn');
				t.find('i').removeClass('rotate1').addClass('rotate');
			}
			if(t.next('.report-list-panel').find('.swiper-container').length>0 && flag){
				slideFun();
			}
			function slideFun(){
				newDetail.detailSlide();
				flag = false;
			}
		});
		// 显示损坏图片
		$('.state-fault').off('tap').on('tap','em',function(){
			var src=$(this).data('href');
			if(src==''|| src == undefined){
				mui.toast('暂时没有图片') ;
			}else{
				$('.report-pop img').attr('src',src);
				$('.select-mask,.report-pop').removeClass('disn')
				popCenterWindow($('.report-pop'));
			}
			
		});
		$('.select-mask').on('tap',function(){
			$('.select-mask,.report-pop').addClass('disn')
		})
		//定义弹出居中窗口的方法 
		function popCenterWindow(layer) {
		    var windowHeight = $(window).height();
		    var windowWidth = $(window).width();
		    var popHeight = layer.height();
		    var popWidth = layer.width();
		    //计算弹出窗口的左上角Y的偏移量 
		    var popY = (windowHeight - popHeight) / 2;
		    var popX = (windowWidth - popWidth) / 2;
		    layer.css("top", popY).css("left", popX);
		}
	},
	myQuestion:function(){
		// 提问
		// 'ques.val()'->获取回答问题
		$('.my_question_btn').on('tap',function(){
			var ques=$(this).parents('.my_question_box').find('.my_question_cont');
			var inner = '<li class="mui-answer-item">\
							<div class="mui-problem">\
								<em>问</em>\
								<span>'+ques.val()+'</span>\
							</div>\
						</li>';
			$(this).parents('.mui-answer-box').find('ul').prepend(inner);
			ques.val(' ');
		});
		// 回答
		$('.mui-my-reply').on('tap',function(){
			var t = $(this);
			var replyHtml=t.parents('.mui-answer-item').find('.mui-problem span').html();
			var replyId=t.data('id')
			$(replyId).find('.replyTit').html(replyHtml);
			$('.my_reply_btn').on('tap',function(){
				t.parents('.mui-answer-item').find('.mui-reply span').html('<i></i>'+$(this).prev('.my_reply_cont').val());
				$('.mask-box').fadeOut();
				$('.mui-content').removeClass('ovfHiden');
			})

		});
	},
	localdata:function(){
		// 跳转页面后,跳转到指定位置
		var dataKey = localStorage.getItem('key');
		var allcostBtn = localStorage.getItem('allcostBtn')
		if(dataKey){
			// 到金融方案
			$('.mui-content').scrollTop(dataKey);
			localStorage.removeItem('key');
		}else{
			// 到保险方案
			$('.mui-content').scrollTop(allcostBtn);
			if(allcostBtn){
				$('.mui-detail-box').find('.news-toggle-tit a').each(function(i,v){
					if($(v).data('id') == '#box2'){
						$(this).trigger('tap');
					}
				})
			}
			localStorage.removeItem('allcostBtn');
		}
	}
}
$(function(){
	newDetail.init();
	mui('.mui-main-detail').on('tap','a',function(){
		if($(this).attr('href')){
			document.location.href=this.href;
		}
	});
	
})