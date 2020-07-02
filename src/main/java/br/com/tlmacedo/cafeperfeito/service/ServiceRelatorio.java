package br.com.tlmacedo.cafeperfeito.service;

import br.com.tlmacedo.cafeperfeito.model.enums.RelatorioTipo;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRelatorio extends JFrame {
    public void gerar(RelatorioTipo tipo, File pathXml) throws JRException {
        JRDataSource ds = new JRXmlDataSource(pathXml, "/nfeProc/NFe/infNFe/det");
//        JRXmlDataSource xml = new JRXmlDataSource(pathXml, "/NFe/infNFe/det");

        InputStream relJasper = getClass().getResourceAsStream(tipo.getDescricao());

        JasperPrint impressao = JasperFillManager.fillReport(relJasper, new HashMap<>(), ds);
//        impressao = JasperFillManager.fillReport(relJasper, new HashMap<>(), xml);
        JasperViewer viewer = new JasperViewer(impressao, false);
        viewer.setVisible(true);
    }

    public void gerar(RelatorioTipo tipo, Blob strXml) throws JRException, UnsupportedEncodingException, SQLException {
        System.out.printf("strXml:\n%s\n", strXml);
//        Document document = ServiceDocumentFactory.documentFactory(strXml);
        String relatorio = "/Volumes/150GB-Development/cafeperfeito/cafeperfeito_v2.02/src/main/resources/relatorio/danfe.jasper";
        //String relatorio = getClass().getResource(tipo.getDescricao()).toString();

        InputStream stream = strXml.getBinaryStream();
        //InputStream stream =


        JRXmlDataSource xml = new JRXmlDataSource(stream, "/nfeProc/NFe/infNFe/det");
        HashMap mapa = new HashMap();
        JasperPrint jp = JasperFillManager.fillReport(relatorio, mapa, xml);

        JasperViewer jv = new JasperViewer(jp, false);
        jv.setTitle("VISUALIZADOR DE DOCUMENTO FISCAL ELETRÔNICA");
        //jv.setIconImage(imagemTituloJanela.getImage());
        jv.setVisible(true);

    }


    public void gerar(RelatorioTipo tipo, Map parametros, List list) throws JRException {

        InputStream relJasper = getClass().getResourceAsStream(tipo.getDescricao());

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(list);

        JasperPrint impressao = null;

        impressao = JasperFillManager.fillReport(relJasper, parametros, ds);
        JasperViewer viewer = new JasperViewer(impressao, false);
        viewer.setVisible(true);
//        JRViewer viewer = new JRViewer(impressao);
//        this.add(viewer);
//        this.setSize(700, 500);
//        this.setVisible(true);
//        System.out.printf("Done!");
//        ServiceAlertMensagem alertMensagem;
    }
}
