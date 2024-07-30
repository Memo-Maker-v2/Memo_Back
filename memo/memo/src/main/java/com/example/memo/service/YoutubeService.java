package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import com.example.memo.dto.YoutubeResponseDto;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.YoutubeVideoRepository;
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
  
  @Autowired
  private YoutubeVideoRepository youtubeVideoRepository;
  
  private final OkHttpClient httpClient = new OkHttpClient();
  
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
      videoTitle = getVideoTitle(url);
      thumbnailUrl = getThumbnailUrl(url);
      
      driver.get(url);
      driver.manage().window().setSize(new Dimension(1920, 1080));
      waitForPageLoad(driver);
      
      clickMoreButton(driver);
      sleepWithExceptionHandling(2000);
      
      clickScriptButton(driver);
      
      fullScript = extractTranscript(driver);
      String subtitleLanguage = extractSubtitleLanguage(driver);
      
      summary = generateSummary(fullScript, subtitleLanguage);
      
      VideoEntity videoEntity = new VideoEntity();
      videoEntity.setSummary(summary);
      videoEntity.setFullScript(fullScript);
      videoEntity.setVideoUrl(url);
      videoEntity.setThumbnailUrl(thumbnailUrl);
      videoEntity.setVideoTitle(videoTitle);
      videoEntity.setMemberEmail(memberEmail);
      videoEntity.setDocumentDate(LocalDate.now());
      
      youtubeVideoRepository.save(videoEntity);
      
    } catch (Exception e) {
      System.err.println("An error occurred: " + e.getMessage());
    } finally {
      closeBrowser(driver);
    }
    
    return new YoutubeResponseDto(videoTitle, fullScript, url, thumbnailUrl, summary, memberEmail, date);
  }
  
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
    chromeOptions.addArguments("--disable-gpu");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--headless");
    
    return new ChromeDriver(chromeOptions);
  }
  
  private void waitForPageLoad(WebDriver driver) {
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }
  
  private void clickMoreButton(WebDriver driver) {
    try {
      WebElement moreButton = driver.findElement(By.cssSelector("tp-yt-paper-button#expand"));
      moreButton.click();
      System.out.println("Clicked 'More' button");
    } catch (Exception e) {
      System.err.println("Failed to find or click 'More' button: " + e.getMessage());
    }
  }
  
  private void clickScriptButton(WebDriver driver) {
    try {
      WebElement scriptButton = driver.findElement(By.cssSelector("button[aria-label='스크립트 표시']"));
      
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", scriptButton);
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", scriptButton);
      
      System.out.println("Clicked '스크립트 표시' button");
    } catch (Exception e) {
      System.err.println("Failed to find or click '스크립트 표시' button: " + e.getMessage());
    }
  }
  
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
      System.out.println("Extracted FULL Transcript: \n" + fullScript);
      return fullScript;
      
    } catch (Exception e) {
      System.err.println("Failed to extract transcript: " + e.getMessage());
    }
    return "";
  }
  
  private String extractSubtitleLanguage(WebDriver driver) {
    try {
      WebElement languageElement = driver.findElement(By.cssSelector("div#label-text.style-scope.yt-dropdown-menu"));
      String languageText = languageElement.getText();
      languageText = languageText.replace(" (자동 생성됨)", "");
      System.out.println("자막 언어: " + languageText);
      return languageText;
    } catch (Exception e) {
      System.err.println("Failed to extract subtitle language: " + e.getMessage());
      return "Unknown";
    }
  }
  
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
  
  private void closeBrowser(WebDriver driver) {
    if (driver != null) {
      driver.quit();
    }
  }
  
  private void sleepWithExceptionHandling(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
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
      
      System.out.println("Video Title: " + title);
      
      LocalDate currentDate = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String formattedDate = currentDate.format(formatter);
      System.out.println("Current Date: " + formattedDate);
      
      return title;
    }
  }
  
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
              .getJSONObject("high")
              .getString("url");
      
      System.out.println("Thumbnail URL: " + thumbnailUrl);
      
      return thumbnailUrl;
    }
  }
  
  private String extractVideoId(String videoUrl) {
    String[] parts = videoUrl.split("v=");
    if (parts.length > 1) {
      return parts[1];
    } else {
      throw new IllegalArgumentException("Invalid YouTube URL");
    }
  }
}
