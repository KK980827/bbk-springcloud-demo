package com.bbk.springcloud.demo.provider.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 针对Zuul设置过滤器，实现身份验证等的操作。
 */
@Component
public class AuthFilter extends ZuulFilter {

    /**
     * 定义拦截器类型：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
     * pre：路由之前
     * routing：路由之时
     * post： 路由之后
     * error：发送错误调用
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 设置过滤器顺序。
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 判断当前请求是否适用于当前过滤器，如果不适用，则不执行run();
     * 当前，仅仅针对请求路径包含hello-api的请求进行验证。
     */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletRequest request = ctx.getRequest();
        System.out.println("判断是否进行拦截：" + request.getRequestURI());
        return request.getRequestURI().contains("hello-api");
    }

    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
        final RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletRequest request = ctx.getRequest();
        final String auth = request.getParameter("auth");
        if ("123".equals(auth)) {
            //通过验证。
            System.out.println("通过验证");
            return null;
        }
        System.out.println("Auth is invalid");
        ctx.setResponseStatusCode(401);
        ctx.setSendZuulResponse(false);
        ctx.getResponse().getWriter().write("Token is invalid");
        return null;
    }
}
