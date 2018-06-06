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
											<input id="mobile" maxlength="11" type="text" class="input_enter"
												placeholder="请输入手机号码" name="mobile">
										</div>
										<div id="send" class="item-title label" style="width: 2rem;">
										    <a href="javascript:void(0);" id="send_btn"
												style="color: #a0a0a0; font-size: 0.8rem;" onclick="del('mobile')"><img src="/static/front/img/close.gif"></a>
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
											<input id="alipay" maxlength="50" type="text" class="input_enter"
												placeholder="请输入支付宝账号" name="alipay">
										</div>
										<div id="alipayopt" class="item-title label" style="width: 2rem;">										    
											<a href="javascript:void(0);" id="send_btn"
												style="color: #a0a0a0; font-size: 0.8rem;" onclick="copyMobile()">复制</a>
										</div>
									</div>
								</div>
							</li>
							<li>
								<div class="item-content">
									<div class="item-media">
										<i class="icon icon-form-email"></i>
									</div>
									<div class="item-inner">
										<div class="item-input">
											<input id="smscode" class="input_enter" type="text" placeholder="输入短信验证码"
												name="smscode">																																				
										</div>
										<div id="send" class="item-title label" style="width: 2rem;">
											<a href="javascript:void(0);" id="send_btn"
												style="color: #a0a0a0; font-size: 0.8rem;" onclick="sendsmscode()">发送</a>
										</div>
										<div id="timer" class="item-title label"
											style="width: 3rem; display: none; color: orangered; font-size: 0.8rem;"></div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div class="content-block">
						<p>
							<a class="button button-big button-fill external"
								data-transition='fade' id="submitlogin" onclick="commit();">申请提现</a>
						</p>
						<p class='text-center signup'>
								<!--<a href="searchorder.html" class='pull-left external'
								style="font-size: 0.8rem;">订单查询</a>
								-->								
								<a href="/searchorder.html"
								class='pull-center external' style="font-size: 0.8rem;">返回</a>								
						</p>
					</div>
			</form>
		</div>

		<div id="result" align="center">
		  <div style="color: red;font-size: 0.7rem;">提现须知：
		  <br/>1.手机号是您的唯一身份标识，用于匹配你的订单
		  <br/>2.支付宝账号用于逛鱼客服人员给您打款，请务必输入您的真实支付宝账号
		  <br/>3.提现成功后请注意查收支付宝，我们的客服会在2小时内给您打款！
		  </div>
		</div>
	</div>
	
	<script>
	  	var mobile = $.cookie('guangfishmobile');
	  	if(mobile){
	  	  $("#mobile").val(mobile);
	  	}	
	  	
	  	function del(id) {
          $('#'+id).val("");
          $("#alipayopt").css("width","2rem");
          $('#alipayopt').html('<a href="javascript:void(0);" id="send_btn" style="color: #a0a0a0; font-size: 0.8rem;" onclick="copyMobile()">复制</a>');
        }	
	</script>

	<script>	
	    function copyMobile() {  
          var mobile = $('#mobile').val();
          if(mobile){
            $('#alipay').val(mobile);
            $("#alipayopt").css("width","2rem");
            $('#alipayopt').html("<a href='javascript:void(0);' id='send_btn' style='color: #a0a0a0; font-size: 0.8rem;' onclick='del(\"alipay\")'><img src='/static/front/img/close.gif'></a>");
          }        
        }
        
      
	    function isPoneAvailable(mobile) {  
          var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;  
          if (!myreg.test(mobile)) {  
              return false;  
          } else {  
              return true;  
          }  
        }
	    
	    function commit(){
	      var mobile = $('#mobile').val();
	      var alipay = $('#alipay').val();
	      var smscode = $('#smscode').val();	   
	      if(!mobile){
	        Core.Dialog.msg("请务必输入正确的手机号码，用于接收短信验证码！",5000);
	        return;
	      }else{
	        if(mobile.toString().length!=11){
			  Core.Dialog.msg("手机号位数不正确！");
			  return;
			}
	        var myreg=/^[1][3,4,5,7,8][0-9]{9}$/; 
	        if (!myreg.test(mobile)) {  
	          Core.Dialog.msg("请务必输入正确的手机号码，用于接收短息验证码！",5000);
              return;  
            } 	        
	      }
	      if(!alipay){
	        Core.Dialog.msg("请务必输入正确的支付宝账号，用于收款！");
	        return;
	      }

	      if(!smscode){
	        Core.Dialog.msg("请输入短信验证码！");
	        return;
	      }else{
	        if(smscode.toString().length!=5){
			  Core.Dialog.msg("短信验证码输入不正确！");
			  return;
			}
	      }	      
	      
	      save(mobile,alipay,smscode);	      	      	      	      
	    }

		function save(mobile,alipay,smscode) {
		        $('#submitlogin').removeAttr('onclick');
				$
						.ajax({
							type : "post",
							url : "/api/orderdraw",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
								"mobile" : ""+mobile,
								"alipay" : ""+alipay,
								"smscode" : ""+smscode
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(JSON.stringify(data) != "{}"){
								  if(data.ret.result.status=="0"){
								    Core.Dialog.msg("提现申请成功,提现商品"+data.ret.result.productNums+"件,返利金额"+data.ret.result.fanli+"元,邀请奖励"+data.ret.result.reward+"元,提现总金额"+data.ret.result.money+"元,请注意支付宝查收！",10000);
								    //$("#mobile").val("");
								    //$("#alipay").val("");
								    $("#smscode").val("");
//								    document.getElementById('num').src='/getCode?'+(new Date()).getTime();
//								    $('#result').html("<br/><br/><font color='red'>提现申请成功了,提现商品"+data.ret.result.productNums+"件,返利金额"+data.ret.result.fanli+"元,邀请奖励"+data.ret.result.reward+"元,提现金额"+data.ret.result.money+"元,请注意支付宝查收！</font>");
								  }					  
								  if(data.ret.result.status=="6"){
								    Core.Dialog.msg("短信验证码已失效，请重新发送!");
								    $("#smscode").val("");
								  }
								  if(data.ret.result.status=="7"){
								    Core.Dialog.msg("短信验证码验证失败!");
								    $("#smscode").val("");
								  }
								  if(data.ret.result.status=="8"){
								    Core.Dialog.msg("亲，已经没有可提现的订单了，赶紧去看看是否没有录入已完成购买商品的订单号！",5000);
								    //$("#mobile").val("");
								    //$("#alipay").val("");
								    $("#smscode").val("");
								  }
								}
								$('#submitlogin').attr('onclick','commit();');
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
		}
		
		function sendsmscode() {
			var mobile = $('#mobile').val();
			if (!mobile) {
				Core.Dialog.msg("请输入手机号码！");
				return;
			}
				$
						.ajax({
							type : "post",
							url : "/api/getSmsCode",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							async:false,
							data : JSON.stringify({
								"mobile" : mobile
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data);
								if(data.ret.result.status=="4"){
								    Core.Dialog.msg("请等待2分钟后再次发送短信验证码");							    
								}
								if(data.ret.result.status=="0"){
								    Core.Dialog.msg("短息验证码发送成功，短信验证码2分钟之内有效，过期后请重新发送！",5000);
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
