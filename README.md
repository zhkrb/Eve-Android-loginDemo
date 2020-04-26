# Eve-Android-loginDemo  
Android客户端使用EVE国服ESI登录demo
## Demo说明
使用 RxJava+Retrofit做网络请求，主要登录实现请看  
- [MainActivity](app/src/main/java/com/zhkrb/eve_oauth2/MainActivity.java)
- [AdvanceWebClient](app/src/main/java/com/zhkrb/eve_oauth2/AdvanceWebClient.java)
- [HttpUtil](app/src/main/java/com/zhkrb/eve_oauth2/netowrk/retrofit/HttpUtil.java)
## 登录流程
### 配置
https://esi.evepc.163.com/latest/swagger.json  
解析这个json，从securityDefinitions→evesso中解析出一个单独的json
```
authorizationUrl
scopes
```
分别为请求token地址和权限列表

### 设备ID
https://mpay-web.g.mkey.163.com/device/init?game_id=aecfu6bgiuaaaal2-g-ma79&device_type=PC&system_name=Windows&system_version=10&resolution=1920*185&device_model=64

1. Host:https://mpay-web.g.mkey.163.com/device/init 主域名
2. game_id:aecfu6bgiuaaaal2-g-ma79 EVE游戏id
3. device_type:PC 设备类型
4. system_name：Windows
5. system_version:10 系统版本
6. resolution:1920*185 大概是分辨率？
7. device_model:64 系统类型  
  
Header需要添加以下内容  
```
header.put("Origin","https://esi.evepc.163.com");
header.put("Content-Type","application/x-www-form-urlencoded");
```

返回json

```
{
    "code": 0,
    "device": {
        "id": "aiav5ilh3g54ndjj-d",
        "key": "39455df8e4f999fe2d7e594ee3e3ce75",
        "urs_device_id": "49630021AAECEA677B68DCB8F3BB4255FFEA5A668C22069DD40BED7FB5B724CC56EF1BC4BCA66104804E0889C39A2A4B"
    },
    "msg": "ok"
}
```
只需要id

### 登录
需要从最开始获取的登录地址拼接以下参数
1. response_type=token
2. "client_id=bc90aa496a404724a93f41b4f4e97761"
3. "redirect_uri=https%3A%2F%2Fesi.evepc.163.com%2Fui%2Foauth2-redirect.html"
4. "scope=esi-alliances.read_contacts.v1%20esi-wallet.read_character_wallet.v1"
5. "state=VGh1IEFwciAyMyAyMDIwIDE0OjQ1OjMxIEdNVCswODAwICjkuK3lm73moIflh4bml7bpl7Qp"
6. "realm=ESI"
7. "device_id=aiav5ijieg54bkzo-d"

##### 参数说明
1. response_type=token Oauth2 请求方式
2. client_id，正常来说需要在后台申请，这里使用ESI的应用id
3. redirect_uri 回调地址，登录成功后会回调这个地址，所以可以拦截这个Host的请求来获取token
4. scope 申请的权限
5. state Base64编码的日期时间，由js的new Date()获得
6. realm 应用名，也就是ESI
7. device_id 之前获取的设备id  
  
- 不带Header直接调用不管webView也好什么也好的应用内浏览器，因为需要自动获取后面的token，所以要保证浏览器能自己可控，方便拦截参数和管理cookie。  
- 注意每次请求时保证cookie为空，如果带网易的已登录cookie会报错。

### 拦截并获取Token
正常登录完成后会进入人物选择页面和权限确认页面，我们需要在这里处理后续的Token获取。  
当点击权限或者人物确认后，会请求`https://login.evepc.163.com/v2/oauth/authorize`这个网址，也就是之前拼接登录地址，但是多了一个人物选择的参数。这个请求如果正常通过，会返回一个304响应，目标地址是`https://esi.evepc.163.com/ui/oauth2-redirect.html`,也就是之前参数里的回调地址，这个304跳转网址后面跟的参数就是需要的Token了。
  

所以我们要获取这个304响应，从他的响应Header中把`Location`这个参数提取出来，然后正则出所需要的Token，格式见下  
```
https://esi.evepc.163.com/ui/oauth2-redirect.html#access_token={token}&expires_in=1199&state=RnJpIEFwciAyNCAyMDIwIDIzOjQxOjAxIEdNVCswODAwICjkuK3lm73moIflh4bml7bpl7Qp
```

这里我用`{token}`代替了正常的token，方便查看

### 使用
两种使用方法  
1. 拼接token到参数中`&token={token}`
2. 添加到header中`authorization: Bearer {token}`


以上  
--zhkrb  
  
## License
```
MIT License

Copyright (c) 2020 zhkrb

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
