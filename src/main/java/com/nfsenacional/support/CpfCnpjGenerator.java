package com.nfsenacional.support;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Gera CPF/CNPJ matematicamente válidos — útil pra massa de teste (não gera documentos reais).
 * <p>
 * Porte de {@code Nfse\Support\CpfCnpjGenerator} (php-api).
 *
 * @author Renato
 */
public class CpfCnpjGenerator {

    private CpfCnpjGenerator() {
    }

    public static String generateCpf(boolean formatted) {
        int[] n = randomDigits(9);

        int d1 = n[8] * 2 + n[7] * 3 + n[6] * 4 + n[5] * 5 + n[4] * 6 + n[3] * 7 + n[2] * 8 + n[1] * 9 + n[0] * 10;
        d1 = 11 - mod(d1, 11);
        if (d1 >= 10) {
            d1 = 0;
        }

        int d2 = d1 * 2 + n[8] * 3 + n[7] * 4 + n[6] * 5 + n[5] * 6 + n[4] * 7 + n[3] * 8 + n[2] * 9 + n[1] * 10 + n[0] * 11;
        d2 = 11 - mod(d2, 11);
        if (d2 >= 10) {
            d2 = 0;
        }

        String cpf = digitsToString(n) + d1 + d2;
        return formatted ? CpfCnpjFormatter.formatCpf(cpf) : cpf;
    }

    public static String generateCnpj(boolean formatted) {
        int[] n = new int[12];
        for (int i = 0; i < 8; i++) {
            n[i] = ThreadLocalRandom.current().nextInt(10);
        }
        // n[8..10] = filial "0001" (0,0,0,1), matriz padrão.
        n[8] = 0;
        n[9] = 0;
        n[10] = 0;
        n[11] = 1;

        int d1 = n[11] * 2 + n[10] * 3 + n[9] * 4 + n[8] * 5 + n[7] * 6 + n[6] * 7 + n[5] * 8 + n[4] * 9 + n[3] * 2 + n[2] * 3 + n[1] * 4 + n[0] * 5;
        d1 = 11 - mod(d1, 11);
        if (d1 >= 10) {
            d1 = 0;
        }

        int d2 = d1 * 2 + n[11] * 3 + n[10] * 4 + n[9] * 5 + n[8] * 6 + n[7] * 7 + n[6] * 8 + n[5] * 9 + n[4] * 2 + n[3] * 3 + n[2] * 4 + n[1] * 5 + n[0] * 6;
        d2 = 11 - mod(d2, 11);
        if (d2 >= 10) {
            d2 = 0;
        }

        String cnpj = digitsToString(n) + d1 + d2;
        return formatted ? CpfCnpjFormatter.formatCnpj(cnpj) : cnpj;
    }

    private static int[] randomDigits(int count) {
        int[] n = new int[count];
        for (int i = 0; i < count; i++) {
            n[i] = ThreadLocalRandom.current().nextInt(10);
        }
        return n;
    }

    private static String digitsToString(int[] digits) {
        StringBuilder sb = new StringBuilder();
        for (int d : digits) {
            sb.append(d);
        }
        return sb.toString();
    }

    private static int mod(int dividendo, int divisor) {
        return dividendo - (int) (Math.floor((double) dividendo / divisor) * divisor);
    }
}
