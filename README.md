# conn TCP socket 研究项目

##cn.mh.com.bio包:BIO TCP 长短连接转换

>*SocketServiceStarter*:**主服务类**

>*SocketInitializer*:**启动器,项目启动后触发socket连接**

>*SocketConfig*:**配置信息初始类**

>*MonitorThread*:**监控线程,长连接发生异常则发起重启操作**

###客户通道:

>*ReceiveResponseThread*:**接收长连接响应线程,个数对应长连接数**

>*SendRequestThread*:**向长连接发送请求线程,一个,对长连接轮询发送**

>*ShortRequestThread*:**短连接请求处理线程,收到一个短连接，就会创建一个**

>*ShortServerThread*:**短连接服务线程,一个,等待短连接**

###服务通道:

>*ReceiveRequestThread*:**接收长连接请求的线程,个数对应长连接数**

>*ReqShortServerThread*:**想短连接服务端发起请求的线程,收到一个长连接请求，就会创建一个**

>*SendResponseThread*:**向长连接发送响应的线程，一个或者长连接个数,一个的话线程开销较小,多个的话效率更高**

>>``默认多个线程,单个线程见SendResponseThread/ReqShortServerThread中注释``

>*ShortClientThread*:**根据长连接请求创建短链接请求的线程**

####欢迎提出各种问题及意见
######*作者:ardorsaki*
######邮箱:*ardorsaki@163.com*``(有问题直接发送邮件)``