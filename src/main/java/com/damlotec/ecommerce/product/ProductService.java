package com.damlotec.ecommerce.product;

import com.damlotec.ecommerce.exceptions.CategoryNotFoundException;
import com.damlotec.ecommerce.exceptions.InsufficientQuanityException;
import com.damlotec.ecommerce.exceptions.ProductPurchaseException;
import com.damlotec.ecommerce.exceptions.ProuctNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IproductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    @Override
    public Integer createProduct(ProductRequest request) {
        log.info("Creating product {}", request);
        Product product = mapper.toEntity(request);
        Optional<Category> category = categoryRepository.findById(product.getCategory().getId());
        if (category.isEmpty()) throw new CategoryNotFoundException(format("Category with id %d not found", request.categoryId()));
        product.setCategory(category.get());
        return productRepository.save(product).getId();
    }
    @Override
    public List<ProductPurchaseResponse> ProductsPurchase(List<ProductPurchaseRequest> request) {
        log.info("Getting products for purchase {}", request);
            List<Integer> productsRequestIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        List<Product> productsInDb = productRepository.findAllByIdInOrderById(productsRequestIds);
        if (productsRequestIds.size() != productsInDb.size())
            throw new ProductPurchaseException("One or more product not exist");

        List<ProductPurchaseRequest> purchaseRequests = request.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        ArrayList<ProductPurchaseResponse> productPurchaseResponses = new ArrayList<>();
        for (int i = 0; i < productsInDb.size(); i++) {
            var product = productsInDb.get(i);
            var purchaseRequest = purchaseRequests.get(i);
            if (product.getAvailableQuantity() < purchaseRequest.quantity())
                throw new InsufficientQuanityException(format("Insufficient quantity for product %d", product.getId()));

            double newAvailableQuantity = product.getAvailableQuantity() - purchaseRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            productRepository.save(product);
            productPurchaseResponses.add(mapper.toProductPurchaseResponse(product, purchaseRequest.quantity()));
        }

        return productPurchaseResponses;

    }

    @Override
    public ProductResponse getProduct(Integer id) {
        log.info("Getting product with id {}", id);
        return findProductById(id);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Getting all products");
        return productRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public Integer updateProduct(int id, ProductRequest request) {
        log.info("Updating product with id {}", id);
        findProductById(id);
        Product product = mapper.toEntity(request);
        product.setId(id);
        return productRepository.save(product).getId();
    }

    @Override
    public void deleteProduct(int id) {
        log.info("Deleting product with id {}", id);
        findProductById(id);
        productRepository.deleteById(id);
    }

    private ProductResponse findProductById(int id) {
        return productRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ProuctNotFoundException(format("Product with id %d not found", id)));
    }

}
