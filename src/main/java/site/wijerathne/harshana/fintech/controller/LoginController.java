package site.wijerathne.harshana.fintech.controller;

import site.wijerathne.harshana.fintech.service.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean validateUser = new LoginService().validateUser(username, password);
        System.out.println(validateUser);
        if(validateUser){
            HttpSession session = req.getSession();
            session.setAttribute("username", username);
            resp.sendRedirect("/");
        }else{
            resp.sendRedirect("pages/login.jsp");
        }
    }


}
