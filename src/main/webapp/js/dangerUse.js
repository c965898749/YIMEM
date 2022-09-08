// 禁止按F12调试
document.onkeydown = document.onkeyup = document.onkeypress = function (event) {
  var e = event || window.event || arguments.callee.caller.arguments[0];
  if (e && e.keyCode == 123) {
    mAlert();
    e.returnValue = false;
    return (false);
  }
}
function mAlert() {
  alert("感谢使用管理平台，禁止对控制台进行操作！");
}

// 防止鼠标右键浏览器‘检查’操作
setInterval(function () {
  debugger;
}, 100)

// 禁止右键
document.oncontextmenu = function () { return false; };


//16进制转文本
function decode(hexCharCodeStr){
  var trimedStr = hexCharCodeStr.trim();
  var rawStr =
    trimedStr.substr(0,2).toLowerCase() === "0x"
      ?
      trimedStr.substr(2)
      :
      trimedStr;
  var len = rawStr.length;
  if(len % 2 !== 0) {
    alert("Illegal Format ASCII Code!");
    return "";
  }
  var curCharCode;
  var resultStr = [];
  for(var i = 0; i < len;i = i + 2) {
    curCharCode = parseInt(rawStr.substr(i, 2), 16); // ASCII Code Value
    resultStr.push(String.fromCharCode(curCharCode));
  }
  return resultStr.join("");
}
