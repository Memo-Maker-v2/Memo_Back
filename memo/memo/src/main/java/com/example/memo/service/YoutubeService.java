package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openqa.selenium.Dimension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

@Service
public class YoutubeService {
  
  @Autowired
  private OpenAIUtils openAIUtils;
  
  // 유튜브 URL을 열고 자막을 추출한 뒤 요약본을 생성하는 메소드
  public void openYoutubeUrl(String url) throws IOException {
    // URL이 쇼츠 URL인지 확인하고 일반 URL로 변환
    if (url.contains("youtube.com/shorts/")) {
      url = url.replace("youtube.com/shorts/", "youtube.com/watch?v=");
    }
    
    WebDriver driver = initializeWebDriver();
    
    try {
      // URL 열기
      driver.get(url);
      
      // 동적으로 브라우저 크기 조정
      driver.manage().window().setSize(new Dimension(1920, 1080));
      
      // 페이지 로딩 대기
      waitForPageLoad(driver);
      
      // "더보기" 버튼 클릭
      clickMoreButton(driver);
      
      // 버튼 클릭 후 일정 시간 대기
      sleepWithExceptionHandling(2000); // 2초 대기
      
      // "스크립트 표시" 버튼 클릭
      clickScriptButton(driver);
      
      // 자막 추출
      String transcript = extractTranscript(driver);
      
      // 자막 언어 추출
      String subtitleLanguage = extractSubtitleLanguage(driver);
      
      // 마지막 버튼 클릭 후 3초 대기
      sleepWithExceptionHandling(3000); // 3초 대기
      
      // 자막을 GPT-3.5-turbo를 통해 요약본 생성
      generateSummary(transcript, subtitleLanguage);
      
    } catch (Exception e) {
      // 오류가 발생하면 크롬 창 종료
      System.err.println("An error occurred: " + e.getMessage());
      closeBrowser(driver);
    } finally {
      // WebDriver 종료
      System.out.println("Closing WebDriver");
      closeBrowser(driver);
    }
  }
  
  // WebDriver 초기화 메소드
  private WebDriver initializeWebDriver() throws IOException {
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
    chromeOptions.addArguments("--disable-gpu"); // GPU 가속 끄기
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--headless"); // headless 모드 활성화
    
    return new ChromeDriver(chromeOptions);
  }
  
  // 페이지 로딩 대기 메소드
  private void waitForPageLoad(WebDriver driver) {
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }
  
  // "더보기" 버튼 클릭 메소드
  private void clickMoreButton(WebDriver driver) {
    try {
      WebElement moreButton = driver.findElement(By.cssSelector("tp-yt-paper-button#expand"));
      moreButton.click();
      System.out.println("Clicked 'More' button");
    } catch (Exception e) {
      System.err.println("Failed to find or click 'More' button: " + e.getMessage());
    }
  }
  
  // "스크립트 표시" 버튼 클릭 메소드
  private void clickScriptButton(WebDriver driver) {
    try {
      WebElement scriptButton = driver.findElement(By.cssSelector("button[aria-label='스크립트 표시']"));
      
      // 요소가 화면에 보이도록 스크롤
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", scriptButton);
      
      // JavaScript Executor를 사용하여 클릭
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", scriptButton);
      
      System.out.println("Clicked '스크립트 표시' button");
    } catch (Exception e) {
      System.err.println("Failed to find or click '스크립트 표시' button: " + e.getMessage());
    }
  }
  
  // 자막 추출 메소드
  private String extractTranscript(WebDriver driver) {
    StringBuilder transcriptBuilder = new StringBuilder();
    try {
      List<WebElement> transcriptSegments = driver.findElements(By.cssSelector("ytd-transcript-segment-renderer"));
      
      for (WebElement segment : transcriptSegments) {
        WebElement timestampElement = segment.findElement(By.cssSelector(".segment-start-offset .segment-timestamp"));
        String ts = timestampElement.getText();
        WebElement textElement = segment.findElement(By.cssSelector(".segment-text"));
        String txt = textElement.getText();
        
        transcriptBuilder.append("TS: ").append(ts).append(" | TXT: ").append(txt).append("\n");
      }
      
      String transcript = transcriptBuilder.toString();
      System.out.println("Extracted FULL Transcript: \n" + transcript); // 로그 추가
      return transcript;
      
    } catch (Exception e) {
      System.err.println("Failed to extract transcript: " + e.getMessage());
    }
    return "";
  }
  
  // 자막 언어 추출 메소드
  private String extractSubtitleLanguage(WebDriver driver) {
    try {
      WebElement languageElement = driver.findElement(By.cssSelector("div#label-text.style-scope.yt-dropdown-menu"));
      String languageText = languageElement.getText();
      languageText = languageText.replace(" (자동 생성됨)", "");  // "자동 생성됨" 텍스트 제거
      System.out.println("Subtitle Language: " + languageText);
      return languageText;
    } catch (Exception e) {
      System.err.println("Failed to extract subtitle language: " + e.getMessage());
      return "Unknown";
    }
  }
  
  // 자막을 GPT-3.5-turbo API를 통해 요약본을 생성하는 메소드
  public void generateSummary(String transcript, String subtitleLanguage) {
    try {
      String summary = openAIUtils.summarizeTranscript(transcript, subtitleLanguage);
      System.out.println("Generated Summary in " + subtitleLanguage + ": \n" + summary);
    } catch (IOException e) {
      System.err.println("Failed to generate summary: " + e.getMessage());
    }
  }
  
  // 브라우저 종료 메소드
  private void closeBrowser(WebDriver driver) {
    if (driver != null) {
      driver.quit();
    }
  }
  
  // 예외 처리를 포함한 대기 메소드
  private void sleepWithExceptionHandling(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
