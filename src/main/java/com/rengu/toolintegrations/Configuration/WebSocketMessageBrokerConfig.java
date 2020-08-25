package com.rengu.toolintegrations.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 启用STOMP协议WebSocket配置
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-09-04 17:44
 **/

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    /**
     *configureMessageBroker()方法：配置了一个简单的消息代理，通俗点讲就是设置消息连接请求的各种规范信息。
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //定义一个（或多个）客户端订阅地址的前缀信息，也就是客户端接收服务器端发送消息的前缀信息。
        config.enableSimpleBroker("/deviceInfo", "/onlineDevice", "/deployProgress");
    }

    /**
     * registerStompEndpoints()方法：添加一个服务端点，来接收客户端的连接，将"/OMS"路径注册为STOMP端点。
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {    //这个方法的作用是添加一个服务器点，来接收客户端的连接
        //添加一个/OMS一个端点，客户端可以通过这个端点来进行连接，withSockJS作用是添加SockJS支持。
        registry.addEndpoint("/OMS").setAllowedOrigins("*").withSockJS();
    }
}
