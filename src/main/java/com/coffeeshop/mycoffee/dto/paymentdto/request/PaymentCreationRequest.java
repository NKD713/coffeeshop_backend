package com.coffeeshop.mycoffee.dto.paymentdto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCreationRequest {

    @NotBlank(message = "Type is required")
    String type;
}
