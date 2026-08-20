package com.nfsenacional.danfse;

import com.nfsenacional.dto.nfse.CodigoServicoData;
import com.nfsenacional.dto.nfse.DescontoData;
import com.nfsenacional.dto.nfse.DeducaoReducaoData;
import com.nfsenacional.dto.nfse.EmitenteData;
import com.nfsenacional.dto.nfse.EnderecoData;
import com.nfsenacional.dto.nfse.EnderecoEmitenteData;
import com.nfsenacional.dto.nfse.InfDpsData;
import com.nfsenacional.dto.nfse.InfNfseData;
import com.nfsenacional.dto.nfse.NfseData;
import com.nfsenacional.dto.nfse.PrestadorData;
import com.nfsenacional.dto.nfse.RegimeTributarioData;
import com.nfsenacional.dto.nfse.ServicoData;
import com.nfsenacional.dto.nfse.TomadorData;
import com.nfsenacional.dto.nfse.TributacaoData;
import com.nfsenacional.dto.nfse.ValorServicoPrestadoData;
import com.nfsenacional.dto.nfse.ValoresData;
import com.nfsenacional.dto.nfse.ValoresNfseData;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Gera o DANFSe (representação em PDF da NFS-e) a partir de {@link NfseData}.
 * <p>
 * Porte 1:1 de {@code danfse-php/src/Danfse.php} + {@code src/Mappers/NfseTemplateMapper.php}
 * (pacote {@code danfse-php-main}), adaptado pra usar os DTOs tipados deste SDK em vez da
 * navegação dinâmica por caminho (dot-path) que o PHP precisa fazer sobre array/objeto genérico —
 * aqui os campos já são conhecidos em tempo de compilação.
 * <p>
 * O motor de renderização (JasperReports) é nativo Java — diferente do restante do porte, aqui é
 * o {@code .jrxml} original que é reaproveitado direto (copiado para
 * {@code src/main/resources/com/nfsenacional/danfse/nfse-nacional.jrxml}), só a montagem dos
 * parâmetros precisou ser portada.
 * <p>
 * <b>Fontes:</b> o template usa {@code fontName="Arial"} sem nenhum atributo
 * {@code pdfFontName}/{@code isPdfEmbedded} explícito. O lado PHP resolve isso embutindo fontes
 * TCPDF via {@code quilhasoft/jasperphp}; aqui não há equivalente. Na exportação Java, o
 * {@link JasperExportManager} tenta mapear "Arial" para uma fonte PDF padrão e, se não encontrar
 * extensão de fonte registrada, cai (sem erro, apenas log) para a fonte padrão configurada em
 * {@code net.sf.jasperreports.default.pdf.font.name} (Helvetica, visualmente muito próxima de
 * Arial). Isso é uma limitação cosmética conhecida, não um bug funcional — ver TASKS.md.
 *
 * @author Renato
 */
public class DanfseGenerator {

    private static final String TEMPLATE_RESOURCE = "/com/nfsenacional/danfse/nfse-nacional.jrxml";
    private static final String IMAGEM_NFSE_RESOURCE = "/com/nfsenacional/danfse/nfse-nacional.png";

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private JasperReport reportCompilado;
    private Path imagemNfseExtraida;

