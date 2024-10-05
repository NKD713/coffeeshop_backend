package com.coffeeshop.mycoffee.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    String password;
    String firstname;
    String lastname;
    LocalDate dob;
    String phone;
    int point;

    @ManyToMany
    Set<Role> roles;

    @OneToOne(
            // indicates that this is the child side of a
            // relationship and refers to the field in the Driver
            // class that defines the relationship there
            mappedBy = "user"
    )
    Order order;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Order> orders;

    // Tự động cập nhật khi tạo bản ghi
    @CreatedDate
    @Column(updatable = false) // Không cho phép cập nhật sau khi tạo
            LocalDateTime createdAt;

    // Tự động cập nhật khi bản ghi thay đổi
    @LastModifiedDate
    LocalDateTime updatedAt;
}
