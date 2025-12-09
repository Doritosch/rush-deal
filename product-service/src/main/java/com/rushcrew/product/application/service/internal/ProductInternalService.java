package com.rushcrew.product.application.service.internal;

import com.rushcrew.product.presentation.internal.dto.ProductInfoResponse;
import java.util.UUID;

public interface ProductInternalService {

    ProductInfoResponse getProductItemIds(UUID uuid);
}
