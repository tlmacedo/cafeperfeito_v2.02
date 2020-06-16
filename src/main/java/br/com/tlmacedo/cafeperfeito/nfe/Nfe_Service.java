package br.com.tlmacedo.cafeperfeito.nfe;

public class Nfe_Service {


    public static String factoryIdLote(int nBase) {
        return String.format("%015d", nBase);
    }

    public static String factoryIdLote(Long nBase) {
        return factoryIdLote(nBase.intValue());
    }
}
