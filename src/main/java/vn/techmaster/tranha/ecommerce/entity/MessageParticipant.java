package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.MessageRole;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_participants")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageParticipant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "message_id")
    Message message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Enumerated(EnumType.STRING)
    MessageRole role;
}
