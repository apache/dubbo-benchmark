package com.dubbo.dlt.trendGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ConsumerGeneratorHtml {
    private static final Logger log = LoggerFactory.getLogger(ConsumerGeneratorHtml.class);

    public static String generateHtmlPageCN(String testData) {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Dubbo 性能测试报告系统</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        body {\n" +
                "            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);\n" +
                "            min-height: 100vh;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .container {\n" +
                "            max-width: 1400px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        /* 顶部导航栏 */\n" +
                "        .top-nav {\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 15px 25px;\n" +
                "            margin-bottom: 25px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            align-items: center;\n" +
                "            flex-wrap: wrap;\n" +
                "            gap: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-title {\n" +
                "            font-size: 1.5rem;\n" +
                "            font-weight: 700;\n" +
                "            color: #1890ff;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-selector-container {\n" +
                "            flex: 1;\n" +
                "            max-width: 600px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-selector {\n" +
                "            display: flex;\n" +
                "            gap: 10px;\n" +
                "            align-items: center;\n" +
                "            flex-wrap: wrap;\n" +
                "        }\n" +
                "        \n" +
                "        .test-select-label {\n" +
                "            font-weight: 600;\n" +
                "            color: #555;\n" +
                "            white-space: nowrap;\n" +
                "        }\n" +
                "        \n" +
                "        .test-dropdown {\n" +
                "            flex: 1;\n" +
                "            min-width: 300px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-dropdown select {\n" +
                "            width: 100%;\n" +
                "            padding: 12px 15px;\n" +
                "            border: 2px solid #e8e8e8;\n" +
                "            border-radius: 8px;\n" +
                "            font-size: 1rem;\n" +
                "            color: #333;\n" +
                "            background: white;\n" +
                "            cursor: pointer;\n" +
                "            transition: border-color 0.3s;\n" +
                "            appearance: none;\n" +
                "            background-image: url(\"data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23333' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e\");\n" +
                "            background-repeat: no-repeat;\n" +
                "            background-position: right 15px center;\n" +
                "            background-size: 16px;\n" +
                "            padding-right: 45px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-dropdown select:focus {\n" +
                "            outline: none;\n" +
                "            border-color: #1890ff;\n" +
                "            box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);\n" +
                "        }\n" +
                "        \n" +
                "        .action-buttons {\n" +
                "            display: flex;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn {\n" +
                "            padding: 10px 20px;\n" +
                "            border: none;\n" +
                "            border-radius: 8px;\n" +
                "            cursor: pointer;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 0.95rem;\n" +
                "            transition: all 0.3s;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 8px;\n" +
                "            white-space: nowrap;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn-primary {\n" +
                "            background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn-success {\n" +
                "            background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn-warning {\n" +
                "            background: linear-gradient(135deg, #fa8c16 0%, #d46b08 100%);\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn:hover {\n" +
                "            transform: translateY(-2px);\n" +
                "            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);\n" +
                "        }\n" +
                "        \n" +
                "        /* 数据统计栏 */\n" +
                "        .stats-bar {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n" +
                "            gap: 15px;\n" +
                "            margin-bottom: 25px;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-item {\n" +
                "            background: white;\n" +
                "            border-radius: 10px;\n" +
                "            padding: 20px;\n" +
                "            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 15px;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-icon {\n" +
                "            width: 50px;\n" +
                "            height: 50px;\n" +
                "            border-radius: 10px;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            font-size: 1.5rem;\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-icon-total { background: linear-gradient(135deg, #722ed1 0%, #9254de 100%); }\n" +
                "        .stat-icon-success { background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%); }\n" +
                "        .stat-icon-avg { background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%); }\n" +
                "        .stat-icon-time { background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%); }\n" +
                "        \n" +
                "        .stat-content h4 {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #666;\n" +
                "            margin-bottom: 5px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-content .value {\n" +
                "            font-size: 1.5rem;\n" +
                "            font-weight: 700;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-content .unit {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #888;\n" +
                "            margin-left: 2px;\n" +
                "        }\n" +
                "        \n" +
                "        /* 原有样式保持不变 */\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 30px;\n" +
                "            padding: 20px;\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "        }\n" +
                "        \n" +
                "        .header h1 {\n" +
                "            color: #1890ff;\n" +
                "            font-size: 2.2rem;\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        \n" +
                "        .header .subtitle {\n" +
                "            color: #666;\n" +
                "            font-size: 1rem;\n" +
                "        }\n" +
                "        \n" +
                "        .timestamp {\n" +
                "            background: #f0f7ff;\n" +
                "            padding: 12px;\n" +
                "            border-radius: 8px;\n" +
                "            margin-top: 15px;\n" +
                "            display: inline-block;\n" +
                "            color: #1890ff;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .test-id {\n" +
                "            background: #f8f9fa;\n" +
                "            padding: 8px 15px;\n" +
                "            border-radius: 20px;\n" +
                "            font-family: monospace;\n" +
                "            color: #666;\n" +
                "            margin-top: 10px;\n" +
                "            display: inline-block;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        \n" +
                "        .dashboard {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));\n" +
                "            gap: 20px;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "        \n" +
                "        .card {\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 25px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "            transition: transform 0.3s ease, box-shadow 0.3s ease;\n" +
                "        }\n" +
                "        \n" +
                "        .card:hover {\n" +
                "            transform: translateY(-5px);\n" +
                "            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);\n" +
                "        }\n" +
                "        \n" +
                "        .card-header {\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            margin-bottom: 20px;\n" +
                "            padding-bottom: 15px;\n" +
                "            border-bottom: 1px solid #f0f0f0;\n" +
                "        }\n" +
                "        \n" +
                "        .card-icon {\n" +
                "            width: 50px;\n" +
                "            height: 50px;\n" +
                "            border-radius: 10px;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            margin-right: 15px;\n" +
                "            color: white;\n" +
                "            font-size: 1.5rem;\n" +
                "        }\n" +
                "        \n" +
                "        .icon-success { background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%); }\n" +
                "        .icon-performance { background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%); }\n" +
                "        .icon-requests { background: linear-gradient(135deg, #722ed1 0%, #9254de 100%); }\n" +
                "        .icon-time { background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%); }\n" +
                "        .icon-distribution { background: linear-gradient(135deg, #f5222d 0%, #ff4d4f 100%); }\n" +
                "        \n" +
                "        .card-title {\n" +
                "            font-size: 1.2rem;\n" +
                "            font-weight: 600;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .card-value {\n" +
                "            font-size: 2.2rem;\n" +
                "            font-weight: 700;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .success-rate .card-value { color: #52c41a; }\n" +
                "        .response-time .card-value { color: #1890ff; }\n" +
                "        .throughput .card-value { color: #722ed1; }\n" +
                "        .requests .card-value { color: #fa8c16; }\n" +
                "        \n" +
                "        .card-unit {\n" +
                "            font-size: 1rem;\n" +
                "            color: #888;\n" +
                "            margin-left: 5px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .card-footer {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #666;\n" +
                "            margin-top: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .highlight {\n" +
                "            color: #1890ff;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        \n" +
                "        .distribution-card {\n" +
                "            grid-column: span 2;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-distribution {\n" +
                "            margin-top: 15px;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-item {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            align-items: center;\n" +
                "            padding: 12px 0;\n" +
                "            border-bottom: 1px solid #f0f0f0;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-item:last-child {\n" +
                "            border-bottom: none;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-name {\n" +
                "            font-weight: 500;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-percentage {\n" +
                "            font-weight: 700;\n" +
                "            color: #1890ff;\n" +
                "        }\n" +
                "        \n" +
                "        .progress-bar {\n" +
                "            height: 10px;\n" +
                "            background: #f0f0f0;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 5px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        \n" +
                "        .progress-fill {\n" +
                "            height: 100%;\n" +
                "            background: linear-gradient(90deg, #1890ff 0%, #40a9ff 100%);\n" +
                "            border-radius: 5px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-section {\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 30px;\n" +
                "            margin-top: 30px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "        }\n" +
                "        \n" +
                "        .summary-title {\n" +
                "            font-size: 1.5rem;\n" +
                "            font-weight: 600;\n" +
                "            margin-bottom: 20px;\n" +
                "            color: #333;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-title i {\n" +
                "            margin-right: 10px;\n" +
                "            color: #1890ff;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-grid {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));\n" +
                "            gap: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-item {\n" +
                "            padding: 15px;\n" +
                "            background: #f8f9fa;\n" +
                "            border-radius: 8px;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-label {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #666;\n" +
                "            margin-bottom: 5px;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-value {\n" +
                "            font-size: 1.2rem;\n" +
                "            font-weight: 600;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .status-badge {\n" +
                "            display: inline-block;\n" +
                "            padding: 5px 12px;\n" +
                "            border-radius: 20px;\n" +
                "            font-size: 0.8rem;\n" +
                "            font-weight: 600;\n" +
                "            margin-left: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .status-success {\n" +
                "            background: #f6ffed;\n" +
                "            color: #52c41a;\n" +
                "            border: 1px solid #b7eb8f;\n" +
                "        }\n" +
                "        \n" +
                "        .status-warning {\n" +
                "            background: #fff7e6;\n" +
                "            color: #fa8c16;\n" +
                "            border: 1px solid #ffd591;\n" +
                "        }\n" +
                "        \n" +
                "        @media (max-width: 768px) {\n" +
                "            .top-nav {\n" +
                "                flex-direction: column;\n" +
                "                align-items: stretch;\n" +
                "            }\n" +
                "            \n" +
                "            .test-selector-container {\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            \n" +
                "            .action-buttons {\n" +
                "                justify-content: center;\n" +
                "            }\n" +
                "            \n" +
                "            .dashboard {\n" +
                "                grid-template-columns: 1fr;\n" +
                "            }\n" +
                "            \n" +
                "            .distribution-card {\n" +
                "                grid-column: span 1;\n" +
                "            }\n" +
                "            \n" +
                "            .header h1 {\n" +
                "                font-size: 1.8rem;\n" +
                "            }\n" +
                "            \n" +
                "            .card-value {\n" +
                "                font-size: 1.8rem;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <!-- 顶部导航栏 -->\n" +
                "        <div class=\"top-nav\">\n" +
                "            <div class=\"nav-title\">\n" +
                "                <i class=\"fas fa-chart-line\"></i>\n" +
                "                Dubbo 性能测试报告系统\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"test-selector-container\">\n" +
                "                <div class=\"test-selector\">\n" +
                "                    <span class=\"test-select-label\">\n" +
                "                        <i class=\"fas fa-list\"></i> 选择测试报告:\n" +
                "                    </span>\n" +
                "                    <div class=\"test-dropdown\">\n" +
                "                        <select id=\"testSelect\">\n" +
                "                            <option value=\"\">请选择测试报告...</option>\n" +
                "                        </select>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"action-buttons\">\n" +
                "                <button class=\"nav-btn nav-btn-primary\" id=\"refreshBtn\">\n" +
                "                    <i class=\"fas fa-sync-alt\"></i> 刷新\n" +
                "                </button>\n" +
                "                <button class=\"nav-btn nav-btn-success\" id=\"historyBtn\">\n" +
                "                    <i class=\"fas fa-history\"></i> 历史记录\n" +
                "                </button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- 数据统计栏 -->\n" +
                "        <div class=\"stats-bar\" id=\"statsBar\">\n" +
                "            <!-- 动态生成统计数据 -->\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- 主报告内容区域 -->\n" +
                "        <div id=\"reportContent\">\n" +
                "            <!-- 动态加载的报告内容 -->\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- 页脚 -->\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2025 Dubbo 性能监控系统 | 最后更新: <span id=\"lastUpdateTime\"></span></p>\n" +
                "            <p>数据仅供参考，测试结果可能受网络环境和系统负载影响</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <!-- 模板：报告内容 -->\n" +
                "    <template id=\"reportTemplate\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1><i class=\"fas fa-chart-line\"></i> Dubbo 性能测试报告</h1>\n" +
                "            <p class=\"subtitle\">接口性能监控与分析结果</p>\n" +
                "            \n" +
                "            <div class=\"timestamp\">\n" +
                "                <i class=\"far fa-clock\"></i> 测试时间: <span id=\"reportTimeRange\"></span>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"test-id\">\n" +
                "                <i class=\"fas fa-fingerprint\"></i> 消费者ID: <span id=\"reportTestId\"></span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"dashboard\">\n" +
                "            <div class=\"card success-rate\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-success\">\n" +
                "                        <i class=\"fas fa-check-circle\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">成功率</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"successRateValue\">100.0<span class=\"card-unit\">%</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    <span class=\"highlight\" id=\"failedRequests\">0</span> 次失败请求 / \n" +
                "                    <span class=\"highlight\" id=\"totalRequests\">100</span> 次总请求\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card response-time\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-performance\">\n" +
                "                        <i class=\"fas fa-tachometer-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">平均响应时间</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"avgResponseTime\">3.23<span class=\"card-unit\">秒</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    测试持续 <span class=\"highlight\" id=\"testDuration\">40.76</span> 秒\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card throughput\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-time\">\n" +
                "                        <i class=\"fas fa-bolt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">吞吐量</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"throughputValue\">2<span class=\"card-unit\">次/秒</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    峰值并发: <span class=\"highlight\" id=\"peakConcurrent\">1</span> 次\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card requests\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-requests\">\n" +
                "                        <i class=\"fas fa-exchange-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">请求统计</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"requestsValue\">100<span class=\"card-unit\">次</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    成功请求: <span class=\"highlight\" id=\"successfulRequests\">100</span> 次\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card distribution-card\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-distribution\">\n" +
                "                        <i class=\"fas fa-server\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">提供者分布</div>\n" +
                "                </div>\n" +
                "                <div class=\"provider-distribution\" id=\"providerDistribution\">\n" +
                "                    <!-- 动态生成提供者分布 -->\n" +
                "                </div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    消费者ID: <span class=\"highlight\" id=\"consumerId\">nullsayHello2</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"summary-section\">\n" +
                "            <div class=\"summary-title\">\n" +
                "                <i class=\"fas fa-info-circle\"></i> 测试摘要\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"summary-grid\">\n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">测试持续时间</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryDuration\">40.76 秒</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">总请求数</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryTotalRequests\">100 次</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">成功请求数</div>\n" +
                "                    <div class=\"summary-value\" id=\"summarySuccessfulRequests\">\n" +
                "                        100 次 <span class=\"status-badge status-success\">全部成功</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">失败请求数</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryFailedRequests\">0 次</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">峰值并发数</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryPeakConcurrent\">1 次</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">消费者ID</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryConsumerId\">nullsayHello2</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">消费者唯一标识</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryTestId\">TEST-1766925301400-74sayHello2</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">测试状态</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryStatus\">\n" +
                "                        已完成 <span class=\"status-badge status-success\">成功</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </template>\n" +
                "\n" +
                "    <script>\n" +
                "        // 从占位符获取测试数据\n" +
                "        const testDataJson = '${TEST_DATA}';\n" +
                "        let testReports = {};\n" +
                "        \n" +
                "        try {\n" +
                "            const dataArray = JSON.parse(testDataJson);\n" +
                "            // 如果是数组，转换为对象格式\n" +
                "            if (Array.isArray(dataArray)) {\n" +
                "                dataArray.forEach(report => {\n" +
                "                    const uniqueKey = report.consumerId;\n" +
                "                    testReports[uniqueKey] = report;\n" +
                "                    report.fileName = report.consumerId;\n" +
                "                });\n" +
                "            } else {\n" +
                "                // 单个对象 - 修正：改用consumerId作为key\n" +
                "                testReports[dataArray.consumerId] = dataArray;\n" +
                "            }\n" +
                "        } catch (error) {\n" +
                "            console.error('解析测试数据失败:', error);\n" +
                "            testReports = {};\n" +
                "        }\n" +
                "        \n" +
                "        // 修正：变量名改为currentConsumerId，语义统一，移除testId\n" +
                "        let currentConsumerId = Object.keys(testReports)[0] || '';\n" +
                "        \n" +
                "        // 初始化\n" +
                "        document.addEventListener('DOMContentLoaded', function() {\n" +
                "            // 初始化下拉框\n" +
                "            populateTestSelect();\n" +
                "            \n" +
                "            // 加载默认测试报告\n" +
                "            if (currentConsumerId) {\n" +
                "                loadTestReport(currentConsumerId);\n" +
                "            } else {\n" +
                "                showEmptyState();\n" +
                "            }\n" +
                "            \n" +
                "            // 更新统计数据\n" +
                "            updateStatsBar();\n" +
                "            \n" +
                "            // 绑定事件\n" +
                "            document.getElementById('testSelect').addEventListener('change', function() {\n" +
                "                if (this.value) {\n" +
                "                    currentConsumerId = this.value;\n" +
                "                    loadTestReport(currentConsumerId);\n" +
                "                }\n" +
                "            });\n" +
                "            \n" +
                "            document.getElementById('refreshBtn').addEventListener('click', function() {\n" +
                "                if (currentConsumerId) {\n" +
                "                    loadTestReport(currentConsumerId);\n" +
                "                    showNotification('报告已刷新', 'success');\n" +
                "                }\n" +
                "            });\n" +
                "            \n" +
                "            // 更新最后更新时间\n" +
                "            updateLastUpdateTime();\n" +
                "            setInterval(updateLastUpdateTime, 60000);\n" +
                "        });\n" +
                "        \n" +
                "        // 填充测试选择下拉框\n" +
                "        function populateTestSelect() {\n" +
                "            const select = document.getElementById('testSelect');\n" +
                "            select.innerHTML = '<option value=\"\">请选择测试报告...</option>';\n" +
                "            \n" +
                "            // 修正：变量名改为consumerId，移除testId\n" +
                "            Object.keys(testReports).forEach(consumerId => {\n" +
                "                const report = testReports[consumerId];\n" +
                "                const option = document.createElement('option');\n" +
                "                option.value = consumerId;\n" +
                "                \n" +
                "                // 显示格式: 文件名 + 测试时间\n" +
                "                const displayName = report.fileName ? \n" +
                "                    `${report.fileName} - ${report.startTime.substring(11, 19)}` : \n" +
                "                    `${report.consumerId} - ${report.startTime.substring(11, 19)}`;\n" +
                "                \n" +
                "                option.textContent = displayName;\n" +
                "                option.selected = consumerId === currentConsumerId;\n" +
                "                select.appendChild(option);\n" +
                "            });\n" +
                "        }\n" +
                "        \n" +
                "        // 修正：方法名+入参改为consumerId，移除testId\n" +
                "        function loadTestReport(consumerId) {\n" +
                "            const report = testReports[consumerId];\n" +
                "            if (!report) {\n" +
                "                showNotification('测试报告不存在', 'error');\n" +
                "                return;\n" +
                "            }\n" +
                "            \n" +
                "            // 克隆模板\n" +
                "            const template = document.getElementById('reportTemplate');\n" +
                "            const reportContent = document.getElementById('reportContent');\n" +
                "            reportContent.innerHTML = '';\n" +
                "            const clone = document.importNode(template.content, true);\n" +
                "            reportContent.appendChild(clone);\n" +
                "            \n" +
                "            // 填充数据\n" +
                "            document.getElementById('reportTimeRange').textContent = \n" +
                "                `${report.startTime} - ${report.endTime.substring(11)}`;\n" +
                "            document.getElementById('reportTestId').textContent = report.consumerId;\n" +
                "            \n" +
                "            document.getElementById('successRateValue').innerHTML = \n" +
                "                `${report.successRate.toFixed(1)}<span class=\"card-unit\">%</span>`;\n" +
                "            document.getElementById('failedRequests').textContent = report.failedRequests;\n" +
                "            document.getElementById('totalRequests').textContent = report.totalRequests;\n" +
                "            \n" +
                "            // 处理响应时间（可能包含ms或s）\n" +
                "            const responseTime = formatResponseTime(report.avgResponseTime);\n" +
                "            document.getElementById('avgResponseTime').innerHTML = \n" +
                "                `${responseTime.value}<span class=\"card-unit\">${responseTime.unit}</span>`;\n" +
                "            document.getElementById('testDuration').textContent = report.testDurationSeconds.toFixed(2);\n" +
                "            \n" +
                "            // 修正BUG2：吞吐量拼接单位\n" +
                "            document.getElementById('throughputValue').innerHTML = `${report.throughput}<span class=\"card-unit\">次/秒</span>`;\n" +
                "            document.getElementById('peakConcurrent').textContent = report.peakConcurrent;\n" +
                "            \n" +
                "            document.getElementById('requestsValue').innerHTML = \n" +
                "                `${report.totalRequests}<span class=\"card-unit\">次</span>`;\n" +
                "            document.getElementById('successfulRequests').textContent = report.successfulRequests;\n" +
                "            \n" +
                "            document.getElementById('consumerId').textContent = report.consumerId;\n" +
                "            \n" +
                "            // 填充提供者分布\n" +
                "            const providerDistribution = document.getElementById('providerDistribution');\n" +
                "            providerDistribution.innerHTML = '';\n" +
                "            Object.entries(report.providerDistribution).forEach(([provider, percentage]) => {\n" +
                "                const providerItem = document.createElement('div');\n" +
                "                providerItem.className = 'provider-item';\n" +
                "                providerItem.innerHTML = `\n" +
                "                    <div class=\"provider-name\">${provider}</div>\n" +
                "                    <div class=\"provider-percentage\">${percentage}%</div>\n" +
                "                `;\n" +
                "                const progressBar = document.createElement('div');\n" +
                "                progressBar.className = 'progress-bar';\n" +
                "                progressBar.innerHTML = `<div class=\"progress-fill\" style=\"width: ${percentage}%\"></div>`;\n" +
                "                providerDistribution.appendChild(providerItem);\n" +
                "                providerDistribution.appendChild(progressBar);\n" +
                "            });\n" +
                "            \n" +
                "            // 填充摘要数据\n" +
                "            document.getElementById('summaryDuration').textContent = `${report.testDurationSeconds.toFixed(2)} 秒`;\n" +
                "            document.getElementById('summaryTotalRequests').textContent = `${report.totalRequests} 次`;\n" +
                "            document.getElementById('summarySuccessfulRequests').innerHTML = \n" +
                "                `${report.successfulRequests} 次 ${getSuccessBadge(report.successRate)}`;\n" +
                "            document.getElementById('summaryFailedRequests').textContent = `${report.failedRequests} 次`;\n" +
                "            document.getElementById('summaryPeakConcurrent').textContent = report.peakConcurrent;\n" +
                "            document.getElementById('summaryConsumerId').textContent = report.consumerId;\n" +
                "            document.getElementById('summaryTestId').textContent = report.consumerId;\n" +
                "            document.getElementById('summaryStatus').innerHTML = \n" +
                "                `已完成 ${getStatusBadge(report.successRate)}`;\n" +
                "            \n" +
                "            // 添加卡片动画\n" +
                "            setTimeout(() => {\n" +
                "                const cards = document.querySelectorAll('.card');\n" +
                "                cards.forEach((card, index) => {\n" +
                "                    card.style.opacity = '0';\n" +
                "                    card.style.transform = 'translateY(20px)';\n" +
                "                    card.style.transition = 'all 0.5s ease';\n" +
                "                    \n" +
                "                    setTimeout(() => {\n" +
                "                        card.style.opacity = '1';\n" +
                "                        card.style.transform = 'translateY(0)';\n" +
                "                    }, index * 100);\n" +
                "                });\n" +
                "            }, 100);\n" +
                "        }\n" +
                "        \n" +
                "        // 格式化响应时间\n" +
                "        function formatResponseTime(responseTimeStr) {\n" +
                "            if (!responseTimeStr) return { value: '0', unit: 'ms' };\n" +
                "            \n" +
                "            // 移除非数字字符\n" +
                "            const numStr = responseTimeStr.replace(/[^0-9.]/g, '');\n" +
                "            const value = parseFloat(numStr) || 0;\n" +
                "            \n" +
                "            // 判断单位\n" +
                "            if (responseTimeStr.includes('ms')) {\n" +
                "                return { value: value.toFixed(2), unit: 'ms' };\n" +
                "            } else if (responseTimeStr.includes('s')) {\n" +
                "                return { value: value.toFixed(2), unit: 's' };\n" +
                "            } else {\n" +
                "                // 默认毫秒\n" +
                "                return { value: value.toFixed(2), unit: 'ms' };\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // 获取成功率徽章\n" +
                "        function getSuccessBadge(successRate) {\n" +
                "            if (successRate >= 95) {\n" +
                "                return '<span class=\"status-badge status-success\">优秀</span>';\n" +
                "            } else if (successRate >= 80) {\n" +
                "                return '<span class=\"status-badge status-warning\">良好</span>';\n" +
                "            } else {\n" +
                "                return '<span class=\"status-badge\" style=\"background:#fff2f0;color:#f5222d;border:1px solid #ffccc7\">需改进</span>';\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // 获取状态徽章\n" +
                "        function getStatusBadge(successRate) {\n" +
                "            if (successRate >= 95) {\n" +
                "                return '<span class=\"status-badge status-success\">成功</span>';\n" +
                "            } else if (successRate >= 80) {\n" +
                "                return '<span class=\"status-badge status-warning\">警告</span>';\n" +
                "            } else {\n" +
                "                return '<span class=\"status-badge\" style=\"background:#fff2f0;color:#f5222d;border:1px solid #ffccc7\">失败</span>';\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // 更新统计数据栏\n" +
                "        function updateStatsBar() {\n" +
                "            const statsBar = document.getElementById('statsBar');\n" +
                "            const reports = Object.values(testReports);\n" +
                "            \n" +
                "            if (reports.length === 0) {\n" +
                "                statsBar.innerHTML = `\n" +
                "                    <div style=\"grid-column:1/-1;text-align:center;padding:20px;color:#999;\">\n" +
                "                        <i class=\"fas fa-inbox\" style=\"font-size:2rem;margin-bottom:10px;\"></i>\n" +
                "                        <p>暂无测试数据</p>\n" +
                "                    </div>\n" +
                "                `;\n" +
                "                return;\n" +
                "            }\n" +
                "            \n" +
                "            // 计算统计值\n" +
                "            const totalTests = reports.length;\n" +
                "            const totalRequests = reports.reduce((sum, r) => sum + r.totalRequests, 0);\n" +
                "            const avgSuccessRate = reports.reduce((sum, r) => sum + r.successRate, 0) / totalTests;\n" +
                "            \n" +
                "            // 计算平均响应时间（转换为毫秒）\n" +
                "            const totalResponseTime = reports.reduce((sum, r) => {\n" +
                "                const timeStr = r.avgResponseTime || '0ms';\n" +
                "                const numStr = timeStr.replace(/[^0-9.]/g, '');\n" +
                "                let value = parseFloat(numStr) || 0;\n" +
                "                \n" +
                "                // 如果是秒，转换为毫秒\n" +
                "                if (timeStr.includes('s') && !timeStr.includes('ms')) {\n" +
                "                    value = value * 1000;\n" +
                "                }\n" +
                "                return sum + value;\n" +
                "            }, 0);\n" +
                "            const avgResponseTimeMs = totalResponseTime / totalTests;\n" +
                "            \n" +
                "            statsBar.innerHTML = `\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-total\">\n" +
                "                        <i class=\"fas fa-file-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>总测试次数</h4>\n" +
                "                        <div class=\"value\">${totalTests}<span class=\"unit\">次</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-success\">\n" +
                "                        <i class=\"fas fa-check-circle\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>平均成功率</h4>\n" +
                "                        <div class=\"value\">${avgSuccessRate.toFixed(1)}<span class=\"unit\">%</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-avg\">\n" +
                "                        <i class=\"fas fa-tachometer-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>平均响应时间</h4>\n" +
                "                        <div class=\"value\">${avgResponseTimeMs.toFixed(2)}<span class=\"unit\">ms</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-time\">\n" +
                "                        <i class=\"fas fa-clock\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>总请求数</h4>\n" +
                "                        <div class=\"value\">${totalRequests}<span class=\"unit\">次</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            `;\n" +
                "        }\n" +
                "        \n" +
                "        // 显示空状态\n" +
                "        function showEmptyState() {\n" +
                "            const reportContent = document.getElementById('reportContent');\n" +
                "            reportContent.innerHTML = `\n" +
                "                <div style=\"text-align:center;padding:60px;background:white;border-radius:12px;\">\n" +
                "                    <i class=\"fas fa-chart-bar\" style=\"font-size:4rem;color:#ddd;margin-bottom:20px;\"></i>\n" +
                "                    <h3 style=\"color:#666;margin-bottom:10px;\">暂无测试数据</h3>\n" +
                "                    <p style=\"color:#999;\">等待测试数据传入...</p>\n" +
                "                </div>\n" +
                "            `;\n" +
                "        }\n" +
                "        \n" +
                "        // 更新最后更新时间\n" +
                "        function updateLastUpdateTime() {\n" +
                "            const now = new Date();\n" +
                "            const timeString = now.toLocaleString('zh-CN');\n" +
                "            document.getElementById('lastUpdateTime').textContent = timeString;\n" +
                "        }\n" +
                "        \n" +
                "        // 显示通知\n" +
                "        function showNotification(message, type = 'info') {\n" +
                "            const notification = document.createElement('div');\n" +
                "            notification.style.cssText = `\n" +
                "                position: fixed;\n" +
                "                top: 20px;\n" +
                "                right: 20px;\n" +
                "                padding: 15px 25px;\n" +
                "                border-radius: 8px;\n" +
                "                color: white;\n" +
                "                font-weight: 600;\n" +
                "                box-shadow: 0 4px 12px rgba(0,0,0,0.15);\n" +
                "                z-index: 1000;\n" +
                "                animation: slideIn 0.3s ease;\n" +
                "                max-width: 400px;\n" +
                "            `;\n" +
                "            \n" +
                "            const bgColors = {\n" +
                "                success: 'linear-gradient(135deg, #52c41a 0%, #389e0d 100%)',\n" +
                "                error: 'linear-gradient(135deg, #f5222d 0%, #cf1322 100%)',\n" +
                "                warning: 'linear-gradient(135deg, #fa8c16 0%, #d46b08 100%)',\n" +
                "                info: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)'\n" +
                "            };\n" +
                "            \n" +
                "            notification.style.background = bgColors[type] || bgColors.info;\n" +
                "            \n" +
                "            const icons = {\n" +
                "                success: '✓',\n" +
                "                error: '✗',\n" +
                "                warning: '⚠',\n" +
                "                info: 'ℹ'\n" +
                "            };\n" +
                "            \n" +
                "            notification.innerHTML = `\n" +
                "                <span style=\"margin-right:10px;font-size:1.2em;\">${icons[type] || 'ℹ'}</span>\n" +
                "                ${message}\n" +
                "            `;\n" +
                "            \n" +
                "            document.body.appendChild(notification);\n" +
                "            \n" +
                "            setTimeout(() => {\n" +
                "                notification.style.animation = 'slideOut 0.3s ease';\n" +
                "                setTimeout(() => notification.remove(), 300);\n" +
                "            }, 3000);\n" +
                "            \n" +
                "            if (!document.querySelector('#notification-styles')) {\n" +
                "                const style = document.createElement('style');\n" +
                "                style.id = 'notification-styles';\n" +
                "                style.textContent = `\n" +
                "                    @keyframes slideIn {\n" +
                "                        from { transform: translateX(100%); opacity: 0; }\n" +
                "                        to { transform: translateX(0); opacity: 1; }\n" +
                "                    }\n" +
                "                    @keyframes slideOut {\n" +
                "                        from { transform: translateX(0); opacity: 1; }\n" +
                "                        to { transform: translateX(100%); opacity: 0; }\n" +
                "                    }\n" +
                "                `;\n" +
                "                document.head.appendChild(style);\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        html = html.replace("${TEST_DATA}",testData);

        return html;
    }
    public static String generateHtmlPageEN(String testData) {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Dubbo Performance Test Report System</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "            font-family: 'Segoe UI', 'Arial', sans-serif;\n" +
                "        }\n" +
                "        \n" +
                "        body {\n" +
                "            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);\n" +
                "            min-height: 100vh;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .container {\n" +
                "            max-width: 1400px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        /* Top Navigation Bar */\n" +
                "        .top-nav {\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 15px 25px;\n" +
                "            margin-bottom: 25px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            align-items: center;\n" +
                "            flex-wrap: wrap;\n" +
                "            gap: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-title {\n" +
                "            font-size: 1.5rem;\n" +
                "            font-weight: 700;\n" +
                "            color: #1890ff;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-selector-container {\n" +
                "            flex: 1;\n" +
                "            max-width: 600px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-selector {\n" +
                "            display: flex;\n" +
                "            gap: 10px;\n" +
                "            align-items: center;\n" +
                "            flex-wrap: wrap;\n" +
                "        }\n" +
                "        \n" +
                "        .test-select-label {\n" +
                "            font-weight: 600;\n" +
                "            color: #555;\n" +
                "            white-space: nowrap;\n" +
                "        }\n" +
                "        \n" +
                "        .test-dropdown {\n" +
                "            flex: 1;\n" +
                "            min-width: 300px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-dropdown select {\n" +
                "            width: 100%;\n" +
                "            padding: 12px 15px;\n" +
                "            border: 2px solid #e8e8e8;\n" +
                "            border-radius: 8px;\n" +
                "            font-size: 1rem;\n" +
                "            color: #333;\n" +
                "            background: white;\n" +
                "            cursor: pointer;\n" +
                "            transition: border-color 0.3s;\n" +
                "            appearance: none;\n" +
                "            background-image: url(\"data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23333' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e\");\n" +
                "            background-repeat: no-repeat;\n" +
                "            background-position: right 15px center;\n" +
                "            background-size: 16px;\n" +
                "            padding-right: 45px;\n" +
                "        }\n" +
                "        \n" +
                "        .test-dropdown select:focus {\n" +
                "            outline: none;\n" +
                "            border-color: #1890ff;\n" +
                "            box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);\n" +
                "        }\n" +
                "        \n" +
                "        .action-buttons {\n" +
                "            display: flex;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn {\n" +
                "            padding: 10px 20px;\n" +
                "            border: none;\n" +
                "            border-radius: 8px;\n" +
                "            cursor: pointer;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 0.95rem;\n" +
                "            transition: all 0.3s;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 8px;\n" +
                "            white-space: nowrap;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn-primary {\n" +
                "            background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn-success {\n" +
                "            background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn-warning {\n" +
                "            background: linear-gradient(135deg, #fa8c16 0%, #d46b08 100%);\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .nav-btn:hover {\n" +
                "            transform: translateY(-2px);\n" +
                "            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);\n" +
                "        }\n" +
                "        \n" +
                "        /* Data Statistics Bar */\n" +
                "        .stats-bar {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n" +
                "            gap: 15px;\n" +
                "            margin-bottom: 25px;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-item {\n" +
                "            background: white;\n" +
                "            border-radius: 10px;\n" +
                "            padding: 20px;\n" +
                "            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            gap: 15px;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-icon {\n" +
                "            width: 50px;\n" +
                "            height: 50px;\n" +
                "            border-radius: 10px;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            font-size: 1.5rem;\n" +
                "            color: white;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-icon-total { background: linear-gradient(135deg, #722ed1 0%, #9254de 100%); }\n" +
                "        .stat-icon-success { background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%); }\n" +
                "        .stat-icon-avg { background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%); }\n" +
                "        .stat-icon-time { background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%); }\n" +
                "        \n" +
                "        .stat-content h4 {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #666;\n" +
                "            margin-bottom: 5px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-content .value {\n" +
                "            font-size: 1.5rem;\n" +
                "            font-weight: 700;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .stat-content .unit {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #888;\n" +
                "            margin-left: 2px;\n" +
                "        }\n" +
                "        \n" +
                "        /* Keep original styles unchanged */\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 30px;\n" +
                "            padding: 20px;\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "        }\n" +
                "        \n" +
                "        .header h1 {\n" +
                "            color: #1890ff;\n" +
                "            font-size: 2.2rem;\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        \n" +
                "        .header .subtitle {\n" +
                "            color: #666;\n" +
                "            font-size: 1rem;\n" +
                "        }\n" +
                "        \n" +
                "        .timestamp {\n" +
                "            background: #f0f7ff;\n" +
                "            padding: 12px;\n" +
                "            border-radius: 8px;\n" +
                "            margin-top: 15px;\n" +
                "            display: inline-block;\n" +
                "            color: #1890ff;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .test-id {\n" +
                "            background: #f8f9fa;\n" +
                "            padding: 8px 15px;\n" +
                "            border-radius: 20px;\n" +
                "            font-family: monospace;\n" +
                "            color: #666;\n" +
                "            margin-top: 10px;\n" +
                "            display: inline-block;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        \n" +
                "        .dashboard {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));\n" +
                "            gap: 20px;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "        \n" +
                "        .card {\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 25px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "            transition: transform 0.3s ease, box-shadow 0.3s ease;\n" +
                "        }\n" +
                "        \n" +
                "        .card:hover {\n" +
                "            transform: translateY(-5px);\n" +
                "            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);\n" +
                "        }\n" +
                "        \n" +
                "        .card-header {\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            margin-bottom: 20px;\n" +
                "            padding-bottom: 15px;\n" +
                "            border-bottom: 1px solid #f0f0f0;\n" +
                "        }\n" +
                "        \n" +
                "        .card-icon {\n" +
                "            width: 50px;\n" +
                "            height: 50px;\n" +
                "            border-radius: 10px;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            margin-right: 15px;\n" +
                "            color: white;\n" +
                "            font-size: 1.5rem;\n" +
                "        }\n" +
                "        \n" +
                "        .icon-success { background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%); }\n" +
                "        .icon-performance { background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%); }\n" +
                "        .icon-requests { background: linear-gradient(135deg, #722ed1 0%, #9254de 100%); }\n" +
                "        .icon-time { background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%); }\n" +
                "        .icon-distribution { background: linear-gradient(135deg, #f5222d 0%, #ff4d4f 100%); }\n" +
                "        \n" +
                "        .card-title {\n" +
                "            font-size: 1.2rem;\n" +
                "            font-weight: 600;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .card-value {\n" +
                "            font-size: 2.2rem;\n" +
                "            font-weight: 700;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .success-rate .card-value { color: #52c41a; }\n" +
                "        .response-time .card-value { color: #1890ff; }\n" +
                "        .throughput .card-value { color: #722ed1; }\n" +
                "        .requests .card-value { color: #fa8c16; }\n" +
                "        \n" +
                "        .card-unit {\n" +
                "            font-size: 1rem;\n" +
                "            color: #888;\n" +
                "            margin-left: 5px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        \n" +
                "        .card-footer {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #666;\n" +
                "            margin-top: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .highlight {\n" +
                "            color: #1890ff;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        \n" +
                "        .distribution-card {\n" +
                "            grid-column: span 2;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-distribution {\n" +
                "            margin-top: 15px;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-item {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            align-items: center;\n" +
                "            padding: 12px 0;\n" +
                "            border-bottom: 1px solid #f0f0f0;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-item:last-child {\n" +
                "            border-bottom: none;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-name {\n" +
                "            font-weight: 500;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .provider-percentage {\n" +
                "            font-weight: 700;\n" +
                "            color: #1890ff;\n" +
                "        }\n" +
                "        \n" +
                "        .progress-bar {\n" +
                "            height: 10px;\n" +
                "            background: #f0f0f0;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 5px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        \n" +
                "        .progress-fill {\n" +
                "            height: 100%;\n" +
                "            background: linear-gradient(90deg, #1890ff 0%, #40a9ff 100%);\n" +
                "            border-radius: 5px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-section {\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 30px;\n" +
                "            margin-top: 30px;\n" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);\n" +
                "        }\n" +
                "        \n" +
                "        .summary-title {\n" +
                "            font-size: 1.5rem;\n" +
                "            font-weight: 600;\n" +
                "            margin-bottom: 20px;\n" +
                "            color: #333;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-title i {\n" +
                "            margin-right: 10px;\n" +
                "            color: #1890ff;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-grid {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));\n" +
                "            gap: 20px;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-item {\n" +
                "            padding: 15px;\n" +
                "            background: #f8f9fa;\n" +
                "            border-radius: 8px;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-label {\n" +
                "            font-size: 0.9rem;\n" +
                "            color: #666;\n" +
                "            margin-bottom: 5px;\n" +
                "        }\n" +
                "        \n" +
                "        .summary-value {\n" +
                "            font-size: 1.2rem;\n" +
                "            font-weight: 600;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        \n" +
                "        .status-badge {\n" +
                "            display: inline-block;\n" +
                "            padding: 5px 12px;\n" +
                "            border-radius: 20px;\n" +
                "            font-size: 0.8rem;\n" +
                "            font-weight: 600;\n" +
                "            margin-left: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        .status-success {\n" +
                "            background: #f6ffed;\n" +
                "            color: #52c41a;\n" +
                "            border: 1px solid #b7eb8f;\n" +
                "        }\n" +
                "        \n" +
                "        .status-warning {\n" +
                "            background: #fff7e6;\n" +
                "            color: #fa8c16;\n" +
                "            border: 1px solid #ffd591;\n" +
                "        }\n" +
                "        \n" +
                "        @media (max-width: 768px) {\n" +
                "            .top-nav {\n" +
                "                flex-direction: column;\n" +
                "                align-items: stretch;\n" +
                "            }\n" +
                "            \n" +
                "            .test-selector-container {\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            \n" +
                "            .action-buttons {\n" +
                "                justify-content: center;\n" +
                "            }\n" +
                "            \n" +
                "            .dashboard {\n" +
                "                grid-template-columns: 1fr;\n" +
                "            }\n" +
                "            \n" +
                "            .distribution-card {\n" +
                "                grid-column: span 1;\n" +
                "            }\n" +
                "            \n" +
                "            .header h1 {\n" +
                "                font-size: 1.8rem;\n" +
                "            }\n" +
                "            \n" +
                "            .card-value {\n" +
                "                font-size: 1.8rem;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <!-- Top Navigation Bar -->\n" +
                "        <div class=\"top-nav\">\n" +
                "            <div class=\"nav-title\">\n" +
                "                <i class=\"fas fa-chart-line\"></i>\n" +
                "                Dubbo Performance Test Report System\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"test-selector-container\">\n" +
                "                <div class=\"test-selector\">\n" +
                "                    <span class=\"test-select-label\">\n" +
                "                        <i class=\"fas fa-list\"></i> Select Test Report:\n" +
                "                    </span>\n" +
                "                    <div class=\"test-dropdown\">\n" +
                "                        <select id=\"testSelect\">\n" +
                "                            <option value=\"\">Please select a test report...</option>\n" +
                "                        </select>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"action-buttons\">\n" +
                "                <button class=\"nav-btn nav-btn-primary\" id=\"refreshBtn\">\n" +
                "                    <i class=\"fas fa-sync-alt\"></i> Refresh\n" +
                "                </button>\n" +
                "                <button class=\"nav-btn nav-btn-success\" id=\"historyBtn\">\n" +
                "                    <i class=\"fas fa-history\"></i> History Records\n" +
                "                </button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Data Statistics Bar -->\n" +
                "        <div class=\"stats-bar\" id=\"statsBar\">\n" +
                "            <!-- Dynamically generated statistics -->\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Main Report Content Area -->\n" +
                "        <div id=\"reportContent\">\n" +
                "            <!-- Dynamically loaded report content -->\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Footer -->\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2025 Dubbo Performance Monitoring System | Last Updated: <span id=\"lastUpdateTime\"></span></p>\n" +
                "            <p>Data is for reference only. Test results may be affected by network environment and system load</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <!-- Template: Report Content -->\n" +
                "    <template id=\"reportTemplate\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1><i class=\"fas fa-chart-line\"></i> Dubbo Performance Test Report</h1>\n" +
                "            <p class=\"subtitle\">Interface Performance Monitoring and Analysis Results</p>\n" +
                "            \n" +
                "            <div class=\"timestamp\">\n" +
                "                <i class=\"far fa-clock\"></i> Test Time: <span id=\"reportTimeRange\"></span>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"test-id\">\n" +
                "                <i class=\"fas fa-fingerprint\"></i> Consumer ID: <span id=\"reportTestId\"></span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"dashboard\">\n" +
                "            <div class=\"card success-rate\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-success\">\n" +
                "                        <i class=\"fas fa-check-circle\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">Success Rate</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"successRateValue\">100.0<span class=\"card-unit\">%</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    <span class=\"highlight\" id=\"failedRequests\">0</span> Failed Requests / \n" +
                "                    <span class=\"highlight\" id=\"totalRequests\">100</span> Total Requests\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card response-time\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-performance\">\n" +
                "                        <i class=\"fas fa-tachometer-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">Average Response Time</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"avgResponseTime\">3.23<span class=\"card-unit\">seconds</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    Test Duration <span class=\"highlight\" id=\"testDuration\">40.76</span> seconds\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card throughput\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-time\">\n" +
                "                        <i class=\"fas fa-bolt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">Throughput</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"throughputValue\">2<span class=\"card-unit\">req/sec</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    Peak Concurrency: <span class=\"highlight\" id=\"peakConcurrent\">1</span> req\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card requests\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-requests\">\n" +
                "                        <i class=\"fas fa-exchange-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">Request Statistics</div>\n" +
                "                </div>\n" +
                "                <div class=\"card-value\" id=\"requestsValue\">100<span class=\"card-unit\">req</span></div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    Successful Requests: <span class=\"highlight\" id=\"successfulRequests\">100</span> req\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"card distribution-card\">\n" +
                "                <div class=\"card-header\">\n" +
                "                    <div class=\"card-icon icon-distribution\">\n" +
                "                        <i class=\"fas fa-server\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"card-title\">Provider Distribution</div>\n" +
                "                </div>\n" +
                "                <div class=\"provider-distribution\" id=\"providerDistribution\">\n" +
                "                    <!-- Dynamically generated provider distribution -->\n" +
                "                </div>\n" +
                "                <div class=\"card-footer\">\n" +
                "                    Consumer ID: <span class=\"highlight\" id=\"consumerId\">nullsayHello2</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"summary-section\">\n" +
                "            <div class=\"summary-title\">\n" +
                "                <i class=\"fas fa-info-circle\"></i> Test Summary\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"summary-grid\">\n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Test Duration</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryDuration\">40.76 seconds</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Total Requests</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryTotalRequests\">100 req</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Successful Requests</div>\n" +
                "                    <div class=\"summary-value\" id=\"summarySuccessfulRequests\">\n" +
                "                        100 req <span class=\"status-badge status-success\">All Successful</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Failed Requests</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryFailedRequests\">0 req</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Peak Concurrency</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryPeakConcurrent\">1 req</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Consumer ID</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryConsumerId\">nullsayHello2</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Consumer Unique ID</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryTestId\">TEST-1766925301400-74sayHello2</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"summary-item\">\n" +
                "                    <div class=\"summary-label\">Test Status</div>\n" +
                "                    <div class=\"summary-value\" id=\"summaryStatus\">\n" +
                "                        Completed <span class=\"status-badge status-success\">Success</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </template>\n" +
                "\n" +
                "    <script>\n" +
                "        // Get test data from placeholder\n" +
                "        const testDataJson = '${TEST_DATA}';\n" +
                "        let testReports = {};\n" +
                "        \n" +
                "        try {\n" +
                "            const dataArray = JSON.parse(testDataJson);\n" +
                "            // Convert to object format if it's an array\n" +
                "            if (Array.isArray(dataArray)) {\n" +
                "                dataArray.forEach(report => {\n" +
                "                    const uniqueKey = report.consumerId;\n" +
                "                    testReports[uniqueKey] = report;\n" +
                "                    report.fileName = report.consumerId;\n" +
                "                });\n" +
                "            } else {\n" +
                "                // Single object - Fix: use consumerId as key\n" +
                "                testReports[dataArray.consumerId] = dataArray;\n" +
                "            }\n" +
                "        } catch (error) {\n" +
                "            console.error('Failed to parse test data:', error);\n" +
                "            testReports = {};\n" +
                "        }\n" +
                "        \n" +
                "        // Fix: Rename variable to currentConsumerId for semantic consistency, remove testId\n" +
                "        let currentConsumerId = Object.keys(testReports)[0] || '';\n" +
                "        \n" +
                "        // Initialization\n" +
                "        document.addEventListener('DOMContentLoaded', function() {\n" +
                "            // Initialize dropdown\n" +
                "            populateTestSelect();\n" +
                "            \n" +
                "            // Load default test report\n" +
                "            if (currentConsumerId) {\n" +
                "                loadTestReport(currentConsumerId);\n" +
                "            } else {\n" +
                "                showEmptyState();\n" +
                "            }\n" +
                "            \n" +
                "            // Update statistics\n" +
                "            updateStatsBar();\n" +
                "            \n" +
                "            // Bind events\n" +
                "            document.getElementById('testSelect').addEventListener('change', function() {\n" +
                "                if (this.value) {\n" +
                "                    currentConsumerId = this.value;\n" +
                "                    loadTestReport(currentConsumerId);\n" +
                "                }\n" +
                "            });\n" +
                "            \n" +
                "            document.getElementById('refreshBtn').addEventListener('click', function() {\n" +
                "                if (currentConsumerId) {\n" +
                "                    loadTestReport(currentConsumerId);\n" +
                "                    showNotification('Report refreshed successfully', 'success');\n" +
                "                }\n" +
                "            });\n" +
                "            \n" +
                "            // Update last update time\n" +
                "            updateLastUpdateTime();\n" +
                "            setInterval(updateLastUpdateTime, 60000);\n" +
                "        });\n" +
                "        \n" +
                "        // Populate test selection dropdown\n" +
                "        function populateTestSelect() {\n" +
                "            const select = document.getElementById('testSelect');\n" +
                "            select.innerHTML = '<option value=\"\">Please select a test report...</option>';\n" +
                "            \n" +
                "            // Fix: Rename variable to consumerId, remove testId\n" +
                "            Object.keys(testReports).forEach(consumerId => {\n" +
                "                const report = testReports[consumerId];\n" +
                "                const option = document.createElement('option');\n" +
                "                option.value = consumerId;\n" +
                "                \n" +
                "                // Display format: File name + Test time\n" +
                "                const displayName = report.fileName ? \n" +
                "                    `${report.fileName} - ${report.startTime.substring(11, 19)}` : \n" +
                "                    `${report.consumerId} - ${report.startTime.substring(11, 19)}`;\n" +
                "                \n" +
                "                option.textContent = displayName;\n" +
                "                option.selected = consumerId === currentConsumerId;\n" +
                "                select.appendChild(option);\n" +
                "            });\n" +
                "        }\n" +
                "        \n" +
                "        // Fix: Rename method + parameter to consumerId, remove testId\n" +
                "        function loadTestReport(consumerId) {\n" +
                "            const report = testReports[consumerId];\n" +
                "            if (!report) {\n" +
                "                showNotification('Test report does not exist', 'error');\n" +
                "                return;\n" +
                "            }\n" +
                "            \n" +
                "            // Clone template\n" +
                "            const template = document.getElementById('reportTemplate');\n" +
                "            const reportContent = document.getElementById('reportContent');\n" +
                "            reportContent.innerHTML = '';\n" +
                "            const clone = document.importNode(template.content, true);\n" +
                "            reportContent.appendChild(clone);\n" +
                "            \n" +
                "            // Populate data\n" +
                "            document.getElementById('reportTimeRange').textContent = \n" +
                "                `${report.startTime} - ${report.endTime.substring(11)}`;\n" +
                "            document.getElementById('reportTestId').textContent = report.consumerId;\n" +
                "            \n" +
                "            document.getElementById('successRateValue').innerHTML = \n" +
                "                `${report.successRate.toFixed(1)}<span class=\"card-unit\">%</span>`;\n" +
                "            document.getElementById('failedRequests').textContent = report.failedRequests;\n" +
                "            document.getElementById('totalRequests').textContent = report.totalRequests;\n" +
                "            \n" +
                "            // Handle response time (may contain ms or s)\n" +
                "            const responseTime = formatResponseTime(report.avgResponseTime);\n" +
                "            document.getElementById('avgResponseTime').innerHTML = \n" +
                "                `${responseTime.value}<span class=\"card-unit\">${responseTime.unit}</span>`;\n" +
                "            document.getElementById('testDuration').textContent = report.testDurationSeconds.toFixed(2);\n" +
                "            \n" +
                "            // Fix BUG2: Append unit to throughput\n" +
                "            document.getElementById('throughputValue').innerHTML = `${report.throughput}<span class=\"card-unit\">req/sec</span>`;\n" +
                "            document.getElementById('peakConcurrent').textContent = report.peakConcurrent;\n" +
                "            \n" +
                "            document.getElementById('requestsValue').innerHTML = \n" +
                "                `${report.totalRequests}<span class=\"card-unit\">req</span>`;\n" +
                "            document.getElementById('successfulRequests').textContent = report.successfulRequests;\n" +
                "            \n" +
                "            document.getElementById('consumerId').textContent = report.consumerId;\n" +
                "            \n" +
                "            // Populate provider distribution\n" +
                "            const providerDistribution = document.getElementById('providerDistribution');\n" +
                "            providerDistribution.innerHTML = '';\n" +
                "            Object.entries(report.providerDistribution).forEach(([provider, percentage]) => {\n" +
                "                const providerItem = document.createElement('div');\n" +
                "                providerItem.className = 'provider-item';\n" +
                "                providerItem.innerHTML = `\n" +
                "                    <div class=\"provider-name\">${provider}</div>\n" +
                "                    <div class=\"provider-percentage\">${percentage}%</div>\n" +
                "                `;\n" +
                "                const progressBar = document.createElement('div');\n" +
                "                progressBar.className = 'progress-bar';\n" +
                "                progressBar.innerHTML = `<div class=\"progress-fill\" style=\"width: ${percentage}%\"></div>`;\n" +
                "                providerDistribution.appendChild(providerItem);\n" +
                "                providerDistribution.appendChild(progressBar);\n" +
                "            });\n" +
                "            \n" +
                "            // Populate summary data\n" +
                "            document.getElementById('summaryDuration').textContent = `${report.testDurationSeconds.toFixed(2)} seconds`;\n" +
                "            document.getElementById('summaryTotalRequests').textContent = `${report.totalRequests} req`;\n" +
                "            document.getElementById('summarySuccessfulRequests').innerHTML = \n" +
                "                `${report.successfulRequests} req ${getSuccessBadge(report.successRate)}`;\n" +
                "            document.getElementById('summaryFailedRequests').textContent = `${report.failedRequests} req`;\n" +
                "            document.getElementById('summaryPeakConcurrent').textContent = report.peakConcurrent;\n" +
                "            document.getElementById('summaryConsumerId').textContent = report.consumerId;\n" +
                "            document.getElementById('summaryTestId').textContent = report.consumerId;\n" +
                "            document.getElementById('summaryStatus').innerHTML = \n" +
                "                `Completed ${getStatusBadge(report.successRate)}`;\n" +
                "            \n" +
                "            // Add card animation\n" +
                "            setTimeout(() => {\n" +
                "                const cards = document.querySelectorAll('.card');\n" +
                "                cards.forEach((card, index) => {\n" +
                "                    card.style.opacity = '0';\n" +
                "                    card.style.transform = 'translateY(20px)';\n" +
                "                    card.style.transition = 'all 0.5s ease';\n" +
                "                    \n" +
                "                    setTimeout(() => {\n" +
                "                        card.style.opacity = '1';\n" +
                "                        card.style.transform = 'translateY(0)';\n" +
                "                    }, index * 100);\n" +
                "                });\n" +
                "            }, 100);\n" +
                "        }\n" +
                "        \n" +
                "        // Format response time\n" +
                "        function formatResponseTime(responseTimeStr) {\n" +
                "            if (!responseTimeStr) return { value: '0', unit: 'ms' };\n" +
                "            \n" +
                "            // Remove non-numeric characters\n" +
                "            const numStr = responseTimeStr.replace(/[^0-9.]/g, '');\n" +
                "            const value = parseFloat(numStr) || 0;\n" +
                "            \n" +
                "            // Determine unit\n" +
                "            if (responseTimeStr.includes('ms')) {\n" +
                "                return { value: value.toFixed(2), unit: 'ms' };\n" +
                "            } else if (responseTimeStr.includes('s')) {\n" +
                "                return { value: value.toFixed(2), unit: 's' };\n" +
                "            } else {\n" +
                "                // Default to milliseconds\n" +
                "                return { value: value.toFixed(2), unit: 'ms' };\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // Get success rate badge\n" +
                "        function getSuccessBadge(successRate) {\n" +
                "            if (successRate >= 95) {\n" +
                "                return '<span class=\"status-badge status-success\">Excellent</span>';\n" +
                "            } else if (successRate >= 80) {\n" +
                "                return '<span class=\"status-badge status-warning\">Good</span>';\n" +
                "            } else {\n" +
                "                return '<span class=\"status-badge\" style=\"background:#fff2f0;color:#f5222d;border:1px solid #ffccc7\">Needs Improvement</span>';\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // Get status badge\n" +
                "        function getStatusBadge(successRate) {\n" +
                "            if (successRate >= 95) {\n" +
                "                return '<span class=\"status-badge status-success\">Success</span>';\n" +
                "            } else if (successRate >= 80) {\n" +
                "                return '<span class=\"status-badge status-warning\">Warning</span>';\n" +
                "            } else {\n" +
                "                return '<span class=\"status-badge\" style=\"background:#fff2f0;color:#f5222d;border:1px solid #ffccc7\">Failed</span>';\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // Update statistics bar\n" +
                "        function updateStatsBar() {\n" +
                "            const statsBar = document.getElementById('statsBar');\n" +
                "            const reports = Object.values(testReports);\n" +
                "            \n" +
                "            if (reports.length === 0) {\n" +
                "                statsBar.innerHTML = `\n" +
                "                    <div style=\"grid-column:1/-1;text-align:center;padding:20px;color:#999;\">\n" +
                "                        <i class=\"fas fa-inbox\" style=\"font-size:2rem;margin-bottom:10px;\"></i>\n" +
                "                        <p>No test data available</p>\n" +
                "                    </div>\n" +
                "                `;\n" +
                "                return;\n" +
                "            }\n" +
                "            \n" +
                "            // Calculate statistics\n" +
                "            const totalTests = reports.length;\n" +
                "            const totalRequests = reports.reduce((sum, r) => sum + r.totalRequests, 0);\n" +
                "            const avgSuccessRate = reports.reduce((sum, r) => sum + r.successRate, 0) / totalTests;\n" +
                "            \n" +
                "            // Calculate average response time (convert to milliseconds)\n" +
                "            const totalResponseTime = reports.reduce((sum, r) => {\n" +
                "                const timeStr = r.avgResponseTime || '0ms';\n" +
                "                const numStr = timeStr.replace(/[^0-9.]/g, '');\n" +
                "                let value = parseFloat(numStr) || 0;\n" +
                "                \n" +
                "                // Convert to milliseconds if it's seconds\n" +
                "                if (timeStr.includes('s') && !timeStr.includes('ms')) {\n" +
                "                    value = value * 1000;\n" +
                "                }\n" +
                "                return sum + value;\n" +
                "            }, 0);\n" +
                "            const avgResponseTimeMs = totalResponseTime / totalTests;\n" +
                "            \n" +
                "            statsBar.innerHTML = `\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-total\">\n" +
                "                        <i class=\"fas fa-file-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>Total Test Runs</h4>\n" +
                "                        <div class=\"value\">${totalTests}<span class=\"unit\">runs</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-success\">\n" +
                "                        <i class=\"fas fa-check-circle\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>Average Success Rate</h4>\n" +
                "                        <div class=\"value\">${avgSuccessRate.toFixed(1)}<span class=\"unit\">%</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-avg\">\n" +
                "                        <i class=\"fas fa-tachometer-alt\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>Average Response Time</h4>\n" +
                "                        <div class=\"value\">${avgResponseTimeMs.toFixed(2)}<span class=\"unit\">ms</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"stat-item\">\n" +
                "                    <div class=\"stat-icon stat-icon-time\">\n" +
                "                        <i class=\"fas fa-clock\"></i>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-content\">\n" +
                "                        <h4>Total Requests</h4>\n" +
                "                        <div class=\"value\">${totalRequests}<span class=\"unit\">req</span></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            `;\n" +
                "        }\n" +
                "        \n" +
                "        // Show empty state\n" +
                "        function showEmptyState() {\n" +
                "            const reportContent = document.getElementById('reportContent');\n" +
                "            reportContent.innerHTML = `\n" +
                "                <div style=\"text-align:center;padding:60px;background:white;border-radius:12px;\">\n" +
                "                    <i class=\"fas fa-chart-bar\" style=\"font-size:4rem;color:#ddd;margin-bottom:20px;\"></i>\n" +
                "                    <h3 style=\"color:#666;margin-bottom:10px;\">No Test Data Available</h3>\n" +
                "                    <p style=\"color:#999;\">Waiting for test data to be imported...</p>\n" +
                "                </div>\n" +
                "            `;\n" +
                "        }\n" +
                "        \n" +
                "        // Update last update time\n" +
                "        function updateLastUpdateTime() {\n" +
                "            const now = new Date();\n" +
                "            const timeString = now.toLocaleString('en-US');\n" +
                "            document.getElementById('lastUpdateTime').textContent = timeString;\n" +
                "        }\n" +
                "        \n" +
                "        // Show notification\n" +
                "        function showNotification(message, type = 'info') {\n" +
                "            const notification = document.createElement('div');\n" +
                "            notification.style.cssText = `\n" +
                "                position: fixed;\n" +
                "                top: 20px;\n" +
                "                right: 20px;\n" +
                "                padding: 15px 25px;\n" +
                "                border-radius: 8px;\n" +
                "                color: white;\n" +
                "                font-weight: 600;\n" +
                "                box-shadow: 0 4px 12px rgba(0,0,0,0.15);\n" +
                "                z-index: 1000;\n" +
                "                animation: slideIn 0.3s ease;\n" +
                "                max-width: 400px;\n" +
                "            `;\n" +
                "            \n" +
                "            const bgColors = {\n" +
                "                success: 'linear-gradient(135deg, #52c41a 0%, #389e0d 100%)',\n" +
                "                error: 'linear-gradient(135deg, #f5222d 0%, #cf1322 100%)',\n" +
                "                warning: 'linear-gradient(135deg, #fa8c16 0%, #d46b08 100%)',\n" +
                "                info: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)'\n" +
                "            };\n" +
                "            \n" +
                "            notification.style.background = bgColors[type] || bgColors.info;\n" +
                "            \n" +
                "            const icons = {\n" +
                "                success: '✓',\n" +
                "                error: '✗',\n" +
                "                warning: '⚠',\n" +
                "                info: 'ℹ'\n" +
                "            };\n" +
                "            \n" +
                "            notification.innerHTML = `\n" +
                "                <span style=\"margin-right:10px;font-size:1.2em;\">${icons[type] || 'ℹ'}</span>\n" +
                "                ${message}\n" +
                "            `;\n" +
                "            \n" +
                "            document.body.appendChild(notification);\n" +
                "            \n" +
                "            setTimeout(() => {\n" +
                "                notification.style.animation = 'slideOut 0.3s ease';\n" +
                "                setTimeout(() => notification.remove(), 300);\n" +
                "            }, 3000);\n" +
                "            \n" +
                "            if (!document.querySelector('#notification-styles')) {\n" +
                "                const style = document.createElement('style');\n" +
                "                style.id = 'notification-styles';\n" +
                "                style.textContent = `\n" +
                "                    @keyframes slideIn {\n" +
                "                        from { transform: translateX(100%); opacity: 0; }\n" +
                "                        to { transform: translateX(0); opacity: 1; }\n" +
                "                    }\n" +
                "                    @keyframes slideOut {\n" +
                "                        from { transform: translateX(0); opacity: 1; }\n" +
                "                        to { transform: translateX(100%); opacity: 0; }\n" +
                "                    }\n" +
                "                `;\n" +
                "                document.head.appendChild(style);\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        html = html.replace("${TEST_DATA}",testData);

        return html;
    }

    public static void writeConsumerHtml(String data, String file_path){
        String generateHtmlPageCN = generateHtmlPageCN(data);
        String generateHtmlPageEN = generateHtmlPageEN(data);
        try {
            writeHtmlToFile(generateHtmlPageEN, file_path + "EN.html");
            writeHtmlToFile(generateHtmlPageCN, file_path + "CN.html");
        } catch (IOException e) {
            log.error("write html to file error", e);
        }

    }
    public static void writeHtmlToFile(String htmlContent, String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        Files.write(file.toPath(), htmlContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}