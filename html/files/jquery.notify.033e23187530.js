// Licensed to Cloudera, Inc. under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  Cloudera, Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
/*
 * jHue notify plugin
 *
 */
;
(function ($, window, document, undefined) {

  var pluginName = "jHueNotify",
      TYPES = {
        INFO: "INFO",
        ERROR: "ERROR",
        GENERAL: "GENERAL"
      },
      defaults = {
        level: TYPES.GENERAL,
        message: "",
        sticky: false,
        css: null
      };

  function Plugin(options) {
    this.options = $.extend({}, defaults, options);
    this._defaults = defaults;
    this._name = pluginName;
    this.show();
  }

  Plugin.prototype.setOptions = function (options) {
    this.options = $.extend({}, defaults, options);
  };

  Plugin.prototype.show = function () {
    var _this = this;
    var MARGIN = 4;
    _this.options.message = $("<span>").text(_this.options.message).html();
    if (_this.options.level == TYPES.ERROR) {
       $.post("/help/postlogs", {
          'messages': _this.options.message,
        }, function (data) {
        }
    ).fail(function (xhr, textStatus, errorThrown) {
       $(document).trigger("error", JSON.parse(xhr.responseText).message);
    });
    }
    var pre_title =  (_this.options.message).indexOf("&lt;title&gt;")
    var end_title =  (_this.options.message).indexOf("&lt;/title&gt;")
    if(pre_title!=-1 && end_title!=-1)  {
         _this.options.message=_this.options.message.substring(pre_title+13,end_title)
         _this.options.message=_this.options.message.replace("&amp;#39;","'")
         _this.options.message=_this.options.message.replace("&amp;#39;","'")
    }

   /* alert(_this.options.message)
    _this.options.message = $("<span>").text(_this.options.message).text(); // escape HTML messages
    alert(_this.options.message)*/
    var oDiv=$('<div></div>');
    var el2 = $("#jHueNotify").clone();
    // stops all the current animations and resets the style
    el2.stop(true);
    var id;
    if (_this.options.level == TYPES.ERROR) {
     oDiv.attr("id", "error_model");
     oDiv.attr("data-title", "错误");
    }
    if (_this.options.level == TYPES.INFO) {
      oDiv.attr("id", "info_model");
       oDiv.attr("data-title", "提示");
    }
    if (_this.options.level == TYPES.GENERAL) {
      oDiv.attr("id", "other_model");
       oDiv.attr("data-title", "警告");
    }
    oDiv.appendTo($("body"));
    $("#"+ oDiv.attr("id")).createModal({
                         background: "#000",//设定弹窗之后的覆盖层的颜色
                         width: "600px",//设定弹窗的宽度
                         height: "150px",//设定弹窗的高度
                         resizable: true,//设定弹窗是否可以拖动改变大小
                         move: false,//规定弹窗是否可以拖动
                         bgClose: false,//规定点击背景是否可以关闭
                         html: "<div class='modal-promot-mess modal-pro-me'>"+_this.options.message+"</div>"  +
                         "<p class='insure-btn-con'><span class='cancel-btn modal-close'>确定</span></p>",
                     })

   /* var el = $("#jHueNotify").clone();
    el.removeAttr("id");

    // stops all the current animations and resets the style
    el.stop(true);
    el.attr("class", "alert jHueNotify");
    el.find(".close").hide();

    if ($(".jHueNotify").last().position() != null) {
      el.css("top", $(".jHueNotify").last().position().top + $(".jHueNotify").last().outerHeight() + MARGIN);
    }

    if (_this.options.level == TYPES.ERROR) {
      el.addClass("alert-error");
    }
    else if (_this.options.level == TYPES.INFO) {
      el.addClass("alert-info");
    }
    el.find(".message").html("<strong>" + _this.options.message + "</strong>");

    if (_this.options.css != null) {
      el.attr("style", _this.options.css);
    }

    if (_this.options.sticky) {
      el.find(".close").click(function () {
        el.fadeOut();
        el.nextAll(".jHueNotify").animate({
          top: '-=' + (el.outerHeight() + MARGIN)
        }, 200);
        el.remove();
      }).show();
      el.show();
    }
    else {
      var t = window.setTimeout(function () {
        el.fadeOut();
        el.nextAll(".jHueNotify").animate({
          top: '-=' + (el.outerHeight() + MARGIN)
        }, 200);
        el.remove();

      }, 3000);
      el.click(function () {
        window.clearTimeout(t);
        $(this).stop(true);
        $(this).fadeOut();
        $(this).nextAll(".jHueNotify").animate({
          top: '-=' + ($(this).outerHeight() + MARGIN)
        }, 200);
      });
      el.show();
    }
    el.appendTo($("body"));*/
  };

  $[pluginName] = function () {
  };

  $[pluginName].info = function (message) {
    new Plugin({level: TYPES.INFO, message: message});
  };

  $[pluginName].warn = function (message) {
    new Plugin({level: TYPES.GENERAL, message: message, sticky: true});
  };

  $[pluginName].error = function (message) {
    new Plugin({level: TYPES.ERROR, message: message, sticky: true});
  };

  $[pluginName].notify = function (options) {
    new Plugin(options);
  };

})(jQuery, window, document);
