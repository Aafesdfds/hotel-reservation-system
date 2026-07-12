<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="云栖酒店 · 在线预订"/>
<c:set var="active" value="home"/>
<%@ include file="../common/user-header.jspf" %>
<% java.time.LocalDate _t = java.time.LocalDate.now(); %>
<c:set var="today" value="<%= _t.toString() %>"/>
<c:set var="tomorrow" value="<%= _t.plusDays(1).toString() %>"/>

<div class="hero">
    <div class="container">
        <div class="en-sub">COMFORT &amp; ELEGANCE</div>
        <h1>入住云栖，安放旅途</h1>
        <p>十余种精心布置的房型，从经济单人间到全景总统套房，为每一次出行找到刚好合适的那一间。</p>
    </div>
</div>

<div class="container">
    <form class="search-box" action="${ctx}/rooms" method="get">
        <div class="field">
            <label>入住日期</label>
            <input type="date" name="checkin" value="${today}" min="${today}" required>
        </div>
        <div class="field">
            <label>退房日期</label>
            <input type="date" name="checkout" value="${tomorrow}" min="${tomorrow}" required>
        </div>
        <div class="field" style="flex:0 0 auto;">
            <button type="submit" class="btn btn-gold" style="padding:10px 26px;">查询空房</button>
        </div>
    </form>
</div>

<div class="section">
    <div class="container">
        <div class="section-head">
            <h2>精选房型</h2>
            <div class="line"></div>
            <p>点击房型查看详情与实时空房，在线即可完成预订</p>
        </div>
        <div class="room-grid">
            <c:forEach var="t" items="${types}">
                <div class="room-card">
                    <a class="thumb" href="${ctx}/room?id=${t.id}">
                        <img src="${ctx}/static/images/rooms/${t.image}" alt="${t.name}">
                    </a>
                    <div class="body">
                        <h3>${t.name}</h3>
                        <div class="meta">${t.bedType} · ${t.area}㎡ · 可住${t.capacity}人</div>
                        <div class="desc"><c:out value="${t.description}"/></div>
                        <div class="price-row">
                            <div class="price"><span class="num">¥<fmt:formatNumber value="${t.price}" maxFractionDigits="0"/></span><span class="unit">/晚</span></div>
                            <a href="${ctx}/room?id=${t.id}" class="btn btn-sm">查看详情</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<%@ include file="../common/user-footer.jspf" %>
