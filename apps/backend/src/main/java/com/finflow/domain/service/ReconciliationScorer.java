package com.finflow.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * ADR-0004 §1: score de matching entre uma transação de extrato e um
 * recebível/pagável candidato. Puro -- sem I/O, sem Spring -- para ficar
 * testável por tabela de casos, incluindo os limites das curvas de decaimento.
 */
public final class ReconciliationScorer {

    private static final BigDecimal VALUE_WEIGHT = new BigDecimal("0.5");
    private static final BigDecimal DATE_WEIGHT = new BigDecimal("0.3");
    private static final BigDecimal DOCUMENT_WEIGHT = new BigDecimal("0.2");

    /** Fora desta janela um candidato nem entra na busca (ADR-0004 §1) -- a curva de decaimento da data vai até ±5. */
    public static final int CANDIDATE_SEARCH_WINDOW_DAYS = 2;
    private static final double DATE_DECAY_DAYS = 5.0;
    private static final double VALUE_DECAY_PCT = 0.02;

    public static final double AUTO_RECONCILE_MIN_SCORE = 0.85;
    public static final double AUTO_RECONCILE_MIN_MARGIN = 0.15;

    private ReconciliationScorer() {
    }

    public static double score(BigDecimal txAmount, LocalDate txDate, String txDescription,
                                BigDecimal targetAmount, LocalDate targetDueDate, String targetDocument) {
        return breakdown(txAmount, txDate, txDescription, targetAmount, targetDueDate, targetDocument).total();
    }

    /** ADR-0004 §3: fila de revisão mostra o detalhamento por sinal, não só o total. */
    public static ScoreBreakdown breakdown(BigDecimal txAmount, LocalDate txDate, String txDescription,
                                            BigDecimal targetAmount, LocalDate targetDueDate, String targetDocument) {
        double value = valueScore(txAmount, targetAmount);
        double date = dateScore(txDate, targetDueDate);
        double document = documentScore(txDescription, targetDocument);
        double total = VALUE_WEIGHT.doubleValue() * value
                + DATE_WEIGHT.doubleValue() * date
                + DOCUMENT_WEIGHT.doubleValue() * document;
        return new ScoreBreakdown(total, value, date, document);
    }

    public record ScoreBreakdown(double total, double valueScore, double dateScore, double documentScore) {
    }

    static double valueScore(BigDecimal txAmount, BigDecimal targetAmount) {
        BigDecimal tx = txAmount.abs();
        BigDecimal target = targetAmount.abs();

        if (tx.compareTo(target) == 0) {
            return 1.0;
        }
        if (target.signum() == 0) {
            return 0.0;
        }
        double diffPct = tx.subtract(target).abs()
                .divide(target, 10, RoundingMode.HALF_UP)
                .doubleValue();
        return Math.max(0.0, 1.0 - diffPct / VALUE_DECAY_PCT);
    }

    static double dateScore(LocalDate txDate, LocalDate targetDate) {
        long days = Math.abs(ChronoUnit.DAYS.between(txDate, targetDate));
        return Math.max(0.0, 1.0 - days / DATE_DECAY_DAYS);
    }

    static double documentScore(String description, String document) {
        if (document == null || document.isBlank() || description == null) {
            return 0.0;
        }
        String documentDigits = document.replaceAll("\\D", "");
        if (documentDigits.isEmpty()) {
            return 0.0;
        }
        String descriptionDigits = description.replaceAll("\\D", "");
        return descriptionDigits.contains(documentDigits) ? 1.0 : 0.0;
    }

    /** ADR-0004 §2: só concilia sozinho se o melhor candidato é bom E inequívoco. */
    public static boolean isAutoReconcilable(double bestScore, double secondBestScore) {
        return bestScore >= AUTO_RECONCILE_MIN_SCORE
                && (bestScore - secondBestScore) >= AUTO_RECONCILE_MIN_MARGIN;
    }
}
