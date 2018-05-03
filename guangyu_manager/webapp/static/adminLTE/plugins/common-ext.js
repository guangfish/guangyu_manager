//alert
(function($) {
    $.fn.extend( {
        "message" : function(options) {
            //className 可以有alert,alert-success,alert-info, alert-error 等选项，对应不同的样式
           switch (options.type){
                
                case "warning" :
                        options.className = "alert-warning";
                        options.title = "系统信息：";
                    break;
                case "success" :
                        options.className = "alert-success";
                        options.title = "温馨提示：";
                    break;
                case "danger": default:
                        options.className = "alert-danger";
                        options.title = "错误提醒：";
                    break;
                
            };


            options = $.extend( {
                //type : options.type,
                msg : options.msg,
                speed: options.speed || 300,
                existTime: options.existTime || 5000
            }, options);
 
            var div = $('<div class="alert ' + options.className + '"  role="alert" style="z-index:9999999;display:none;width:680px;left:30%;right:30%;margin:auto;top:8%;position:fixed;">'
                +'<button type="button" class="close" data-dismiss="alert">×</button>'
                +'<div class="pull-left mLeft10"><strong>'+ options.title+'</strong></div>'
                +'<div class="pull-left mLeft10"><span>'+options.msg+'</span></div>'
                +'</div>');
            $('body').append(div);
            div.show(options.speed);
            
            if(options.type != "error"){
                 //隐藏对象
                setTimeout(function(){div.toggle(options.speed);},options.existTime);
                //移除对象
                setTimeout(function(){div.remove();},options.existTime+5000);   
            }else{
            	 //隐藏对象
                setTimeout(function(){div.toggle(options.speed);},options.existTime);
                //移除对象
                setTimeout(function(){div.remove();},options.existTime+8000);
            }
            
        }
    });
    return this;
})(jQuery);

var poverError = [
	{
		bgColor: "#ff0000",
		type : "错误信息",
		content : "网络链接超时" 
	},
	{
		bgColor: "green",
		type : "友情信息",
		content : "顶戴顶替超时" 
	},
	{
		bgColor: "blue",
		type : "blue",
		content : "blue" 
	}
];

function pover(selector,options){
	var node = document.createElement("span");
		options = $.extend({
			bgColor: "#ff0000",
			type : "错误信息",
	    	content : "网络链接超时" 
		},options);
	$(node).css("padding","3px 4px")
		   .css("cursor","pointer")
		   .css("line-height","18px")
		   .css("height","18px")
		   .css("color","#fff")
		   //.css("display","block")
		   .css("background-color",options.bgColor)
		   .css("font-weight","600");
	$(node).attr("data-html","true")
		   .attr("data-toggle","popover")
		   .attr("data-trigger","hover")
		   .attr("data-placement","right")
		   .attr("data-content",options.content);
	$(node).html(options.type);
	$(selector).append(node);
	$('[data-toggle=popover]').popover();
};

// 关闭当前窗口
function closeWin(){
	window.opener=null;window.open('','_self');window.close();
}


//倒计时  （使用方法：addTimer("timer1", 604800); ）
var addTimer = function () {     
        var list = [],     
            interval;     
    
        return function (id, time) {     
            if (!interval)     
                interval = setInterval(go, 1000);     
            list.push({ 'id':id, 'time': time });     
        };     
    
        function go() {   
        	if(list.length==0){
        		clearTimeout(interval);
        		interval = "";
        	}
        	
            for (var i = 0; i < list.length; i++) {
            	var obj = document.getElementById( list[i].id );
            	if(obj==undefined){
            		 list.splice(i--, 1); 
            		 continue;
            	}
            	
               obj.innerHTML = getTimerString(list[i].time ? list[i].time -= 1 : 0);   
               
               if (list[i].time<0){     
                   list.splice(i--, 1);  
                   continue;
               }
            }     
        }     
    
        function getTimerString(time) {     
                d = Math.floor(time / 86400),     
                h = Math.floor((time % 86400) / 3600),     
                m = Math.floor(((time % 86400) % 3600) / 60),     
                s = Math.floor(((time % 86400) % 3600) % 60);   
         /*       
                if(h<10)
                	h = "0" + h;
                if(m<10)
                	m= "0" + h;
                if(s<10)
                	s = "0" + s;
           */
            if (time>0)     
                return "剩余：<span style=\"color:red;\">" + d + "</span>" + "天" + "<span style=\"color:red;\">" + h + "</span>" + "小时" + "<span style=\"color:red;\">" + m + "</span>" + "分";       
            else return "<span style=\"color:red;\">已截止</span>";     
        }     
} (); 


