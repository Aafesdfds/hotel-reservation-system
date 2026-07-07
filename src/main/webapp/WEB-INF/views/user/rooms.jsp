<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="房型预订 · 云栖酒店"/>
<c:set var="active" value="rooms"/>
<%@ include file="../common/user-header.jspf" %>

<div class="container">
    <div class="page-head">
        <h1>房型预订</h1>
        <p style="color:var(--muted);">选择入住与退房日期，查看各房型的实时可订数量</p>
    </div>

    <form class="search-box" style="margin:10px 0 26px; max-width:none;" action="${ctx}/rooms" method="get">
        <div class="field">
            <label>入住日期</label>
            <input type="date" name="checkin" value="${checkin}" min="${checkin}" required>
        </div>
        <div class="field">
            <label>退房日期</label>
            <input type="date" name="checkout" value="${checkout}" required>
        </div>
        <div class="field" style="flex:0 0 auto;">
            <button type="submit" class="btn btn-gold" style="padding:10px 26px;">查询空房</button>
        </div>
    </form>

    <div class="alert alert-info">
        查询区间：<b>${checkin}</b> 至 <b>${checkout}</b>，共 <b>${nights}</b> 晚。下方为该区间各房型可预订数量。
    </div>

    <div class="room-grid" style="padding-bottom:40px;">
        <c:forEach var="t" items="${types}">
            <div class="room-card">
                <a class="thumb" href="${ctx}/room?id=${t.id}&checkin=${checkin}&checkout=${checkout}">
                    <img src="${ctx}/static/images/rooms/${t.image}" alt="${t.name}">
                </a>
                <div class="body">
                    <div style="display:flex;justify-content:space-between;align-items:center;">
                        <h3>${t.name}</h3>
                        <c:choose>
                            <c:when test="${t.availableCount == 0}"><span class="badge badge-full">已订满</span></c:when>
                            <c:when test="${t.availableCount <= 2}"><span class="badge badge-few">仅剩 ${t.availableCount} 间</span></c:when>
                            <c:otherwise><span class="badge badge-avail">有房 ${t.availableCount} 间</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="meta">${t.bedType} · ${t.area}㎡ · 可住${t.capacity}人</div>
                    <div class="tags">
                        <c:forEach var="a" items="${t.amenityList}" begin="0" end="3">
                            <span class="tag">${a}</span>
                        </c:forEach>
                    </div>
                    <div class="price-row">
                        <div class="price"><span class="num">¥<fmt:formatNumber value="${t.price}" maxFractionDigits="0"/></span><span class="unit">/晚</span></div>
                        <c:choose>
                            <c:when test="${t.availableCount == 0}">
                                <button class="btn btn-sm" disabled>已订满</button>
                            </c:when>
                            <c:otherwise>
                                <a href="${ctx}/booking?typeId=${t.id}&checkin=${checkin}&checkout=${checkout}" class="btn btn-gold btn-sm">立即预订</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@ include file="../common/user-footer.jspf" %>
