package de.hse.swa;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation for FileCounter
 */
@WebServlet(urlPatterns = { "/FileCounter" }, initParams = {
        @WebInitParam(name = "initparam1", value = "Dummy", description = "just to show init parameters") })
public class FileCounterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        FileCounter dao = new FileCounter();
        try {
            dao.getCount(); // just to initialize
        } catch (Exception e) {
            getServletContext().log("An exception occurred in FileCounter", e);
            throw new ServletException("An exception occurred in FileCounter" + e.getMessage());
        }
    }

    /**
     * @see HttpServlet#doGet()
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set a cookie for the user, so that the counter does not increase
        // every time the user press refresh
        HttpSession session = request.getSession(true);
        // Set the session valid for 5 secs
        session.setMaxInactiveInterval(5);
        FileCounter dao = new FileCounter();
        int count = 0;
        count = dao.getCount(); // just to initialize
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        if (session.isNew()) {
            count++;
            dao.save(count);
        }
        out.println("This site has been accessed " + count + " times.");
    }

    /**
     * @see HttpServlet#doPost()
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