//倒计时  （使用方法：addTimer("timer1", 604800); ）
var addProductTimer = function () {     
      var list = [],     
          interval;     
  
      return function (id, date,now) {     
          if (!interval)     
              interval = setInterval(go, 1000); 
          var d1 = new Date(date);
          var d2 = new Date(now);
          var time = (d1-d2)/1000;
          
          list.push({ 'id':id, 'time': time });     
      };     
  
      function go() {   
      	if(list.length==0){
      		clearTimeout(interval);
      		interval = "";
      	}
      	
          for (var i = 0; i < list.length; i++) {
          	var obj = document.getElementById( list[i].id );
          	if(obj==undefined){
          		 list.splice(i--, 1); 
          		 continue;
          	}
          	
             obj.innerHTML = getTimerString(list[i].time ? list[i].time -= 1 : 0);   
             
             if (list[i].time<0){     
                 list.splice(i--, 1);  
                 continue;
             }
          }     
      }     
  
      function getTimerString(time) {     
              d = Math.floor(time / 86400),     
              h = Math.floor((time % 86400) / 3600),     
              m = Math.floor(((time % 86400) % 3600) / 60),     
              s = Math.floor(((time % 86400) % 3600) % 60);   
       /*       
              if(h<10)
              	h = "0" + h;
              if(m<10)
              	m= "0" + h;
              if(s<10)
              	s = "0" + s;
         */
          if (time>0)     
              return "剩余：<span style=\"color:red;\">" + d + "</span>" + "天" + "<span style=\"color:red;\">" + h + "</span>" + "小时" + "<span style=\"color:red;\">" + m + "</span>" + "分";       
          else return "<span style=\"color:red;\">已截止</span>";     
      }     
} (); 
//方法可能重复



// 检查后台处理状态(场景：后台处理时间较长，前台定时请求后台的执行状态)
var checkProcessStatus = function (){
	var interval;
	var uuid;
	
	return function (uuid, callback) {  
        if (!interval)     
            interval = setInterval(go, 2000);     
        
        this.callback  = callback;
        this.uuid = uuid;
	};   

	function go() {
		 $.ajax({
		        type: "POST",
		        url: "/epean/Public/checkProcess.html",
		        data: {"uuid" : this.uuid},
		        dataType: "json",
		        success: function(data){
		        	var pm = data.processMsg;
		        	
		            if(pm==undefined || pm.code!=0){
		            	clearTimeout(interval);
		            	interval = "";
		            }
	            	callback(pm);
		        }
		    });
	 }
}();

/**
 * 根据name，得到某组checkbox选中的值(多个用逗号分隔)
 */
function getCheckBoxValByName(name){
	var s = "";
	$("input[name='"+name+"']:checked").each(function(){
		var v = $(this).val();
		if(s!=""){
			s = s + ",";
		}
		s = s + v;
	});
	return s;
}

/**
 * 根据name，得到所有checkbox的值（多个用逗号分隔）
 */
var getAllCheckBoxValByName = function(name){
	var s = "";
	$("input[name='"+name+"']").each(function(){
		var v = $(this).val();
		if(s!=""){
			s = s + ",";
		}
		s = s + v;
	});
	return s;
};

/**
 * 选中当前页复选框
 * @param obj
 * @param name
 */
function selAllCurrPage(obj, name){
	var isChecked = $(obj).is(':checked');
	if(isChecked){
		$("input[name='"+name+"']").prop("checked", true);
	}else{
		$("input[name='"+name+"']").prop("checked", false);
	}
}

/**
 * 字符串格式化
 * 用法1：
 * 	var s = "成功删除&{successNum}个";
 * 	s.format({successNum:20});
 * 用法2：
 * 	var s = "成功删除&{0}个,错误&{1}个";
 * 	s.format(20, "abc");
 * 
 */
String.prototype.format = function(args) {
	if (arguments.length>0) {
		var result = this;
		if (arguments.length == 1 && typeof (args) == "object") { 
			for(var key in args){ 
				var reg=new RegExp ("(&{"+key+"})","g"); 
				//alert(key);
				result = result.replace(reg, args[key]); 
				
			} 
		}else{
			for(var i = 0; i < arguments.length; i++){ 
				if(arguments[i]==undefined) 
				{ 
						
					return ""; 
				} 
				else{
					
					var reg=new RegExp ("(&{["+i+"]})","g"); 
					result = result.replace(reg, arguments[i]); 
				} 
			} 
		} 
			return result; 
	}else{ 
		return this; 
	} 
};

