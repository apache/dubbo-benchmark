package org.apache.dubbo.benchmark.util;

import org.apache.dubbo.benchmark.Client;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * @author plusman
 * @since 2021/5/31 2:07 PM
 */
public class ClientCommonUtil {
    /**
     * Handle JMH common options 
     * @param args 
     * @return
     */
    public static ChainedOptionsBuilder doOptions(String[] args) throws ParseException {
        // Handle args
        org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

        options.addOption(Option.builder().longOpt("warmupIterations").hasArg().build());
        options.addOption(Option.builder().longOpt("warmupTime").hasArg().build());
        options.addOption(Option.builder().longOpt("measurementIterations").hasArg().build());
        options.addOption(Option.builder().longOpt("measurementTime").hasArg().build());
        options.addOption(Option.builder().longOpt("concurrency").hasArg().build());

        CommandLineParser parser = new DefaultParser();

        CommandLine line = parser.parse(options, args);

        // Just for development: --warmupIterations=1 --warmupTime=5 --measurementIterations=1 --measurementTime=5 --concurrency=6
        int warmupIterations = Integer.valueOf(line.getOptionValue("warmupIterations", "3"));
        int warmupTime = Integer.valueOf(line.getOptionValue("warmupTime", "10"));
        int measurementIterations = Integer.valueOf(line.getOptionValue("measurementIterations", "3"));
        int measurementTime = Integer.valueOf(line.getOptionValue("measurementTime", "10"));
        int concurrency = Integer.valueOf(line.getOptionValue("concurrency", "32"));

        ChainedOptionsBuilder optBuilder = new OptionsBuilder()
                .include(Client.class.getSimpleName() + "\\.")
                .warmupIterations(warmupIterations)
                .warmupTime(TimeValue.seconds(warmupTime))
                .measurementIterations(measurementIterations)
                .measurementTime(TimeValue.seconds(measurementTime))
                .timeout(TimeValue.minutes(10))
                .threads(concurrency)
                .forks(1);

        // Handle output 
        String output = System.getProperty("benchmark.output.dir");
        Options optInner = optBuilder.build();
        
        if (output != null && !output.trim().isEmpty()) {
            String name = System.getProperty("benchmark.output.name", "output");
            String finalName = String.format("%sc%s", name, optInner.getThreads().get());
            
            optBuilder
                    .output(output + finalName + ".log")
                    .resultFormat(ResultFormatType.JSON)
                    .result(output + finalName + ".json");
        }
        
        return optBuilder;
    }
}
