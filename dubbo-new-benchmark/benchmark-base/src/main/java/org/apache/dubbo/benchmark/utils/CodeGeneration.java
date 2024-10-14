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
package org.apache.dubbo.benchmark.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.sql.Types;
import java.util.Collections;

/**
 * 代码生成
 */
public class CodeGeneration {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:postgresql://121.250.209.74:5432/benchmark", "admin", "cong0917")
            .globalConfig(builder -> {
                builder.author("conghuhu")
                    .enableSwagger() // 开启 swagger 模式
                    .outputDir("D:\\MainProject\\dubbo-new-benchmark\\benchmark-base\\src\\main\\resources\\"); // 指定输出目录
            })
            .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                if (typeCode == Types.SMALLINT) {
                    // 自定义类型转换
                    return DbColumnType.INTEGER;
                }
                return typeRegistry.getColumnType(metaInfo);

            }))
            .packageConfig(builder -> {
                builder.parent("org.apache.dubbo.benchmark") // 设置父包名
                    .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\MainProject\\dubbo-new-benchmark\\benchmark-base\\src\\main\\resources\\")); // 设置mapperXml生成路径
            })
            .strategyConfig(builder -> {
                builder.addInclude(".*");
            })
            .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
            .execute();
    }
}
