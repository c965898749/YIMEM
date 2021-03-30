//定义双向链表存放浏览历史
var operation_history = new DbList({
      "parentId": "-1",
      "is_active": false
    }),
    timeOutFn = null,
    is_ctrl_down = false,
    is_shift_down = false,
    focus_index = -1;
var dp=null;
/*mode:1->复制  2->剪切
 * id:被复制或剪切的文件或文件夹的id
 * parentId:被复制文件的上级目录id
 * can_paste:false->不能黏贴 true->能黏贴
 * */
oprate_param = {
  "mode": "",
  "id": "",
  "parentId": "",
  "can_paste": false
};

function init(parentId, mode) {
  //初始化导航栏
  navigation(parentId);
  //将当前页面插入历史记录链表里面
  /*mode
   *0:初始化进入页面
   *1:新建文件夹
   *2:删除文件夹或文件
   *3:刷新当前文件夹
   *4:将当前目录下的文件夹和文件排序
   *5:上传文件
   *6:打开文件夹
   *7:点击后退按钮
   *8:点击前进按钮
   *9:点击主页
   *10:返回上一层目录
   *11:点击地址栏
   *
   */
  if (mode == 0) {
    operation_history.insertLast({
      "parentId": parentId,
      "is_active": true
    });
  } else if (mode == 1) {
    //新建文件夹不插入历史记录
  } else if (mode == 2) {
    //删除文件夹或文件不会改变历史记录
  } else if (mode == 3) {
    //刷新当前文件夹不插入历史记录
  } else if (mode == 4) {
    //排序不插入历史记录
  } else if (mode == 5) {
    //上传文件现在会重新进入页面，这个之后改
  } else if (mode == 6) {
    //打开文件夹会删除父目录(也就是当前激活的目录)往后的所有记录，再插入新的子目录记录
    var currNode = find_active_node();
    operation_history.removeAfter(currNode);
    currNode.element.is_active = false;
    operation_history.insertLast({
      "parentId": parentId,
      "is_active": true
    });
  } else if (mode == 7) {
    var currNode = find_active_node();
    currNode.element.is_active = false;
    currNode.previous.element.is_active = true;
  } else if (mode == 8) {
    var currNode = find_active_node();
    currNode.element.is_active = false;
    currNode.next.element.is_active = true;
  } else if (mode == 9) {
    //返回主页会删除父目录(也就是当前激活的目录)往后的所有记录，再插入新的子目录(主页)记录
    var currNode = find_active_node();
    operation_history.removeAfter(currNode);
    currNode.element.is_active = false;
    operation_history.insertLast({
      "parentId": parentId,
      "is_active": true
    });
  } else if (mode == 10) {
    //返回上级目录会删除父目录(也就是当前激活的目录)往后的所有记录，再插入新的子目录(上级目录)记录
    var currNode = find_active_node();
    operation_history.removeAfter(currNode);
    currNode.element.is_active = false;
    operation_history.insertLast({
      "parentId": parentId,
      "is_active": true
    });
  } else if (mode == 11) {
    //点击地址栏仅仅往历史记录里面加一条记录,如果链表里面最后一条记录
    //与将要添加的相同则不添加记录而是把最后一条记录设为激活状态
    var currNode = find_active_node(),
        lastNode = operation_history.findLast();
    currNode.element.is_active = false;
    if (lastNode.element.parentId == parentId) {
      lastNode.element.is_active = true;
    } else {
      operation_history.insertLast({
        "parentId": parentId,
        "is_active": true
      });
    }
  }
  //载入文件夹
  load();
  //载入信息栏   第一个参数->  1:展示文件夹的记录数   2:展示选中的文件信息  3:展示选中的文件夹数目
  //       第二个参数-> 展示的文件的id
  info(1, 0);
  //绑定右键菜单
  contextMenu();
  //绑定左键事件
  leftClick();
  //绑定获取焦点事件
  focus();
  //绑定失去焦点事件
  blur();
  //定义文件名更改事件
  change();
  //定义双击事件
  dbclick();
  //定义键盘按键按下事件
  keydown();
  //定义键盘按键弹起事件
  keyup();
  //将元素绑定拖拽方法--还没开发完
  // drag();
}
function preventDe(e) {
  e.preventDefault();
}
document.addEventListener("drop", preventDe);
document.addEventListener("dragleave", preventDe);
document.addEventListener("dragover", preventDe);
document.addEventListener("dragenter", preventDe);
document.addEventListener("drop", function (e) {
  e.preventDefault();
  var file = e.dataTransfer.files;
  console.log(file)
  uploadfile(file)
//file.type; 文件类型
//file.name;文件名
//file.size; 文件大小 btye

  // var img = document.getElementsByTagName("img")[0];
  // var dataURL = URL.createObjectURL(file);
  // img.src = dataURL;
  // console.log(dataURL)

//         var formData = new FormData();
//         formData.append("file", file);
// // 发送XHR
//         XHR.send(formData);
})
//在历史记录中找到当前的节点
function find_active_node() {
  var currNode = operation_history.getHead();
  while (currNode.element.is_active != true) {
    currNode = currNode.next;
  }
  return currNode;
}

function contextMenu() {
  contextMenu_folder();
  contextMenu_blank();
  contextMenu_file();
}

function closetanchuan() {
  $("#background").removeData()
  $("#background2").removeData()
  $("#background3").removeData()
  $("#background2").css("display", "none");
  $("#background3").css("display", "none");
  $("#background").css("display", "none");
  if (dp!=null) {
    dp.pause()
  }

}

