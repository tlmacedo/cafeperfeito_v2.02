package br.com.tlmacedo.cafeperfeito.service;

import br.com.tlmacedo.service.ServiceAlertMensagem;
import javafx.concurrent.Task;

import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisSistema.TCONFIG;

public class ServiceSegundoPlano {


    public boolean executaListaTarefas(Task<?> task, String titulo) {
        ServiceAlertMensagem alertMensagem = new ServiceAlertMensagem(
                TCONFIG.getTimeOut(),
                ServiceVariaveisSistema.SPLASH_IMAGENS,
                TCONFIG.getPersonalizacao().getStyleSheets()
        );
        alertMensagem.setCabecalho(titulo);
        return alertMensagem.alertProgressBar(task, false);
    }

    public boolean execListaTarefas(Task<?> task) {
        try {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
