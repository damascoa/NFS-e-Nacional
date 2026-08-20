package com.nfsenacional.validator;

import com.nfsenacional.dto.nfse.DpsData;
import com.nfsenacional.dto.nfse.InfDpsData;
import com.nfsenacional.dto.nfse.ReembolsoDocumentoData;
import com.nfsenacional.enums.EmitenteDPS;
import com.nfsenacional.enums.IndicadorDestinatario;
import com.nfsenacional.enums.TipoOperacaoRtc;
import com.nfsenacional.enums.TipoReembolsoRepasseRessarcimento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validações de negócio da DPS anteriores ao envio — pra falhar cedo com mensagem clara em vez de
 * deixar a Sefin rejeitar.
 * <p>
 * Porte 1:1 de {@code Nfse\Validator\DpsValidator} (php-api, 200 linhas) — mesmas regras, mesmos
 * números de regra do schema (comentados igual ao original).
 *
 * @author Renato
 */
public class DpsValidator {

    private static final List<String> CODIGOS_CONSTRUCAO = Arrays.asList(
            "070201", "070202", "070401", "070501", "070502",
            "070601", "070602", "070701", "070801", "071701", "071901"
    );

    public ValidationResult validate(DpsData dps) {
        List<String> errors = new ArrayList<>();
        InfDpsData infDps = dps.getInfDps();

        if (infDps == null) {
            return ValidationResult.failure(Arrays.asList("InfDpsData is required."));
        }

        validatePrestador(infDps, errors);
        validateTomador(infDps, errors);
        validateValores(infDps, errors);
        validateServico(infDps, errors);
        validateIbscbs(infDps, errors);

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }

    private void validatePrestador(InfDpsData infDps, List<String> errors) {
        if (infDps.getPrestador() == null) {
            errors.add("Prestador data is required.");
            return;
        }

        // Regra E0129: se o prestador não for o emitente, o endereço é obrigatório.
        if (infDps.getTipoEmitente() != EmitenteDPS.PRESTADOR) {
            if (infDps.getPrestador().getEndereco() == null) {
                errors.add("Endereço do prestador é obrigatório quando o prestador não for o emitente.");
            }
        } else {
            // Regra E0128: se o prestador for o emitente, o endereço NÃO deve ser informado — a
            // Sefin deriva do CNC. O PHP de origem deixava isso como "aviso implícito" (comentário,
            // sem erro); aqui virou validação de verdade após confirmação empírica contra a Sefin
            // real (rejeição HTTP 400 com o endereço informado, ver TASKS.md/EmitirExemplo).
            if (infDps.getPrestador().getEndereco() != null) {
                errors.add("Endereço do prestador não deve ser informado quando o prestador for o emitente "
                        + "(a Sefin deriva os dados cadastrais do CNC a partir do CNPJ/CPF).");
            }
        }
    }

    private void validateTomador(InfDpsData infDps, List<String> errors) {
        if (infDps.getTomador() == null) {
            return;
        }

        boolean isIdentified = hasText(infDps.getTomador().getCpf())
                || hasText(infDps.getTomador().getCnpj())
                || hasText(infDps.getTomador().getNif());

        if (!isIdentified) {
            return;
        }

        if (infDps.getTomador().getEndereco() == null) {
            errors.add("Endereço do tomador é obrigatório quando o tomador é identificado.");
            return;
        }

        if (hasText(infDps.getTomador().getNif())) {
            if (infDps.getTomador().getEndereco().getEnderecoExterior() == null) {
                errors.add("Endereço no exterior do tomador é obrigatório quando identificado por NIF.");
            }
        } else {
            if (!hasText(infDps.getTomador().getEndereco().getCodigoMunicipio())) {
                errors.add("Código do município do tomador é obrigatório para endereço nacional.");
            }
        }
    }

    private void validateValores(InfDpsData infDps, List<String> errors) {
        if (infDps.getValores() == null) {
            return;
        }

        double vServ = infDps.getValores().getValorServicoPrestado() != null
                ? orZero(infDps.getValores().getValorServicoPrestado().getValorServico()) : 0d;
        double vDescIncond = infDps.getValores().getDesconto() != null
                ? orZero(infDps.getValores().getDesconto().getValorDescontoIncondicionado()) : 0d;
        double vDescCond = infDps.getValores().getDesconto() != null
                ? orZero(infDps.getValores().getDesconto().getValorDescontoCondicionado()) : 0d;

        // Regra 307: vDescIncond < vServ
        if (vDescIncond > 0 && vDescIncond >= vServ) {
            errors.add("O valor do desconto incondicionado deve ser menor que o valor do serviço.");
        }

        // Regra 309: vDescCond < vServ
        if (vDescCond > 0 && vDescCond >= vServ) {
            errors.add("O valor do desconto condicionado deve ser menor que o valor do serviço.");
        }

        // Regra 303: vServ >= descIncond + vDR + vRedBCBM
        double vDR = infDps.getValores().getDeducaoReducao() != null
                ? orZero(infDps.getValores().getDeducaoReducao().getValorDeducaoReducao()) : 0d;
        double vRedBCBM = 0d;
        if (infDps.getValores().getTributacao() != null && infDps.getValores().getTributacao().getBeneficioMunicipal() != null) {
            vRedBCBM = orZero(infDps.getValores().getTributacao().getBeneficioMunicipal().getValorReducaoBcBm());
        }

        if (vServ < (vDescIncond + vDR + vRedBCBM)) {
            errors.add("O valor do serviço deve ser maior ou igual ao somatório dos valores informados para "
                    + "Desconto Incondicionado, Deduções/Reduções e Benefício Municipal.");
        }
    }

