
	/**
	 * 上下滑动使用overflow:hidden;左右滑动使用marget方法;
	 * */
mui.init();
$('.deck').on('tap', '.deck-img', function() {
	$(this).removeClass('active');
	$('.deck-div').addClass('active');
}).find('.deck-div').on('tap', 'i', function() {
	$(this).closest('.deck-div').removeClass('active');
	$('.deck-img').addClass('active');
}).on('tap', 'ul li', function() {
	$(this).addClass('active').siblings().removeClass('active');
})
var dArr = [];
function DArr(){
	$('.main-details').each(function(i, p) {
		$(p).find("[data-idg]").each(function(j, k) {
			if(i == 0) {
				dArr[j] = [];
			}
			dArr[j].push($(k).data('idg'))
		})
	})
}
$(".kt").on('click', 'div', function() {
	var _i = $(this).find('i');
	var checked;
	_i.hasClass('active') ? (_i.removeClass('active'),checked=0) : (_i.addClass('active'),checked=1);
	DArr();/*获取darr*/
	samed(dArr, $(this).data('dt'),checked);
})
/*点击关闭*/
$('.closed').on('tap', function() {
	var _kt=$(this).closest('.kt');
	$('.main-details').eq(_kt.index()).remove();
	_kt.remove();
	/*重新绑定方法*/
	wDetails();
	marget('close'); /*重新注册*/
	DArr();/*重新获取darr*/
	var _in=[];
	$('.bkposition').find('.active').parent().each(function(i,p){
		_in.push($(p).data('dt'));
	})
	if($.inArray('nosame',_in)!=-1){
		$('.nosame').removeClass('nosame');
		samed(dArr, 'nosame',1);
	}
	if($.inArray('same',_in)!=-1){
		$('.dis').removeClass('dis');
		samed(dArr, 'same',1);
	}
	if($.inArray('no',_in)!=-1){
		$('.dis').removeClass('ndis');
		samed(dArr, 'no',1);
	}
})
function samed(dArr, str,check) {
	console.log(check,dArr)
	$.each(dArr, function(i, p) {
		var same = 1;
		$.each(p, function(j, k) {
			if(j != 0) {
				if(p[0] != k) {
					same = 0;
				}
			}
		})
		if(!same && str == 'nosame') { /*差异高亮*/
			$('.main-details').each(function(z, v) {
				var _thisIdg = $(v).find('[data-idg]').eq(i);
				if(check){
					_thisIdg.addClass('nosame')
				}else{
					_thisIdg.removeClass('nosame')
				}
			})
		} else if(same == 1 && str == 'same' && p[0] != '') { /*相同隐藏*/
			$('.main-details').each(function(z, v) {
				var _thisIdg = $(v).find('[data-idg]').eq(i);
				if(z == 0) {
					var _mainHeadDiv = $('.main-head').children('div').eq(_thisIdg.index());
					if(check){
						_mainHeadDiv.addClass('dis')
					}else{
						_mainHeadDiv.removeClass('dis')
					}
				}
				if(check){
					_thisIdg.addClass('dis');
				}else{
					_thisIdg.removeClass('dis')
				}
			})
		}else if(same == 1 && str == 'no' && p[0] == ''){/*隐藏暂无*/
			$('.main-details').each(function(z, v) {
				var _thisIdg = $(v).find('[data-idg]').eq(i);
				if(z == 0) {
					var _mainHeadDiv = $('.main-head').children('div').eq(_thisIdg.index());
					if(check){
						_mainHeadDiv.addClass('ndis')
					}else{
						_mainHeadDiv.removeClass('ndis')
					}
				}
				if(check){
					_thisIdg.addClass('ndis');
				}else{
					_thisIdg.removeClass('ndis')
				}
			})
		}
	})
}
/*位置fixed*/
function from_top() {
	var  miFixed;
	$('.middle').each(function(i, p) {
		var top = ($('.middle').eq(i).offset().top - $(document).scrollTop()-44)/37.5;
		if(top <= 2.5) {
			$('.middle').removeClass('middle-fixed');
			$(this).last().addClass('middle-fixed');
			miFixed =$(this).attr('id');
		}
	});
	$('.deck').find('ul li').each(function(){
		var deckHref=$(this).find('a').attr('href');
		$(this).removeClass('active');
		if(deckHref.replace("#","") == miFixed){
			$(this).addClass('active');
		}
	})

}
//from_top();
$(window).scroll(function() {
	$('.middle').each(function(i, p) {
		var top = ($('.middle').eq(i).offset().top - $(document).scrollTop()-44)/37.5;
		if(top >= 2.5) {
			$('.middle').eq(i).removeClass('middle-fixed');
		}
	});
	from_top();
});


