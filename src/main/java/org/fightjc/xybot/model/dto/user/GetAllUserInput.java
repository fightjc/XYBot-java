package org.fightjc.xybot.model.dto.user;

import lombok.Data;
import org.fightjc.xybot.model.dto.common.PagedResultRequestDto;

@Data
public class GetAllUserInput extends PagedResultRequestDto {

    private String filter;
}
