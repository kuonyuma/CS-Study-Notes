@[TOC](目录)

---

# 前言
本篇文章主要通过通俗易懂的生活化比喻（如“送快递”、“去医院看病”等），带你拆解 HTTP 协议中最核心的**HTTP 请求**部分。我们将从抓包工具的实际操作出发，详细剖析请求的四大组成部分：请求行（包含 URL 与方法）、请求头（Header，重点解析长短连接、防盗链及鉴权机制）以及请求体（Body）。希望能帮助你把抽象的网络协议转化为大脑中直观的画面。

---

# 引入
TCP与IP协议主要讲的是，一台计算机发出的数据如何到达另一台计算机上。
就像送快递一样，将包裹从北京送往上海。

我们买东西的目的是使用包裹里面的东西。
HTTP协议主要讲的就是我们如何使用别人传递过来的数据。
如同说明书一样。

HTTP的通信方式大概是这样的。
我在浏览器里输入这一串字符串"www.sogou.com"。此时浏览器会给搜狗的服务器发送一个请求。

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/7277659ff25049aa86214c6aa4d3faa6.png)


我按下回车后，浏览器会得到一个搜狗搜索的页面。

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/a0879053009246fc93c983ca9dbc36c3.png)



搜狗的服务器会给我返回一个响应。我之所以看到这个搜狗界面，就是因为浏览器得到了这个响应里面的内容并解析了。

# 抓包工具
我们可以通过``抓包工具``来查看，浏览器给搜狗服务器发送的请求，搜狗服务器返回的响应。

这里我使用的工具叫做``fiddler``。

这就是我输入www.sogou.com按下回车后抓包得到的结果

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/d687df2d62094fb6a2ecb53b8a0eee6d.png)
点击进去我们可以查看请求与响应。

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/dc4f91fd82904d59b75b54c774f4bab3.png)
这里上面的红框内是请求内容，下面的红框则是响应的信息。

接下来我们就是来聊聊，如何读懂请求。


# 请求

我们以上述图中的请求来分析。

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/2452afe1ad574ddc8c97161f23795c4a.png)

一个请求被分成四个部分。
1. 请求行（Request Line）
	红框上面的那一行，包含请求方法、URL和HTTP版本号。
2. header
	红框里面的内容。
3. 空行
	区分header与body。相当于分隔符。
4. body
	紫色框里面的，不过这个请求没有内容。

## URL
 https://www.sogou.com/
 这就是URL，也就是我们常说的网址。
 一个常见的URL的格式为：
 协议 + IP + 端口 + 文件路径 + 查询字符串 + #片段标识符

https这里就是一个协议。https是HTTP的加密版本，后续我们会聊到。
协议有很多种，学习数据库的时候应该学过一个叫做JDBC的东西。
比如：jdbc:mysql

关于IP与端口。这里并没有看见IP与端口，其原因是这里用了域名，代替了IP与端口。我们先看看另一个URL。``http://127.0.0.1:8080/Class/put``
这里我们看到其中IP是127.0.0.1端口是8080。

关于文件路径。这里的文件路径是Class/put。
可以这样理解，你通过IP与端口找到了图书馆在哪里。你通过文件路径就知道，你想要看的书在多少层楼，哪个书架。

关于查询字符串（query String）。
我们看这个URL。

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/2ec02752e39f4c9387216a35929b7fd4.png)
你可以看到web后面有一个问号``?``，这后面的就是查询字符串的内容，
它是``键值对``的格式``key=val``，等号左边是key，右边则是值。其中键值对之间由``&``分割开。

URL的最后有时还会带一个 `#` 号，被称为片段标识符（Fragment）或锚点。例如 `https://www.sogou.com/index.html#section1`，主要用于网页内跳转到特定位置，不会发送给服务器。
有点类似于书签，打开书后就是你上次阅读到的地方。

一个常见的URL常见的内容大概就是这些东西。如果想知道更加详细的内容可以看``https://datatracker.ietf.org/doc/html/rfc1738``。

这里补充一下域名这个概念。
正常来讲我们是通过IP去访问一个网页的，但是这样的方式不利于记忆。
于是为了偷懒我们就研发出来了域名，这样就非常的直观。一个域名对应一个IP。

