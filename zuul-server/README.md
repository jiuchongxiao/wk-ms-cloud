# http://start.spring.io/

# What is Zuul?

    Zuul is the front door for all requests from devices and web sites to the backend of the Netflix 
    streaming application. As an edge service application, Zuul is built to enable dynamic routing, 
    monitoring, resiliency and security. It also has the ability to route requests to multiple Amazon 
    Auto Scaling Groups as appropriate. 
    
# Why did we build Zuul?
    
    The volume and diversity of Netflix API traffic sometimes results in production issues arising quickly and without warning. We need a system that allows us to rapidly change behavior in order to react to these situations.
    Zuul uses a range of different types of filters that enables us to quickly and nimbly apply functionality to our edge service. These filters help us perform the following functions:

    1. Authentication and Security - identifying authentication requirements for each resource and rejecting requests that do not satisfy them.
    2. Insights and Monitoring - tracking meaningful data and statistics at the edge in order to give us an accurate view of production.
    3. Dynamic Routing - dynamically routing requests to different backend clusters as needed.
    4. Stress Testing  - gradually increasing the traffic to a cluster in order to gauge performance.
    5. Load Shedding - allocating capacity for each type of request and dropping requests that go over the limit.
    6. Static Response handling - building some responses directly at the edge instead of forwarding them to an internal cluster
    7. Multiregion Resiliency - routing requests across AWS regions in order to diversify our ELB usage and move our edge closer to our member
     
# Zuul Components
    1. zuul-core - library which contains the core functionality of compiling and executing Filters
    2. zuul-simple-webapp - webapp which shows a simple example of how to build an application with zuul-core
    3. zuul-netflix - library which adds other NetflixOSS components to Zuul - using Ribbon for routing requests, for example.
    4. zuul-netflix-webapp - webapp which packages zuul-core and zuul-netflix together into an easy to use package

# Zuul 类型
    1. filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
    2. pre：可以在请求被路由之前调用
    3. route：在路由请求时候被调用
    4. post：在routing和error过滤器之后被调用
    5. error：处理请求时发生错误时被调用



2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/baidu/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/cc/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/static/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/config-server/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/consul/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/helloclient/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/helloserver/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
2017-01-21 18:16:36.969  INFO 24624 --- [nio-8080-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping      : Mapped URL path [/zuulserver/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]