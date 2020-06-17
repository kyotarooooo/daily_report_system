package controllers.favorites;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
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
 * Servlet implementation class FavoritesReportIndex
 */
@WebServlet("/favorites/report/index")
public class FavoritesReportIndex extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavoritesReportIndex() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch(Exception e) {
            page = 1;
        }

        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
        //ログインユーザーがお気に入りしている日報を取得
        List<Favorite> favorite_report = em.createNamedQuery("getFavoriteReportsIndex", Favorite.class).setParameter("employee", login_employee).getResultList();
        //Favorite型からReport型に変更したものを入れる
        List<Report> favorite_report_list = new ArrayList<>();
        List<Integer> favorite_report_list_count = new ArrayList<>();
        long favorite_report_count = 0;
        for(Favorite favorite : favorite_report) {
            //int型に変える必要がある
            //まずReport型に変更
            Report report = favorite.getReport();
            //int型に変更
            int report_id = report.getId();
            //ログインユーザーがお気に入りしている日報のidをint型の変数に格納して、それに対応した日報をReportテーブルから取得
            List<Report> reports = em.createNamedQuery("getFavoriteReports", Report.class).setParameter("id", report_id).getResultList();
            //上で取得したものをfavorite_report_listに入れていく
            favorite_report_list.addAll(reports);
            favorite_report_list_count.add(report_id);
        }
        if(favorite_report_list.size() > 0) {
            favorite_report_count = (long)em.createNamedQuery("getFavoriteReportsCount", Long.class).setParameter("favorite_report_list_count", favorite_report_list_count).getSingleResult();
        }
        //ページネーション
        int toIndex = ((page - 1) * 15) + 15;
        if(toIndex >= favorite_report_list.size()) {
            toIndex = favorite_report_list.size();
        }

        em.close();
        request.setAttribute("page", page);
        request.setAttribute("reports", favorite_report_list.subList((page - 1) * 15, toIndex));
        request.setAttribute("favorite_report_count", favorite_report_count);


        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/favorites/reportindex.jsp");
        rd.forward(request, response);
    }

}
