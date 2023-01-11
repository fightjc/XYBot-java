package org.fightjc.xybot.model.dto.common;

import lombok.Data;

@Data
public abstract class PagedResultRequestDto {

    private int skipCount;

    private int maxResultCount;
}
