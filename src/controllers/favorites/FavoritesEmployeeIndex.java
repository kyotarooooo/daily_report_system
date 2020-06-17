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
 * Servlet implementation class FavoritesEmployeeIndex
 */
@WebServlet("/favorites/employee/index")
public class FavoritesEmployeeIndex extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavoritesEmployeeIndex() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();
        //日報をお気に入りした従業員一覧を表示させる(report_idが○のものを取ってくる)

        //ページネーションの追加(これを実装する)
        int page;
        try{
            page = Integer.parseInt(request.getParameter("page"));
        } catch(Exception e) {
            page = 1;
        }


        //ログインユーザー
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
        //特定の日報を取得
        Report r = em.find(Report.class, (Integer)(request.getSession().getAttribute("report_id")));
        //report_idに特定の日報、フラグが1になっているもの(お気に入りされている)を取ってきてListに格納(下で型を変えるためにfor文で回す)
        List<Favorite> favorite_employee = em.createNamedQuery("getFavoriteEmployees", Favorite.class).setParameter("report", r).getResultList();
        //employee型の配列を用意
        List<Employee> favorite_employee_list = new ArrayList<>();
        List<Integer> favorite_employee_list_count = new ArrayList<>();
        //お気に入りした従業員の総数を取得
        long favorite_employee_count = 0;
        //for文で回す
        for(Favorite favorite : favorite_employee) {
            //employeesテーブルのidに代入できるようにする
            Employee e = favorite.getEmployee();
            int employee_id = e.getId();
            //特定の日報をお気に入りしている従業員を取得(setParameter("id", 〜)に代入するためにはint型に変換する必要があったため上の2行が必要になる)
            List<Employee> employee = em.createNamedQuery("getFavoriteEmployee", Employee.class).setParameter("id", employee_id).getResultList();
            //上で取得してものをfavorite_employee_listに格納
            favorite_employee_list.addAll(employee);
            favorite_employee_list_count.add(employee_id);
        }
        if(favorite_employee_list.size() > 0) {
            favorite_employee_count = (long)em.createNamedQuery("getFavoriteEmployeeCount", Long.class).setParameter("favorite_employee_list_count", favorite_employee_list_count).getSingleResult();
        }
        int toIndex = ((page - 1) * 15) + 15;
        if(toIndex >= favorite_employee_list.size()) {
            toIndex = favorite_employee_list.size();
        }

        em.close();

        request.setAttribute("report", r);
        request.setAttribute("page", page);
        request.setAttribute("login_employee", login_employee);
        request.setAttribute("favorite_employee_list", favorite_employee_list.subList((page - 1) * 15, toIndex));
        request.setAttribute("favorite_employee_count", favorite_employee_count);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/favorites/employeeindex.jsp");
        rd.forward(request, response);
    }
}
