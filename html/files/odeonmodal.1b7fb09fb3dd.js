/**
 * @author bh-lay
 *
 * @github https://github.com/bh-lay/UI
 * @modified 2017-9-29 9:43
 *
 **/

(function (global, doc, UI_factory, BaseClass_factory, utils_factory) {

  //初始化工具类
  var utils = utils_factory(global, doc),
    BaseClass = BaseClass_factory(utils),
    //初始化UI模块
    Odeon = UI_factory(global, doc, utils, BaseClass);

  //提供window.UI的接口
  global.Odeon = global.Odeon || {};
  utils.clone(Odeon, global.Odeon);

  global.Odeon._utils = utils;

  //提供CommonJS规范的接口
  global.define && define(function () {
    return Odeon;
  });
})(window, document, function (window, document, utils, BaseClass) {
  /**
   * 缓存utils下常用工具
   *   为压缩变量名做准备
   */
  var isNum = utils.isNum,
    setCSS = utils.css,
    animation = utils.animation,
    outerWidth = utils.outerWidth,
    outerHeight = utils.outerHeight,
    findByClassName = utils.findByClassName,
    bindEvent = utils.bind;

  /**
   * 基础模版
   */
  var allCnt_tpl = '<div class="UI-lawyer"><div class="UI-mask"></div></div>',
    pop_tpl = '<div class="UI_pop"><% if(title){ %><div class="UI_pop_cpt"><%=title %></div><% } %><div class="UI_cnt"></div><a href="javascript:;" class="UI_pop_close" title="\u5173\u95ED">×</a></div>',
    confirm_tpl = '<div class="UI-confirm"><div class="UI-confirm-title"><%=title %></div><% if(icon){ %><div class="UI-confirm-icon"><%=icon%></div><% } %><div class="UI-confirm-content<% if(icon){ %> UI-confirm-with-icon<% } %>"><%=content %></div><div class="UI-confirm-footer"><% if(isShowCancelButton){ %><a href="javascript:;" class="UI-button UI-button-cancel"><%=cancelButtonText %></a><% } %><a href="javascript:;" class="UI-button UI-button-ok"><%=confirmButtonText %></a></div><a href="javascript:;" class="UI-close-btn" title="\u5173\u95ED">×</a></div>',
    notification_tpl = '<div class="UI-notification"><% if(icon){ %><div class="UI-notification-icon"><%=icon%></div><% } %><div class="UI-notification-body<% if(icon){ %> UI-notification-body-with-icon<% } %>"><div class="UI-notification-title"><%=title%></div><div class="UI-notification-message"><%=message%></div></div><a href="javascript:;" class="UI-close-btn" title="\u5173\u95ED">×</a></div>',
    popCSS = '.UI-lawyer{position:absolute;top:0;left:0;width:100%;height:0;overflow:visible}.UI-lawyer a,.UI-lawyer a:hover,.UI-lawyer a:active{outline:none;text-decoration:none;-webkit-tap-highlight-color:transparent}.UI-mask{position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0, 0, 0, .5);visibility:hidden;transition:.2s;opacity:0}.UI-mask-show{visibility:visible;opacity:1}.UI-noscroll{overflow:hidden}.UI_pop{width:200px;position:absolute;top:400px;left:300px;background:#fff;box-shadow:2px 3px 10px rgba(0, 0, 0, 0.6)}.UI_pop_cpt{position:relative;height:36px;line-height:36px;overflow:hidden;border-bottom:1px solid #ebebeb;color:#777;font-size:16px;text-indent:15px;cursor:default}.UI_pop .UI_cnt{position:relative;min-height:100px;overflow:auto}.UI-button{display:inline-block;line-height:1;white-space:nowrap;cursor:pointer;background:#fff;border:1px solid #c4c4c4;color:#1f2d3d;-webkit-appearance:none;text-align:center;box-sizing:border-box;outline:none;margin:0;-moz-user-select:none;-webkit-user-select:none;-ms-user-select:none;padding:10px 15px;font-size:14px;border-radius:4px;transition:.2s ease-in-out}.UI-button-ok{color:#fff;background-color:#20a0ff;border-color:#20a0ff}.UI-button-ok:hover{background:#0088f0;color:#8accfe;border-color:#0070c7}.UI-button-ok:active{background:#0070c7;color:#b4dfff}.UI-button-cancel{margin-right:10px;color:#818182}.UI-button-cancel:hover{background:#f7f7f7;color:#6d8ba2}.UI-button-cancel:active{background:#d8d4d4;color:#2c4c67}.UI-close-btn{display:block;position:absolute;top:12px;right:15px;width:30px;height:30px;text-align:center;color:#bfcbd9;font:200 30px/26px "simsun";transition:0.1s}.UI-close-btn:hover{color:#888}.UI-close-btn:active{color:#222}.UI-confirm{position:absolute;width:420px;background:#fff;overflow:hidden;text-align:left;vertical-align:middle;border-radius:3px;font-size:16px;-webkit-backface-visibility:hidden;backface-visibility:hidden}.UI-confirm-title{position:relative;padding:20px 20px 0}.UI-confirm-icon{position:absolute;width:40px;height:40px;top:45%;left:24px;margin-top:-20px}.UI-confirm-icon svg{display:block;width:100%}.UI-confirm-content{max-height:350px;padding:30px 20px;color:#48576a;font-size:14px;overflow:auto}.UI-confirm-with-icon{padding-left:80px}.UI-confirm-footer{padding:10px 20px 15px;text-align:right}.UI-confirm-text{padding:30px 10px 20px;line-height:26px;text-align:center;font-size:20px;color:#333}.UI_pop_confirm a{display:inline-block;width:50%;font-size:14px;line-height:36px;color:#03f;transition:0.15s}.UI_pop_confirm a:hover{background:#eee}.UI_pop_confirm_ok{border-right:1px solid #ddd}.UI-notification{position:fixed;width:330px;min-height:90px;right:16px;background:#fff;border-radius:2px;box-shadow:0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);transition:opacity .3s, transform .3s, right .3s, top .4s;-webkit-animation:UI-notification-in 0.2s ease-out both;animation:UI-notification-in 0.2s ease-out both}.UI-notification-icon{position:absolute;width:40px;height:40px;top:24px;left:24px}.UI-notification-icon svg{display:block;width:100%}.UI-notification-body{padding:20px}.UI-notification-body-with-icon{padding-left:80px}.UI-notification-title{padding-right:20px;font-weight:400;font-size:16px;color:#1f2d3d}.UI-notification-message{font-size:14px;line-height:21px;margin:10px 0 0;color:#8391a5;text-align:justify}.UI-notification-out{opacity:0}.UI-notification .UI_cnt{padding:30px 10px;font-size:18px;color:#333;text-align:center}@-webkit-keyframes UI-notification-in{0%{-webkit-transform:translateX(120%)}100%{-webkit-transform:translateX(0)}}@keyframes UI-notification-in{0%{transform:translateX(120%)}100%{transform:translateX(0)}}/** * CSS3动画 * **/@-webkit-keyframes UI-fadeInDown{0%{opacity:0;-webkit-transform:translateY(-15px)}100%{opacity:1;-webkit-transform:translateY(0)}}@keyframes UI-fadeInDown{0%{opacity:0;transform:translateY(-15px)}100%{opacity:1;transform:translateY(0)}}@-webkit-keyframes UI-fadeOutUp{0%{opacity:1;-webkit-transform:translateY(0)}100%{opacity:0;-webkit-transform:translateY(-15px)}}@keyframes UI-fadeOutUp{0%{opacity:1;transform:translateY(0)}100%{opacity:0;transform:translateY(-15px)}}.UI-fadeIn{-webkit-animation:UI-fadeInDown 0.2s ease both;animation:UI-fadeInDown 0.2s ease both}.UI-fadeOut{-webkit-animation:UI-fadeOutUp 0.2s ease both;animation:UI-fadeOutUp 0.2s ease both}';

  /**
   * 定义私有变量
   *
   **/
  var private_allCnt = utils.createDom(allCnt_tpl)[0],
    private_maskDom = findByClassName(private_allCnt, 'UI-mask')[0],
    private_body = document.body,
    private_root_node = document.compatMode == "BackCompat" ? private_body : document.documentElement,
    private_docW,
    private_winH,
    private_docH,
    private_scrollTop,
    private_config_zIndex = 2000,
    private_config_gap = {
      top: 0,
      left: 0,
      bottom: 0,
      right: 0
    },

    // 默认弹框动画
    private_config_defaultAnimationClass = ['UI-fadeIn', 'UI-fadeOut'];

//重新计算浏览器窗口尺寸
  function refreshSize() {
    private_scrollTop = private_root_node.scrollTop == 0 ? private_body.scrollTop : private_root_node.scrollTop;
    private_winH = window.innerHeight || document.documentElement.clientHeight;
    private_winW = window.innerWidth || document.documentElement.clientWidth;
    private_docH = private_root_node.scrollHeight;
    private_docW = private_root_node.clientWidth;
  }

  //记录当前正在显示的对象
  var active_objs = [];

  //从记录中移除对象
  function remove_active_obj(obj) {
    utils.each(active_objs, function (index, item) {
      if (item == obj) {
        active_objs.splice(index, 1);
        return false;
      }
    });
  }

  //关闭最后一个正在显示的易于关闭的对象
  function closeLastEasyCloseObj(type) {
    var isEscMatch, isClickMatch;
    for (var i = active_objs.length - 1; i >= 0; i--) {
      isEscMatch = type === 'esc' && active_objs[i].options &&  active_objs[i].options.closeOnPressEscape;
      isClickMatch = type === 'click' && active_objs[i]['closeOnClickModal'];

      if (isEscMatch || isClickMatch) {
        active_objs[i].destroy && active_objs[i].destroy();
        break;
      }
    }
  }

  //调整正在显示的对象的位置
  var adapt_delay;

  function adapt_active_obj() {
    clearTimeout(adapt_delay);
    adapt_delay = setTimeout(function () {
      utils.each(active_objs, function (index, item) {
        item.adaption && item.adaption();
      });
    }, 150);
  }


  /**
   * 处理对象易于关闭的扩展
   *   点击自身以外 or 按下esc
   */
  //检测body的mouseup事件
  bindEvent(private_body, 'mouseup', function checkClick(event) {
    var target = event.srcElement || event.target;
    setTimeout(function () {
      while (!utils.hasClass(target, 'UI-easyClose')) {
        target = target.parentNode;
        if (!target) {
          closeLastEasyCloseObj('click');
          break
        }
      }
    });
  });
  //检测window的keydown事件（esc）
  bindEvent(private_body, 'keyup', function checkClick(event) {
    if (event.keyCode === 27) {
      closeLastEasyCloseObj('esc');
    }
  });

  /**
   * 对象易于关闭方法拓展
   *   default_value 为默认参数
   */
  function closeOnClickModalHandle(obj, mark) {
    if (!mark) {
      return;
    }
    utils.addClass(obj.node, 'UI-easyClose');
    setTimeout(function () {
      obj.closeOnClickModal = true;
    });
  }

  //初始化组件基础功能
//  utils.ready(function(){
  //插入css样式表
  utils.createStyleSheet(popCSS, {'data-module': "UI"});

  //插入基础dom
  private_body.appendChild(private_allCnt);

  //释放掉无用的内存
  popCSS = null;
  allCnt_tpl = null;

  //更新窗口尺寸
  refreshSize();
  setTimeout(refreshSize, 500);

  function rebuild_fn() {
    refreshSize();
    adapt_active_obj();
  }

  //监听浏览器缩放、滚屏事件
  bindEvent(window, 'resize', rebuild_fn);
  bindEvent(window, 'scroll', rebuild_fn);
//  });

  var svgTypeConfig = {
    success: '<svg viewBox="157 158 710 710" version="1.1" xmlns="http://www.w3.org/2000/svg" fill="#13ce66"><path d="M512 160c-194.4 0-352 157.6-352 352 0 194.416 157.6 352 352 352s352-157.6 352-352C864 317.584 706.4 160 512 160zM727.072 410.864 485.76 647.184c-1.216 2.016-2.688 4.064-4.672 6.048l-11.312 11.312c-6.256 6.256-13.616 9.024-16.432 6.208l-114.944-120.288c-2.816-2.816-0.048-10.16 6.208-16.4l11.312-11.328c6.24-6.256 13.6-9.024 16.416-6.208l86.16 90.144 234.64-229.776c6.24-6.256 16.368-6.256 22.624 0l11.312 11.312C733.328 394.48 733.328 404.608 727.072 410.864z"></path></svg>',
    warning: '<svg viewBox="156 173 710 710" version="1.1" xmlns="http://www.w3.org/2000/svg" fill="#f7ba29"><path d="M512 176c-194.4 0-352 157.6-352 352 0 194.416 157.6 352 352 352s352-157.584 352-352C864 333.584 706.4 176 512 176zM512 720.592c-22.304 0-40.384-18.08-40.384-40.384 0-22.304 18.08-40.384 40.384-40.384 22.32 0 40.384 18.096 40.384 40.384C552.384 702.512 534.32 720.592 512 720.592zM534.256 589.504c-3.504 10.992-12.32 18-22.256 18-9.952 0-19.248-8.512-22.256-18C486.752 579.984 464 431.68 464 368.688c0-16 16-48.992 47.504-48.992S560 352.192 560 368.688C560 432.176 537.744 578.496 534.256 589.504z" ></path></svg>',
    info: '<svg viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" fill="#50bfff"><path d="M512 0C229.242435 0 0 229.242435 0 512S229.242435 1024 512 1024 1024 794.757565 1024 512 794.757565 0 512 0zM578.782609 734.608696a66.782609 66.782609 0 0 1-133.565218 0v-267.130435a66.782609 66.782609 0 1 1 133.565218 0v267.130435zM512 356.173913a66.782609 66.782609 0 1 1 0.044522-133.609739A66.782609 66.782609 0 0 1 512 356.173913z"></path></svg>',
    error: '<svg viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" fill="#ff4949"><path d="M511.360799 0C229.345318 0 0.681814 228.663504 0.681814 510.678985c0 282.015481 228.663504 510.678985 510.678985 510.678985s510.678985-228.663504 510.678985-510.678985C1022.039784 228.663504 793.37628 0 511.360799 0zM720.336912 690.251852c13.465834 13.465834 13.551061 35.283895 0.340907 48.579276L711.047191 748.461756C697.75181 761.757137 676.104203 761.586683 662.723596 748.120849L511.275572 596.076238 359.401415 747.268581c-13.465834 13.465834-35.283895 13.551061-48.579276 0.340907L301.191511 737.97886C287.89613 724.683479 287.981357 703.035872 301.532418 689.655264l152.044611-151.448023L302.384686 386.333084c-13.465834-13.465834-13.551061-35.283895-0.340907-48.579276l9.630628-9.630628C324.969788 314.827799 346.617395 314.998252 359.998002 328.464087l151.448023 152.044611 151.788931-151.192343c13.465834-13.465834 35.283895-13.551061 48.579276-0.340907l9.630628 9.630628c13.295381 13.295381 13.210154 34.857761-0.340907 48.323596L569.144569 538.377695 720.336912 690.251852z"></path></svg>'
  };
  function iconTypeToSvg(type){
    return svgTypeConfig[type] || null;
  }
  //限制位置区域的方法
  function fix_position(top, left, width, height) {
    var gap = private_config_gap;
    if (top < private_scrollTop + gap.top) {
      //屏幕上方
      top = private_scrollTop + gap.top;
    } else if (top + height - private_scrollTop > private_winH - gap.bottom) {
      //屏幕下方
      if (height > private_winH - gap.top - gap.bottom) {
        //比屏幕高
        top = private_scrollTop + gap.top;
      } else {
        //比屏幕矮
        top = private_scrollTop + private_winH - height - gap.bottom;
      }
    }
    if (left < gap.left) {
      left = gap.left;
    } else if (left + width > private_docW - gap.right) {
      left = private_docW - width - gap.right;
    }

    return {
      top: Math.ceil(top),
      left: Math.ceil(left)
    }
  }

  //为基类扩展自适应于页面位置的原型方法
  BaseClass.prototype.adaption = function () {

    var initTop,
      initLeft,
      useMethod = animation,
      initPosition = this._initPosition;

    if (initPosition) {
      initTop = initPosition.top;
      initLeft = initPosition.left;
      // 初始时不使用动画
      useMethod = setCSS;
      // 删除初始位置
      delete this._initPosition;
    }
    var node = this.node,
      width = outerWidth(node),
      height = outerHeight(node),
      top = isNum(initTop) ? initTop : (private_winH - height) / 2 + private_scrollTop,
      left = isNum(initLeft) ? initLeft : (private_docW - width) / 2,
      newPosition = fix_position(top, left, width, height);

    useMethod(node, {
      top: Math.ceil(newPosition.top),
      left: Math.ceil(newPosition.left)
    }, 80);
  };


  //最后一个有蒙层的对象
  function last_has_mask_item() {
    //逆序遍历所有显示中的对象
    for (var i = active_objs.length - 1; i >= 0; i--) {
      //判断是否含有蒙层
      if (active_objs[i]._mask) {
        return active_objs[i];
      }
    }
    return null;
  }

  //最后一个有蒙层的对象的zIndex值，
  function last_has_mask_zIndex() {
    var item = last_has_mask_item();
    return item ? item._zIndex : private_config_zIndex; // 无则返回默认值
  }

  /**
   * 开场动画
   **/
  function openAnimation() {
    var me = this,
      lastHasMaskZindex = last_has_mask_zIndex();

    me._zIndex = lastHasMaskZindex + 2;

    setCSS(me.node, {
      zIndex: me._zIndex
    });

    // 若有蒙层则显示蒙层
    if (me._mask) {
      setCSS(private_maskDom, {
        zIndex: lastHasMaskZindex + 1
      });

      //之前蒙层未显示，显示蒙层
      if (lastHasMaskZindex <= private_config_zIndex) {
        utils.addClass(private_maskDom, 'UI-mask-show');
      }
    }

    //向全局记录的对象内添加对象
    active_objs.push(me);
    // 有动画配置，显示效果
    if (me.animationClass) {
      utils.addClass(me.node, me.animationClass[0]);
    }
  }

  /**
   * 处理对象关闭及结束动画
   */
  function closeAnimation() {
    var me = this,
      DOM = me.node,
      animationClass = me.animationClass[1];

    //从全局记录的对象内删除自己；
    remove_active_obj(me);

    // 若有蒙层，则关闭或移至下一个需要显示蒙层的位置
    if (me._mask) {
      var lastHasMaskZindex = last_has_mask_zIndex();
      setCSS(private_maskDom, {
        zIndex: lastHasMaskZindex - 1
      });

      if (lastHasMaskZindex <= private_config_zIndex) {
        utils.removeClass(private_maskDom, 'UI-mask-show');
      }
    }

    function end() {
      //删除dom
      utils.removeNode(DOM);
    }

    //ie系列或未配置动画class，立即结束
    if (!animationClass) {
      end();
    } else {
      utils.addClass(DOM, animationClass);
      setTimeout(end, 500);
    }
  }

  // 过滤参数
  function filterParam(param, defaults) {
    param = param || {};
    // 动画定义
    this.animationClass = ( param.animationClass || '' ).constructor == Array ? param.animationClass : private_config_defaultAnimationClass;
    // 蒙层参数
    this._mask = typeof( param.mask ) == 'boolean' ? param.mask : defaults.mask;
    // 初始位置
    this._initPosition = {
      top: param.top,
      left: param.left
    };
  }

  /**
   * 弹框
   * pop
   */
  function POP(param) {
    if (!(this instanceof POP)) {
      return new POP(param);
    }
    param = param || {};
    var me = this;
    filterParam.call(me, param, {
      mask: true
    });
    me.node = utils.createDom(utils.render(pop_tpl, {
      title: param.title
    }))[0];
    me.cntDom = findByClassName(me.node, 'UI_cnt')[0];


    //处理title参数
    if (param.title) {
      //can drag is pop
      utils.drag(findByClassName(me.node, 'UI_pop_cpt')[0], me.node, {
        move: function (mx, my, l_start, t_start, w_start, h_start) {
          var left = mx + l_start,
            top = my + t_start,
            newSize = fix_position(top, left, w_start, h_start);
          setCSS(me.node, {
            left: newSize.left,
            top: newSize.top
          });
        }
      });
    }

    bindEvent(me.node, 'click', '.UI_pop_close', function () {
      me.destroy();
    });

    //插入内容
    me.cntDom.innerHTML = param.html || '';

    //设置宽度，为计算位置尺寸做准备
    setCSS(me.node, {
      width: Math.min(param.width || 600, private_docW - 20)
    });
    private_allCnt.appendChild(me.node);

    //校正位置
    me.adaption();

    //处理是否易于关闭
    closeOnClickModalHandle(me, param.easyClose);

    //开场动画
    openAnimation.call(me);
  }

  POP.prototype = new BaseClass({
    destroy: closeAnimation
  });

  /**
   * CONFIRM
   */
  function CONFIRM(param) {
    if (!(this instanceof CONFIRM)) {
      return new CONFIRM(param);
    }
    var defaults = {
      title: '提示',
      content: '请输入内容',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      closeOnPressEscape: true,
      closeOnClickModal: true,
      isShowCancelButton: true
    };
    param = param || {};
    var options = utils.clone(param, defaults);
    options.icon = iconTypeToSvg(options.type);
    var me = this;
    this.options = options;
       // 动画定义
    this.animationClass = private_config_defaultAnimationClass;
    // 蒙层参数
    this._mask = true;

    // 是否点击了确定按钮
    this.isClickConfirm = false;
    this.node = utils.createDom(utils.render(confirm_tpl, options))[0];

    //绑定事件，根据执行结果判断是否要关闭弹框
    bindEvent(this.node, 'click', '.UI-button-ok', function () {
      me.isClickConfirm = true;
      //点击确认按钮
      me.destroy();
    });
    bindEvent(this.node, 'click', '.UI-button-cancel', function () {
      //点击取消按钮
      me.destroy();
    });
    bindEvent(this.node, 'click', '.UI-close-btn', function () {
      //点击关闭按钮
      me.destroy();
    });

    private_allCnt.appendChild(me.node);

    me.adaption();

    //处理是否易于关闭
    closeOnClickModalHandle(me, options.closeOnClickModal);
    openAnimation.call(me);
    this.on('destroy', function(){
      if(this.isClickConfirm){
        options.onConfirm && options.onConfirm();
      }else{
        options.onCancel && options.onCancel();
      }
    });
  }

  CONFIRM.prototype = new BaseClass({
    destroy: closeAnimation
  });


  var notification_list = [];

  function adjustNotification() {
    var lastPositionBottom = 16;
    notification_list.forEach(function (item) {
      utils.css(item.node, {
        top: lastPositionBottom,
        zIndex: private_config_zIndex
      });
      lastPositionBottom += item.node.offsetHeight + 16;
    })
  }

  //从记录中移除对象
  function removeNotificationFromList(obj) {
    utils.each(notification_list, function (index, item) {
      if (item === obj) {
        notification_list.splice(index, 1);
        return false;
      }
    });
  }

  /**
   * Notification
   * @param param
   * @returns {Notification}
   * @constructor
   */
  function Notification(param) {
    if (!(this instanceof Notification)) {
      return new Notification(param);
    }
    var defaults = {
      title: '提示',
      message: '请输入提示内容'
    };
    var options = utils.clone(param, defaults);
    options.icon = iconTypeToSvg(options.type);

    var me = this;
    var html = utils.render(notification_tpl, options);
    filterParam.call(me, param, {
      mask: false
    });
    me.node = utils.createDom(html)[0];

    if (options.duration !== 0) {
      setTimeout(function () {
        me.destroy();
      }, (options.duration || 4500));
    }

    bindEvent(me.node, 'click', '.UI-close-btn', function () {
      me.destroy();
    });
    // create pop
    private_allCnt.appendChild(me.node);

    notification_list.push(this);
    adjustNotification();
  }

  Notification.prototype = new BaseClass({
    destroy: function closeAnimation() {
      var DOM = this.node;
      //
      removeNotificationFromList(this);
      adjustNotification();
      utils.addClass(DOM, 'UI-notification-out');
      setTimeout(function () {
        //删除dom
        utils.removeNode(DOM);
      }, 600);
    }
  });

  /**
   *  抛出对外接口
   */
  return {
    pop: POP,
    config: {
      gap: function (name, value) {
        //name符合top/right/bottom/left,且value值为数字类型（兼容字符类型）
        if (name && typeof(private_config_gap[name]) == 'number' && isNum(value)) {
          private_config_gap[name] = parseInt(value);
        }
      },
      setDefaultAnimationClass: function (startClassStr, endClassStr) {
        private_config_defaultAnimationClass[0] = startClassStr;
        endClassStr && (private_config_defaultAnimationClass[1] = endClassStr);
      },
      zIndex: function (num) {
        var num = parseInt(num);
        if (num > 0) {
          private_config_zIndex = num;
          setCSS(private_allCnt, {
            zIndex: num
          });
        }
      }
    },
    Confirm: CONFIRM,
    Notification: Notification
  };
}, function(){
	function isFunction( input ){
		return typeof( input ) === 'function'
	}
	function isNotEmptyString( input ){
		return typeof( input ) === 'string' && input.length > 0
	}


	function BaseClass( param ){
		if( !param || !isFunction( param.destroy ) ){
	      throw new Error("use BaseClass must define param & param.destroy");
		}
		param = param || {};
		// 切勿在此处定义事件集合
		// 避免实例化在其他原型链上导致内存共享的问题
		// this._events = {};
		this._isDestroyed = false;
		this._onDestroy = param.destroy;
	}
	BaseClass.prototype = {
		//监听自定义事件
		on: function( eventName, callback ){
			this._events = this._events || {}
			if( isNotEmptyString( eventName ) && isFunction( callback ) ){
				//事件集合无该事件，创建一个事件集合
				this._events[eventName] = this._events[eventName] || [];
				// 追加至事件列表
				this._events[eventName].push( callback );
			}
			//提供链式调用的支持
			return this;
		},
		//解除自定义事件监听
		un: function( eventName, callback ){
			this._events = this._events || {}
			var eventList = this._events[eventName];
			//事件集合无该事件队列，或未传入事件名结束运行
			if( !eventList || !isNotEmptyString( eventName ) ){
				return
			}
			// 若未传入回调参数，则直接置空事件队列
			if( !isFunction( callback ) ){
				eventList = [];
			}else{
				// 逆序遍历事件队列
				for( var i = eventList.length-1; i!=-1; i-- ){
					// 回调相同，移除当前项
					if( eventList[i] == callback ){
						eventList.splice(i,1);
					}
				}
			}
			//提供链式调用的支持
			return this;
		},
		// 主动触发自定义事件
		emit: function( eventName ){
			this._events = this._events || {}
			// 获取除了事件名之外的参数
			var args = Array.prototype.slice.call( arguments, 1, arguments.length );
			//事件集合无该事件，结束运行
			if(!this._events[eventName]){
				return
			}
			for(var i=0,total=this._events[eventName].length;i<total;i++){
				this._events[eventName][i].apply( this, args );
			}
		},
		// 保证只有一遍有效执行
		destroy: function(){
			if( this._isDestroyed ){
				return;
			}
			this._isDestroyed = true;
			this._onDestroy.call( this );
			this.emit( 'destroy' );
		}
	};
	return BaseClass;
}, function (window,document) {
  /**
   * 判断对象类型
   * string number array
   * object function 
   * htmldocument
   * undefined null
   */
  function TypeOf(obj) {
    return Object.prototype.toString.call(obj).match(/\s(\w+)/)[1].toLowerCase();
  }

  /**
   * 检测是否为数字
   * 兼容字符类数字 '23'
   */
  function isNum(ipt){
    return (ipt !== '') && (ipt == +ipt) ? true : false;
  }

  /**
   * 遍历数组或对象
   * 
   */
  function each(arr,fn){
    //检测输入的值
    if(typeof(arr) != 'object' || typeof(fn) != 'function'){
      return;
    }
    var Length = arr.length;
    if( isNum(Length) ){
      for(var i=0;i<Length;i++){
        if(fn.call(this,i,arr[i]) === false){
          break
        }
      }
    }else{
      for(var i in arr){
        if (!arr.hasOwnProperty(i)){
          continue;
        }
        if(fn.call(this,i,arr[i]) === false){
          break
        }
      }
    }
  }

  /**
   * 对象拷贝
   *
   */
  function clone(fromObj,toObj){
    each(fromObj,function(i,item){
      if(typeof item == "object"){   
        toObj[i] = item.constructor==Array ? [] : {};

        clone(item,toObj[i]);
      }else{
        toObj[i] = item;
      }
    });

    return toObj;
  }	
  /**
   * 判断是否支持css属性
   * 兼容css3
   */
  var supports = (function() {
    var styles = document.createElement('div').style,
        vendors = 'Webkit Khtml Ms O Moz'.split(/\s/);

    return function(prop) {
      var returns = false;
      if ( prop in styles ){
        returns = prop;
      }else{
        prop = prop.replace(/^[a-z]/, function(val) {
          return val.toUpperCase();
        });
        each(vendors,function(i,value){
          if ( value + prop in styles ) {
            returns = ('-' + value + '-' + prop).toLowerCase();
          }
        });
      }
      return returns;
    };
  })();

  /**
   * class 操作
   */
  var private_css3 = !!(supports('transition') && supports('transform')),
      supports_classList = !!document.createElement('div').classList,
      // 是否含有某个 class
      hasClass = supports_classList ? function( node, classSingle ){
        return node && node.classList && node.classList.contains( classSingle );
      } : function ( node, classSingle ){
        if( !node || typeof( node.className ) !== 'string'  ){
          return false;
        }
        return !! node.className.match(new RegExp('(\\s|^)' + classSingle + '(\\s|$)'));
      },
      // 增加一个 class
      addClass = supports_classList ? function( node, classSingle ){
        node && node.classList && node.classList.add( classSingle );
      } : function ( node, cls) {
        !hasClass(node, cls) && ( node.className += " " + cls );
      },
      // 移除一个 class
      removeClass = supports_classList ? function ( node, classSingle ) {
        node && node.classList && node.classList.remove( classSingle );  
      } : function ( node, classSingle ) {
        if ( hasClass( node, classSingle ) ) {
          node.className = node.className.replace( new RegExp('(\\s+|^)' + classSingle + '(\\s+|$)'), '' );
        }
      };
  //获取样式
  function getStyle(elem, prop) {
    var value;
    prop == "borderWidth" ? prop = "borderLeftWidth" : prop;
    if (elem.style[prop]){
      value = elem.style[prop];
    } else if(document.defaultView) {
      var style = document.defaultView.getComputedStyle(elem, null);
      value = prop in style ? style[prop] : style.getPropertyValue(prop);
    } else if (elem.currentStyle) {
      value = elem.currentStyle[prop];
    }


    if (/\px$/.test(value)){
      value = parseInt(value);
    }else if (isNum(value) ){
      value = Number(value);
    } else if(value == '' || value == 'medium'){
      value = 0;
    } else if (value == 'auto'){
      if(prop == 'height'){
        value = elem.clientHeight;
      }else if(prop == 'width'){
        value = elem.clientWidth;
      }
    }

    return value;
  }


  /**
   * dom设置样式
   */
  function setStyle(elem,prop,value){
    prop = prop.toString();
    if (prop == "opacity") {
      elem.style.filter = 'alpha(opacity=' + (value * 100)+ ')';
      value = value;
    } else if ( isNum(value) && prop != 'zIndex'){
      value = value + "px";
    }
    elem.style[prop] = value;
  }
  //设置css
  function setCss(doms,cssObj){
    doms = [].concat(doms);

    /**
     * 为css3属性增加扩展
     */
    each(cssObj,function(key,value){
      if(key == 'transform' || key == 'transition'){
        each(['webkit','o','moz'],function(i,text){
          cssObj['-' + text + '-' + key] = value
        });
      }
    });
    each(doms,function(i,dom){
      each(cssObj,function(key,value){
        setStyle(dom,key,value);
      });
    });
  }

  /**
   * css3动画
   * 内部类，不检测参数
   */
  function css3_anim(elem,cssObj,durtime,animType,onEnd){
    //记录初始transition值
    var transition_start = getStyle(elem,'transition');
    var cssSet = clone(cssObj,{
        transition: durtime + 'ms ' + animType
    });

    //开启3d加速
    if(!cssSet.transform){
      cssSet.transform = 'translate3d(0, 0, 0)';
    }else if(!cssSet.transform.match('translate3d')){
      cssSet.transform = cssSet.transform + ' translate3d(0, 0, 0)';
    }
    /**
     * 动画结束回调
     */
    function endFn(){
      endFn = null;
      elem.removeEventListener("webkitTransitionEnd",transitionFn, true);
      //还原transition值
      setCss(elem,{
          transition: transition_start || 'all 0s'
      });
      onEnd && onEnd.call(elem);
    }

    /**
     * 高大上的webkitTransitionEnd
     *   动画过程中，在每一帧持续触发
     */
    var delay;
    function transitionFn(){
      clearTimeout(delay);
      delay = setTimeout(function(){
        endFn && endFn();
      },40);
    }
    elem.addEventListener("webkitTransitionEnd",transitionFn, true);

    /**
     * 加一份保险
     *   解决 css无变化时webkitTransitionEnd事件不会被触发的问题
     */
    setTimeout(function(){
      endFn && endFn();
    },durtime + 80);

    /**
     * 不知道为啥，若刚设置完css再修改同一属性，firefox下没效果
     *   可能是浏览器优化css动画的逻辑
     *	 故加定时器解决此bug
     */
    setTimeout(function(){
      setCss(elem,cssSet);
    },10);
  }
  /**
   * css3动画
   * @param elem dom对象
   * @param cssObj 动画对象
   * @param durtime 持续时间
   * @param [animType] 缓动类型
   * @param [callback] 回调
   */
  function animation(elem,cssObj,durtime,a,b) {
    var animType = "linear",
        onEnd = null;

    if (arguments.length < 3) {
      throw new Error("missing arguments [dom,cssObj,durtime]");
    } else {
      if (TypeOf(a) == "function") {
        onEnd = a;
      }else if (typeof (a) == "string") {
        animType = a;
      }

      if (TypeOf(b) == "function") {
        onEnd = b;
      }
    }
    if(private_css3){
      return css3_anim(elem,cssObj,durtime,animType,onEnd);
    }else{
      setCss(elem,cssObj);
      onEnd && onEnd.call(elem);
    }
  }
  var isSupportGBCR = !!document.createElement('div').getBoundingClientRect,
      //用生命在计算宽度
      count_outerWidth = function ( node ){
        return (getStyle(node,'borderLeftWidth') + getStyle(node,'paddingLeft') + getStyle(node,'width') + getStyle(node,'paddingRight') + getStyle(node,'borderRightWidth'));
      },
      //用生命在计算高度
      count_outerHeight = function ( node ){
        return (getStyle(node,'borderTopWidth') + getStyle(node,'paddingTop') + getStyle(node,'height') + getStyle(node,'paddingBottom') + getStyle(node,'borderBottomWidth'));
      },
      // 外部宽度
      outerWidth = isSupportGBCR ? function( node ){
        var output = node.getBoundingClientRect().width;
        return typeof(output) == 'number' ? output : count_outerWidth( node );
      } : count_outerWidth,
      // 外部高度
      outerHeight = isSupportGBCR ? function( node ){
        var output = node.getBoundingClientRect().height;
        return typeof(output) == 'number' ? output : count_outerHeight( node );
      } : count_outerHeight;

  var supportEventListener = !!window.addEventListener,
    /**
     * 事件绑定
     * elem:节点
     * type:事件类型
     * handler:回调
     */
    bindHandler = supportEventListener ? function(elem, type, handler) {
      // 最后一个参数为true:在捕获阶段调用事件处理程序
      //为false:在冒泡阶段调用事件处理程序
      elem.addEventListener(type, handler, false);
    } : function(elem, type, handler) {
        elem.attachEvent("on" + type, handler);
    },
    /**
     * 事件解除
     * elem:节点
     * type:事件类型
     * handler:回调
     */
    removeHandler = supportEventListener ? function(elem, type, handler) {
      elem.removeEventListener(type, handler, false);
    } : function(elem, type, handler) {
      elem.detachEvent("on" + type, handler);
    };

  function checkEventForClass(event,classStr,dom){
    var target = event.srcElement || event.target;
    while (1) {
      if(target == dom || !target){
        return false;
      }
      if(hasClass(target,classStr)){
        return target;
      }

      target = target.parentNode;
    }
  }
  function bind(elem, type,a,b){
    var className,fn;
    if(typeof(a) == 'string'){
      className = a.replace(/^\./,'');
      fn = b;
      callback = function(e){
        var bingoDom = checkEventForClass(e,className,elem);
        if(bingoDom){
          fn && fn.call(bingoDom);
        }
      };
    }else{
      callback = a;
    }
    bindHandler(elem,type,callback);
  }


  return {
    TypeOf : TypeOf,
    isNum : isNum,
    each : each,
    getStyle : getStyle,
    css : setCss,
    animation : animation,
    supports : supports,
    outerWidth : outerWidth,
    outerHeight : outerHeight,
    bind : bind,
    clone : clone,
    unbind : removeHandler,
    hasClass : hasClass,
    addClass : addClass,
    removeClass : removeClass,
    /**
     * 页面加载
     */
    ready : (function(){
      var readyFns = [];
      function completed() {
        removeHandler(document,"DOMContentLoaded", completed);
        removeHandler(window,"load", completed);
        each(readyFns,function(i,fn){
          fn();
        });
        readyFns = null;
      }
      return function (callback){
        if ( document.readyState === "complete" ) {
          callback && callback();
        } else {
          callback && readyFns.push(callback);

          bindHandler(document,'DOMContentLoaded',completed);
          bindHandler(window,'load',completed);
        }
      }
    })(),
    //通用拖动方法
    drag : function drag(handle_dom,dom,param){
      var param = param || {};
      var onStart = param.start || null;
      var onMove = param.move || null;
      var onEnd = param.end || null;

      var X, Y,L,T,W,H;
      bindHandler(handle_dom,'mousedown',function (e){
        e.preventDefault && e.preventDefault();
        e.stopPropagation && e.stopPropagation();
        X = e.clientX;
        Y = e.clientY;
        L = getStyle(dom,'left');
        T = getStyle(dom,'top');
        W = outerWidth(dom);
        H = outerHeight(dom);
        onStart && onStart.call(dom,X,Y);
        bindHandler(document,'mousemove',move);
        bindHandler(document,'mouseup',up);
      });

      function move(e){
        onMove && onMove.call(dom,(e.clientX - X),(e.clientY - Y),L,T,W,H);
        //做了点儿猥琐的事情，你懂得
        e.preventDefault && e.preventDefault();
        e.stopPropagation && e.stopPropagation();
        window.getSelection?window.getSelection().removeAllRanges():document.selection.empty();
      }
      function up(e) {
        removeHandler(document,'mousemove',move);
        removeHandler(document,'mouseup',up);
        onEnd && onEnd.call(dom);
      }
    },
    //创建dom
    createDom : function (html){
      var a = document.createElement('div');
      a.innerHTML = html;
      return a.childNodes;
    },
    //在指定DOM后插入新DOM
    insertAfter : function (newElement, targetElement){
      var parent = targetElement.parentNode;
      if (parent.lastChild == targetElement) {
        //如果最后的节点是目标元素，则直接追加
        parent.appendChild(newElement);
      } else {
        //插入到目标元素的下一个兄弟节点之前
        parent.insertBefore(newElement, targetElement.nextSibling);
      }
    },
    //移除dom节点
    removeNode : function (elem){  
      if(elem && elem.parentNode && elem.tagName != 'BODY'){  
        elem.parentNode.removeChild(elem);  
      }  
    },
    //创建style标签
    createStyleSheet : function (cssStr,attr){
      var styleTag = document.createElement('style');

      attr = attr || {};
      attr.type = "text/css";
      //设置标签属性
      each(attr,function(i,value){
        styleTag.setAttribute(i, value);
      });

      // IE
      if (styleTag.styleSheet) {
        styleTag.styleSheet.cssText = cssStr;
      } else {
        var tt1 = document.createTextNode(cssStr);
        styleTag.appendChild(tt1);
      }
      //插入页面中
      (document.head || document.getElementsByTagName('head')[0]).appendChild(styleTag);
      return styleTag;
    },
    //根据class查找元素
    findByClassName : (function(){
      if(typeof(document.getElementsByClassName) !== 'undefined'){
        //支持gEbCN
        return function (dom,classStr){
          return dom.getElementsByClassName(classStr);
        };
      }else{
        //无奈采用遍历法
        return function (dom,classStr){
          var returns = [];
          //尝试获取所有元素
          var caches = dom.getElementsByTagName("*");
          //遍历结果
          each(caches,function(i,thisDom){
            //检查class是否合法
            if(hasClass(thisDom,classStr)){
              returns.push(thisDom);
            }
          });
          return returns;
        };
      }
    })(),
     //读取dom在页面中的位置
    offset : function (elem){
      var box = {
        top : 0,
        left : 0,
        screen_top : 0,
        screen_left : 0
      },
      size;

      if (typeof(elem.getBoundingClientRect) !== 'undefined' ) {
        size = elem.getBoundingClientRect();
      }
      box.screen_top = size.top;
      box.screen_left = size.left;

      box.top = size.top + (document.documentElement.scrollTop == 0 ? document.body.scrollTop : document.documentElement.scrollTop);
      box.left = size.left + document.body.scrollLeft;

      return box;
    },
    //淡入
    fadeIn : function (DOM,time,fn){
      var op = getStyle(DOM,'opacity');
      setCss(DOM,{
        opacity: 0,
        display: 'block'
      });
      animation(DOM,{
        opacity: op
      }, time, function(){
        fn && fn.call(DOM);
      });
    },
    //淡出
    fadeOut : function (DOM,time,fn){
      var op = getStyle(DOM,'opacity');
      animation(DOM,{
        opacity: 0
      }, time,function(){
        DOM.style.opacity = op;
        DOM.style.display = 'none';
        fn && fn.call(DOM);
      });
    },
    render : function (str, data){
      if(!str || !data){
        return '';
      }
      return (new Function("obj",
        "var p=[];" +
        "with(obj){p.push('" +
        str
        .replace(/[\r\t\n]/g, " ")
        .split("<%").join("\t")
        .replace(/((^|%>)[^\t]*)'/g, "$1\r")
        .replace(/\t=(.*?)%>/g, "',$1,'")
        .split("\t").join("');")
        .split("%>").join("p.push('")
        .split("\r").join("\\'")
      + "');}return p.join('');"))(data);
    }
  }
});