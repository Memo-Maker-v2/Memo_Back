package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import com.example.memo.dto.YoutubeResponseDto;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.YoutubeVideoRepository;
import okhttp3.*;
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
  
  private static final int MAX_RETRIES = 3;
  private static final long RETRY_DELAY_MS = 3000;
  private static final String CHECK_DUPLICATE_URL = "http://localhost:8080/api/v1/video/check-duplicate";
  
  private boolean isVideoDuplicate(String url, String memberEmail) throws IOException {
    String requestBody = String.format("{\"videoUrl\": \"%s\", \"memberEmail\": \"%s\"}", url, memberEmail);
    RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));
    Request request = new Request.Builder().url(CHECK_DUPLICATE_URL).post(body).build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      String responseBody = response.body().string();
      System.out.println("Server response: " + responseBody);
      if ("true".equalsIgnoreCase(responseBody.trim())) {
        return true;
      } else if ("false".equalsIgnoreCase(responseBody.trim())) {
        return false;
      } else {
        throw new IOException("Unexpected response body: " + responseBody);
      }
    }
  }
  
  public YoutubeResponseDto processYoutubeUrl(String url, String memberEmail) throws IOException, JSONException {
    String youtubeUrl = url;
    if (url.contains("youtube.com/shorts/")) {
      url = url.replace("youtube.com/shorts/", "youtube.com/watch?v=");
    }
    
    if (isVideoDuplicate(youtubeUrl, memberEmail)) {
      VideoEntity videoEntity = youtubeVideoRepository.findByMemberEmailAndVideoUrl(memberEmail, youtubeUrl);
      if (videoEntity != null) {
        return new YoutubeResponseDto(
                videoEntity.getVideoTitle(),
                videoEntity.getFullScript(),
                videoEntity.getVideoUrl(),
                videoEntity.getThumbnailUrl(),
                videoEntity.getSummary(),
                memberEmail,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
      }
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
      summary = generateSummaryWithRetries(fullScript, subtitleLanguage);
      
      VideoEntity videoEntity = new VideoEntity();
      videoEntity.setSummary(summary);
      videoEntity.setFullScript(fullScript);
      videoEntity.setVideoUrl(youtubeUrl);
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
    // 이 메소드는 SeleniumConfig에서 생성한 WebDriver를 사용하도록 별도 변경하지 않아도 됩니다.
    // 필요시 SeleniumConfig.getChromeDriver()를 주입받아 사용하면 됩니다.
    // 여기서는 예시로 새로 생성하는 코드로 남겨두었습니다.
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
      System.err.println("Failed to click 'More' button: " + e.getMessage());
    }
  }
  
  private void clickScriptButton(WebDriver driver) {
    try {
      WebElement scriptButton = driver.findElement(By.cssSelector("button[aria-label='스크립트 표시']"));
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", scriptButton);
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", scriptButton);
      System.out.println("Clicked '스크립트 표시' button");
    } catch (Exception e) {
      System.err.println("Failed to click '스크립트 표시' button: " + e.getMessage());
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
      System.out.println("Extracted Transcript: \n" + fullScript);
      return fullScript;
    } catch (Exception e) {
      System.err.println("Failed to extract transcript: " + e.getMessage());
    }
    return "";
  }
  
  private String extractSubtitleLanguage(WebDriver driver) {
    try {
      WebElement languageElement = driver.findElement(By.cssSelector("div#label-text.style-scope.yt-dropdown-menu"));
      String languageText = languageElement.getText().replace(" (자동 생성됨)", "");
      System.out.println("Subtitle Language: " + languageText);
      return languageText;
    } catch (Exception e) {
      System.err.println("Failed to extract subtitle language: " + e.getMessage());
      return "Unknown";
    }
  }
  
  public String generateSummaryWithRetries(String transcript, String subtitleLanguage) {
    int attempt = 0;
    while (attempt < MAX_RETRIES) {
      try {
        String summary = openAIUtils.summarize(transcript, subtitleLanguage, "youtube");
        System.out.println("Generated Summary: \n" + summary);
        return summary;
      } catch (IOException e) {
        attempt++;
        System.err.println("Summary generation failed: " + e.getMessage());
        if (attempt < MAX_RETRIES) {
          try {
            Thread.sleep(RETRY_DELAY_MS);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    return "Summary generation failed.";
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
    Request request = new Request.Builder().url(apiUrl).build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
      JSONObject jsonResponse = new JSONObject(response.body().string());
      String title = jsonResponse.getJSONArray("items")
              .getJSONObject(0)
              .getJSONObject("snippet")
              .getString("title");
      System.out.println("Video Title: " + title);
      return title;
    }
  }
  
  public String getThumbnailUrl(String videoUrl) throws IOException, JSONException {
    String videoId = extractVideoId(videoUrl);
    String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;
    Request request = new Request.Builder().url(apiUrl).build();
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
