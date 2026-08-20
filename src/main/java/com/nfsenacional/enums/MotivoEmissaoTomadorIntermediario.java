package com.nfsenacional.enums;

/**
 * Motivo da emissão da DPS pelo Tomador ou Intermediário Baseado no schema: TSMotivoEmisTI
 * <p>Porte de {@code Nfse\Enums\MotivoEmissaoTomadorIntermediario} (php-api).
 */
public enum MotivoEmissaoTomadorIntermediario {
    /** Rejeição de NFS-e emitida pelo prestador */
    REJEICAO("4", "Rejeição de NFS-e emitida pelo prestador");

    private final String codigo;
    private final String descricao;

    MotivoEmissaoTomadorIntermediario(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static MotivoEmissaoTomadorIntermediario fromCodigo(String codigo) {
        for (MotivoEmissaoTomadorIntermediario v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para MotivoEmissaoTomadorIntermediario: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
