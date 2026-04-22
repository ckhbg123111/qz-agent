package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzSpecimenItem", description = "送检标本条目")
public class QzSpecimenItem {

    @Schema(description = "标本编码")
    private String specimenCode;

    @NotBlank(message = "标本名称不能为空")
    @Schema(description = "标本名称")
    private String specimenName;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "是否送病理")
    private Boolean sendPathologyFlag;
}
