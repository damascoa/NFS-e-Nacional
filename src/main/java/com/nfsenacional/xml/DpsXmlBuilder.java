package com.nfsenacional.xml;

import com.nfsenacional.dto.nfse.DpsData;
import com.nfsenacional.dto.nfse.EnderecoData;
import com.nfsenacional.dto.nfse.IbscbsData;
import com.nfsenacional.dto.nfse.InfDpsData;
import com.nfsenacional.dto.nfse.PrestadorData;
import com.nfsenacional.dto.nfse.ReembolsoDocumentoData;
import com.nfsenacional.dto.nfse.ServicoData;
import com.nfsenacional.dto.nfse.TomadorData;
import com.nfsenacional.dto.nfse.ValoresData;
import com.nfsenacional.enums.OpcaoSimplesNacional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Locale;

/**
 * Monta o XML da DPS (Declaração de Prestação de Serviço) a partir de {@link DpsData}, na ordem
 * exata das tags exigida pelo XSD do Sistema Nacional NFS-e.
 * <p>
 * Porte 1:1 de {@code Nfse\Xml\DpsXmlBuilder} (php-api, 552 linhas) — mesma estrutura e mesma
 * ordem de chamadas do original; a única mudança de comportamento é o uso de
 * {@link String#format} no lugar de {@code number_format} do PHP para os campos monetários (ambos
 * produzem o mesmo resultado: 2 casas decimais, ponto como separador).
 *
 * @author Renato
 */
public class DpsXmlBuilder {

    private Document dom;

    public String build(DpsData dps) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            this.dom = dbf.newDocumentBuilder().newDocument();

            Element root = dom.createElementNS("http://www.sped.fazenda.gov.br/nfse", "DPS");
            root.setAttribute("versao", String.valueOf(dps.getVersao()));
            dom.appendChild(root);

            Element infDps = dom.createElement("infDPS");
            infDps.setAttribute("Id", String.valueOf(dps.getInfDps().getId()));
            root.appendChild(infDps);

            buildInfDps(infDps, dps.getInfDps());

