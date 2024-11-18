package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.MessageStatus;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message extends BaseEntity {

    String content;

    @Enumerated(EnumType.STRING)
    MessageStatus status;
}
