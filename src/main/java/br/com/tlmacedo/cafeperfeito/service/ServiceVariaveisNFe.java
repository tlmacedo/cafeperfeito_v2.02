package br.com.tlmacedo.cafeperfeito.service;

//import br.com.tlmacedo.cafeperfeito.xsd.sistema.config.TConfig;

import br.com.cafeperfeito.xsd.configNFe.config.MyInfNfe;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ServiceVariaveisNFe {

    public static MyInfNfe MYINFNFE;
//    public static List<NatOp> NATOPS;
//    public static List<IndPag> INDPAGS;
//    public static List<Ide.Mods.Mod> MODS;

    private FileReader arqConfgSistema = null;

    public ServiceVariaveisNFe() throws FileNotFoundException {
        arqConfgSistema = new FileReader(getClass().getClassLoader().getResource("configNFe.xml").getFile());
    }

    public void getVariaveisNFe() {
        try {
            String xml = ServiceUtilXml.FileXml4String(arqConfgSistema);
            MYINFNFE = ServiceUtilXml.xmlToObject(xml, MyInfNfe.class);
//            NATOPS = MYINFNFE.getIde().getNatOps().getNatOp();
//            INDPAGS = MYINFNFE.getIde().getIndPags().getIndPag();
//            MODS = MYINFNFE.getIde().getMods().getMod();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
