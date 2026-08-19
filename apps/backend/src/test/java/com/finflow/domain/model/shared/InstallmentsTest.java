package com.finflow.domain.model.shared;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InstallmentsTest {

    @ParameterizedTest
    @CsvSource({
            "100.00, 3",
            "100.00, 7",
            "0.01, 3",
            "999999.99, 11",
            "10.00, 1",
            "0.10, 3",
    })
    void splitAlwaysSumsBackToTheOriginalTotal(BigDecimal total, int n) {
        List<BigDecimal> parts = Installments.split(total, n);

        assertThat(parts).hasSize(n);
        BigDecimal sum = parts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(sum).isEqualByComparingTo(total);
    }

    @ParameterizedTest
    @CsvSource({"100.00, 3", "10.00, 1"})
    void everyInstallmentHasTwoDecimalScale(BigDecimal total, int n) {
        List<BigDecimal> parts = Installments.split(total, n);

        assertThat(parts).allSatisfy(part -> assertThat(part.scale()).isEqualTo(2));
    }
}
