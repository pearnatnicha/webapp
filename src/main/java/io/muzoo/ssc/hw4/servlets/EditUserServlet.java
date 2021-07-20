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

public class EditUserServlet extends AbstractRoutableHttpServlet {

    private SecurityService securityService;

    @Override
    public void setSecurityService(SecurityService securityService){
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (securityService.isAuthorized(req)){
            String username = StringUtils.trim(req.getParameter("username")); // From query part
            UserService userService = UserService.getInstance();

            User user = userService.findByUsername(username);
            req.setAttribute("user", user);
            req.setAttribute("username", user.getUsername());
            req.setAttribute("displayName", user.getDisplayName());

            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/edit.jsp");
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
        return "/user/edit";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (securityService.isAuthorized(req)){
            // Edit user is similar to create user but we only allow editing display name
            // Ensure that username and displayName do not contain leading and trailing spaces
            String username = StringUtils.trim(req.getParameter("username")); /* From query part. */
            String displayName = StringUtils.trim(req.getParameter("displayName"));

            UserService userService = UserService.getInstance();
            User user = userService.findByUsername(username);
            String errorMessage = null;

            // check if user exist
            if (user == null){
                // user does not exists
                errorMessage = String.format("Username %s does not exist", username);
            }

            // check if displayName valid
            else if (StringUtils.isBlank(displayName)){
                // displayName cannot be blank
                errorMessage = "Display Name cannot be blank";
            }

            if (errorMessage != null) {
                req.getSession().setAttribute("hasError", true);
                req.getSession().setAttribute("message", errorMessage);
            }else{
                //edit a user
                try {

                    userService.updateUserByUsername(username, displayName);
                    // if no error redirect
                    req.getSession().setAttribute("hasError", false);
                    req.getSession().setAttribute("message", String.format("User %s has been updated successfully", username));
                    resp.sendRedirect("/");
                    return;
                } catch (Exception e) {
                    req.getSession().setAttribute("hasError", true);
                    req.getSession().setAttribute("message", e.getMessage());
                }
            }

            // Prefill the form
            req.setAttribute("username", username);
            req.setAttribute("displayName", displayName);
            // if not success, it will arrive here
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/edit.jsp");
            requestDispatcher.include(req, resp);

            // removing attributes as they used is known as flash session
            req.getSession().removeAttribute("hasError");
            req.getSession().removeAttribute("message");


        }else{
            // Just add some extra precaution to delete these two attributes
            req.removeAttribute("hasError");
            req.removeAttribute("message");
            resp.sendRedirect("/login");
        }
    }
}
