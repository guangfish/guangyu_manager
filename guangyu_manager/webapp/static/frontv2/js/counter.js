var wid =0,
widAging=0;
$('.mui-input-range').on('input','input',function(){
	var _valued=$(this).parent().siblings('.valued');
    _valued.find('div').removeClass('active');
    _valued.find('.k'+this.value).addClass('active');  
	$(this).parents('.mui-input-range').find('.range_pay').css('width',100/40*(this.value-30)+'%');
	$(this).parents('.mui-input-range').find('.range_aging').css('width',100/48*(this.value-12)+'%');
});
$('.nav-tid').on('click','div',function(){
	$('.nav-tid>div').removeClass('active');
	$(this).addClass('active');
	$('.k-details>div').addClass('dis');
	$('.'+$(this).data('k')).removeClass('dis');

})

// 必要花费
mui.init({
    swipe: false,
});

$('.mui-exp-item').off('tap').on('tap','div',function(){
	var t= $(this);
	var dataId= t.data('id');
	var itemType=$(this).find('.item-type').data('type');
	if(t.children('.icon_exp').length>0){
		$('.exp-pop-box').addClass('disn');
		$(dataId).removeClass('disn');
		$(dataId).find('p').removeClass('selected');
		$(dataId).find('p').each(function(i,v){
			if($(v).data('type') == itemType){
				$(this).addClass('selected');
			}
		})
		mui('.mui-off-canvas-wrap').offCanvas('show');
	}
	$('.exp-pop-cont').off('tap').on('tap','p',function(){
		$(this).parent('.exp-pop-cont').find('p').removeClass('selected');
		$(this).addClass('selected');
		t.find('.item-type').data('type',$(this).data('type')).html($(this).text());
		// 点击提交获取价格
		$.post('', {param1: $(this).data('type')}, function(data) {
			t.find('.exp-price').html(data.price);
			allCost();
		});
	});
});
$('.pop-tit-close').on('tap',function(){
	mui('.mui-off-canvas-wrap').offCanvas('close');
	$('.exp-pop-box').addClass('disn');
});
 //主界面和侧滑菜单界面均支持区域滚动；
mui('#offCanvasSideScroll').scroll();
mui('#offCanvasContentScroll').scroll();


//商业保险
// 点击本地存储价格
$('.in_checked').on('tap',function(){
	var exPrice = $(this).siblings('div').find('.exp-price');
	if($(this).hasClass('in_check')){
		$(this).removeClass('in_check');
		exPrice.html(localStorage.getItem("b"));
	}else{
		$(this).addClass('in_check');
		localStorage.setItem("b",exPrice.html());
		exPrice.html('0');
	}
	allCost();
});
function allCost(){
	var sum =0;
	$('.exp-price').each(function(){
		sum += Number($(this).html());
	});
	$('.mui-all-cost').find('span').html(sum);
}
allCost();

// 计算机页面 点击跳转 
$('.typeChecked').on('tap',function(){
	var counterId=$(this).data('id');
	window.location.href = 'car_type.html?id='+counterId;
});