function contextMenu_folder() {
  $("#divall li.folder").contextMenu('myMenu2', {
    bindings: {
      'open': function (t) {
        //alert("进入这个文件夹");
        var id = $(t).children().attr("data-id");
        init(id, 6);
      },
      'rename': function (t) {
        $("#background2").css("display", "block");
        //重命名
        var folder = $(t).children("input.changename"),
            // folderName = folder.val(),
            id = folder.attr("data-id"),
            isDirectory = $(t).hasClass("folder") ? 1 : 0;
        doc_type = $(t).hasClass("folder") ? "" : folder.attr("data-filetype"),
            parentId = $("#navigation").val(),
            // params = {
            //     "folderName": folderName,
            //     "id": id,
            //     "isDirectory": isDirectory,
            //     "doc_type": doc_type,
            //     "parentId": parentId,
            //     "description": ""
            // };
            $("#background2").data("id", id);
        $("#background2").data("isDirectory", isDirectory);
        $("#background2").data("doc_type", doc_type);
        $("#background2").data("parentId", parentId);
        // update_folderName(params)
        // // form.setparams($("#M8610F001"), params);
        // // popup($("#M8610P001"));

      },
      'delete': function (t) {
        //删除文件夹
        var id = $(t).children("input.changename").attr("data-id"),
            isDirectory = $(t).hasClass("folder") ? 1 : 0,
            params = {
              "id": id,
              "isDirectory": isDirectory
            };
        dele(params);
      },
      'download': function (t) {
        //将文件夹打包下载
        var folder = $(t).children("input.changename"),
            id = folder.attr("data-id"),
            isDirectory = $(t).hasClass("folder") ? 1 : 0,
            folderName = folder.val(),
            parentId = $("#navigation").val(),
            params = {
              "id": id,
              "isDirectory": isDirectory,
              "folderName": folderName,
              "parentId": parentId
            };
        download(params);
        alert("下载成功!");
      },
      'copy': function (t) {
        //复制
        var focus_id = [];
        $("#divall").find("li").each(function (i) {
          if ($(this).hasClass("focus")) {
            focus_id.push($(this).children("input.changename").attr("data-id"));
          }
        });
        oprate_param.mode = 1;
        oprate_param.id = focus_id;
        oprate_param.parentId = $("#navigation").val();
        oprate_param.can_paste = true;
        alert("copy了：" + focus_id);
      },
      'cut': function (t) {
        //剪切
        var focus_id = [];
        $("#divall").find("li").each(function (i) {
          if ($(this).hasClass("focus")) {
            focus_id.push($(this).children("input.changename").attr("data-id"));
          }
        });
        oprate_param.mode = 2;
        oprate_param.id = focus_id;
        oprate_param.parentId = $("#navigation").val();
        oprate_param.can_paste = true;
        alert("cut了：" + focus_id);
      },
      'paste': function (t) {
        var folder = $(t).children("input.changename"),
            id = folder.attr("data-id");
        if (oprate_param.can_paste != true) {
          alert("剪切板中无内容!");
        } else {
          if (oprate_param.parentId == id) {
            alert("文件已存在!");
          } else {
            //黏贴

          }
        }
      }
    },
    onContextMenu: function (e) {
      var i_index = $(e.target).attr("index"),
          all_focus_index = [];
      $("#divall").find("li").each(function (i) {
        if ($(this).hasClass("focus")) {
          all_focus_index.push($(this).attr("index"));
        }
      });
      if ($.inArray(i_index, all_focus_index) == -1) {
        $("#divall").find("li").each(function (i) {
          $(this).removeClass("focus");
        });
        $(e.target).addClass("focus");
      }
      return true;
    }
  });
}

function contextMenu_file() {
  $("#divall li.file").contextMenu('myMenu3', {
    bindings: {
      'open': function (t) {
        var folder = $(t).children("input.changename"),
            id = folder.attr("data-id");
        var rows = select("M8610EQ008", {"id": id});
        if (rows.type=='pdf') {
          localStorage.videoUrl =  rows.src;
          window.open("pdf.html")
        }else if (rows.type=='pdf'||rows.type=='svg'||rows.type=='png'||rows.type=='jpg'||rows.type=='jpeg'||rows.type=='gif') {
          prewImg(rows.src)
        }else if (rows.type=='mp4'||rows.type=='avi'||rows.type=='rm'||rows.type=='mkv'||rows.type=='asf'||rows.type=='vob'||rows.type=='asx'||rows.type=='fla'||rows.type=='mpe'||rows.type=='mov'||rows.type=='flv'||rows.type=='swf'||rows.type=='wmv'||rows.type=='mpg'||rows.type=='rmvb'||rows.type=='mpeg'
        ){
          getDplayer(rows.src)
        }else if(rows.type=='doc'||rows.type=='docx'||rows.type=='xlsx'||rows.type=='pptx'){
          prewdoc(rows.src)
        }else {
          alert("该文件格式不支持在线预览");
        }

        // var id = $(t).children().attr("data-id");
        // init(id, 6);
      },
      'rename': function (t) {
        //重命名
        $("#background2").css("display", "block");
        //重命名
        var folder = $(t).children("input.changename"),
            // folderName = folder.val(),
            id = folder.attr("data-id"),
            isDirectory = $(t).hasClass("folder") ? 1 : 0;
        doc_type = $(t).hasClass("folder") ? "" : folder.attr("data-filetype"),
            parentId = $("#navigation").val(),
            // params = {
            //     "folderName": folderName,
            //     "id": id,
            //     "isDirectory": isDirectory,
            //     "doc_type": doc_type,
            //     "parentId": parentId,
            //     "description": ""
            // };
            $("#background2").data("id", id);
        $("#background2").data("isDirectory", isDirectory);
        $("#background2").data("doc_type", doc_type);
        $("#background2").data("parentId", parentId);

        // K.form.setparams($("#M8610F001"), params);
        // K.popup($("#M8610P001"));
      },
      'copy': function (t) {
        //复制
        var focus_id = [];
        $("#divall").find("li").each(function (i) {
          if ($(this).hasClass("focus")) {
            focus_id.push($(this).children("input.changename").attr("data-id"));
          }
        });
        oprate_param.mode = 1;
        oprate_param.id = focus_id;
        oprate_param.parentId = $("#navigation").val();
        oprate_param.can_paste = true;
        alert("copy了：" + focus_id);
      },
      'cut': function (t) {
        //剪切
        var focus_id = [];
        $("#divall").find("li").each(function (i) {
          if ($(this).hasClass("focus")) {
            focus_id.push($(this).children("input.changename").attr("data-id"));
          }
        });
        oprate_param.mode = 2;
        oprate_param.id = focus_id;
        oprate_param.parentId = $("#navigation").val();
        oprate_param.can_paste = true;
        alert("cut了：" + focus_id);
      },
      'delete': function (t) {
        //删除单个文件
        var id = $(t).children("input.changename").attr("data-id"),
            isDirectory = $(t).hasClass("folder") ? 1 : 0,
            params = {
              "id": id,
              "isDirectory": isDirectory
            };
        dele(params);
      },
      'download': function (t) {
        //下载单个文件
        var folder = $(t).children("input.changename"),
            id = folder.attr("data-id"),
            isDirectory = $(t).hasClass("folder") ? 1 : 0,
            folderName = folder.val() + "." + folder.attr("data-filetype");
            // parentId = $("#navigation").val();
            // params = {
            //   "id": id,
            //   "isDirectory": isDirectory,
            //   "folderName": folderName,
            //   "parentId": parentId
            // };
        var rows = select("M8610EQ008", {"id": id});
        localStorage.videoUrl =  rows.src;
        window.open(rows.src+"?attname="+folderName)
        // ?attname=
        //download(params);
        // alert("下载成功!");
      }
    },
    onContextMenu: function (e) {
      var i_index = $(e.target).attr("index"),
          all_focus_index = [];
      $("#divall").find("li").each(function (i) {
        if ($(this).hasClass("focus")) {
          all_focus_index.push($(this).attr("index"));
        }
      });
      if ($.inArray(i_index, all_focus_index) == -1) {
        $("#divall").find("li").each(function (i) {
          $(this).removeClass("focus");
        });
        $(e.target).addClass("focus");
      }
      return true;
    }
  });
}

