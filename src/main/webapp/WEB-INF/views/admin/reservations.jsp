<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="订单管理"/>
<c:set var="active" value="reservations"/>
<%@ include file="../common/admin-header.jspf" %>

<c:if test="${not empty flash}"><div class="alert alert-ok">${flash}</div></c:if>

<form class="toolbar" method="get" action="${ctx}/admin/reservations">
    <select name="status">
        <option value="">全部状态</option>
        <option value="RESERVED" ${filterStatus=='RESERVED'?'selected':''}>已预订</option>
        <option value="CHECKED_IN" ${filterStatus=='CHECKED_IN'?'selected':''}>已入住</option>
        <option value="CHECKED_OUT" ${filterStatus=='CHECKED_OUT'?'selected':''}>已退房</option>
        <option value="CANCELLED" ${filterStatus=='CANCELLED'?'selected':''}>已取消</option>
    </select>
    <input type="text" name="keyword" value="${keyword}" placeholder="订单号 / 入住人 / 手机号">
    <button class="btn btn-sm">查询</button>
    <a href="${ctx}/admin/reservations" class="btn btn-outline btn-sm">重置</a>
    <div class="spacer"></div>
    <span style="color:var(--muted);font-size:13px;">共 ${fn:length(orders)} 条</span>
</form>

<div class="card" style="overflow-x:auto;">
    <table class="table">
        <thead>
        <tr><th>订单号</th><th>房型</th><th>房间</th><th>入住人</th><th>手机</th><th>入住</th><th>退房</th><th class="num">金额</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="o" items="${orders}">
            <tr>
                <td style="font-size:13px;">${o.orderNo}</td>
                <td>${o.typeName}</td>
                <td>${empty o.roomNo ? '—' : o.roomNo}</td>
                <td>${o.guestName}</td>
                <td style="font-size:13px;">${o.guestPhone}</td>
                <td><fmt:formatDate value="${o.checkinDate}" pattern="MM-dd"/></td>
                <td><fmt:formatDate value="${o.checkoutDate}" pattern="MM-dd"/></td>
                <td class="num">¥<fmt:formatNumber value="${o.totalPrice}" maxFractionDigits="0"/></td>
                <td><span class="dot st-${o.status}">${o.statusText}</span></td>
                <td style="white-space:nowrap;">
                    <c:choose>
                        <c:when test="${o.status=='RESERVED'}">
                            <form method="post" action="${ctx}/admin/reservations" style="display:inline">
                                <input type="hidden" name="action" value="checkin"><input type="hidden" name="id" value="${o.id}">
                                <button class="btn btn-sm btn-gold">办理入住</button>
                            </form>
                            <form method="post" action="${ctx}/admin/reservations" style="display:inline" onsubmit="return confirm('确定取消该订单？');">
                                <input type="hidden" name="action" value="cancel"><input type="hidden" name="id" value="${o.id}">
                                <button class="btn btn-sm btn-outline">取消</button>
                            </form>
                        </c:when>
                        <c:when test="${o.status=='CHECKED_IN'}">
                            <form method="post" action="${ctx}/admin/reservations" style="display:inline">
                                <input type="hidden" name="action" value="checkout"><input type="hidden" name="id" value="${o.id}">
                                <button class="btn btn-sm">办理退房</button>
                            </form>
                        </c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="../common/admin-footer.jspf" %>
