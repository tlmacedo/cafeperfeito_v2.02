package br.com.tlmacedo.cafeperfeito.model.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisSistema.TCONFIG;

public enum RelatorioTipo {

    RECIBO(0, String.format("/relatorio_v2.01/recibo_v%s.jasper", TCONFIG.getVersao())),
    NFE(1, String.format("/relatorio_v2.01/danfe_v%s.jasper", TCONFIG.getVersao()));

    private Integer cod;
    private String descricao;

    private RelatorioTipo(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static List<RelatorioTipo> getList() {
        List list = Arrays.asList(RelatorioTipo.values());
        Collections.sort(list, new Comparator<RelatorioTipo>() {
            @Override
            public int compare(RelatorioTipo e1, RelatorioTipo e2) {
                return e1.getDescricao().compareTo(e2.getDescricao());
            }
        });
        return list;
    }

    public Integer getCod() {
        return cod;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }

}
