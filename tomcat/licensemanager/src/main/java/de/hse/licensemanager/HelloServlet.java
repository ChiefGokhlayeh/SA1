package de.hse.licensemanager;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.Query;

@WebServlet(name = "HelloServlet", urlPatterns = { "/hello" }, loadOnStartup = 1)
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 5736844113933764478L;
    private static final String PERSISTENCE_UNIT_NAME = "licensemanager";

    public HelloServlet() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        final Query q = entityManager.createQuery("SELECT m FROM SystemGroup m");

        System.out.println("We found " + q.getResultList().size() + " entries for SystemGroup");

        for (Object obj : q.getResultList()) {
            final SystemGroup sg = (SystemGroup) obj;
            System.out.println("\t" + sg.getDisplayName());
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().print("Hello, World!");
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
