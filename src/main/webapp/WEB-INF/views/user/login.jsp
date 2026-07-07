<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="登录 · 云栖酒店"/>
<%@ include file="../common/user-header.jspf" %>

<div class="auth-wrap">
    <div class="panel">
        <h2>登录</h2>
        <div class="sub">欢迎回到云栖酒店</div>

        <c:if test="${not empty tip}"><div class="alert alert-info">${tip}</div></c:if>
        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

        <form action="${ctx}/auth" method="post">
            <input type="hidden" name="action" value="login">
            <div class="form-row">
                <label>用户名</label>
                <input type="text" name="username" value="${username}" required autofocus>
            </div>
            <div class="form-row">
                <label>密码</label>
                <input type="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-gold">登录</button>
        </form>

        <div class="auth-foot">还没有账号？<a href="${ctx}/auth?action=register">立即注册</a></div>
        <div class="alert alert-info" style="margin-top:18px;font-size:13px;">
            演示账号：管理员 admin / admin123　　普通用户 user / 123456
        </div>
    </div>
</div>

<%@ include file="../common/user-footer.jspf" %>
