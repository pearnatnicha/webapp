package io.muzoo.ssc.hw4.servlets;

import io.muzoo.ssc.hw4.security.SecurityService;
import io.muzoo.ssc.hw4.security.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class HomeServlet extends AbstractRoutableHttpServlet {

    private SecurityService securityService;

    @Override
    public void setSecurityService(SecurityService securityService){
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        if (securityService.isAuthorized(req)){
            String username = securityService.getCurrentUsername(req);
            UserService userService = UserService.getInstance();

            req.setAttribute("currentUser", userService.findByUsername(username));
            req.setAttribute("users", userService.findAll());

            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/home.jsp");
            requestDispatcher.include(req,resp);
            // Removing attributes as soon as they are used is known as flash session
            // Doesn't look like session attributes were removed
            req.getSession().removeAttribute("hasError");
            req.getSession().removeAttribute("message");


        }else{
            // Just add some extra precaution to delete these two attributes
            req.removeAttribute("hasError");
            req.removeAttribute("message");
            resp.sendRedirect("/login");
        }

//        Date date = new Date();
//        req.setAttribute("date1", date);


    }
    @Override
    public String getPattern() {
        return "/index.jsp";
    }
}