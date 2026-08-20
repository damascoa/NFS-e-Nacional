package com.nfsenacional.xml;

import com.nfsenacional.dto.nfse.CodigoServicoData;
import com.nfsenacional.dto.nfse.DescontoData;
import com.nfsenacional.dto.nfse.DpsData;
import com.nfsenacional.dto.nfse.EmitenteData;
import com.nfsenacional.dto.nfse.EnderecoData;
import com.nfsenacional.dto.nfse.EnderecoEmitenteData;
import com.nfsenacional.dto.nfse.EnderecoExteriorData;
import com.nfsenacional.dto.nfse.IbscbsNfseData;
import com.nfsenacional.dto.nfse.InfDpsData;
import com.nfsenacional.dto.nfse.InfEventoData;
import com.nfsenacional.dto.nfse.InfNfseData;
import com.nfsenacional.dto.nfse.LocalPrestacaoData;
import com.nfsenacional.dto.nfse.NfseData;
import com.nfsenacional.dto.nfse.PrestadorData;
import com.nfsenacional.dto.nfse.RegimeTributarioData;
import com.nfsenacional.dto.nfse.ServicoData;
import com.nfsenacional.dto.nfse.TomadorData;
import com.nfsenacional.dto.nfse.TributacaoData;
import com.nfsenacional.dto.nfse.ValorServicoPrestadoData;
import com.nfsenacional.dto.nfse.ValoresData;
import com.nfsenacional.dto.nfse.ValoresNfseData;
import com.nfsenacional.enums.AmbienteGerador;
import com.nfsenacional.enums.CodigoStatus;
import com.nfsenacional.enums.CstPisCofins;
import com.nfsenacional.enums.EmitenteDPS;
import com.nfsenacional.enums.MotivoEmissaoTomadorIntermediario;
import com.nfsenacional.enums.MotivoNaoNif;
import com.nfsenacional.enums.OpcaoSimplesNacional;
import com.nfsenacional.enums.ProcessoEmissao;
import com.nfsenacional.enums.RegimeApuracaoSN;
import com.nfsenacional.enums.RegimeEspecialTributacao;
import com.nfsenacional.enums.TipoAmbiente;
import com.nfsenacional.enums.TipoImunidade;
import com.nfsenacional.enums.TipoRetencaoIssqn;
import com.nfsenacional.enums.TipoRetencaoPisCofins;
import com.nfsenacional.enums.TipoSuspensao;
import com.nfsenacional.enums.TributacaoIssqn;
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

        Element dpsEl = firstChild(el, "DPS");
        if (dpsEl != null) {
            b.dps(parseDps(dpsEl));
        }

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

    private DpsData parseDps(Element el) {
        Element infDpsEl = firstChild(el, "infDPS");
        return DpsData.builder()
                .versao(el.getAttribute("versao"))
                .infDps(infDpsEl != null ? parseInfDps(infDpsEl) : null)
                .build();
    }

    private InfDpsData parseInfDps(Element el) {
        InfDpsData.InfDpsDataBuilder b = InfDpsData.builder()
                .id(el.getAttribute("Id"))
                .dataEmissao(text(el, "dhEmi"))
                .versaoAplicativo(text(el, "verAplic"))
                .serie(text(el, "serie"))
                .numeroDps(text(el, "nDPS"))
                .dataCompetencia(text(el, "dCompet"))
                .codigoLocalEmissao(text(el, "cLocEmi"))
                .chaveNfseRejeitada(text(el, "chNFSeRej"));

        String tpAmb = text(el, "tpAmb");
        if (tpAmb != null) {
            b.tipoAmbiente(TipoAmbiente.fromCodigo(tpAmb));
        }
        String tpEmit = text(el, "tpEmit");
        if (tpEmit != null) {
            b.tipoEmitente(EmitenteDPS.fromCodigo(tpEmit));
        }
        String cMotivoEmisTI = text(el, "cMotivoEmisTI");
        if (cMotivoEmisTI != null) {
            b.motivoEmissaoTomadorIntermediario(MotivoEmissaoTomadorIntermediario.fromCodigo(cMotivoEmisTI));
        }

        Element prestEl = firstChild(el, "prest");
        if (prestEl != null) {
            b.prestador(parsePrestador(prestEl));
        }
        Element tomaEl = firstChild(el, "toma");
        if (tomaEl != null) {
            b.tomador(parseTomador(tomaEl));
        }
        Element servEl = firstChild(el, "serv");
        if (servEl != null) {
            b.servico(parseServico(servEl));
        }
        Element valoresEl = firstChild(el, "valores");
        if (valoresEl != null) {
            b.valores(parseValoresDps(valoresEl));
        }

        return b.build();
    }

    private PrestadorData parsePrestador(Element el) {
        PrestadorData.PrestadorDataBuilder b = PrestadorData.builder()
                .cnpj(text(el, "CNPJ"))
                .cpf(text(el, "CPF"))
                .nif(text(el, "NIF"))
                .codigoNaoNif(text(el, "cNaoNIF"))
                .caepf(text(el, "CAEPF"))
                .inscricaoMunicipal(text(el, "IM"))
                .nome(text(el, "xNome"))
                .telefone(text(el, "fone"))
                .email(text(el, "email"));

        Element endEl = firstChild(el, "end");
        if (endEl != null) {
            b.endereco(parseEndereco(endEl));
        }
        Element regTribEl = firstChild(el, "regTrib");
        if (regTribEl != null) {
            b.regimeTributario(parseRegimeTributario(regTribEl));
        }

        return b.build();
    }

    private TomadorData parseTomador(Element el) {
        TomadorData.TomadorDataBuilder b = TomadorData.builder()
                .cnpj(text(el, "CNPJ"))
                .cpf(text(el, "CPF"))
                .nif(text(el, "NIF"))
                .caepf(text(el, "CAEPF"))
                .inscricaoMunicipal(text(el, "IM"))
                .nome(text(el, "xNome"))
                .telefone(text(el, "fone"))
                .email(text(el, "email"));

        String cNaoNif = text(el, "cNaoNIF");
        if (cNaoNif != null) {
            b.codigoNaoNif(MotivoNaoNif.fromCodigo(cNaoNif));
        }
        Element endEl = firstChild(el, "end");
        if (endEl != null) {
            b.endereco(parseEndereco(endEl));
        }

        return b.build();
    }

    private EnderecoData parseEndereco(Element el) {
        Element endNacEl = firstChild(el, "endNac");
        Element endExtEl = firstChild(el, "endExt");
        String cep = text(endNacEl, "CEP");
        if (cep == null) {
            cep = text(el, "CEP");
        }

        EnderecoData.EnderecoDataBuilder b = EnderecoData.builder()
                .codigoMunicipio(text(endNacEl, "cMun"))
                .cep(cep)
                .logradouro(text(el, "xLgr"))
                .numero(text(el, "nro"))
                .bairro(text(el, "xBairro"))
                .complemento(text(el, "xCpl"));

        if (endExtEl != null) {
            b.enderecoExterior(EnderecoExteriorData.builder()
                    .codigoPais(text(endExtEl, "cPais"))
                    .codigoEnderecamentoPostal(text(endExtEl, "cEndPost"))
                    .cidade(text(endExtEl, "xCidade"))
                    .estadoProvinciaRegiao(text(endExtEl, "xEstProvReg"))
                    .build());
        }

        return b.build();
    }

    private RegimeTributarioData parseRegimeTributario(Element el) {
        RegimeTributarioData.RegimeTributarioDataBuilder b = RegimeTributarioData.builder();
        String opSimpNac = text(el, "opSimpNac");
        if (opSimpNac != null) {
            b.opcaoSimplesNacional(OpcaoSimplesNacional.fromCodigo(opSimpNac));
        }
        String regApTribSN = text(el, "regApTribSN");
        if (regApTribSN != null) {
            b.regimeApuracaoTributosSn(RegimeApuracaoSN.fromCodigo(regApTribSN));
        }
        String regEspTrib = text(el, "regEspTrib");
        if (regEspTrib != null) {
            b.regimeEspecialTributacao(RegimeEspecialTributacao.fromCodigo(regEspTrib));
        }
        return b.build();
    }

    private ServicoData parseServico(Element el) {
        ServicoData.ServicoDataBuilder b = ServicoData.builder();

        Element locPrestEl = firstChild(el, "locPrest");
        if (locPrestEl != null) {
            b.localPrestacao(LocalPrestacaoData.builder()
                    .codigoLocalPrestacao(text(locPrestEl, "cLocPrestacao"))
                    .codigoPaisPrestacao(text(locPrestEl, "cPaisPrestacao"))
                    .build());
        }
        Element cServEl = firstChild(el, "cServ");
        if (cServEl != null) {
            b.codigoServico(CodigoServicoData.builder()
                    .codigoTributacaoNacional(text(cServEl, "cTribNac"))
                    .codigoTributacaoMunicipal(text(cServEl, "cTribMun"))
                    .descricaoServico(text(cServEl, "xDescServ"))
                    .codigoNbs(text(cServEl, "cNBS"))
                    .codigoInternoContribuinte(text(cServEl, "cIntContrib"))
                    .build());
        }

        return b.build();
    }

    private ValoresData parseValoresDps(Element el) {
        ValoresData.ValoresDataBuilder b = ValoresData.builder();

        Element vServPrestEl = firstChild(el, "vServPrest");
        if (vServPrestEl != null) {
            b.valorServicoPrestado(ValorServicoPrestadoData.builder()
                    .valorRecebido(doubleOrNull(text(vServPrestEl, "vReceb")))
                    .valorServico(doubleOrNull(text(vServPrestEl, "vServ")))
                    .build());
        }
        Element vDescEl = firstChild(el, "vDescCondIncond");
        if (vDescEl != null) {
            b.desconto(DescontoData.builder()
                    .valorDescontoIncondicionado(doubleOrNull(text(vDescEl, "vDescIncond")))
                    .valorDescontoCondicionado(doubleOrNull(text(vDescEl, "vDescCond")))
                    .build());
        }
        Element tribEl = firstChild(el, "trib");
        if (tribEl != null) {
            b.tributacao(parseTributacao(tribEl));
        }

        return b.build();
    }

    private TributacaoData parseTributacao(Element el) {
        TributacaoData.TributacaoDataBuilder b = TributacaoData.builder();

        Element tribMunEl = firstChild(el, "tribMun");
        if (tribMunEl != null) {
            String tribISSQN = text(tribMunEl, "tribISSQN");
            if (tribISSQN != null) {
                b.tributacaoIssqn(TributacaoIssqn.fromCodigo(Integer.parseInt(tribISSQN)));
            }
            String tpImunidade = text(tribMunEl, "tpImunidade");
            if (tpImunidade != null) {
                b.tipoImunidade(TipoImunidade.fromCodigo(Integer.parseInt(tpImunidade)));
            }
            String tpRetISSQN = text(tribMunEl, "tpRetISSQN");
            if (tpRetISSQN != null) {
                b.tipoRetencaoIssqn(TipoRetencaoIssqn.fromCodigo(Integer.parseInt(tpRetISSQN)));
            }
            b.aliquota(doubleOrNull(text(tribMunEl, "pAliq")));

            Element exigSuspEl = firstChild(tribMunEl, "exigSusp");
            if (exigSuspEl != null) {
                String tpSusp = text(exigSuspEl, "tpSusp");
                if (tpSusp != null) {
                    b.tipoSuspensao(TipoSuspensao.fromCodigo(Integer.parseInt(tpSusp)));
                }
                b.numeroProcessoSuspensao(text(exigSuspEl, "nProcesso"));
            }
        }

        Element tribFedEl = firstChild(el, "tribFed");
        if (tribFedEl != null) {
            Element piscofinsEl = firstChild(tribFedEl, "piscofins");
            if (piscofinsEl != null) {
                String cst = text(piscofinsEl, "CST");
                if (cst != null) {
                    b.cstPisCofins(CstPisCofins.fromCodigo(cst));
                }
                b.baseCalculoPisCofins(doubleOrNull(text(piscofinsEl, "vBCPisCofins")))
                        .aliquotaPis(doubleOrNull(text(piscofinsEl, "pAliqPis")))
                        .aliquotaCofins(doubleOrNull(text(piscofinsEl, "pAliqCofins")))
                        .valorPis(doubleOrNull(text(piscofinsEl, "vPis")))
                        .valorCofins(doubleOrNull(text(piscofinsEl, "vCofins")));

                String tpRetPisCofins = text(piscofinsEl, "tpRetPisCofins");
                if (tpRetPisCofins != null) {
                    b.tipoRetencaoPisCofins(TipoRetencaoPisCofins.fromCodigo(Integer.parseInt(tpRetPisCofins)));
                }
            }
            b.valorRetidoIrrf(doubleOrNull(text(tribFedEl, "vRetIRRF")))
                    .valorRetidoCsll(doubleOrNull(text(tribFedEl, "vRetCSLL")));
        }

        Element totTribEl = firstChild(el, "totTrib");
        if (totTribEl != null) {
            b.percentualTotalTributosSN(doubleOrNull(text(totTribEl, "pTotTribSN")));
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
