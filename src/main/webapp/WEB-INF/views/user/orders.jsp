<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="我的订单 · 云栖酒店"/>
<c:set var="active" value="orders"/>
<%@ include file="../common/user-header.jspf" %>

<div class="container">
    <div class="page-head">
        <h1>我的订单</h1>
    </div>

    <c:if test="${not empty flash}">
        <div class="alert alert-ok">${flash}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="panel" style="text-align:center;padding:50px;">
                <p style="color:var(--muted);">你还没有预订记录</p>
                <a href="${ctx}/rooms" class="btn btn-gold">去看看房型</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card" style="overflow-x:auto;margin-bottom:40px;">
                <table class="table">
                    <thead>
                    <tr>
                        <th>订单号</th><th>房型</th><th>房间号</th><th>入住</th><th>退房</th>
                        <th>晚数</th><th>入住人</th><th class="num">金额</th><th>状态</th><th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td>${o.orderNo}</td>
                            <td>${o.typeName}</td>
                            <td>${empty o.roomNo ? '—' : o.roomNo}</td>
                            <td><fmt:formatDate value="${o.checkinDate}" pattern="yyyy-MM-dd"/></td>
                            <td><fmt:formatDate value="${o.checkoutDate}" pattern="yyyy-MM-dd"/></td>
                            <td>${o.nights}</td>
                            <td>${o.guestName}</td>
                            <td class="num">¥<fmt:formatNumber value="${o.totalPrice}" maxFractionDigits="0"/></td>
                            <td><span class="dot st-${o.status}">${o.statusText}</span></td>
                            <td>
                                <c:if test="${o.status == 'RESERVED'}">
                                    <form action="${ctx}/orders" method="post" style="display:inline"
                                          onsubmit="return confirm('确定取消该订单吗？');">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="id" value="${o.id}">
                                        <button class="btn btn-sm btn-outline">取消</button>
                                    </form>
                                </c:if>
                                <c:if test="${o.status != 'RESERVED'}">—</c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="../common/user-footer.jspf" %>
