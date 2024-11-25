package vn.techmaster.tranha.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import vn.techmaster.tranha.ecommerce.entity.Role;
import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.repository.RoleRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;
import vn.techmaster.tranha.ecommerce.statics.Constant;
import vn.techmaster.tranha.ecommerce.statics.Roles;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseInitializer implements CommandLineRunner {

    final UserRepository userRepository;

    final RoleRepository roleRepository;

    final PasswordEncoder passwordEncoder;

    @Value("${application.account.admin.email}")
    String adminEmail;

    @Value("${application.account.admin.password}")
    String adminPassword;

    @Override
    public void run(String... args) {

        Optional<Role> roleUserOptional = roleRepository.findByName(Roles.USER);
        if (roleUserOptional.isEmpty()) {
            Role userRole = Role.builder().name(Roles.USER).build();
            roleRepository.save(userRole);
        }

        Optional<Role> userUserOptional = roleRepository.findByName(Roles.ADMIN);
        if (userUserOptional.isEmpty()) {
            Role adminRole = Role.builder().name(Roles.ADMIN).build();
            roleRepository.save(adminRole);

            Optional<User> admin = userRepository.findByEmail(adminEmail);
            if (admin.isEmpty()) {
                User user = new User();
                user.setName("admin");
                user.setEmail(adminEmail);
                user.setPassword(passwordEncoder.encode(adminPassword)); // Encrypt the password
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                user.setRoles(roles);
                user.setStatus(UserStatus.ACTIVATED);
                user.setCreatedBy(Constant.DEFAULT_CREATOR);
                user.setLastModifiedBy(Constant.DEFAULT_CREATOR);
                userRepository.save(user);
            }
        }
    }

}
