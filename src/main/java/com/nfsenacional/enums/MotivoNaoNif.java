package com.nfsenacional.enums;

/**
 * Motivo da não informação do NIF Baseado no schema: TSCNaoNIF
 * <p>Porte de {@code Nfse\Enums\MotivoNaoNif} (php-api).
 */
public enum MotivoNaoNif {
    /** Dispensado do NIF */
    DISPENSADO("1", "Dispensado do NIF"),
    /** Não exigência do NIF */
    NAO_EXIGENCIA("2", "Não exigência do NIF");

    private final String codigo;
    private final String descricao;

    MotivoNaoNif(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static MotivoNaoNif fromCodigo(String codigo) {
        for (MotivoNaoNif v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para MotivoNaoNif: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
