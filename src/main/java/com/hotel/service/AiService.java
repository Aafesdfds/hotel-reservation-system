package com.hotel.service;

import com.hotel.util.AiClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * AI 经营分析：把真实经营数据整理成文字，发给大模型生成分析和建议。
 * 如果没配密钥或接口调用失败，退回本地按数据规则生成的分析，保证页面永远有结果。
 */
public class AiService {

    private final StatService statService = new StatService();

    public AiResult analyze() {
        String dataSummary = buildDataSummary();
        String system = "你是一名资深的酒店经营分析顾问。请根据给出的经营数据，用简洁专业的中文做分析并给出可落地的建议，"
                + "不要空话套话，分点表述，控制在 400 字以内。";
        String user = "以下是本酒店 2026 年的经营数据：\n\n" + dataSummary
                + "\n\n请从三个方面分析：1) 入住率与营收趋势；2) 房型结构与热度；3) 存在的问题。"
                + "然后给出 3 到 5 条具体的经营优化和房价调整建议。";
        try {
            String text = AiClient.chat(system, user);
            return new AiResult(true, text, "DeepSeek 大模型", dataSummary);
        } catch (Exception e) {
            String local = localAnalysis();
            return new AiResult(false,
                    local,
                    "本地数据分析（未调用大模型：" + e.getMessage() + "）",
                    dataSummary);
        }
    }

    /** 把统计数据拼成给大模型看的文字 */
    private String buildDataSummary() {
        StringBuilder sb = new StringBuilder();
        int year = LocalDate.now().getYear();
        StatService.MonthlyChart chart = statService.monthlyChart(year);
        sb.append("一、月度经营（").append(year).append("年）\n");
        int last = chart.labels.size() - 1;
        for (int i = 0; i < chart.labels.size(); i++) {
            sb.append(chart.labels.get(i))
                    .append("：营收 ").append(chart.revenue.get(i)).append(" 元，")
                    .append("订单 ").append(chart.orders.get(i)).append(" 单，")
                    .append("入住率 ").append(chart.occupancy.get(i)).append("%")
                    .append(i == last ? "（本月尚未结束，数据不完整，仅供参考）" : "")
                    .append("\n");
        }
        sb.append("\n二、各房型累计表现\n");
        for (Object[] row : statService.typeDistribution()) {
            sb.append(row[0]).append("：成交 ").append(row[1]).append(" 单，营收 ").append(row[2]).append(" 元\n");
        }
        return sb.toString();
    }

    /** 大模型不可用时的本地兜底分析，同样基于真实数据 */
    private String localAnalysis() {
        StringBuilder sb = new StringBuilder();
        int year = LocalDate.now().getYear();
        StatService.MonthlyChart chart = statService.monthlyChart(year);

        // 找营收最高/最低月、平均入住率
        int n = chart.labels.size();
        double occSum = 0;
        for (int i = 0; i < n; i++) {
            occSum += chart.occupancy.get(i);
        }
        double avgOcc = n == 0 ? 0 : occSum / n;

        // 当前月（序列最后一个月）尚未结束，不参与"营收最高/最低月"对比，
        // 否则会把进行中的当月误判成营收最低月/淡季。
        int cmp = n > 1 ? n - 1 : n;
        int bestMonth = 0, worstMonth = 0;
        BigDecimal bestRev = BigDecimal.valueOf(-1), worstRev = null;
        for (int i = 0; i < cmp; i++) {
            BigDecimal r = chart.revenue.get(i);
            if (r.compareTo(bestRev) > 0) {
                bestRev = r;
                bestMonth = i;
            }
            if (worstRev == null || r.compareTo(worstRev) < 0) {
                worstRev = r;
                worstMonth = i;
            }
        }

        List<Object[]> types = statService.typeDistribution();
        String hotType = types.isEmpty() ? "无" : (String) types.get(0)[0];
        String coldType = types.isEmpty() ? "无" : (String) types.get(types.size() - 1)[0];

        sb.append("一、入住率与营收趋势\n");
        sb.append("今年 1 至今平均入住率约 ").append(String.format("%.1f", avgOcc)).append("%。");
        if (n > 0) {
            sb.append("其中 ").append(chart.labels.get(bestMonth)).append("营收最高（")
                    .append(bestRev).append(" 元），")
                    .append(chart.labels.get(worstMonth)).append("最低（").append(worstRev).append(" 元）。");
        }
        sb.append("整体呈现明显的淡旺季波动，节假日所在月份表现更好。\n\n");

        sb.append("二、房型结构与热度\n");
        sb.append("最热门房型是「").append(hotType).append("」，成交量领先；")
                .append("「").append(coldType).append("」成交较少，动销偏弱。")
                .append("中端标准房、大床房是营收主力，高端套房单价高但成交量有限。\n\n");

        sb.append("三、经营建议\n");
        sb.append("1. 淡季（如营收最低月）推出连住优惠、提前预订折扣，把闲置房间盘活；\n");
        sb.append("2. 旺季对热门房型适度上调房价，或搭配早餐、延迟退房等增值服务提高客单价；\n");
        sb.append("3. 动销弱的「").append(coldType).append("」可做打包套餐或会员专享价，提升曝光；\n");
        sb.append("4. 关注入住率偏低的月份，投放本地渠道广告、和企业客户签协议价稳定客源；\n");
        sb.append("5. 建立会员体系，用积分和复购优惠提高回头客比例。\n");
        return sb.toString();
    }

    public static class AiResult {
        private final boolean fromAi;
        private final String content;
        private final String source;
        private final String dataSummary;

        public AiResult(boolean fromAi, String content, String source, String dataSummary) {
            this.fromAi = fromAi;
            this.content = content;
            this.source = source;
            this.dataSummary = dataSummary;
        }

        public boolean isFromAi() { return fromAi; }
        public String getContent() { return content; }
        public String getSource() { return source; }
        public String getDataSummary() { return dataSummary; }
    }
}
