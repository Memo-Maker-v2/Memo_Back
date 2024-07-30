package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import com.example.memo.dto.YoutubeResponseDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class YoutubeService {
  
  @Autowired
  private OpenAIUtils openAIUtils;
  
  @Value("${youtube.api.key}")
  private String apiKey;
  
  private final OkHttpClient httpClient = new OkHttpClient();
  
  // 유튜브 URL을 열고 자막을 추출한 뒤 요약본을 생성하고 DTO로 반환하는 메소드
  public YoutubeResponseDto processYoutubeUrl(String url, String memberEmail) throws IOException, JSONException {
    if (url.contains("youtube.com/shorts/")) {
      url = url.replace("youtube.com/shorts/", "youtube.com/watch?v=");
    }
    
    WebDriver driver = initializeWebDriver();
    String videoTitle = "";
    String thumbnailUrl = "";
    String fullScript = "";
    String summary = "";
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    
    try {
      // 비디오 제목과 썸네일 URL 가져오기
      videoTitle = getVideoTitle(url);
      thumbnailUrl = getThumbnailUrl(url);
      
      // URL 열기
      driver.get(url);
      driver.manage().window().setSize(new Dimension(1920, 1080));
      waitForPageLoad(driver);
      
      // "더보기" 버튼 클릭
      clickMoreButton(driver);
      sleepWithExceptionHandling(2000);
      
      // "스크립트 표시" 버튼 클릭
      clickScriptButton(driver);
      
      // 자막 추출
      fullScript = extractTranscript(driver);
      String subtitleLanguage = extractSubtitleLanguage(driver);
      
      // 자막을 GPT-3.5-turbo를 통해 요약본 생성
      summary = generateSummary(fullScript, subtitleLanguage);
      
    } catch (Exception e) {
      System.err.println("An error occurred: " + e.getMessage());
    } finally {
      closeBrowser(driver);
    }
    
    return new YoutubeResponseDto(videoTitle, fullScript, url, thumbnailUrl, summary, memberEmail, date);
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
      
      String fullScript = transcriptBuilder.toString();
      System.out.println("Extracted FULL Transcript: \n" + fullScript); // 로그 추가
      return fullScript;
      
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
      System.out.println("자막 언어: " + languageText);
      return languageText;
    } catch (Exception e) {
      System.err.println("Failed to extract subtitle language: " + e.getMessage());
      return "Unknown";
    }
  }
  
  // 자막을 GPT-3.5-turbo API를 통해 요약본을 생성하는 메소드
  public String generateSummary(String transcript, String subtitleLanguage) {
    try {
      String summary = openAIUtils.summarizeTranscript(transcript, subtitleLanguage);
      System.out.println("Generated Summary in " + subtitleLanguage + ": \n" + summary);
      return summary;
    } catch (IOException e) {
      System.err.println("Failed to generate summary: " + e.getMessage());
      return "Summary generation failed.";
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
  
  // 유튜브 영상 제목 가져오기 메소드
  public String getVideoTitle(String videoUrl) throws IOException, JSONException {
    String videoId = extractVideoId(videoUrl);
    String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;
    
    Request request = new Request.Builder()
            .url(apiUrl)
            .build();
    
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
      
      JSONObject jsonResponse = new JSONObject(response.body().string());
      String title = jsonResponse.getJSONArray("items")
              .getJSONObject(0)
              .getJSONObject("snippet")
              .getString("title");
      
      // 제목을 출력
      System.out.println("Video Title: " + title);
      
      // 현재 날짜 출력
      LocalDate currentDate = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String formattedDate = currentDate.format(formatter);
      System.out.println("Current Date: " + formattedDate);
      
      return title;
    }
  }
  
  // 유튜브 썸네일 이미지 주소 가져오기 메소드
  public String getThumbnailUrl(String videoUrl) throws IOException, JSONException {
    String videoId = extractVideoId(videoUrl);
    String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;
    
    Request request = new Request.Builder()
            .url(apiUrl)
            .build();
    
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
      
      JSONObject jsonResponse = new JSONObject(response.body().string());
      String thumbnailUrl = jsonResponse.getJSONArray("items")
              .getJSONObject(0)
              .getJSONObject("snippet")
              .getJSONObject("thumbnails")
              .getJSONObject("high")  // 썸네일의 품질을 선택 ("default", "medium", "high")
              .getString("url");
      
      // 썸네일 URL을 출력
      System.out.println("Thumbnail URL: " + thumbnailUrl);
      
      return thumbnailUrl;
    }
  }
  
  // 유튜브 URL에서 비디오 ID 추출
  private String extractVideoId(String videoUrl) {
    String[] parts = videoUrl.split("v=");
    if (parts.length > 1) {
      return parts[1];
    } else {
      throw new IllegalArgumentException("Invalid YouTube URL");
    }
  }
}
