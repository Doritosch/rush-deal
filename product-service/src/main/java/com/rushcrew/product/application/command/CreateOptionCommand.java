package com.rushcrew.product.application.command;

import com.rushcrew.product.presentation.dto.request.CreateOptionRequest;
import java.util.List;

public record CreateOptionCommand(
    String size,
    String color
) {

    public static List<CreateOptionCommand> fromList(List<CreateOptionRequest> request) {
        return request.stream().map(req ->
            new CreateOptionCommand(req.size(), req.color())).toList();
    }
}
