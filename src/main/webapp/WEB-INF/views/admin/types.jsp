<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="房型管理"/>
<c:set var="active" value="types"/>
<%@ include file="../common/admin-header.jspf" %>

<c:if test="${not empty flash}"><div class="alert alert-ok">${flash}</div></c:if>

<div class="section-title">
    <h3>房型列表（共 ${fn:length(types)} 种）</h3>
    <a href="${ctx}/admin/types?action=new" class="btn btn-gold btn-sm">+ 新增房型</a>
</div>

<div class="card" style="overflow-x:auto;">
    <table class="table">
        <thead>
        <tr><th>图片</th><th>房型名称</th><th class="num">房价</th><th>可住</th><th>床型</th><th>面积</th><th>房间数</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="t" items="${types}">
            <tr>
                <td><img src="${ctx}/static/images/rooms/${t.image}" alt="" style="width:66px;height:46px;object-fit:cover;border-radius:5px;"></td>
                <td><b>${t.name}</b></td>
                <td class="num">¥<fmt:formatNumber value="${t.price}" maxFractionDigits="0"/></td>
                <td>${t.capacity}人</td>
                <td>${t.bedType}</td>
                <td>${t.area}㎡</td>
                <td>${t.totalRooms}</td>
                <td style="white-space:nowrap;">
                    <a href="${ctx}/admin/types?action=edit&id=${t.id}" class="btn btn-sm btn-outline">编辑</a>
                    <a href="${ctx}/admin/types?action=delete&id=${t.id}" class="btn btn-sm btn-danger"
                       onclick="return confirm('确定删除该房型吗？');">删除</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="../common/admin-footer.jspf" %>
