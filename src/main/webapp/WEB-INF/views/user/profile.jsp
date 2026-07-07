<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="个人中心 · 云栖酒店"/>
<%@ include file="../common/user-header.jspf" %>
<c:set var="u" value="${sessionScope.user}"/>

<div class="container">
    <div class="page-head"><h1>个人中心</h1></div>

    <c:if test="${not empty msg}"><div class="alert alert-ok">${msg}</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

    <div class="grid-2" style="padding-bottom:44px;">
        <div class="panel">
            <h3>基本资料</h3>
            <form action="${ctx}/profile" method="post" style="margin-top:12px;">
                <input type="hidden" name="action" value="profile">
                <div class="form-row">
                    <label>用户名</label>
                    <input type="text" value="${u.username}" disabled>
                </div>
                <div class="form-row">
                    <label>姓名</label>
                    <input type="text" name="realName" value="${u.realName}">
                </div>
                <div class="form-grid2">
                    <div class="form-row">
                        <label>手机号</label>
                        <input type="text" name="phone" value="${u.phone}" pattern="1\d{10}" title="请输入11位手机号">
                    </div>
                    <div class="form-row">
                        <label>性别</label>
                        <select name="gender">
                            <option value="男" ${u.gender=='男'?'selected':''}>男</option>
                            <option value="女" ${u.gender=='女'?'selected':''}>女</option>
                        </select>
                    </div>
                </div>
                <button class="btn btn-gold">保存资料</button>
            </form>
        </div>

        <div class="panel">
            <h3>修改密码</h3>
            <form action="${ctx}/profile" method="post" style="margin-top:12px;">
                <input type="hidden" name="action" value="password">
                <div class="form-row">
                    <label>原密码</label>
                    <input type="password" name="oldPwd" required>
                </div>
                <div class="form-row">
                    <label>新密码</label>
                    <input type="password" name="newPwd" required minlength="6" placeholder="至少 6 位">
                </div>
                <button class="btn">修改密码</button>
            </form>
        </div>
    </div>
</div>

<%@ include file="../common/user-footer.jspf" %>
