# TASKS — NFS-e Nacional Java SDK

Framework standalone (biblioteca, `com.nfsenacional:nfse-nacional-java`) — porte do SDK oficial
PHP (`nfse-nacional/nfse-php`) para integração com o Sistema Nacional NFS-e
(`sefin.nfse.gov.br`). Autenticação por certificado digital A1 (mTLS), sem OAuth.

Fonte de referência: `nfse-nacional/nfse-php` (código original em
`C:\Users\Renato\Documents\Projects\Metre\MetreFinanceiro\src\php-api`).

Plano completo/detalhado de cada etapa: ver o `TASKS-NFSE.md` original em
`MetreFinanceiro` (escopo mais amplo, incluía integração num sistema específico —
aqui o escopo é só a biblioteca, sem Etapa "integração no Metre").

## Progresso

- [x] **Etapa 0 — Esqueleto.** `pom.xml` (Java 8, Lombok, Gson, OkHttp — sem dependência extra pra assinatura/certificado, tudo nativo do JDK). Pacote `com.nfsenacional.*`.
- [x] **Etapa 1 — Enums (28 classes).** Gerados a partir de `src/php-api/src/Enums/*.php` — `codigo` + `descricao`, `fromCodigo()`, `toString()`. Compilado OK.
- [x] **Etapa 2 — DTOs (63 classes).** 51 da árvore DPS/NFS-e (`dto/nfse/`) + 12 de transporte HTTP (`dto/http/`). Lombok (`@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`). Compilado OK. Campos `array` do PHP mapeados pra `List<Tipo>` usando o `@var Tipo[]` do docblock de origem.
- [x] **Etapa 3 — Certificado + assinatura XML.**
  - `signer/NfseCertificate.java` — carrega PFX via `KeyStore` PKCS12 nativo, valida expiração e força mínima de chave (2048 bits), expõe `PrivateKey`/`X509Certificate`.
  - `signer/XmlSigner.java` — assina uma tag (enveloped XMLDSig) usando `javax.xml.crypto.dsig` (API pública do JDK, não classes internas). **Diferença de design vs o PHP**: o `.php` de origem tem uma inconsistência (declara C14N "inclusive" na tag mas canonicaliza com `exclusive: true` internamente); esta implementação é internamente consistente (inclusive nos dois pontos) — documentado no javadoc da classe.
  - **Verificado de verdade**, não só compilado: `XmlSignerTest` gera um PFX de teste via `keytool`, assina um XML de exemplo e valida a assinatura de volta pela própria API `javax.xml.crypto.dsig` (`signature.validate()` retorna `true`). `mvn test` passa.
  - Achado/corrigido durante a verificação: parsing do XML **precisa ser namespace-aware** (`DocumentBuilderFactory.setNamespaceAware(true)`) — sem isso a validação falha silenciosamente (digest não bate). Documento real da NFS-e nacional declara namespace default no elemento raiz, então isso é o comportamento correto de qualquer forma.

- [x] **Etapa 4 — Builders XML.**
  - `xml/DpsXmlBuilder.java` — porte 1:1 de 552 linhas do PHP (mesma ordem de tags, mesmos nomes). `appendElement` genérico usa `Enum.toString()` (todos os enums retornam o `codigo` — ver Etapa 1) em vez de checar `BackedEnum` como o PHP.
  - `xml/EventosXmlBuilder.java` — porte 1:1. Mesma limitação do original: só o evento `e101101` (cancelamento) está implementado, os outros ~15 tipos de evento não.
  - **Verificado**: `DpsXmlBuilderTest` monta uma DPS mínima, gera XML, assina com `XmlSigner` e valida a assinatura de volta. Passa.
- [x] **Etapa 5 — Parser XML.**
  - `xml/NfseXmlParser.java` — porte funcional (não literal) de `NfseXmlParser.php`. O PHP usa hidratação reflexiva genérica (`#[MapFrom]` + biblioteca de DTO); aqui a extração é explícita, navegando o DOM por tag **sempre escopada ao elemento pai** (nunca busca global — o XML de resposta tem tags repetidas em ramos diferentes, ex. `xLgr`/`nro` aparecem tanto em `emit/enderNac` quanto ecoados dentro de `DPS/infDPS/prest`).
  - **Escopo**: extrai `InfNfseData` completo (status, chave, emitente, valores, tributação IBS/CBS) e `InfEventoData`, **exceto** o campo `dps` (DPS original ecoada na resposta) e `pedRegEvento` — ficam `null`, documentado no javadoc. Reconstituir esses exigiria um "builder inverso" do tamanho do `DpsXmlBuilder`; não crítico (o chamador já tem o `DpsData` original que ele mesmo montou).
  - **Verificado**: `NfseXmlParserTest` interpreta um XML de resposta de exemplo e confere status/chave/emitente/valores. Passa.

