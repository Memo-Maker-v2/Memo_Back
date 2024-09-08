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
  
  // 최대 재시도 횟수와 재시도 간 대기 시간을 설정
  private static final int MAX_RETRIES = 3;
  private static final long RETRY_DELAY_MS =3000;
  
  private static final String CHECK_DUPLICATE_URL = "http://localhost:8080/api/v1/video/check-duplicate"; // CHECK_DUPLICATE API 주소
  
  /**
   * 비디오 URL과 이메일을 기반으로 중복 여부를 확인하는 메소드.
   *
   * @param url 유튜브 비디오 URL
   * @param memberEmail 회원 이메일
   * @return 비디오 중복 여부 (true: 중복, false: 비중복)
   * @throws IOException
   */
  private boolean isVideoDuplicate(String url, String memberEmail) throws IOException {
    String requestBody = String.format("{\"videoUrl\": \"%s\", \"memberEmail\": \"%s\"}", url, memberEmail);
    RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));
    
    Request request = new Request.Builder()
            .url(CHECK_DUPLICATE_URL)
            .post(body)
            .build();
    
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      
      // 서버 응답을 문자열로 가져오기
      String responseBody = response.body().string();
      System.out.println("Server response: " + responseBody); // 서버 응답 내용 확인
      
      // 응답이 "true" 또는 "false" 문자열인지 확인
      if ("true".equalsIgnoreCase(responseBody.trim())) {
        return true;
      } else if ("false".equalsIgnoreCase(responseBody.trim())) {
        return false;
      } else {
        throw new IOException("Unexpected response body: " + responseBody);
      }
    }
  }
  
  /**
   * 주어진 YouTube URL과 회원 이메일을 기반으로 유튜브 비디오 정보를 처리하는 메소드.
   * 비디오 제목, 썸네일 URL, 전체 스크립트, 요약 등을 추출하고, 이를 데이터베이스에 저장합니다.
   *
   * @param url        유튜브 비디오 URL
   * @param memberEmail 회원 이메일
   * @return 유튜브 비디오 정보를 담은 DTO 객체
   * @throws IOException, JSONException
   */
  public YoutubeResponseDto processYoutubeUrl(String url, String memberEmail) throws IOException, JSONException {
    // URL이 'shorts' 형식이면 'watch?v=' 형식으로 변환
    if (url.contains("youtube.com/shorts/")) {
      url = url.replace("youtube.com/shorts/", "youtube.com/watch?v=");
    }
    
//    // 비디오 중복 여부 확인
//    if (isVideoDuplicate(url, memberEmail)) {
//      System.out.println("Video is already in the database.");
//      return new YoutubeResponseDto("Video already exists", "", url, "", "", memberEmail, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//    }
    
    WebDriver driver = initializeWebDriver(); // 웹 드라이버 초기화
    String videoTitle = "";
    String thumbnailUrl = "";
    String fullScript = "";
    String summary = "";
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    
    try {
      // 중복 비디오 확인
      if (isVideoDuplicate(url, memberEmail)) {
        // 중복된 비디오 정보 가져오기
        VideoEntity videoEntity = youtubeVideoRepository.findByMemberEmailAndVideoUrl(memberEmail, url);
        if (videoEntity != null) {
          return new YoutubeResponseDto(
                  videoEntity.getVideoTitle(),
                  videoEntity.getFullScript(),
                  videoEntity.getVideoUrl(),
                  videoEntity.getThumbnailUrl(),
                  videoEntity.getSummary(),
                  memberEmail,
                  date
          );
        }
      }
      
      videoTitle = getVideoTitle(url); // 비디오 제목 가져오기
      thumbnailUrl = getThumbnailUrl(url); // 썸네일 URL 가져오기
      
      driver.get(url); // 비디오 페이지 로드
      driver.manage().window().setSize(new Dimension(1920, 1080)); // 브라우저 창 크기 설정
      waitForPageLoad(driver); // 페이지 로드 대기
      
      clickMoreButton(driver); // '더보기' 버튼 클릭
      sleepWithExceptionHandling(2000); // 2초 대기
      
      clickScriptButton(driver); // '스크립트 표시' 버튼 클릭
      
      fullScript = extractTranscript(driver); // 스크립트 추출
      String subtitleLanguage = extractSubtitleLanguage(driver); // 자막 언어 추출
      
      summary = generateSummaryWithRetries(fullScript, subtitleLanguage); // 요약 생성
      
      // 비디오 정보를 데이터베이스에 저장
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
      closeBrowser(driver); // 브라우저 닫기
    }
    
    // 유튜브 비디오 정보가 담긴 DTO 객체 반환
    return new YoutubeResponseDto(videoTitle, fullScript, url, thumbnailUrl, summary, memberEmail, date);
  }
  
  /**
   * Chrome 웹 드라이버를 초기화하는 메소드.
   * 드라이버를 임시 디렉토리에 저장하고, ChromeDriver를 설정하여 반환합니다.
   *
   * @return 초기화된 WebDriver 객체
   * @throws IOException
   */
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
  
  /**
   * 페이지 로드가 완료될 때까지 대기하는 메소드.
   *
   * @param driver 웹 드라이버 객체
   */
  private void waitForPageLoad(WebDriver driver) {
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }
  
  /**
   * '더보기' 버튼을 클릭하는 메소드.
   *
   * @param driver 웹 드라이버 객체
   */
  private void clickMoreButton(WebDriver driver) {
    try {
      WebElement moreButton = driver.findElement(By.cssSelector("tp-yt-paper-button#expand"));
      moreButton.click();
      System.out.println("Clicked 'More' button");
    } catch (Exception e) {
      System.err.println("Failed to find or click 'More' button: " + e.getMessage());
    }
  }
  
  /**
   * '스크립트 표시' 버튼을 클릭하는 메소드.
   *
   * @param driver 웹 드라이버 객체
   */
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
  
  /**
   * 웹 페이지에서 스크립트를 추출하는 메소드.
   *
   * @param driver 웹 드라이버 객체
   * @return 추출한 스크립트 문자열
   */
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
  
  /**
   * 웹 페이지에서 자막 언어를 추출하는 메소드.
   *
   * @param driver 웹 드라이버 객체
   * @return 자막 언어 문자열
   */
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
  
  /**
   * 스크립트와 자막 언어를 기반으로 요약을 생성하는 메소드.
   * 요약 생성 요청이 타임아웃되거나 실패하면 최대 재시도 횟수만큼 다시 시도합니다.
   *
   * @param transcript      비디오 스크립트
   * @param subtitleLanguage 자막 언어
   * @return 생성된 요약 문자열
   */
  public String generateSummaryWithRetries(String transcript, String subtitleLanguage) {
    int attempt = 0;
    while (attempt < MAX_RETRIES) {
      try {
        String summary = openAIUtils.youtubeSummarize(transcript, subtitleLanguage);
        System.out.println("Generated Summary in " + subtitleLanguage + ": \n" + summary);
        return summary;
      } catch (IOException e) {
        attempt++;
        System.err.println("Failed to generate summary: " + e.getMessage());
        if (attempt < MAX_RETRIES) {
          try {
            Thread.sleep(RETRY_DELAY_MS); // 재시도 전 대기
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    return "Summary generation failed.";
  }
  
  /**
   * 브라우저를 닫는 메소드.
   *
   * @param driver 웹 드라이버 객체
   */
  private void closeBrowser(WebDriver driver) {
    if (driver != null) {
      driver.quit();
    }
  }
  
  /**
   * 주어진 밀리초만큼 대기하는 메소드.
   *
   * @param millis 대기 시간 (밀리초)
   */
  private void sleepWithExceptionHandling(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  /**
   * 비디오 URL에서 비디오 ID를 추출하는 메소드.
   *
   * @param videoUrl 유튜브 비디오 URL
   * @return 추출한 비디오 ID
   * @throws IllegalArgumentException 비디오 URL이 유효하지 않은 경우
   */
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
  
  /**
   * 비디오 URL에서 썸네일 URL을 추출하는 메소드.
   *
   * @param videoUrl 유튜브 비디오 URL
   * @return 추출한 썸네일 URL
   * @throws IOException, JSONException
   */
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
  
  /**
   * 유튜브 비디오 URL에서 비디오 ID를 추출하는 메소드.
   *
   * @param videoUrl 유튜브 비디오 URL
   * @return 비디오 ID
   * @throws IllegalArgumentException 비디오 URL이 유효하지 않은 경우
   */
  private String extractVideoId(String videoUrl) {
    String[] parts = videoUrl.split("v=");
    if (parts.length > 1) {
      return parts[1];
    } else {
      throw new IllegalArgumentException("Invalid YouTube URL");
    }
  }
}
