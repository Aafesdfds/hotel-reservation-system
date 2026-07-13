<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="AI 经营分析"/>
<c:set var="active" value="analysis"/>
<%@ include file="../common/admin-header.jspf" %>

<div class="panel no-print" style="margin-bottom:18px;">
    <div class="section-title">
        <h3>AI 经营分析</h3>
        <c:choose>
            <c:when test="${aiConfigured}"><span class="dot st-available">大模型已接入</span></c:when>
            <c:otherwise><span class="dot st-DIRTY">未配置密钥，将用本地分析</span></c:otherwise>
        </c:choose>
    </div>
    <p style="color:var(--muted);font-size:14px;">
        系统会把本酒店的月度入住率、营收、房型成交等真实数据整理后发给大模型（DeepSeek），
        生成一份经营分析报告。点击下方按钮生成，调用大模型通常需要几秒钟。生成后可以下载成 Word 文档，或打印保存成 PDF。
    </p>
    <form action="${ctx}/admin/analysis" method="post" onsubmit="document.getElementById('genBtn').disabled=true;document.getElementById('genBtn').innerText='正在生成，请稍候…';">
        <button id="genBtn" class="btn btn-gold">${empty result ? '生成经营分析报告' : '重新生成报告'}</button>
    </form>
</div>

<c:if test="${not empty result}">
    <div class="report-actions no-print">
        <a href="${ctx}/admin/analysis?action=word" class="btn btn-gold">下载 Word 文档</a>
        <button type="button" class="btn btn-outline" onclick="window.print()">打印 / 导出 PDF</button>
    </div>

    <div class="report-doc">
        <div class="report-head">
            <h1>云栖酒店经营分析报告</h1>
            <div class="report-meta">报告日期：${reportDate}　|　数据来源：${result.source}</div>
        </div>

        <h2>一、经营数据概况</h2>
        <p class="report-sub">1. 月度经营（${result.year} 年）</p>
        <table class="report-table">
            <thead>
            <tr><th>月份</th><th>营收（元）</th><th>订单（单）</th><th>入住率</th></tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${result.monthlyRows}">
                <tr>
                    <td>${row[0]}<c:if test="${not empty row[4]}">（${row[4]}）</c:if></td>
                    <td>${row[1]}</td>
                    <td>${row[2]}</td>
                    <td>${row[3]}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <p class="report-sub">2. 各房型累计表现</p>
        <table class="report-table">
            <thead>
            <tr><th>房型</th><th>成交（单）</th><th>营收（元）</th></tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${result.typeDist}">
                <tr>
                    <td>${row[0]}</td>
                    <td>${row[1]}</td>
                    <td>${row[2]}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <h2>二、分析与建议</h2>
        <div class="report-body">${result.contentHtml}</div>

        <div class="report-foot">
            <p>——— 本报告由云栖酒店管理系统根据经营数据生成 ———</p>
            <p class="sign">云栖酒店运营管理部　${reportDate}</p>
        </div>
    </div>
</c:if>

<%@ include file="../common/admin-footer.jspf" %>
