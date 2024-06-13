/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.benchmark;

import com.alibaba.fastjson2.JSON;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.benchmark.config.TestConfig;
import org.apache.dubbo.benchmark.entity.CpuResults;
import org.apache.dubbo.benchmark.entity.JmhResults;
import org.apache.dubbo.benchmark.entity.MemResults;
import org.apache.dubbo.benchmark.mapper.CpuResultsMapper;
import org.apache.dubbo.benchmark.mapper.JmhResultsMapper;
import org.apache.dubbo.benchmark.mapper.MemResultsMapper;
import org.apache.dubbo.benchmark.prom.PromDataInfo;
import org.apache.dubbo.benchmark.prom.PromResponse;
import org.apache.dubbo.benchmark.prom.PromResultInfo;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.util.ClassUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.apache.dubbo.benchmark.utils.BenchmarkTypeHelper.getClientContainerByProtocol;
import static org.apache.dubbo.benchmark.utils.BenchmarkTypeHelper.getServerContainerByProtocol;

@Slf4j
@Service
public class ResultService {

    private static final CloseableHttpClient httpclient;

    static {
        httpclient = HttpClients.createDefault();
    }

    private final TestConfig config;
    private final CpuResultsMapper cpuResultsMapper;
    private final MemResultsMapper memResultsMapper;
    private final JmhResultsMapper jmhResultsMapper;
    @Value("${spring.application.name}")
    private String appName;

    public ResultService(TestConfig config, CpuResultsMapper cpuResultsMapper, MemResultsMapper memResultsMapper,
        JmhResultsMapper jmhResultsMapper) {
        this.config = config;
        this.cpuResultsMapper = cpuResultsMapper;
        this.memResultsMapper = memResultsMapper;
        this.jmhResultsMapper = jmhResultsMapper;
    }

