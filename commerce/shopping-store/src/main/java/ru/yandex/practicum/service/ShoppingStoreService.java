package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shoppingStore.dto.Pageable;
import shoppingStore.dto.ProductDto;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        List<Product> products = productRepository.findAllByProductCategory(category, pageable).getContent();

        return products.stream()
                .map(productMapper::toProductDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                    return new ProductNotFoundException("Продукт не найден")
                );
        updateProductFields(product, productDto);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDto(updatedProduct);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                    return new ProductNotFoundException("Продукт не найден");
                );
        productRepository.delete(product);
        return true;
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        UUID productId = request.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                    return new ProductNotFoundException("Продукт не найден")
                );

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                    return new ProductNotFoundException("Продукт не найден")
                );
        return productMapper.toProductDto(product);
    }

    private void updateProductFields(Product product, ProductDto productDto) {
        if (productDto.getProductName() != null) {
            product.setProductName(productDto.getProductName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getImageSrc() != null) {
            product.setImageSrc(productDto.getImageSrc());
        }
        if (productDto.getQuantityState() != null) {
            product.setQuantityState(productDto.getQuantityState());
        }
        if (productDto.getProductState() != null) {
            product.setProductState(productDto.getProductState());
        }
        if (productDto.getRating() > 0) {
            product.setRating(productDto.getRating());
        }
        if (productDto.getProductCategory() != null) {
            product.setProductCategory(productDto.getProductCategory());
        }
        if (productDto.getPrice() != null && productDto.getPrice().compareTo(BigDecimal.ONE) >= 0) {
            product.setPrice(productDto.getPrice());
        }
    }

}
