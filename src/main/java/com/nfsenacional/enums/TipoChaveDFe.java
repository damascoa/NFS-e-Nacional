package com.nfsenacional.enums;

/**
 * Documento fiscal a que se refere a chave de DF-e do Repositório Nacional
 * <p>Porte de {@code Nfse\Enums\TipoChaveDFe} (php-api).
 */
public enum TipoChaveDFe {
    /** NFS-e */
    NFSE(1, "NFS-e"),
    /** NF-e */
    NFE(2, "NF-e"),
    /** CT-e */
    CTE(3, "CT-e"),
    /** Outro */
    OUTRO(9, "Outro");

    private final int codigo;
    private final String descricao;

    TipoChaveDFe(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoChaveDFe fromCodigo(int codigo) {
        for (TipoChaveDFe v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoChaveDFe: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
