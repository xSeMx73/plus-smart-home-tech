package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.shoppingStore.dto.ProductDto;

import java.util.UUID;


public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE p.productCategory = :category")
    Page<Product> findAllByProductCategory(@Param("category") ProductDto.ProductCategory category, Pageable pageable);
}