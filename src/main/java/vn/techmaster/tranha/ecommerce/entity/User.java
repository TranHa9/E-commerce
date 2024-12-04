package vn.techmaster.tranha.ecommerce.entity;

import vn.techmaster.tranha.ecommerce.statics.Gender;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    String name;

    String email;

    String password;

    String phone;

    @Enumerated(EnumType.STRING)
    Gender gender;

    LocalDate dob;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    String avatar;

    //LocalDateTime deletedDateTime;

    LocalDateTime activationMailSentAt;

    Integer activationMailSentCount;

    LocalDateTime forgotPasswordMailSentAt;

}
