<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>页面出错了</title>
    <link rel="stylesheet" href="${ctx}/static/css/style.css">
</head>
<body>
<div class="auth-wrap">
    <div class="panel" style="text-align:center;">
        <h2>页面走丢了</h2>
        <p class="sub">你访问的页面不存在，或系统开小差了，请返回首页重试。</p>
        <a href="${ctx}/home" class="btn btn-gold">返回首页</a>
    </div>
</div>
</body>
</html>