- [x] **Etapa 6 — Endpoint resolver + clientes HTTP mTLS.**
  - `NfseContext.java` — config (ambiente, certificado, código município, endpoint customizado).
  - `NfseCertificate` ganhou `buildMtlsSslContext()`/`defaultTrustManager()` — client cert real via JSSE, **valida o servidor contra o trust store padrão da JVM** (o PHP de origem desliga `SSL_VERIFYPEER`/`SSL_VERIFYHOST` — decisão consciente de não replicar isso aqui).
  - `http/SefinEndpointResolver` — porte 1:1 (default + override por município + customizado).
  - `http/SefinClient` — emitirNfse/consultarNfse/consultarDps/registrarEvento/consultarEvento/verificarDps/listarEventos.
  - `http/AdnClient` (317L) — porte completo: distribuição DFe (contribuinte/município), envio de lote, parâmetros de convênio, alíquotas (achei e corrigi uma incompatibilidade: `aliquotas` é `Map<servico, List<AliquotaDto>>`, não lista simples — o docblock do PHP original estava impreciso), benefícios/regimes/retenções, DANFSe (marcado `@Deprecated`, a API oficial de geração é descontinuada em 01/07/2026).
  - `http/CncClient` (172L) — porte completo. Endpoints sem DTO fixo no PHP viram `JsonElement` no Java (não inventei formato).
  - `http/NfseApiException` — porte de `NfseApiException.php`.
  - **Verificado**: `SefinClientTest` confirma que o pipeline mTLS (KeyStore→SSLContext→OkHttpClient) monta sem exceção com o certificado de teste. **Não** testado contra rede real (isso é Etapa 10).

- [x] **Etapa 7 — Validador.** `validator/DpsValidator.java` (porte 1:1 das regras, mesmos números de regra do schema comentados) + `validator/ValidationResult.java`. Não tem teste dedicado ainda (lógica pura, baixo risco, mas não verificada empiricamente — próximo passo se for usar valendo).
- [x] **Etapa 8 — Suporte.** `support/IdGenerator`, `TaxCalculator`, `CpfCnpjFormatter`, `CpfCnpjGenerator`. **Verificado**: `SupportUtilsTest` confere formato do ID da DPS e valida os dígitos verificadores de CPF/CNPJ gerados com uma implementação independente (não reaproveita o código do gerador pra validar). 44 testes, todos passando.
- [x] **Etapa 9 — Serviço de orquestração.** `service/ContribuinteService.java` — emitir (build→assina→gzip/base64→envia→gunzip/base64→parseia), consultar, consultarDps, verificarDps, registrarEvento/registrarEventoData, cancelar (atalho evento 101101), consultarEvento, listarEventos, downloadDanfse (`@Deprecated`), + todos os métodos ADN (baixarDfe, parâmetros/alíquotas/benefícios/regimes/retenções). **`MunicipioService` não portado** — caso de uso é papel de prefeitura, fora do escopo de quem consome esta lib como emissor; portar depois se surgir necessidade real.
  - Sem teste dedicado (orquestra peças já testadas individualmente — builder, signer, gzip/base64 padrão do JDK, SefinClient mTLS — mas o método `emitir()` em si só é exercitado de ponta a ponta na Etapa 10, contra rede real).

## Próximas etapas (não iniciadas)

