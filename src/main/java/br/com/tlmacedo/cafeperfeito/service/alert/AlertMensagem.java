package br.com.tlmacedo.service.alert;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisSistema.SPLASH_IMAGENS;
//import static br.com.tlmacedo.cafeperfeito.service.ServiceVariaveisSistema.TCONFIG;

public class AlertMensagem {

    private static Thread thread;
    private static Dialog dialog = new Dialog();
    private static DialogPane dialogPane = dialog.getDialogPane();
    private static Task<?> task;
    private static String cabecalho = null, contentText = null, strIco = null;

    private static boolean retornoWait = false;
    private static boolean retornoProgressBar = false;
    private static HBox hBox = new HBox();
    private static VBox vBox = new VBox();
    private static ImageView imageView;
    private static List<Button> btns = new ArrayList<>();
    private static Button btnOk, btnCancel, btnYes, btnNo, btnApply, btnClose, btnFinish;
    private static Label lblMsg = new Label(), lblContagem = new Label();
    private static int TIME_OUT;
    private static Timeline timeline;
    private static ProgressBar progressBar;
    private static ProgressIndicator progressIndicator;
    private static List SPLASH_IMAGENS;
    private static String PATH_STYLE_SHEETS;

    public AlertMensagem(int timeOut, List splashImagens, String pathStyleSheets) {
        setTimeOut(timeOut);
        setSplashImagens(splashImagens);
        setPathStyleSheets(pathStyleSheets);

    }

    public Optional<ButtonType> alertYesNo() {
        loadDialog();
        loadDialogPane();

        setBtnYes(new Button());
        getBtns().add(getBtnYes());

        setBtnNo(new Button());
        getBtns().add(getBtnNo());

        addButton();

//        getDialog().setResultConverter(o -> {
//            if (o == ButtonType.YES)
//                return true;
//            return false;
//        });

        return getDialog().showAndWait();
    }


    public Optional<ButtonType> alertYesNoCancel() {
        loadDialog();
        loadDialogPane();

        setBtnYes(new Button());
        getBtns().add(getBtnYes());

        setBtnNo(new Button());
        getBtns().add(getBtnNo());

        setBtnCancel(new Button());
        getBtns().add(getBtnCancel());

        addButton();

//        getDialog().setResultConverter(o -> {
//            if (o == ButtonType.YES)
//                return true;
//            return false;
//        });


//        Optional<ButtonType> result = getDialog().showAndWait();
//        if (result.get() == ButtonType.YES)
//            return true;
//        else if (result.get() == ButtonType.NO)
//            return false;
//        else
//            return null;

        return getDialog().showAndWait();
    }

