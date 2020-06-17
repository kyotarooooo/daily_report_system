package controllers.reports;

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

        //フォロー機能〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜
        //フォローボタンで追加
        Employee e = em.find(Employee.class, r.getEmployee().getId());
        //条件分岐で追加
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
        //followがログインしているユーザー、followerが日報作成者、follow_flagが1(フォローしている)これに該当したデータがあったら1を返す。なかったら0を返す。
        long follows_count = (long)em.createNamedQuery("followCheck", Long.class).setParameter("follow", login_employee).setParameter("follower", e).getSingleResult();

        //お気に入り機能〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜
        //お気に入りボタンを表示させるときに使う条件分岐
        long favorite_count = (long)em.createNamedQuery("favoriteCheck", Long.class).setParameter("employee", login_employee).setParameter("report", r).getSingleResult();
        //ある日報をお気に入りしている従業員を取得
        List<Favorite> favorite_employee = em.createNamedQuery("getFavoriteEmployees", Favorite.class).setParameter("report", r).getResultList();
        //favorite_employeeをfor文で回すため配列を用意(お気に入りしている従業員)
        List<Employee> favorite_employee_list = new ArrayList<>();
        //お気に入りしている従業員の件数を取得
        List<Integer> favorite_employee_list_count = new ArrayList<>();
        //初期化
        long favorite_employee_count = 0;
        //for文で回す
        for(Favorite favorite : favorite_employee) {
            Employee employee = favorite.getEmployee();
            int employee_id = employee.getId();
            //setParameterのidはint型なのでemployee_idにする必要があるため上の2つの文が必要
            List<Employee> employees = em.createNamedQuery("getFavoriteEmployee", Employee.class).setParameter("id", employee_id).getResultList();
            favorite_employee_list.addAll(employees);
            favorite_employee_list_count.add(employee_id);
        }
        if(favorite_employee_list.size() > 0) {
            favorite_employee_count = (long)em.createNamedQuery("getFavoriteEmployeeCount", Long.class).setParameter("favorite_employee_list_count", favorite_employee_list_count).getSingleResult();
        }


        em.close();

        request.setAttribute("report", r);
        request.setAttribute("_token", request.getSession().getId());
        //フォローボタンで追加
        request.getSession().setAttribute("report_id", r.getId());
        request.setAttribute("employee", e);
        //条件分岐で追加
        request.setAttribute("follows_count", follows_count);
        request.setAttribute("login_employee", login_employee);
        //お気に入り機能で追加
        request.setAttribute("favorite_count", favorite_count);
        request.setAttribute("favorite_employee_count", favorite_employee_count);
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