function contextMenu_blank() {
  $("#all_folder").contextMenu('myMenu1', {
    bindings: {
      'newfolder': function (t) {
        //获取新文件夹的名称
        var folderNames = [],
            newfolderName = "";
        $("#all_folder").find("ul").eq(0).find("li.folder").each(function (index) {
          folderNames.push($(this).children("input.changename").val());
        });
        for (var i = 0; i < 100; i++) {
          if (i == 0) {
            newfolderName = "新文件夹";
          } else {
            newfolderName = "新文件夹[" + i + "]";
          }
          if ($.inArray(newfolderName, folderNames) == -1) {
            break;
          }
          ;
        }
        //调用新增文件夹代码
        var params = {
          "id": $("#navigation").val(),
          "discription": "",
          "folderName": newfolderName
        };
        var flag = add_folder(params);
        if (flag) {
          init($("#navigation").val(), 1);
        }
      },
      'paste': function (t) {
        //黏贴
        var parentId = $("#navigation").val();
        let data =t.clipboardData||window.clipboardData;
        console.dir(data);
        // let items = data.items;
        // let fileList = []; //存储文件数据
        // if (items && items.length) {
        //   // 检索剪切板items
        //   for (let i = 0; i < items.length; i++) {
        //     console.log(items[i].getAsFile()); // <--- 这里打印出来就就是你想要的文件
        //     fileList.push(items[i].getAsFile());
        //   }
        // }
        // if (oprate_param.can_paste != true) {
        //   alert("无黏贴内容");
        // } else {
        //   if (oprate_param.parentId == parentId) {
        //     alert("文件已存在!");
        //     paste(oprate_param);
        //   } else {
        //     alert("正在黏贴");
        //     paste(oprate_param);
        //   }
        // }
        //oprate_param.can_paste = false;
        //init($("#navigation").val(),3);
      },
      'flush': function (t) {
        //刷新
        init($("#navigation").val(), 3);
      },
      'sort': function (t) {
        init($("#navigation").val(), 4);
      },
      'upload': function (t) {
        // btn.onclick = function show() {
        //   //console.log(11111111111111)
        //   div.style.display = "block";
        // }
        //console.log(11111111111111)
        // close.onclick = function close() {
        //     div.style.display = "none";
        // }

        $("#background").css("display", "block")
        // var folder = $(t).children("input.changename"),
        // // folderName = folder.val(),
        // id = folder.attr("data-id"),
        // isDirectory = $(t).hasClass("folder") ? 1 : 0;
        // doc_type = $(t).hasClass("folder") ? "" : folder.attr("data-filetype"),
        var parentId = $("#navigation").val()
        // params = {
        //     "folderName": folderName,
        //     "id": id,
        //     "isDirectory": isDirectory,
        //     "doc_type": doc_type,
        //     "parentId": parentId,
        //     "description": ""
        // };
        //     $("#background2").data("id",id);
        // $("#background2").data("isDirectory",isDirectory);
        // $("#background2").data("doc_type",doc_type);
        $("#background").data("parentId", parentId);
        // window.onclick = function close(e) {
        //   if (e.target == div) {
        //     div.style.display = "none";
        //   }
        // }
        //上传文件
        // var $M8610F002 = $("#M8610F002");
        // K.form.reset($M8610F002);
        // K.field.value($('#upload_id'), $("#navigation").val());
        // K.popup($("#M8610P002"));
      }
    }
  });
}

var check_res;

function select(sqlExecute, params) {
  $.ajax({
    async: false,
    url: sqlExecute,
    data: JSON.stringify(params),
    type: "POST",
    dataType: "json",
    contentType: 'application/json;charset=utf-8',
    success: function (jsonData) {
      check_res = jsonData;
    }
    , error: function (res) {
      ////console.log("登入状态ajax提交错误")
    }
  })
  return check_res;
}

var insert_res;

function insert(sqlExecute, params) {
  $.ajax({
    async: false,
    url: sqlExecute,
    data: JSON.stringify(params),
    type: "POST",
    dataType: "json",
    contentType: 'application/json;charset=utf-8',
    success: function (jsonData) {
      insert_res = jsonData;
    }
    , error: function (res) {
      ////console.log("登入状态ajax提交错误")
    }
  })
  return insert_res;
}

var update_res;

function update(sqlExecute, params) {
  $.ajax({
    async: false,
    url: sqlExecute,
    data: JSON.stringify(params),
    type: "POST",
    dataType: "json",
    contentType: 'application/json;charset=utf-8',
    success: function (jsonData) {
      update_res = jsonData;
    }
    , error: function (res) {
      ////console.log("登入状态ajax提交错误")
    }
  })
  return update_res;
}

var del_res;

function del(sqlExecute, params) {
  $.ajax({
    async: false,
    url: sqlExecute,
    data: JSON.stringify(params),
    type: "POST",
    dataType: "json",
    contentType: 'application/json;charset=utf-8',
    success: function (jsonData) {
      del_res = jsonData;
    }
    , error: function (res) {
      ////console.log("登入状态ajax提交错误")
    }
  })
  return del_res;
}


