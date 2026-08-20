package com.nfsenacional.enums;

/**
 * Tipo de NSU para consulta no ADN
 * <p>Porte de {@code Nfse\Enums\TipoNsu} (php-api).
 */
public enum TipoNsu {
    /** Recepção */
    RECEPCAO("RECEPCAO", "Recepção"),
    /** Distribuição */
    DISTRIBUICAO("DISTRIBUICAO", "Distribuição"),
    /** Geral */
    GERAL("GERAL", "Geral"),
    /** MEI */
    MEI("MEI", "MEI");

    private final String codigo;
    private final String descricao;

    TipoNsu(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoNsu fromCodigo(String codigo) {
        for (TipoNsu v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoNsu: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
