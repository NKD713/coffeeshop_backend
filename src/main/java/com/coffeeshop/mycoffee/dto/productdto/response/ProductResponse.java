package com.coffeeshop.mycoffee.dto.productdto.response;

import com.coffeeshop.mycoffee.dto.categorydto.response.CategoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    String id;
    String name;
    float price;
    CategoryResponse category;
    String createdAt;
    String updatedAt;
    String deletedAt;
}