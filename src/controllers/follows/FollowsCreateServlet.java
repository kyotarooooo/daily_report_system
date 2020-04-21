package controllers.follows;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Follow;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class FollowsCreateServlet
 */
@WebServlet("/follows/create")
public class FollowsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowsCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();


            Report r = em.find(Report.class, (Integer)(request.getSession().getAttribute("report_id")));

            Follow f = new Follow();
            f.setFollow((Employee)request.getSession().getAttribute("login_employee"));
            f.setFollower(r.getEmployee());
            f.setFollow_flag(1);
            f.setCreated_at(new Timestamp(System.currentTimeMillis()));
            f.setUpdated_at(new Timestamp(System.currentTimeMillis()));

            em.getTransaction().begin();
            em.persist(f);
            em.getTransaction().commit();
            em.close();
            request.getSession().setAttribute("flush", "フォローしました。");

            request.getSession().removeAttribute("report_id");

            //あとでreportのshowにリダイレクトされるように設定する
            response.sendRedirect(request.getContextPath() + "/reports/index");
        }
    }

}
