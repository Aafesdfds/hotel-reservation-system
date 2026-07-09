<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="会员管理"/>
<c:set var="active" value="users"/>
<%@ include file="../common/admin-header.jspf" %>

<div class="section-title">
    <h3>注册用户（共 ${fn:length(users)} 人）</h3>
</div>

<div class="card" style="overflow-x:auto;">
    <table class="table">
        <thead>
        <tr><th>ID</th><th>用户名</th><th>姓名</th><th>手机号</th><th>性别</th><th>角色</th><th>注册时间</th></tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td>${u.id}</td>
                <td><b>${u.username}</b></td>
                <td>${u.realName}</td>
                <td>${u.phone}</td>
                <td>${u.gender}</td>
                <td>
                    <c:choose>
                        <c:when test="${u.role=='ADMIN'}"><span class="dot st-CHECKED_IN">管理员</span></c:when>
                        <c:otherwise><span class="dot st-available">普通会员</span></c:otherwise>
                    </c:choose>
                </td>
                <td><fmt:formatDate value="${u.createdAt}" pattern="yyyy-MM-dd"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="../common/admin-footer.jspf" %>
