package zuulserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
zuul:
  #Service will be mapped under the /api URI
  prefix: /api
#  ignoredServices: '*'
  routes:
    customer-by-address:
      path: /customer-by-address/**
      url: http://localhost:9098
    customer-by-service:
      path: /customer-by-service/**
      serviceId: CUSTOMER-SERVICE
    static:
      path: /static/**


#这里的配置表示，访问/baidu/** 直接重定向到http://www.baidu.com
zuul.routes.baidu.path=/baidu/**
zuul.routes.baidu.url=http://www.baidu.com

#反响代理配置
#这里的配置类似nginx的反响代理
#当请求/api/**会直接交给listOfServers配置的服务器处理
#当stripPrefix=true的时候 （http://127.0.0.1:8181/api/user/list -> http://192.168.1.100:8080/user/list）
#当stripPrefix=false的时候（http://127.0.0.1:8181/api/user/list -> http://192.168.1.100:8080/api/user/list）
zuul.routes.api.path=/api/**
zuul.routes.api.stripPrefix=false

#url重写配置
#这里的配置，相当于访问/index/** 会直接渲染/home的请求内容(和直接请求/home效果一样), url地址不变
zuul.routes.index.path=/index/**
zuul.routes.index.url=forward:/home


 */
@SpringBootApplication
@EnableZuulProxy
@RestController
@EnableDiscoveryClient
public class ZuulServerApplication {
	
    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
    }

    @RequestMapping("/timeout")
    public String timeout() throws InterruptedException {
        Thread.sleep(2*1000);
        return "timeout";
    }

    @Bean
    public ZuulFallbackProvider zuulFallbackProvider() {
        return new ZuulFallbackProvider() {
            @Override
            public String getRoute() {
                return "zuulserver";
            }

            @Override
            public ClientHttpResponse fallbackResponse() {
                return new ClientHttpResponse() {
                    @Override
                    public HttpStatus getStatusCode() throws IOException {
                        return HttpStatus.OK;
                    }

                    @Override
                    public int getRawStatusCode() throws IOException {
                        return 200;
                    }

                    @Override
                    public String getStatusText() throws IOException {
                        return "OK";
                    }

                    @Override
                    public void close() {

                    }

                    @Override
                    public InputStream getBody() throws IOException {
                        return new ByteArrayInputStream("fallback".getBytes());
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        return headers;
                    }
                };
            }
        };
    }

}
