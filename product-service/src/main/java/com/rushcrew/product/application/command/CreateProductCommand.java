package com.rushcrew.product.application.command;

import com.rushcrew.product.domain.vo.Category;
import com.rushcrew.product.domain.vo.Price;
import com.rushcrew.product.domain.vo.ProductInfo;
import com.rushcrew.product.domain.vo.SellerId;
import com.rushcrew.product.presentation.dto.request.CreateProductRequest;
import java.util.List;

public record CreateProductCommand(
    SellerId sellerId,
    String companyName,
    ProductInfo productInfo,
    Price price,
    Category category,
    List<CreateOptionCommand> optionCommands
) {

    public static CreateProductCommand from(CreateProductRequest request) {
        return new CreateProductCommand(
            SellerId.of(request.userId()),
            request.companyName(),
            ProductInfo.of(request.productName(), request.description()),
            Price.of(request.price()),
            request.category(),
            CreateOptionCommand.fromList(request.optionRequests())
        );
    }
}