- [x] **Etapa 10 — Verificação end-to-end contra homologação real. CONCLUÍDA — emissão real bem-sucedida.**
  `EmitirExemplo` rodado contra `sefin.producaorestrita.nfse.gov.br` de verdade, com certificado A1
  real (METRE SISTEMAS LTDA) e CNPJ real, e **emitiu uma NFS-e de verdade em homologação**:
  chave de acesso `NFS31480042227330329000177000000000000126081836647250`. Depuração até chegar lá,
  achados reais (todos corrigidos no código, não só descritos):

  1. **TLS: `PKIX path building failed`.** Causa raiz confirmada (não mais hipótese): a cadeia de
     `sefin.producaorestrita.nfse.gov.br` sobe folha (SERPRO) → intermediária
     `AC SERPRO AR46 OV TLS CA 2025` → raiz pública **GlobalSign Root R46** — ausente no `cacerts`
     de JDKs mais antigos (confirmado com `keytool -list` no JDK 1.8.0_202 do ambiente de teste:
     raiz não estava lá). Corrigido criando `support/TrustStoreGenerator` (gera um PKCS12 com as
     raízes da JVM + GlobalSign Root R46 embutida) e tornando `NfseContext.trustStorePath`
     **obrigatório** — nenhum fallback silencioso pro `cacerts` do sistema, que exigiria admin pra
     corrigir. `NfseCertificate.buildMtlsSslContext`/`trustManager` agora exigem o trust store
     explícito.
  2. **`E1235 - Falha no esquema XML do DF-e` em `dhEmi`.** `DateTimeFormatter.ISO_OFFSET_DATE_TIME`
     inclui fração de segundo quando há nanossegundos (`"...23.8-03:00"`), e o schema
     `TSDateTimeUTC` não aceita fração. Corrigido nos exemplos com `DataUtil` (padrão
     `yyyy-MM-dd'T'HH:mm:ssXXX`, sem fração).
  3. **`E0008 - dhEmi posterior à data de processamento.`** Confirmado com evidência direta
     (`dhEmi=13:51:31` vs `dataHoraProcessamento=13:51:29` — relógio local ~2s à frente do de
     referência da Sefin, mesmo com "hora do Windows certa"). Um clock 1-2s adiantado é o
     suficiente pra rejeição — sem tolerância. Corrigido com `support/NtpClock` (cliente SNTP
     simples contra `a.st1.ntp.br`/`b.st1.ntp.br`/`pool.ntp.org`, com fallback pro relógio local se
     nenhum servidor responder) — `dhEmi` e `dCompet` agora derivam do mesmo instante corrigido.
  4. **`E0120`/`E0121` — IM e nome do prestador não devem ser informados.** Quando
     `tipoEmitente=PRESTADOR`, a Sefin deriva `IM`/`xNome` do CNC a partir do CNPJ — informar de
     novo é rejeitado. Confirma também a regra E0128 (endereço do prestador não deve ser informado
     nesse caso), que o `DpsValidator.php` de origem só comentava sem validar — **agora é validação
     de verdade** em `DpsValidator.java` (uma melhoria real sobre o PHP, motivada pela confirmação
     empírica). A nota real de referência (`nfse-real-exemplo.xml`) também confirma: `<prest>` sem
     `<end>`/`<xNome>` quando `tpEmit=1`.

  Ferramenta de apoio criada no processo: `examples/RelogioDiagnostico.java` (imprime
  zona/offset/instante que a JVM enxerga — usar se suspeitar de descompasso de relógio de novo).

