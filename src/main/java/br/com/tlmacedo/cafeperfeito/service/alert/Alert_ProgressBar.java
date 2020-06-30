package br.com.tlmacedo.cafeperfeito.service.alert;

import br.com.tlmacedo.nfe.service.ExceptionNFe;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.io.Serializable;

import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisNFe.MYINFNFE;

public class Alert_ProgressBar extends AlertMensagem implements Serializable {

    public static final long serialVersionUID = 1L;

    public Alert_ProgressBar(Task<?> task, String titulo, boolean isWait) throws ExceptionNFe {
        setCabecalho(titulo);
        setTask(task);
        setWaitReturn(isWait);

        setTimeline(newTimeLine(0));

        loadDialog();
        loadDialogPane();
        getDialogPane().getStyleClass().remove("alertMsg_return");
        getDialogPane().getStyleClass().add("alertMsg_progress");


        if (isWait) {
            setBtnOk(new Button());
            getBtns().add(getBtnOk());
        }
        setBtnCancel(new Button());
        getBtns().add(getBtnCancel());

        addButton();

        getDialogPane().setContent(contentProgress(!isWait));

        startContagemRegressiva();

        getBtnCancel().setOnAction(actionEvent -> getTask().cancel());

        getTask().setOnFailed(event -> {
            setRetornoValido(false);
            dialogClose();
            try {
                throw new ExceptionNFe(MYINFNFE.getMyConfig().getTpAmb().intValue(), 99, "erroAlert");
            } catch (ExceptionNFe exceptionNFe) {
                exceptionNFe.printStackTrace();
            }
        });

        getTask().setOnCancelled(event -> {
            setRetornoValido(false);
            dialogClose();
        });

        getTask().setOnSucceeded(event -> {
            setRetornoValido(true);
            getTimeline().stop();
            if (isWait) {
                getProgressBar().setProgress(100);
                addImage("/image/sis_logo_240dp.png");
                getBtnOk().setDisable(false);
            } else {
                dialogClose();
            }
        });

        getTimeline().setOnFinished(event -> {
            setRetornoValido(false);
            dialogClose();
            return;
        });

        setThread(new Thread(getTask()));
        getThread().setDaemon(true);
        getThread().start();

        getDialog().showAndWait();

    }

    public boolean retorno() {
        return isRetornoValido();

    }

}
