package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzHpMedicineItem", description = "处方药品条目")
public class QzHpMedicineItem {

    @NotBlank(message = "药品编码不能为空")
    @Schema(description = "药品编码，优先传院内药品字典编码；示例：DRUG_INSULIN_001（胰岛素）、DRUG_LIRAGLUTIDE_001（利拉鲁肽）、DRUG_SEMAGLUTIDE_001（司美格鲁肽）、DRUG_TIRZEPATIDE_001（替尔泊肽）")
    private String medicineCode;

    @NotBlank(message = "药品名称不能为空")
    @Schema(description = "药品名称，如门冬胰岛素注射液、利拉鲁肽注射液、司美格鲁肽注射液、替尔泊肽注射液")
    private String medicineName;

    @Schema(description = "药品编码体系，默认院内药品字典；如已维护医保编码可在此注明")
    private String medicineCodeSystem;

    @Schema(description = "规格，如 3ml:300IU、3ml:18mg、1.5ml:4mg")
    private String specification;

    @Schema(description = "单次剂量")
    private String dosage;

    @Schema(description = "剂量单位，如 IU、mg、ml")
    private String dosageUnit;

    @Schema(description = "给药频次，如 QD、BID、QW")
    private String frequency;

    @Schema(description = "给药途径，如 SC、IH、PO")
    private String route;
}
