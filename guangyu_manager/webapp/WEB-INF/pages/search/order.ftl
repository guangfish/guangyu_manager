<@model.webheadsearch />
	<div class="page">
		<div class="content">
			<form id="formdata">
				<div class="page-login" style="margin-top: 2rem">
					<div class="list-block inset text-center">
						<ul>
							<li>
								<div class="item-content">
									<div class="item-inner">
										<div class="item-input">
											<input id="orderid" maxlength="18" type="text" class="input_enter"
												placeholder="请粘贴购买的商品订单号" name="order_id">
										</div>
										<div id="send" class="item-title label" style="width: 1.2rem;">
										    <a href="javascript:void(0);" id="send_btn"
												style="color: #a0a0a0; font-size: 0.8rem;" onclick="del('orderid')"><img src="/static/front/img/close.gif"></a>
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
											<input id="mobile" maxlength="11" type="text" class="input_enter"
												placeholder="请输入您的手机号码" name="mobile">
										</div>
										<div id="send" class="item-title label" style="width: 1.2rem;">
										    <a href="javascript:void(0);" id="send_btn"
												style="color: #a0a0a0; font-size: 0.8rem;" onclick="del('mobile')"><img src="/static/front/img/close.gif"></a>
										</div>
									</div>
								</div>
							</li>
							<!-- 暂时屏蔽掉
							<li>
								<div class="item-content">
									<div class="item-media">
										<i class="icon icon-form-email"></i>
									</div>
									<div class="item-inner" style="padding:0">
										<div class="item-input">
											<input id="vcode" maxlength="5" class="input_enter" type="text" placeholder="输入验证码"
												name="codeimage">
										</div>
										<div class="item-title label" style="width:100px;height:35px;">
											 <img id="num" height="35" src="/getCode?%27+(new%20Date()).getTime()" onclick="document.getElementById('num').src='/getCode?'+(new Date()).getTime()"  alt="换一换" title="换一换" class="codeimage">
										</div>
										
									</div>
								</div>
							</li>
							-->
						</ul>
					</div>
					<div class="content-block">
						<p>
							<a class="button button-big button-fill external"
								data-transition='fade' id="submitlogin" onclick="commit();">提交订单</a>
						</p>
						<p class='text-center signup'>
						        <!--<a href="searchorder.html" class='pull-left external'
								style="font-size: 0.8rem;">查订单</a>
								-->
								<!--<a href="/search.html"
								class='pull-center external' style="font-size: 0.8rem;">继续搜返利</a>-->							
						</p>
					</div>
			</form>
		</div>

		<div id="result" align="center">
		  <font style="font-size: 0.7rem;color:red">提示：刚录入的订单号，最快1分钟、最迟次日可查</font>
		</div>
	</div>
	
	<script>
	  	var mobile = $.cookie('guangfishmobile');
	  	if(mobile){
	  	  $("#mobile").val(mobile);
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
	      var orderid = $('#orderid').val();
	      var mobile = $('#mobile').val();
	      var vcode = $('#vcode').val();
	      if (!orderid) {
	        Core.Dialog.msg("请粘贴或输入订单号！");
	        return;
	      }else{
	        if(orderid.toString().length==18 || orderid.toString().length==11){			  
			}else{ 
			  Core.Dialog.msg("订单号位数不正确！");
			  return; 
			}
	      }
	      if(!mobile){
	        Core.Dialog.msg("请输入手机号！");
	        return;
	      }else{
	        if(mobile.toString().length!=11){
			  Core.Dialog.msg("手机号位数不正确！");
			  return;
			}
	        var myreg=/^[1][3,4,5,7,8][0-9]{9}$/; 
	        if (!myreg.test(mobile)) {  
	          Core.Dialog.msg("请输入正确的手机号码");
              return;  
            } 	        
	      }  
	      //暂时屏蔽掉  
	      //if(!vcode){
	      //  alert("请输入验证码！");
	      //  return;
	      //}else{
	      //  if(vcode.toString().length!=5){
		  //  alert("验证码输入不正确！");
		  //	  return;
		  //	}
	      //}
	      
	      var mobile_cookie = $.cookie('guangfishmobile');
	  	  if(!mobile_cookie){
	  	    $.cookie('guangfishmobile', mobile, { expires: 365, path: '/',domain:'${cookieDomain?if_exists}'});
	  	  }	      
	      
	      save(orderid,mobile,vcode);	      	      	      	      
	    }

		function save(orderid,mobile,vcode) {
				$
						.ajax({
							type : "post",
							url : "/api/ordersave",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
								"orderid" : ""+orderid,
								"mobile" : ""+mobile,
								"vcode" : ""+vcode
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(JSON.stringify(data) != "{}"){
								  if(data.ret.result=="0"){
								    Core.Dialog.msg("订单号提交成功，请收货后，去淘宝/京东【确认收货】后方可提现",8000);								    
								    var orderSubmitNotice = $.cookie('ordersubmitnotice');
								    if(!orderSubmitNotice){
								      Core.Dialog.msg("订单号提交成功，刚录入的订单号，最快1分钟、最迟次日可查<a href=\"searchorder.html\">点我查订单</a>",9000);
								      $.cookie('ordersubmitnotice', 'ordersubmitnotice', { expires: 7, path: '/',domain:'${cookieDomain?if_exists}'});
								    }
								    $("#orderid").val("");
								    //$("#mobile").val("");
								    //暂时屏蔽掉
								    $("#vcode").val("");								    
								  }
								  
								  if(data.ret.result=="-1"){
								    Core.Dialog.msg("该订单号已存在，请勿重复提交!",3000);
								    //alert("该订单号已存在，请勿重复提交!");
								    $("#orderid").val("");
								    //暂时屏蔽掉
								    $("#vcode").val("");
								  }
								  
								  if(data.ret.result=="4"){
								    $('#result').html("验证码错误");
								    //暂时屏蔽掉
								    $("#vcode").val("");								    
								  }
								}
								//暂时屏蔽掉
								//document.getElementById('num').src='/getCode?'+(new Date()).getTime();							
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
		}
	</script>
<@model.webendsearch />
