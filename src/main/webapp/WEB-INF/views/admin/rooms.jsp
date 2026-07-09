<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="房间管理"/>
<c:set var="active" value="rooms"/>
<%@ include file="../common/admin-header.jspf" %>

<c:if test="${not empty flash}"><div class="alert alert-ok">${flash}</div></c:if>

<div class="section-title">
    <h3>房间列表（共 ${fn:length(rooms)} 间）</h3>
    <a href="${ctx}/admin/rooms?action=new" class="btn btn-gold btn-sm">+ 新增房间</a>
</div>

<form class="toolbar" method="get" action="${ctx}/admin/rooms">
    <select name="typeId">
        <option value="">全部房型</option>
        <c:forEach var="t" items="${types}">
            <option value="${t.id}" ${filterType == t.id ? 'selected' : ''}>${t.name}</option>
        </c:forEach>
    </select>
    <select name="status">
        <option value="">全部房态</option>
        <option value="AVAILABLE" ${filterStatus=='AVAILABLE'?'selected':''}>空闲</option>
        <option value="OCCUPIED" ${filterStatus=='OCCUPIED'?'selected':''}>已入住</option>
        <option value="DIRTY" ${filterStatus=='DIRTY'?'selected':''}>待清洁</option>
        <option value="CLEANING" ${filterStatus=='CLEANING'?'selected':''}>清洁中</option>
        <option value="CLEANED" ${filterStatus=='CLEANED'?'selected':''}>已清洁</option>
        <option value="MAINTENANCE" ${filterStatus=='MAINTENANCE'?'selected':''}>维修</option>
    </select>
    <button class="btn btn-sm">筛选</button>
    <a href="${ctx}/admin/rooms" class="btn btn-outline btn-sm">重置</a>
</form>

<div class="card" style="overflow-x:auto;">
    <table class="table">
        <thead>
        <tr><th>房间号</th><th>房型</th><th>楼层</th><th>房态</th><th>备注</th><th style="width:340px;">房态流转 / 操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${rooms}">
            <tr>
                <td><b>${r.roomNo}</b></td>
                <td>${r.typeName}</td>
                <td>${r.floor} 层</td>
                <td><span class="dot st-${r.status=='AVAILABLE'?'available':r.status}">${r.statusText}</span></td>
                <td>${r.note}</td>
                <td style="white-space:nowrap;">
                    <c:choose>
                        <c:when test="${r.status=='OCCUPIED'}"><span style="color:var(--muted);font-size:13px;">在住中，退房后可清洁</span></c:when>
                        <c:when test="${r.status=='DIRTY'}">
                            <form method="post" action="${ctx}/admin/rooms" style="display:inline">
                                <input type="hidden" name="action" value="status"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="target" value="CLEANING">
                                <button class="btn btn-sm">开始清洁</button>
                            </form>
                        </c:when>
                        <c:when test="${r.status=='CLEANING'}">
                            <form method="post" action="${ctx}/admin/rooms" style="display:inline">
                                <input type="hidden" name="action" value="status"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="target" value="CLEANED">
                                <button class="btn btn-sm btn-gold">清洁完成</button>
                            </form>
                        </c:when>
                        <c:when test="${r.status=='CLEANED'}">
                            <form method="post" action="${ctx}/admin/rooms" style="display:inline">
                                <input type="hidden" name="action" value="status"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="target" value="AVAILABLE">
                                <button class="btn btn-sm">上架为空闲</button>
                            </form>
                        </c:when>
                        <c:when test="${r.status=='MAINTENANCE'}">
                            <form method="post" action="${ctx}/admin/rooms" style="display:inline">
                                <input type="hidden" name="action" value="status"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="target" value="AVAILABLE">
                                <button class="btn btn-sm">取消维修</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${ctx}/admin/rooms" style="display:inline">
                                <input type="hidden" name="action" value="status"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="target" value="MAINTENANCE">
                                <button class="btn btn-sm btn-outline">报修</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                    <a href="${ctx}/admin/rooms?action=edit&id=${r.id}" class="btn btn-sm btn-outline">编辑</a>
                    <form method="post" action="${ctx}/admin/rooms" style="display:inline" onsubmit="return confirm('确定删除房间 ${r.roomNo} 吗？');">
                        <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${r.id}">
                        <button class="btn btn-sm btn-danger">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="../common/admin-footer.jspf" %>