//加载文件夹和文件
function load() {
  var parentid = $("#navigation").val(),
      rows = select("M8610EQ006", {"id": parentid});
  $("#divall").empty();
  if (rows.length > 0) {
    var str = "";
    for (var i = 0; i < rows.length; i++) {
      if (rows[i].isDirectory == "1") {
        str += "<li class='folder' title='" + rows[i].folderName + "' index='" + i + "'><input type='text' class='changename' value='";
        str += rows[i].folderName;
        str += "' data-id='" + rows[i].id + "' disabled='disabled' data-last-value='" + rows[i].folderName + "'/></li>";
      } else if (rows[i].isDirectory == "0") {
        var doc_fullname = rows[i].folderName,
            doc_name = doc_fullname.substring(0, doc_fullname.lastIndexOf('.')),
            doc_type = doc_fullname.substring(doc_fullname.lastIndexOf('.') + 1),
            doc_type_class = $.inArray(doc_type, ["cab", "rar", "tar", "zip","pptx","svg","png","jpg","jpeg","gif","doc", "docx", "xls", "xlsx", "pdf", "mp4","avi","rm","mkv","asf","vob","asx","fla","mpe","mov","flv","swf","wmv","mpg","rmvb","mpeg"]) != -1 ? doc_type : "other-filetype";
        str += "<li class='file " + doc_type_class + "' title='" + rows[i].folderName + "' index='" + i + "'><input type='text' class='changename' value='";
        str += doc_name;
        str += "' data-id='" + rows[i].id + "' data-filetype='" + doc_type + "' disabled='disabled' data-last-value='" + rows[i].folderName + "'/></li>";
      }
    }
    $("#divall").append(str);
  }
}

function info(mode, id) {
  var str = "";
  if (mode == 1) { //展示目录下的对象数目
    $("#info-bar").empty();
    str += '<div class="folder info-icon"></div>';
    str += '<div class="info-detail"><form class="detail-form"><div class="detail-field detail-field2" ><span><var class="detail-var">';
    str += $("#divall").children("li").length;
    str += '</var>个对象</span></div>';
    str += '</form></div>';
    $("#info-bar").append(str);
  } else if (mode == 2) {
    $("#info-bar").empty();
    var rows = select("M8610EQ008", {"id": id});
    if (rows.length > 0) {
      var row = rows[0],
          folderName = row.folderName,
          isDirectory = row.isDirectory,
          file_type = isDirectory == "1" ? "" : folderName.substring(folderName.lastIndexOf('.') + 1),
          file_type_class = file_type == "" ? "folder" : ($.inArray(file_type, ["doc", "docx", "xls", "xlsx", "pdf"]) != -1 ? file_type : "other-filetype"),
          file_type_info = isDirectory == "1" ? "文件夹" : folderName.substring(folderName.lastIndexOf('.') + 1) + "文件",
          crtUsername = row.crtUsername,
          updUsername = row.updUsername,
          crtDate = row.crtDate,
          crtTime = row.crtTime,
          updDate = row.updDate,
          updTime = row.updTime;
      str += '<div class="' + file_type_class + ' info-icon"></div><div class="info-detail"><form class="detail-form"><div class="detail-field" ><label>文件名:</label><span>';
      str += folderName;
      str += '</span></div><div class="detail-field" ><label>创建人:</label><span>';
      str += crtUsername != null ? crtUsername : '';
      str += '</span></div><div class="detail-field" ><label>修改人:</label><span>';
      str += updUsername != null ? updUsername : '';
      str += '</span></div><div class="detail-field" ><label>文件类型:</label><span>';
      str += file_type_info;
      str += '</span></div><div class="detail-field" ><label>创建时间:</label><span>';
      str += crtDate != "" && crtDate != null ? crtDate.substring(0, 4) + '/' + crtDate.substring(4, 6) + "/" + crtDate.substring(6, 8) : "";
      str += crtTime != "" && crtTime != null ? "  " + crtTime.substring(0, 2) + ":" + crtTime.substring(2, 4) + ":" + crtTime.substring(4, 6) : "";
      str += '</span></div><div class="detail-field" ><label>修改时间:</label><span>';
      str += updDate != "" && updDate != null ? updDate.substring(0, 4) + '/' + updDate.substring(4, 6) + "/" + updDate.substring(6, 8) : "";
      str += updTime != "" && updTime != null ? "  " + updTime.substring(0, 2) + ":" + updTime.substring(2, 4) + ":" + updTime.substring(4, 6) : "";
      str += '</span></div></form></div>';
      $("#info-bar").append(str);
    }
  } else if (mode == 3) {
    $("#info-bar").empty();
    str += '<div class="folder info-icon"></div>';
    str += '<div class="info-detail"><form class="detail-form"><div class="detail-field detail-field2" ><span>已选中<var class="detail-var">';
    str += $("#divall").children("li.focus").length;
    str += '</var>个对象</span></div>';
    str += '</form></div>';
    $("#info-bar").append(str);
  }
}

function navigation(parentId) {
  $("#navigation").val(parentId);
  //查询文件路径
  var id = parentId,
      flag = true,
      path = [],
      str = "";
  // do {
  var rows = select("M8610EQ005", {"id": id});
  if (rows.length > 0) {
    var row = rows[0];
    if (row.parentId != 0) {
      path.unshift({
        "folderName": row.folderName,
        "parentId": id
      });
      id = row.parentId;
    } else {
      flag = false;
      path.unshift({
        "folderName": row.folderName,
        "parentId": id
      });
    }
  }
  // } while (flag);
  $("#folder-navigation").empty();
  for (var i = 0; i < path.length; i++) {
    str += '<a class="foldername" data-id="' + path[i].parentId + '">' + path[i].folderName + '</a>';
    if (i != path.length - 1) {
      str += '<img class="triangle" src="images/triangle.png"/>';
    }
  }
  $("#folder-navigation").append(str);
}

// function paste(param) {
//   Util.ajaxRequest({
//     url: "pasteDocManage.json",
//     params: param,
//     async: false,
//     afterSuccess: function (json) {
//       alert(3);
//       var msg = json.returnmsg;
//       var success = json.success;
//       if (success == false) {
//         if (msg == "windows") {
//           alert("请检查windows的文档上传路径配置是否正确！");
//         }
//       }
//       if (success == true) {
//
//       }
//       return false;
//     }
//   }, false);
// }

