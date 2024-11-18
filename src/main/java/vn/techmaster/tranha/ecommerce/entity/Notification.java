package vn.techmaster.tranha.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.techmaster.tranha.ecommerce.statics.NotificationStatus;
import vn.techmaster.tranha.ecommerce.statics.NotificationType;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {

    String title;

    String content;

    @Enumerated(EnumType.STRING)
    NotificationStatus status;

    @Enumerated(EnumType.STRING)
    NotificationType type;
}
