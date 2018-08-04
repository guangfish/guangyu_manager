<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
	<meta name="description" content=""/>
	<meta name="keywords" content=""/>
	<meta name="format-detection" content="telephone=no,email=no">
	<link rel="stylesheet" type="text/css" href="/static/frontv2/css/mui.min.css">
	<script type="text/javascript" src="/static/frontv2/js/jquery-1.8.3.min.js"></script>
	<script src="/static/frontv2/js/flexible/flexible_css.debug.js"></script>
    <script src="/static/frontv2/js/flexible/flexible.debug.js"></script>
	<title>专属客服</title>	
</head>
<body>
<script type="text/javascript" src="/static/frontv2/js/mui/mui.min.js"></script>
<script type="text/javascript" src="/static/frontv2/js/swiper.min.js"></script>
<script type="text/javascript" src="/static/frontv2/js/common.js"></script>

<script type="text/javascript" src="/static/front/js/js/layer/layer.js"></script>
<script type="text/javascript" src="/static/front/js/js/public.core.js"></script>

<script type='text/javascript' src='/static/front/js/clipboard.min.js' charset='utf-8'></script>

<link rel="stylesheet" type="text/css" href="/static/frontv2/css/kefu-app/help.css">
	<!-- 主要内容 -->
	<div class="mui-content mui-event-box">
		<div class="mui-main-event">
			<div id="pullrefresh" class="mui-scroll-wrapper scroll-refresh">
				<div class="mui-scroll">															
					<div id="taobao" class="mui-event-cont">
						<div class="mui-cont-box">
								<div class="mui-cont-box">					    
								<div class="eventImg" align="center"><img id='copy' onclick="copyWeixinCode()" class="eventImg" src="${kefuWeixinQrcodeUrl?if_exists}" alt=""></div>	
								<div class="mui-cont-tit">
									<h2 align="center" >扫码或点击二维码复制微信号</h2><br/>
								</div>
								<div class="mui-cont-tit">
									<h2 align="center">服务时间：周一至周五9:00-18:00</h2>
								</div>																	
						</div>							
					</div>
				</div>
			</div>
			
		</div>
	</div>
	
	<script>
	  
	  function copyWeixinCode() {
	    var value = $('#myInviteCode').val();
		  $('#copy').attr('data-clipboard-text', '${kefuWeixin?if_exists}');
		  var clipboard = new Clipboard('#copy');
          clipboard.on('success', function (e) {
            Core.Dialog.msg('邀请码复制成功',3000);
            $('#copy').removeAttr('data-clipboard-text');
          });
          clipboard.on('error', function (e) {
            Core.Dialog.msg('复制失败');
          });
	  }
	</script>
	
<@model.webendsearchv2 />
