package site.wijerathne.harshana.fintech.controller;

import site.wijerathne.harshana.fintech.dto.LoginRequestDTO;
import site.wijerathne.harshana.fintech.model.User;
import site.wijerathne.harshana.fintech.service.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        LoginRequestDTO loginDTO = new LoginRequestDTO(username, password);
        User authenticatedUser = loginService.authenticate(loginDTO, req);

        if (authenticatedUser != null) {
            HttpSession session = req.getSession();
            session.setAttribute("username", authenticatedUser);
            resp.sendRedirect("/");
        } else {
            resp.sendRedirect("pages/login.jsp?error=invalid");
        }
    }
}
