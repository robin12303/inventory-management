package com.portfolio.wms.warehouse.dto;

import jakarta.validation.constraints.NotBlank;

public record WarehouseCreateRequest(

        @NotBlank(message = "창고명은 필수입니다.")
        String name,

        @NotBlank(message = "창고 코드는 필수입니다.")
        String code,

        @NotBlank(message = "창고 주소는 필수입니다.")
        String address
) {
}