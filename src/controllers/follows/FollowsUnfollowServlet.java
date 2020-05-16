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
 * Servlet implementation class FollowsUnfollowServlet
 */
@WebServlet("/follows/unfollow")
public class FollowsUnfollowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowsUnfollowServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //フォロー解除の方法をかく
        //follow_flagとupdated_atを変更
        String _token = (String)request.getParameter("_token");
        if(_token != null &&_token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            Report r = em.find(Report.class, (Integer)(request.getSession().getAttribute("report_id")));

            Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
            Employee e = em.find(Employee.class, r.getEmployee().getId());

            Follow f = (Follow)em.createNamedQuery("getFollowTable", Follow.class).setParameter("follow", login_employee).setParameter("follower", e).getSingleResult();

            //f.〜でデータをアップデート
            f.setFollow((Employee)request.getSession().getAttribute("login_employee"));
            f.setFollower(r.getEmployee());
            f.setFollow_flag(0);
            f.setUpdated_at(new Timestamp(System.currentTimeMillis()));

            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();
            request.getSession().setAttribute("flush", "フォロー解除しました。");

            response.sendRedirect(request.getContextPath() + "/reports/show?id=" + r.getId());
        }
    }

}
