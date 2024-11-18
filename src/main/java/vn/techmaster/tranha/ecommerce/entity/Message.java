package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.MessageStatus;

import java.time.LocalDateTime;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    Conversation conversation;
    
    String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    User sender;

    LocalDateTime sentAt;
}