    private static URI buildURI(String metricsURL, String query, double start, double end, String step) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("query", query));
        nvps.add(new BasicNameValuePair("start", String.valueOf(start)));
        nvps.add(new BasicNameValuePair("end", String.valueOf(end)));
        nvps.add(new BasicNameValuePair("step", step));

        try {
            return new URIBuilder(new URI(metricsURL))
                .addParameters(nvps)
                .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static PromResponse get(URI uri) {
        PromResponse promDataInfo = null;
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setUri(uri);
        try {
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                String resultContent = EntityUtils.toString(entity);
                log.info("Query result from prometheus is {}", resultContent);
                promDataInfo = JSON.parseObject(resultContent, PromResponse.class);
            }

        } catch (IOException | ParseException e) {
            log.error("Get res from prometheus error: ", e);
            e.printStackTrace();
        }
        return promDataInfo;
    }

    public void saveJmhResults(Collection<RunResult> runResults) {
        Collection<String> benchNames = new ArrayList<>();
        for (RunResult runResult : runResults) {
            benchNames.add(runResult.getParams().getBenchmark());
            for (String label : runResult.getSecondaryResults().keySet()) {
                benchNames.add(runResult.getParams().getBenchmark() + ":" + label);
            }
        }
        Map<String, String> benchPrefixes = ClassUtils.denseClassNames(benchNames);

        for (RunResult runResult : runResults) {
            Mode mode = runResult.getParams().getMode();
            String benchmarkName = benchPrefixes.get(runResult.getParams().getBenchmark());
            Result pRes = runResult.getPrimaryResult();
            long cnt = pRes.getSampleCount();
            double score = pRes.getScore();

            double scoreError = pRes.getScoreError();
            String scoreUnit = pRes.getScoreUnit();

            log.info("Benchmark: {}, Mode: {}, Cnt: {}, Score: {}, Error: {}, Unit: {}",
                benchmarkName, mode.shortLabel(), cnt, score, scoreError, scoreUnit);

            JmhResults results = JmhResults.builder().benchmark(benchmarkName).cnt(cnt)
                .mode(mode.shortLabel()).protocolName(config.getProtocol())
                .rpcVersion(config.getRpcVersion())
                .score(score).units(scoreUnit).error(scoreError).build();
            jmhResultsMapper.insert(results);

            for (Map.Entry<String, Result> e : runResult.getSecondaryResults().entrySet()) {
                String label = e.getKey();
                Result subRes = e.getValue();

                String subBenchmarkName = benchPrefixes.get(runResult.getParams().getBenchmark() + ":" + label);
                long subCount = subRes.getSampleCount();
                double subScore = subRes.getScore();
                double subScoreError = subRes.getScoreError();
                String subScoreUnit = subRes.getScoreUnit();
                log.info("Benchmark: {}, Mode: {}, Cnt: {}, Score: {}, Error: {}, Unit: {}",
                    subBenchmarkName, mode.shortLabel(), subCount, subScore, subScoreError, subScoreUnit);
                JmhResults subResults = JmhResults.builder().benchmark(subBenchmarkName).cnt(subCount)
                    .mode(mode.shortLabel()).protocolName(config.getProtocol())
                    .rpcVersion(config.getRpcVersion())
                    .score(subScore).units(subScoreUnit).error(subScoreError).build();
                jmhResultsMapper.insert(subResults);
            }
        }
    }

    public void saveCpuResults(LocalDateTime startTime, LocalDateTime endTime) {
        String serverContainerName = getServerContainerByProtocol(config.getProtocol());
        this.doSaveCpuResults(serverContainerName, "server", startTime, endTime);

        String clientContainerName = getClientContainerByProtocol(config.getProtocol());
        this.doSaveCpuResults(clientContainerName, "client", startTime, endTime);
    }

    private void doSaveCpuResults(String containerName, String side, LocalDateTime startTime, LocalDateTime endTime) {
        String query = String.format("avg ((sum by (pod) (irate(container_cpu_usage_seconds_total{container!=\"\",container=\"%s\"}[5m]))) / on(pod) sum by (pod) (kube_pod_container_resource_limits{resource=\"cpu\",pod=~\"%s.*\"})) * 100.00",
            containerName, containerName);
        log.info("cpu query: {}", query);

        double start = startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000.00;
        double end = endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000.00;
        URI uri = buildURI(config.getPrometheusUrl(), query, start, end, "60s");
        PromResponse res = get(uri);
        if (res == null) {
            log.warn("PromResponse is null");
            return;
        }

        PromDataInfo promDataInfo = res.getData();
        List<PromResultInfo> result = promDataInfo.getResult();
        if (result == null || result.isEmpty()) {
            log.warn("Not found result for cpu load");
            return;
        }

        List<double[]> values = result.get(0).getValues();

        // 算出value[1]的平均值
        double sum = 0;
        double cpuMin = Double.MAX_VALUE;
        double cpuMax = Double.MIN_VALUE;
        for (double[] value : values) {
            sum += value[1];
            cpuMax = Math.max(cpuMax, value[1]);
            cpuMin = Math.min(cpuMin, value[1]);
        }
        double avg = sum / values.size();
        log.info("CPU Load: {}", avg);
        CpuResults cpuResults = CpuResults.builder().protocol(config.getProtocol())
            .startTime(startTime).endTime(endTime)
            .cpuLoad(avg).cpuMax(cpuMax).cpuMin(cpuMin).side(side).build();
        cpuResultsMapper.insert(cpuResults);
    }

    public void saveMemResults(LocalDateTime startTime, LocalDateTime endTime) {
        String serverContainerName = getServerContainerByProtocol(config.getProtocol());
        this.doSaveMemResults(serverContainerName, "server", startTime, endTime);

        String clientContainerName = getClientContainerByProtocol(config.getProtocol());
        this.doSaveMemResults(clientContainerName, "client", startTime, endTime);
    }

    private void doSaveMemResults(String containerName, String side, LocalDateTime startTime, LocalDateTime endTime) {
        String query = String.format("sum(container_memory_working_set_bytes{namespace=\"dubbo-benchmark\",container=\"%s\", image!=\"\"}) by (container)",
            containerName);
        log.info("mem query: {}", query);
        double start = startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000.00;
        double end = endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000.00;
        PromResponse res = get(buildURI(config.getPrometheusUrl(), query, start, end, "60s"));
        if (res == null) {
            log.warn("PromResponse is null");
            return;
        }
        PromDataInfo promDataInfo = res.getData();
        List<PromResultInfo> result = promDataInfo.getResult();
        if (result == null || result.isEmpty()) {
            log.warn("Not found result for mem load");
            return;
        }
        List<double[]> values = result.get(0).getValues();
        double sum = 0;
        double memMin = Double.MAX_VALUE;
        double memMax = Double.MIN_VALUE;
        for (double[] value : values) {
            sum += value[1];
            memMax = Math.max(memMax, value[1]);
            memMin = Math.min(memMin, value[1]);
        }
        double avg = sum / values.size();
        MemResults memResults = MemResults.builder().protocol(config.getProtocol())
            .startTime(startTime).endTime(endTime)
            .memLoad(avg).memMax(memMax).memMin(memMin).side(side).build();
        memResultsMapper.insert(memResults);
    }

    /**
     * only for test
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String queryJson = "{\"status\":\"success\",\"data\":{\"resultType\":\"matrix\",\"result\":[{\"metric\":{},\"values\":[[1711601686.263,\"0.07178520218839923\"],[1711601746.263,\"0.05682459167185162\"],[1711601806.263,\"4.8474125889622615\"],[1711601866.263,\"13.247583631888645\"],[1711601926.263,\"0.06987499735472855\"],[1711601986.263,\"0.0632709071190294\"],[1711602046.263,\"0.06737732086902697\"],[1711602106.263,\"0.05597124019312168\"],[1711602166.263,\"1.9652464832585697\"],[1711602226.263,\"13.601034688259492\"],[1711602286.263,\"0.1007093927711463\"],[1711602346.263,\"0.055604477931522345\"],[1711602406.263,\"0.07011979307357707\"],[1711602466.263,\"0.09646824144436661\"],[1711602526.263,\"0.061025238318332335\"],[1711602586.263,\"3.8061278665525373\"],[1711602646.263,\"0.06290802816888687\"],[1711602706.263,\"0.10228552338316582\"],[1711602766.263,\"0.06417597270626761\"],[1711602826.263,\"0.061983749686327604\"],[1711602886.263,\"4.99440286982409\"],[1711602946.263,\"12.51032311864132\"],[1711603006.263,\"0.08053515080515435\"],[1711603066.263,\"0.065106506154014\"],[1711603126.263,\"0.04687996111828417\"],[1711603186.263,\"0.07917016745684466\"],[1711603246.263,\"0.10670538728905614\"],[1711603306.263,\"15.134932726432481\"],[1711603366.263,\"0.05571385069570071\"],[1711603426.263,\"0.0664284551791871\"],[1711603486.263,\"0.050470803073630714\"]]}]}}";
        PromResponse promResponse = JSON.parseObject(queryJson, PromResponse.class);
        System.out.println(promResponse.getData().getResult());
        System.out.println(Arrays.toString(promResponse.getData().getResult().get(0).getValues().get(0)));

        String query = String.format("avg ((sum by (pod) (irate(container_cpu_usage_seconds_total{container!=\"\",container=\"%s\"}[5m]))) / on(pod) sum by (pod) (kube_pod_container_resource_limits{resource=\"cpu\",pod=~\"%s.*\"})) * 100.00",
            "dubbo-dubbo-server", "dubbo-dubbo-server");
        double end = System.currentTimeMillis() / 1000.00;
        double start = end - 60 * 30;
        URI uri = buildURI("http://localhost:9090/api/v1/query_range", query, start, end, "60s");
        get(uri);
    }
}