细心的朋友可能看见了URL后面还有一串数字。`/HTTP/1.1`
这是目前HTTP的版本。它告诉通信双方，我们这次交流遵循的是 1.1 版本的规则。

## 方法
我们看到了URL的前面有一个叫做GET的前缀。

GET可以理解为，浏览器获取服务器上的某个资源。
它的特征是：
1. 查询字符串可以为空。
2. 通常不携带body内容，即使携带，也可能会被服务器忽略。

方法其实还有很多种，这里介绍一个常见的：POST。

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/ff45bbcf01aa4e608e9e814b76e0975a.png)

这就是用PostMan，构造的一个POST请求。
它与GET最大的不同在于它的body里面其实是可以有内容的，只不过这里没有写。

直观感受 POST 的 Body：
 如果这是一个真正的注册请求，它的 Body 里可能会长这样：
 1. 表单格式：`username=lisi&password=123`
 2. JSON格式：`{"username": "lisi", "password": "123"}`

说一下两者的区别。

1. 两者其实都能用来获取服务器上的某些资源。

2. 在语义上的区别，GET一般用来获取，POST一般用来发数据。

3. GET如果要传递数据一般是写在queryString里面，POST一般是写在body里面。

4. GET是幂等的，POST不幂等。

5. GET可以被缓存，POST不能被缓存。

幂等：多次相同的请求，所造成的结果相同。
GET：你在浏览器上重复搜索“HTTP协议”，浏览器也只是重复地给你返回相同的界面。
POST：你给商家重复付款，你的钱会重复地减少。

缓存：
GET：你这次搜索“HTTP协议”，服务器就把这个信息存储下来，方便下次调用，而不是重复去查找。
POST：你这次买东西用了100，如果你下次去商店买东西，商家看到你就直接扣了100，即使你没有买到100价值的东西。所以这里的支付信息就不能存储。

注意在不加密的情况下，POST和GET其实都是不安全的，都是明文传输，好比你直接在大街上吼一嗓子。

这里给出其他的方法。

TRACE：回显服务器端收到的请求，测试的时候会用到这个
HEAD：类似于GET，只不过响应体不返回，只返回响应头
OPTIONS：返回服务器所支持的请求方法
PUT：与POST作用类似
DELETE：删除服务器指定资源

## header（报头）
1. HOST
	表示的是主机的IP与端口，Host: 127.0.0.1:8080。
2. User-Agent
	表示的用户的浏览器，电脑的信息
	User-Agent: PostmanRuntime/7.54.0
	User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 
	
	这里Mozilla/5.0是浏览器代号
	Windows NT 10.0; Win64; x64，电脑系统是win11，64位。
	Chrome/148.0.0.0，谷歌浏览器及其版本号，
	(KHTML, like Gecko)这个是Linux系统常使用的浏览器，
	Safari/537.36，苹果浏览器。

3. Content-Length
	表示请求中body的长度
	Content-Length: 0，body长度为0

4. Content-Type 与 Accept 
	*   `Content-Type`：
	表示body中的数据类型。这就好比我给你写信，我在信封上写“这是一封纯中文信件”（`text/plain`），
	或者“这是一个包裹着照片的盒子”（常用于上传文件的 `multipart/form-data`），
	又或者“这是一份结构化表格”（`application/json`）。
	* `Accept`：
	向服务器声明该浏览器可以处理哪些数据类型。
	这就好比你对我说：“我只能看懂中文和英文（`text/html`），
	如果你给我发法文，我就看不懂了。”

5. Referer 
	表示由哪一个页面跳转过来的。
	这就好比你去某家公司面试，前台问你：“你是怎么知道我们公司的？”
	你回答：“我是从Boss直聘上看到的。”这个“Boss直聘”就是你的 `Referer`。

6. Connection
	表示这次是长连接还是短连接。这就好比打电话 vs 发微信。
	*   **短连接 (`close`)**：就像“打电话说一件事，说完立马挂断”。如果马上又要说第二件事，还得重新拨号（重新建立 TCP 连接），非常耗时。
	*   **长连接 (`keep-alive`)**：HTTP/1.1 默认的方式。就像“拨通电话后，一直不挂断”，你可以接二连三地向对方要东西（请求多个资源，如网页里的多张图片），等全部要完了，或者过了一段时间没说话了，再挂断。这大大提高了网页加载速度。

