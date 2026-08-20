package com.nfsenacional.support;

/**
 * Porte de {@code Nfse\Support\CpfCnpjFormatter} (php-api).
 *
 * @author Renato
 */
public class CpfCnpjFormatter {

    private CpfCnpjFormatter() {
    }

    public static String formatCpf(String cpf) {
        String digits = unformat(cpf);
        return digits.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static String formatCnpj(String cnpj) {
        String digits = unformat(cnpj);
        return digits.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    public static String unformat(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    public static String formatCep(String cep) {
        String digits = unformat(cep);
        return digits.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }
}
