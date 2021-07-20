package io.muzoo.ssc.hw4.servlets;

import io.muzoo.ssc.hw4.security.SecurityService;
import io.muzoo.ssc.hw4.security.User;
import io.muzoo.ssc.hw4.security.UserService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteUserServlet extends AbstractRoutableHttpServlet {

    private SecurityService securityService;

    @Override
    public void setSecurityService(SecurityService securityService){
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        if (securityService.isAuthorized(req)) {
            String username = securityService.getCurrentUsername(req);
            UserService userService = UserService.getInstance();
            // Just in case there is any error, we will silently suppress the error with nice error message
            try {
                User currentUser = userService.findByUsername(username);
                // We will delete user by username, so we need to get requested username from parameter
                User deletingUser = userService.findByUsername(req.getParameter("username"));
                // Prevent deleting own account, either from UI or send request directly to server
                if (StringUtils.equals(currentUser.getUsername(), deletingUser.getUsername())) {
                    req.getSession().setAttribute("hasFlash", true);
                    req.getSession().setAttribute("hasError", true);
                    req.getSession().setAttribute("message", "You cannot delete your own account.");
                } else {
                    if (userService.deleteUserByUsername(deletingUser.getUsername())) {
                        // Go to user list page with successful delete message
                        // Put message in the session
                        // These attributes are added to session so they will persist unless remove from session
                        // We need to ensure that they are deleted when they are read next time
                        // Since in all cases, it will be redirect to home page, so we will remove them in home servlet
                        req.getSession().setAttribute("hasError", false);
                        req.getSession().setAttribute("message", String.format("User %s is successfully deleted.", deletingUser.getUsername()));
                    } else {
                        // Go to user list page with error delete message
                        req.getSession().setAttribute("hasError", true);
                        req.getSession().setAttribute("message", String.format("Unable to delete user %s.", deletingUser.getUsername()));
                    }
                }
            } catch (Exception e) {
                // Go to user list page with error delete message
                req.getSession().setAttribute("hasError", true);
                req.getSession().setAttribute("message", String.format("Unable to delete user %s.", req.getParameter("username")));
            }
            resp.sendRedirect("/");
        }else{
            resp.sendRedirect("/login");
        }


//        Date date = new Date();
//        req.setAttribute("date1", date);


    }
    @Override
    public String getPattern() {
        return "/user/delete";
    }
}