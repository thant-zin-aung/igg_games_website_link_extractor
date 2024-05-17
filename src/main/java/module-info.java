module com.blacksky.igg_game_download_link_extractor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.support;
    requires org.seleniumhq.selenium.remote_driver;
    requires org.seleniumhq.selenium.chrome_driver;
    requires dev.failsafe.core;
    requires com.google.common;


    opens com.blacksky.igg_game_download_link_extractor to javafx.fxml;
    exports com.blacksky.igg_game_download_link_extractor;
    exports com.blacksky.igg_game_download_link_extractor.cotrollers;
    opens com.blacksky.igg_game_download_link_extractor.cotrollers to javafx.fxml;
}