    public boolean alertProgressBar(Task<?> task, boolean isWait) {
        setTask(task);
        setRetornoWait(isWait);

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
            setRetornoProgressBar(false);
            dialogClose();
        });

        getTask().setOnCancelled(event -> {
            setRetornoProgressBar(false);
            dialogClose();
        });

        getTask().setOnSucceeded(event -> {
            setRetornoProgressBar(true);
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
            setRetornoProgressBar(false);
            dialogClose();
            return;
        });

        setThread(new Thread(getTask()));
        getThread().setDaemon(true);
        getThread().start();

        getDialog().showAndWait();

        return isRetornoProgressBar();
    }

    /**
     * Begin Returns
     */

    private VBox contentProgress(boolean loading) {
        setProgressBar(new ProgressBar());
        getProgressBar().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        getProgressBar().prefWidthProperty().bind(getDialogPane().widthProperty().subtract(20));

        //getLblMsg().prefWidthProperty().bind(getDialogPane().widthProperty().subtract(15));
        getLblMsg().getStyleClass().add("dialog-update-msg");

        gethBox().setSpacing(7);
        gethBox().setAlignment(Pos.CENTER_LEFT);
        getvBox().setAlignment(Pos.CENTER);

        if (loading) {
            int random = (int) (Math.random() * SPLASH_IMAGENS.size());
            addImage(SPLASH_IMAGENS.get(random).toString());
            getImageView().setClip(new Circle(120, 120, 120));
            getvBox().getChildren().add(getImageView());
            getvBox().getChildren().add(new Label(""));
            getvBox().getChildren().add(new Label(""));
            HBox hBox1 = new HBox();
            hBox1.getChildren().addAll(getLblMsg(), getLblContagem());
            getvBox().getChildren().add(hBox1);
        } else {
            setProgressIndicator(new ProgressIndicator());
            getProgressIndicator().setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            getProgressIndicator().setPrefSize(25, 25);
            gethBox().getChildren().addAll(getProgressIndicator(), getLblMsg());
            getvBox().getChildren().add(gethBox());
        }

        if (!isRetornoProgressBar()) {
            if (getTask().getTotalWork() > 1)
                getProgressBar().progressProperty().bind(getTask().progressProperty());
        }

        getvBox().getChildren().add(getProgressBar());
        return getvBox();
    }

    /**
     * END Returns
     */

    /**
     * Begin Voids
     */

    public static void dialogClose() {
        getDialog().setResult(ButtonType.CANCEL);
        getDialog().close();
    }

    public static void loadDialog() {
        getDialogPane().getStylesheets().add(AlertMensagem.class.getResource(getPathStyleSheets()).toString());
        getDialogPane().getButtonTypes().clear();
        getDialogPane().getStyleClass().add("alertMsg_return");
    }

    public static void loadDialogPane() {
        getDialogPane().setHeaderText(getCabecalho());
        getDialogPane().setContentText(getContentText());
        if (getStrIco() != null)
            getDialog().setGraphic(new ImageView(AlertMensagem.class.getResource(getStrIco()).toString()));
    }

    public static void addButton() {
        for (Button btn : getBtns()) {
            if (btn == getBtnOk()) {
                getDialogPane().getButtonTypes().add(ButtonType.OK);
                setBtnOk((Button) getDialogPane().lookupButton(ButtonType.OK));
                getBtnOk().setDisable(true);
                getBtnOk().setDefaultButton(true);
            } else if (btn == getBtnCancel()) {
                getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                setBtnCancel((Button) getDialogPane().lookupButton(ButtonType.CANCEL));
                getBtnCancel().setCancelButton(true);
            } else if (btn == getBtnYes()) {
                getDialogPane().getButtonTypes().add(ButtonType.YES);
                setBtnYes((Button) getDialogPane().lookupButton(ButtonType.YES));
                getBtnYes().setDefaultButton(true);
            } else if (btn == getBtnNo()) {
                getDialogPane().getButtonTypes().add(ButtonType.NO);
                setBtnNo((Button) getDialogPane().lookupButton(ButtonType.NO));
                if (!getBtns().contains(getBtnCancel()))
                    getBtnNo().setCancelButton(true);
            }
        }
    }

    public static void addImage(String pathImg) {
        if (getImageView() == null)
            setImageView(new ImageView());
        getImageView().setImage(new Image(AlertMensagem.class.getResource(pathImg).toString()));
    }

    public static void startContagemRegressiva() {
        getTimeline().setCycleCount(getTimeOut() * 10);
        getTimeline().play();
    }

    public static Timeline newTimeLine(int tempo) {
        if (tempo > 0)
            setTimeOut(tempo);
        getLblMsg().textProperty().bind(getTask().messageProperty());
        final String[] pontos = {""}, contagem = {""};
        final int[] i = {0};

        return new Timeline(new KeyFrame(
                Duration.millis(100),
                ae -> {
                    if (i[0] % 10 == 1)
                        if (pontos[0].length() < 3)
                            pontos[0] += ".";
                        else
                            pontos[0] = "";
                    i[0]++;
                    contagem[0] = String.format("(%d) %s", (getTimeOut() - (i[0] / 10)), pontos[0]);
                    getLblContagem().setText(contagem[0]);
                }));
    }


    /**
     * END Voids
     */


    /**
     * Begin Getters and Setters
     */

    public static Thread getThread() {
        return thread;
    }

    public static void setThread(Thread thread) {
        AlertMensagem.thread = thread;
    }

    public static Dialog getDialog() {
        return dialog;
    }

    public static void setDialog(Dialog dialog) {
        AlertMensagem.dialog = dialog;
    }

    public static DialogPane getDialogPane() {
        return dialogPane;
    }

    public static void setDialogPane(DialogPane dialogPane) {
        AlertMensagem.dialogPane = dialogPane;
    }

    public static Task<?> getTask() {
        return task;
    }

    public static void setTask(Task<?> task) {
        AlertMensagem.task = task;
    }

    public static String getCabecalho() {
        return cabecalho;
    }

    public static void setCabecalho(String cabecalho) {
        AlertMensagem.cabecalho = cabecalho;
    }

    public static String getContentText() {
        return contentText;
    }

    public static void setContentText(String contentText) {
        AlertMensagem.contentText = contentText;
    }

    public static String getStrIco() {
        return strIco;
    }

    public static void setStrIco(String strIco) {
        AlertMensagem.strIco = strIco;
    }

    public static boolean isRetornoWait() {
        return retornoWait;
    }

    public static void setRetornoWait(boolean retornoWait) {
        AlertMensagem.retornoWait = retornoWait;
    }

    public static boolean isRetornoProgressBar() {
        return retornoProgressBar;
    }

    public static void setRetornoProgressBar(boolean retornoProgressBar) {
        AlertMensagem.retornoProgressBar = retornoProgressBar;
    }

    public static HBox gethBox() {
        return hBox;
    }

    public static void sethBox(HBox hBox) {
        AlertMensagem.hBox = hBox;
    }

    public static VBox getvBox() {
        return vBox;
    }

    public static void setvBox(VBox vBox) {
        AlertMensagem.vBox = vBox;
    }

    public static ImageView getImageView() {
        return imageView;
    }

    public static void setImageView(ImageView imageView) {
        AlertMensagem.imageView = imageView;
    }

    public static List<Button> getBtns() {
        return btns;
    }

    public static void setBtns(List<Button> btns) {
        AlertMensagem.btns = btns;
    }

    public static Button getBtnOk() {
        return btnOk;
    }

    public static void setBtnOk(Button btnOk) {
        AlertMensagem.btnOk = btnOk;
    }

    public static Button getBtnCancel() {
        return btnCancel;
    }

    public static void setBtnCancel(Button btnCancel) {
        AlertMensagem.btnCancel = btnCancel;
    }

    public static Button getBtnYes() {
        return btnYes;
    }

    public static void setBtnYes(Button btnYes) {
        AlertMensagem.btnYes = btnYes;
    }

    public static Button getBtnNo() {
        return btnNo;
    }

    public static void setBtnNo(Button btnNo) {
        AlertMensagem.btnNo = btnNo;
    }

    public static Button getBtnApply() {
        return btnApply;
    }

    public static void setBtnApply(Button btnApply) {
        AlertMensagem.btnApply = btnApply;
    }

    public static Button getBtnClose() {
        return btnClose;
    }

    public static void setBtnClose(Button btnClose) {
        AlertMensagem.btnClose = btnClose;
    }

    public static Button getBtnFinish() {
        return btnFinish;
    }

    public static void setBtnFinish(Button btnFinish) {
        AlertMensagem.btnFinish = btnFinish;
    }

    public static Label getLblMsg() {
        return lblMsg;
    }

    public static void setLblMsg(Label lblMsg) {
        AlertMensagem.lblMsg = lblMsg;
    }

    public static Label getLblContagem() {
        return lblContagem;
    }

    public static void setLblContagem(Label lblContagem) {
        AlertMensagem.lblContagem = lblContagem;
    }

    public static int getTimeOut() {
        return TIME_OUT;
    }

    public static void setTimeOut(int timeOut) {
        TIME_OUT = timeOut;
    }

    public static Timeline getTimeline() {
        return timeline;
    }

    public static void setTimeline(Timeline timeline) {
        AlertMensagem.timeline = timeline;
    }

    public static ProgressBar getProgressBar() {
        return progressBar;
    }

    public static void setProgressBar(ProgressBar progressBar) {
        AlertMensagem.progressBar = progressBar;
    }

    public static ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public static void setProgressIndicator(ProgressIndicator progressIndicator) {
        AlertMensagem.progressIndicator = progressIndicator;
    }

    public static List getSplashImagens() {
        return SPLASH_IMAGENS;
    }

    public static void setSplashImagens(List splashImagens) {
        SPLASH_IMAGENS = splashImagens;
    }

    public static String getPathStyleSheets() {
        return PATH_STYLE_SHEETS;
    }

    public static void setPathStyleSheets(String pathStyleSheets) {
        PATH_STYLE_SHEETS = pathStyleSheets;
    }

    /**
     * END Getters and Setters
     */

}
