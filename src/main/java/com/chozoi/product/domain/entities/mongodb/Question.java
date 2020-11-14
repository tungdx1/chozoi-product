package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "comments.question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    private Long id;

    private Object user;

    private Object product;

    private Object shop;

    private String state;

    private String text;

    //  private Integer countAnswers;

    private List<Answer> answers;

    private Long createdAt;

    private Long updatedAt;
}
