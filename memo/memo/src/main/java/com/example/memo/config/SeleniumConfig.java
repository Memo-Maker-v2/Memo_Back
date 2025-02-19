package com.example.memo.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Configuration
public class SeleniumConfig {
  
  @Bean
  public WebDriver getChromeDriver() throws IOException, URISyntaxException {
    // chromedriver.exe 설정 (resources\drivers\chromedriver.exe)
    Path tempDir = Files.createTempDirectory("selenium");
    InputStream chromedriverStream = getClass().getClassLoader().getResourceAsStream("drivers/chromedriver.exe");
    if (chromedriverStream == null) {
      throw new RuntimeException("Chromedriver binary not found in resources/drivers");
    }
    File tempDriverFile = new File(tempDir.toFile(), "chromedriver.exe");
    Files.copy(chromedriverStream, tempDriverFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    chromedriverStream.close();
    System.setProperty("webdriver.chrome.driver", tempDriverFile.getAbsolutePath());
    
    // 전체 chrome-win64 폴더 복사 (resources\drivers\chrome-win64)
    URL chromeFolderUrl = getClass().getClassLoader().getResource("drivers/chrome-win64");
    if (chromeFolderUrl == null) {
      throw new RuntimeException("chrome-win64 folder not found in resources/drivers");
    }
    Path chromeFolderSource = Paths.get(chromeFolderUrl.toURI());
    Path tempChromeDir = Files.createTempDirectory("chrome");
    Path targetChromeFolder = tempChromeDir.resolve("chrome-win64");
    copyDirectory(chromeFolderSource, targetChromeFolder);
    
    // 복사한 폴더 내의 chrome.exe를 바이너리로 설정
    File chromeBinary = new File(targetChromeFolder.toFile(), "chrome.exe");
    if (!chromeBinary.exists()) {
      throw new RuntimeException("chrome.exe not found in the copied chrome-win64 folder");
    }
    
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setBinary(chromeBinary.getAbsolutePath());
    chromeOptions.addArguments("--start-maximized");
    chromeOptions.addArguments("--disable-gpu");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--headless"); // 테스트 시 필요에 따라 제거 가능
    
    return new ChromeDriver(chromeOptions);
  }
  
  // 폴더 전체를 재귀적으로 복사하는 헬퍼 메소드
  private void copyDirectory(Path source, Path target) throws IOException {
    Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path targetDir = target.resolve(source.relativize(dir));
        if (!Files.exists(targetDir)) {
          Files.createDirectory(targetDir);
        }
        return FileVisitResult.CONTINUE;
      }
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
