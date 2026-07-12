<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="AI 经营分析"/>
<c:set var="active" value="analysis"/>
<%@ include file="../common/admin-header.jspf" %>

<div class="panel" style="margin-bottom:18px;">
    <div class="section-title">
        <h3>AI 经营分析</h3>
        <c:choose>
            <c:when test="${aiConfigured}"><span class="dot st-available">大模型已接入</span></c:when>
            <c:otherwise><span class="dot st-DIRTY">未配置密钥，将用本地分析</span></c:otherwise>
        </c:choose>
    </div>
    <p style="color:var(--muted);font-size:14px;">
        系统会把本酒店的月度入住率、营收、房型成交等真实数据整理后发给大模型（DeepSeek），
        生成经营分析和房价调整建议。点击下方按钮生成，调用大模型通常需要几秒钟。
    </p>
    <form action="${ctx}/admin/analysis" method="post" onsubmit="document.getElementById('genBtn').disabled=true;document.getElementById('genBtn').innerText='正在生成，请稍候…';">
        <button id="genBtn" class="btn btn-gold">生成经营分析</button>
    </form>
</div>

<c:if test="${not empty result}">
    <div class="grid-2-3">
        <div class="chart-panel">
            <div class="section-title"><h3>分析结果</h3></div>
            <div class="ai-output">${result.content}</div>
            <div class="ai-source">来源：${result.source}</div>
        </div>
        <div class="chart-panel">
            <h3>分析所用数据</h3>
            <div class="data-preview">${result.dataSummary}</div>
        </div>
    </div>
</c:if>

<%@ include file="../common/admin-footer.jspf" %>
