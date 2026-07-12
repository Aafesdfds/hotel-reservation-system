<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="填写预订信息 · 云栖酒店"/>
<c:set var="active" value="rooms"/>
<%@ include file="../common/user-header.jspf" %>

<div class="container">
    <div class="page-head">
        <div class="breadcrumb"><a href="${ctx}/rooms">房型预订</a> / <a href="${ctx}/room?id=${type.id}">${type.name}</a> / 填写信息</div>
        <h1>填写预订信息</h1>
    </div>

    <div class="detail-grid" style="padding-bottom:44px;">
        <div class="panel">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            <form action="${ctx}/booking" method="post">
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
                <div class="form-grid2">
                    <div class="form-row">
                        <label>入住人姓名</label>
                        <input type="text" name="guestName" required
                               value="${empty guestName ? sessionScope.user.realName : guestName}">
                    </div>
                    <div class="form-row">
                        <label>联系手机</label>
                        <input type="text" name="guestPhone" required pattern="1\d{10}" title="请输入11位手机号"
                               value="${empty guestPhone ? sessionScope.user.phone : guestPhone}">
                    </div>
                </div>
                <div class="form-grid2">
                    <div class="form-row">
                        <label>身份证号（选填）</label>
                        <input type="text" name="guestIdCard" value="${guestIdCard}">
                    </div>
                    <div class="form-row">
                        <label>入住人数</label>
                        <select name="guestCount">
                            <c:forEach var="i" begin="1" end="${type.capacity}">
                                <option value="${i}" ${guestCount == i ? 'selected' : ''}>${i} 人</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-row">
                    <label>备注（选填）</label>
                    <textarea name="remark" rows="2" placeholder="如需无烟房、高楼层等可在此说明">${remark}</textarea>
                </div>
                <button type="submit" class="btn btn-gold" style="width:100%;padding:12px;font-size:15px;">确认预订</button>
            </form>
        </div>

        <div>
            <div class="panel">
                <div style="display:flex;gap:14px;margin-bottom:14px;">
                    <img src="${ctx}/static/images/rooms/${type.image}" alt="${type.name}"
                         style="width:110px;height:80px;object-fit:cover;border-radius:8px;">
                    <div>
                        <h3 style="margin-bottom:2px;">${type.name}</h3>
                        <div class="meta" style="color:var(--muted);font-size:13px;">${type.bedType} · 可住${type.capacity}人</div>
                    </div>
                </div>
                <div class="booking-summary">
                    <div class="row"><span>入住</span><span>${checkin}</span></div>
                    <div class="row"><span>退房</span><span>${checkout}</span></div>
                    <div class="row"><span>房价</span><span>¥<fmt:formatNumber value="${type.price}" maxFractionDigits="0"/> × ${nights} 晚</span></div>
                    <div class="row total"><span>合计</span><b>¥<fmt:formatNumber value="${totalPrice}" maxFractionDigits="0"/></b></div>
                </div>
                <p style="font-size:13px;color:var(--muted);">提交后系统将自动为你分配一间该房型的空房，到店支付。</p>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/user-footer.jspf" %>
