//package com.apigateway.filter;
//
//import java.net.http.HttpHeaders;
//
//import org.slf4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractChangeRequestUriGatewayFilterFactory;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.stereotype.Component;
//
//import com.apigateway.service.JwtUtil;
//import com.apigateway.service.Openfeign;
//
//import jakarta.validation.Validator;
//
//@Component
//public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
//	
//	private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AuthenticationFilter.class);
//
//	
//	@Autowired
//	private RouteValidator routeValidator;
//
//	
//	
//	 @Autowired
//	 private JwtUtil jwtUtil;
//
//	public static class Config {
//
//	}
//
//	@Override
//	public GatewayFilter apply(Config config) {
//		// TODO Auto-generated method stub
//		return ((exchange,chain) -> {
//			if(routeValidator.isSecured.test(exchange.getRequest())) {
//				//header content tokens
//				if(!exchange.getRequest().getHeaders().containsKey(org.springframework.http.HttpHeaders.AUTHORIZATION)) {
//					throw new RuntimeException("missing authorization header");
//				}
//				
//				String authHeader = exchange.getRequest().getHeaders().get(org.springframework.http.HttpHeaders.AUTHORIZATION).get(0);
//				if(authHeader != null && authHeader.startsWith("Bearer")) {
//					authHeader = authHeader.substring(7);
//			}try {
//				//REST Call to Auth Service
//				
//		//	String ans =openfeign.validateToken(authHeader);
//				
//			jwtUtil.validateToken(authHeader);
//			
//			
//			}catch(Exception e) {
//				
//
//				System.out.println("Error Occured Invalid access");
//				throw new RuntimeException("Unauthorize access to appplication");
//			}
//		}
//			return chain.filter(exchange);
//		});
//	}
//
//	public AuthenticationFilter() {
//		super(Config.class);
//	}
//
//}

package com.apigateway.filter;

import com.apigateway.service.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;
    
//    @Autowired
//    @Lazy
//    private Openfeign openfeign;

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                // Header contains token
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    logger.error("Missing authorization header for request: {}", exchange.getRequest().getPath());
                    throw new RuntimeException("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                logger.info("Received Authorization Header: {}", authHeader);

                String token = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    logger.info("Extracted Token: {}", token);
                }

                if (token == null) {
                    logger.error("Invalid authorization header format for request: {}", exchange.getRequest().getPath());
                    throw new RuntimeException("Invalid authorization header format");
                }

                try {
                	
                    jwtUtil.validateToken(token);
                //String s =openfeign.validateToken(token);
               
                    logger.info("Token validation successful for request: {}", exchange.getRequest().getPath());
                
                } catch (Exception e) {
                    logger.error("Unauthorized access for request: {}", exchange.getRequest().getPath(), e);
                    System.out.println("Error Occured Invalid access");
                    throw new RuntimeException("Unauthorized access to application");
                }
            }
            return chain.filter(exchange);
        });
    }

    public AuthenticationFilter() {
        super(Config.class);
    }
}