package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "QzOperationItem", description = "手术条目")
public class QzOperationItem {

    @NotNull(message = "手术序号不能为空")
    @Schema(description = "手术序号")
    private Integer operationSeq;

    @Schema(description = "ICD-9-CM3 或院内手术编码")
    private String operationCode;

    @NotBlank(message = "手术名称不能为空")
    @Schema(description = "手术名称")
    private String operationName;

    @Schema(description = "手术部位")
    private String operationSite;

    @Schema(description = "手术方式或入路")
    private String operationMethod;

    @Schema(description = "是否主手术")
    private Boolean mainOperationFlag;

    @Schema(description = "是否完成")
    private Boolean completedFlag;

    @Schema(description = "未完成或中止原因")
    private String cancelReason;
}
