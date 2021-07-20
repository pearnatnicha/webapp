package io.muzoo.ssc.hw4;

import io.muzoo.ssc.hw4.security.SecurityService;
import io.muzoo.ssc.hw4.security.UserService;
import io.muzoo.ssc.hw4.servlets.HomeServlet;
import io.muzoo.ssc.hw4.servlets.LoginServlet;
import io.muzoo.ssc.hw4.servlets.LogoutServlet;
import io.muzoo.ssc.hw4.servlets.ServletRouter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import javax.servlet.ServletException;
import java.io.File;

public class Webapp {

    public static void main(String[] args) {
        File docBase = new File("src/main/webapp/");
        docBase.mkdirs();
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8501);

        SecurityService securityService = new SecurityService();
        securityService.setUserService(UserService.getInstance());

        ServletRouter servletRouter = new ServletRouter();
        servletRouter.setSecurityService(securityService);

        Context ctx;

        try {
            ctx = tomcat.addWebapp("", docBase.getAbsolutePath());
            servletRouter.init(ctx);

            /* Customize error page for redirection. */
            ErrorPage error404Page = new ErrorPage();
            error404Page.setErrorCode(404);
            error404Page.setLocation("/WEB-INF/error404.jsp");
            ctx.addErrorPage(error404Page);

            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException ex) {
            ex.printStackTrace();
        }

    }
}
