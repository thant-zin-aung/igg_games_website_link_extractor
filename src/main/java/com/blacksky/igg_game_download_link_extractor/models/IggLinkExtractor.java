package com.blacksky.igg_game_download_link_extractor.models;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IggLinkExtractor {
    private Label progressInfoLabel;
    private TextArea downloadLinkArea;
    private Label extractLabel;
    private Elements partLinks;
    private WebDriver driver;
    private Element partLinkWrapper;
    private int totalPart;
    private final String REDIRECT_BASE_URI = "https://urlbluemedia.shop/get-url.php?url=";
    private List<String> downloadLinks;

    public IggLinkExtractor() {
        System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER"));
        downloadLinks = new ArrayList<>();
    }



    public void setProgressInfo(Label progressInfoLabel) {
        this.progressInfoLabel = progressInfoLabel;
    }

    public void setDownloadLinkArea(TextArea downloadLinkArea) {
        this.downloadLinkArea = downloadLinkArea;
    }

    public void setExtractLabel(Label extractLabel ) { this.extractLabel = extractLabel; }

    public void extractLinks(String url, String cloudProviderName) throws IOException {
        cleanCache();
        Document document = Jsoup.connect(url).get();
        Elements partLinkWrappers = document.getElementsByClass("uk-margin-medium-top");
        for (Element linkWrapper : partLinkWrappers ) {
            if ( linkWrapper.attr("property").equals("text") ) {
                partLinkWrapper = linkWrapper;
                break;
            }
        }
        partLinkWrappers = partLinkWrapper.getElementsByTag("p");
        for ( Element linkWrapper : partLinkWrappers ) {
            try {
                String cloudStorageProvider = linkWrapper.getElementsByClass("uk-heading-bullet").get(0).text();
                if ( cloudStorageProvider.toLowerCase().contains(cloudProviderName.toLowerCase()) ) {
                    partLinkWrapper = linkWrapper;
                    break;
                }
            } catch (IndexOutOfBoundsException ioe) {
                continue;
            }
        }
        partLinks = partLinkWrapper.getElementsByTag("a");
        totalPart = partLinks.size();
        updateProgressInfo();


//        for ( Element link : partLinks ) {
//            Thread scraperThread = new Thread(()->{
//                // Order is important...
//                try {
//                    System.setProperty("webdriver.chrome.driver", "C:\\WebDrivers\\chromedriver.exe");
//                    WebDriver driver = new ChromeDriver();
//                    driver.get(link.attr("href"));
//                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2000));
//                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nut")));
//                    Document doc = Jsoup.parse(driver.getPageSource());
//                    Element form = doc.getElementsByTag("form").get(0);
//                    String gameRedirectLink = form.getElementById("url").attr("value");
//                    driver.close();
//                    String gameActualLink = Jsoup.connect(REDIRECT_BASE_URI+gameRedirectLink).get().baseUri();
//                    downloadLinks.add(gameActualLink);
//                    updateProgressInfo();
//                    if ( totalPart == downloadLinks.size() ) {
//                        downloadLinks.sort(Comparator.comparing(o -> o.substring(o.length() - 9)));
////                        downloadLinks.forEach(System.out::println);
//                        updateDownloadLinkArea();
//                    }
//                } catch (IOException ioe) {
//                    ioe.printStackTrace();
//                }
//                // Order is important...
//            });
//            executor.execute(scraperThread);
//        }
//        executor.shutdown();
        processExtract();
    }

    private void processExtract() {
        try {
            driver = new ChromeDriver();
            driver.manage().window().setSize(new Dimension(480, 360));
        } catch (Exception e) {
            System.out.println("Chrome driver error");
            Platform.runLater(() -> {
                extractLabel.setTextFill(Color.RED);
                extractLabel.setText("# CHROME_DRIVER error");
            });
            return;
        }
        new Thread(() -> {
            for ( int count = 0 ; count < totalPart ; count++ ) {
                ((JavascriptExecutor) driver).executeScript("window.open()");
                ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
                driver.switchTo().window(tabs.get(tabs.size() - 1));
                // Load the URL in the new tab/window
                driver.get(partLinks.get(count).attr("href"));
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2000));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nut")));
                Document doc = Jsoup.parse(driver.getPageSource());
                Element form = doc.getElementsByTag("form").get(0);
                String gameRedirectLink = form.getElementById("url").attr("value");
                String gameActualLink = null;
                try {
                    gameActualLink = Jsoup.connect(REDIRECT_BASE_URI+gameRedirectLink).get().baseUri();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                downloadLinks.add(gameActualLink);
                updateProgressInfo();
                if ( totalPart == downloadLinks.size() ) {
                    downloadLinks.sort(Comparator.comparing(o -> o.substring(o.length() - 9)));
                    updateDownloadLinkArea();
                    downloadLinks.forEach(System.out::println);
                }
            }
            driver.quit();
        }).start();
    }

    private void updateProgressInfo() {
        Platform.runLater(()->progressInfoLabel.setText(downloadLinks.size() +" / "+ totalPart));
    }

    private void updateDownloadLinkArea() {
        Platform.runLater(()->downloadLinkArea.setText(getExtractedDownloadLinks()));
    }

    public String getExtractedDownloadLinks() {
        StringBuilder downloadLinkBuilder = new StringBuilder();
        downloadLinks.forEach(link -> downloadLinkBuilder.append(link).append("\n"));
        return downloadLinkBuilder.toString();
    }

    private void cleanCache() {
        totalPart = 0;
        downloadLinks = new ArrayList<>();
    }
}
