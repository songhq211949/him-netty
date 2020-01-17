package com.lmxdawn.him.api.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.ws.RequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @program: springboot
 * @description:
 * @author: Sid
 * @date: 2018-11-19 09:21
 * @since: 1.0
 **/
@Order(0)
/**
 * 注册过滤器
 * */
@WebFilter(filterName = "RequestResponseLogFilter", urlPatterns = "/*")
public class RequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getQueryString();
        String servletPath = request.getServletPath();
        String url = request.getRequestURI();
        filterChain.doFilter(request, response);
        logger.info("========================》  url:" + url + " & queryString:" + path+" & servletPath:"+servletPath);
        logger.info("========================》request uri: {}",request.getRequestURI());
        logger.info("========================》request ContentType: {}",request.getContentType());
        logger.info("========================》response status: {}",response.getStatus());
        logger.info("========================》response ContentType: {}",response.getContentType());
    }

}