# Eve-Android-loginDemo
Android client login demo using Eve Serenity service ESI
## Demo description
Use rxjava + retrofit to make network request. Please see the main login implementation
- [MainActivity](app/src/main/java/com/zhkrb/eve_oauth2/MainActivity.java)
- [AdvanceWebClient](app/src/main/java/com/zhkrb/eve_oauth2/AdvanceWebClient.java)
- [HttpUtil](app/src/main/java/com/zhkrb/eve_oauth2/netowrk/retrofit/HttpUtil.java)
## Login process
### Configuration

https://esi.evepc.163.com/latest/swagger.json

Parse this JSON, and parse a separate JSON from `securitydefinitions → eventso`

```
authorizationUrl
scopes
```
They are the request token address and permission list

### Device ID

#### URL
https://mpay-web.g.mkey.163.com/device/init?game_id=aecfu6bgiuaaaal2-g-ma79&device_type=PC&system_name=Windows&system_version=10&resolution=1920*185&device_model=64

#### Method

`GET`

#### Parameters

1. Host:`https://mpay-web.g.mkey.163.com/device/init` main domain name
2. game_id: `aecfu6bgiuaaaal2-g-ma79` Eve game ID
3. device_type:`PC` Equipment type
4. system_name：`Windows`
5. system_version:`10` System version
6. resolution:`1920*185`, about resolution?
7. device_model:`64` System type

#### Header
The header needs to add the following
```
header.put("Origin","https://esi.evepc.163.com");
header.put("Content-Type","application/x-www-form-urlencoded");
```
#### Responses
Return to JSON

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

Just need ID

### Login

You need to splice the following parameters from the initial login address

#### URL
https://login.evepc.163.com/v2/oauth/authorize?response_type=token&client_id=bc90aa496a404724a93f41b4f4e97761&redirect_uri=https://esi.evepc.163.com/ui/oauth2-redirect.html&scope=esi-alliances.read_contacts.v1%20esi-wallet.read_character_wallet.v1&state=VGh1IEFwciAyMyAyMDIwIDE0OjQ1OjMxIEdNVCswODAwICjkuK3lm73moIflh4bml7bpl7Qp&realm=ESI&device_id=aiav5ijieg54bkzo-d

#### Method

`GET`

#### Parameters

1. Host: `login.evepc.163.com/v2/oauth/authorize`
2. response_type: `token`
3. client_id: `bc90aa496a404724a93f41b4f4e97761`
4. redirect_uri: `https%3A%2F%2Fesi.evepc.163.com%2Fui%2Foauth2-redirect.html`
5. scope: `esi-alliances.read_contacts.v1%20esi-wallet.read_character_wallet.v1`
6. state: `VGh1IEFwciAyMyAyMDIwIDE0OjQ1OjMxIEdNVCswODAwICjkuK3lm73moIflh4bml7bpl7Qp`
7. realm: `ESI`
8. device_id: `aiav5ijieg54bkzo-d`



##### Parameter description

1. Host: get form `swagger.json`
2. response_type: oauth2 request mode
3. client_id: normally, you need to apply in the background. The application ID of ESI is used here
4. redirect_uri: callback address, which will be called back after successful login, so you can intercept the host's request to obtain the token
5. scope: application authority
6. state: The date and time encoded by state Base64 is obtained by JS's new date()
7. realm: application name, which is ESI
8. device_id: The device id just obtained

- Call the in application browser directly without header, no matter WebView or anything, because you need to automatically obtain the following token, so you need to ensure that the browser can control itself, and it is convenient to intercept parameters and manage cookies.
- Pay attention to ensure that the cookie is empty every time you request. If you have logged in the cookie with Netease, you will report an error.



### Intercept and obtain token

After normal login, you will enter the person selection page and permission confirmation page. We need to process the subsequent token acquisition here.
When you click permission or person confirmation, you will request` https://login.evepc.163.com/v2/oauth/authorize `This web address, which is the previous splicing login address, but has one more character selection parameter. If the request passes normally, a 304 response will be returned. The destination address is` https://esi.evepc.163.com/ui/oauth2-redirect.html `, which is the callback address in the previous parameter. The 304 jump URL is followed by the required token.

So we need to get the 304 response, extract the parameter 'location' from its response header, and then regularize the token required. See the format below

```
https://esi.evepc.163.com/ui/oauth2-redirect.html#access_ token={token}&expires_ in=1199&state=RnJpIEFwciAyNCAyMDIwIDIzOjQxOjAxIEdNVCswODAwICjkuK3lm73moIflh4bml7bpl7Qp
```

Here, I use '{token}' instead of the normal token for easy viewing

### Use
Two ways to use

1. Splice the token into the parameter '& token = {token}`
2. Add to header ` authorization: bearer {token}`

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
