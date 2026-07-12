<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${type.name} · 云栖酒店"/>
<c:set var="active" value="rooms"/>
<%@ include file="../common/user-header.jspf" %>

<div class="container">
    <div class="page-head">
        <div class="breadcrumb"><a href="${ctx}/rooms">房型预订</a> / ${type.name}</div>
    </div>

    <div class="detail-grid" style="padding-bottom:44px;">
        <div class="detail-img">
            <img src="${ctx}/static/images/rooms/${type.image}" alt="${type.name}">
        </div>
        <div>
            <h1 style="font-size:28px;">${type.name}</h1>
            <div class="price" style="margin:6px 0 4px;">
                <span class="num">¥<fmt:formatNumber value="${type.price}" maxFractionDigits="0"/></span><span class="unit">/晚</span>
            </div>
            <p style="color:#555;"><c:out value="${type.description}"/></p>

            <ul class="spec-list">
                <li><span>床型</span><span>${type.bedType}</span></li>
                <li><span>面积</span><span>${type.area} ㎡</span></li>
                <li><span>可住</span><span>${type.capacity} 人</span></li>
                <li><span>房间数</span><span>共 ${type.totalRooms} 间</span></li>
            </ul>

            <div class="tags" style="margin-bottom:18px;">
                <c:forEach var="a" items="${type.amenityList}">
                    <span class="tag">${a}</span>
                </c:forEach>
            </div>

            <div class="panel" style="padding:18px 20px;">
                <form action="${ctx}/booking" method="get">
                    <input type="hidden" name="typeId" value="${type.id}">
                    <div class="form-grid2">
                        <div class="form-row">
                            <label>入住日期</label>
                            <input type="date" name="checkin" value="${checkin}" min="${checkin}" required>
                        </div>
                        <div class="form-row">
                            <label>退房日期</label>
                            <input type="date" name="checkout" value="${checkout}" required>
                        </div>
                    </div>
                    <div style="display:flex;align-items:center;justify-content:space-between;">
                        <div>
                            <c:choose>
                                <c:when test="${type.availableCount == 0}"><span class="badge badge-full">该区间已订满</span></c:when>
                                <c:when test="${type.availableCount <= 2}"><span class="badge badge-few">仅剩 ${type.availableCount} 间</span></c:when>
                                <c:otherwise><span class="badge badge-avail">可订 ${type.availableCount} 间</span></c:otherwise>
                            </c:choose>
                        </div>
                        <c:choose>
                            <c:when test="${type.availableCount == 0}">
                                <button class="btn" disabled>已订满</button>
                            </c:when>
                            <c:otherwise>
                                <button type="submit" class="btn btn-gold" style="padding:10px 30px;">去预订</button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/user-footer.jspf" %>
