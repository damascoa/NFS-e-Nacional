# NFS-e Nacional — Java SDK

Framework Java para integração com o **Sistema Nacional NFS-e** (`sefin.nfse.gov.br`) — emissão,
consulta e cancelamento de Nota Fiscal de Serviço eletrônica pelo padrão nacional (DPS em XML,
assinado com certificado digital A1, autenticação por mTLS).

Porte do SDK oficial em PHP ([`nfse-nacional/nfse-php`](https://github.com/nfse-nacional/nfse-php)) —
mesma estrutura, mesmos DTOs, mesmas regras de negócio.



## Pré-requisitos

1. **Certificado digital A1** (ou A3) da empresa (e-CNPJ) ou da pessoa física (e-CPF), emitido por
   uma Autoridade Certificadora credenciada pela ICP-Brasil (Serasa Certificado Digital, Certisign,
   Soluti, Safeweb, etc.). Um e-CNPJ A1 custa algo em torno de R$100–300/ano e sai na hora
   (validação em vídeo). **O mesmo certificado serve pra homologação e produção** — não existe
   certificado "de teste" separado.
2. **Município aderente ao Sistema Nacional NFS-e.** Nem toda prefeitura aderiu ainda. Confira a
   lista atualizada em
   [gov.br/nfse — Monitoramento de Adesões](https://www.gov.br/nfse/pt-br/municipios/monitoramento-adesoes).
   Sem o município aderente, tanto homologação quanto produção rejeitam a emissão.
3. **Código IBGE do município** de prestação do serviço (7 dígitos) — necessário pra montar o
   `Id` da DPS e pro campo `cLocEmi`.

## Configurando a conta — Homologação vs Produção

A diferença entre os dois ambientes é **só a URL**. Não existe passo de "criar conta" separado —
o certificado já é a sua identidade nos dois.

| | Homologação (Produção Restrita) | Produção |
|---|---|---|
| URL Sefin | `https://sefin.producaorestrita.nfse.gov.br/SefinNacional` | `https://sefin.nfse.gov.br/SefinNacional` |
| URL ADN | `https://adn.producaorestrita.nfse.gov.br` | `https://adn.nfse.gov.br` |
| URL CNC | `https://adn.producaorestrita.nfse.gov.br/cnc` | `https://adn.nfse.gov.br/cnc` |
| Certificado | o mesmo A1/A3 real | o mesmo A1/A3 real |
| `TipoAmbiente` no SDK | `TipoAmbiente.HOMOLOGACAO` | `TipoAmbiente.PRODUCAO` |

O SDK resolve a URL certa sozinho a partir do `TipoAmbiente` que você passar no `NfseContext` — não
precisa configurar URL na mão, a menos que o município use infraestrutura própria (ver
`SefinEndpointResolver` — hoje só Catanduva/SP está mapeado; outros casos exigem
`NfseContext.endpoint(...)` customizado).

### Passo a passo pra testar em Homologação

1. Tenha o certificado A1 em mãos (arquivo `.pfx`/`.p12` + senha).
2. Confirme que o município do prestador está aderente (link acima).
3. Rode o exemplo de emissão de verdade (arquivo real, não só o trecho de código abaixo):

   ```bash
   export NFSE_CERT_PATH=/caminho/para/certificado.pfx
   export NFSE_CERT_PASSWORD=senha-do-certificado
   mvn -q test-compile exec:java \
       -Dexec.mainClass=com.nfsenacional.examples.EmitirExemplo \
       -Dexec.classpathScope=test
   ```

   Arquivo: [`src/test/java/com/nfsenacional/examples/EmitirExemplo.java`](src/test/java/com/nfsenacional/examples/EmitirExemplo.java).
   Sem as variáveis de ambiente configuradas, ele avisa e para — não estoura stacktrace.

4. Se os dois requisitos acima estiverem OK e ainda assim der erro de TLS ao chamar a Sefin, o
   problema costuma ser um destes:
   - certificado vencido, senha errada, ou chave menor que 2048 bits — o SDK valida os três na
     hora de carregar o certificado e já avisa com mensagem específica;
   - **erro `PKIX path building failed` ao validar o certificado do *servidor*** — confirmado contra
     a Sefin real: `sefin.producaorestrita.nfse.gov.br` serve um certificado cuja cadeia sobe até a
     raiz pública **GlobalSign Root R46** (via SERPRO), que JDKs mais antigos (o `cacerts` deles) não
     têm. **Resolvido** — ver seção seguinte, não precisa mais mexer no `cacerts` do sistema
     (não precisa de admin).
   - **`E1235 - Falha no esquema XML do DF-e` reclamando de `dhEmi`/`dhEvento`** — achado real
     testando contra a Sefin: `DateTimeFormatter.ISO_OFFSET_DATE_TIME` do Java inclui fração de
     segundo quando o relógio tem nanossegundos (`"...23.8-03:00"`), e o schema `TSDateTimeUTC` não
     aceita fração nenhuma. Os exemplos já usam o formato certo
     (`yyyy-MM-dd'T'HH:mm:ssXXX`, sem fração) via `DataUtil.formatarDataHora` — se você montar a
     data na mão, use esse padrão.

### Certificado do servidor não confiável (trust store)

`trustStorePath` é **obrigatório** no `NfseContext` — não existe fallback silencioso pro `cacerts`
do sistema. Motivo: como visto acima, o TLS da Sefin depende de uma raiz que pode faltar no seu
JDK, e corrigir isso no `cacerts` do sistema operacional exige privilégio de administrador. Em vez
disso, gere um trust store próprio da aplicação (não mexe no sistema, não precisa de admin):

```java
import com.nfsenacional.support.TrustStoreGenerator;
import java.nio.file.Path;
import java.nio.file.Paths;

Path trustStorePath = Paths.get(System.getProperty("user.home"), ".nfse-nacional", "truststore.p12");
if (!trustStorePath.toFile().exists()) {
    TrustStoreGenerator.gerar(trustStorePath, "changeit".toCharArray());
}
```

`TrustStoreGenerator.gerar(...)` cria um PKCS12 novo contendo **todas as raízes que sua própria JVM
já confia** (copiadas do `cacerts` em memória, não precisa ler o arquivo do disco) **+ a raiz
GlobalSign Root R46** embutida no SDK — cobre o caso real que travou o handshake com a Sefin.
Gere uma vez (ex: na inicialização da aplicação) e reaproveite o arquivo nas próximas execuções.

### Passo a passo pra ir pra Produção

1. Repita o teste em homologação até funcionar de ponta a ponta.
2. Troque só `TipoAmbiente.HOMOLOGACAO` → `TipoAmbiente.PRODUCAO` no `NfseContext`. Mais nada muda
   no código.
3. **Cuidado com os dados de teste**: os exemplos abaixo usam CNPJ/CPF de exemplo — em produção,
   qualquer nota emitida é real e não tem "modo teste" pra desfazer sem um evento de cancelamento.

### Portal web (uso manual, não é a API)

Existe também um portal humano em
[producaorestrita.nfse.gov.br/EmissorNacional](https://www.producaorestrita.nfse.gov.br/EmissorNacional/Acesso/PrimeiroAcesso)
pra quem quer emitir manualmente pelo navegador, com login por senha (sem certificado). **Isso é
outro sistema, não tem relação com o acesso via API/mTLS que este SDK usa** — só citado aqui pra não
confundir se você esbarrar nele numa busca.

## Instalação

Ainda não publicado em repositório público (ver Etapa 11 do `TASKS.md`). Por enquanto, use como
dependência local:

```bash
git clone <este-repositorio>
cd NFS-e-Nacional
mvn install
```

```xml
<dependency>
    <groupId>com.nfsenacional</groupId>
    <artifactId>nfse-nacional-java</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Exemplos de uso

Todo exemplo abaixo existe como arquivo real e executável em
[`src/test/java/com/nfsenacional/examples/`](src/test/java/com/nfsenacional/examples/) — não é só
código solto no markdown. Rode com `mvn -q test-compile exec:java -Dexec.mainClass=... -Dexec.classpathScope=test`
(ficam em `src/test` de propósito: não entram no JAR publicado da biblioteca).

### Configurando o contexto

```java
Path trustStorePath = Paths.get(System.getProperty("user.home"), ".nfse-nacional", "truststore.p12");
if (!trustStorePath.toFile().exists()) {
    TrustStoreGenerator.gerar(trustStorePath, "changeit".toCharArray());
}

NfseContext context = NfseContext.builder()
        .ambiente(TipoAmbiente.HOMOLOGACAO)          // TipoAmbiente.PRODUCAO em produção
        .certificatePath("/caminho/para/certificado.pfx")
        .certificatePassword("senha-do-certificado")
        .codigoMunicipio("2304400")                   // Fortaleza/CE — usado só se o município
        .trustStorePath(trustStorePath.toString())    // tiver endpoint próprio mapeado
        .trustStorePassword("changeit")                // opcional — default já é "changeit"
        .build();

ContribuinteService service = new ContribuinteService(context);
```

Se preferir carregar o certificado a partir de bytes (ex: vindo de um banco de dados em vez de
arquivo em disco), use `certificateContent(byte[])` no lugar de `certificatePath`.

### Emitir uma NFS-e

```java
String cnpjPrestador = "03279735000194";
String codigoMunicipio = "2304400"; // Fortaleza/CE
String serie = "1";
String numero = "100";

String idDps = IdGenerator.generateDpsId(cnpjPrestador, codigoMunicipio, serie, numero);

DpsData dps = DpsData.builder()
        .versao("1.01")
        .infDps(InfDpsData.builder()
                .id(idDps)
                .tipoAmbiente(TipoAmbiente.HOMOLOGACAO)
                .dataEmissao(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))) // sem fração de segundo — ver nota abaixo
                .versaoAplicativo("SDK-Java-1.0")
                .serie(serie)
                .numeroDps(numero)
                .dataCompetencia(LocalDate.now().toString())
                .tipoEmitente(EmitenteDPS.PRESTADOR)
                .codigoLocalEmissao(codigoMunicipio)
                .prestador(PrestadorData.builder()
                        .cnpj(cnpjPrestador)
                        .nome("Empresa de Teste")
                        .endereco(EnderecoData.builder()
                                .codigoMunicipio(codigoMunicipio)
                                .cep("60000000")
                                .logradouro("Rua Teste")
                                .numero("123")
                                .complemento("Sala 1")
                                .bairro("Centro")
                                .build())
                        .telefone("85999999999")
                        .email("teste@empresa.com.br")
                        .regimeTributario(RegimeTributarioData.builder()
                                .opcaoSimplesNacional(OpcaoSimplesNacional.NAO_OPTANTE)
                                .regimeEspecialTributacao(RegimeEspecialTributacao.NENHUM)
                                .build())
                        .build())
                .tomador(TomadorData.builder()
                        .cnpj("44827692000111")
                        .nome("Cliente de Teste")
                        // Endereço é obrigatório sempre que o tomador é identificado
                        // (CPF/CNPJ/NIF informado) — ver DpsValidator.
                        .endereco(EnderecoData.builder()
                                .codigoMunicipio(codigoMunicipio)
                                .cep("60000000")
                                .logradouro("Av. do Cliente")
                                .numero("456")
                                .bairro("Aldeota")
                                .build())
                        .build())
                .servico(ServicoData.builder()
                        .localPrestacao(LocalPrestacaoData.builder()
                                .codigoLocalPrestacao(codigoMunicipio)
                                .build())
                        .codigoServico(CodigoServicoData.builder()
                                .codigoTributacaoNacional("010101")
                                .descricaoServico("Desenvolvimento de Software")
                                .build())
                        .build())
                .valores(ValoresData.builder()
                        .valorServicoPrestado(ValorServicoPrestadoData.builder()
                                .valorServico(100.00)
                                .build())
                        .tributacao(TributacaoData.builder()
                                .tributacaoIssqn(TributacaoIssqn.OPERACAO_TRIBUTAVEL)
                                .tipoRetencaoIssqn(TipoRetencaoIssqn.NAO_RETIDO) // conferir código correto pro seu caso
                                .indicadorTotalTributos(IndicadorTotalTributos.SEM_INFORMACAO) // idem
                                .build())
                        .build())
                .build())
        .build();

// Valide antes de gastar uma chamada de rede — devolve os mesmos erros que a Sefin rejeitaria.
ValidationResult validacao = new DpsValidator().validate(dps);
if (!validacao.isValid()) {
    throw new IllegalStateException("DPS inválida: " + validacao.getErrors());
}

NfseData nfseData = service.emitir(dps);
System.out.println("NFS-e emitida! Chave de acesso: " + nfseData.getInfNfse().getId());
```

> Os nomes exatos dos códigos de `TipoRetencaoIssqn`/`IndicadorTotalTributos` acima precisam ser
> conferidos contra o enum gerado (`src/main/java/com/nfsenacional/enums/`) — cada schema tem
> valores específicos por versão do leiaute; use o valor que faz sentido pro seu caso real.

### Consultar uma NFS-e emitida

Arquivo: [`ConsultarExemplo.java`](src/test/java/com/nfsenacional/examples/ConsultarExemplo.java) —
`mvn -q test-compile exec:java -Dexec.mainClass=com.nfsenacional.examples.ConsultarExemplo -Dexec.classpathScope=test -Dexec.args="<chaveDeAcesso>"`

```java
NfseData nfseData = service.consultar("35503082123456780001990000...chaveDeAcesso...");
if (nfseData == null) {
    System.out.println("Nota não encontrada.");
} else {
    System.out.println("Status: " + nfseData.getInfNfse().getCodigoStatus());
    System.out.println("Valor líquido: " + nfseData.getInfNfse().getValores().getValorLiquido());
}
```

### Cancelar uma NFS-e (evento 101101)

Arquivo: [`CancelarExemplo.java`](src/test/java/com/nfsenacional/examples/CancelarExemplo.java) —
`mvn -q test-compile exec:java -Dexec.mainClass=com.nfsenacional.examples.CancelarExemplo -Dexec.classpathScope=test -Dexec.args="<chaveNfse> <cnpjAutor>"`

```java
String chaveNfse = "35503080000000000000000000000000000000000000";
String cnpjAutor = "03279735000194";

PedRegEventoData evento = PedRegEventoData.builder()
        .versao("1.01")
        .infPedReg(InfPedRegData.builder()
                .tipoAmbiente(2) // 1-Produção, 2-Homologação
                .versaoAplicativo("SDK-Java-1.0")
                .dataHoraEvento(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))) // sem fração de segundo — ver nota abaixo
                .chaveNfse(chaveNfse)
                .cnpjAutor(cnpjAutor)
                .tipoEvento("101101") // cancelar() já garante esse valor, mas fica explícito aqui
                .e101101(CancelamentoData.builder()
                        .descricao("Cancelamento de NFS-e")
                        .codigoMotivo("1") // 1 = Erro na emissão
                        .motivo("Teste de cancelamento via SDK Java")
                        .build())
                .build())
        .build();

