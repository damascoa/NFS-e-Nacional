package com.nfsenacional.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupportUtilsTest {

    @Test
    void geraIdDpsComTamanhoEFormatoCorretos() {
        String id = IdGenerator.generateDpsId("12345678000199", "3550308", "1", "1001");
        assertEquals(45, id.length());
        assertTrue(id.startsWith("DPS3550308"));
        // Tipo de inscrição federal: 2 = CNPJ (14 dígitos)
        assertEquals('2', id.charAt(10));
    }

    @Test
    void geraIdDpsComCpfUsaTipoInscricao1() {
        String id = IdGenerator.generateDpsId("11122233344", "3550308", "1", "1");
        assertEquals('1', id.charAt(10));
    }

    @Test
    void calculaImpostoArredondado() {
        assertEquals(50.03, TaxCalculator.calculate(1000.50, 5.0));
    }

    @Test
    void formataCpfCnpjCep() {
        assertEquals("111.222.333-44", CpfCnpjFormatter.formatCpf("11122233344"));
        assertEquals("12.345.678/0001-99", CpfCnpjFormatter.formatCnpj("12345678000199"));
        assertEquals("01310-100", CpfCnpjFormatter.formatCep("01310100"));
        assertEquals("11122233344", CpfCnpjFormatter.unformat("111.222.333-44"));
    }

    @RepeatedTest(20)
    void geraCpfValido() {
        String cpf = CpfCnpjGenerator.generateCpf(false);
        assertEquals(11, cpf.length());
        assertTrue(isCpfValido(cpf), "CPF gerado deve ter dígitos verificadores corretos: " + cpf);
    }

    @RepeatedTest(20)
    void geraCnpjValido() {
        String cnpj = CpfCnpjGenerator.generateCnpj(false);
        assertEquals(14, cnpj.length());
        assertTrue(isCnpjValido(cnpj), "CNPJ gerado deve ter dígitos verificadores corretos: " + cnpj);
    }

    /** Validação independente (não usa o próprio gerador) — confere os dígitos verificadores do CPF. */
    private boolean isCpfValido(String cpf) {
        int[] d = cpf.chars().map(c -> c - '0').toArray();
        int soma1 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += d[i] * (10 - i);
        }
        int dv1 = 11 - (soma1 % 11);
        if (dv1 >= 10) {
            dv1 = 0;
        }
        int soma2 = 0;
        for (int i = 0; i < 10; i++) {
            soma2 += d[i] * (11 - i);
        }
        int dv2 = 11 - (soma2 % 11);
        if (dv2 >= 10) {
            dv2 = 0;
        }
        return d[9] == dv1 && d[10] == dv2;
    }

    /** Validação independente do CNPJ. */
    private boolean isCnpjValido(String cnpj) {
        int[] d = cnpj.chars().map(c -> c - '0').toArray();
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma1 = 0;
        for (int i = 0; i < 12; i++) {
            soma1 += d[i] * pesos1[i];
        }
        int dv1 = 11 - (soma1 % 11);
        if (dv1 >= 10) {
            dv1 = 0;
        }
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma2 = 0;
        for (int i = 0; i < 13; i++) {
            soma2 += d[i] * pesos2[i];
        }
        int dv2 = 11 - (soma2 % 11);
        if (dv2 >= 10) {
            dv2 = 0;
        }
        return d[12] == dv1 && d[13] == dv2;
    }
}
