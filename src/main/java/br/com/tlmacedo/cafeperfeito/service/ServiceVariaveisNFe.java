package br.com.tlmacedo.cafeperfeito.service;

import br.com.cafeperfeito.xsd.configNFe.config.MyInfNfe;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ServiceVariaveisNFe {

    public static MyInfNfe MYINFNFE;

    private FileReader arqConfgSistema = null;

    public ServiceVariaveisNFe() throws FileNotFoundException {
        arqConfgSistema = new FileReader(getClass().getClassLoader().getResource("configNFe.xml").getFile());
    }

    public void getVariaveisNFe() {
        try {
            String xml = ServiceUtilXml.FileXml4String(arqConfgSistema);
            MYINFNFE = ServiceUtilXml.xmlToObject(xml, MyInfNfe.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}