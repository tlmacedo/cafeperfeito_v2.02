package br.com.tlmacedo.cafeperfeito.service;

import br.com.tlmacedo.cafeperfeito.service.alert.Alert_ProgressBar;
import javafx.concurrent.Task;

public class ServiceSegundoPlano {


    public boolean executaListaTarefas(Task<?> task, String titulo) throws Exception {
        return new Alert_ProgressBar(task, titulo, false).retorno();
    }


}
