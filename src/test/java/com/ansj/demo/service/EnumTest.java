package com.ansj.demo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumTest {

    @Test
    void test() {
        String devName = Type.DEV.name();
        Assertions.assertThat(devName).isEqualTo("DEV");
    }

    static enum Type {
        DEV, PROD, LOCAL
    }

}
