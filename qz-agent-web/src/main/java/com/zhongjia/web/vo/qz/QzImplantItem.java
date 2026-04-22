package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzImplantItem", description = "植入物或耗材条目")
public class QzImplantItem {

    @Schema(description = "耗材或植入物编码")
    private String itemCode;

    @NotBlank(message = "耗材或植入物名称不能为空")
    @Schema(description = "耗材或植入物名称")
    private String itemName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "生产厂家")
    private String manufacturer;
}
