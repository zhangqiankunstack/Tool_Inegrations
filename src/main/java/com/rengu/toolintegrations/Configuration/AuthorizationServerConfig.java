package com.rengu.toolintegrations.Configuration;

import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权服务代码（认证服务器）（3）
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 17:18
 **/


@Configuration
@EnableAuthorizationServer          //这个注解就代表了它是个认证服务端。
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public AuthorizationServerConfig(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        //对称秘钥，资源服务器使用该秘钥来验证
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("rengu");
        return jwtAccessTokenConverter;
    }

    /**
     * 客户端配置（给谁发令牌）
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("OAUTH_CLIENT_ID")//客户端ID
                .authorizedGrantTypes("authorization_code", "client_credentials", "refresh_token", "password", "implicit")
                .secret(new BCryptPasswordEncoder().encode("OAUTH_CLIENT_SECRET"))//密码
                .scopes("SCOPES")//授权用户的操作权限
                .accessTokenValiditySeconds(60 * 60 * 6)//设置token有效期为6小时
                .refreshTokenValiditySeconds(60 * 60 * 24);//Refresh_token:24个小时
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {                    //用来配置令牌端点(Token Endpoint)的安全约束
        super.configure(security);
    }


    //jwtTokenStore存储
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {  //用来配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)
        //将增强的token设置到增强链中
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

        endpoints.tokenStore(tokenStore());
        endpoints.accessTokenConverter(accessTokenConverter());

        endpoints.tokenEnhancer(tokenEnhancerChain);
        endpoints.authenticationManager(authenticationManager);
        //设置用户详情服务
        endpoints.userDetailsService(userService);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {

        return new CustomTokenEnhancer();
    }


    //定制TokenEnhancer,加入一些额外的有用信息
    public class CustomTokenEnhancer implements TokenEnhancer {
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            UserEntity loginUser = userService.getUserByUsername(authentication.getName());
            Map<String, Object> additionalInfo = new HashMap<>();
            // 注意添加的额外信息，最好不要和已有的json对象中的key重名，容易出现错误
            additionalInfo.put("userId", loginUser.getId());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        }
    }
}
