package controllers.follows;

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
import models.Follow;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class FollowerReportIndex
 */
@WebServlet("/follower/report/index")
public class FollowerReportIndex extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowerReportIndex() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();
        int page;
        try{
            page = Integer.parseInt(request.getParameter("page"));
        } catch(Exception e) {
            page = 1;
        }
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
        List<Follow> followers = em.createNamedQuery("getFollowers", Follow.class).setParameter("follow", login_employee).getResultList();
        List<Report> reportsList = new ArrayList<>();
        List<Employee> followerList = new ArrayList<>();
        long follower_reports_count = 0;
        for(Follow follow : followers) {
            List<Report> reports = em.createNamedQuery("getFollowerReports", Report.class).setParameter("employee", follow.getFollower()).getResultList();
            reportsList.addAll(reports);
            followerList.add(follow.getFollower());
        }
        if(followerList.size() > 0) {
            follower_reports_count = (long)em.createNamedQuery("getFollowerReportsCount", Long.class).setParameter("followerList", followerList).getSingleResult();
        }
        int toIndex = ((page - 1) * 15) + 15;
        if(toIndex >= reportsList.size()) {
            toIndex = reportsList.size();
        }

        em.close();
        request.setAttribute("reports", reportsList.subList((page - 1) * 15, toIndex));
        request.setAttribute("follower_reports_count", follower_reports_count);
        request.setAttribute("page", page);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/follows/reportsindex.jsp");
        rd.forward(request, response);
    }

}
