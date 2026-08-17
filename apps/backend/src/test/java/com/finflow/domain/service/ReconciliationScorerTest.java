package com.finflow.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ReconciliationScorerTest {

    private static final LocalDate DAY = LocalDate.of(2026, 3, 10);

    @Test
    void exactValueSameDateWithDocumentMatchScoresPerfect() {
        double score = ReconciliationScorer.score(
                new BigDecimal("500.00"), DAY, "PIX RECEBIDO CPF 11122233344",
                new BigDecimal("500.00"), DAY, "111.222.333-44");

        assertThat(score).isCloseTo(1.0, within(0.001));
    }

    @Test
    void valueScoreDecaysToZeroAtTwoPercentDifference() {
        // 2% de 1000 = 20 -> diferença de 20 deve zerar o sinal de valor
        double atThreshold = ReconciliationScorer.valueScore(new BigDecimal("980.00"), new BigDecimal("1000.00"));
        double beyondThreshold = ReconciliationScorer.valueScore(new BigDecimal("970.00"), new BigDecimal("1000.00"));

        assertThat(atThreshold).isCloseTo(0.0, within(0.001));
        assertThat(beyondThreshold).isEqualTo(0.0);
    }

    @Test
    void valueScoreIsPartialAtHalfTheDecayWindow() {
        // 1% de diferença = metade do caminho até o limite de 2%
        double score = ReconciliationScorer.valueScore(new BigDecimal("990.00"), new BigDecimal("1000.00"));
        assertThat(score).isCloseTo(0.5, within(0.001));
    }

    @Test
    void dateScoreDecaysToZeroAtFiveDays() {
        double atThreshold = ReconciliationScorer.dateScore(DAY, DAY.plusDays(5));
        double withinWindow = ReconciliationScorer.dateScore(DAY, DAY.plusDays(2));

        assertThat(atThreshold).isCloseTo(0.0, within(0.001));
        assertThat(withinWindow).isCloseTo(0.6, within(0.001)); // 1 - 2/5
    }

    @Test
    void documentScoreMatchesDigitsIgnoringFormatting() {
        assertThat(ReconciliationScorer.documentScore("PIX DE 111.222.333-44 JOAO", "11122233344")).isEqualTo(1.0);
        assertThat(ReconciliationScorer.documentScore("PIX RECEBIDO JOAO S", "11122233344")).isEqualTo(0.0);
        assertThat(ReconciliationScorer.documentScore("PIX RECEBIDO", null)).isEqualTo(0.0);
    }

    @Test
    void clearWinnerIsAutoReconcilable() {
        assertThat(ReconciliationScorer.isAutoReconcilable(0.95, 0.40)).isTrue();
    }

    @Test
    void highScoreWithoutMarginIsNotAutoReconcilable() {
        // ADR-0004 §2: duas parcelas de mesmo valor/data no mesmo dia -- ambos scores altos, mas empatados.
        assertThat(ReconciliationScorer.isAutoReconcilable(0.90, 0.90)).isFalse();
        assertThat(ReconciliationScorer.isAutoReconcilable(0.90, 0.80)).isFalse(); // margem 0.10 < 0.15 exigido
    }

    @Test
    void scoreBelowMinimumIsNotAutoReconcilableEvenWithMargin() {
        assertThat(ReconciliationScorer.isAutoReconcilable(0.80, 0.10)).isFalse();
    }
}
