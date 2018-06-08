
var counter ={
	init:function(){
		this.backTop();//返回顶部
		this.limitText();//限制字数
		this.tabLinks(); //点击切换
		this.titClick();
		this.detailPop();//弹窗
		this.settingClick();
		this.detailContbox();
		this.nn();
	},
	backTop:function(){
		$(window).scroll(function() {
			var scrollt = document.documentElement.scrollTop + document.body.scrollTop;
			if(scrollt > 0) {
				$(".backTop").show();
			} else {
				$(".backTop").stop().hide();
			}
		});
		$(".backTop").on('tap','a',function() {
			$("html,body").animate({
				scrollTop: "0px"
			}, 200);
		});
	},
	limitText: function() {  
		$('.limit_text').each(function() {
			var _t = $(this);
			var _txt = $.trim(_t.text());
			var _link = _t.attr("more-link");
			_t.data('text', _txt);
			_t.html('<span>' + _txt + '</span>');
			var _span = _t.find('span');
			if(_span.height() - _t.height() > 5) { //文字超出范围
				while(_span.height() - _t.height() > 5) {
					_txt = _txt.substr(0, _txt.length - 1);
					if($('.special_text').length > 0 && _t.hasClass("special_text")) {
						_span.html(_txt + '...<a href="' + _link + '" class="view-detail">查看详情>></a>');
						$(".special_text a").addClass("special_det")
					} else {
						_span.html(_txt + '<em>...</em>');
					}

				}
			}
		}).attr('data-limited', 'yes');
	},
	tabLinks:function(){//点击切换
		$('.tab_links a').on('tap', function () {
		    var _t = $(this);
		    _t.parents('.tab_links').find('a').removeClass('active');
		    _t.addClass('active');
		    _t.parents('.tab_links').find('a').not(_t).each(function () {
		        hideBox($(this));
		    });
		    showBox(_t);
		    counter.limitText();
		    if($('.scroll-refresh').length>0){
		    	mui(".mui-scroll-wrapper").scroll().refresh();
		    }
		});

		function showBox(lk) {
		    var _tlbox = null;
		    try {
		        _tlbox = $(lk.data('id'));
		    } catch (e) {
		        console.log(e);
		    }
		    if (_tlbox && _tlbox.length) {
		        _tlbox.removeClass('disn');
		    }
		}

		function hideBox(lk) {
		    var _tlbox = null;
		    try {
		        _tlbox = $(lk.data('id'));
		    } catch (e) {
		        console.log(e);
		    }
		    if (_tlbox && _tlbox.length) {
		        _tlbox.addClass('disn');
		    }
		}
	},
	titClick:function(){
		$('.mui-search').on('tap','.mui-search-tit',function(){
			if($(this).parents('.mui-search').hasClass('select-style')){
				$(this).parents('.mui-search').removeClass('select-style');
				$(this).find('.search-tit-select').addClass('disn');
				$(this).parents('.mui-bar').css('zIndex','10');
			}else{
				$(this).parents('.mui-search').addClass('select-style');
				$(this).find('.search-tit-select').removeClass('disn');
				$(this).parents('.mui-bar').css('zIndex','14');
			}
			
		})
	},
	detailPop:function(){
		$('.mask-click-btn').on('tap','a',function(){
			var _t = $(this);
			var _scrollTop=$(document).scrollTop();
			$(_t.data('id')).parents('.mask-box').fadeIn(function(){
				$('.mui-content').addClass('ovfHiden');
			});
			window.scrollTo(0,_scrollTop);
			// 关闭弹窗
			$('.mask-title').on('tap','.mask-close',function(e){
				var _target = e.target;
				$('.mui-content').css('position','static');
				$(_t.data('id')).parents('.mask-box').fadeOut();
				$('.mui-content').removeClass('ovfHiden');
			});
		});
		if($('.color-box span').length>7){
			$('.car-color').on('tap','.color-box',function(e){
				e.stopPropagation();
				$(this).addClass('color-pos');
				$('.in_box').css('marginTop','.67rem');
			});
			$('.mui-mask-cont').on('tap','.inventory-cont',function(e){
				e.stopPropagation();
				$(this).find('.color-box').removeClass('color-pos');
				$('.in_box').css('marginTop','.5rem');
			});
		}
		$('.color-box').on('tap','span',function(){
			var _t = $(this);
			 _t.parents('.color-box').find('span').removeClass('active');
			 _t.addClass('active');

		});
		$('.in_box').on('tap','.in_explain',function(){
			$(this).next('p').hasClass('disn')?$(this).next('p').removeClass('disn'):$(this).next('p').addClass('disn');
		});

		// 点击底部菜单栏 弹窗
		$('.barClick').on('tap',function(){
			var text = $(this).text();
			var _scrollTop=$(document).scrollTop();
			var dataId=$(this).data('id');
			$(dataId).find('.mask-title span').html(text);
			$('.mask-box').fadeOut();
			$(dataId).parents('.mask-box').fadeIn();
			$('.mui-content').addClass('ovfHiden');
			window.scrollTo(0,_scrollTop);
		});
		// 关闭弹窗
		$('.select-maskbox,.mask-close').on('tap',function(){
			$('.mask-box').fadeOut();
			$('.mui-content').removeClass('ovfHiden');
		});
		$('.mui-icon-collect').parents('.mui-tab-item').off('tap').on('tap',function(){
			if($(this).hasClass('collected')){
				$(this).removeClass('collected');
				mui.toast('取消收藏成功',{ duration:1000, type:'div' }) 
			}else{
				$(this).addClass('collected');
				mui.toast('收藏成功',{ duration:1000, type:'div' }) 
			}
		});
		
	},
	settingClick:function(){
		$('.deck').on('tap','.deck-img',function(){
			$(this).removeClass('active');
			$('.deck-div').addClass('active');
		}).find('.deck-div').on('tap','i',function(){
			$(this).closest('.deck-div').removeClass('active');
			$('.deck-img').addClass('active');
		}).on('tap','ul li',function(){
			$(this).addClass('active').siblings().removeClass('active');
		})
	},
	rule:function(method,a){/*验证规则*/
		switch(method){
			case '*':
			    var r = /[\w\W]+/;
			break;case 'mobile':
			    var r = /^1[3-9]{1}[\d]{9}$/;
			break;case 'phone':
			    return (/^1[3-9]{1}[\d]{9}$/.test(a) || /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(a) || /^([0-9]{3,4})?[0-9]{7,8}$/.test(a));
			break;default:
				var r = /^[+\-]?\d+(\.\d+)?$/;
			break;
		}
		return r.test(a);
	},
	detailContbox :function(){
   		$('.mui-item-contbox').on('tap',function(){
   			$(this).addClass('active').siblings().removeClass('active');
   		})
	},
	nn:function(){
		$('#back_compare').on('tap',function(){
			 localStorage.setItem('h','a');
		})
	}
}
$(function(){
	counter.init();
	mui('.mui-bar').on('tap','a',function(){
		if($(this).attr('href')){
			document.location.href=this.href;
		}
	});
	mui('.mui-scroll-wrapper').scroll({
    	deceleration: 0.0005 //flick 减速系数，系数越大，滚动速度越慢，滚动距离越小，默认值0.0006
    });
	$('.price-imged').on('tap',function(){
		var src="img/detail/slideimg.jpg";
		$(this).find('img').attr('src',src)
	});
	
	
})
