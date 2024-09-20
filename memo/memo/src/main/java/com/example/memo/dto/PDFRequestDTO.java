package com.example.memo.dto;

public class PDFRequestDTO {
  private String memberEmail;
  private String pdfTitle;
  
  // Getters and Setters
  public String getMemberEmail() {
    return memberEmail;
  }
  
  public void setMemberEmail(String memberEmail) {
    this.memberEmail = memberEmail;
  }
  
  public String getPdfTitle() {
    return pdfTitle;
  }
  
  public void setPdfTitle(String pdfTitle) {
    this.pdfTitle = pdfTitle;
  }
}
