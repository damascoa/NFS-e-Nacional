package com.nfsenacional.enums;

/**
 * Tipo de dedução/redução Baseado no schema: TSTpDedRed
 * <p>Porte de {@code Nfse\Enums\TipoDeducaoReducao} (php-api).
 */
public enum TipoDeducaoReducao {
    /** Materiais */
    MATERIAIS("1", "Materiais"),
    /** Subempreitada */
    SUBEMPREITADA("2", "Subempreitada"),
    /** Reembolso */
    REEMBOLSO("3", "Reembolso"),
    /** Outros */
    OUTROS("99", "Outros");

    private final String codigo;
    private final String descricao;

    TipoDeducaoReducao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoDeducaoReducao fromCodigo(String codigo) {
        for (TipoDeducaoReducao v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoDeducaoReducao: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