function drag() {
  $("#divall li").each(function (i) {
    //$(".item_content .item").each(function(i) {
    this.init = function () { // 初始化
      this.box = $(this);
      //console.log("left: " + this.box.offset().left + " top: " + this.box.offset().top);
      $(this).attr("index", i);
      // css({
      //                 position : "absolute",
      //                 left : this.box.offset().left,
      //                 top : this.box.offset().top
      //             }).appendTo("#divall")
      this.drag();
    },
        this.move = function (callback) { // 移动
          $(this).stop(true).animate({
            left: this.box.offset().left,
            top: this.box.offset().top
          }, 500, function () {
            if (callback) {
              callback.call(this);
            }
          });
        },
        this.collisionCheck = function () {
          var currentItem = this;
          var direction = null;
          $(this).siblings(".item").each(function () {
            if (
                currentItem.pointer.x > this.box.offset().left &&
                currentItem.pointer.y > this.box.offset().top &&
                (currentItem.pointer.x < this.box.offset().left + this.box.width()) &&
                (currentItem.pointer.y < this.box.offset().top + this.box.height())
            ) {
              // 返回对象和方向
              if (currentItem.box.offset().top < this.box.offset().top) {
                direction = "down";
              } else if (currentItem.box.offset().top > this.box.offset().top) {
                direction = "up";
              } else {
                direction = "normal";
              }
              this.swap(currentItem, direction);
            }
          });
        },
        this.swap = function (currentItem, direction) { // 交换位置
          if (this.moveing) return false;
          var directions = {
            normal: function () {
              var saveBox = this.box;
              this.box = currentItem.box;
              currentItem.box = saveBox;
              this.move();
              $(this).attr("index", this.box.index());
              $(currentItem).attr("index", currentItem.box.index());
            },
            down: function () {
              // 移到上方
              var box = this.box;
              var node = this;
              var startIndex = currentItem.box.index();
              var endIndex = node.box.index();
              ;
              for (var i = endIndex; i > startIndex; i--) {
                var prevNode = $(".item_container .item[index=" + (i - 1) + "]")[0];
                node.box = prevNode.box;
                $(node).attr("index", node.box.index());
                node.move();
                node = prevNode;
              }
              currentItem.box = box;
              $(currentItem).attr("index", box.index());
            },
            up: function () {
              // 移到上方
              var box = this.box;
              var node = this;
              var startIndex = node.box.index();
              var endIndex = currentItem.box.index();
              ;
              for (var i = startIndex; i < endIndex; i++) {
                var nextNode = $(".item_container .item[index=" + (i + 1) + "]")[0];
                node.box = nextNode.box;
                $(node).attr("index", node.box.index());
                node.move();
                node = nextNode;
              }
              currentItem.box = box;
              $(currentItem).attr("index", box.index());
            }
          };
          directions[direction].call(this);
        },
        this.drag = function () { // 拖拽
          var oldPosition = new Position();
          var oldPointer = new Pointer();
          var isDrag = false;
          var currentItem = null;
          $(this).mousedown(function (e) {
            e.preventDefault();
            oldPosition.left = this.box.offset().left;

            oldPosition.top = this.box.offset().top;
            //console.log("oldleft" + oldPosition.left + "oldtop" + oldPosition.top);
            oldPointer.x = e.clientX;
            oldPointer.y = e.clientY;
            isDrag = true;

            currentItem = this;

          });
          $(document).mousemove(function (e) {
            var currentPointer = new Pointer(e.clientX, e.clientY);
            if (!isDrag) return false;
            $(currentItem).css({
              "opacity": "0.8",
              "z-index": 999
            });
            var left = currentPointer.x - oldPointer.x + oldPosition.left;
            var top = currentPointer.y - oldPointer.y + oldPosition.top;
            $(currentItem).css({
              left: left,
              top: top
            });
            currentItem.pointer = currentPointer;
            // 开始交换位置

            currentItem.collisionCheck();


          });
          $(document).mouseup(function () {
            if (!isDrag) return false;
            isDrag = false;
            currentItem.move(function () {
              $(this).css({
                "opacity": "1",
                "z-index": 0
              });
            });
          });
        };
    this.init();
  });
}

function leftClick() {
  //点击文件夹
  $("#divall li").click(function (event) {
    var $this = $(this),
        folderName = $this.children("input.changename"),
        index = $this.attr("index");
    if (is_ctrl_down == true && is_shift_down == false) { //按下ctrl
      event.stopPropagation();
      focus_index = index;
      $("#divall").find("li").each(function (index) {
        $(this).children("input.changename").attr("disabled", "disabled");
      });
      if ($this.hasClass("focus")) {
        $this.removeClass("focus");
      } else {
        $this.addClass("focus");
      }
      info(3, 0);
    } else if (is_ctrl_down == false && is_shift_down == true) { //按下shift
      event.stopPropagation();
      if (focus_index == -1) {
        focus_index = index;
        $this.addClass("focus");
      } else {
        var index_min = Math.min(index, focus_index),
            index_max = Math.max(index, focus_index);
        $("#divall").find("li").each(function (i) {
          var i_index = $(this).attr("index");
          $(this).removeClass("focus");
          $(this).children("input.changename").attr("disabled", "disabled");
          if (i_index >= index_min && i_index <= index_max) {
            $(this).addClass("focus");
          }
        });
      }
      info(3, 0);
    } else {
      event.stopPropagation();
      $("#divall").find("li").each(function (index) {
        $(this).removeClass("focus");
        $(this).children("input.changename").attr("disabled", "disabled");
      });
      $this.addClass("focus");
      focus_index = index;
      clearTimeout(timeOutFn);
      timeOutFn = setTimeout(function () {
        folderName.removeAttr("disabled");
        info(2, folderName.attr("data-id"));
      }, 300);
    }

  });
  //点击文件名称
  $("#divall li input.changename").click(function (event) {
    if (is_ctrl_down == false) { //没有按下ctrl
      event.stopPropagation();
      console.log("input click");
    }

  });
  //点击空白的地方
  $("#all_folder").click(function () {
    //console.log("blank click");
     $("#background").css("display", "none");
     $("#background2").css("display", "none");
     $("#background3").css("display", "none");
    if (dp!=null) {
      dp.pause()
    }
    $("#divall").find("li").each(function (index) {
      $(this).removeClass("focus");
      $(this).children("input.changename").attr("disabled", "disabled");
    });
    info(1, 0);
  });
  //点击后退按钮
  $("button.backward").off("click").click(function () {
    $("#background").css("display", "none");
    $("#background2").css("display", "none");
    $("#background3").css("display", "none");
    if (dp!=null) {
      dp.pause()
    }
    //console.log("backward click");
    var currNode = find_active_node();
    if (currNode.previous.previous != null) {
      var preNode = currNode.previous,
          parentId = preNode.element.parentId;
      init(parentId, 7);
    }
  });
  //点击前进按钮
  $("button.forward").off("click").click(function () {
    $("#background").css("display", "none");
    $("#background2").css("display", "none");
    $("#background3").css("display", "none");
    if (dp!=null) {
      dp.pause()
    }
    //console.log("forward click");
    var currNode = find_active_node();
    if (currNode.next != null) {
      var nextNode = currNode.next,
          parentId = nextNode.element.parentId;
      init(parentId, 8);
    }
  });
  //点击主页按钮
  $("button.home").off("click").click(function () {
    $("#background").css("display", "none");
    $("#background2").css("display", "none");
    $("#background3").css("display", "none");
    if (dp!=null) {
      dp.pause()
    }
    //console.log("home click");
    if ($("#navigation").val() != 1) {
      init(1, 9);
    }
  });
  //点击返回上级目录
  $("button.gotopre").off("click").click(function () {
    $("#background").css("display", "none");
    $("#background2").css("display", "none");
    $("#background3").css("display", "none");
    if (dp!=null) {
      dp.pause()
    }
    //console.log("gotopre click");
    if ($("#navigation").val() != 1) {
      //查询上级目录的parentId
      var rows = select("M8610EQ005", {"id": $("#navigation").val()});
      if (rows.length > 0) {
        var parentId = rows[0].parentId;
        if (parentId != 0) {
          init(parentId, 10);
        }
      }
    }
  });
  //点击地址栏地址
  $("a.foldername").off("click").click(function () {
    $("#background").css("display", "none");
    $("#background2").css("display", "none");
    $("#background3").css("display", "none");
    if (dp!=null) {
      dp.pause()
    }
    var parentId = $(this).attr("data-id");
    if ($("#navigation").val() != parentId) {
      init(parentId, 11);
    }
  });
}

