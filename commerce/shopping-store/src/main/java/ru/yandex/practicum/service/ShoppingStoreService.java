package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.shoppingStore.dto.PageableDto;
import ru.yandex.practicum.shoppingStore.dto.ProductDto;
import ru.yandex.practicum.shoppingStore.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    @Qualifier("mvcConversionService")
    private final ConversionService converter;


    public List<ProductDto> getProductsByCategory(ProductDto.ProductCategory category, PageableDto pageableDto) {
        Pageable pageable = convertToPageable(pageableDto);
        List<Product> products = productRepository.findAllByProductCategory(category, pageable).getContent();

        return products.stream()
                .map(product -> converter.convert(product, ProductDto.class))
                .collect(Collectors.toList());
    }


    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = converter.convert(productDto, Product.class);
        Product savedProduct = productRepository.save(Objects.requireNonNull(product));
        return converter.convert(savedProduct, ProductDto.class);
    }


    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Продукт не найден"));
        updateProductFields(product, productDto);
        Product updatedProduct = productRepository.save(product);
        return converter.convert(updatedProduct, ProductDto.class);
    }


    public boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Продукт не найден"));
        productRepository.delete(product);
        return true;
    }


    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        UUID productId = request.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Продукт не найден"));

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        return true;
    }


    public ProductDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->  new NoSuchElementException("Продукт не найден"));
        return converter.convert(product, ProductDto.class);
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
        if (productDto.getPrice() != null && productDto.getPrice() >= 1) {
            product.setPrice(productDto.getPrice());
        }
    }

    private Pageable convertToPageable(PageableDto pageableDto) {
        if (pageableDto.getSort() == null || pageableDto.getSort().isEmpty()) {
            return PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
        }

        Sort sort = Sort.by(
                pageableDto.getSort().stream()
                        .map(sortStr -> {
                            String[] sortParams = sortStr.split(",");
                            if (sortParams.length == 2 && sortParams[1].equalsIgnoreCase("desc")) {
                                return Sort.Order.desc(sortParams[0]);
                            }
                            return Sort.Order.asc(sortParams[0]);
                        })
                        .collect(Collectors.toList()));
        return PageRequest.of(pageableDto.getPage(), pageableDto.getSize(), sort);
    }

}
