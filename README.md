本项目为指定号段的手机号爬取www.ip138.com网站获取信息
爬取前需进行如下操作：
1、把本项目打成jar包，放到能运行 java -jar 指令的电脑的某个路径下，如：桌面
2、在jar包所在的同一路径下创建 phone_msg.txt 文件，里面添加两行数据，如：
    phone:199
    start:1
3、使用java -jar spider_phone.jar 运行jar包。运行过程如下：
    1、首先会读取 phone_msg.txt的数据 会把 phone 和 start 拼接成 1990001 的7位数的手机号号段
    2、生成 http://www.ip138.com:8080/search.asp?mobile=1990001&action=mobile 的url
    3、生成 httpClient 和 httpGet（可按本机的浏览器信息，修改模拟请求头信息）
    4、接收网站返回的response，使用Jsoup解析，获取需要的html下的div的信息。
    5、对获取的信息进行处理（根据自己需要的格式处理），
       写入 199msg.txt（会根据phone_msg.txt 中的 phone 进行生成） 文件中。

重点！！！：
4、由于爬取的是小网站，大量持续的请求会使网站断开连接，jar包在cmd窗口会报错，并抛出异常。
   会打印报错日志："在100条数据报错:100,1990100,北京,北京,中国电信,010,100000"
   该日志表示 程序在写入第100条数据时候 网站断开了连接，
   修改 phone_msg.txt 文件的 start 为 报错时候的位置，如：start:100
   然后重新运行jar包，会从1990100开始爬取信息，继续写入199msg.txt文件。