package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.EmitenteDPS;
import com.nfsenacional.enums.MotivoEmissaoTomadorIntermediario;
import com.nfsenacional.enums.TipoAmbiente;

/**
 * Porte de {@code Nfse\Dto\Nfse\InfDpsData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfDpsData {

    /** Identificador da tag a ser assinada. Formado por: "DPS" + Cód.Mun.Emi. + Tipo Inscrição + Inscrição + Série + Número. */
    private String id;

    /** Ambiente de emissão. 1 - Produção 2 - Homologação */
    private TipoAmbiente tipoAmbiente;

    /** Data e hora de emissão da DPS. Formato: AAAA-MM-DDThh:mm:ssTZD */
    private String dataEmissao;

    /** Versão do aplicativo emissor. */
    private String versaoAplicativo;

    /** Série da DPS. */
    private String serie;

    /** Número da DPS. */
    private String numeroDps;

    /** Data de competência da DPS. Formato: AAAA-MM-DD */
    private String dataCompetencia;

    /** Tipo de emitente da DPS. 1 - Prestador 2 - Tomador 3 - Intermediário */
    private EmitenteDPS tipoEmitente;

    /** Código do município emissor da DPS (IBGE). */
    private String codigoLocalEmissao;

    /** Motivo da emissão da DPS pelo Tomador ou Intermediário. Obrigatório se tpEmit = 2 ou 3. */
    private MotivoEmissaoTomadorIntermediario motivoEmissaoTomadorIntermediario;

    /** Chave de acesso da NFS-e rejeitada. Obrigatório se cMotivoEmisTI = 4. */
    private String chaveNfseRejeitada;

    /** Informações de substituição de NFS-e. */
    private SubstituicaoData substituicao;

    /** Dados do prestador do serviço. */
    private PrestadorData prestador;

    /** Dados do tomador do serviço. */
    private TomadorData tomador;

    /** Dados do intermediário do serviço. */
    private IntermediarioData intermediario;

    /** Dados do serviço prestado. */
    private ServicoData servico;

    /** Grupo de informações declaradas pelo emitente referentes ao IBS e à CBS. */
    private IbscbsData ibscbs;

    /** Valores do serviço e tributos. */
    private ValoresData valores;

}
