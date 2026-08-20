package com.nfsenacional.enums;

/**
 * Processo de Emissão da DPS Baseado no schema: TSProcEmissao
 * <p>Porte de {@code Nfse\Enums\ProcessoEmissao} (php-api).
 */
public enum ProcessoEmissao {
    /** Emissão com aplicativo do contribuinte (via Web Service) */
    WEB_SERVICE("1", "Emissão com aplicativo do contribuinte (via Web Service)"),
    /** Emissão com aplicativo disponibilizado pelo fisco (Web) */
    WEB_FISCO("2", "Emissão com aplicativo disponibilizado pelo fisco (Web)"),
    /** Emissão com aplicativo disponibilizado pelo fisco (App) */
    APP_FISCO("3", "Emissão com aplicativo disponibilizado pelo fisco (App)");

    private final String codigo;
    private final String descricao;

    ProcessoEmissao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static ProcessoEmissao fromCodigo(String codigo) {
        for (ProcessoEmissao v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para ProcessoEmissao: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
