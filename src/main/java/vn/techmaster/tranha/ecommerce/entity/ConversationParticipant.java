package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversation_participants")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationParticipant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    Conversation conversation;


    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

}
