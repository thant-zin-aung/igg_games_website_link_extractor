package com.blacksky.igg_game_download_link_extractor.cotrollers;

import com.blacksky.igg_game_download_link_extractor.models.IggLinkExtractor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainController {
    private IggLinkExtractor iggLinkExtractor;

    private Map<String,String> availableCloudProviderMap;
    @FXML
    private ComboBox<String> cloudProvider;
    @FXML
    private Spinner<Integer> threadBox;
    @FXML
    private TextField gameInfoUrlBox;
    @FXML
    private Label progressInfoLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label extractLabel;
    @FXML
    private TextArea downloadLinkArea;
    @FXML
    private Button copyClipboardButton;

    @FXML
    public void initialize() {
        iggLinkExtractor = new IggLinkExtractor();
        iggLinkExtractor.setProgressInfo(progressInfoLabel);
        iggLinkExtractor.setDownloadLinkArea(downloadLinkArea);
        iggLinkExtractor.setExtractLabel(extractLabel);

        availableCloudProviderMap = new LinkedHashMap<>();
        availableCloudProviderMap.put("megaup","MegaUp.net");
        availableCloudProviderMap.put("mega","Mega.nz");
        availableCloudProviderMap.put("1fichier","1Fichier");
        availableCloudProviderMap.put("gofile","GoFile");
        availableCloudProviderMap.put("hexupload","HexUpload");
        availableCloudProviderMap.put("rapidgator","Rapidgator");
        availableCloudProviderMap.put("uptobox","Uptobox");
        availableCloudProviderMap.put("sendcm","SendCM");
        availableCloudProviderMap.put("clicknupload","Clicknupload");
        availableCloudProviderMap.forEach((key, value) -> cloudProvider.getItems().add(value));
        cloudProvider.getSelectionModel().selectFirst();

        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        spinnerValueFactory.setValue(5);
        threadBox.setValueFactory(spinnerValueFactory);

        progressIndicator.setVisible(false);
        extractLabel.setVisible(false);
        listenDownloadLinkAreaChanges();
    }

    @FXML
    public void clickOnExtractButton() {
        progressIndicator.setVisible(true);
        extractLabel.setVisible(true);
        int numberOfThread = threadBox.getValue();
        String selectedCloudProvider = cloudProvider.getSelectionModel().getSelectedItem();
        String gameInfoUrl = gameInfoUrlBox.getText();

        try {
            iggLinkExtractor.extractLinks(gameInfoUrl,selectedCloudProvider);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public void clickOnCopyToClipboardButton() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(iggLinkExtractor.getExtractedDownloadLinks());
        clipboard.setContent(clipboardContent);
        copyClipboardButton.setText("Copied!");
    }

    private void listenDownloadLinkAreaChanges() {
        downloadLinkArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                extractLabel.setVisible(false);
                progressIndicator.setVisible(false);
            }
        });
    }


}





