/**
 * 日期格式化
 * 用法：
 * new Date(毫秒数).format("yyyy-MM-dd hh:mm:ss");
 */
Date.prototype.format = function(format) {  
    /* 
     * eg:format="yyyy-MM-dd hh:mm:ss"; 
     */  
    var o = {  
        "M+" : this.getMonth() + 1, // month  
        "d+" : this.getDate(), // day  
        "h+" : this.getHours(), // hour  
        "m+" : this.getMinutes(), // minute  
        "s+" : this.getSeconds(), // second  
        "q+" : Math.floor((this.getMonth() + 3) / 3), // quarter  
        "S" : this.getMilliseconds()  
        // millisecond  
    }
  
    if (/(y+)/.test(format)) {  
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4  
                        - RegExp.$1.length));  
    }  
  
    for (var k in o) {  
        if (new RegExp("(" + k + ")").test(format)) {  
            format = format.replace(RegExp.$1, RegExp.$1.length == 1  
                            ? o[k]  
                            : ("00" + o[k]).substr(("" + o[k]).length));  
        }  
    }  
    return format;  
}

/**
 * 比较两个字符串日期大小
 */
function compareDate(startTime, endTime){
    var start=new Date(startTime.replace("-", "/").replace("-", "/"));
    var end=new Date(endTime.replace("-", "/").replace("-", "/"));
    if(start == end){  
        return 0;  
    }else if(start > end){
    	return 1;
    }else if(start < end){
    	return -1;
    }
}

/**
 * 判断数组中是否有重复
 * @param arr 传入的数组
 * @returns 
 * 如果有重复返回重复的值，如果没有重复返回空串
 * 
 */
function isRepeatInArray(arr){
	for(var i = 0; i < arr.length; i++){
		for(var j=i+1; j<arr.length; j++){
			if(arr[i] == arr[j]){
				return arr[i];
			}
		}
	}
	return "";	
}

/**
 * 判断字符串中是否包含中文
 * @param str
 * @returns {Boolean}
 * 如果包含中文返回true，否则返回false
 */
function isContainChinese(str){ 
	return /.*[\u4e00-\u9fa5]+.*/.test(str);	
}

/**
 * 百度翻译
 * @param str
 */
