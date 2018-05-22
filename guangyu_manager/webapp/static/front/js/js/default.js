(function(){
    //set宽高
    var _body = $('body');
    var _win = $(window);
    if(window == top){
        _body.addClass('top-doc');
    }else{
        if(_body.hasClass('autoY') || ($('.table_box').length==0)){
          _body.addClass('fade-doc');
        }
        if((!_body.hasClass('autoY')) && $('.table_box').length>0){
          _body.addClass('main-doc');
        }
    }
    $.extend({
        wider: function() {
            var _winWidth = _win.width() * 1;
            var _bodyWidth = _body.width() * 1;
            var wider = _winWidth > _bodyWidth ? _win : _body;
            return wider;
        },
        higher: function() {
            var _winHeight = _win.height() * 1;
            var _bodyHeight = _body.height() * 1;
            var higher = _winHeight > _bodyHeight ? _win : _body;
            return higher;
        }
    });
})();
(function(){
    window.prevent = false;
    window.maxsbarw = false;
    $.extend({
        calcStyle: function() {
            try {
                $('body').trigger('calcStyle');
            } catch(e) {
                console.log(e);
            }

        },
        datagridResize: function(o){
            var t = $(o).find('.table_main');
            if(!t.parent().is('.table_box')) {
              t.datagrid('resize', {
                width: 10,
                height: 10
              });
              window.prevent = true;
              t.datagrid('resize', {
                width: (Core.Easyui.default['maxsize'])?$(o).width():Core.Easyui.default['config']['width'],
                height: (Core.Easyui.default['maxsize'])?($.higher().height() - $(o).offset().top):Core.Easyui.default['config']['height']
              });
              window.prevent = false;
            }
        },
        searchStyle: function(){
            //search two rows
            var _f = $('.search_form');
            _f.find('.auto-timelist').find('.time_range').remove();
            var _a = _f.find('.auto-timelist');
            var _s = _f.find('.search_list');
            var _clonea = null;
            if(window.ww == $.wider().width()){return;}
            if(_f.length>0 && _f.attr('attr-rows') === 'auto'){
              _clonea = _a.clone(true);
              _f.find('.auto-timelist').remove();
              _f.find('.add_req').after(_clonea);
              _f.removeClass('rows_two').addClass('rows_one');

              window.maxsbarw = true;
              var _wb = _f.find('.sc_wrap').width();
              if(_wb > $.wider().width()){
                if(!_f.hasClass('rows_two')){
                  if(_s){
                    _clonea = _a.clone(true);
                    // console.log(_clonea);
                    _f.find('.auto-timelist').remove();
                    _s.after(_clonea);
                    _f.find('.auto-timelist').find('.req_value').eq(0).before('<div class="req_title time_range">时间范围：</div>');
                    _f.addClass('rows_two').removeClass('rows_one');
                  }
                }
              }
              window.maxsbarw = false;
            }
        },
        renderTable: function(){
          //重新渲染datagrid table的宽高
          if($('body .table_box').length>0) {
              $('body .table_box').each(function() {
                  var _t = $(this);
                  if(_t.parents('.dialog_box').length>0){ return;}
                  $.datagridResize(_t);
              });
          }
        }
    });
    $('.search_key').on('setZsValue.serswitch',function(){
        var _vstimer = setTimeout(function(){
          var _f = $('.search_form');
          $.searchStyle(_f);
          $.renderTable();
          clearTimeout(_vstimer);
        }, 300);
    });

    var main = $('.main');
    var leftsidebar = $('.leftsidebar');
    var defaultW = 1200;
    $('body').on('calcStyle', function() {
        if(window!== top){
            window.maxsbarw || $.searchStyle();
            //如果有table则resize
            window.prevent || $.renderTable();
            return;
        }
        //最外层window布局
        if(window == top) {
            var wider = $.wider().width();
            var higher = $.higher().height();
            if(defaultW>=wider){
              $('body').addClass('defaultW');
            }else{
              $('body').removeClass('defaultW');
            }
            //设置main的宽度
            main.css('width','auto');
            var _leftsidebar = leftsidebar.not(':hidden');
            var newWidth = $.wider().width() - (_leftsidebar.length>0?_leftsidebar.width():0);
            main.width(newWidth);
            //设置main的高度   
            var offset_top = leftsidebar.offset().top == 0? $(".topnav").outerHeight():leftsidebar.offset().top;
            var newHeight = $.higher().height() - offset_top;
            var oldHeight = leftsidebar.height() * 1;
            // if(newHeight != oldHeight) {
                main.height(newHeight);
                _leftsidebar.height(newHeight);
            // }
        }
    });
    $.calcStyle();

    //展开或收起搜索工具栏
    $(document).on('click', '.switch_search', function(e) {
        e.preventDefault();
        $('.search_form').slideToggle(300);
        var stmer = setTimeout(function(){$.calcStyle();clearTimeout(stmer);}, 300);
    });

})();
(function(){
  var ToolBar = {
    switchLeft: function(){
      //切换左侧导航条显示
      var isAnimating = false;
      var _main = $('.main');
      var _leftsidebar = $('.leftsidebar');
      $('body').on('click', '.switch_left', function() {
          if(!isAnimating) {
              isAnimating = true;
              if(_leftsidebar.is(':hidden')) {
                  _leftsidebar.show().animate({
                      left: 0
                  }, 200, function() {
                      isAnimating = false;
                  });
                  _main.animate({
                      left: '200px',
                      width: ($.wider().width() - 200) + 'px'
                  }, 200);
              } else {
                  _leftsidebar.animate({
                      left: '-200px'
                  }, 200, function() {
                      _leftsidebar.hide();
                      isAnimating = false;
                  });
                  _main.animate({
                      left: 0,
                      width: $.wider().width() + 'px'
                  }, 200);
                  
              }
              var stm2 = setTimeout(function() {
                  $.calcStyle();
                  clearTimeout(stm2);
              }, 250);
          }
      });
    },
    menuList: function(){
      //init
      var _lev1List = $('.leftsidebar .level_1');
      _lev1List.filter('.active').find('.open_close').removeClass('open').addClass('close');
      $('.level_1.active .level_2').show();
      $('.level_box.menu-show .level_menu').show();

      //level1
      $('.leftsidebar').on('click', '.level_tit', function(e) {
          var _this = $(this);
          var lev_parent = _this.parent('.level_box');
          lev_parent.siblings('.level_box.menu-show').removeClass('menu-show')
                .find('.level_menu').slideUp(200);

          if(lev_parent.find('.level_menu .level_1').length > 0){
              e.preventDefault();
              if(lev_parent.hasClass('menu-show')){
                  lev_parent.find('.level_menu').slideUp(200);
              }else{
                  lev_parent.find('.level_menu').slideDown(200);
              }
          }else{
            lev_parent.siblings('.level_box').each(function(){
              $(this).find('.level_1.active').removeClass('active');
              $(this).find('a.current').removeClass('current');
            })
          }
          lev_parent.toggleClass('menu-show');
      });

      //level2
      $('.leftsidebar').on('click', '.title_link', function(e) {
          var _this = $(this);
          var lev_parent = _this.parents('.level_box');
          var lev1 = _this.parent('.level_1');

          lev1.siblings('.level_1').filter('.active').each(function(){
            if($(this).find('.level_2').length>0){
              $(this).removeClass('active').find('.level_2').slideUp(200);
            }
          })
          lev1.siblings('.level_1').find('.open_close').removeClass('close').addClass('open');

          if(lev1.find('.level_2').length > 0){
              e.preventDefault();
              if(lev1.hasClass('active')) {
                  lev1.removeClass('active').find('.open_close').removeClass('close').addClass('open');;
                  lev1.find('.level_2').slideUp(200);
              }else{
                  lev1.addClass('active').find('.open_close').removeClass('open').addClass('close');;
                  lev1.find('.level_2').slideDown(200);
              }
          }else{
              $('.level_box').each(function(_k, _itm){
                $(_itm).find('.level_1').filter('.active').removeClass('active').find('.level_2').slideUp(200);
                $(_itm).find('.open_close').removeClass('close').addClass('open');
                $(_itm).find('a.current').removeClass('current');
              })
              lev1.addClass('active');
          }
      });

      //level3
      $('.leftsidebar').on('click', '.level_2 a', function(e){
          var _this = $(this);
          var lev_parent = _this.parents('.level_box');
          if(_this.attr('href') && _this.attr('href')!=''){
            lev_parent.siblings('.level_box').each(function(_k, _itm){
              $(_itm).find('.level_1').filter('.active').removeClass('active').find('.level_2').slideUp(200);
              $(_itm).find('.open_close').removeClass('close').addClass('open');
              $(_itm).find('a.current').removeClass('current');
            })
            _this.parents('.level_1').siblings('.level_1').removeClass('active');
            lev_parent.find('a.current').removeClass('current');
            _this.addClass('current');
          }
      });
    },
    init: function(){
      //布局切换及左侧菜单仅在最外层布局使用
      ToolBar.switchLeft();
      ToolBar.menuList();
    }
  };
  var switchStore = {
      closeAll: function(){
          var s = $(".switch-open");
          if(s.length>0){
              s.next("ul").slideUp(100);
              s.removeClass("switch-open");
          }
      },
      menuInit: function(){
          //切换门店
          $(document).on("click",".switch-store .site-links span",function(e){
              e.preventDefault();
              var _t = $(this);
              var _p = _t.parent(".site-links");
              if(_p.hasClass("switch-open")){
                  _p.removeClass("switch-open");
                  _p.next("ul").slideUp(100);
                  return false;
              }else{
                  _p.addClass("switch-open");
                  _p.next("ul").slideDown(100);
                  $(document).one("click", function(){
                      _p.removeClass("switch-open");
                      _p.next("ul").slideUp(100);
                  });
                  return false;
              }
          })
          $(document).on("click",".switch-store,.plat-cell",function(e){
              e.stopPropagation();
              var _t = $(this);
              if($(e.target).closest("a").length>0){
                  switchStore.closeAll();
              }
          })
      }
  }
  
  if(window == top){
    switchStore.menuInit();
    ToolBar.init();
  }
})();

