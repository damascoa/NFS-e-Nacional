package com.nfsenacional.enums;

/**
 * Opção pelo Simples Nacional Baseado no schema: TSOpSimpNac
 * <p>Porte de {@code Nfse\Enums\OpcaoSimplesNacional} (php-api).
 */
public enum OpcaoSimplesNacional {
    /** Não Optante */
    NAO_OPTANTE("1", "Não Optante"),
    /** Optante - Microempreendedor Individual (MEI) */
    MEI("2", "Optante - Microempreendedor Individual (MEI)"),
    /** Optante - Microempresa ou Empresa de Pequeno Porte (ME/EPP) */
    ME_EPP("3", "Optante - Microempresa ou Empresa de Pequeno Porte (ME/EPP)");

    private final String codigo;
    private final String descricao;

    OpcaoSimplesNacional(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static OpcaoSimplesNacional fromCodigo(String codigo) {
        for (OpcaoSimplesNacional v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para OpcaoSimplesNacional: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
