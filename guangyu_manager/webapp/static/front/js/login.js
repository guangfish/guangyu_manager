        var lrkj_service_ip=localStorage["lrkj_serverIp"];
	  
	
		function submitlogin() {
			
			
			$.showPreloader();

			$.ajax({
				type : "post",
				url : lrkj_service_ip+"front/loginAction.jhtml",
				dataType : "json",// 返回json格式的数据
				timeout : 15000,
				data : $("#formdata").serialize(),
				success : function(json, textStatus, XMLHttpRequest) {
					$.hidePreloader();
					if (json.code == 0) {
						$.toast("登陆成功");
						localStorage["user_token"]=json.data.token;
						//localStorage["fanli_did"]=json.data.did;
		                location.href="user_info.html";
					} else {
						$.toast(json.msg);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					$.hidePreloader();
					$.toast("系统发生错误,请稍后再试");
				}
			});
		}
		
		$('.input_enter').bind('keyup', function(event) {
			if (event.keyCode == "13") {
				//回车执行查询
				//$('#search_button').click();
				submitlogin();
			}
		});
		
		$("#submitlogin").click(submitlogin);
		
	
		
		/*
		chrome.browserAction.showHTMLBalloon({
			"path":"../register.html",
			"width":300,
			"height":300
		});
		*/