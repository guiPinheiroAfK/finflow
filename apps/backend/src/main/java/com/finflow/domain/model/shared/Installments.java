package com.finflow.domain.model.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ADR-0001 §5: divide um total em N parcelas sem perder nem sobrar centavo.
 * A última parcela absorve o resíduo de arredondamento.
 */
public final class Installments {

    private Installments() {
    }

    public static List<BigDecimal> split(BigDecimal total, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Número de parcelas deve ser positivo: " + n);
        }
        BigDecimal base = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        List<BigDecimal> parts = new ArrayList<>(Collections.nCopies(n - 1, base));
        BigDecimal last = total.subtract(base.multiply(BigDecimal.valueOf(n - 1L)));
        parts.add(last);
        return parts;
    }
}