function focus() {
  $("#divall li input.changename").focus(function () {
    //console.log("input focus");
  });

  $("#divall li").focus(function () {
    //console.log("li focus");
  });
}

function blur() {
  $("#divall li").blur(function () {
    //console.log("li blur");
  });

  $("#divall li input.changename").blur(function () {
    //console.log("input blur");
    $(this).attr("disabled", "disabled");
  });

}

function change() {
  $("#divall li input.changename").change(function () {
    //console.log("input change");
    var folder = $(this).parent("li"),
        data_last_value = $(this).attr("data-last-value"),
        folderName = $(this).val(),
        id = $(this).attr("data-id"),
        isDirectory = folder.hasClass("folder") ? 1 : 0,
        doc_type = folder.hasClass("folder") ? "" : $(this).attr("data-filetype"),
        parentId = $("#navigation").val(),
        params = {
          "folderName": folderName,
          "id": id,
          "isDirectory": isDirectory,
          "doc_type": doc_type,
          "parentId": parentId,
          "description": ""
        };
    if (update_folderName(params)) {
      $(this).attr("data-last-value", folderName);
      info(2, $(this).attr("data-id"));
    } else {
      $(this).val(data_last_value);
    }
  });
}

function dbclick() {
  $("#divall li.folder").dblclick(function () {
    clearTimeout(timeOutFn);
    // console.log("li dblclick");
    var folder = $(this).children("input.changename");
    init(folder.attr("data-id"), 6);
  });
  $("#divall li.file").click(function (event) {
    clearTimeout(timeOutFn);
    // console.log("li dblclick");
    var folder = $(this).children("input.changename");
    var id = folder.attr("data-id");
    var rows = select("M8610EQ008", {"id": id});
    if (rows.type=='pdf') {
      localStorage.videoUrl =  rows.src;
      window.open("pdf.html")
    }else if (rows.type=='pdf'||rows.type=='svg'||rows.type=='png'||rows.type=='jpg'||rows.type=='jpeg'||rows.type=='gif') {
      prewImg(rows.src)
    }else if (rows.type=='mp4'||rows.type=='avi'||rows.type=='rm'||rows.type=='mkv'||rows.type=='asf'||rows.type=='vob'||rows.type=='asx'||rows.type=='fla'||rows.type=='mpe'||rows.type=='mov'||rows.type=='flv'||rows.type=='swf'||rows.type=='wmv'||rows.type=='mpg'||rows.type=='rmvb'||rows.type=='mpeg'
    ){
      getDplayer(rows.src)
    }else if(rows.type=='doc'||rows.type=='docx'||rows.type=='xlsx'||rows.type=='pptx'){
      prewdoc(rows.src)
    }else {
      alert("该文件格式不支持在线预览");
    }

    // init(folder.attr("data-id"), 6);
  });
}

function prewdoc(url) {
  $(".back3").css("display", "block");
  $("<iframe src='https://view.officeapps.live.com/op/view.aspx?src="+ url +"' width='100%' height='900px' >").appendTo($(".cccc"));
}

function canclePrew2() {
  $(".back3").css("display", "none");
  $(".cccc").empty()
}


function prewImg(url) {
  $(".back2").css("display", "block");
  $("#prewImg").attr("src", url);
}

function canclePrew() {
  $(".back2").css("display", "none");
  $("#prewImg").attr("src", "")
}


function getDplayer(videoUrl) {
  ////console.log("这是"+videoUrl)
  // const userId = 1
  $("#background3").css("display", "block");
   dp=new DPlayer({
    element: document.getElementById('Dplayer'),
    video: {
      // url: 'http://www.yimem.com/group1/M00/00/07/wKgAaWBHPKqAK6-GAeKDAL22n5E231.mp4',
      // url:'/mp4/KK/22.mp4',
      url: videoUrl,
      // pic: coverUrl
      // pic: 'http://www.yimem.com/group1/M00/00/00/wKgBBV7dFPCAKpdxAAA-0c-9Y4o668.jpg'
      // pic: '/imgs/loading/fm.jpg'
    },
    // theme: "yellow",
    // live:true,
    hotkey: true,
    loop: true,
    logo:'/imgs/logo.png',
    contextmenu: [
      {
        text: '观看更多……',
        link: 'http://www.yimem.com/app.html',
      },
      {
        text: '下载视屏',
        click: (player) => {
          location.href = "downloadResource?id=" + videoId;
        },
      },
    ],
  });
}