(function(){
  //页面resize
  $(window).on('resize', function() {
    var resizeTimer = null;
    if (resizeTimer){clearTimeout(resizeTimer)};
    resizeTimer = resizeTimer ? null : setTimeout(function(){
      $.calcStyle();
    },300);
      
  });

  //初始a链接 link点击
  $(document).on('click', 'a', function(e) {
      var _t = $(this);
      if((_t.attr('href') || '').replace(/\#/g, '') === '') {
          e.preventDefault();
      }
  });
  
  //打开日期选择
  $(document).on('click','.input-text.date-ico',function(e){
      var target = e.target || e.srcElement;
      var _t = $(target);
      if(_t.hasClass('disabled')) return;
      var opt = _t.attr('data-options');
      var seconds = (typeof opt != 'undefined' && opt.indexOf('seconds:')>-1 && opt.split(':')[1]!='')?opt.split(':')[1]:false;
      if(eval(seconds.toString().toLowerCase())){
          WdatePicker({el:target,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true});
      }else{
          WdatePicker({el:target,readOnly:true});
      }
      //WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})
  })
   
  //全选 反选
  $(document).on('change', ':checkbox', function() {
      var _t = $(this);
      if(_t.hasClass('check-all')) {
          var _name = _t.attr('data-name');
          $(':checkbox[name="' + _name + '"]').not(_t).prop('checked', _t.prop('checked'));
      } else {
          var _name = _t.attr('name');
          $(':checkbox[data-name="' + _name + '"]').filter('.check-all').prop('checked', $(':checkbox[name="' + _name + '"]').not('.check-all').not(':checked').length == 0);
      }
  });

  //tab
  $(document).on('click','.tab_links a', function(e) {
      if(!$(this).parent('.tab_links').hasClass('onlylink')){
        e.preventDefault();
      }
  });
  $(document).on('tabload','.tab_links',function(e){
      Core.Tbox.load(this);
  });
  if($('.tab_links').length>0){
    $('.tab_links').each(function(index,item){
      if(!$(item).hasClass('onlylink')){
        $(item).trigger('tabload');
      }
    })
  }

  //关闭弹出层
  $.fn.extend({
    dialogClose: function(){
      if(window!==top){
        $('.layui-layer-shade', parent.document).fadeOut().remove();
        $('.layui-layer-move', parent.document).fadeOut().remove();
        $('.layui-layer-iframe', parent.document).fadeOut().remove();
      }
      var _this = $(this);
      if(_this.parents('.layui-layer-page').length>0){
        _this.parents('.layui-layer-page').find('.layui-layer-close')[0].click();
      }
    }
  })
  $(document).on('click', '.layui-layer-page a[href="#close"],.dialog_box a[href="#close"]', function(e) {
      e.preventDefault();
      if(window!==top){
        $('.layui-layer-shade', parent.document).fadeOut().remove();
        $('.layui-layer-move', parent.document).fadeOut().remove();
        $('.layui-layer-iframe', parent.document).fadeOut().remove();
      }
      $(this).parents('.layui-layer-page').find('.layui-layer-close')[0].click();
  });
})();