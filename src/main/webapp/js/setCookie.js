// * 默认缩放值
const scale = {
    width: '1',
    height: '1',
};

// * 设计稿尺寸（px）
const baseWidth = 1920;
const baseHeight = 1080;

// const baseWidth = 1380;
// const baseHeight = 782;

// const baseWidth = 1565;
// const baseHeight = 865;


// * 需保持的比例（默认16:9）
const baseProportion = parseFloat((baseWidth / baseHeight).toFixed(5));

window.addEventListener('resize', this.resize);

function resize() {
    const appRef = this.$refs['appRef'];
    if (!appRef) return;
    // 当前宽高比
    const currentRate = parseFloat(
        (window.innerWidth / window.innerHeight).toFixed(5)
    );
    if (appRef) {
        if (currentRate > baseProportion) {
            // 表示更宽
            scale.width = (
                (window.innerHeight * baseProportion) /
                baseWidth
            ).toFixed(5);
            scale.height = (window.innerHeight / baseHeight).toFixed(5);
            // appRef.style.transform = `scale(${scale.width}, ${scale.height}) translate(-50%, -50%)`;
            appRef.style.transform = `scale(${scale.width}, ${scale.height})`;
        } else {
            // 表示更高
            scale.height = (
                window.innerWidth /
                baseProportion /
                baseHeight
            ).toFixed(5);
            scale.width = (window.innerWidth / baseWidth).toFixed(5);
            // appRef.style.transform = `scale(${scale.width}, ${scale.height}) translate(-50%, -50%)`;
            appRef.style.transform = `scale(${scale.width}, ${scale.height})`;
        }
    }
}

function getCookie(c_name)
{
    if (document.cookie.length>0)
    {
        c_start=document.cookie.indexOf(c_name + "=")
        if (c_start!=-1)
        {
            c_start=c_start + c_name.length+1
            c_end=document.cookie.indexOf(";",c_start)
            if (c_end==-1) c_end=document.cookie.length
            return unescape(document.cookie.substring(c_start,c_end))
        }
    }
    return ""
}

function setCookie(c_name,value,expiredays)
{
    var exdate=new Date()
    exdate.setDate(exdate.getDate()+expiredays)
    document.cookie=c_name+ "=" +escape(value)+
        ((expiredays==null) ? "" : ";expires="+exdate.toGMTString())
}

function checkCookie()
{
    token=getCookie('token')
    if (token!=null && token!="")
    {alert('Welcome again '+token+'!')}
    else
    {
        token=prompt('Please enter your name:',"")
        if (token!=null && token!="")
        {
            setCookie('token',token,30)
        }
    }
}
function syslogininfor() {
    $.ajax({
        url: "syslogininfor"
        , type: "post"
        ,data: {"cip": returnCitySN["cip"],"name":returnCitySN["cname"],"msg":document.title}
        , dataType: "json"
        , success: function (jsonData) {
        }
        , error: function (res) {
        }
    })
}
syslogininfor()
