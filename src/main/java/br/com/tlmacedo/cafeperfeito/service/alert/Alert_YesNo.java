package br.com.tlmacedo.cafeperfeito.service.alert;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.io.Serializable;
import java.util.Optional;

public class Alert_YesNo extends AlertMensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    public Alert_YesNo(String cabecalho, String contextText, String icone) {
        setCabecalho(cabecalho);
        setContentText(contextText);
        if (icone != null)
            addImage(icone);

        loadDialog();
        loadDialogPane();

        setBtnYes(new Button());
        getBtns().add(getBtnYes());

        setBtnNo(new Button());
        getBtns().add(getBtnNo());

        addButton();

    }

    public Optional<ButtonType> retorno() {
        return getDialog().showAndWait();

    }
}
