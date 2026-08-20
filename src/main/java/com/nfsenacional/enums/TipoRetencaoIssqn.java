package com.nfsenacional.enums;

/**
 * Tipo de Retenção do ISSQN
 * <p>Porte de {@code Nfse\Enums\TipoRetencaoIssqn} (php-api).
 */
public enum TipoRetencaoIssqn {
    /** Não Retido */
    NAO_RETIDO(1, "Não Retido"),
    /** Retido pelo Tomador */
    RETIDO_TOMADOR(2, "Retido pelo Tomador"),
    /** Retido pelo Intermediário */
    RETIDO_INTERMEDIARIO(3, "Retido pelo Intermediário");

    private final int codigo;
    private final String descricao;

    TipoRetencaoIssqn(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoRetencaoIssqn fromCodigo(int codigo) {
        for (TipoRetencaoIssqn v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoRetencaoIssqn: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