function keydown() {
  $(document).keydown(function (event) {
    if (event.which == '17') {
      is_ctrl_down = true;
    } else if (event.which == '16') {
      is_shift_down = true;
    }
  });
}

function keyup() {
  $(document).keyup(function (event) {
    if (event.which == '17') {
      is_ctrl_down = false;
    } else if (event.which == '16') {
      is_shift_down = false;
    }
  });
}

//新增目录
var add_folder = function (params) {
  var flag = false;
  if (!testFolderName(params.folderName)) {
    alert("文件夹名不能包括\\\/:*?\"<>|等特殊符号");
    return flag;
  }
  if (judgeDocExist(params.id, params.folderName)) {
    alert("该目录已存在，不能添加");
    return flag;
  }
  var param = {
    description: params.description,
    folderName: params.folderName,
    isDirectory: 1,
    parentId: params.id,
    portLevel: 1,
    username: "semitree",
    // date: new Date().Format("yyyyMMdd"),
    // time: new Date().Format("hhmmss")
  };
  insert("M8610ES001", param);
  return true;
};


//修改文件名称
var update_folderName = function () {
  params = {
    "folderName": $('input[name="folder_name"]').val(),
    "id": $("#background2").data("id"),
    "isDirectory": $("#background2").data("isDirectory"),
    "doc_type": $("#background2").data("doc_type"),
    "parentId": $("#background2").data("parentId"),

    "description": ""
  };
  var folderName = "",
      flag = false;
  if (params.isDirectory == 1) {
    folderName = params.folderName;
  } else {
    folderName = params.folderName + '.' + params.doc_type;
  }
  if (!testFolderName(params.folderName)) {
    alert("文件名不能包括\\\/:*?\"<>|等特殊符号");
    return flag;
  }
  if (judgeDocUpdate(params.id, folderName)) {
    alert("该目录/文档已存在，请重新输入!");
    return flag;
  }
  // var current_date = new Date(),
  //     date = current_date.Format("yyyyMMdd"),
  //     time = current_date.Format("hhmmss");
  update("M8610EU001", {
    "description": params.description,
    "folderName": params.folderName,
    "username": "semitree",
    // "date": date,
    // "time": time,
    "id": params.id
  });
  flag = true;
  // K.popup.close($("#M8610P001"));
  //修改文件名
  $("#divall").find("input[data-id=" + params.id + "]").val(params.folderName);
  $("#background2").removeData();
  $("#background2").css("display", "none");
  return flag;
};

// 上传组件

/**

 * 侦查附件上传情况 ,这个方法大概0.05-0.1秒执行一次

 */

function onprogress(evt) {

  // document.getElementById("progressBar").style.visibility="visible";
  var loaded = evt.loaded;       //已经上传大小情况

  var tot = evt.total;       //附件总大小

  var per = Math.floor(99 * loaded / tot);   //已经上传的百分比


  if (per>=99){
    document.getElementById("progressPersent").innerText ="正在转码请稍等……(请不要刷新页面)";
    document.getElementById("progress").style.width = per + "%";
  }else {
    document.getElementById("progressPersent").innerText = per + "%";
    document.getElementById("progress").style.width = per + "%";
  }

}

//上传文件

function uploadfile(picFileList) {
  var parentId = $("#navigation").val()
  var xhr = new XMLHttpRequest();
  //将上传的多个文件放入formData中

  // var picFileList = document.getElementById("pic").files;
  if ($("#pic").val() == '') {
    layer.msg("上传文件不能为空")
    return false;
  }
  var formData = new FormData();

  formData.append("file", picFileList[0]);

  //监听事件

  xhr.upload.addEventListener("progress", onprogress, false);
  xhr.overrideMimeType('multipart/form-data; charset=utf-8');
  // xhr.addEventListener("error", uploadFailed, false);//发送文件和表单自定义参数

  xhr.open("POST", "fileUpload", true);

  //记得加入上传数据formData

  xhr.send(formData);

  xhr.onreadystatechange = function () {

    if (xhr.readyState == 4 && xhr.status == 200) {
      // 获取json数据
      var jsoncontent = JSON.parse(xhr.responseText)
      console.log(jsoncontent)
      // if(xhr.responseText == "succes"){
      if (jsoncontent.success == 1) {
        var param = {
          description: "",
          folderName: jsoncontent.data.original,
          isDirectory: 0,
          parentId: parentId,
          portLevel: 1,
          username: "semitree",
          src:jsoncontent.data.url,
          size:jsoncontent.data.size,
          type:jsoncontent.data.type
        };
        insert("M8610ES001", param);
        $("#background").css("display", "none");
        init($("#navigation").val(), 1);
        document.getElementById("progressPersent").innerText = "上传成功！";
        document.getElementById("progress").style.width = "100%";
      } else {

        document.getElementById("progressPersent").innerText = jsoncontent.errorMsg;

      }

    } else {
      document.getElementById("progressPersent").innerText = "服务器异常，请稍后上传！";
    }

  }

}


//上传文档
// var uploadFile = function (params) {
var uploadFile = function () {
  params = {
    "id": $("#background").data("parentId")
  }
  var $need_appendix = $('#M8610F002').find('input[name=need_appendix]'),
      fileList = $need_appendix.get(0).files,
      fileNameWithSuffixList = [],
      fileNameList = [];

  $.each(fileList, function (index, file) {
    var name = file.name;
    fileNameWithSuffixList.push(name);
    fileNameList.push(name.substring(0, name.lastIndexOf('.')));
  });
  if (fileNameWithSuffixList.length == 0) {
    $.pt({
      target: $need_appendix,
      position: 'r',
      align: 't',
      width: 'auto',
      height: 'auto',
      content: "请先选择文件"
    });
    return;
  } else {
    $.each(fileNameList, function (index, fileName) {
      //文件名真实长度不能超过100
      var blen = 0;
      for (var i = 0; i < fileName.length; i++) {
        if ((fileName.charCodeAt(i) & 0xff00) != 0) {
          blen++;
        }
        blen++;
      }
      if (blen > 100) {
        $.pt({
          target: $need_appendix,
          position: 'r',
          align: 't',
          width: 'auto',
          height: 'auto',
          content: "文件名过长!(支持50个中文或100个英文)"
        });
        //console.log("文件名过长!(支持50个中文或100个英文)");
        return false;
      }
    });
    // $.each(fileNameWithSuffixList, function (index, docName) {
    //     var param = {
    //         description: "",
    //         folderName: docName,
    //         isDirectory: 0,
    //         parentId: params.id,
    //         portLevel: 1,
    //         username: "semitree",
    //         // date: new Date().Format("yyyyMMdd"),
    //         // time: new Date().Format("hhmmss")
    //     };
    //     insert("M8610ES001", param);
    // });
  }
  uploadfile(fileList)
  // K.popup.close($("#M8610P002"));
  return true;
};

