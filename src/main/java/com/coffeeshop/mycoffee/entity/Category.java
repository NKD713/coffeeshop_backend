package com.coffeeshop.mycoffee.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class) // Lắng nghe sự kiện tạo và cập nhật
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    List<Product> products;

    // Tự động cập nhật khi tạo bản ghi
    @CreatedDate
    @Column(updatable = false) // Không cho phép cập nhật sau khi tạo
            LocalDateTime createdAt;

    // Tự động cập nhật khi bản ghi thay đổi
    @LastModifiedDate
    LocalDateTime updatedAt;

    // Cột để lưu trữ thời gian khi thực hiện xóa mềm
    LocalDateTime deletedAt;

    // Hàm để xóa mềm (chỉ cập nhật deletedAt thay vì xóa khỏi DB)
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}