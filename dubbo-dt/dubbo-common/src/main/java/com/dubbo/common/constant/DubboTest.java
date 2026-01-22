package com.dubbo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DubboTest<T> {
    private T name;
}
