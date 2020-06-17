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
 * Servlet implementation class FavoritesCreateServlet
 */
@WebServlet("/favorites/create")
public class FavoritesCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavoritesCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //すでにお気に入りされていたらアップデートをかける、初めてだったらそのまま新規登録(試すときにデータベースを消しておく)
        //jspも条件分岐


        //お気に入りをクリックしたときの機能
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            //日報を一件取得(ReportShowでセッションに保存してある)
            Report r = em.find(Report.class, (Integer)request.getSession().getAttribute("report_id"));
            //ログインしているユーザー
            Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
            //条件分岐させるためにcountを使う
            long favorite_count = (long)em.createNamedQuery("favoriteExistenceCheck", Long.class).setParameter("employee", login_employee).setParameter("report", r).getSingleResult();
            //カウントが1だったらアップデートをかける、0だったら新規作成
            if(favorite_count == 1) {
                //データベースから取得してアップデート
                Favorite favorite = (Favorite)em.createNamedQuery("getFavoriteTable", Favorite.class).setParameter("employee", login_employee).setParameter("report", r).getSingleResult();
                //お気に入りしたユーザー(ログインしているユーザー)
                favorite.setEmployee(login_employee);
                //お気に入りされた日報
                favorite.setReport(r);
                //お気に入りされたらフラグを1にする
                favorite.setFavorite_flag(1);
                //お決まりのタイムスタンプ(アップデートのみ)
                favorite.setUpdated_at(new Timestamp(System.currentTimeMillis()));
                em.getTransaction().begin();
                em.getTransaction().commit();
                em.close();
            } else {
                //インスタンス作成後、値を格納
                Favorite favorite = new Favorite();
                //お気に入りしたユーザー(ログインしているユーザー)
                favorite.setEmployee(login_employee);
                //お気に入りされた日報
                favorite.setReport(r);
                //お気に入りされたらフラグを1にする
                favorite.setFavorite_flag(1);
                //お決まりのタイムスタンプ
                favorite.setCreated_at(new Timestamp(System.currentTimeMillis()));
                favorite.setUpdated_at(new Timestamp(System.currentTimeMillis()));
                em.getTransaction().begin();
                em.persist(favorite);
                em.getTransaction().commit();
                em.close();
            }

            //フラッシュメッセージ
            request.getSession().setAttribute("flush", "お気に入りに登録しました。");
            //report_idを削除
            request.getSession().removeAttribute("report_id");
            //リダイレクト
            response.sendRedirect(request.getContextPath() + "/reports/show?id=" + r.getId());
        }
    }

}
