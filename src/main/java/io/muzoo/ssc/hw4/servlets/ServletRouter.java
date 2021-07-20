package io.muzoo.ssc.hw4.servlets;


import io.muzoo.ssc.hw4.security.SecurityService;
import io.muzoo.ssc.hw4.security.User;
import io.muzoo.ssc.hw4.security.UserService;
import io.muzoo.ssc.hw4.servlets.AbstractRoutableHttpServlet;
import io.muzoo.ssc.hw4.servlets.HomeServlet;
import io.muzoo.ssc.hw4.servlets.LoginServlet;
import io.muzoo.ssc.hw4.servlets.LogoutServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import java.util.List;

public class ServletRouter {
    private SecurityService securityService;

    public void setSecurityService(SecurityService securityService){
        this.securityService = securityService;
    }

    private final List<Class<? extends AbstractRoutableHttpServlet>> servletClasses = new ArrayList<>();

    {
        servletClasses.add(HomeServlet.class);
        servletClasses.add(DeleteUserServlet.class);
        servletClasses.add(CreateUserServlet.class);
        servletClasses.add(EditUserServlet.class);
        servletClasses.add(LoginServlet.class);
        servletClasses.add(LogoutServlet.class);
        servletClasses.add(ChangePasswordServlet.class);
    }

    public void init(Context ctx) {
        SecurityService securityService = new SecurityService();
        securityService.setUserService(UserService.getInstance());

        for (Class<? extends AbstractRoutableHttpServlet> servletClass: servletClasses) {
            try {
                AbstractRoutableHttpServlet httpServlet = servletClass.newInstance();
                httpServlet.setSecurityService(securityService);
                Tomcat.addServlet(ctx, servletClass.getSimpleName(), httpServlet);
                ctx.addServletMappingDecoded(httpServlet.getPattern(), servletClass.getSimpleName());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
