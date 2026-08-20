package com.nfsenacional.enums;

/**
 * Indicador a respeito do destinatário dos serviços
 * <p>Porte de {@code Nfse\Enums\IndicadorDestinatario} (php-api).
 */
public enum IndicadorDestinatario {
    /** O destinatário é o próprio tomador/adquirente identificado na NFS-e (tomador = adquirente = destinatário) */
    DESTINATARIO_TOMADOR(0, "O destinatário é o próprio tomador/adquirente identificado na NFS-e (tomador = adquirente = destinatário)"),
    /** O destinatário não é o próprio adquirente, podendo ser outra pessoa, física ou jurídica (ou equiparada), ou um estabelecimento diferente do indicado como tomador (tomador = adquirente ≠ destinatário) */
    DESTINATARIO_DIVERSO(1, "O destinatário não é o próprio adquirente, podendo ser outra pessoa, física ou jurídica (ou equiparada), ou um estabelecimento diferente do indicado como tomador (tomador = adquirente ≠ destinatário)");

    private final int codigo;
    private final String descricao;

    IndicadorDestinatario(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static IndicadorDestinatario fromCodigo(int codigo) {
        for (IndicadorDestinatario v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para IndicadorDestinatario: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
