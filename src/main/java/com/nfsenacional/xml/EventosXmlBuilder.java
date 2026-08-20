package com.nfsenacional.xml;

import com.nfsenacional.dto.nfse.PedRegEventoData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Monta o XML de pedido de registro de evento ({@code pedRegEvento}) — usado para cancelamento e
 * demais eventos da NFS-e.
 * <p>
 * Porte 1:1 de {@code Nfse\Xml\EventosXmlBuilder} (php-api). <b>Mesma limitação do original:</b> só
 * o evento {@code e101101} (cancelamento) está de fato implementado — os outros ~15 tipos de evento
 * que já existem em {@link com.nfsenacional.dto.nfse.InfPedRegData} (e105102, e101103, e202201...)
 * ainda não têm o bloco correspondente montado aqui nem no PHP de origem.
 *
 * @author Renato
 */
public class EventosXmlBuilder {

    private Document dom;

    public String buildPedRegEvento(PedRegEventoData data) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            this.dom = dbf.newDocumentBuilder().newDocument();

            Element root = dom.createElementNS("http://www.sped.fazenda.gov.br/nfse", "pedRegEvento");
            root.setAttribute("versao", String.valueOf(data.getVersao()));
            dom.appendChild(root);

            Element inf = dom.createElement("infPedReg");

            // Id do infPedReg: PRE + chNFSe + tipoEvento.
            // nPedRegEvento foi removido do Id a partir de jan/2026 (TSIdPedRegEvt: PRE[0-9]{56}).
            String chave = data.getInfPedReg().getChaveNfse();
            String tipo = data.getInfPedReg().getTipoEvento();
            inf.setAttribute("Id", "PRE" + chave + tipo);

            appendElement(inf, "tpAmb", String.valueOf(data.getInfPedReg().getTipoAmbiente()));
            appendElement(inf, "verAplic", data.getInfPedReg().getVersaoAplicativo());
            appendElement(inf, "dhEvento", data.getInfPedReg().getDataHoraEvento());

            if (hasText(data.getInfPedReg().getCnpjAutor())) {
                appendElement(inf, "CNPJAutor", data.getInfPedReg().getCnpjAutor());
            }
            if (hasText(data.getInfPedReg().getCpfAutor())) {
                appendElement(inf, "CPFAutor", data.getInfPedReg().getCpfAutor());
            }

            appendElement(inf, "chNFSe", chave);

            // nPedRegEvento não existe no schema XSD (nem v1.00 nem v1.01) — depois de chNFSe vem
            // direto o elemento do tipo de evento (e101101 etc).
            if (data.getInfPedReg().getE101101() != null) {
                Element e = dom.createElement("e101101");
                appendElement(e, "xDesc", data.getInfPedReg().getE101101().getDescricao());
                appendElement(e, "cMotivo", data.getInfPedReg().getE101101().getCodigoMotivo());
                appendElement(e, "xMotivo", data.getInfPedReg().getE101101().getMotivo());
                inf.appendChild(e);
            }

            root.appendChild(inf);

            return serialize(root);
        } catch (Exception e) {
            throw new NfseXmlException("Falha ao montar XML de evento: " + e.getMessage(), e);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }

    private void appendElement(Element parent, String name, String value) {
        if (value == null) {
            return;
        }
        Element el = dom.createElement(name);
        el.appendChild(dom.createTextNode(value));
        parent.appendChild(el);
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
