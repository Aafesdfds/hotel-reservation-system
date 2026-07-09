<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="仪表盘"/>
<c:set var="active" value="dashboard"/>
<%@ include file="../common/admin-header.jspf" %>

<div class="stat-grid">
    <div class="stat-tile accent">
        <div class="label">当前在住</div>
        <div class="value">${m.inHouse}<span class="unit">间</span></div>
    </div>
    <div class="stat-tile">
        <div class="label">今日预抵（入住）</div>
        <div class="value">${m.todayCheckin}<span class="unit">单</span></div>
    </div>
    <div class="stat-tile">
        <div class="label">今日预离（退房）</div>
        <div class="value">${m.todayCheckout}<span class="unit">单</span></div>
    </div>
    <div class="stat-tile">
        <div class="label">今日实时入住率</div>
        <div class="value">${m.occupancyRate}<span class="unit">%</span></div>
    </div>
</div>

<div class="chart-panel" style="margin-bottom:18px;">
    <div class="section-title"><h3>房态看板</h3>
        <a href="${ctx}/admin/rooms" class="btn btn-outline btn-sm">房间管理</a>
    </div>
    <div class="board-grid">
        <c:forEach var="b" items="${board}">
            <div class="board-cell">
                <div class="n">${b[2]}</div>
                <div class="t"><span class="dot st-${b[0]=='AVAILABLE'?'available':b[0]}">${b[1]}</span></div>
            </div>
        </c:forEach>
    </div>
</div>

<div class="grid-2-3">
    <div class="chart-panel">
        <h3>月度营收与入住率（${m.totalRooms} 间房）</h3>
        <div id="trendChart" class="chart"></div>
    </div>
    <div class="chart-panel">
        <div class="section-title"><h3>最新订单</h3>
            <a href="${ctx}/admin/reservations" class="btn btn-outline btn-sm">全部</a>
        </div>
        <table class="table">
            <thead><tr><th>房型</th><th>入住人</th><th>入住日</th><th>状态</th></tr></thead>
            <tbody>
            <c:forEach var="o" items="${recentOrders}" end="6">
                <tr>
                    <td>${o.typeName}</td>
                    <td>${o.guestName}</td>
                    <td><fmt:formatDate value="${o.checkinDate}" pattern="MM-dd"/></td>
                    <td><span class="dot st-${o.status}">${o.statusText}</span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script>
    var labels = ${chartLabels};
    var revenue = ${chartRevenue};
    var occupancy = ${chartOccupancy};
    var chart = echarts.init(document.getElementById('trendChart'));
    chart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['营收(元)', '入住率(%)'], bottom: 0 },
        grid: { left: 60, right: 55, top: 20, bottom: 40 },
        xAxis: { type: 'category', data: labels },
        yAxis: [
            { type: 'value', name: '营收' },
            { type: 'value', name: '入住率', max: 100, axisLabel: { formatter: '{value}%' } }
        ],
        series: [
            { name: '营收(元)', type: 'bar', data: revenue, itemStyle: { color: '#123a3f' }, barWidth: '46%' },
            { name: '入住率(%)', type: 'line', yAxisIndex: 1, data: occupancy, smooth: true,
              itemStyle: { color: '#b0863c' }, lineStyle: { width: 3 } }
        ]
    });
    window.addEventListener('resize', function () { chart.resize(); });
</script>

<%@ include file="../common/admin-footer.jspf" %>
