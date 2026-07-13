<%@ page contentType="application/msword;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:w="urn:schemas-microsoft-com:office:word">
<head>
    <meta charset="UTF-8">
    <title>云栖酒店经营分析报告</title>
    <style>
        body { font-family: "SimSun", "宋体", serif; font-size: 14px; color: #000; margin: 32px; }
        h1 { text-align: center; font-size: 22px; margin-bottom: 6px; }
        .meta { text-align: center; color: #555; font-size: 12px; margin-bottom: 20px; }
        h2 { font-size: 16px; border-bottom: 1px solid #999; padding-bottom: 4px; margin: 22px 0 12px; }
        .sub { font-weight: bold; margin: 12px 0 6px; }
        table { border-collapse: collapse; width: 100%; margin-bottom: 14px; }
        th, td { border: 1px solid #999; padding: 6px 10px; font-size: 13px; text-align: left; }
        th { background: #f0f0f0; }
        .body { line-height: 1.9; }
        .foot { margin-top: 28px; text-align: center; color: #666; font-size: 12px; }
        .foot .sign { text-align: right; margin-top: 16px; color: #333; }
    </style>
</head>
<body>
<h1>云栖酒店经营分析报告</h1>
<div class="meta">报告日期：${reportDate}　|　数据来源：${result.source}</div>

<h2>一、经营数据概况</h2>
<p class="sub">1. 月度经营（${result.year} 年）</p>
<table>
    <tr><th>月份</th><th>营收（元）</th><th>订单（单）</th><th>入住率</th></tr>
    <c:forEach var="row" items="${result.monthlyRows}">
        <tr>
            <td>${row[0]}<c:if test="${not empty row[4]}">（${row[4]}）</c:if></td>
            <td>${row[1]}</td>
            <td>${row[2]}</td>
            <td>${row[3]}</td>
        </tr>
    </c:forEach>
</table>

<p class="sub">2. 各房型累计表现</p>
<table>
    <tr><th>房型</th><th>成交（单）</th><th>营收（元）</th></tr>
    <c:forEach var="row" items="${result.typeDist}">
        <tr>
            <td>${row[0]}</td>
            <td>${row[1]}</td>
            <td>${row[2]}</td>
        </tr>
    </c:forEach>
</table>

<h2>二、分析与建议</h2>
<div class="body">${result.contentHtml}</div>

<div class="foot">
    <p>——— 本报告由云栖酒店管理系统根据经营数据生成 ———</p>
    <p class="sign">云栖酒店运营管理部　${reportDate}</p>
</div>
</body>
</html>
