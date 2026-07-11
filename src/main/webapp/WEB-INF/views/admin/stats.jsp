<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="数据统计"/>
<c:set var="active" value="stats"/>
<%@ include file="../common/admin-header.jspf" %>

<div class="grid-2">
    <div class="chart-panel"><h3>月度营收与入住率</h3><div id="c1" class="chart"></div></div>
    <div class="chart-panel"><h3>月度订单量</h3><div id="c2" class="chart"></div></div>
</div>
<div class="grid-2" style="margin-top:18px;">
    <div class="chart-panel"><h3>各房型成交分布</h3><div id="c3" class="chart"></div></div>
    <div class="chart-panel"><h3>订单状态分布</h3><div id="c4" class="chart"></div></div>
</div>

<script>
    var labels = ${chartLabels};
    var revenue = ${chartRevenue};
    var orders = ${chartOrders};
    var occupancy = ${chartOccupancy};
    var typeNames = ${typeNames};
    var typeCounts = ${typeCounts};
    var typeRevenue = ${typeRevenue};
    var statusData = ${statusData};

    var palette = ['#123a3f', '#b0863c', '#3f7d84', '#c99b4e', '#6b8b2f', '#2f5f9f', '#a9553b', '#7c8a8c', '#4a6b52', '#8a6d3b'];

    var c1 = echarts.init(document.getElementById('c1'));
    c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['营收(元)', '入住率(%)'], bottom: 0 },
        grid: { left: 62, right: 55, top: 20, bottom: 40 },
        xAxis: { type: 'category', data: labels },
        yAxis: [{ type: 'value', name: '营收' }, { type: 'value', name: '入住率', max: 100, axisLabel: { formatter: '{value}%' } }],
        series: [
            { name: '营收(元)', type: 'bar', data: revenue, itemStyle: { color: '#123a3f' }, barWidth: '46%' },
            { name: '入住率(%)', type: 'line', yAxisIndex: 1, data: occupancy, smooth: true, itemStyle: { color: '#b0863c' }, lineStyle: { width: 3 } }
        ]
    });

    var c2 = echarts.init(document.getElementById('c2'));
    c2.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 45, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: labels },
        yAxis: { type: 'value' },
        series: [{ name: '订单量', type: 'line', data: orders, smooth: true, areaStyle: { color: 'rgba(18,58,63,.12)' }, itemStyle: { color: '#123a3f' } }]
    });

    var c3 = echarts.init(document.getElementById('c3'));
    c3.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: 90, right: 30, top: 20, bottom: 30 },
        xAxis: { type: 'value' },
        yAxis: { type: 'category', data: typeNames.slice().reverse() },
        series: [{ name: '成交单数', type: 'bar', data: typeCounts.slice().reverse(), itemStyle: { color: '#b0863c' }, barWidth: '58%' }]
    });

    var c4 = echarts.init(document.getElementById('c4'));
    c4.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 0 },
        color: palette,
        series: [{ name: '订单状态', type: 'pie', radius: ['40%', '66%'], center: ['50%', '46%'],
            data: statusData, label: { formatter: '{b}\n{c}' } }]
    });

    window.addEventListener('resize', function () { c1.resize(); c2.resize(); c3.resize(); c4.resize(); });
</script>

<%@ include file="../common/admin-footer.jspf" %>