- [x] **Etapa 11 — Geração do DANFSe (PDF). CONCLUÍDA.** Porte de `danfse-php-main`
  (`nfse-nacional/danfse-php`, pacote separado do `nfse-php`) — gera a representação em PDF da
  NFS-e a partir de {@code NfseData}. Diferente do resto do porte, o motor de renderização
  (JasperReports) é nativo Java, então só a montagem de parâmetros precisou ser portada
  (`NfseTemplateMapper.php` → `danfse.DanfseGenerator`), reaproveitando o `.jrxml` original quase
  sem alteração (copiado pra `src/main/resources/com/nfsenacional/danfse/`). Diferente também do
  resto: aqui a navegação dinâmica por dot-path do PHP (`value()`/`array()`) foi trocada por acesso
  direto aos getters tipados dos DTOs já existentes — mais simples e sem risco de erro de digitação
  no path, já que o compilador garante que os campos existem.

  Achados reais (não hipóteses — descobertos rodando o compile/fill/export de verdade):

  1. **Versão do JasperReports fixada em 5.6.0** (pedido explícito do usuário, pra não colidir com
     a versão que outros sistemas que importam esta lib já possam usar). Isso quebrou o componente
     `jr:QRCode` do template original — esse componente barcode4j só existe a partir da 6.0; na
     5.6.0 nem a classe existe no jar (confirmado inspecionando o jar: só `Code128`, `EAN13`,
     `PDF417` etc., sem `QRCode`). **Corrigido** trocando o `<componentElement><jr:QRCode>` do jrxml
     por um `<image>` comum ligado a um novo parâmetro `imgQrCode`, preenchido em
     `DanfseGenerator.gerarQrCode()` usando **ZXing** (`com.google.zxing:core`/`javase`) pra gerar o
     PNG do QR Code do link de consulta pública em tempo de execução (arquivo temporário,
     `deleteOnExit`). Mesmo conteúdo/link, engine diferente — visualmente equivalente.
  2. **`imgPrefeitura` vazio derruba a exportação com `Image read failed`.** No PHP/TCPDF uma string
     vazia como caminho de imagem é tolerada (renderiza em branco); no JasperReports o comportamento
     padrão do elemento `<image>` é lançar erro (`onErrorType="Error"`, o default) quando o caminho
     é inválido/vazio. **Corrigido** adicionando `onErrorType="Blank"` no elemento de imagem do
     brasão da prefeitura no jrxml — sem brasão informado, fica em branco em vez de quebrar a
     geração (o valor é sempre opcional, passado via `extras.put("imgPrefeitura", caminho)`).
  3. **Fontes: template usa só `fontName="Arial"`**, sem nenhum atributo `pdfFontName`/
     `isPdfEmbedded` explícito (confirmado via grep no `.jrxml`, nenhuma ocorrência). O lado PHP
     resolve isso embutindo fontes TCPDF via `quilhasoft/jasperphp`; aqui não há equivalente
     configurado. Na exportação Java, sem extensão de fonte registrada pra "Arial", o exportador
     cai (sem erro, log silencioso) pra `net.sf.jasperreports.default.pdf.font.name` (Helvetica —
     visualmente muito próxima de Arial). **Limitação cosmética conhecida, não funcional** — se
     precisar da fonte exata, registrar uma extensão de fonte JasperReports com Arial/Liberation
     Sans embutida (`jasperreports_extension.properties` + `.ttf`).

  **Verificado**: `DanfseGeneratorTest` roda o pipeline completo (`nfse-real-exemplo.xml` →
  `NfseXmlParser` → `DanfseGenerator.gerarPdf()`) e confirma um PDF válido e não-vazio
  (header `%PDF-`). Não comparado byte a byte com o PDF de exemplo do PHP (fontes/engine diferentes
  tornam isso não confiável como critério de sucesso). Exemplo executável:
  `examples/GerarDanfseExemplo.java` (busca uma NFS-e real via `consultar()` e grava o PDF em disco).

- [ ] **Etapa 12 — Publicação.** Maven Central / repositório interno, versionamento semântico.

## Documentação

- [x] **`README.md`** — pré-requisitos (certificado A1/A3 ICP-Brasil, município aderente), diferença
  entre homologação e produção (só a URL — mesmo certificado nos dois, sem "conta de teste"
  separada — bem diferente do modelo Celcoin/OAuth), exemplos de emitir/consultar/cancelar/consultar
  alíquota, tratamento de erros. Fatos de credenciamento confirmados via busca (não inventados):
  [gov.br/nfse — APIs Prod. Restrita e Produção](https://www.gov.br/nfse/pt-br/biblioteca/documentacao-tecnica/apis-prod-restrita-e-producao),
  portal humano de primeiro acesso (mencionado só pra não confundir — não é o mecanismo de auth da API).
  - **Verificado**: `src/test/java/.../examples/ReadmeExamplesCompileTest.java` — todo código de
    exemplo do README também existe como teste `@Disabled` (não roda, precisa de cert+rede reais)
    mas **compila** contra a API de verdade. Achei e corrigi 1 erro de nome de enum no processo
    (`IndicadorTotalTributos.NAO_INFORMADO` não existe → `SEM_INFORMACAO`).

## Estrutura atual

```
src/main/java/com/nfsenacional/
├── enums/            28 classes ✅
├── dto/
│   ├── nfse/         51 classes ✅
│   └── http/         12 classes ✅
└── signer/
    ├── NfseCertificate.java       ✅
    ├── NfseCertificateException.java ✅
    ├── XmlSigner.java             ✅
    └── NfseSignerException.java   ✅

src/test/java/com/nfsenacional/signer/
└── XmlSignerTest.java  ✅ (passa: mvn test)

src/test/resources/
└── certificado-teste.p12  (gerado via keytool, senha "senha123" — só pra teste, não é certificado real)
```
