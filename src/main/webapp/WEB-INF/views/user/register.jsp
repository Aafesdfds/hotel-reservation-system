<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="注册 · 云栖酒店"/>
<%@ include file="../common/user-header.jspf" %>

<div class="auth-wrap" style="max-width:460px;">
    <div class="panel">
        <h2>注册新账号</h2>
        <div class="sub">注册后即可在线预订房间</div>

        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

        <form action="${ctx}/auth" method="post">
            <input type="hidden" name="action" value="register">
            <div class="form-row">
                <label>用户名</label>
                <input type="text" name="username" value="${username}" required minlength="3" placeholder="至少 3 个字符">
            </div>
            <div class="form-grid2">
                <div class="form-row">
                    <label>密码</label>
                    <input type="password" name="password" required minlength="6" placeholder="至少 6 位">
                </div>
                <div class="form-row">
                    <label>确认密码</label>
                    <input type="password" name="confirm" required minlength="6">
                </div>
            </div>
            <div class="form-grid2">
                <div class="form-row">
                    <label>姓名</label>
                    <input type="text" name="realName" value="${realName}" required>
                </div>
                <div class="form-row">
                    <label>手机号</label>
                    <input type="text" name="phone" value="${phone}" pattern="1\d{10}" title="请输入11位手机号">
                </div>
            </div>
            <div class="form-row">
                <label>性别</label>
                <select name="gender">
                    <option value="男">男</option>
                    <option value="女">女</option>
                </select>
            </div>
            <button type="submit" class="btn btn-gold">注册并登录</button>
        </form>

        <div class="auth-foot">已有账号？<a href="${ctx}/auth?action=login">去登录</a></div>
    </div>
</div>

<%@ include file="../common/user-footer.jspf" %>
