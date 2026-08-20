package com.nfsenacional.enums;

/**
 * Ambiente Gerador da NFS-e Baseado no schema: TSAmbGer
 * <p>Porte de {@code Nfse\Enums\AmbienteGerador} (php-api).
 */
public enum AmbienteGerador {
    /** Sistema Próprio do Município */
    MUNICIPIO("1", "Sistema Próprio do Município"),
    /** Sefin Nacional */
    SEFIN_NACIONAL("2", "Sefin Nacional");

    private final String codigo;
    private final String descricao;

    AmbienteGerador(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static AmbienteGerador fromCodigo(String codigo) {
        for (AmbienteGerador v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para AmbienteGerador: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
