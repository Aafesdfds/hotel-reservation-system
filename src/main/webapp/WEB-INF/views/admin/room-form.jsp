<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty room ? '新增房间' : '编辑房间'}"/>
<c:set var="active" value="rooms"/>
<%@ include file="../common/admin-header.jspf" %>

<div class="panel" style="max-width:640px;">
    <h3>${empty room ? '新增房间（可一次新增多间）' : '编辑房间'}</h3>
    <form action="${ctx}/admin/rooms" method="post" style="margin-top:14px;">
        <c:if test="${not empty room}"><input type="hidden" name="id" value="${room.id}"></c:if>
        <div class="form-grid2">
            <div class="form-row">
                <label>${empty room ? '起始房间号' : '房间号'}</label>
                <input type="text" name="roomNo" value="${room.roomNo}" required placeholder="如 301">
            </div>
            <div class="form-row">
                <label>楼层</label>
                <input type="number" name="floor" value="${empty room ? 3 : room.floor}" min="1" required>
            </div>
        </div>
        <c:if test="${empty room}">
            <div class="form-row">
                <label>数量（一次新增几间）</label>
                <input type="number" name="count" value="1" min="1" max="100" required style="max-width:160px;">
                <div class="hint">从起始房号连续生成，比如起始 301、数量 5，就一次建好 301、302、303、304、305；已存在的房号会自动跳过。</div>
            </div>
        </c:if>
        <div class="form-grid2">
            <div class="form-row">
                <label>所属房型</label>
                <select name="typeId" required>
                    <c:forEach var="t" items="${types}">
                        <option value="${t.id}" ${room.typeId == t.id ? 'selected' : ''}>${t.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-row">
                <label>房态</label>
                <select name="status">
                    <option value="AVAILABLE" ${room.status=='AVAILABLE'?'selected':''}>空闲</option>
                    <option value="OCCUPIED" ${room.status=='OCCUPIED'?'selected':''}>已入住</option>
                    <option value="DIRTY" ${room.status=='DIRTY'?'selected':''}>待清洁</option>
                    <option value="CLEANING" ${room.status=='CLEANING'?'selected':''}>清洁中</option>
                    <option value="CLEANED" ${room.status=='CLEANED'?'selected':''}>已清洁</option>
                    <option value="MAINTENANCE" ${room.status=='MAINTENANCE'?'selected':''}>维修</option>
                </select>
            </div>
        </div>
        <div class="form-row">
            <label>备注（选填）</label>
            <input type="text" name="note" value="${room.note}" placeholder="如 无烟房、朝南">
        </div>
        <button class="btn btn-gold">保存</button>
        <a href="${ctx}/admin/rooms" class="btn btn-outline">返回</a>
    </form>
</div>

<%@ include file="../common/admin-footer.jspf" %>
