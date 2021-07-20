package io.muzoo.ssc.hw4.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends AbstractRoutableHttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        securityService.logout(request);
        response.sendRedirect("/login");

    }

    @Override
    public String getPattern() {
        return "/logout";
    }
}
