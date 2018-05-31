<@model.webheadsearch />
	<div class="page">
		<div class="content">
			<form id="formdata">
				<div class="page-login" style="margin-top: 1rem">				    
					<div class="list-block inset text-center">
						<ul>
							<li>
								<div class="item-content">
									<div class="item-inner">
										<div class="item-input">
											<input id="product_url" type="text" class="input_enter"
												placeholder="请粘贴从淘宝或京东复制的商品链接" name="product_url">	
											<input id="mobile" type="hidden" value="" name="mobile">										
										</div>										
									</div>
								</div>
							</li>
							<!-- 暂时屏蔽掉
							<li>
								<div class="item-content">
									<div class="item-media">
										<i class="icon icon-form-name"></i>
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
								data-transition='fade' id="submitlogin" onclick="fetch();">搜返利</a>
						</p>
						<p class='text-center signup'>                               
								<a href="/order.html"
								class='pull-left external' style="font-size: 0.8rem;">录入订单号</a>
								<!--<a href="/api/invitation.html"
								class='pull-center external' style="font-size: 0.8rem;color:red">拿奖励</a>
								&nbsp;&nbsp;&nbsp;-->
								<a href="/help.html"
								class='pull-center external' style="font-size: 0.8rem;color:red">操作指南</a>
								<a href="/searchorder.html"
								class='pull-right external' style="font-size: 0.8rem;">我要提现</a>
								
						</p>
					</div>
			</form>
		</div> 

		<div id="result" align="center">
		  <a href="/api/invitation.html"><img id="search" width="90%" src="http://help.guangfish.com/imgs/invitation.png"></a>
		  <!--<br/><font style="color: red;">【花钱也能赚钱、让逛鱼带你飞】</font> -->
		  <br/><font style="color: red;font-size: 0.7rem;">复制商品链接地址请看下面图示</font>
		  <br/><font style="color: red;font-size: 1.0rem;">▽</font>
		  <!--<br/><font style="font-size: 0.6rem;color: red;">往下拉查看商品链接复制、保存书签</font>-->		
		  <br/><img width="90%" src="http://help.guangfish.com/imgs/ios-taobao-jd.png">
		  <br/><img width="90%" src="http://help.guangfish.com/imgs/ios-jd.png">
		  <!--<br/><img width="90%" src="http://help.guangfish.com/imgs/andorid-taobao.png">
		  <br/><font style="font-size: 0.6rem;color: red;">保存书签：请在手机浏览器打开本页面，并保存成书签</font>
		  <br/><img width="90%" src="http://help.guangfish.com/imgs/bookmark.png">	  
		  -->
		  <!--<br/><img width="45%" src="http://help.guangfish.com/imgs/wx-qun.png"><img width="45%" src="http://help.guangfish.com/imgs/wx-kefu.png">-->
		  <br/><img width="45%" src="http://help.guangfish.com/imgs/wx-kefu.png">
		</div>	
	</div>
			
	<script type='text/javascript' src='/static/front/js/clipboard.min.js' charset='utf-8'></script>
	
	<script>		
	  var B = setInterval(function(){
	    $
						.ajax({
							type : "post",
							url : "/api/notice", 
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							timeout : 30000, 
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(JSON.stringify(data) != "{}"){
								  for(var i = 0; i < data.ret.length; i++){
								    //alert(data.ret[i].type + " " + data.ret[i].content);
								    var notice = $.cookie('guangfishnotice'+data.ret[i].id);
								    if(!notice){
								      if(data.ret[i].type==2){
								        Core.Dialog.note({'title':data.ret[i].title,'content':data.ret[i].content,'btn':['<div style="font-size:12px;">知道了</div>'],'callback':function(){}})
								      }else{
								        Core.Dialog.msg(data.ret[i].content,data.ret[i].noticeTime);
								      }
								      $.cookie('guangfishnotice'+data.ret[i].id, 'notice', { expires: data.ret[i].expires, path: '/',domain:'${cookieDomain?if_exists}'});
								      break;
								    }								    
								  }
								}
								
//								if(JSON.stringify(data) != "{}"){
//								  var notice = $.cookie('guangfishnotice'+data.ret.id);
//                                  if(!notice){
//                                    if(data.ret.type==2){
//								      Core.Dialog.note({'title':data.ret.title,'content':data.ret.content,'btn':['<div style="font-size:12px;">知道了</div>'],'callback':function(){}})
//								    }else{
//								      Core.Dialog.msg(data.ret.content,data.ret.noticeTime);
//								    }
//								    $.cookie('guangfishnotice'+data.ret.id, 'notice', { expires: data.ret.expires, path: '/',domain:'${cookieDomain?if_exists}'});
//                                  }								  
//								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
	  },15000);
	  
//	  <#if notice??>
//      var notice = $.cookie('guangfishnotice${notice.id?if_exists}');
//      if(!notice){
//        if(${notice.type?if_exists}==2){
//	  	  Core.Dialog.note({'title':'${notice.title?if_exists}','content':'${notice.content?if_exists}','btn':['<div style="font-size:12px;">知道了</div>'],'callback':function(){}})	      	    
//	    }else{
//	      Core.Dialog.msg('${notice.content?if_exists}',${notice.noticeTime?if_exists});
//	    }
//	    $.cookie('guangfishnotice${notice.id?if_exists}', 'notice', { expires: ${notice.expires?if_exists}, path: '/',domain:'${cookieDomain?if_exists}'});
//	  }
//	  </#if>
     
	  function noticeClick(){
	    $('.layui-layer-btn0').click();
	  }
	  
    </script>
   
    <script>
    var helpnotice = $.cookie('helpnotice');
	if(!helpnotice){
	  Core.Dialog.confirm({'title':'<div style="font-size:14px;">您是否需要查看使用帮助？</div>','btn':['<div style="font-size:12px;">需要帮助</div>','<div style="font-size:12px;">不需要</div>'],'callback':function(){helpnext1()}});
	  $.cookie('helpnotice', 'helpnotice', { expires: 7, path: '/',domain:'${cookieDomain?if_exists}'});
	}	
	function helpnext1(){
	  Core.Dialog.open({'title':'<div style="font-size:14px;">step 1 复制链接</div>','width':'380px','height':'550px','content':'<font style="color: red;font-size: 0.6rem;">&nbsp;&nbsp;▽淘宝商品链接复制▽</font><img width="100%" src="http://help.guangfish.com/imgs/1-1-1.jpg"><font style="color: red;font-size: 0.6rem;">&nbsp;&nbsp;▽京东商品链接复制▽</font><img width="100%" src="http://help.guangfish.com/imgs/ios-jd.png">','btn':['<div style="font-size:12px;">下一步：怎么搜索返利</div>'],'callback':function(){helpnext2()}});
	}	
	function helpnext2(){
	  Core.Dialog.open({'title':'<div style="font-size:14px;">step 2 搜索返利</div>','width':'380px','height':'380px','content':'<font style="color: red;font-size: 0.6rem;">&nbsp;&nbsp;打开逛鱼搜索页，粘贴复制的链接地址</font><img width="100%" src="http://help.guangfish.com/imgs/2-1-1.jpg">','btn':['<div style="font-size:12px;">下一步：怎么完成购物</div>'],'callback':function(){helpnext3()}});
	}
	function helpnext3(){
	  Core.Dialog.open({'title':'<div style="font-size:14px;">step 3 完成购物</div>','width':'380px','height':'380px','content':'<font style="color: red;font-size: 0.6rem;">&nbsp;&nbsp;打开手机淘宝，自动跳出商品提示页面</font><img width="100%" src="http://help.guangfish.com/imgs/3-1-1.jpg">','btn':['<div style="font-size:12px;">下一步：怎么提交订单号</div>'],'callback':function(){helpnext4()}});
	}
	function helpnext4(){
	  Core.Dialog.open({'title':'<div style="font-size:14px;">step 4 提交订单号</div>','width':'380px','height':'380px','content':'<font style="color: red;font-size: 0.6rem;">&nbsp;&nbsp;打开【逛鱼搜索->录入订单号】页面</font><img width="100%" src="http://help.guangfish.com/imgs/4-1-1.jpg">','btn':['<div style="font-size:12px;">下一步：怎么申请提现</div>'],'callback':function(){helpnext5()}});
	}
	function helpnext5(){
	  Core.Dialog.open({'title':'<div style="font-size:14px;">step 4 申请提现</div>','width':'380px','height':'380px','content':'<font style="color: red;font-size: 0.6rem;">&nbsp;&nbsp;打开【逛鱼搜索->我要提现】页面</font><img width="100%" src="http://help.guangfish.com/imgs/5-1-1.jpg">','btn':['<div style="font-size:12px;">我知道了</div>'],'callback':function(){}});
	}
    </script>

    <script>
	  	var mobile = $.cookie('guangfishmobile');
	  	if(mobile){
	  	  $("#mobile").val(mobile);
	  	}
	</script>
	
	<script>	    	    
		function drump(link) {
			//location.href=link;
			alert('提示：【付款时不要用红包抵扣】，完成购买后记得回来录入订单号拿返利哦！');
			window.open(link);
		  //if(isContains(link,"taobao.com")){
			//if(isWeiXin()){
			  //alert("无法用微信浏览器打开淘宝商品页，建议您在手机浏览器中打开本网址，并保存成书签。操作步骤：按右上角'...' 苹果手机选择'在Safari中打开'，安卓手机选择'在浏览器打开'；也可以选择'复制链接'，在喜欢的浏览器打开；");
			//}else{
			  //window.open(link);
			//}
		  //}else{
		    //window.open(link);
		  //}		
		}
		
		function isContains(str, substr) {
         return str.indexOf(substr) >= 0;
        }
		
		//判断是否是微信浏览器的函数
        function isWeiXin(){
          //window.navigator.userAgent属性包含了浏览器类型、版本、操作系统类型、浏览器引擎类型等信息，这个属性可以用来判断浏览器类型
          var ua = window.navigator.userAgent.toLowerCase();
          //通过正则表达式匹配ua中是否含有MicroMessenger字符串
          if(ua.match(/MicroMessenger/i) == 'micromessenger'){
            return true;
          }else{
            return false;
          }
        }
        
        function jsCopy(tkl){
		  var value = $('#'+tkl).val();
		  $('#copy').attr('data-clipboard-text', value);
		  var clipboard = new Clipboard('#copy');
          clipboard.on('success', function (e) {
            //alert('淘口令复制成功，去打开手机淘宝完成商品购买');
            //alert('完成购买后记得回来录入订单号拿返利哦！');
            Core.Dialog.msg('淘口令复制成功，去打开手机淘宝完成商品购买，【付款时不要用红包抵扣】，完成购买后记得回来录入订单号拿返利哦！',9000);
            $('#copy').removeAttr('data-clipboard-text');
          });
          clipboard.on('error', function (e) {
            alert('复制失败');
          });
	     }
		
		function vcodevalid() {
			var vcode = $('#vcode').val();

			if (!vcode) {
				alert("请输入验证码！");
				return -1;
			}
			if(vcode.toString().length!=5){
			  alert("验证码输入不正确！");
			  return -2;
			}
			 
			var dt=0;

				$
						.ajax({
							type : "post",
							url : "/api/vcodevaild",
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							async:false,
							data : JSON.stringify({
								"vcode" : vcode
							}),
							timeout : 30000,
							success : function(data) {
								console.log('请求到的数据为：', data)
								if(data.ret.result=="0"){
								  dt=0;
								}else{
								  dt=1;
								}					
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
						return dt;
		}

		function fetch() {
		    var mobile = $('#mobile').val();
			var producturl = $('#product_url').val();
			var reg = /http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/;
			var regtkl=/￥.*￥/;
			var regtkl1=/《.*《/;
			if(!producturl.match(regtkl) || !producturl.match(regtkl1)){
			    if (!reg.test(producturl)) {
				  alert("请粘贴有效的链接或内容！");
				  return;
			    }else{
			      //暂时屏蔽掉
			      //var adt=vcodevalid();
			      //if(adt==-1 || adt==-2){
			      //  return;
			    //}			  
			    //if(!(adt==0)){
		        //  alert("验证码验证失败");
		        //  return;
		        //}else{		        
		        //  document.getElementById('num').src='/getCode?'+(new Date()).getTime();
		        //}
			  }
			}						
			
			$('#e-c').remove();
			
            $('#submitlogin').removeAttr('onclick');
			//加载中						
			$('#result')
					.html(
							"<div id='waiting'><img src='http://help.guangfish.com/imgs/timg.gif'><br/><br/><img width='80%' src='http://help.guangfish.com/imgs/search"+Math.ceil(Math.random()*13)+".gif'></div>");
			
			if (producturl) {
				$
						.ajax({
							type : "post",
							url : "/api/productInfo", 
							contentType : "application/json",
							dataType : "json",// 返回json格式的数据
							data : JSON.stringify({
								"user_id" : "",
								"product_url" : producturl,
								"mobile" : mobile
							}),
							timeout : 30000, 
							success : function(data) {
								console.log('请求到的数据为：', data)
								$('#waiting').remove(); 
								if (JSON.stringify(data) == "{}") {
									$('#result')
											.html(
													"<div id='e-c' align=center><div style='font-size:12px;width:330px;top:10%;left:38%;background:#fff;border-radius:10px;box-shadow:5px 5px 10px #888;'><h2 style='padding:5px;font-size:18px;'>该商品无返利</h2></div></div>");
								} else {
								    if(data.ret.code==101){
								      if(data.ret.result.status=="4"){
								        alert("系统只支持手机淘宝、京东的商品地址");
								      }
								      else if(data.ret.result.status=="1"){
								        alert("系统繁忙，请稍后再试");
								      }
								      else if(data.ret.result.status=="2"){
								        alert("请输入商品链接地址");
								      }
								      else if(data.ret.result.status=="3"){
								        alert("商品链接地址不正确");
								      }
								      else{
								        alert("系统忙，请重新再试！");
								      }								      
								    }else{								     
								      if(data.ret.result.status=="0"){
								        $('#result').html(data.ret.result.msg);
								      }
								    }							    
								}
								$("#product_url").val("");
								//暂时屏蔽掉
								$("#vcode").val("");
								
								$('#submitlogin').attr('onclick','fetch()');
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								console.log('请求失败')
							}
						});
			} else {
				alert("请输入商品地址后再查询");
			}
		}
	</script>
<@model.webendsearch />
