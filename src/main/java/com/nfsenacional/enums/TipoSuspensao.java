package com.nfsenacional.enums;

/**
 * Tipo de Suspensão da Exigibilidade do ISSQN
 * <p>Porte de {@code Nfse\Enums\TipoSuspensao} (php-api).
 */
public enum TipoSuspensao {
    /** Suspenso por decisão judicial */
    DECISAO_JUDICIAL(1, "Suspenso por decisão judicial"),
    /** Suspenso por decisão administrativa */
    DECISAO_ADMINISTRATIVA(2, "Suspenso por decisão administrativa");

    private final int codigo;
    private final String descricao;

    TipoSuspensao(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoSuspensao fromCodigo(int codigo) {
        for (TipoSuspensao v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoSuspensao: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
