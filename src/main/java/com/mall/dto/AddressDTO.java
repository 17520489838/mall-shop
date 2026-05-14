package com.mall.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "地址参数")
public class AddressDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", required = true)
    private String name;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收货人电话", required = true)
    private String phone;

    @NotBlank(message = "省不能为空")
    @Schema(description = "省", required = true)
    private String province;

    @NotBlank(message = "市不能为空")
    @Schema(description = "市", required = true)
    private String city;

    @NotBlank(message = "区/县不能为空")
    @Schema(description = "区/县", required = true)
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址", required = true)
    private String detail;

    @Schema(description = "邮编")
    private String zipCode;

    @Schema(description = "是否默认")
    private Integer isDefault;
}