RegistroEventoResponse response = service.cancelar(evento);
System.out.println("Evento registrado, aguardando processamento (assíncrono via consulta/eventos).");
```

O cancelamento (como a própria emissão) é processado pela prefeitura — a resposta imediata só
confirma que a Sefin aceitou o pedido; confira o resultado depois com
`service.consultarEvento(chaveNfse, 101101, numSeqEvento)` ou `service.listarEventos(chaveNfse)`.

### Consultar alíquota de um serviço num município (ADN)

Arquivo: [`ConsultarAliquotaExemplo.java`](src/test/java/com/nfsenacional/examples/ConsultarAliquotaExemplo.java) —
`mvn -q test-compile exec:java -Dexec.mainClass=com.nfsenacional.examples.ConsultarAliquotaExemplo -Dexec.classpathScope=test -Dexec.args="2304400 010101 2026-01"`

```java
ResultadoConsultaAliquotasResponse resultado =
        service.consultarAliquota("2304400", "010101", "2026-01");

resultado.getAliquotas().forEach((codigoServico, lista) -> {
    lista.forEach(aliquota -> System.out.println(
            codigoServico + ": " + aliquota.getAliquota() + "% (vigência " + aliquota.getDataInicio() + ")"));
});
```

### Verificar se uma DPS já foi processada antes de reenviar

```java
boolean existe = service.verificarDps(idDps);
if (existe) {
    System.out.println("Essa DPS já foi enviada — não reenviar.");
}
```

### Gerar o DANFSe (PDF) de uma NFS-e emitida

Porte de [`nfse-nacional/danfse-php`](https://github.com/nfse-nacional/danfse-php) — gera a
representação em PDF da NFS-e (o "DANFSe") a partir de um `NfseData` já emitido/consultado. Usa
[JasperReports](https://community.jaspersoft.com/) (motor nativo Java) pra renderizar o `.jrxml`
oficial do template, que já vem embutido no jar (`com/nfsenacional/danfse/nfse-nacional.jrxml`) —
não precisa baixar nada à parte.

Arquivo: [`GerarDanfseExemplo.java`](src/test/java/com/nfsenacional/examples/GerarDanfseExemplo.java) —
`mvn -q test-compile exec:java -Dexec.mainClass=com.nfsenacional.examples.GerarDanfseExemplo -Dexec.classpathScope=test`

```java
NfseData nfseData = service.consultar(chaveAcesso);