/*判断box-ged长度*/
function wDetails() {
	$('.box-ged').css('width', $('.main-details').length * 3.24 + 'rem');
}
wDetails();

var containerObj = $('.details-box')[0];
var boxGet = $('.box-ged')[0];
var box = $('.main-details');
var startX = 0,
	/*开始坐标x*/
	startY = 0,
	/*开始坐标y*/
	moveX = 0,
	/*移动坐标x*/
	moveY = 0,
	/*移动坐标 y*/
	distanceX = 0,
	/*距离坐标*/
	currentX = 0,
	aspect = 0,
	/*方向锁定*/
	timer = 0; /*滚动次数*/
function marget(mr) { /*手势左右滑动(mr---close:是否删除,statr:是否是第一次)*/
	mui(document).off();
	var maxLeft = 0,
		maxRight = containerObj.offsetWidth - boxGet.offsetWidth;
	var half = $('.main-details').length * 1.62 < -1 * parseFloat($('.box-ged').css('margin-left')) / parseFloat($('html').css('font-size')); /*删除的是否是一半选择向左还是向右*/
	if($('.main-details').length * 3.24 < 7.52) {
		$('.box-ged').animate({
			'margin-left': '0'
		})
		return;
	} else {
		if(mr != 'statr') {
			if(half) {
				$('.box-ged').animate({
					'margin-left': maxRight
				})
			} else {
				$('.box-ged').animate({
					'margin-left': 0
				})
			}
		}
	}
	/*移动方法*/
	var setTranslate = function(x) {
		//boxGet.style.marginLeft = x + 'px';
		$('.box-ged').each(function(){
			$(this).css('margin-left',x);
		})
	}

	function d1(e) { /*获取开始坐标*/
		startX = e.detail.center.x;
		startY = e.detail.center.y;
		aspect = 1; /*允许滑动*/
		timer = 0; /*滚动次数*/
		$('.box-ged').stop(); /*停止动画*/
	}

	function d2(e) { /*移动中*/
		moveX = e.detail.center.x;
		moveY = e.detail.center.y;
		distanceX = moveX - startX;
		distanceY = moveY - startY;
		var x = currentX + distanceX;
		if(Math.abs(distanceY) > Math.abs(distanceX) && distanceY > 0 || Math.abs(distanceY) > Math.abs(distanceX) && distanceY < 0) { /*锁定方向判断是否上下滑动*/
			timer++;
			if(timer == 1) {
				aspect = 0; /*锁定方向*/
			}
		} else if(Math.abs(distanceX) > Math.abs(distanceY) && distanceX > 0 || Math.abs(distanceX) > Math.abs(distanceY) && distanceX < 0) {
			timer++;
			if(timer == 1) {
				aspect = 1; /*锁定方向*/
				$('body').css({'overflow':'hidden'}); /*关闭上下滚动*/
			}
		}
		if(!aspect) {
			return;
		}
		if(mr == 'close') {
			if(half) { /*是否是后半部分删除*/
				x = maxRight;
				currentX = maxRight;
			}
			mr = 0; /*只触发一次*/
		} else {
			if(x > maxLeft + 100) {
				x = maxLeft + 100;
			} /*加一百为抵抗边界*/
			if(x < maxRight - 100) {
				x = maxRight - 100;
			} /*加一百为抵抗边界*/
		}
		if(moveX > 10) {
			setTranslate(x);
		}
	}

	function d3(e) {
		if(!aspect) {
			return; /*锁定反向完成*/
		}
		currentX = currentX + distanceX;
		if(currentX > 0) {
			currentX = 0;
			$('.box-ged').animate({
				'margin-left': '-1'
			}) /*抵抗100(-1是因为有一个1px的边框)*/
		}
		if(currentX < maxRight) {
			currentX = maxRight;
			$('.box-ged').animate({
				'margin-left': maxRight
			}) /*抵抗-100*/
		}
		//setTranslate(currentX);/*原来的位移*/
		aspect = 0; /*恢复使方向可选*/
		$('body').css('overflow', 'auto'); /*开启上下滚动*/
	}
	mui(document).on('dragstart', 'body', d1);
	mui(document).on('drag', 'body', d2);
	mui(document).on('dragend', 'body', d3);
}
marget('statr'); /*初始化左右滚动*/

