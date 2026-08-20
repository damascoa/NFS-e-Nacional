package com.nfsenacional.enums;

/**
 * Tipo de Pessoa (Física ou Jurídica)
 * <p>Porte de {@code Nfse\Enums\TipoPessoa} (php-api).
 */
public enum TipoPessoa {
    /** Pessoa Jurídica */
    JURIDICA(1, "Pessoa Jurídica"),
    /** Pessoa Física */
    FISICA(2, "Pessoa Física");

    private final int codigo;
    private final String descricao;

    TipoPessoa(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoPessoa fromCodigo(int codigo) {
        for (TipoPessoa v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoPessoa: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
