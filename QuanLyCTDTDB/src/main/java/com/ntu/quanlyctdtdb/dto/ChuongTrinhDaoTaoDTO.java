package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChuongTrinhDaoTaoDTO {

    @NotBlank(message = "Ma CTDT khong duoc de trong")
    @Size(max = 20)
    private String maCTDT;

    @NotBlank(message = "Ten CTDT khong duoc de trong")
    @Size(max = 200)
    private String tenCTDT;

    @Size(max = 20)
    private String khoa;

    // FileWord xu ly rieng trong controller
    private String fileWord;
}
