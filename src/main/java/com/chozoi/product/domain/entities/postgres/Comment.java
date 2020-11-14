package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.CommentState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(schema = "products", name = "review")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@Where(clause = "state = 'PUBLIC'")
public class Comment {
    @Id
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "text")
    private String text;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, columnDefinition = "comment_state", name = "state")
    @Type(type = "pg-enum")
    private CommentState state;
}
