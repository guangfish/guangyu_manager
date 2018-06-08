var newList = {
	init:function(){
		this.filtrate();//筛选新车车型
		this.clickNews();
	},
	filtrate:function(){
		var flag =true;
		// 金融 保险 顾问 问答切换
		$('.mui-table-view-cell').off('tap').on('tap','.mui-navigate-right',function(){
		    var _t = $(this);
		    function selectBox(){
			    var _act = _t.parents('.tl_links').hasClass('mui-active');
			        _t.parents('.tl_links').siblings('.mui-select-box').find('.mui-conceal').each(function(){
			    		$(this).addClass('disn');
			    		addMask();
			        });
			        _t.parents('.new-content-box').find('.new-main-mask').each(function(){
			    		$(this).addClass('disn');
			    		addMask();
			        })
			    if(!_act){
	  		    	showBox(_t);
	  		    	if(_t.hasClass('sBrand') && flag){
	  		    		brand();
	  		    	}
	  		    	else if(_t.hasClass('filtrate') && $('.mui-screen-box').length==0){
	  	    			moreFiltrate();
	  		    	}
	  		    	$('.mui-collapse-content').off('tap').on('tap','.mui-item,.mui-screen-list',function(){  
	  		    		selected(this);	
	  		    		post();
	  		    	});
			    }
		    }
		    selectBox();
		    $('body').off('tap').on('tap','.select-mask',function(){
		    	selectBox();
		    	_t.parents('.tl_links').removeClass('mui-active');
		    })
		    // 显示
		    function showBox(lk) {
		        var _tlbox = null;
		        var _height=document.documentElement.clientHeight;
		        try {
		            _tlbox = $(lk.attr('href'));
		        } catch (e) {
		            console.log(e);
		        }
		        if (_tlbox && _tlbox.length) {
		            _tlbox.removeClass('disn');
		            $('.mui-nav-view').css('top','0');
		            $('.select-mask').removeClass('disn');
		            $('html,body').css({'overflow':'hidden','height':_height});
		        }
		    }
		    // 点击获取mui-select
		    function selected(t){
		    	var _this =$(t);
		    	if(_this.hasClass('mui-item')){
		    		if(_t.parents('.mui-table-view-cell').find('#sort').length == 0){//判断是否为综合排序
		    			if(_this.attr('v') == ''){
		    				_this.parents('.mui-collapse-content').find('.mui-item').removeClass('mui-current');
		    				_this.parents('.mui-table-view-box').find('.mui-item').removeClass('mui-current');
		    			}
		    		}else{
		    			_t.next('input.apt-input').val(_this.attr('v'));
		    			_this.parents('.mui-collapse-content').find('.mui-item').removeClass('mui-current');
		    			var _tit = _this.attr('title');
		    			_t.html(_tit).attr('title', _title);
		    		}
		    		if(_this.hasClass('mui-current')){
		    			_this.removeClass('mui-current');
		    			if(!_this.parents('.mui-collapse-content').find('.mui-item').hasClass('mui-current')){
		    				_this.parents('.mui-collapse-content').find('.mui-item-all').addClass('mui-current');
		    			}
		    		}else{
		    			_this.parents('.mui-table-view-box').find('.mui-unlimited').removeClass('mui-current');
		    			_this.parents('.mui-collapse-content').find('.mui-item-all').removeClass('mui-current');
		    			_this.addClass('mui-current');
		    			_this.parents('.mui-price').next('.custom-price').find('.cur-priceText').val('');
		    			_this.parents('.mui-price').next('.custom-price').find('.price-customBtn').removeClass('active');
		    		}

		    	}
		    	var _title = _t.next().attr('title');
		    	_t.html(_title).attr('title', _title);
		    	_t.addClass('current-style');
		    }		    		    
		});
		// 点击品牌返回按钮
		$('.mui-bar-nav .mui-pull-left,#list .mui-btn-class').off('tap').on('tap',function(){
			var _this = $(this);
			_this.parents('.mui-conceal').addClass('disn');
			$('#sBrand').parents('.tl_links').removeClass('mui-active');
			$('#filtrate').parents('.tl_links').removeClass('mui-active');
			addMask();
		});
		
		// 获取品牌 inner为热门 
		function brand(){
			if(flag){
				var words = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
				var group = {};
				var hotgroup=[];
				$.each(words.split(''), function(index, word) {
					group[word] = [];
				});
				$.ajax({
					url: './data/car-brand.json',
					type: 'get',
					dataType: 'json',
					success:function(data){
						var d = data.data;
						$.each(d,function(i,v){
							var brandName = v.title;
							if($.inArray(v.fletter,words.split('')) !=-1){
								var firstWord = v.fletter;
								group[firstWord].push(v);
							}else{
								hotgroup.push(v);
							}
						});
						var innerHtml = '';
						for(var i in group){
							if(group[i].length) {
								innerHtml += '<div class="mui-list-box">';
								innerHtml += '<div data-group="'+i+'" class="mui-table-view-divider mui-indexed-list-group">'+i+'</div>';
								innerHtml += '<ul>';
								$.each(group[i],function(i,v){
									innerHtml += '<li v="'+v.id+'" title="品牌" class="mui-table-view-cell mui-indexed-list-item mui-item">'+v.title+'</li>';
								});
								innerHtml += '</ul>'
								innerHtml +='</div>';
							}
						}
						var inner = '';
						inner +='<div class="mui-list-box"><div class="mui-unlimited mui-item mui-current" v="" title="不限品牌">不限品牌</div></div>';
						inner += '<div class="mui-list-box">';
						inner += '<div id="mui-hot" data-group="热" class="mui-table-view-divider mui-indexed-list-group">热门品牌</div>';
						inner += '<div class="mui-hot-brand">';
						$.each(hotgroup,function(index,brand){
							inner += '<a class="mui-item" v="'+brand.id+'" title="品牌">'+brand.title+'</a>'
						})
						inner += '</div>';
						inner += '</div>';
						$('.mui-mask-head').find('#mui-hot-list').append(inner);
						$('.mui-mask-head').find('#mui-brand-list').append(innerHtml);
					}
				});
				
				// 品牌mui组件
				mui.ready(function() {
					var item = $('.mui-indexed-list-bar a');
					var header = document.querySelector('header.mui-bar');
					var list = document.getElementById('list');
					//calc hieght
					list.style.height = (document.documentElement.clientHeight - header.offsetHeight) + 'px';
					//create
					window.indexedList = new mui.IndexedList(list);
					var barItemHeight = (($('.mui-indexed-list-bar').height()) / item.length) + 'px';
					item.height(barItemHeight);
					item.css('lineHeight',barItemHeight);
				});

				flag = false;	
			}else{
				return
			}	
		}
		// 点击更多筛选
		$('.mui-more-screening').off('tap').on('tap','.mui-screen-list',function(){
			var _t = $(this);
			var _v = _t.attr('v');
			if(_v == ''){
				_t.parents('ul').find('.mui-screen-list').removeClass('mui-current');
			}else{
				_t.parents('ul').find('.mui-item-all').removeClass('mui-current');
			}
			_t.addClass('mui-current');
		})
		//更多筛选
		function moreFiltrate(){
			$('.mui-more-screening').find('.mui-arg-box').html('');
			$.ajax({
				url: './data/filtrate.json',
				type: 'get',
				dataType: 'json',
				success:function(data){
					var d = data.result;
					var inner = '';
					$.each(d,function(i,v){
						inner += '<div class="mui-screen-box">';
						inner += '<div class="mui-screen-tit">'+v.headline+'</div>';
						inner += '<input class="apt-input" type="hidden" name="'+v.name+'">';
						inner += '<ul>';
						$.each(v.data,function(index,cont){
							inner += '<li><a class=" mui-screen-list" v="'+cont.id+'" title="'+cont.title+'">'+cont.title+'</a></li>';
						})
						inner += '</ul>';
						inner += '</div>';
						inner += '</div>';
					})
					$('.mui-more-screening').find('.mui-arg-box').append(inner);
					$('.mui-more-screening').find('.mui-screen-list').each(function(){
						var _v = $(this).attr('v');
						if(_v == ''){
							$(this).addClass('mui-item-all mui-current');
						}
					})
				}
			});
		}
		// 获取数据
		function getInfor(){
			var infor = {};
			//排序
			var sortValue = $('#sort').next('input.apt-input').val();
			var field=$('#sort').next('input.apt-input').attr('name');
			if(sortValue != ''){
				infor[field] = sortValue;
			}
			//价格
			
			var valueData=[];
			$('.mui-price').find('.mui-current').each(function(){
				var _t = $(this);
				if(_t.attr('v')!=''){
					valueData.push(_t.attr('v'));
					infor.price =valueData.join(',');
				}
			});
			$('.custom-price').find('.cur-priceText').each(function(){
				var _t = $(this);
				if(_t.val()!=''){
					field = _t.attr('name');
					infor[field]=_t.val();
				}
			})
			//品牌
			var brandData=[];
			$('#list').find('.mui-list-box').find('.mui-current').each(function(){
				var _t =$(this);
				if($(this).attr('v')!=''){
					brandData.push($(this).attr('v'));
					infor.brand =brandData.join(',');
				}
			})
			// 更多筛选
			$('.mui-screen-box').each(function(i,p){
				var filtrateData=[];
				$(p).find('.mui-current').each(function(i,_k){
					filtrateData.push($(_k).attr('v'));
				})
				filtrateData = filtrateData.join(',');
				if(filtrateData.indexOf(',') != '-1' || filtrateData !=''){
					infor[$(p).find('input.apt-input').attr('name')]=filtrateData;
				}
			})
			return infor;
		}
		// 提交数据
		function post(){
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
					 innerHtml += '<li class="mui-table-view-cell mui-media pos">\
							<a href="javascript:;">\
								<img class="mui-media-object mui-pull-left" src="img/img1.jpg">\
								<div class="mui-media-body">\
									<h2 class="mui-body-tit">全新JEEP自由侠 180T 智能版</h2>\
									<div class="mui-new-price"><span>参考价:<em class="mui-price-color">22.60 — 43.80万</em></span></div>\
									<div class="mui-text-box">\
										<span class="mui-new-des fl">JEEP自由侠/紧凑型SUV</span>\
										<span class="mui-new-type fr">4款车型</span>\
									</div>\
								</div>\
							</a>\
							<div class="mui-new-panel disn">\
								<div class="mui-panel-box">\
									<h2>1.5升 涡轮增压 136马力</h2>\
									<ul class="mui-panel-cont">\
										<li><a href="new_detail.html"><span>2018款 sDrive18Li 时尚型</span><span class="panel-price">22.60万</span></a></li>\
										<li><a href="new_detail.html"><span>2018款 sDrive18Li 尊享型</span><span class="panel-price">30.60万</span></a></li>\
									</ul>\
								</div>\
								<div class="mui-panel-box">\
									<h2>1.5升 涡轮增压 136马力</h2>\
									<ul class="mui-panel-cont">\
										<li><a href="new_detail.html"><span>2018款 sDrive18Li 时尚型</span><span class="panel-price">22.60万</span></a></li>\
										<li><a href="new_detail.html"><span>2018款 sDrive18Li 尊享型 </span><span class="panel-price">30.60万</span></a></li>\
									</ul>\
								</div>\
							</div>\
						</li>';
					}
					$('.mui-cont-box').find('.mui-table-view').html(innerHtml);
					mui.toast('一共有'+d.length+'款车源',{duration:'long'});
				}
			})
		}
		// 点击确定查看
		$('.mui-screen-btn').on('tap','.mui-btn-inquire',function(){
			post();
			$('#idBox4').addClass('disn');
			$('.mui-navigate-right').parents('.tl_links').removeClass('mui-active');
			addMask();
		})
		// 取消按钮	
		$('.mui-screen-btn').on('tap','.mui-btn-disn',function(){
			$('#idBox4').addClass('disn');
			$('.mui-navigate-right').parents('.tl_links').removeClass('mui-active');
			addMask();

		});
		// 价格自定义
		$('.cur-priceText').on('input propertychange',function(){
			var _t = $(this);
			if($('.cur-priceText').val() != ''){
				$('.mui-price').find('.mui-item').removeClass('mui-current');
				_t.parents('.custom-price').find('.price-customBtn').addClass('active');
			}else if($('.custom-min').find('.cur-priceText').val() == ''&& $('.custom-max').find('.cur-priceText').val() == ''){
				_t.parents('.custom-price').find('.price-customBtn').removeClass('active');
			}	
		});
		$('.custom-price').on('tap','.price-customBtn.active',function(){
			var _t = $(this);
			post();
		});
		// 滚动隐藏
		function mainScroll(){
			var scroll = mui('.scroll-box').scroll();
		    document.querySelector('.mui-scroll-wrapper').addEventListener('scroll', function (e) {
		    	if(scroll.y<-50){
		    		$('.mui-nav-view').css('top','0');
		    	}else{
		    		$('.mui-nav-view').css('top','1.3rem');
		    	}
		    })
		}
		mainScroll();
		function addMask(){
			var _height=document.documentElement.clientHeight;
			$('.select-mask').css('height','100%').addClass('disn');
			$('html,body').css({'overflow':'visible','height':'100%'});
			 $('.mui-nav-view').css('top','1.3rem');
			mainScroll();
		}
	},
	clickNews:function(){
		$('.mui-content').on('tap','.mui-media-box',function(){
			var _t = $(this);
			$(this).parents('.mui-media').find('.mui-new-panel').toggle('40');
		});
	},
	rangeFun:function(){//二手车筛选
		function allRange(op,v,n){
			var va = [];
			for(var i =0;i<v;i++){
				va.push(i);
			}
			va.push('不限');
			var $range = op;
			var trank=function() {
			    var $this = $(this),
			        from = $this.data("from"),
			        to = $this.data("to");
			    if(isNaN(to) && from=='0' || isNaN(from)){
			    	$this.siblings('.range-tit').find('.tip-content').html( "不限" );
			    }else if(!isNaN(from) && isNaN(to)){
			    	$this.siblings('.range-tit').find('.tip-content').html(from + n+"以上" );
			    }
			    else if(from != to && !isNaN(to) ){
			    	$this.siblings('.range-tit').find('.tip-content').html(from + " - " + to + n)
			    }else{
			    	$this.siblings('.range-tit').find('.tip-content').html(to+n)
			    }
			}
			$range.ionRangeSlider({
			    type: "double",
			    values:va,
			    grid: true
			});

			$range.on('change',trank);
		};

		var rAge = $('#range_age');
		var Mileage = $('#range_mileage');
		var rangeCc = $('#range_cc')
		allRange(rAge,12,'年');
		allRange(Mileage,15,'万公里');
		allRange(rangeCc,5,'升');
		rAge.parents('.mui-range').find('.irs-grid-text').each(function(i,v){
			if(i%2 != 0  ){
				$(v).css('opacity',0);
			}
		})
		Mileage.parents('.mui-range').find('.irs-grid-text').each(function(i,v){
			if(i%3 != 0  ){
				$(v).css('opacity',0);
			}
		})
		Mileage.parents('.mui-range').find('.irs-grid-text').last().addClass('grid-text-style')
	}
}
$(function(){
	newList.init();

})
