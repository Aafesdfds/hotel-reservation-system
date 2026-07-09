<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty type ? '新增房型' : '编辑房型'}"/>
<c:set var="active" value="types"/>
<%@ include file="../common/admin-header.jspf" %>

<div class="panel" style="max-width:720px;">
    <h3>${empty type ? '新增房型' : '编辑房型'}</h3>
    <form action="${ctx}/admin/types" method="post" style="margin-top:14px;">
        <c:if test="${not empty type}"><input type="hidden" name="id" value="${type.id}"></c:if>
        <div class="form-grid2">
            <div class="form-row">
                <label>房型名称</label>
                <input type="text" name="name" value="${type.name}" required>
            </div>
            <div class="form-row">
                <label>房价（元/晚）</label>
                <input type="number" name="price" value="${type.price}" min="0" step="1" required>
            </div>
        </div>
        <div class="form-grid2">
            <div class="form-row">
                <label>可住人数</label>
                <input type="number" name="capacity" value="${empty type ? 2 : type.capacity}" min="1" required>
            </div>
            <div class="form-row">
                <label>面积（㎡）</label>
                <input type="number" name="area" value="${empty type ? 26 : type.area}" min="1" required>
            </div>
        </div>
        <div class="form-row">
            <label>床型</label>
            <input type="text" name="bedType" value="${type.bedType}" placeholder="如 1.8米大床 / 1.2米双床两张">
        </div>
        <div class="form-row">
            <label>设施（逗号分隔）</label>
            <input type="text" name="amenities" value="${type.amenities}" placeholder="免费WiFi,空调,液晶电视,独立卫浴">
        </div>
        <div class="form-row">
            <label>房型简介</label>
            <textarea name="description" rows="3">${type.description}</textarea>
        </div>
        <div class="form-row">
            <label>图片文件名</label>
            <input type="text" name="image" value="${empty type ? 'queen.jpg' : type.image}">
            <div class="hint">图片放在 static/images/rooms/ 目录下，填文件名即可，如 queen.jpg、deluxe.jpg</div>
        </div>
        <button class="btn btn-gold">保存</button>
        <a href="${ctx}/admin/types" class="btn btn-outline">返回</a>
    </form>
</div>

<%@ include file="../common/admin-footer.jspf" %>