            return serialize(root);
        } catch (Exception e) {
            throw new NfseXmlException("Falha ao montar XML da DPS: " + e.getMessage(), e);
        }
    }

    private void buildInfDps(Element parent, InfDpsData data) {
        appendElement(parent, "tpAmb", data.getTipoAmbiente());
        appendElement(parent, "dhEmi", data.getDataEmissao());
        appendElement(parent, "verAplic", data.getVersaoAplicativo());
        appendElement(parent, "serie", data.getSerie());
        appendElement(parent, "nDPS", data.getNumeroDps());
        appendElement(parent, "dCompet", data.getDataCompetencia());
        appendElement(parent, "tpEmit", data.getTipoEmitente());
        appendElement(parent, "cMotivoEmisTI", data.getMotivoEmissaoTomadorIntermediario());
        appendElement(parent, "chNFSeRej", data.getChaveNfseRejeitada());
        appendElement(parent, "cLocEmi", data.getCodigoLocalEmissao());

        if (data.getSubstituicao() != null) {
            Element subst = dom.createElement("subst");
            appendElement(subst, "chSubstda", data.getSubstituicao().getChaveNfseSubstituida());
            appendElement(subst, "cMotivo", data.getSubstituicao().getCodigoMotivo());
            appendElement(subst, "xMotivo", data.getSubstituicao().getDescricaoMotivo());
            parent.appendChild(subst);
        }

        if (data.getPrestador() != null) {
            buildPrestador(parent, data.getPrestador());
        }

        if (data.getTomador() != null) {
            buildTomador(parent, data.getTomador());
        }

        if (data.getIntermediario() != null) {
            Element interm = dom.createElement("interm");
            appendElement(interm, "CNPJ", data.getIntermediario().getCnpj());
            appendElement(interm, "CPF", data.getIntermediario().getCpf());
            appendElement(interm, "NIF", data.getIntermediario().getNif());
            appendElement(interm, "cNaoNIF", data.getIntermediario().getCodigoNaoNif());
            appendElement(interm, "CAEPF", data.getIntermediario().getCaepf());
            appendElement(interm, "IM", data.getIntermediario().getInscricaoMunicipal());
            appendElement(interm, "xNome", data.getIntermediario().getNome());

            if (data.getIntermediario().getEndereco() != null) {
                buildEndereco(interm, data.getIntermediario().getEndereco());
            }

            appendElement(interm, "fone", data.getIntermediario().getTelefone());
            appendElement(interm, "email", data.getIntermediario().getEmail());
            parent.appendChild(interm);
        }

        if (data.getServico() != null) {
            buildServico(parent, data.getServico());
        }

        if (data.getValores() != null) {
            buildValores(parent, data.getValores(), data.getPrestador());
        }

        if (data.getIbscbs() != null) {
            buildIbscbs(parent, data.getIbscbs());
        }
    }

    private void buildPrestador(Element parent, PrestadorData data) {
        Element prest = dom.createElement("prest");
        appendElement(prest, "CNPJ", data.getCnpj());
        appendElement(prest, "CPF", data.getCpf());
        appendElement(prest, "NIF", data.getNif());
        appendElement(prest, "cNaoNIF", data.getCodigoNaoNif());
        appendElement(prest, "CAEPF", data.getCaepf());
        appendElement(prest, "IM", data.getInscricaoMunicipal());
        appendElement(prest, "xNome", data.getNome());

        if (data.getEndereco() != null) {
            buildEndereco(prest, data.getEndereco());
        }

        appendElement(prest, "fone", data.getTelefone());
        appendElement(prest, "email", data.getEmail());

        if (data.getRegimeTributario() != null) {
            Element regTrib = dom.createElement("regTrib");
            appendElement(regTrib, "opSimpNac", data.getRegimeTributario().getOpcaoSimplesNacional());
            appendElement(regTrib, "regApTribSN", data.getRegimeTributario().getRegimeApuracaoTributosSn());
            appendElement(regTrib, "regEspTrib", data.getRegimeTributario().getRegimeEspecialTributacao());
            prest.appendChild(regTrib);
        }

        parent.appendChild(prest);
    }

    private void buildTomador(Element parent, TomadorData data) {
        Element toma = dom.createElement("toma");
        appendElement(toma, "CNPJ", data.getCnpj());
        appendElement(toma, "CPF", data.getCpf());
        appendElement(toma, "NIF", data.getNif());
        appendElement(toma, "cNaoNIF", data.getCodigoNaoNif());
        appendElement(toma, "CAEPF", data.getCaepf());
        appendElement(toma, "IM", data.getInscricaoMunicipal());
        appendElement(toma, "xNome", data.getNome());

        if (data.getEndereco() != null) {
            buildEndereco(toma, data.getEndereco());
        }

        appendElement(toma, "fone", data.getTelefone());
        appendElement(toma, "email", data.getEmail());
        parent.appendChild(toma);
    }

    /**
     * O endereço simples (obra, evento e imóvel) traz o CEP direto no lugar do grupo endNac e não
     * informa o país no endereço no exterior.
     */
    private void buildEndereco(Element parent, EnderecoData data) {
        buildEndereco(parent, data, false);
    }

    private void buildEndereco(Element parent, EnderecoData data, boolean enderecoSimples) {
        Element end = dom.createElement("end");

        if (enderecoSimples) {
            if (data.getEnderecoExterior() == null) {
                appendElement(end, "CEP", data.getCep());
            }
        } else if (hasText(data.getCodigoMunicipio()) || hasText(data.getCep())) {
            Element endNac = dom.createElement("endNac");
            appendElement(endNac, "cMun", data.getCodigoMunicipio());
            appendElement(endNac, "CEP", data.getCep());
            end.appendChild(endNac);
        }

        if (data.getEnderecoExterior() != null) {
            Element endExt = dom.createElement("endExt");
            if (!enderecoSimples) {
                appendElement(endExt, "cPais", data.getEnderecoExterior().getCodigoPais());
            }
            appendElement(endExt, "cEndPost", data.getEnderecoExterior().getCodigoEnderecamentoPostal());
            appendElement(endExt, "xCidade", data.getEnderecoExterior().getCidade());
            appendElement(endExt, "xEstProvReg", data.getEnderecoExterior().getEstadoProvinciaRegiao());
            end.appendChild(endExt);
        }

        appendElement(end, "xLgr", data.getLogradouro());
        appendElement(end, "nro", data.getNumero());
        appendElement(end, "xCpl", data.getComplemento());
        appendElement(end, "xBairro", data.getBairro());

        parent.appendChild(end);
    }

    private void buildServico(Element parent, ServicoData data) {
        Element serv = dom.createElement("serv");

        if (data.getLocalPrestacao() != null) {
            Element locPrest = dom.createElement("locPrest");
            if (hasText(data.getLocalPrestacao().getCodigoLocalPrestacao())) {
                appendElement(locPrest, "cLocPrestacao", data.getLocalPrestacao().getCodigoLocalPrestacao());
            } else if (hasText(data.getLocalPrestacao().getCodigoPaisPrestacao())) {
                appendElement(locPrest, "cPaisPrestacao", data.getLocalPrestacao().getCodigoPaisPrestacao());
            }
            serv.appendChild(locPrest);
        }

        if (data.getCodigoServico() != null) {
            Element cServ = dom.createElement("cServ");
            appendElement(cServ, "cTribNac", data.getCodigoServico().getCodigoTributacaoNacional());
            appendElement(cServ, "cTribMun", data.getCodigoServico().getCodigoTributacaoMunicipal());
            appendElement(cServ, "xDescServ", data.getCodigoServico().getDescricaoServico());
            appendElement(cServ, "cNBS", data.getCodigoServico().getCodigoNbs());
            appendElement(cServ, "cIntContrib", data.getCodigoServico().getCodigoInternoContribuinte());
            serv.appendChild(cServ);
        }

        if (data.getComercioExterior() != null) {
            Element comExt = dom.createElement("comExt");
            appendElement(comExt, "mdPrestacao", data.getComercioExterior().getModoPrestacao());
            appendElement(comExt, "vincPrest", data.getComercioExterior().getVinculoPrestacao());
            appendElement(comExt, "tpMoeda", data.getComercioExterior().getTipoMoeda());
            appendElement(comExt, "vServMoeda", data.getComercioExterior().getValorServicoMoeda());
            appendElement(comExt, "mecAFComexP", data.getComercioExterior().getMecanismoApoioComexPrestador());
            appendElement(comExt, "mecAFComexT", data.getComercioExterior().getMecanismoApoioComexTomador());
            appendElement(comExt, "movTempBens", data.getComercioExterior().getMovimentacaoTemporariaBens());
            appendElement(comExt, "nDI", data.getComercioExterior().getNumeroDeclaracaoImportacao());
            appendElement(comExt, "nRE", data.getComercioExterior().getNumeroRegistroExportacao());
            appendElement(comExt, "mdic", data.getComercioExterior().getMdic());
            serv.appendChild(comExt);
        }

        if (data.getObra() != null) {
            Element obra = dom.createElement("obra");
            appendElement(obra, "inscImobFisc", data.getObra().getInscricaoImobiliariaFiscal());
            appendElement(obra, "cObra", data.getObra().getCodigoObra());
            if (data.getObra().getEndereco() != null) {
                buildEndereco(obra, data.getObra().getEndereco());
            }
            serv.appendChild(obra);
        }

        if (data.getAtividadeEvento() != null) {
            Element atvEvento = dom.createElement("atvEvento");
            appendElement(atvEvento, "xNome", data.getAtividadeEvento().getNome());
            appendElement(atvEvento, "dtIni", data.getAtividadeEvento().getDataInicio());
            appendElement(atvEvento, "dtFim", data.getAtividadeEvento().getDataFim());
            appendElement(atvEvento, "idAtvEvt", data.getAtividadeEvento().getIdAtividadeEvento());
            if (data.getAtividadeEvento().getEndereco() != null) {
                buildEndereco(atvEvento, data.getAtividadeEvento().getEndereco());
            }
            serv.appendChild(atvEvento);
        }

        if (data.getInformacaoComplemento() != null
                && (hasText(data.getInformacaoComplemento().getIdDocumentoTecnico())
                || hasText(data.getInformacaoComplemento().getDocumentoReferencia())
                || hasText(data.getInformacaoComplemento().getInformacoesComplementares()))) {
            Element infoCompl = dom.createElement("infoCompl");
            if (hasText(data.getInformacaoComplemento().getIdDocumentoTecnico())) {
                appendElement(infoCompl, "idDocTec", data.getInformacaoComplemento().getIdDocumentoTecnico());
            }
            if (hasText(data.getInformacaoComplemento().getDocumentoReferencia())) {
                appendElement(infoCompl, "docRef", data.getInformacaoComplemento().getDocumentoReferencia());
            }
            if (hasText(data.getInformacaoComplemento().getInformacoesComplementares())) {
                appendElement(infoCompl, "xInfComp", data.getInformacaoComplemento().getInformacoesComplementares());
            }
            serv.appendChild(infoCompl);
        }

        parent.appendChild(serv);
    }

    private void buildValores(Element parent, ValoresData data, PrestadorData prestador) {
        Element valores = dom.createElement("valores");

        if (data.getValorServicoPrestado() != null) {
            Element vServPrest = dom.createElement("vServPrest");
            appendElement(vServPrest, "vReceb", money(data.getValorServicoPrestado().getValorRecebido()));
            appendElement(vServPrest, "vServ", money(data.getValorServicoPrestado().getValorServico()));
            valores.appendChild(vServPrest);
        }

        if (data.getDesconto() != null) {
            Element vDescCondIncond = dom.createElement("vDescCondIncond");
            appendElement(vDescCondIncond, "vDescIncond", money(data.getDesconto().getValorDescontoIncondicionado()));
            appendElement(vDescCondIncond, "vDescCond", money(data.getDesconto().getValorDescontoCondicionado()));
            valores.appendChild(vDescCondIncond);
        }

        if (data.getDeducaoReducao() != null) {
            Element vDedRed = dom.createElement("vDedRed");
            appendElement(vDedRed, "pDR", money(data.getDeducaoReducao().getPercentualDeducaoReducao()));
            appendElement(vDedRed, "vDR", money(data.getDeducaoReducao().getValorDeducaoReducao()));

            if (data.getDeducaoReducao().getDocumentos() != null && !data.getDeducaoReducao().getDocumentos().isEmpty()) {
                Element documentos = dom.createElement("documentos");
                data.getDeducaoReducao().getDocumentos().forEach(docData -> {
                    Element doc = dom.createElement("doc");
                    appendElement(doc, "chNFSe", docData.getChaveNfse());
                    appendElement(doc, "chNFe", docData.getChaveNfe());
                    appendElement(doc, "tpDedRed", docData.getTipoDeducaoReducao());
                    appendElement(doc, "xDescOutDed", docData.getDescricaoOutrasDeducoes());
                    appendElement(doc, "dEmiDoc", docData.getDataEmissaoDocumento());
                    appendElement(doc, "vDedutivelRedutivel", money(docData.getValorDedutivelRedutivel()));
                    appendElement(doc, "vDeducaoReducao", money(docData.getValorDeducaoReducao()));
                    documentos.appendChild(doc);
                });
                vDedRed.appendChild(documentos);
            }

            valores.appendChild(vDedRed);
        }

        if (data.getTributacao() != null) {
            Element trib = dom.createElement("trib");

            Element tribMun = dom.createElement("tribMun");
            appendElement(tribMun, "tribISSQN", data.getTributacao().getTributacaoIssqn());
            appendElement(tribMun, "tpImunidade", data.getTributacao().getTipoImunidade());

            if (data.getTributacao().getTipoSuspensao() != null) {
                Element exigSusp = dom.createElement("exigSusp");
                appendElement(exigSusp, "tpSusp", data.getTributacao().getTipoSuspensao());
                appendElement(exigSusp, "nProcesso", data.getTributacao().getNumeroProcessoSuspensao());
                tribMun.appendChild(exigSusp);
            }

            if (data.getTributacao().getBeneficioMunicipal() != null) {
                Element bm = dom.createElement("BM");
                appendElement(bm, "pRedBCBM", money(data.getTributacao().getBeneficioMunicipal().getPercentualReducaoBcBm()));
                appendElement(bm, "vRedBCBM", money(data.getTributacao().getBeneficioMunicipal().getValorReducaoBcBm()));
                tribMun.appendChild(bm);
            }

            appendElement(tribMun, "tpRetISSQN", data.getTributacao().getTipoRetencaoIssqn());
            appendElement(tribMun, "pAliq", money(data.getTributacao().getAliquota()));

            trib.appendChild(tribMun);

            boolean hasPiscofins = data.getTributacao().getCstPisCofins() != null;
            boolean hasRetencoesFed = data.getTributacao().getValorRetidoIrrf() != null
                    || data.getTributacao().getValorRetidoCsll() != null;

            if (hasPiscofins || hasRetencoesFed) {
                Element tribFed = dom.createElement("tribFed");

                if (hasPiscofins) {
                    Element piscofins = dom.createElement("piscofins");
                    appendElement(piscofins, "CST", data.getTributacao().getCstPisCofins());
                    appendElement(piscofins, "vBCPisCofins", money(data.getTributacao().getBaseCalculoPisCofins()));
                    appendElement(piscofins, "pAliqPis", money(data.getTributacao().getAliquotaPis()));
                    appendElement(piscofins, "pAliqCofins", money(data.getTributacao().getAliquotaCofins()));
                    appendElement(piscofins, "vPis", money(data.getTributacao().getValorPis()));
                    appendElement(piscofins, "vCofins", money(data.getTributacao().getValorCofins()));
                    appendElement(piscofins, "tpRetPisCofins", data.getTributacao().getTipoRetencaoPisCofins());
                    tribFed.appendChild(piscofins);
                }

                appendElement(tribFed, "vRetIRRF", money(data.getTributacao().getValorRetidoIrrf()));
                appendElement(tribFed, "vRetCSLL", money(data.getTributacao().getValorRetidoCsll()));
                // vRetContPrev: placeholder, igual ao PHP (campo ainda não suportado pelo DTO de origem).

                trib.appendChild(tribFed);
            }

            // isSimplesNacional calculado no PHP mas não usado em lugar nenhum do método (dead code
            // de origem) — mantido de fora aqui de propósito, não é usado ali também.
            boolean isSimplesNacional = prestador != null && prestador.getRegimeTributario() != null
                    && prestador.getRegimeTributario().getOpcaoSimplesNacional() == OpcaoSimplesNacional.ME_EPP;

            Element totTrib = null;

            if (data.getTributacao().getPercentualTotalTributosSN() != null && data.getTributacao().getPercentualTotalTributosSN() != 0) {
                totTrib = dom.createElement("totTrib");
                appendElement(totTrib, "pTotTribSN", money(data.getTributacao().getPercentualTotalTributosSN()));
            } else if (data.getTributacao().getValorTotalTributosFederais() != null
                    || data.getTributacao().getValorTotalTributosEstaduais() != null
                    || data.getTributacao().getValorTotalTributosMunicipais() != null) {
                totTrib = dom.createElement("totTrib");
                Element vTotTrib = dom.createElement("vTotTrib");
                appendElement(vTotTrib, "vTotTribFed", money(data.getTributacao().getValorTotalTributosFederais()));
                appendElement(vTotTrib, "vTotTribEst", money(data.getTributacao().getValorTotalTributosEstaduais()));
                appendElement(vTotTrib, "vTotTribMun", money(data.getTributacao().getValorTotalTributosMunicipais()));
                totTrib.appendChild(vTotTrib);
            } else if (data.getTributacao().getPercentualTotalTributosFederais() != null
                    || data.getTributacao().getPercentualTotalTributosEstaduais() != null
                    || data.getTributacao().getPercentualTotalTributosMunicipais() != null) {
                totTrib = dom.createElement("totTrib");
                Element pTotTrib = dom.createElement("pTotTrib");
                appendElement(pTotTrib, "pTotTribFed", money(data.getTributacao().getPercentualTotalTributosFederais()));
                appendElement(pTotTrib, "pTotTribEst", money(data.getTributacao().getPercentualTotalTributosEstaduais()));
                appendElement(pTotTrib, "pTotTribMun", money(data.getTributacao().getPercentualTotalTributosMunicipais()));
                totTrib.appendChild(pTotTrib);
            } else if (data.getTributacao().getIndicadorTotalTributos() != null) {
                totTrib = dom.createElement("totTrib");
                appendElement(totTrib, "indTotTrib", data.getTributacao().getIndicadorTotalTributos());
            }

            if (totTrib != null) {
                trib.appendChild(totTrib);
            }

            valores.appendChild(trib);
        }

        parent.appendChild(valores);
    }

    private void buildIbscbs(Element parent, IbscbsData data) {
        Element ibscbs = dom.createElement("IBSCBS");

        appendElement(ibscbs, "finNFSe", data.getFinalidadeNfse());
        appendElement(ibscbs, "indFinal", data.getIndicadorUsoConsumoPessoal());
        appendElement(ibscbs, "cIndOp", data.getCodigoIndicadorOperacao());
        appendElement(ibscbs, "tpOper", data.getTipoOperacao());

        if (data.getChavesNfseReferenciadas() != null && !data.getChavesNfseReferenciadas().isEmpty()) {
            Element gRefNFSe = dom.createElement("gRefNFSe");
            data.getChavesNfseReferenciadas().forEach(chave -> appendElement(gRefNFSe, "refNFSe", chave));
            ibscbs.appendChild(gRefNFSe);
        }

        appendElement(ibscbs, "tpEnteGov", data.getTipoEnteGovernamental());
        appendElement(ibscbs, "indDest", data.getIndicadorDestinatario());

        if (data.getDestinatario() != null) {
            Element dest = dom.createElement("dest");
            appendElement(dest, "CNPJ", data.getDestinatario().getCnpj());
            appendElement(dest, "CPF", data.getDestinatario().getCpf());
            appendElement(dest, "NIF", data.getDestinatario().getNif());
            appendElement(dest, "cNaoNIF", data.getDestinatario().getCodigoNaoNif());
            appendElement(dest, "xNome", data.getDestinatario().getNome());

            if (data.getDestinatario().getEndereco() != null) {
                buildEndereco(dest, data.getDestinatario().getEndereco());
            }

            appendElement(dest, "fone", data.getDestinatario().getTelefone());
            appendElement(dest, "email", data.getDestinatario().getEmail());
            ibscbs.appendChild(dest);
        }

        if (data.getImovel() != null) {
            Element imovel = dom.createElement("imovel");
            appendElement(imovel, "inscImobFisc", data.getImovel().getInscricaoImobiliariaFiscal());

            if (hasText(data.getImovel().getCodigoCib())) {
                appendElement(imovel, "cCIB", data.getImovel().getCodigoCib());
            } else if (data.getImovel().getEndereco() != null) {
                buildEndereco(imovel, data.getImovel().getEndereco(), true);
            }

            ibscbs.appendChild(imovel);
        }

        Element valores = dom.createElement("valores");

        if (data.getDocumentosReembolso() != null && !data.getDocumentosReembolso().isEmpty()) {
            Element gReeRepRes = dom.createElement("gReeRepRes");
            data.getDocumentosReembolso().forEach(documento -> buildDocumentoReembolso(gReeRepRes, documento));
            valores.appendChild(gReeRepRes);
        }

        Element trib = dom.createElement("trib");
        Element gIbscbs = dom.createElement("gIBSCBS");
        appendElement(gIbscbs, "CST", data.getCst());
        appendElement(gIbscbs, "cClassTrib", data.getCodigoClassificacaoTributaria());
        appendElement(gIbscbs, "cCredPres", data.getCodigoCreditoPresumido());

        if (hasText(data.getCstTributacaoRegular())) {
            Element gTribRegular = dom.createElement("gTribRegular");
            appendElement(gTribRegular, "CSTReg", data.getCstTributacaoRegular());
            appendElement(gTribRegular, "cClassTribReg", data.getCodigoClassificacaoTributariaRegular());
            gIbscbs.appendChild(gTribRegular);
        }

        if (data.getPercentualDiferimentoUf() != null || data.getPercentualDiferimentoMunicipal() != null
                || data.getPercentualDiferimentoCbs() != null) {
            Element gDif = dom.createElement("gDif");
            appendElement(gDif, "pDifUF", money(orZero(data.getPercentualDiferimentoUf())));
            appendElement(gDif, "pDifMun", money(orZero(data.getPercentualDiferimentoMunicipal())));
            appendElement(gDif, "pDifCBS", money(orZero(data.getPercentualDiferimentoCbs())));
            gIbscbs.appendChild(gDif);
        }

        trib.appendChild(gIbscbs);
        valores.appendChild(trib);
        ibscbs.appendChild(valores);

        parent.appendChild(ibscbs);
    }

    private void buildDocumentoReembolso(Element parent, ReembolsoDocumentoData data) {
        Element documentos = dom.createElement("documentos");

        if (hasText(data.getChaveDfe())) {
            Element dFeNacional = dom.createElement("dFeNacional");
            appendElement(dFeNacional, "tipoChaveDFe", data.getTipoChaveDfe());
            appendElement(dFeNacional, "xTipoChaveDFe", data.getDescricaoTipoChaveDfe());
            appendElement(dFeNacional, "chaveDFe", data.getChaveDfe());
            documentos.appendChild(dFeNacional);
        } else if (hasText(data.getNumeroDocumentoFiscal())) {
            Element docFiscalOutro = dom.createElement("docFiscalOutro");
            appendElement(docFiscalOutro, "cMunDocFiscal", data.getCodigoMunicipioDocumentoFiscal());
            appendElement(docFiscalOutro, "nDocFiscal", data.getNumeroDocumentoFiscal());
            appendElement(docFiscalOutro, "xDocFiscal", data.getDescricaoDocumentoFiscal());
            documentos.appendChild(docFiscalOutro);
        } else if (hasText(data.getNumeroDocumento())) {
            Element docOutro = dom.createElement("docOutro");
            appendElement(docOutro, "nDoc", data.getNumeroDocumento());
            appendElement(docOutro, "xDoc", data.getDescricaoDocumento());
            documentos.appendChild(docOutro);
        }

        if (data.getFornecedor() != null) {
            Element fornec = dom.createElement("fornec");
            appendElement(fornec, "CNPJ", data.getFornecedor().getCnpj());
            appendElement(fornec, "CPF", data.getFornecedor().getCpf());
            appendElement(fornec, "NIF", data.getFornecedor().getNif());
            appendElement(fornec, "cNaoNIF", data.getFornecedor().getCodigoNaoNif());
            appendElement(fornec, "xNome", data.getFornecedor().getNome());
            documentos.appendChild(fornec);
        }

        appendElement(documentos, "dtEmiDoc", data.getDataEmissaoDocumento());
        appendElement(documentos, "dtCompDoc", data.getDataCompetenciaDocumento());
        appendElement(documentos, "tpReeRepRes", data.getTipoReembolso());
        appendElement(documentos, "xTpReeRepRes", data.getDescricaoTipoReembolso());
        appendElement(documentos, "vlrReeRepRes", money(data.getValorReembolso()));

        parent.appendChild(documentos);
    }

    /** Formata valor monetário/percentual com 2 casas decimais e ponto — equivalente a {@code number_format($v, 2, '.', '')}. */
    private String money(Double value) {
        return value == null ? null : String.format(Locale.ROOT, "%.2f", value);
    }

    private double orZero(Double value) {
        return value == null ? 0d : value;
    }

    private boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }

    private void appendElement(Element parent, String name, Object value) {
        if (value == null) {
            return;
        }
        String text = (value instanceof Enum<?>) ? value.toString() : String.valueOf(value);
        if (text == null || text.isEmpty()) {
            return;
        }
        Element element = dom.createElement(name);
        element.appendChild(dom.createTextNode(text));
        parent.appendChild(element);
    }

    private String serialize(Element root) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(root), new StreamResult(sw));
        return sw.toString().replace("\n", "").replace("\r", "").replace("\t", "");
    }
}