    private void validateServico(InfDpsData infDps, List<String> errors) {
        if (infDps.getServico() == null) {
            return;
        }

        String cTribNac = infDps.getServico().getCodigoServico() != null
                ? infDps.getServico().getCodigoServico().getCodigoTributacaoNacional() : null;

        // Regra 260: obra é obrigatória pra serviços de construção civil.
        if (cTribNac != null && CODIGOS_CONSTRUCAO.contains(cTribNac) && infDps.getServico().getObra() == null) {
            errors.add("O grupo de informações de obra é obrigatório para o serviço informado.");
        }

        // Regra 276: atvEvento é obrigatório pro item 12.
        if (cTribNac != null && cTribNac.startsWith("12") && infDps.getServico().getAtividadeEvento() == null) {
            errors.add("O grupo de informações de Atividade/Evento é obrigatório para o serviço informado.");
        }
    }

    private void validateIbscbs(InfDpsData infDps, List<String> errors) {
        if (infDps.getIbscbs() == null) {
            return;
        }

        // Regra 542: IBS/CBS só pode ser declarado a partir da competência 01/01/2026.
        if (infDps.getDataCompetencia() != null && infDps.getDataCompetencia().compareTo("2026-01-01") < 0) {
            errors.add("As informações de IBS/CBS só podem ser declaradas a partir da data de competência 01/01/2026.");
        }

        // Regra 324: cNBS é obrigatório quando o grupo de IBS/CBS é informado.
        String codigoNbs = infDps.getServico() != null && infDps.getServico().getCodigoServico() != null
                ? infDps.getServico().getCodigoServico().getCodigoNbs() : null;
        if (codigoNbs == null) {
            errors.add("O item da NBS é obrigatório quando o grupo de informações de IBS/CBS é informado.");
        }

        // Regra 549: gRefNFSe é obrigatório quando tpOper = 2 ou 3.
        TipoOperacaoRtc tipoOperacao = infDps.getIbscbs().getTipoOperacao();
        boolean exigeNfseReferenciada = tipoOperacao != null
                && (tipoOperacao.getCodigo() == 2 || tipoOperacao.getCodigo() == 3);
        if (exigeNfseReferenciada
                && (infDps.getIbscbs().getChavesNfseReferenciadas() == null || infDps.getIbscbs().getChavesNfseReferenciadas().isEmpty())) {
            errors.add("O grupo de NFS-e referenciadas é obrigatório quando o tipo de operação for 2 ou 3.");
        }

        // Regra 554: o destinatário só deve ser identificado quando indDest = 1.
        if (infDps.getIbscbs().getDestinatario() != null
                && infDps.getIbscbs().getIndicadorDestinatario() != IndicadorDestinatario.DESTINATARIO_DIVERSO) {
            errors.add("O destinatário do serviço só deve ser identificado quando o indicador de destinatário for 1.");
        }

        // Regra 627: os 3 primeiros dígitos do cClassTrib devem corresponder ao CST.
        if (infDps.getIbscbs().getCst() != null && infDps.getIbscbs().getCodigoClassificacaoTributaria() != null
                && !infDps.getIbscbs().getCodigoClassificacaoTributaria().startsWith(infDps.getIbscbs().getCst())) {
            errors.add("O código de classificação tributária informado não pertence ao grupo do CST de IBS/CBS informado.");
        }

        double vServ = infDps.getValores() != null && infDps.getValores().getValorServicoPrestado() != null
                ? orZero(infDps.getValores().getValorServicoPrestado().getValorServico()) : 0d;

        List<ReembolsoDocumentoData> documentos = infDps.getIbscbs().getDocumentosReembolso();
        if (documentos != null) {
            for (ReembolsoDocumentoData documento : documentos) {
                // Regra 621: xTpReeRepRes só deve ser informado quando tpReeRepRes = 99.
                if (documento.getDescricaoTipoReembolso() != null
                        && documento.getTipoReembolso() != TipoReembolsoRepasseRessarcimento.OUTROS) {
                    errors.add("A descrição do tipo de reembolso, repasse e ressarcimento só deve ser informada quando o tipo for 99.");
                }

                // Regra 618/619: a data de emissão deve ser igual ou posterior à data de competência.
                if (documento.getDataEmissaoDocumento() != null && documento.getDataCompetenciaDocumento() != null
                        && documento.getDataEmissaoDocumento().compareTo(documento.getDataCompetenciaDocumento()) < 0) {
                    errors.add("A data de emissão do documento de reembolso deve ser igual ou posterior à sua data de competência.");
                }

                // Regra 622: o valor do reembolso deve ser menor ou igual ao valor do serviço prestado.
                if (documento.getValorReembolso() != null && documento.getValorReembolso() > vServ) {
                    errors.add("O valor de reembolso, repasse e ressarcimento deve ser menor ou igual ao valor do serviço prestado.");
                }
            }
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }

    private double orZero(Double value) {
        return value == null ? 0d : value;
    }
}
