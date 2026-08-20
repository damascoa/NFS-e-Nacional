package com.nfsenacional.support;

/**
 * Gera o ID da DPS (Declaração de Prestação de Serviço), usado no atributo {@code Id} de
 * {@code infDPS} e assinado pelo {@code XmlSigner}.
 * <p>
 * Formato: {@code DPS} + Cód.Mun.(7) + Tipo Inscr.(1) + Inscr.Fed.(14) + Série(5) + Número(15) = 45 caracteres.
 * <p>
 * Porte de {@code Nfse\Support\IdGenerator} (php-api).
 *
 * @author Renato
 */
public class IdGenerator {

    private IdGenerator() {
    }

    /**
     * @param cpfCnpj CPF ou CNPJ do emitente
     * @param codIbge código IBGE do município de emissão (7 dígitos)
     * @param serieDps série da DPS (até 5 caracteres)
     * @param numDps   número da DPS (até 15 dígitos)
     * @return ID gerado (45 caracteres)
     */
    public static String generateDpsId(String cpfCnpj, String codIbge, String serieDps, String numDps) {
        String digits = cpfCnpj.replaceAll("\\D", "");

        StringBuilder sb = new StringBuilder();
        sb.append("DPS");
        sb.append(codIbge.length() > 7 ? codIbge.substring(0, 7) : codIbge);
        sb.append(digits.length() == 14 ? "2" : "1");
        sb.append(leftPad(digits, 14, '0'));
        sb.append(leftPad(serieDps, 5, '0'));
        sb.append(leftPad(numDps, 15, '0'));

        return sb.toString();
    }

    private static String leftPad(String value, int length, char pad) {
        StringBuilder sb = new StringBuilder(value == null ? "" : value);
        while (sb.length() < length) {
            sb.insert(0, pad);
        }
        return sb.toString();
    }
}
