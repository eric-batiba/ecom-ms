package com.damlotec.ecommerce.product;

import com.damlotec.ecommerce.exceptions.CategoryNotFoundException;
import com.damlotec.ecommerce.exceptions.InsufficientQuanityException;
import com.damlotec.ecommerce.exceptions.ProductPurchaseException;
import com.damlotec.ecommerce.exceptions.ProuctNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService underTest;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Test
    void testConnexionDb() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    Category category;
    ProductRequest productRequest;
    Product product;
    List<ProductPurchaseRequest> productsRequestIds;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1).name("cat").description("cat").build();
        productRequest = new ProductRequest("test", "test", new BigDecimal(100), 100, 1);
        product = Product.builder().name("test").description("test").price(new BigDecimal(100)).availableQuantity(100).category(category).build();
    }

    @Test
    void shouldCreateProduct() {
        //given
        Product savedProduct = product;
        savedProduct.setId(1);
        when(mapper.toEntity(productRequest)).thenReturn(product);
        when(categoryRepository.findById(productRequest.categoryId())).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(savedProduct);
        //when
        Integer result = underTest.createProduct(productRequest);
        //then
        verify(mapper, times(1)).toEntity(productRequest);
        verify(categoryRepository, times(1)).findById(anyInt());
        verify(productRepository, times(1)).save(product);
        assertThat(result).isNotNull().isEqualTo(1);
    }

    @Test
    void shouldNotCreateProductWhenCategoryNotFound() {
        when(mapper.toEntity(productRequest)).thenReturn(product);
        when(categoryRepository.findById(productRequest.categoryId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.createProduct(productRequest))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage(format("Category with id %d not found", productRequest.categoryId()));
    }

    @Test
    void shouldProductsPurchase() {
        //given
        ProductPurchaseRequest productPurchaseRequest = new ProductPurchaseRequest(1, 2);
        ProductPurchaseRequest productPurchaseRequest1 = new ProductPurchaseRequest(2, 4);
        ProductPurchaseRequest productPurchaseRequest2 = new ProductPurchaseRequest(3, 6);

        productsRequestIds = List.of(productPurchaseRequest, productPurchaseRequest1, productPurchaseRequest2);

        Product product0 = Product.builder().id(1).name("test").description("test").price(new BigDecimal(100)).availableQuantity(10).category(category).build();
        Product product1 = Product.builder().id(2).name("test2").description("test2").price(new BigDecimal(200)).availableQuantity(20).category(category).build();
        Product product2 = Product.builder().id(3).name("test3").description("test3").price(new BigDecimal(300)).availableQuantity(30).category(category).build();

        when(productRepository.findAllByIdInOrderById(anyList())).thenReturn(List.of(product0, product1, product2));
        when(mapper.toProductPurchaseResponse(product0, 2)).thenReturn(new ProductPurchaseResponse(1, "test", "test", new BigDecimal(100), 2));
        when(mapper.toProductPurchaseResponse(product1, 4)).thenReturn(new ProductPurchaseResponse(2, "test2", "test2", new BigDecimal(200), 4));
        when(mapper.toProductPurchaseResponse(product2, 6)).thenReturn(new ProductPurchaseResponse(3, "test3", "test3", new BigDecimal(300), 6));

        //when
        List<ProductPurchaseResponse> result = underTest.ProductsPurchase(productsRequestIds);
        //then
        assertThat(result).isNotNull().isNotEmpty().hasSize(3);
        verify(productRepository, times(1)).findAllByIdInOrderById(anyList());
        verify(productRepository, times(3)).save(any(Product.class));
        verify(mapper, times(3)).toProductPurchaseResponse(any(Product.class), anyInt());
    }

    @Test
    void shouldProductsPurchaseNotFound() {
        //given
        ProductPurchaseRequest productPurchaseRequest = new ProductPurchaseRequest(1, 2);
        ProductPurchaseRequest productPurchaseRequest1 = new ProductPurchaseRequest(2, 4);
        ProductPurchaseRequest productPurchaseRequest2 = new ProductPurchaseRequest(3, 6);

        productsRequestIds = List.of(productPurchaseRequest, productPurchaseRequest1, productPurchaseRequest2);

        Product product0 = Product.builder().id(1).name("test").description("test").price(new BigDecimal(100)).availableQuantity(10).category(category).build();
        Product product1 = Product.builder().id(2).name("test2").description("test2").price(new BigDecimal(200)).availableQuantity(20).category(category).build();
        when(productRepository.findAllByIdInOrderById(anyList())).thenReturn(List.of(product0, product1));

        //when
        //then
        assertThatThrownBy(() -> underTest.ProductsPurchase(productsRequestIds))
                .isInstanceOf(ProductPurchaseException.class)
                .hasMessage("One or more product not exist");

    }

    @Test
    void shouldProductsPurchaseNotEnoughQuantity() {
        //given
        ProductPurchaseRequest productPurchaseRequest = new ProductPurchaseRequest(1, 2);
        ProductPurchaseRequest productPurchaseRequest1 = new ProductPurchaseRequest(2, 10);

        productsRequestIds = List.of(productPurchaseRequest, productPurchaseRequest1);

        Product product0 = Product.builder().id(1).name("test").description("test").price(new BigDecimal(100)).availableQuantity(5).category(category).build();
        Product product1 = Product.builder().id(2).name("test2").description("test2").price(new BigDecimal(200)).availableQuantity(5).category(category).build();
        when(productRepository.findAllByIdInOrderById(anyList())).thenReturn(List.of(product0, product1));
        //when
        //then
        assertThatThrownBy(() -> underTest.ProductsPurchase(productsRequestIds))
                .isInstanceOf(InsufficientQuanityException.class)
                .hasMessage(format("Insufficient quantity for product %d", product1.getId()));
    }

    @Test
    void shouldGetProduct() {
        //given
        Integer id = 1;
        Product product1 = product;
        product1.setId(id);
        ProductResponse productResponse = new ProductResponse(id, "test", "test", new BigDecimal(100), 100, 1, "cat", "cat");
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(mapper.toResponse(any(Product.class))).thenReturn(productResponse);
        //when
        ProductResponse result = underTest.getProduct(id);
        //then
        verify(productRepository, times(1)).findById(id);
        verify(mapper, times(1)).toResponse(product1);
        assertThat(result.id()).isNotNull().isEqualTo(id);
    }

    @Test
    void shouldGetProductNotFound() {
        //given
        Integer id = 1;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getProduct(id))
                .isInstanceOf(ProuctNotFoundException.class)
                .hasMessage(format("Product with id %d not found", id));
    }


    @Test
    void shouldGetAllProducts() {
        Product product0 = Product.builder().id(1).name("test").description("test").price(new BigDecimal(100)).availableQuantity(10).category(category).build();
        Product product1 = Product.builder().id(2).name("test2").description("test2").price(new BigDecimal(200)).availableQuantity(20).category(category).build();
        ProductResponse productResponse0 = new ProductResponse(1, "test", "test", new BigDecimal(100), 10, 1, "cat", "cat");
        ProductResponse productResponse1 = new ProductResponse(2, "test2", "test2", new BigDecimal(200), 20, 1, "cat", "cat");

        List<Product> productList = List.of(product0, product1);
        when(productRepository.findAll()).thenReturn(productList);
        when(mapper.toResponse(any(Product.class))).thenReturn(productResponse0, productResponse1);
        //when
        List<ProductResponse> result = underTest.getAllProducts();
        //then
        verify(productRepository, times(1)).findAll();
        verify(mapper, times(2)).toResponse(any(Product.class));
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    void shouldUpdateProduct() {
        int id = 1;
        Product product0 = Product.builder().name("test").description("test").price(new BigDecimal(100)).availableQuantity(10).category(category).build();
        Product savedProduct = Product.builder().id(1).name("test").description("test").price(new BigDecimal(100)).availableQuantity(10).category(category).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product0));
        when(mapper.toResponse(product0)).thenReturn(new ProductResponse(1, "testUpdate", "test", new BigDecimal(100), 10, 1, "cat", "cat"));
        when(mapper.toEntity(productRequest)).thenReturn(product0);
        when(productRepository.save(product0)).thenReturn(savedProduct);

        Integer result = underTest.updateProduct(id, productRequest);
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(product0);
        verify(mapper, times(1)).toEntity(productRequest);
        verify(mapper, times(1)).toResponse(product0);
        assertThat(result).isNotNull().isEqualTo(1);

    }

    @Test
    void shouldDeleteProduct() {
        int id = 1;
        Product product0 = Product.builder().id(1).name("test").description("test").price(new BigDecimal(100)).availableQuantity(10).category(category).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product0));
        when(mapper.toResponse(product0)).thenReturn(new ProductResponse(1, "test", "test", new BigDecimal(100), 10, 1, "cat", "cat"));
        underTest.deleteProduct(id);
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).deleteById(id);

    }
}