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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.Field;

class ClientHelper {

    private ClientHelper() {
    }

    static Options getSupports() {
        Options options = new Options();
        String[] supportArgumentNames = Arguments.getSupportArgumentNames();
        for (String supportArgumentName : supportArgumentNames) {
            options.addOption(Option.builder().longOpt(supportArgumentName).hasArg().build());
        }
        return options;
    }

    private static CommandLine parseLine(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(getSupports(), args);
    }

    static Arguments parseArguments(String[] args) throws ParseException {
        return new Arguments(parseLine(args));
    }

    static ChainedOptionsBuilder newBaseChainedOptionsBuilder(Arguments arguments) {
        int warmupIterations = arguments.getWarmupIterations();
        int warmupTime = arguments.getWarmupTime();
        int measurementIterations = arguments.getMeasurementIterations();
        int measurementTime = arguments.getMeasurementTime();
        String format = arguments.getResultFormat();
        String output = System.getProperty("benchmark.output");
        ChainedOptionsBuilder optBuilder = new OptionsBuilder()
                .resultFormat(ResultFormatType.valueOf(format.toUpperCase()))
                .warmupIterations(warmupIterations)
                .warmupTime(TimeValue.seconds(warmupTime))
                .measurementIterations(measurementIterations)
                .measurementTime(TimeValue.seconds(measurementTime));
        if (output != null && !output.trim().isEmpty()) {
            optBuilder.output(output);
        }
        return optBuilder;
    }

    static class Arguments {

        public int warmupIterations = 3;

        public int warmupTime = 10;

        public int measurementIterations = 3;

        public int measurementTime = 10;

        public String resultFormat = "json";


        private Arguments(CommandLine line) {
            this.warmupIterations = Integer.parseInt(line.getOptionValue("warmupIterations", "3"));
            this.warmupTime = Integer.parseInt(line.getOptionValue("warmupTime", "10"));
            this.measurementIterations = Integer.parseInt(line.getOptionValue("measurementIterations", "3"));
            this.measurementTime = Integer.parseInt(line.getOptionValue("measurementTime", "10"));
            this.resultFormat = line.getOptionValue("resultFormat", "json");
        }

        public int getWarmupIterations() {
            return warmupIterations;
        }

        public void setWarmupIterations(int warmupIterations) {
            this.warmupIterations = warmupIterations;
        }

        public int getWarmupTime() {
            return warmupTime;
        }

        public void setWarmupTime(int warmupTime) {
            this.warmupTime = warmupTime;
        }

        public int getMeasurementIterations() {
            return measurementIterations;
        }

        public void setMeasurementIterations(int measurementIterations) {
            this.measurementIterations = measurementIterations;
        }

        public int getMeasurementTime() {
            return measurementTime;
        }

        public void setMeasurementTime(int measurementTime) {
            this.measurementTime = measurementTime;
        }

        public String getResultFormat() {
            return resultFormat;
        }

        public void setResultFormat(String resultFormat) {
            this.resultFormat = resultFormat;
        }

        private static String[] getSupportArgumentNames() {
            Field[] declaredFields = Arguments.class.getDeclaredFields();
            String[] names = new String[declaredFields.length];
            for (int i = 0; i < declaredFields.length; i++) {
                names[i] = declaredFields[i].getName();
            }
            return names;
        }
    }
}
