package com.nfsenacional.enums;

/**
 * Regime Especial de Tributação Baseado no schema: TSRegEspTrib
 * <p>Porte de {@code Nfse\Enums\RegimeEspecialTributacao} (php-api).
 */
public enum RegimeEspecialTributacao {
    /** Nenhum */
    NENHUM("0", "Nenhum"),
    /** Ato Cooperado (Cooperativa) */
    ATO_COOPERADO("1", "Ato Cooperado (Cooperativa)"),
    /** Estimativa */
    ESTIMATIVA("2", "Estimativa"),
    /** Microempresa Municipal */
    MICROEMPRESA_MUNICIPAL("3", "Microempresa Municipal"),
    /** Notário ou Registrador */
    NOTARIO_OU_REGISTRADOR("4", "Notário ou Registrador"),
    /** Profissional Autônomo */
    PROFISSIONAL_AUTONOMO("5", "Profissional Autônomo"),
    /** Sociedade de Profissionais */
    SOCIEDADE_DE_PROFISSIONAIS("6", "Sociedade de Profissionais");

    private final String codigo;
    private final String descricao;

    RegimeEspecialTributacao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static RegimeEspecialTributacao fromCodigo(String codigo) {
        for (RegimeEspecialTributacao v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para RegimeEspecialTributacao: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
