package com.coffeeshop.mycoffee.service;

import com.coffeeshop.mycoffee.dto.productdto.request.ProductCreationRequest;
import com.coffeeshop.mycoffee.dto.productdto.request.ProductUpdateRequest;
import com.coffeeshop.mycoffee.dto.productdto.response.ProductResponse;
import com.coffeeshop.mycoffee.entity.Category;
import com.coffeeshop.mycoffee.entity.Product;
import com.coffeeshop.mycoffee.exception.AppException;
import com.coffeeshop.mycoffee.exception.ErrorCode;
import com.coffeeshop.mycoffee.mapper.ProductMapper;
import com.coffeeshop.mycoffee.repository.CategoryRepository;
import com.coffeeshop.mycoffee.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(ProductCreationRequest request) {
        if (productRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        Product product = productMapper.toProduct(request);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        product.setCategory(category);

//        if (imageFile != null && !imageFile.isEmpty()) {
//            String imageUrl = saveProductImage(imageFile, product.getId());
//            product.setImageUrl(imageUrl);
//        }

        try {
            product = productRepository.save(product);
        }catch (DataIntegrityViolationException e){
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        return productMapper.toProductResponse(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponse> getProducts(){
        return productRepository.findAll().stream().map(productMapper::toProductResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Cập nhật `name` nếu `request.name` không phải là null
        if (request.getName() != null) {
            product.setName(request.getName());
        }

        // Cập nhật `price` nếu `request.price` không phải là null
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        // Cập nhật `category` nếu `request.categoryId` không phải là null
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
            product.setCategory(category);
        }

//        if (imageFile != null && !imageFile.isEmpty()) {
//            String imageUrl = saveProductImage(imageFile, productId);
//            product.setImageUrl(imageUrl);
//        }

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(String productId){
        productRepository.deleteById(productId);
    }

    public String saveProductImage(MultipartFile imageFile, String productId) throws IOException {
        if (imageFile.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }

        // Define the path where the image will be saved
        Path imagePath = Paths.get("images/products/" + productId + ".jpg");

        // Create directories if they do not exist
        Files.createDirectories(imagePath.getParent());

        // Save the image file
        Files.write(imagePath, imageFile.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        product.setImageUrl(imagePath.toString());
        productRepository.save(product);

        // Return the image URL or path
        return imagePath.toString();
    }

}