7. Accept-Encoding 
	告诉服务器，浏览器支持哪些压缩算法。
	这就好比你在网上买了一个超级大的毛绒玩具。直接寄过来运费（带宽）极贵。
	于是你告诉商家：“你可以把它**抽真空压缩**后再寄给我，我家里有**打气筒（解压能力）**可以把它还原”。
	最常见的值是 `gzip`，告诉服务器请把网页代码压缩后再发给我，这样传输更快。

8. Cookie / Session
	这个比较重要，这里将会花较大篇幅讲解。
	
	首先我们得明确，HTTP是无状态的。就好比你在小卖部里买一包烟，第二天你再去，直接对老板说，老板还是昨天那个。此时的老板就会一脸懵逼的看着你，他并不知道你昨天是否来过，他会把所有来过的客人都当作是第一次来这个店里。
	去医院看病大概是这样的一个流程。
	你去正畸科，大夫会问你的名字，看看你的牙齿大概的情况。这时候会让你填一个表格，这个表格里面有你的个人信息。填完表格后医生会给你发一张小卡片。让你去某个科室具体去查看情况。你走到这个科室里面，大夫就只会让你拿出卡片，然后在机器上滴一下，此时医生就知道你的情况了。然后开始展开调查.....
	以后你去医院复诊，只需要在机器上滴一下卡片，医生就明白该干什么了。
	如果这个卡片弄丢了，此时你再滴，机器也读不出信息。

	Session就可以理解为这里的表格。
	Cookie就可以理解为这个小卡片。
	Session是存储在服务器端（如内存或数据库中）的，Cookie是存储在你电脑硬盘上面的。
	Cookie是HTTP协议的一部分，而Session不是HTTP协议的内容，它是应用服务器（如Tomcat、Spring）为了记录用户状态，基于Cookie实现的一种机制。

	你第一次访问对方的程序，对方的服务器就会为你创建一个Session和Cookie。以后你再去访问对方的程序，浏览器就自动带上了这个Cookie。
	我们看这样的一串代码
	```java
	  
	@RestController  
	@RequestMapping("/Login")  
	public class Login {  
	  
	    @RequestMapping("/input")  
	    public String inPut(HttpSession session){  
	  
	        String keyval = (String)session.getAttribute("Loginkey");  
	  
	        if(StringUtils.hasLength(keyval)){  
	            return "您好"+ keyval;  
	        }  
	  
	        session.setAttribute("Loginkey","lisi");  
	        return "您已经初始化账号";  
	    }  
	  
	}
	```
	这里模拟了登录的情况，此时我们第一次访问这个网站，服务器会自动返回``您已经初始化账号 ``，我们来看看。

	此时我还没有按下回车，可以看见右边Cookie里面是没有内容的。
	![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/064481a20ac242a88d76214716881650.png)
	按下回车后。

	![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/f4dd20e877dd43a19490948ad25bab85.png)
	可以看见，服务器返回了信息，右侧也创建了一个东西，这个其实就是SessionID，相当于你的档案账号。

	此时当我再次重新访问。

	![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/f9fe41da162b4d3295ce613d3c65b6ed.png)
	这里由于我已经创建好了Session，服务器已经存储了我的信息，当我拿着这个SessionID去重复访问这个程序时，程序就知道我是lisi。
	如果我把这个SessionID删除...

	![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/ff00ddf646a64becaa1294bc00e5d904.png)
	此时按下回车后。

	![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/00f096dd33094d6d81199b7bfe443e40.png)


# 总结
1. 整体结构：
HTTP请求包含四个部分：请求行、请求头(Header)、空行、请求体(Body)。
2. 请求行关键要素
包含了方法（如 GET/POST）、URL 以及 HTTP 版本号。
3. GET 与 POST 核心区别
GET 通常用于获取数据，具有幂等性且可缓存；POST 用于发送数据，数据一般放 Body 里。注意，不加 HTTPS 时两者都不安全，都是明文传输。
4. 请求头核心字段梳理
    * `Content-Type` / `Accept`：声明“我发的是什么”与“我能看懂什么”。
    * `Referer`：标明跳转来源.
    * `Connection`：控制长连接与短连接。
    * `Accept-Encoding`：声明客户端支持的压缩算法，
5. 状态保持机制
HTTP 本身无状态。传 Web 依靠 `Cookie` 和 `Session`
