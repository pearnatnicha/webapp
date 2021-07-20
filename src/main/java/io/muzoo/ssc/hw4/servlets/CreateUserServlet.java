package io.muzoo.ssc.hw4.servlets;

import io.muzoo.ssc.hw4.security.SecurityService;
import io.muzoo.ssc.hw4.security.UserService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateUserServlet extends AbstractRoutableHttpServlet {

    private SecurityService securityService;

    @Override
    public void setSecurityService(SecurityService securityService){
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (securityService.isAuthorized(req)){
            String username = securityService.getCurrentUsername(req);
//            UserService userService = UserService.getInstance();
//
            req.setAttribute("username", username);
//            req.setAttribute("users", userService.findAll());

            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/create.jsp");
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
        return "/user/create";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (securityService.isAuthorized(req)){
            // Ensure that username and displayName do not contain leading and trailing spaces
            String username = (String) req.getParameter("username");
            String displayName = (String) req.getParameter("displayName");
            String password = (String) req.getParameter("password");
            String cpassword = (String) req.getParameter("cpassword");

            UserService userService = UserService.getInstance();
            String errorMessage = null;

            // check if username valid
            if (userService.findByUsername(username) != null){
                // user already exists
                errorMessage = String.format("Username %s has already been taken", username);
            }

            // check if displayName valid
            else if (StringUtils.isBlank(displayName)){
                // displayName cannot be blank
                errorMessage = "Display Name cannot be blank";
            }

            // Check if password is valid
            else if (StringUtils.isBlank(password)) {
                errorMessage = "Password cannot be blank";
            }
            // check if confirmed password is correct
            else if (!StringUtils.equals(password, cpassword)) {
                // confirmed password mismatched
                errorMessage = "Confirmed password mismatches";
            }

            if (errorMessage != null) {
                req.getSession().setAttribute("hasError", true);
                req.getSession().setAttribute("message", errorMessage);
            }else{
                //create a user
                try {
                    // if no error redirect
                    userService.createUser(username, password, displayName);
                    req.getSession().setAttribute("hasError", false);
                    req.getSession().setAttribute("message", String.format("User %s has been created successfully", username));
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
            req.setAttribute("password", password);
            req.setAttribute("cpassword", cpassword);

            // if not success, it will arrive here
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/create.jsp");
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
