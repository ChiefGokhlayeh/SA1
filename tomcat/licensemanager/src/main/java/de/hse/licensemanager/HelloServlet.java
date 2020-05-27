package de.hse.licensemanager;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.User;

@WebServlet(name = "HelloServlet", urlPatterns = { "/hello" }, loadOnStartup = 1)
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 5736844113933764478L;

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().println("Hello, World!");

        final List<User> users = UserDao.getInstance().getUsers();
        response.getWriter().println("We found " + users.size() + " users:");

        for (final User user : users) {
            response.getWriter().println(
                    "\t" + user.getLoginname() + " from company " + user.getCompanyDepartment().getCompany().getName());
        }
    }

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null)
            name = "World";
        request.setAttribute("user", name);
        request.getRequestDispatcher("response.jsp").forward(request, response);
    }
}
