package com.damlotec.ecommerce.product;

import java.util.List;

public interface IproductService {
    Integer createProduct(ProductRequest request);

    List<ProductPurchaseResponse> ProductsPurchase(List<ProductPurchaseRequest> request);

    ProductResponse getProduct(Integer id);

    List<ProductResponse> getAllProducts();

    Integer updateProduct(int id, ProductRequest request);

    void deleteProduct(int id);
}
