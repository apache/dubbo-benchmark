package com.dubbo.dlt.trendGenerator;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dubbo.dlt.trendGenerator.entry.CallRecord;
import com.dubbo.dlt.trendGenerator.entry.TrendData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ProviderCallTrendGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ProviderCallTrendGenerator.class);

    private static final java.time.format.DateTimeFormatter SECOND_FORMAT = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final java.time.format.DateTimeFormatter MINUTE_FORMAT = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void generateCallTrendHtml(String rawEscapeJson, String htmlGeneratePath) throws IOException {
        String cleanJson = cleanEscapeJson(rawEscapeJson);
        logger.info("JSON转义清理完成，开始解析数据");
        List<CallRecord> callRecords = parseJsonToCallRecords(cleanJson);
        List<TrendData> secondTrendData = aggregateDynamicData(callRecords, "second");
        String secondDataJson = JSON.toJSONString(secondTrendData);
        List<TrendData> minuteTrendData = aggregateDynamicData(callRecords, "minute");
        String minuteDataJson = JSON.toJSONString(minuteTrendData);

        String htmlTemplate = getDynamicEchartsHtmlTemplate();
        String finalHtml = htmlTemplate
                .replace("{{SECOND_DATA}}", secondDataJson)
                .replace("{{MINUTE_DATA}}", minuteDataJson);

        writeHtmlToFile(finalHtml, htmlGeneratePath);
        logger.info("✅ 动态趋势图生成完成！路径：{}，共解析到【{}】个不同的接口方法", htmlGeneratePath, getUniqueMethodNames(callRecords).size());
    }

    private static List<TrendData> aggregateDynamicData(List<CallRecord> callRecords, String timeUnit) {
        Map<String, List<CallRecord>> timeGroupMap;
        if ("second".equalsIgnoreCase(timeUnit)) {
            timeGroupMap = callRecords.stream().collect(Collectors.groupingBy(r -> r.getStartTimeDt().format(SECOND_FORMAT)));
        } else {
            timeGroupMap = callRecords.stream().collect(Collectors.groupingBy(r -> r.getStartTimeDt().format(MINUTE_FORMAT)));
        }
        List<TrendData> trendDataList = new ArrayList<>();
        for (Map.Entry<String, List<CallRecord>> entry : timeGroupMap.entrySet()) {
            String time = entry.getKey();
            List<CallRecord> records = entry.getValue();

            TrendData trendData = new TrendData();
            trendData.setTime(time);
            trendData.setTotalCount(records.size());

            long successNum = records.stream().filter(CallRecord::isSuccess).count();
            double successRate = records.size() == 0 ? 0 : (successNum * 100.0) / records.size();
            trendData.setSuccessRate(successRate);

            Map<String, Integer> methodCountMap = records.stream()
                    .collect(Collectors.groupingBy(CallRecord::getMethodName, Collectors.summingInt(e -> 1)));
            trendData.setMethodCountMap(methodCountMap);

            trendDataList.add(trendData);
        }
        trendDataList.sort(Comparator.comparing(TrendData::getTime));
        return trendDataList;
    }

    private static Set<String> getUniqueMethodNames(List<CallRecord> callRecords) {
        return callRecords.stream().map(CallRecord::getMethodName).collect(Collectors.toSet());
    }

    public static String cleanEscapeJson(String escapeJson) {
        if (escapeJson == null || escapeJson.isEmpty()) {
            return "";
        }
        String cleanJson = escapeJson.replaceAll("\\\\", "").replaceAll("^\"|\"$", "");
        Object jsonObj = JSON.parse(cleanJson);
        return JSONObject.toJSONString(jsonObj);
    }


    public static List<CallRecord> parseJsonToCallRecords(String cleanJson) {
        List<CallRecord> records = new ArrayList<>();
        JSONObject root = JSON.parseObject(cleanJson);
        JSONObject providerObj = root.getJSONObject("TestService-provider");
        List<JSONObject> list = providerObj.getJSONArray("provideResultList").toJavaList(JSONObject.class);

        for (JSONObject item : list) {
            String methodName = item.getString("methodName");
            String startTime = item.getString("startTime");
            boolean success = item.getBooleanValue("success");
            records.add(new CallRecord(methodName, startTime, success));
        }
        return records;
    }

    public static void writeHtmlToFile(String htmlContent, String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        Files.write(file.toPath(), htmlContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String getDynamicEchartsHtmlTemplate() {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"zh-CN\">\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <title>接口调用趋势图（动态适配所有方法）</title>\n"
                + "    <script src=\"https://cdn.bootcdn.net/ajax/libs/echarts/5.4.3/echarts.min.js\"></script>\n"
                + "    <style>\n"
                + "        body { padding: 20px; }\n"
                + "        .chart-container { width: 100%; height: 700px; margin-top: 20px; }\n"
                + "        .btn-group { margin-bottom: 10px; }\n"
                + "        button { \n"
                + "            padding: 8px 20px; margin-right: 10px; \n"
                + "            border: none; background: #1677ff; color: #fff; border-radius: 4px; cursor: pointer;\n"
                + "        }\n"
                + "        button:hover { background: #0d5bbd; }\n"
                + "        button.active { background: #0d5bbd; border: 1px solid #004094; }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <div class=\"btn-group\">\n"
                + "        <button id=\"secondBtn\" class=\"active\" onclick=\"changeTimeUnit('second')\">按【秒】展示趋势</button>\n"
                + "        <button id=\"minuteBtn\" onclick=\"changeTimeUnit('minute')\">按【分钟】展示趋势</button>\n"
                + "    </div>\n"
                + "    <div class=\"chart-container\" id=\"callTrendChart\"></div>\n"
                + "\n"
                + "    <script>\n"
                + "        // 后端传递的动态数据\n"
                + "        const secondData = {{SECOND_DATA}};\n"
                + "        const minuteData = {{MINUTE_DATA}};\n"
                + "        \n"
                + "        // 当前展示维度\n"
                + "        let currentUnit = 'second';\n"
                + "        let currentData = secondData;\n"
                + "        \n"
                + "        // 初始化图表\n"
                + "        const chartDom = document.getElementById('callTrendChart');\n"
                + "        const myChart = echarts.init(chartDom);\n"
                + "        renderChart(currentUnit);\n"
                + "        \n"
                + "        // 切换维度方法\n"
                + "        function changeTimeUnit(unit) {\n"
                + "            currentUnit = unit;\n"
                + "            currentData = unit === 'second' ? secondData : minuteData;\n"
                + "            // 切换按钮样式\n"
                + "            document.getElementById('secondBtn').className = unit === 'second' ? 'active' : '';\n"
                + "            document.getElementById('minuteBtn').className = unit === 'minute' ? 'active' : '';\n"
                + "            // 重新渲染图表\n"
                + "            renderChart(unit);\n"
                + "        }\n"
                + "        \n"
                + "        // ✅ 核心方法：动态渲染图表（无任何硬编码，自动适配所有方法）\n"
                + "        function renderChart(unit) {\n"
                + "            if (currentData.length === 0) {\n"
                + "                myChart.setOption({title: {text: '暂无数据'}});\n"
                + "                return;\n"
                + "            }\n"
                + "            \n"
                + "            // 步骤1：提取【所有唯一的方法名】+【所有时间轴】，自动去重\n"
                + "            const allMethodNames = new Set();\n"
                + "            const allTimes = currentData.map(item => item.time);\n"
                + "            currentData.forEach(item => {\n"
                + "                Object.keys(item.methodCountMap).forEach(method => allMethodNames.add(method));\n"
                + "            });\n"
                + "            const methodList = Array.from(allMethodNames); // 转成数组\n"
                + "            \n"
                + "            // 步骤2：动态构造【每个方法】的折线数据\n"
                + "            const seriesData = [];\n"
                + "            // ① 先加【总调用量】折线\n"
                + "            seriesData.push({\n"
                + "                name: '总调用量',\n"
                + "                type: 'line',\n"
                + "                data: currentData.map(item => item.totalCount),\n"
                + "                smooth: true,\n"
                + "                lineWidth: 2,\n"
                + "                color: '#ff4d4f'\n"
                + "            });\n"
                + "            // ② 动态添加【每个接口方法】的折线，有多少个方法加多少条\n"
                + "            const colors = ['#1677ff', '#36cbcb', '#722ed1', '#fa8c16', '#52c41a', '#ffc53d'];\n"
                + "            methodList.forEach((method, index) => {\n"
                + "                seriesData.push({\n"
                + "                    name: method,\n"
                + "                    type: 'line',\n"
                + "                    data: currentData.map(item => item.methodCountMap[method] || 0), // 无数据则显示0\n"
                + "                    smooth: true,\n"
                + "                    lineWidth: 2,\n"
                + "                    color: colors[index % colors.length] // 循环取颜色\n"
                + "                });\n"
                + "            });\n"
                + "            \n"
                + "            // 步骤3：构造完整的图表配置项\n"
                + "            const option = {\n"
                + "                title: { text: '接口调用趋势统计（共'+methodList.length+'个接口方法）', left: 'center' },\n"
                + "                tooltip: {\n"
                + "                    trigger: 'axis',\n"
                + "                    formatter: function(params) {\n"
                + "                        let res = params[0].name;\n"
                + "                        const targetItem = currentData.find(item => item.time === params[0].name);\n"
                + "                        // 遍历展示每个指标\n"
                + "                        params.forEach(item => {\n"
                + "                            res += '<br/>' + item.seriesName + ': ' + item.value;\n"
                + "                        });\n"
                + "                        // 展示成功率，兜底防报错\n"
                + "                        const successRate = targetItem?.successRate || 0;\n"
                + "                        res += '<br/>成功率: ' + successRate.toFixed(2) + '%';\n"
                + "                        return res;\n"
                + "                    }\n"
                + "                },\n"
                + "                legend: { data: ['总调用量', ...methodList], top: 30 }, // 动态图例\n"
                + "                grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },\n"
                + "                xAxis: { type: 'category', data: allTimes, axisLabel: { rotate: 30 } },\n"
                + "                yAxis: { type: 'value', name: '调用量', min: 0 },\n"
                + "                series: seriesData\n"
                + "            };\n"
                + "            \n"
                + "            // 渲染图表\n"
                + "            myChart.setOption(option);\n"
                + "        }\n"
                + "        \n"
                + "        // 自适应窗口大小\n"
                + "        window.addEventListener('resize', () => myChart.resize());\n"
                + "    </script>\n"
                + "</body>\n"
                + "</html>";
    }

    public static void main(String[] args) throws IOException {
        String rawJson = "{\"TestService-provider\":{\"count\":200,\"provideResultList\":[{\"endTime\":\"2025-12-28 09:19:15.842\",\"methodName\":\"sayHello\",\"serviceName\":\"TestService-provider\",\"startTime\":\"2025-12-28 09:19:15.823\",\"success\":true},{\"endTime\":\"2025-12-28 09:19:15.97\",\"methodName\":\"sayHello2\",\"serviceName\":\"TestService-provider\",\"startTime\":\"2025-12-28 09:19:15.97\",\"success\":true},{\"endTime\":\"2025-12-28 09:19:16.214\",\"methodName\":\"sayHello3\",\"serviceName\":\"TestService-provider\",\"startTime\":\"2025-12-28 09:19:16.173\",\"success\":true}]}}";
        generateCallTrendHtml(rawJson, "./service_call_trend_dynamic.html");
    }
}