/**
 * 分页插件
 * @author zhaoyd
 */
(function($) {
	$.fn.pagination = function(config) {
		var obj = this;
		//默认page
		var page = {
			numPerPage: getLocalNumPerPage(), //每页记录数,读取cookie，默认20
			pageNum: 1, //当前页码
			totalCount: 0, //总记录数
			//计算出来的参数
			pageCount: 0, //总页数
			firstPage: false, //是否第一页
			lastPage: false, //是否第二页
			hasPrev: false, //是否有上一页
			hasNext: false, //是否有下一页
		}
		var searchData = {}//默认searchData
		page = $.extend(page, config.page);
		//存cookie，初始化page值的时候生效
		setLocalNumPerPage(page.numPerPage);
		searchData = $.extend(searchData, config.searchData);
		var onPageClicked = config.onPageClicked;//回调函数
		initPage();
		obj.find("li .go").trigger("click");
		/**
		 * 初始化pagebarDom
		 * @param {Object} returnPage 后台返回的page对象
		 */
		function initPage(returnPage) {
			if(!$.isEmptyObject(returnPage)){
				page = returnPage;
			}
			//计算page其他字段
			page.pageCount = parseInt(page.totalCount / page.numPerPage) + (page.totalCount % page.numPerPage == 0 ? 0 : 1);
			page.firstPage = (page.pageNum == 1);
			page.lastPage = (page.pageCount == 0 || page.pageCount == page.pageNum);
			page.hasPrev = (page.pageNum > 1);
			page.hasNext = (page.pageNum < page.pageCount);
			//page字段处理结束
			//组装dom
			var str = '<div class="col-xs-6">'
					+ '<div class="pageinfo">共<span id="pageCountSpan">'+page.pageCount+'</span>页,<span id="totalCountSpan">'+page.totalCount+'</span>条记录,每页<span id="numPerPageSpan">'+page.numPerPage+'</span>条,当前第<span id="pageNumSpan">'+page.pageNum+'</span>页</div>'
					+ '</div>';
			str += '<div class="col-xs-6"><div><ul class="pagination pull-right no-margin">';		
			//str += '<li class="numPerPage" style="float: left;"><select class="form-control"><option>20</option><option>50</option><option>100</option><option>300</option><option>1000</option></select></li>';
			if(page.firstPage) {
				str += '<li class=""><a class="first" href="javascript:;">首页</a></li>';
			} else {
				str += '<li class=""><a class="first" href="javascript:;">首页</a></li>';
			}
			if(page.hasPrev) {
				str += '<li class=""><a class="pre" href="javascript:;">上一页</a></li>';
			} else {
				str += '<li class=""><a class="pre" href="javascript:;">上一页</a></li>';
			}
			str += '<li class="page active"><a href="javascript:;">' + page.pageNum + '</a></li>'
			if(page.hasNext) {
				str += '<li class=""><a class="next" href="javascript:;">下一页</a></li>';
			} else {
				str += '<li class=""><a class="next" href="javascript:;">下一页</a></li>';
			}
			if(page.lastPage) {
				str += '<li class="disabled"><a class="last" href="javascript:;">末页</a></li>';
			} else {
				str += '<li><a class="last" href="javascript:;">末页</a></li>';
			}
			str += '<li class="disabled"><a href="javascript:;">' + page.pageNum+'/'+page.pageCount + '</a></li>';
			str += '<li class="num" style="float: left;"><input value="' + page.pageNum + '" type="number" style="width:50px;line-height:1.42857143;height: 34px;"></li>';
			str += '<li class=""><a class="go" href="javascript:;">go</a></li>';
			str += '</ul></div></div>';
			obj.empty().append(str);
			//设置numPerPage
			obj.find(".numPerPage select").val(page.numPerPage);
			if(parseInt(obj.find(".numPerPage select").val())!=obj.find(".numPerPage select").val()){
				obj.find(".numPerPage select option:first").prop("selected",true);
			}
			//跳转到某一页
			obj.find("li .go").on("click", function() {
				var pageNum = obj.find(".num input[type=number]").val();
				if(parseInt(pageNum)!=pageNum){
					return;
				}
				//校验pageNum合法性
				if(pageNum<1){
					pageNum=1;
				}else if(pageNum >page.pageCount&&page.pageCount!=0){
					pageNum = page.pageCount;
				}
				page.pageNum = parseInt(pageNum);
				page.numPerPage = obj.find(".numPerPage select").val();
				searchData.pageNum = page.pageNum;
				searchData.numPerPage = page.numPerPage;
				//执行回调函数
				onPageClicked(searchData,initPage);
				//设置pagebar不可用
				obj.find("li").addClass("disabled");
				obj.find("select").attr("disabled","disabled");
			});
			//首页
			obj.find("li .first").on("click", function() {
				if($(this).parent().hasClass('disabled')) {
					return;
				}
				var pageNum = obj.find(".num input[type=number]").val(1);
				obj.find("li .go").trigger("click");
			});
			//末页
			obj.find("li .last").on("click", function() {
				if($(this).parent().hasClass('disabled')) {
					return;
				}
				var pageNum = obj.find(".num input[type=number]").val(page.pageCount);
				obj.find("li .go").trigger("click");
			});
			//上一页
			obj.find("li .pre").on("click", function() {
				if($(this).parent().hasClass('disabled')) {
					return;
				}
				var pageNum = obj.find(".num input[type=number]").val(page.pageNum - 1);
				obj.find("li .go").trigger("click");
			});
			//下一页
			obj.find("li .next").on("click", function() {
				if($(this).parent().hasClass('disabled')) {
					return;
				}
				var pageNum = obj.find(".num input[type=number]").val(page.pageNum + 1);
				
				obj.find("li .go").trigger("click");
			});
			//修改numPerPage
			obj.find(".numPerPage select").change(function(){
				obj.find(".num input[type=number]").val(1);
				obj.find("li .go").trigger("click");
				setLocalNumPerPage(obj.find(".numPerPage select").val());
			});
		}
	};
})(jQuery);

//刷新当前页面
function refreshPage(){
	$(".pagination li .go").trigger("click");//刷新页面
}
//cookie存取页码，以当前菜单名为key
function getLocalNumPerPage(){
	//var name = menu+"numPerPage";
	var name = "numPerPage";
	var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
	if(arr=document.cookie.match(reg)){
		return unescape(arr[2]);
	}else{
		return 20;
	}
}
function setLocalNumPerPage(numPerPage){
	var Days = 30;
	var exp = new Date();
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	var name = "numPerPage";
	document.cookie = name + "="+ escape (numPerPage) + ";expires=" + exp.toGMTString();
}
