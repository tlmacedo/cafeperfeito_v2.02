package br.com.tlmacedo.cafeperfeito.service.alert;

import javafx.scene.control.Button;

public class AlertOk {

    public AlertOk() {
        AlertMensagem.loadDialog();
        AlertMensagem.loadDialogPane();

        AlertMensagem.setBtnOk(new Button());
        AlertMensagem.getBtns().add(AlertMensagem.getBtnOk());

        AlertMensagem.addButton();
        AlertMensagem.getBtnOk().setDisable(false);

        AlertMensagem.getDialog().showAndWait();
    }
}
