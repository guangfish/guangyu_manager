var inputval;
var nameval;
$('.selectSex label').on('tap',function(){
	$(this).prev().attr('checked',true).siblings().removeAttr('checked');
});
// 点击按钮提交
$('.confirmBtn').on('tap',function(){	
	$('.selectSex').find('input').each(function(i,v){
		if(typeof($(this).attr('checked')) != 'undefined'){
			inputval = $(this).val();
		}
	})	
	nickName();
});
$('.resetBtn').on('tap',function(){
	nameval=$('.nickname').val('');
	$('.mui-sex').removeAttr('checked');
})
function nickName(){
    nameval=$('.nickname').val();	
	if(nameval == ''){
		 mui.toast('昵称不能为空') ;
		 
	}else{
		settingPost();
	}
}
// 设置昵称
function settingPost(){
	var infor = {};
	infor[inputval] = inputval;
	infor[nameval] = nameval;
    $.post('', {param1: infor}, function(data){
     	  if(data.nickname == nameval){
     	  		mui.toast('昵称已被占用');
     	  }else{
     	  		mui.alert('修改成功',' ','确认');
     	  }
     });
}
