package com.nfsenacional.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Porte de {@code Nfse\Support\TaxCalculator} (php-api).
 *
 * @author Renato
 */
public class TaxCalculator {

    private TaxCalculator() {
    }

    /**
     * @param baseCalculation base de cálculo
     * @param aliquot         alíquota em porcentagem (ex: 5.0 para 5%)
     * @return valor do imposto arredondado para 2 casas decimais
     */
    public static double calculate(double baseCalculation, double aliquot) {
        return BigDecimal.valueOf(baseCalculation * (aliquot / 100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
