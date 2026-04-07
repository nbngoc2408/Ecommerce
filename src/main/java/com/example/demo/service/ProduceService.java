package com.example.demo.service;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProduceService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;


    public ProduceService(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public ProductDto save(ProductDto product) {
        Optional<Category> category = categoryRepository.findById(product.getCategoryId());
        if (category.isPresent()) {
            Product entityProduct = productRepository.save(productMapper.mapProductDtoToProduct(product));
            return productMapper.mapProductToProductDto(entityProduct);
        }

        return null;
    }

    @Transactional
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }
}
