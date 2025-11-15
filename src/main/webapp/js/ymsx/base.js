document.writeln("<script type=\"text/javascript\" src=\"/g/template/jelly/js/jquery.js\"></script>");
document.writeln("<script type=\"text/javascript\" src=\"/g/template/jelly/js/jquery.lazyload.min.js\"></script>");
document.writeln("<script type=\"text/javascript\" src=\"/g/template/jelly/js/common.js?v=20190609\"></script>");
document.writeln("<script type=\"text/javascript\" src=\"/g/template/jelly/js/scorll.js?v=20190609\"></script>");
//每日情话
     fetch('https://api.vvhan.com/api/text/love')  
        .then(response => response.text()) // 解析响应为文本  
        .then(data => {  
            // 将API返回的文本插入到id为"result"的元素中  
            document.getElementById('result').textContent = data;  
        });  