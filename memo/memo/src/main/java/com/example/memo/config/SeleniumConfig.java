package com.example.memo.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Configuration
public class SeleniumConfig {
  
  @Bean
  public WebDriver getChromeDriver() throws IOException {
    // `resources/drivers/chromedriver.exe`의 경로를 얻습니다.
    Path tempDir = Files.createTempDirectory("selenium");
    InputStream chromedriverStream = getClass().getClassLoader().getResourceAsStream("drivers/chromedriver.exe");
    
    if (chromedriverStream == null) {
      throw new RuntimeException("Chromedriver binary not found in resources.");
    }
    
    File tempFile = new File(tempDir.toFile(), "chromedriver.exe");
    Files.copy(chromedriverStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    chromedriverStream.close();
    
    System.setProperty("webdriver.chrome.driver", tempFile.getAbsolutePath());
    
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--start-maximized");
    chromeOptions.addArguments("--disable-gpu");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--headless"); // 헤드리스 모드 설정
    
    return new ChromeDriver(chromeOptions);
  }
}