var BAIDU_TRANSLATE_URL = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=f2iK6uIQm8YcOqZO0WcSmVxm&from=zh&to=en&q=";
function baiDuTranslate(str, callback){
	var s = "";
	if(str != undefined && str != ""){
		str = $.trim(str);
		if(str.length > 5000){
			$.fn.message({type:"error",msg:"超过了翻译的最大字数限制"});
			return callback(s);
		}
		// 包含中文的时候才进行翻译
		if(!isContainChinese(str)){
			return callback(s);
		}
		str = encodeURIComponent(str);
		var url = BAIDU_TRANSLATE_URL + str;
		
		$.ajax({
			async : false,
	        type : "get",
	        url : url,
	        dataType : "jsonp",
	        jsonp : "callback",
	        data : {},
	        success : function(data){
	        	if(data != null){
	        		// 如果error_code属性，证明请求失败
	        		if(data.hasOwnProperty("error_code")){
	        			$.fn.message({type:"error",msg:data.error_msg});
	        		}else if(data.hasOwnProperty("trans_result")){
	        			for(var i in data.trans_result){
	        				if(s == ""){
	        					s = data.trans_result[i].dst;
	        				}else{
	        					s += "\n" + data.trans_result[i].dst;
	        				}
	        			}
	        			callback(s);
	        		}else{
	        			$.fn.message({type:"error",msg:"网络连接超时，请稍后再试！"});
	        		}
	        	}
	        },
	        error:function(err){
	        	$.fn.message({type:"error",msg:"网络连接超时，请稍后再试！"});
	        }
	    });
	}
}
/**fuyi add**/
(function ($, window, location, undefined) {
    var ie = $.imageEditor;
    window.imgEdit = function (cosUrl, imageUrl, wcb, selectPhotoFunc) {
        ie.params.allowFullscreen = false;
        ie.params.movie = "";
        ie.params.wmode = "transparent";
        ie.setLaunchVars('file_name', 'filedata');
        ie.setLaunchVars('file_type', 'jpg');
        if (selectPhotoFunc && typeof selectPhotoFunc == 'function'){
            ie.setLaunchVars('preventBrowseDefault', 1);
        }
        ie.setLaunchVars('cropPresets', [{'主图': '800x800'}, {'eBay主图': '1000x1000'}]);
        ie.setLaunchVars('customMenu', [{'decorate': ['basicEdit', 'inpaint', 'trinket', 'text', 'particle', 'effect', 'border', 'magic', 'localFixes']}]);
        ie.setLaunchVars('nav', 'decorate/basicEdit');
        ie.setLaunchVars('customMaterial', location.protocol + '//' + location.host + '/imageditor/custom_material.xml');
        ie.setUploadType(2);
        ie.setUploadURL(location.protocol + '//' + location.host + '/imageditor/file/upload.json');
        ie.onInit = function () {
            imageUrl && ie.loadPhoto(imageUrl);
        };
        ie.onBeforeUpload = function (data, id) {
            ie.setUploadArgs({filetype: data.type, type: 'image', url: imageUrl, filename: data.name});
        };
        ie.onUploadResponse = function (data) {
            var result = eval('(' + data + ')');
            wcb && typeof wcb == 'function' && wcb(result);
        };
        ie.onBrowse = function (channel, multipleSelection, canClose, id) {
            selectPhotoFunc && typeof selectPhotoFunc == 'function' && selectPhotoFunc(multipleSelection, canClose, function (src) {
                ie.loadPhoto(src, false, ie.defaultID, {loadImageChannel: channel});
            });
            return false;
        };
        ie.embedSWF('editor-embeded', 3, '100%', '100%');
        //修正360 flash遮挡
        var o = $('object');
		o.params.movie = "";
    };
    window.watermarkEdit = function (watermarkUrl, watermarkId, wcb) {
        ie.params.allowFullscreen = false;
        ie.params.movie = "";
        ie.params.wmode = "transparent";
        ie.setLaunchVars('file_type', 'png');
        ie.setLaunchVars('file_name', 'filedata');
        ie.setLaunchVars('customMenu', [{'decorate': ['basicEdit', 'effect', 'text', 'border', 'magic', 'localFixes', 'particle', 'inpaint']}]);
        ie.setLaunchVars('nav', 'decorate/basicEdit');
        ie.setUploadType(2);
        ie.setUploadURL(location.protocol + '//' + location.host + '/watermark/file/upload.json');
        ie.onInit = function () {
            watermarkUrl && ie.loadPhoto(watermarkUrl);
        };
        ie.onBeforeUpload = function (data, id) {
            ie.setUploadArgs({
                filetype: data.type,
                type: 'image',
                url: watermarkUrl,
                id: watermarkId,
                filename: data.name
            });
        };
        ie.onUploadResponse = function (data) {
            var result = eval('(' + data + ')');
            wcb && typeof wcb == 'function' && wcb(result);
        };
        ie.embedSWF('editor-embeded', 3, '100%', '100%');
        //修正360 flash遮挡
        var o = $('object');
		o.params.movie = "";
    };
})(jQuery, window, window.location);

/**fuyi add end**/
//简单验证
function validateUser(obj){
	var validateStr = $(obj).val(),
	validateType = $(obj).attr('name'),
	//validateEmail = /^([a-zA-Z0-9]+[_|_|.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|_|.]?)*[a-zA-Z0-9]+\.(?:com|cn)$/;
	//validateEmail = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9_]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	validateEmail = /^([a-zA-Z0-9]+[_|\_|\.|\-]?)*[a-zA-Z0-9_-]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	//validateEmail =  /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
	var validateName = /^[a-zA-Z0-9][a-zA-Z0-9_-]{3,29}$/;
	var validatePassword = /^[^\s\u4e00-\u9fa5]{6,20}$/;
	var validateQQ = /^[1-9][0-9]{4,16}$/;

	switch (validateType)
	{
	case "account":
		msgTitle = "请输入您的用户名！";
		if (validateStr != '')
		{
			if (!validateStr.match(validateName))
			{
				$.fn.message({type:"error",msg:"用户名格式不正确！格式：字母数字下划线,不能以下划线开头,长度4-30字符"});
				return false;
			}
		};
		break;
	case "password":
		msgTitle = "密码不能为空！";
		if (validateStr != '')
		{
			if (!validateStr.match(validatePassword))
			{
				$.fn.message({type:"error",msg:"密码格式不正确！不能含有空格,长度为6-20个字符！"});
				return false;
			}
		};
		break;
	case "password2":
		msgTitle = "确认密码不能为空！";
		if (validateStr != '')
		{
			if (!validateStr.match(validatePassword))
			{
				$.fn.message({type:"error",msg:"确认密码格式不正确！不能含有空格,长度为6-20个字符！"});
				return false;
			}
		};
		break;
	case "shopName":
		msgTitle = "请填写有效的店铺名称";
		break;
	case "merchantId":
		msgTitle = "请填写有效的商户ID";
		break;
	case "content":
		msgTitle = "反馈内容不能为空";
		break;
	case "email":
		msgTitle = "邮箱不能为空";
	
		if (validateStr != '')
		{
			if (!validateStr.match(validateEmail))
			{
				$.fn.message({type:"error",msg:"邮箱格式不正确！"});
				return false;
			}
		};
		break;
	case "qq":
		msgTitle = "QQ不能为空";
		
		if (validateStr != '')
		{
			if (!validateStr.match(validateQQ))
			{
				$.fn.message({type:"error",msg:"QQ格式不正确！"});
				return false;
			}
		}else{
			$.fn.message({type:"error",msg:msgTitle});
			return false;
		};
		break;
	
	};

	if (validateStr == ''){
		$.fn.message({type:"error",msg:msgTitle});
		return false;
	};
	return true;
}
//首字母大写
String.prototype.firstUpperCase =  function (){
	var str = this.toLowerCase(),
		reg = /^[a-z]/i;
	return str.replace(reg,function(str){
		return str.toUpperCase();
	});
};

