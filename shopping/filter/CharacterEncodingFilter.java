package com.shopping.filter;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * 字符编码过滤器
 */
// 注解注册移除，使用 web.xml 中的配置
public class CharacterEncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodingParam = filterConfig.getInitParameter("encoding");
        if (encodingParam != null && !encodingParam.isEmpty()) {
            this.encoding = encodingParam;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        // 不在此处设置 response.setContentType，避免误把静态资源（CSS/JS）当成 HTML 返回

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
