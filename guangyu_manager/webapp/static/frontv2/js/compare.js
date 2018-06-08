var typeCompare = {
	init:function(){
		this.carType();//车型添加
		this.counterType();
	},
	carType:function(){//车型添加
		$('.mui-compare-item').on('tap','.com_close',function(){
			$(this).parents('.mui-compare-item').html('<a class="mui-compare-add" href="javascript:void(0);"><i></i></a>');
			// 删除后再次绑定事件
			$('.mui-compare-add').on('tap',function(){
				addTap($(this));
			});
			
		});
		// 点击车型品牌 改变样式
		$('.mui-table-view-box').on('tap','.mui-item',function(){
			$(this).parents('.mui-table-view-box').find('.mui-item').removeClass('mui-current');
			$(this).addClass('mui-current');
		});
		// 点击添加
		$('.mui-compare-add').off('tap').on('tap',function(){
			addTap($(this));
		});
		

		function addTap(t){	
			var addArr = [];
			var t = t;
			var selectCont=$('.mui-control-content .mui-table-view');
			// 隐藏当前的box
			t.parents('.compare-box').addClass('disn').siblings().removeClass('disn');
			// 获取'添加页面'的id
			$('.mui-compare-cont').find('.mui-compare-item').each(function(){
				if($(this).find('span').hasClass('typeChecked') >0){
					var typeChecked=$(this).find('.typeChecked').data('id');
					addArr.push(typeChecked.toString());
				}
			});
			selectCont.find('p').removeClass('typeSelected').find('span').html('');
			// 查找获取的车型中有没有已选择的车型   
			$.each(addArr,function(i,v){
				selectCont.find('p').each(function(k,z){
					if($(z).data('id') == v){
						$(this).addClass('typeSelected').find('span').html('(已选择)');
					}
				})
			});
			// 点击选择车型
			selectCont.off('tap').on('tap','p',function(){
				if(!$(this).hasClass('typeSelected')){
					var text = $(this).text();
					$(this).parents('.compare-box').addClass('disn').siblings().removeClass('disn');
					t.parents('.mui-compare-item').html('<span data-id="'+$(this).data('id')+'" class="typeChecked">'+text+'</span><i class="com_close"></i>');
				}
				else{
					mui.toast('已选择'); 
				}
			});
			// 获取品牌
			typeCompare.typeBrand();
			// 获取车型数据
			typeCompare.typeAjax(t,addArr);	
		}
		
		$('.left-close').on('tap',function(){
			$(this).parents('.compare-box').addClass('disn').siblings().removeClass('disn')
		})
		mui('.mui-scroll-wrapper').scroll({
			deceleration: 0.0006, //flick 减速系数，系数越大，滚动速度越慢，滚动距离越小，默认值0.0006;
			bounce: false,
			 indicators: true
		});
	},
	typeAjax:function(t,a){
		var t = t,
			addArr = a;
			// 点击品牌
		$('#item2').off('tap').on('tap','.mui-item',function(){
			$('.cartypePop-list-box').html('');
			$('.cartypePop-title').find('.mui-title').html('选择车系');
			$('.mui-cartypePop,.mask-cartypePop').removeClass('disn');
			$('.carSeries-close').on('tap',function(){
				$('.mui-cartypePop,.mask-cartypePop').addClass('disn');
			})
			var it = $(this);
			//获取车系数据
			function carSeries(){
				$.ajax({
					url: './data/car-brand-2.json',
					type: 'get',
					dataType: 'json',
					data: {param1: it.attr('v')},
					success:function(data){
						var carSeries='';
						var s = data.List;
						$.each(s,function(i,v){
								carSeries += '<div class="cartypePop-list-box">'
							    carSeries += '<div class="cartypePop-list-title">'+v.N+'</div>';
							    carSeries += '<ul>';
							$.each(v.List,function(j,k){
							    carSeries +='<li class="cartypePop-item" v='+k.I+' data-text="'+k.N+'">'+k.N+'</li>';
							});	
								carSeries += '</ul>';	
								carSeries += '</div>'
						});
						$('.cartypePop-cont').html(carSeries);

						//返回获取车型
						$('.cartypePop-item').off('tap').on('tap',function(){
							var seriesBack = $(this).parents('.mui-cartypePop').find('.mui-pull-left');
							seriesBack.removeClass('carSeries-close').addClass('cartypePop-close').off('tap');
							backFunc($(this));
						});
						
					}
				})
			}
			carSeries();
			function backFunc(n){ //返回获取车系数据
				var $this =n
				cartype($this);//获取车型数据
				$('.cartypePop-title').find('.mui-title').html('选择车型');
				$('.cartypePop-close').on('tap',function(){
					carSeries($this);
					$(this).removeClass('cartypePop-close').addClass('carSeries-close');
					$('.carSeries-close').on('tap',function(){
						$('.mui-cartypePop,.mask-cartypePop').addClass('disn');
					})
				});
			}
		});
		//获取车型数据
		function cartype(k){
			var k = k;
			$.ajax({
				url: './data/car-brand-3.json',
				type: 'get',
				dataType: 'json',
				data: {param1: k.attr('v')},
				success:function(data){
					var d = data.List;
					var cartypeInner='';
					$.each(d,function(i,v){
							cartypeInner += '<div class="cartypePop-list-box">'
						    cartypeInner += '<div class="cartypePop-list-title">'+v.N+'</div>';
						    cartypeInner += '<ul>';
						$.each(v.List,function(j,k){
						    cartypeInner +='<li class="cartypePop-item cartype-text" v='+k.I+' data-text="'+k.N+'"><span>'+k.N+'</span><p class="cartypePop-price">价格<span>19.8万</span></p></li>';
						});	
							cartypeInner += '</ul>';	
							cartypeInner += '</div>'
					});
					$('.cartypePop-cont').html(cartypeInner);
					// 获取最终车系车型添加到页面上
					$('.cartype-text').off('tap').on('tap',function(){
						var $this = $(this);
						if($('.compare-box').length>0){
							var dataText =k.attr('data-text')+' '+$this.attr('data-text');
							$('.mask-cartypePop,.mui-cartypePop').addClass('disn');							
							$this.parents('.compare-box').addClass('disn').siblings().removeClass('disn');
							if($.inArray($this.attr('v'), addArr) == -1){
								t.parents('.mui-compare-item').html('<span data-id="'+$this.attr('v')+'" class="typeChecked">'+dataText+'</span><i class="com_close"></i>');
							}else{
								mui.toast('已添加对比,请重新选择');
							}
						}else{
							//计算机页面获取车型的ID
							$this.attr('v');
						}
					});
					
					
				}
			})
		}
		$('.mask-cartypePop').on('tap',function(){
			$('.mui-cartypePop,.mask-cartypePop').addClass('disn');
		})
	},
	typeBrand:function(){ //获取车型品牌数据
		var flag=true;
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
					$('#mui-hot-list').append(inner);
					$('#mui-brand-list').append(innerHtml);
				}
			});
			flag = false;	
		}else{
			return
		}
		
		// 品牌
		var list = document.getElementById('list');
		var item = $('.mui-indexed-list-bar a');
		_height= (document.documentElement.clientHeight-$('.car-type-box').outerHeight()-$('header.mui-bar').outerHeight()) + 'px';
		//calc hieght
		list.style.height = _height;
		//create 查找字母的高度
		window.indexedList = new mui.IndexedList(list);
		$('.mui-indexed-list-bar').height(_height);
		$('.mui-indexed-list-inner').height(_height);
		var barItemHeight = (($('.mui-indexed-list-bar').height() - 40) / item.length) + 'px';
		item.height(barItemHeight);
		item.css('lineHeight',barItemHeight);
	},
	counterType:function(){
		// 计算机页面获取车型ID
		function getUrlParam(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); 
            var r = window.location.search.substr(1).match(reg);  
            if (r != null) return unescape(r[2]); return null; 
        }
        var a = getUrlParam('id');
        $('.mui-table-view-cell p').each(function(i,v){
        	var t = $(this);
        	if($(v).data('id') == a){
        		t.addClass('typeSelected').find('span').html('(已选择)');
        	}
        	t.on('tap',function(){
        		if($(this).hasClass('typeSelected')){
        			mui.toast('已选择'); 
        		}else{
        			$(this).data('id');
        			console.log($(this).data('id'))
        		}
        	})
        	
        });
        if($('.mui-car-type').length>0){
        	typeCompare.typeBrand();
        	typeCompare.typeAjax();
        }
        
	}
}
$(function(){
	typeCompare.init();
					
})

