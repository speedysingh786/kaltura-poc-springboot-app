package com.nessathon.filter;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class AppFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String requestUrl = httpServletRequest.getRequestURI();

        if (requestUrl.matches("/app[\\w/]+")) {

            HttpSession session = httpServletRequest.getSession();
            if (session == null || session.getAttribute("validatedUsername") == null) {

                ((HttpServletResponse) servletResponse).sendRedirect("/login.html");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }
}