var dele = function (params) {
  //查询该目录下是否存在子目录/文档
  var rows = select("M8610EQ004", {"id": params.id});
      // desc = rows.length > 0 ? "删除整个文件夹(包含所有子目录和子文档)?" : "确认删除?";
  if (confirm(rows.length > 0 ? "删除整个文件夹(包含所有子目录和子文档)?" : "确认删除?") == true) {
    deleteDoc(params);
    init($("#navigation").val(), 2);
  }
  // confirm(desc, function(ok) {
  //     if (ok) {
  //         deleteDoc(params);
  //         init($("#navigation").val(), 2);
  //     }
  // });

};

//删除文档或目录
var deleteDoc = function (params) {
  var isDirectory = params.isDirectory,
      id = params.id;
  if (isDirectory == 0) {//删除单个文件
    del("M8610ED001", {"id": id});
  } else {//删除文件夹
    //递归删除文件夹下所有的子文件和文件夹
    var rows = select("M8610EQ006", {"id": id});
    if (rows.length > 0) {
      for (var i = 0; i < rows.length; i++) {
        deleteDoc(rows[i]);
      }
    }
    del("M8610ED001", {"id": id});
  }
};

//打包下载文件夹或文件
var download = function (params) {
  // var $M8610F003 = $("#M8610F003");
  // K.form.reset($M8610F003);
  // K.field.value($('#_id'), params.id);
  // K.field.value($('#p_id'), params.parentId);
  // K.field.value($('#f_name'), params.folderName);
  // K.field.value($('#i_directory'), params.isDirectory);
  if (params.isDirectory == 1) {
    confirm("是否打包下载整个文件夹?(可能需要较长时间，请耐心等待)", function (ok) {
      if (ok) {
        //判断所下载的目录是否为空
        Util.ajaxRequest({
          url: "directoryIsNull.json",
          params: params,
          async: false,
          afterSuccess: function (json) {
            var msg = json.returnmsg;
            if (msg == "目录为空") {
              alert("请不要下载空目录");
            }
            if (msg == "目录不为空") {
              K.submit($('#M8610F003'), null, true);
            }
            return false;
          }
        }, false);
      }
    });
  } else {
    K.submit($('#M8610F003'), null, true);
  }
};

var testFolderName = function (folderName) {
  var reg = new RegExp('^[^\\\\\\/:*?\\"<>|]+$');
  return reg.test(folderName);
};

//同一父目录下不能有同名目录或同名文档
function judgeDocUpdate(id, name) {
  var pd = false;
  // var rows = select("M8610EQ002",{"id":id,"folderName":name});
  // if(rows.length > 0){
  // 	if (rows[0].count > 0) {
  //         pd = true;
  //     } else {
  //         pd = false;
  //     }
  // }
  return pd;
};

//新增目录、上传文档时校验同目录下是否有同名目录/文档
function judgeDocExist(id, folderName) {
  var pd = false;
  // var rows = select("M8610EQ003",{"id":id,"folderName":folderName});
  // if(rows.length > 0){
  // 	if (rows[0].count > 0) {
  //         pd = true;
  //     } else {
  //         pd = false;
  //     }
  // }
  return pd;
}

//选择文件时动态加载信息框
function select_file(ele) {
  var $this = $(ele),
      fileList = $this.get(0).files,
      fileNameList = [],
      fieldsetNameList = [];
  //获取所有选择文件的文件名
  for (var i = 0; i < fileList.length; i++) {
    fileNameList.push(fileList[i].name);
  }
  //获取所有已有的图片fieldset名
  $("#M8610F002").children("fieldset.picture-fieldset").each(function () {
    var $this = $(this),
        $legend = $this.children("legend"),
        legend_name = $legend.text();
    fieldsetNameList.push(legend_name);
  });
  for (var index in fieldsetNameList) {
    if ($.inArray(fieldsetNameList[index], fileNameList) == -1) {
      $("#M8610F002").find("legend.picture-legend:contains(" + fieldsetNameList[index] + ")").parent().remove();
    }
  }
  var first = true;
  $.each(fileNameList, function (index, fileName) {
    if ($.inArray(fileName, fieldsetNameList) == -1) {

      //添加一个对应的fieldset
      var html = "", checked = "";
      if (first) {
        checked = "checked";
      }
      html += '<fieldset class="single-fieldset picture-fieldset">' +
          '<legend class="picture-legend">' + fileName + '</legend>' +
          '<input class="hide" name="picture_name" value="' + fileName + '">' +
          '<div>' +
          '<label class="my-label">标题:</label>' +
          '<input class="my-input" type="text" name="picture_title" placeholder="请输入图片标题" maxlength="16"/>' +
          '<input type="radio" ' + checked + ' name="cover" value="' + fileName + '">设为封面' +
          '</div><div>' +
          '<label class="my-textarea-label">描述:</label>' +
          '<textarea class="my-textarea" name="picture_desc" placeholder="请输入图片描述" maxlength="100"></textarea>' +
          '</div></fieldset>';
      $("#M8610F002").append(html);
      textarea_bind();
      first = false;
    }
  });
  //K.init($("#M8610F002"));
}

function textarea_bind() {
  $("#M8610F002").find("textarea").each(function (index) {
    $(this).unbind('input').bind('input', function () {
      var self = this,
          maxLength = parseInt($(this).attr("maxlength")),
          curLength = $(this).val().length,
          span_html = "";
      span_html += '<span><var class="word">' + (maxLength - curLength) + '</var>/' + maxLength + '</span>';
      //console.log(span_html);
      $.pt({
        target: self,
        position: 'r',
        align: 't',
        width: 'auto',
        height: 'auto',
        content: span_html
      });
    });
  });
}

/*
 * 定义拖动类
 */
function Pointer(x, y) {
  this.x = x;
  this.y = y;
}

function Position(left, top) {
  this.left = left;
  this.top = top;
}
