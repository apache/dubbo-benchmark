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
package org.apache.dubbo.benchmark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mem_results")
public class MemResults implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createdTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double memLoad;

    private String protocol;

    private Double memMax;

    private Double memMin;

    private String side;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getMemLoad() {
        return memLoad;
    }

    public void setMemLoad(Double memLoad) {
        this.memLoad = memLoad;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Double getMemMax() {
        return memMax;
    }

    public void setMemMax(Double memMax) {
        this.memMax = memMax;
    }

    public Double getMemMin() {
        return memMin;
    }

    public void setMemMin(Double memMin) {
        this.memMin = memMin;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    @Override public String toString() {
        return "MemResults{" +
            "id=" + id +
            ", createdTime=" + createdTime +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", memLoad=" + memLoad +
            ", protocol='" + protocol + '\'' +
            ", memMax=" + memMax +
            ", memMin=" + memMin +
            '}';
    }
}
