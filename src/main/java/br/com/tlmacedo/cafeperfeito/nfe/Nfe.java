package br.com.tlmacedo.cafeperfeito.nfe;

import br.com.tlmacedo.cafeperfeito.model.dao.EmpresaDAO;
import br.com.tlmacedo.cafeperfeito.model.dao.SaidaProdutoNfeDAO;
import br.com.tlmacedo.cafeperfeito.model.enums.NfeCobrancaDuplicataPagamentoMeio;
import br.com.tlmacedo.cafeperfeito.model.vo.Empresa;
import br.com.tlmacedo.cafeperfeito.model.vo.SaidaProduto;
import br.com.tlmacedo.cafeperfeito.model.vo.SaidaProdutoNfe;
import br.com.tlmacedo.cafeperfeito.service.ServiceMascara;
import br.com.tlmacedo.cafeperfeito.service.ServiceSegundoPlano;
import br.com.tlmacedo.cafeperfeito.service.ServiceValidarDado;
import br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisSistema;
import br.com.tlmacedo.nfe.service.NFev400;
import br.com.tlmacedo.service.ServiceAlertMensagem;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;

import static br.com.tlmacedo.cafeperfeito.interfaces.Regex_Convert.DTF_DATA;
import static br.com.tlmacedo.cafeperfeito.interfaces.Regex_Convert.MY_ZONE_TIME;
import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisNFe.MYINFNFE;
import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisSistema.TCONFIG;

public class Nfe {

    private NFev400 nFev400;
    private SaidaProdutoNfe saidaProdutoNfe;
    private String xml;
    private static ServiceAlertMensagem ALERT_MENSAGEM =
            new ServiceAlertMensagem(TCONFIG.getTimeOut(),
                    ServiceVariaveisSistema.SPLASH_IMAGENS,
                    TCONFIG.getPersonalizacao().getStyleSheets());


    public Nfe(SaidaProdutoNfe saidaProdutoNfe, boolean imprimeLote) {
        setSaidaProdutoNfe(saidaProdutoNfe);

        setnFev400(new NFev400(null, ALERT_MENSAGEM,
                MY_ZONE_TIME, (MYINFNFE.getMyConfig().getTpAmb() == 1), true));
        if (!getnFev400().validCertificate())
            return;

        if (saidaProdutoNfe.idProperty().getValue() == 0)
            newSaidaProdutoNfe(imprimeLote);

        if (getSaidaProdutoNfe().getXmlProtNfe() != null)
            setXml(getSaidaProdutoNfe().getXmlProtNfe().toString());
        else if (getSaidaProdutoNfe().getXmlConsRecibo() != null)
            setXml(getSaidaProdutoNfe().getXmlConsRecibo().toString());
        else if (getSaidaProdutoNfe().getXmlAssinatura() != null)
            setXml(getSaidaProdutoNfe().getXmlAssinatura().toString());
        else
            setXml(null);

        if (getXml() != null)
            getnFev400().newNFev400(getXml());
        else
            getnFev400().newNFev400(new Nfe_EnviNfeVO(getSaidaProdutoNfe(), imprimeLote).getEnviNfeVO());

        boolean retorno;
        if (retorno = new ServiceSegundoPlano().executaListaTarefas(getnFev400().getNewTaskNFe(), "NF-e"))
            update_MyNfe();
        System.out.printf("\nretorno: [%s]\n", retorno);

    }

