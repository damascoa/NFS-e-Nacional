package com.nfsenacional.enums;

/**
 * Tipo de Operação com Entes Governamentais ou outros serviços sobre bens imóveis
 * <p>Porte de {@code Nfse\Enums\TipoOperacaoRtc} (php-api).
 */
public enum TipoOperacaoRtc {
    /** Fornecimento com pagamento posterior */
    FORNECIMENTO_PAGAMENTO_POSTERIOR(1, "Fornecimento com pagamento posterior"),
    /** Recebimento do pagamento com fornecimento já realizado */
    RECEBIMENTO_FORNECIMENTO_REALIZADO(2, "Recebimento do pagamento com fornecimento já realizado"),
    /** Fornecimento com pagamento já realizado */
    FORNECIMENTO_PAGAMENTO_REALIZADO(3, "Fornecimento com pagamento já realizado"),
    /** Recebimento do pagamento com fornecimento posterior */
    RECEBIMENTO_FORNECIMENTO_POSTERIOR(4, "Recebimento do pagamento com fornecimento posterior"),
    /** Fornecimento e recebimento do pagamento concomitantes */
    FORNECIMENTO_RECEBIMENTO_CONCOMITANTES(5, "Fornecimento e recebimento do pagamento concomitantes");

    private final int codigo;
    private final String descricao;

    TipoOperacaoRtc(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoOperacaoRtc fromCodigo(int codigo) {
        for (TipoOperacaoRtc v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoOperacaoRtc: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
