package org.fightjc.xybot.model.dto.common;

import lombok.Getter;

@Getter
public class PageResultDto<T> {

    private int totalCount;

    private T items;

    public PageResultDto(int totalCount, T items) {
        this.totalCount = totalCount;
        this.items = items;
    }
}
