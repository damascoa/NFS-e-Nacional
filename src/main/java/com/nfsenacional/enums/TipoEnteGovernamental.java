package com.nfsenacional.enums;

/**
 * Tipo de ente governamental, para administração pública direta e suas autarquias e fundações
 * <p>Porte de {@code Nfse\Enums\TipoEnteGovernamental} (php-api).
 */
public enum TipoEnteGovernamental {
    /** União */
    UNIAO(1, "União"),
    /** Estado */
    ESTADO(2, "Estado"),
    /** Distrito Federal */
    DISTRITO_FEDERAL(3, "Distrito Federal"),
    /** Município */
    MUNICIPIO(4, "Município");

    private final int codigo;
    private final String descricao;

    TipoEnteGovernamental(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoEnteGovernamental fromCodigo(int codigo) {
        for (TipoEnteGovernamental v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoEnteGovernamental: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
