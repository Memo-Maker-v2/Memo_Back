package com.example.memo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pdf_question_table")
public class PDFQuestionEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pdf_question_id")
  private long pdfQuestionId;
  
  @Column(name = "question")
  private String question;
  
  @Column(name = "answer")
  private String answer;
  
  @Column(name = "member_email")
  private String memberEmail;
  
  @Column(name = "pdf_title")
  private String pdfTitle;
}