    private void update_MyNfe() {
        try {
            System.out.printf("\npegando_meu_xmlAssinado:\n%s\n\n", getnFev400().getXmlAssinado());
            if (getnFev400().getXmlAssinado() != null)
                getSaidaProdutoNfe().setXmlAssinatura(new SerialBlob(getnFev400().getXmlAssinado().getBytes()));

            System.out.printf("\npegando_meu_xmlConsRecibo:\n%s\n\n", getnFev400().getXmlConsRecibo());
            if (getnFev400().getXmlConsRecibo() != null)
                getSaidaProdutoNfe().setXmlConsRecibo(new SerialBlob(getnFev400().getXmlConsRecibo().getBytes()));

            System.out.printf("\npegando_meu_xmlProtNfe:\n%s\n\n", getnFev400().getXmlProtNfe());
            if (getnFev400().getXmlProtNfe() != null)
                getSaidaProdutoNfe().setXmlProtNfe(new SerialBlob(getnFev400().getXmlProtNfe().getBytes()));

            setSaidaProdutoNfe(new SaidaProdutoNfeDAO().merger(getSaidaProdutoNfe()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void newSaidaProdutoNfe(boolean imprimeLote) {
        Empresa emissor = new EmpresaDAO().getById(Empresa.class, Long.valueOf(TCONFIG.getInfLoja().getId()));
        SaidaProduto saidaProduto = getSaidaProdutoNfe().saidaProdutoProperty().getValue();

        getSaidaProdutoNfe().canceladaProperty().setValue(false);
        getSaidaProdutoNfe().statusSefazProperty().setValue(1);
        getSaidaProdutoNfe().naturezaOperacaoProperty().setValue(MYINFNFE.getMyConfig().getNatOp());
        getSaidaProdutoNfe().modeloProperty().setValue(MYINFNFE.getMyConfig().getMod());

        addNumeroSerieUltimaNfe();
        getSaidaProdutoNfe().dtHoraEmissaoProperty().setValue(saidaProduto.dtCadastroProperty().getValue());
        if (saidaProduto.dtSaidaProperty().getValue()
                .compareTo(saidaProduto.dtCadastroProperty().getValue().toLocalDate()) <= 0) {
            getSaidaProdutoNfe().dtHoraSaidaProperty().setValue(saidaProduto
                    .dtCadastroProperty().getValue());
        } else {
            getSaidaProdutoNfe().dtHoraSaidaProperty().setValue(saidaProduto
                    .dtSaidaProperty().getValue().atTime(8, 0, 0));
        }
        getSaidaProdutoNfe().destinoOperacaoProperty().setValue(MYINFNFE.getMyConfig().getIdDest());
        getSaidaProdutoNfe().impressaoTpImpProperty().setValue(MYINFNFE.getMyConfig().getTpImp());
        getSaidaProdutoNfe().impressaoTpEmisProperty().setValue(MYINFNFE.getMyConfig().getTpImp());
        getSaidaProdutoNfe().impressaoFinNFeProperty().setValue(MYINFNFE.getMyConfig().getFinNFe());
        getSaidaProdutoNfe().impressaoLtProdutoProperty().setValue(imprimeLote);
        if (saidaProduto.clienteProperty().getValue().ieProperty().getValue().equals(""))
            getSaidaProdutoNfe().consumidorFinalProperty().setValue(1);
        else
            getSaidaProdutoNfe().consumidorFinalProperty().setValue(0);
        getSaidaProdutoNfe().indicadorPresencaProperty().setValue(MYINFNFE.getMyConfig().getIndPres());
        getSaidaProdutoNfe().modFreteProperty().setValue(0);
        getSaidaProdutoNfe().transportadorProperty().setValue(emissor);
        getSaidaProdutoNfe().cobrancaNumeroProperty().setValue(getSaidaProdutoNfe().numeroProperty().getValue().toString());
        getSaidaProdutoNfe().pagamentoIndicadorProperty().setValue(MYINFNFE.getMyConfig().getIndPag());
        getSaidaProdutoNfe().pagamentoMeioProperty().setValue(NfeCobrancaDuplicataPagamentoMeio.OUTROS.getCod());
        getSaidaProdutoNfe().informacaoAdicionalProperty().setValue(
                String.format(MYINFNFE.getMyConfig().getInfAdic(),
                        ServiceMascara.getMoeda(saidaProduto.getSaidaProdutoProdutoList().stream()
                                .map(saidaProdutoProduto -> saidaProdutoProduto.vlrBrutoProperty().getValue()
                                        .subtract(saidaProdutoProduto.vlrDescontoProperty().getValue()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add), 2),
                        (saidaProduto.contasAReceberProperty().getValue().dtVencimentoProperty().getValue() != null)
                                ? String.format(" dt. Venc.: %s",
                                saidaProduto.contasAReceberProperty().getValue()
                                        .dtVencimentoProperty().getValue().format(DTF_DATA))
                                : "",
                        TCONFIG.getInfLoja().getBanco(),
                        TCONFIG.getInfLoja().getAgencia(), TCONFIG.getInfLoja().getContaCorrente())
                        .toUpperCase());
        getSaidaProdutoNfe().digValProperty().setValue(null);
        getSaidaProdutoNfe().xmlAssinaturaProperty().setValue(null);
        getSaidaProdutoNfe().xmlConsReciboProperty().setValue(null);
        getSaidaProdutoNfe().xmlProtNfeProperty().setValue(null);

        getSaidaProdutoNfe().chaveProperty().setValue(ServiceValidarDado.getChaveNfe(getSaidaProdutoNfe()));
        try {
            setSaidaProdutoNfe(new SaidaProdutoNfeDAO().merger(getSaidaProdutoNfe()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void addNumeroSerieUltimaNfe() {
        SaidaProdutoNfe nfeTemp;
        int num = 606, serie = 1;
        if ((nfeTemp = new SaidaProdutoNfeDAO().getAll(SaidaProdutoNfe.class, null, "numero DESC")
                .stream().findFirst().orElse(null)) != null) {
            num = nfeTemp.numeroProperty().getValue() + 1;
            serie = nfeTemp.serieProperty().getValue();
        }
        getSaidaProdutoNfe().serieProperty().setValue(serie);
        getSaidaProdutoNfe().numeroProperty().setValue(num);
    }


    /**
     * Begin Getters and Setters
     */


    public NFev400 getnFev400() {
        return nFev400;
    }

    public void setnFev400(NFev400 nFev400) {
        this.nFev400 = nFev400;
    }

    public SaidaProdutoNfe getSaidaProdutoNfe() {
        return saidaProdutoNfe;
    }

    public void setSaidaProdutoNfe(SaidaProdutoNfe saidaProdutoNfe) {
        this.saidaProdutoNfe = saidaProdutoNfe;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }


    /**
     * END Getters and Setters
     */
}
