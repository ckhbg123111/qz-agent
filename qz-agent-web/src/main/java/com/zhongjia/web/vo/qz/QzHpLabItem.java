package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzHpLabItem", description = "检验项目条目")
public class QzHpLabItem {

    @NotBlank(message = "检验项目编码不能为空")
    @Schema(description = "检验项目编码，优先传院内 LIS 项目编码；示例：GLU_FAST（空腹血糖）、HBA1C（糖化血红蛋白）、C13BT（13C/14C 呼气试验）")
    private String labItemCode;

    @NotBlank(message = "检验项目名称不能为空")
    @Schema(description = "检验项目名称，如空腹血糖、糖化血红蛋白、13C 呼气试验")
    private String labItemName;

    @Schema(description = "检验项目编码体系，默认院内 LIS 项目字典；可兼容区域检验互认编码")
    private String labItemCodeSystem;

    @Schema(description = "标本类型，如静脉血、末梢血、呼气")
    private String specimenType;

    @Schema(description = "项目分组，如生化、免疫、呼气试验")
    private String itemCategory;
}