    /**
     * Renderiza o DANFSe em PDF pra um {@link OutputStream}.
     *
     * @param nfseData NFS-e já emitida (retorno de {@code ContribuinteService.emitir()}/{@code consultar()}).
     * @param extras   parâmetros extras/overrides (ex: {@code "imgPrefeitura"} com o brasão da prefeitura,
     *                 {@code "linkPublico"} pra sobrescrever a URL de consulta pública). Pode ser vazio.
     */
    public void gerarPdf(NfseData nfseData, Map<String, Object> extras, OutputStream destino) {
        Map<String, Object> parametros = parametros(nfseData, extras);

        List<Map<String, Object>> linhas = new ArrayList<>();
        linhas.add(Collections.singletonMap("id", (Object) 1));

        try {
            JasperReport report = reportCompilado();
            JasperPrint print = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(linhas));
            JasperExportManager.exportReportToPdfStream(print, destino);
        } catch (JRException e) {
            throw new DanfseGeneracaoException("Falha ao gerar o DANFSe.", e);
        }
    }

    /** Conveniência: gera o PDF já como {@code byte[]}. */
    public byte[] gerarPdf(NfseData nfseData, Map<String, Object> extras) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        gerarPdf(nfseData, extras, buffer);
        return buffer.toByteArray();
    }

    /** Conveniência: grava o PDF direto num arquivo. */
    public void gerarPdf(NfseData nfseData, Map<String, Object> extras, Path destino) {
        try (OutputStream out = Files.newOutputStream(destino)) {
            gerarPdf(nfseData, extras, out);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gravar o DANFSe em " + destino, e);
        }
    }

    private synchronized JasperReport reportCompilado() {
        if (reportCompilado == null) {
            try (InputStream in = getClass().getResourceAsStream(TEMPLATE_RESOURCE)) {
                if (in == null) {
                    throw new IllegalStateException("Template não encontrado no classpath: " + TEMPLATE_RESOURCE);
                }
                reportCompilado = JasperCompileManager.compileReport(in);
            } catch (JRException | IOException e) {
                throw new DanfseGeneracaoException("Falha ao compilar o template do DANFSe.", e);
            }
        }
        return reportCompilado;
    }

    /**
     * Extrai um recurso de imagem embutido no jar pra um arquivo temporário — o elemento
     * {@code <image>} do jrxml espera um caminho de arquivo (ou URL) em String, não um recurso de
     * classpath diretamente.
     */
    private synchronized Path imagemNfseExtraida() {
        if (imagemNfseExtraida == null || !Files.exists(imagemNfseExtraida)) {
            imagemNfseExtraida = extrairImagem(IMAGEM_NFSE_RESOURCE);
        }
        return imagemNfseExtraida;
    }

    /**
     * Gera o QR Code do link de consulta pública como PNG num arquivo temporário.
     * <p>
     * Substitui o componente nativo {@code jr:QRCode} do JasperReports (só disponível a partir da
     * 6.0) — este SDK fixa a versão 5.6.0 de propósito, pra não colidir com a versão que outros
     * sistemas que importam esta lib já possam usar. O jrxml foi adaptado pra receber o QR como
     * imagem comum (parâmetro {@code imgQrCode}) em vez do componente barcode4j.
     */
    private Path gerarQrCode(String conteudo) {
        try {
            String texto = conteudo == null ? "" : conteudo;
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> dicas = new HashMap<>();
            dicas.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            dicas.put(EncodeHintType.MARGIN, 1);
            BitMatrix matriz = writer.encode(texto, BarcodeFormat.QR_CODE, 240, 240, dicas);

            Path arquivo = Files.createTempFile("danfse-qrcode-", ".png");
            arquivo.toFile().deleteOnExit();
            MatrixToImageWriter.writeToPath(matriz, "PNG", arquivo);
            return arquivo;
        } catch (WriterException | IOException e) {
            throw new DanfseGeneracaoException("Falha ao gerar o QR Code do DANFSe.", e);
        }
    }

    private Path extrairImagem(String resource) {
        try (InputStream in = getClass().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Imagem não encontrada no classpath: " + resource);
            }
            Path arquivo = Files.createTempFile("danfse-", "-" + resource.substring(resource.lastIndexOf('/') + 1));
            arquivo.toFile().deleteOnExit();
            Files.copy(in, arquivo, StandardCopyOption.REPLACE_EXISTING);
            return arquivo;
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao extrair imagem do DANFSe: " + resource, e);
        }
    }

    // ------------------------------------------------------------------------------------------
    // Montagem dos parâmetros — porte de NfseTemplateMapper::parameters()
    // ------------------------------------------------------------------------------------------

    private Map<String, Object> parametros(NfseData nfseData, Map<String, Object> extras) {
        InfNfseData infNfse = nfseData.getInfNfse() != null ? nfseData.getInfNfse() : new InfNfseData();
        InfDpsData dps = (infNfse.getDps() != null && infNfse.getDps().getInfDps() != null)
                ? infNfse.getDps().getInfDps() : new InfDpsData();
        EmitenteData emitente = infNfse.getEmitente() != null ? infNfse.getEmitente() : new EmitenteData();
        PrestadorData prestador = dps.getPrestador() != null ? dps.getPrestador() : new PrestadorData();
        TomadorData tomador = dps.getTomador() != null ? dps.getTomador() : new TomadorData();
        ServicoData servico = dps.getServico() != null ? dps.getServico() : new ServicoData();
        CodigoServicoData codigoServico = servico.getCodigoServico() != null ? servico.getCodigoServico() : new CodigoServicoData();
        RegimeTributarioData regime = prestador.getRegimeTributario() != null ? prestador.getRegimeTributario() : new RegimeTributarioData();
        ValoresData valoresDps = dps.getValores() != null ? dps.getValores() : new ValoresData();
        ValorServicoPrestadoData servicoPrestado = valoresDps.getValorServicoPrestado() != null
                ? valoresDps.getValorServicoPrestado() : new ValorServicoPrestadoData();
        DeducaoReducaoData deducaoReducao = valoresDps.getDeducaoReducao() != null ? valoresDps.getDeducaoReducao() : new DeducaoReducaoData();
        DescontoData desconto = valoresDps.getDesconto() != null ? valoresDps.getDesconto() : new DescontoData();
        TributacaoData tributacao = valoresDps.getTributacao() != null ? valoresDps.getTributacao() : new TributacaoData();
        ValoresNfseData valores = infNfse.getValores() != null ? infNfse.getValores() : new ValoresNfseData();
        EnderecoEmitenteData emitenteEndereco = emitente.getEndereco() != null ? emitente.getEndereco() : new EnderecoEmitenteData();
        EnderecoData tomadorEndereco = tomador.getEndereco() != null ? tomador.getEndereco() : new EnderecoData();
        EnderecoData prestadorEndereco = prestador.getEndereco();

        String chave = digits(coalesce(infNfse.getId()));
        String municipioCodigo = digits(coalesce(dps.getCodigoLocalEmissao(), emitenteEndereco.getCodigoMunicipio()));

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cdChave", chave);
        parametros.put("linkPublico", linkPublicoConsulta(chave));
        parametros.put("nNFSe", str(infNfse.getNumeroNfse()));
        parametros.put("dCompet", data(dps.getDataCompetencia()));
        parametros.put("dhEmi", dataHora(coalesce(dps.getDataEmissao(), infNfse.getDataProcessamento())));
        parametros.put("nDPS", str(dps.getNumeroDps()));
        parametros.put("serie", str(dps.getSerie()));
        parametros.put("emitCNPJ", cpfCnpj(coalesce(emitente.getCnpj(), emitente.getCpf())));
        parametros.put("emitxNome", str(coalesce(emitente.getNome(), prestador.getNome())));
        parametros.put("emitIM", str(coalesce(emitente.getInscricaoMunicipal(), prestador.getInscricaoMunicipal())));
        parametros.put("emitfone", str(coalesce(emitente.getTelefone(), prestador.getTelefone())));
        parametros.put("emitemail", str(coalesce(emitente.getEmail(), prestador.getEmail())));
        parametros.put("emitxLgr", emitente.getEndereco() != null
                ? enderecoEmitente(emitente.getEndereco())
                : enderecoTomador(prestadorEndereco));
        parametros.put("emitxMun", str(infNfse.getLocalEmissao()));
        parametros.put("emitCEP", str(coalesce(emitenteEndereco.getCep(), prestadorEndereco != null ? prestadorEndereco.getCep() : null)));
        parametros.put("tomaCNPJ", cpfCnpj(coalesce(tomador.getCnpj(), tomador.getCpf())));
        parametros.put("tomaCPF", cpfCnpj(tomador.getCpf()));
        parametros.put("tomaxNome", str(tomador.getNome()));
        parametros.put("tomaIM", str(tomador.getInscricaoMunicipal()));
        parametros.put("tomafone", str(tomador.getTelefone()));
        parametros.put("tomaemail", str(tomador.getEmail()));
        parametros.put("tomaxLgr", enderecoTomador(tomador.getEndereco()));
        parametros.put("tomaxMun", str(tomadorEndereco.getCodigoMunicipio()));
        parametros.put("tomaCEP", str(tomadorEndereco.getCep()));
        parametros.put("xDescServ", str(codigoServico.getDescricaoServico()));
        parametros.put("cTribNac", str(codigoServico.getCodigoTributacaoNacional()));
        parametros.put("cTribMun", str(codigoServico.getCodigoTributacaoMunicipal()));
        parametros.put("cPaisPrestacao", str(servico.getLocalPrestacao() != null ? servico.getLocalPrestacao().getCodigoPaisPrestacao() : null));
        parametros.put("xLocPrestacao", str(infNfse.getLocalPrestacao()));
        parametros.put("xLocIncid", str(infNfse.getNomeLocalIncidencia()));
        parametros.put("xNBS", str(coalesce(infNfse.getDescricaoNbs(), codigoServico.getCodigoNbs())));
        parametros.put("vServ", money(servicoPrestado.getValorServico()));
        parametros.put("vLiq", money(valores.getValorLiquido()));
        parametros.put("vBC", money(valores.getBaseCalculo()));
        parametros.put("pAliq", money(coalesce(valores.getAliquotaAplicada(), tributacao.getAliquota())));
        parametros.put("vISSQN", money(valores.getValorIssqn()));
        parametros.put("vTotTribFed", money(tributacao.getValorTotalTributosFederais()));
        parametros.put("vTotTribEst", money(tributacao.getValorTotalTributosEstaduais()));
        parametros.put("vTotTribMun", money(tributacao.getValorTotalTributosMunicipais()));
        parametros.put("vCalcDR", money(coalesce(valores.getValorCalculadoDeducaoReducao(), deducaoReducao.getValorDeducaoReducao())));
        parametros.put("vCalcBM", money(valores.getValorCalculadoBeneficioMunicipal()));
        parametros.put("vDescIncond", money(desconto.getValorDescontoIncondicionado()));
        parametros.put("vDescCond", money(desconto.getValorDescontoCondicionado()));
        parametros.put("vTotalRet", money(valores.getValorTotalRetido()));
        parametros.put("vPis", money(tributacao.getValorPis()));
        parametros.put("vCofins", money(tributacao.getValorCofins()));
        // vRetCP (valor retido de contribuição previdenciária) não existe em TributacaoData deste
        // SDK (o php-api de origem também não expõe esse campo na DTO de tributação — a Sefin não
        // retorna esse valor calculado; deixado vazio de propósito, igual ao PHP quando ausente).
        parametros.put("vRetCP", "");
        parametros.put("vRetCSLL", money(tributacao.getValorRetidoCsll()));
        parametros.put("vRetIRRF", money(tributacao.getValorRetidoIrrf()));
        parametros.put("vRetPisCofins", "");
        parametros.put("opSimpNac", str(regime.getOpcaoSimplesNacional()));
        parametros.put("regApTribSN", str(regime.getRegimeApuracaoTributosSn()));
        parametros.put("regEspTrib", str(regime.getRegimeEspecialTributacao()));
        parametros.put("tribISSQN", str(tributacao.getTributacaoIssqn()));
        parametros.put("tpImunidade", str(tributacao.getTipoImunidade()));
        parametros.put("tpSusp", str(tributacao.getTipoSuspensao()));
        parametros.put("tpRetISSQN", str(tributacao.getTipoRetencaoIssqn()));
        parametros.put("tpRetPisCofins", str(tributacao.getTipoRetencaoPisCofins()));
        parametros.put("tpBM", str(valores.getTipoBeneficioMunicipal()));
        parametros.put("nProcesso", str(tributacao.getNumeroProcessoSuspensao()));
        parametros.put("cPaisResult", str(servico.getComercioExterior() != null ? servico.getComercioExterior().getCodigoPaisExportador() : null));
        parametros.put("xRetCP", "");
        parametros.put("infCompl", str(infNfse.getOutrasInformacoes()));
        parametros.put("imgNfse", imagemNfseExtraida().toAbsolutePath().toString());
        parametros.put("imgQrCode", gerarQrCode((String) parametros.get("linkPublico")).toAbsolutePath().toString());
        parametros.put("imgPrefeitura", "");
        parametros.put("municipioCodigo", municipioCodigo);
        parametros.put("municipioEmissao", municipioEmissao(infNfse, codigoServico, emitenteEndereco));

        // extras/overrides por último — permite sobrescrever qualquer parâmetro (ex: imgPrefeitura
        // real, linkPublico customizado), igual ao "mapParameter()" do PHP.
        if (extras != null) {
            parametros.putAll(extras);
        }

        return parametros;
    }

    private String enderecoEmitente(EnderecoEmitenteData endereco) {
        if (endereco == null) {
            return "";
        }
        return endereco(endereco.getLogradouro(), endereco.getNumero(), endereco.getComplemento(), endereco.getBairro());
    }

    private String enderecoTomador(EnderecoData endereco) {
        if (endereco == null) {
            return "";
        }
        return endereco(endereco.getLogradouro(), endereco.getNumero(), endereco.getComplemento(), endereco.getBairro());
    }

    private String endereco(String logradouro, String numero, String complemento, String bairro) {
        List<String> partes = new ArrayList<>();
        for (String parte : new String[]{logradouro, numero, complemento, bairro}) {
            if (parte != null && !parte.isEmpty()) {
                partes.add(parte);
            }
        }
        return String.join(", ", partes);
    }

    private String linkPublicoConsulta(String chave) {
        if (chave == null || chave.isEmpty()) {
            return "";
        }
        try {
            return "https://www.nfse.gov.br/ConsultaPublica/?tpc=1&chave=" + URLEncoder.encode(chave, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 sempre disponível.", e);
        }
    }

    private String municipioEmissao(InfNfseData infNfse, CodigoServicoData codigoServico, EnderecoEmitenteData emitenteEndereco) {
        String codigoTributacaoNacional = digits(codigoServico.getCodigoTributacaoNacional());
        if (codigoTributacaoNacional != null && codigoTributacaoNacional.startsWith("99")) {
            return "";
        }
        String municipio = str(coalesce(infNfse.getLocalEmissao(), emitenteEndereco.getCodigoMunicipio()));
        String estado = str(emitenteEndereco.getUf());
        if (municipio.isEmpty() || estado.isEmpty()) {
            return "";
        }
        return municipio + " / " + estado;
    }

    // ------------------------------------------------------------------------------------------
    // Helpers de formatação — porte dos métodos privados de NfseTemplateMapper
    // ------------------------------------------------------------------------------------------

    private static String str(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Enum) {
            // Enums deste SDK expõem getCodigo(); usa toString() (já sobrescrito nas enums geradas
            // pra retornar o código) como equivalente ao BackedEnum::$value do PHP.
            return value.toString();
        }
        return String.valueOf(value);
    }

    private static String digits(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\D+", "");
    }

    private static String money(Double value) {
        if (value == null) {
            return "";
        }
        String formatado = String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", value);
        return formatado;
    }

    private static String cpfCnpj(String value) {
        String digitos = digits(value);
        if (digitos.length() == 11) {
            return digitos.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        if (digitos.length() == 14) {
            return digitos.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return digitos;
    }

    private static String data(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "";
        }
        try {
            return LocalDate.parse(isoDate.length() > 10 ? isoDate.substring(0, 10) : isoDate).format(DATA_BR);
        } catch (DateTimeParseException e) {
            return isoDate;
        }
    }

    private static String dataHora(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return "";
        }
        try {
            String semOffset = isoDateTime.replaceAll("[+-]\\d{2}:\\d{2}$|Z$", "");
            return LocalDateTime.parse(semOffset).format(DATA_HORA_BR);
        } catch (DateTimeParseException e) {
            return isoDateTime;
        }
    }

    private static String coalesce(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isEmpty()) {
                return valor;
            }
        }
        return null;
    }

    private static Double coalesce(Double... valores) {
        for (Double valor : valores) {
            if (valor != null) {
                return valor;
            }
        }
        return null;
    }
}
