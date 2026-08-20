package com.nfsenacional.xml;

import com.nfsenacional.dto.nfse.EmitenteData;
import com.nfsenacional.dto.nfse.EnderecoEmitenteData;
import com.nfsenacional.dto.nfse.IbscbsNfseData;
import com.nfsenacional.dto.nfse.InfEventoData;
import com.nfsenacional.dto.nfse.InfNfseData;
import com.nfsenacional.dto.nfse.NfseData;
import com.nfsenacional.dto.nfse.ValoresNfseData;
import com.nfsenacional.enums.AmbienteGerador;
import com.nfsenacional.enums.CodigoStatus;
import com.nfsenacional.enums.ProcessoEmissao;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Interpreta o XML de retorno da Sefin (NFS-e emitida, ou confirmação de evento) num
 * {@link NfseData}.
 * <p>
 * Porte funcional de {@code Nfse\Xml\NfseXmlParser} (php-api) — o PHP converte o XML pra array via
 * SimpleXML→JSON e deixa a hidratação recursiva (via os atributos {@code #[MapFrom]} de cada DTO)
 * pra biblioteca de DTO fazer sozinha. Aqui, sem esse mecanismo reflexivo, a extração é explícita:
 * navega o DOM por tag, escopada sempre ao elemento pai — nunca busca global no documento — porque
 * o XML de resposta tem tags de mesmo nome repetidas em ramos diferentes (ex: {@code xLgr}/{@code nro}
 * aparecem tanto dentro de {@code emit/enderNac} quanto dentro da DPS original ecoada em
 * {@code infNFSe/DPS/infDPS/prest}).
 * <p>
 * <b>Escopo desta versão</b> (ver TASKS.md, Etapa 5): extrai todos os campos de
 * {@link InfNfseData} exceto {@link InfNfseData#getDps()} (a DPS original ecoada dentro da
 * resposta) — que fica {@code null} por ora, já que o chamador normalmente já tem o
 * {@code DpsData} original em mãos (foi ele quem montou e enviou). Reconstituir esse campo exigiria
 * um "builder inverso" do tamanho do {@link DpsXmlBuilder}; não implementado ainda.
 *
 * @author Renato
 */
public class NfseXmlParser {

    public NfseData parse(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

            Element root = doc.getDocumentElement();

            NfseData.NfseDataBuilder builder = NfseData.builder()
                    .versao(root.getAttribute("versao"))
                    .nfseXml(xml);

            Element infNfseEl = firstChild(root, "infNFSe");
            if (infNfseEl != null) {
                builder.infNfse(parseInfNfse(infNfseEl));
            }

            Element infEventoEl = firstChild(root, "infEvento");
            if (infEventoEl != null) {
                builder.infEvento(parseInfEvento(infEventoEl));
            }

            return builder.build();
        } catch (Exception e) {
            throw new NfseXmlException("Falha ao interpretar XML da NFS-e: " + e.getMessage(), e);
        }
    }

    private InfNfseData parseInfNfse(Element el) {
        InfNfseData.InfNfseDataBuilder b = InfNfseData.builder()
                .id(el.getAttribute("Id"))
                .numeroNfse(text(el, "nNFSe"))
                .numeroDfse(text(el, "nDFSe"))
                .codigoVerificacao(text(el, "cVerif"))
                .dataProcessamento(text(el, "dhProc"))
                .versaoAplicativo(text(el, "verAplic"))
                .localEmissao(text(el, "xLocEmi"))
                .localPrestacao(text(el, "xLocPrestacao"))
                .codigoLocalIncidencia(text(el, "cLocIncid"))
                .nomeLocalIncidencia(text(el, "xLocIncid"))
                .descricaoTributacaoNacional(text(el, "xTribNac"))
                .descricaoTributacaoMunicipal(text(el, "xTribMun"))
                .descricaoNbs(text(el, "xNBS"))
                .tipoEmissao(intOrNull(text(el, "tpEmis")))
                .outrasInformacoes(text(el, "xOutInf"));

        String ambGer = text(el, "ambGer");
        if (ambGer != null) {
            b.ambienteGerador(AmbienteGerador.fromCodigo(ambGer));
        }
        String procEmi = text(el, "procEmi");
        if (procEmi != null) {
            b.processoEmissao(ProcessoEmissao.fromCodigo(procEmi));
        }
        String cStat = text(el, "cStat");
        if (cStat != null) {
            b.codigoStatus(CodigoStatus.fromCodigo(Integer.parseInt(cStat)));
        }

        // dps: não reconstituído nesta versão (ver javadoc da classe).

        Element emitEl = firstChild(el, "emit");
        if (emitEl != null) {
            b.emitente(parseEmitente(emitEl));
        }

        Element valoresEl = firstChild(el, "valores");
        if (valoresEl != null) {
            b.valores(parseValoresNfse(valoresEl));
        }

        Element ibscbsEl = firstChild(el, "IBSCBS");
        if (ibscbsEl != null) {
            b.ibscbs(parseIbscbsNfse(ibscbsEl));
        }

        return b.build();
    }

    private InfEventoData parseInfEvento(Element el) {
        InfEventoData.InfEventoDataBuilder b = InfEventoData.builder()
                .id(el.getAttribute("Id"))
                .versaoAplicativo(text(el, "verAplic"))
                .ambiente(intOrNull(text(el, "ambGer")))
                .numeroSequencialEvento(intOrNull(text(el, "nSeqEvento")))
                .dataHoraProcessamento(text(el, "dhProc"))
                .numeroDfe(text(el, "nDFe"));
        // pedRegEvento: eco do pedido de registro de evento — não reconstituído nesta versão
        // (mesma decisão de escopo do campo `dps` em InfNfseData).
        return b.build();
    }

    private EmitenteData parseEmitente(Element el) {
        EmitenteData.EmitenteDataBuilder b = EmitenteData.builder()
                .cnpj(text(el, "CNPJ"))
                .cpf(text(el, "CPF"))
                .inscricaoMunicipal(text(el, "IM"))
                .nome(text(el, "xNome"))
                .nomeFantasia(text(el, "xFant"))
                .telefone(text(el, "fone"))
                .email(text(el, "email"));

        Element enderEl = firstChild(el, "enderNac");
        if (enderEl != null) {
            b.endereco(EnderecoEmitenteData.builder()
                    .logradouro(text(enderEl, "xLgr"))
                    .numero(text(enderEl, "nro"))
                    .complemento(text(enderEl, "xCpl"))
                    .bairro(text(enderEl, "xBairro"))
                    .codigoMunicipio(text(enderEl, "cMun"))
                    .uf(text(enderEl, "UF"))
                    .cep(text(enderEl, "CEP"))
                    .build());
        }

        return b.build();
    }

    private ValoresNfseData parseValoresNfse(Element el) {
        return ValoresNfseData.builder()
                .valorCalculadoDeducaoReducao(doubleOrNull(text(el, "vCalcDR")))
                .tipoBeneficioMunicipal(intOrNull(text(el, "tpBM")))
                .valorCalculadoBeneficioMunicipal(doubleOrNull(text(el, "vCalcBM")))
                .baseCalculo(doubleOrNull(text(el, "vBC")))
                .aliquotaAplicada(doubleOrNull(text(el, "pAliqAplic")))
                .valorIssqn(doubleOrNull(text(el, "vISSQN")))
                .valorTotalRetido(doubleOrNull(text(el, "vTotalRet")))
                .valorLiquido(doubleOrNull(text(el, "vLiq")))
                .build();
    }

    private IbscbsNfseData parseIbscbsNfse(Element el) {
        Element valoresEl = firstChild(el, "valores");
        Element ufEl = valoresEl != null ? firstChild(valoresEl, "uf") : null;
        Element munEl = valoresEl != null ? firstChild(valoresEl, "mun") : null;
        Element fedEl = valoresEl != null ? firstChild(valoresEl, "fed") : null;

        Element totCibsEl = firstChild(el, "totCIBS");
        Element gIbsEl = totCibsEl != null ? firstChild(totCibsEl, "gIBS") : null;
        Element gIbsCredPresEl = gIbsEl != null ? firstChild(gIbsEl, "gIBSCredPres") : null;
        Element gIbsUfTotEl = gIbsEl != null ? firstChild(gIbsEl, "gIBSUFTot") : null;
        Element gIbsMunTotEl = gIbsEl != null ? firstChild(gIbsEl, "gIBSMunTot") : null;
        Element gCbsEl = totCibsEl != null ? firstChild(totCibsEl, "gCBS") : null;
        Element gCbsCredPresEl = gCbsEl != null ? firstChild(gCbsEl, "gCBSCredPres") : null;
        Element gTribRegularEl = totCibsEl != null ? firstChild(totCibsEl, "gTribRegular") : null;
        Element gTribCompraGovEl = totCibsEl != null ? firstChild(totCibsEl, "gTribCompraGov") : null;

        return IbscbsNfseData.builder()
                .codigoLocalidadeIncidencia(text(el, "cLocalidadeIncid"))
                .nomeLocalidadeIncidencia(text(el, "xLocalidadeIncid"))
                .percentualRedutor(doubleOrNull(text(el, "pRedutor")))
                .baseCalculo(doubleOrNull(text(valoresEl, "vBC")))
                .valorCalculadoReembolso(doubleOrNull(text(valoresEl, "vCalcReeRepRes")))
                .aliquotaIbsUf(doubleOrNull(text(ufEl, "pIBSUF")))
                .percentualReducaoAliquotaUf(doubleOrNull(text(ufEl, "pRedAliqUF")))
                .aliquotaEfetivaUf(doubleOrNull(text(ufEl, "pAliqEfetUF")))
                .aliquotaIbsMunicipal(doubleOrNull(text(munEl, "pIBSMun")))
                .percentualReducaoAliquotaMunicipal(doubleOrNull(text(munEl, "pRedAliqMun")))
                .aliquotaEfetivaMunicipal(doubleOrNull(text(munEl, "pAliqEfetMun")))
                .aliquotaCbs(doubleOrNull(text(fedEl, "pCBS")))
                .percentualReducaoAliquotaCbs(doubleOrNull(text(fedEl, "pRedAliqCBS")))
                .aliquotaEfetivaCbs(doubleOrNull(text(fedEl, "pAliqEfetCBS")))
                .valorTotalNota(doubleOrNull(text(totCibsEl, "vTotNF")))
                .valorTotalIbs(doubleOrNull(text(gIbsEl, "vIBSTot")))
                .aliquotaCreditoPresumidoIbs(doubleOrNull(text(gIbsCredPresEl, "pCredPresIBS")))
                .valorCreditoPresumidoIbs(doubleOrNull(text(gIbsCredPresEl, "vCredPresIBS")))
                .valorDiferimentoUf(doubleOrNull(text(gIbsUfTotEl, "vDifUF")))
                .valorIbsUf(doubleOrNull(text(gIbsUfTotEl, "vIBSUF")))
                .valorDiferimentoMunicipal(doubleOrNull(text(gIbsMunTotEl, "vDifMun")))
                .valorIbsMunicipal(doubleOrNull(text(gIbsMunTotEl, "vIBSMun")))
                .aliquotaCreditoPresumidoCbs(doubleOrNull(text(gCbsCredPresEl, "pCredPresCBS")))
                .valorCreditoPresumidoCbs(doubleOrNull(text(gCbsCredPresEl, "vCredPresCBS")))
                .valorDiferimentoCbs(doubleOrNull(text(gCbsEl, "vDifCBS")))
                .valorCbs(doubleOrNull(text(gCbsEl, "vCBS")))
                .aliquotaEfetivaRegularIbsUf(doubleOrNull(text(gTribRegularEl, "pAliqEfeRegIBSUF")))
                .valorTributacaoRegularIbsUf(doubleOrNull(text(gTribRegularEl, "vTribRegIBSUF")))
                .aliquotaEfetivaRegularIbsMunicipal(doubleOrNull(text(gTribRegularEl, "pAliqEfeRegIBSMun")))
                .valorTributacaoRegularIbsMunicipal(doubleOrNull(text(gTribRegularEl, "vTribRegIBSMun")))
                .aliquotaEfetivaRegularCbs(doubleOrNull(text(gTribRegularEl, "pAliqEfeRegCBS")))
                .valorTributacaoRegularCbs(doubleOrNull(text(gTribRegularEl, "vTribRegCBS")))
                .aliquotaCompraGovIbsUf(doubleOrNull(text(gTribCompraGovEl, "pIBSUF")))
                .valorCompraGovIbsUf(doubleOrNull(text(gTribCompraGovEl, "vIBSUF")))
                .aliquotaCompraGovIbsMunicipal(doubleOrNull(text(gTribCompraGovEl, "pIBSMun")))
                .valorCompraGovIbsMunicipal(doubleOrNull(text(gTribCompraGovEl, "vIBSMun")))
                .aliquotaCompraGovCbs(doubleOrNull(text(gTribCompraGovEl, "pCBS")))
                .valorCompraGovCbs(doubleOrNull(text(gTribCompraGovEl, "vCBS")))
                .build();
    }

    /** Primeiro filho direto com o nome de tag informado (não busca em descendentes mais profundos). */
    private Element firstChild(Element parent, String tagName) {
        if (parent == null) {
            return null;
        }
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && tagName.equals(node.getLocalName())) {
                return (Element) node;
            }
        }
        return null;
    }

    /** Texto do primeiro filho direto com essa tag, ou {@code null} se ausente/vazio. */
    private String text(Element parent, String tagName) {
        Element child = firstChild(parent, tagName);
        if (child == null) {
            return null;
        }
        String content = child.getTextContent();
        return (content == null || content.trim().isEmpty()) ? null : content.trim();
    }

    private Integer intOrNull(String value) {
        return value == null ? null : Integer.parseInt(value);
    }

    private Double doubleOrNull(String value) {
        return value == null ? null : Double.parseDouble(value);
    }
}
