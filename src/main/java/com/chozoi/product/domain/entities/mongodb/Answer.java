package com.chozoi.product.domain.entities.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments.answer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id
    private Long id;

    private Long questionId;

    private Object user;

    private String text;

    private Long createdAt;

    private Long updatedAt;
}