// 判断是否是图片
function isPic(name){
    var strFilter =".jpeg|.gif|.jpg|.png|.bmp|.pic|";
    if(name.indexOf(".")>-1){
		var p = name.lastIndexOf(".");
		var strPostfix = name.substring(p, name.length) + "|";
		strPostfix = strPostfix.toLowerCase();
		if(strFilter.indexOf(strPostfix)>-1){
		  return true;
		}
    }
    return false;
}

// 兼容图片访问地址
function getPicRealUrl(url){
	var realUrl = "";
	if(url){
		if(url.indexOf("http") == -1 && url.indexOf("HTTP") == -1){
			// cos地址
			var cosImgUrl="http://cos.myqcloud.com/11000460/";
			// 万象图片地址
			var wxImgUrlPrefix="http://XXXXX-10001658.image.myqcloud.com/";
			// 万象图片项目ID(10001658)
			//var wxAppId = "10001658";
			
			// 判断图片是cos的还是万象的
			if(url.startWith("wx")){
				var bucket = url.split("/")[0];
				realUrl = wxImgUrlPrefix.replace("XXXXX", bucket) + url;
			}else{
				realUrl = cosImgUrl + url;
			}
		}else{
			realUrl = url;
		}
	}
	
	return realUrl;
}

function getTinyPicRealUrl(url){
	var realUrl = "";
	if(url){
		if(url.indexOf("http") == -1 && url.indexOf("HTTP") == -1){
			realUrl = getPicRealUrl(url);
		}else{
			realUrl = url;
		}
	}
	
	return realUrl;
}
/**
 * 客户端上传图片成功后回调处理
 * @param bucket
 * @param fileId
 * @param fullCid
 * @param fileName
 */
function clientPicCallBack(bucket, fileId, fullCid, fileName, isNeedTree, callBack){
	$.ajax({
		type:'POST',
		url: "album/clientPicCallBack.json",
		data:{
			"bucket":bucket,
			"fileId":fileId,
			"fullCid":fullCid,
			"fileName":fileName,
			"isNeedTree":isNeedTree
		},
		dataType:'json',
		async : true,
		success:function(data){
			console.log(data);
			if(data.code == 0){
				if(callBack){
					callBack(data.treeJson);
				}
			}else{
				$.fn.message({type:"error", msg:data.msg});
			}
		}
	});
}

/**
 * 判断字符串是否以某字符开头
 */
String.prototype.startWith=function(str){
    if(str==null||str==""||this.length==0||str.length>this.length)  
      return false;  
    if(this.substr(0,str.length)==str)  
      return true;  
    else
      return false;
    return true;  
}

/**
 * 判断字符串是否以某字符结尾
 */
String.prototype.endWith=function(str){
    if(str==null||str==""||this.length==0||str.length>this.length)
      return false;
    if(this.substring(this.length-str.length)==str)
      return true;
    else
      return false;
    return true;
}


// icon 添加Tooltip

/*
 * form不提交
 * add by fuyi 2015.5.6
 */
$(document).off('submit','form.doNotSubmit');
$(document).on('submit','form.doNotSubmit',function(){
	return false;
});


