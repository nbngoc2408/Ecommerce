package com.example.demo.mapper;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(target = "category", expression = "java(findCategory(productDto.getCategoryId()))")
    public abstract Product mapProductDtoToProduct(ProductDto productDto);

    @Mapping(source = "category.id", target = "categoryId")
    public abstract ProductDto mapProductToProductDto(Product product);

    public Category findCategory(int categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.orElseGet(Category::new);
    }
}
