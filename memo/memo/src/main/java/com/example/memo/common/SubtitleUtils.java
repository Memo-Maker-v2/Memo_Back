// src/main/java/com/example/memo/common/SubtitleUtils.java
package com.example.memo.common;

import java.util.ArrayList;
import java.util.List;

public class SubtitleUtils {
  
  public static List<String> splitTranscript(String transcript, int chunkSize) {
    List<String> chunks = new ArrayList<>();
    int length = transcript.length();
    for (int i = 0; i < length; i += chunkSize) {
      chunks.add(transcript.substring(i, Math.min(length, i + chunkSize)));
    }
    return chunks;
  }
}
