package controllers.reports;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsShowServlet
 */
@WebServlet("/reports/show")
public class ReportsShowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsShowServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));
        //フォローボタンで追加
        Employee e = em.find(Employee.class, r.getEmployee().getId());
        //条件分岐で追加
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
        //followがログインしているユーザー、followerが日報作成者、follow_flagが1(フォローしている)これに該当したデータがあったら1を返す。なかったら0を返す。
        long follows_count = (long)em.createNamedQuery("followCheck", Long.class).setParameter("follow", login_employee).setParameter("follower", e).getSingleResult();
        em.close();

        request.setAttribute("report", r);
        request.setAttribute("_token", request.getSession().getId());
        //フォローボタンで追加
        request.getSession().setAttribute("report_id", r.getId());
        request.setAttribute("employee", e);
        //条件分岐で追加
        request.setAttribute("follows_count", follows_count);
        request.setAttribute("login_employee", login_employee);
        //フォローしたとフォロー解除のフラッシュメッセージを消す
        //もしセッションスコープにフラッシュメッセージが登録されていたら...
        if(request.getSession().getAttribute("flush") != null) {
            //フラッシュメッセージをセッションスコープから取得
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            //そして削除
            request.getSession().removeAttribute("flush");
        }


        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/show.jsp");
        rd.forward(request, response);
    }

}