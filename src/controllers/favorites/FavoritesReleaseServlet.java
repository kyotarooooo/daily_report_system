package controllers.favorites;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Favorite;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class FavoritesReleaseServlet
 */
@WebServlet("/favorites/release")
public class FavoritesReleaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavoritesReleaseServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //お気に入り解除(FavoritesCreateServletとほぼ同じ)
        String _token = request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            Report r = em.find(Report.class, (Integer)request.getSession().getAttribute("report_id"));
            Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");

            Favorite favorite = em.createNamedQuery("getFavoriteTable", Favorite.class).setParameter("employee", login_employee).setParameter("report", r).getSingleResult();
            //値をアップデートさせる
            favorite.setEmployee(login_employee);
            favorite.setReport(r);
            favorite.setFavorite_flag(0);
            favorite.setUpdated_at(new Timestamp(System.currentTimeMillis()));

            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();

            request.getSession().setAttribute("flush", "お気に入りを解除しました。");
            request.getSession().removeAttribute("report_id");
            response.sendRedirect(request.getContextPath() + "/reports/show?id=" + r.getId());
        }
    }

}
