<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:if test="${flush != null}">
            <div id="flush_success">
                <c:out value="${flush}"></c:out>
            </div>
        </c:if>
        <c:choose>
            <c:when test="${report != null}">
                <h2>日報　詳細ページ</h2>

                <!-- 条件分岐を変える必要がある -->
                <c:choose>
                    <c:when test="${report.employee.id == login_employee.id}">
                        <!-- 自分の日報だったらお気に入りは表示させない -->
                    </c:when>
                    <c:when test="${favorite_count == 1}">
                        <form method="POST" action="<c:url value='/favorites/release' />">
                            <input type="hidden" name="_token" value="${_token}" />
                            <input class="black_favorite" type="submit" value="お気に入り中">
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form method="POST" action="<c:url value='/favorites/create' />">
                            <input type="hidden" name="_token" value="${_token}" />
                            <input class="white_favorite" type="submit" value="お気に入り" />
                        </form>
                    </c:otherwise>
                </c:choose>
                <div class="total_favorite">
                    <a href="<c:url value='/favorites/employee/index' />">お気に入り件数<c:out value="${favorite_employee_count}" />件</a>&nbsp;
                </div>
                <table>
                    <tbody>
                        <tr>
                            <th>氏名</th>
                            <td>
                                <c:choose>
                                    <c:when test="${report.employee.id == login_employee.id}">
                                        <p><c:out value="${report.employee.name}" /></p>
                                    </c:when>
                                    <c:when test="${follows_count == 1}">
                                        <p><c:out value="${report.employee.name}" /></p>
                                        <form method="POST" action="<c:url value='/follows/unfollow' />">
                                            <input type="hidden" name="_token" value="${_token}" />
                                            <input class="follow_button" type="submit" value="フォロー中" />
                                        </form>
                                    </c:when >
                                    <c:otherwise>
                                        <p><c:out value="${report.employee.name}" /></p>
                                        <form method="POST" action="<c:url value='/follows/create' />">
                                            <input type="hidden" name="_token" value="${_token}" />
                                            <input class="follow_button2" type="submit" value="フォロー" />
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th>日付</th>
                            <td><fmt:formatDate value="${report.report_date}" pattern="yyyy-MM-dd" /></td>
                        </tr>
                        <tr>
                            <th>内容</th>
                            <td>
                                <pre><c:out value="${report.content}" /></pre>
                            </td>
                        </tr>
                        <tr>
                            <th>登録日時</th>
                            <td>
                                <fmt:formatDate value="${report.created_at}" pattern="yyyy-MM-dd HH:mm:ss" />
                            </td>
                        </tr>
                        <tr>
                            <th>更新日時</th>
                            <td>
                                <fmt:formatDate value="${report.updated_at}" pattern="yyyy-MM-dd HH:mm:ss" />
                            </td>
                        </tr>
                    </tbody>
                </table>

                <c:if test="${sessionScope.login_employee.id == report.employee.id}">
                    <p><a href="<c:url value="/reports/edit?id=${report.id}" />">この日報を編集する</a></p>
                </c:if>
            </c:when>
            <c:otherwise>
                <h2>お探しのデータは見つかりませんでした。</h2>
            </c:otherwise>
        </c:choose>

        <p><a href="<c:url value="/reports/index" />">一覧に戻る</a></p>
    </c:param>
</c:import>