DanfseGenerator gerador = new DanfseGenerator();

// Direto pra arquivo:
gerador.gerarPdf(nfseData, Collections.emptyMap(), Paths.get("danfse.pdf"));

// Ou como byte[] (ex: pra devolver numa API):
byte[] pdf = gerador.gerarPdf(nfseData, Collections.emptyMap());
```

O segundo parâmetro (`extras`) permite sobrescrever/adicionar qualquer parâmetro do template — por
exemplo, informar o brasão da prefeitura (opcional, fica em branco se não informado):

```java
Map<String, Object> extras = new HashMap<>();
extras.put("imgPrefeitura", "/caminho/para/brasao.png"); // ou classpath extraído pra arquivo
gerador.gerarPdf(nfseData, extras, destino);
```

**Notas importantes sobre esta parte do SDK** (ver `TASKS.md` — Etapa 11 — pros detalhes completos):

- A versão do JasperReports usada é a **5.6.0**, fixada de propósito pra não colidir com a versão
  que outros sistemas que já importam esta lib possam usar. Como consequência, o componente nativo
  de QR Code (`jr:QRCode`, só disponível a partir da 6.0) não está disponível — o QR Code do link
  de consulta pública é gerado à parte com [ZXing](https://github.com/zxing/zxing) e injetado como
  imagem comum. Visualmente equivalente, engine diferente.
- O template usa a fonte `Arial` sem nenhuma fonte PDF embutida configurada (o lado PHP resolve
  isso via TCPDF; aqui não há equivalente). Na exportação, sem extensão de fonte registrada, o
  JasperReports cai silenciosamente pra Helvetica (visualmente muito próxima). Isso é uma
  **limitação cosmética conhecida**, não afeta a validade fiscal do documento (o PDF é só uma
  representação visual — o que vale legalmente é a NFS-e/XML assinado).

## Tratamento de erros

Toda falha de rede/negócio vira `NfseApiException` (`com.nfsenacional.http`), com `getStatusCode()`,
`getResponseBody()` e `getErros()` (lista de `MensagemProcessamentoDto` quando a Sefin devolveu erro
estruturado). Erros de certificado (vencido, senha errada, chave fraca) viram
`NfseCertificateException`; erros de montagem/leitura de XML viram `NfseXmlException`.

```java
try {
    service.emitir(dps);
} catch (NfseApiException e) {
    System.err.println("HTTP " + e.getStatusCode() + ": " + e.getMessage());
    e.getErros().forEach(erro -> System.err.println(" - " + erro.getCodigo() + ": " + erro.getDescricao()));
}
```

## Estrutura, progresso e limitações conhecidas

Ver [`TASKS.md`](TASKS.md) — cobre o que foi portado, o que foi verificado com teste automatizado
(e como), e o que ainda falta (principalmente: teste contra a Sefin real com certificado de
verdade, `MunicipioService`, geração de DANFSe local — a API oficial de geração é descontinuada em
01/07/2026 e passa a ser responsabilidade do sistema emissor).
