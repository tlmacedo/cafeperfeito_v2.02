package br.com.tlmacedo.cafeperfeito.service;

import br.com.tlmacedo.cafeperfeito.model.enums.RelatorioTipo;
import br.inf.portalfiscal.xsd.nfe.enviNFe.TEnviNFe;
import br.inf.portalfiscal.xsd.nfe.enviNFe.TNFe;
import br.inf.portalfiscal.xsd.nfe.procNFe.TNfeProc;
import net.sf.jasperreports.engine.JRException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisSistema.TCONFIG;

public class ServiceFileXmlSave {

    private static FileWriter arqXml;
    private static String conteudoXml;
    private static File newFileXml;

    public static boolean saveXml(String pathFile, String xml) throws IOException {
        setConteudoXml(xml);

        if (!pathFile.contains("/"))
            pathFile = String.format("%s%s%s.xml",
                    System.getProperty("user.dir"),
                    TCONFIG.getPaths().getPathNFeSaveXmlOut().trim(),
                    pathFile);

        setNewFileXml(new File(pathFile));

        return salvaArquivo();
    }

    public static boolean saveTEnviNFeToFile(TEnviNFe tEnviNFe) throws IOException, JAXBException {
        TNFe tnFe = tEnviNFe.getNFe().get(0);

        setConteudoXml(ServiceUtilXml.objectToXml(tnFe));

        setNewFileXml(new File(
                String.format("%s%s%s%s.xml",
                        System.getProperty("user.dir"),
                        TCONFIG.getPaths().getPathNFeSaveXmlOut().trim(),
                        tnFe.getInfNFe().getId(),
                        (tnFe.getSignature() != null) ? "-assinada" : "")
        ));
        return salvaArquivo();
    }

    public static boolean saveTNfeProcToFile(TNfeProc tNfeProc) throws IOException, JAXBException {

        setConteudoXml(ServiceUtilXml.objectToXml(tNfeProc));

        setNewFileXml(new File(
                String.format("%s%s%s%s.xml",
                        System.getProperty("user.dir"),
                        TCONFIG.getPaths().getPathNFeSaveXmlOut().trim(),
                        tNfeProc.getNFe().getInfNFe().getId(),
                        (tNfeProc.getProtNFe().getInfProt().getCStat().equals("100")) ? "-proc" : "")
        ));

        if (salvaArquivo()) {
            System.out.printf("caminhoArquivo: [%s]\n", getNewFileXml());
            System.out.printf("caminhoArquivo.exists: [%s]\n", getNewFileXml().exists());

            System.out.printf("getArqXml.getName: [%s]\n", getArqXml().getClass().getName());
            System.out.printf("getArqXml.getSimpleName: [%s]\n", getArqXml().getClass().getSimpleName());

            try {
                ServiceRelatorio nfeDANFE = new ServiceRelatorio();
                nfeDANFE.gerar(RelatorioTipo.NFE, getNewFileXml());
            } catch (JRException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private static boolean salvaArquivo() {
        try {
//            if (getArqXml() == null)
            setArqXml(new FileWriter(getNewFileXml()));
            getArqXml().write(getConteudoXml());
            getArqXml().close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Begin Getters and Setters
     */
    private static FileWriter getArqXml() {
        return arqXml;
    }

    private static void setArqXml(FileWriter arqXml) {
        ServiceFileXmlSave.arqXml = arqXml;
    }

    private static String getConteudoXml() {
        return conteudoXml;
    }

    private static void setConteudoXml(String conteudoXml) {
        ServiceFileXmlSave.conteudoXml = conteudoXml;
    }

    public static File getNewFileXml() {
        return newFileXml;
    }

    public static void setNewFileXml(File newFileXml) {
        ServiceFileXmlSave.newFileXml = newFileXml;
    }

    /**
     * END Getters and Setters
     */
}
