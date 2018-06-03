<link rel="stylesheet" href="/static/front/css/table.css">
<@model.webheadsearch />
	<div class="page">
		<div class="content">
			<form id="formdata">
				<div class="page-login" style="margin-top: 2rem">
					<div class="list-block inset text-center">
						<ul>
							<li>
								<div class="item-content">
								    <div class="item-media">
										<i class="icon icon-form-name"></i>
									</div>
									<div class="item-inner">
										<div class="item-input">
											<input id="mobile_me" maxlength="11" type="text" class="input_enter"
												placeholder="我的手机号码" name="mobile_me">
										</div>
										<div id="send" class="item-title label" style="width: 1.1rem;">
										    <a href="javascript:void(0);" id="send_btn"
												style="color: #a0a0a0; font-size: 0.8rem;" onclick="del('mobile_me')"><img src="/static/front/img/close.gif"></a>
										</div>
									</div>
								</div>
							</li>
							<li>
								<div class="item-content">
								    <div class="item-media">
										<i class="icon icon-form-name"></i>
									</div>
									<div class="item-inner">
										<div class="item-input">
											<input id="mobile_friend" maxlength="11" type="text" class="input_enter"
												placeholder="我朋友的手机号码" name="mobile_friend">
										</div>
										<div id="send" class="item-title label" style="width: 1.1rem;">
										    <a href="javascript:void(0);" id="send_btn"
												style="color: #a0a0a0; font-size: 0.8rem;" onclick="del('mobile_friend')"><img src="/static/front/img/close.gif"></a>
										</div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div class="content-block">
						<p>
							<a class="button button-big button-fill external"
								data-transition='fade' id="submitlogin" onclick="commit();">提交邀请</a>
						</p>
						<!--<p class='text-center signup'>				        					
								<a href="/search.html"
								class='pull-center external' style="font-size: 0.8rem;">返回</a>								
						</p>-->
					</div>
			</form>
		</div>

		<div id="result" align="center">
		  <div style="color: red;font-size: 0.7rem;">
             <!--邀请朋友使用逛鱼搜索，并通过逛鱼搜索完成一单购买任务后即可得到10元奖励，可通过“订单查询”页面查看奖励情况，奖励会在您申请提现时一起发放。-->
             每邀请一个朋友并通过逛鱼搜索完成一单购买任务后即可得到${reward?if_exists}元奖励
             <br/><br/>第一步：跟好友沟通后，提交我和好友手机号
             <br/>第二步：指导好友并通过逛鱼搜索完成商品购买
             <br/>第三步：等待好友购买的商品核对完毕
             <br/>第四步：奖励到账
             <br/>第五步：在申请提现时连同奖励一起提取
		  </div>
		</div>

	</div>
	
	<script>
	  	var mobile = $.cookie('guangfishmobile');
	  	if(mobile){
	  	  $("#mobile_me").val(mobile);
	  	}	  
	  	
	  	function del(id) {
          $('#'+id).val("");
        }	
	</script>

	<script>	
	    function isPoneAvailable(mobile) {  
          var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;  
          if (!myreg.test(mobile)) {  
              return false;  
          } else {  
              return true;  
          }  
      }
	    
	    function commit(){
	      var mobile_me = $('#mobile_me').val();
	      var mobile_friend = $('#mobile_friend').val();
	      if(!mobile_me){
	        Core.Dialog.msg("请输入我的手机号！");
	        return;
	      }else{
	        if(mobile_me.toString().length!=11){
			  Core.Dialog.msg("我的手机号位数不正确！");
			  return;
			}
	        var myreg=/^[1][3,4,5,7,8][0-9]{9}$/; 
	        if (!myreg.test(mobile_me)) {  
	          Core.Dialog.msg("我的手机号格式不正确");
              return;  
            } 	        
	      }
	      
	      if(!mobile_friend){
	        Core.Dialog.msg("请输入朋友的手机号！");
	        return;
	      }else{
	        if(mobile_friend.toString().length!=11){
			  Core.Dialog.msg("朋友的手机号位数不正确！");
			  return;
			}
	        var myreg=/^[1][3,4,5,7,8][0-9]{9}$/; 
	        if (!myreg.test(mobile_friend)) {  
	          Core.Dialog.msg("朋友的手机号格式不正确");
              return;  
            } 	        
	      }
	      
	      if(mobile_me==mobile_friend){
	        Core.Dialog.msg("不能自己邀请自己");
            return;
	      }	     
	      
	      save(mobile_me,mobile_friend);	      	      	      	      
	    }

		function save(mobile_me,mobile_friend) {
				$
						.ajax({
							type : "post",
							url : "/api/saveinvitation",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
								"mobileme" : ""+mobile_me,
								"mobilefriend" : ""+mobile_friend
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(JSON.stringify(data) != "{}"){								  		  
                                   if(data.ret.result == "3"){
                                     Core.Dialog.msg("邀请的用户已经在使用逛鱼搜索或已被邀请或已邀请过别人",5000);
                                   }                               
                                   if(data.ret.result == "0"){
                                     Core.Dialog.msg("邀请好友成功，请细心帮助好友使用逛鱼搜索，当好友通过逛鱼搜索完成购买任务时，您才可以得到奖励哦！",6000);
                                   }
								}				
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
		}
	</script>
<@model.webendsearch />
