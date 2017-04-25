package hvcntt.org.shoppingweb.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hvcntt.org.shoppingweb.dao.dto.ProductDto;
import hvcntt.org.shoppingweb.dao.entity.Category;
import hvcntt.org.shoppingweb.dao.entity.Image;
import hvcntt.org.shoppingweb.dao.entity.Product;
import hvcntt.org.shoppingweb.dao.entity.TransactionType;
import hvcntt.org.shoppingweb.dao.repository.*;
import hvcntt.org.shoppingweb.exception.ProductNotFoundException;
import hvcntt.org.shoppingweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
public class ProductServiceImpl implements ProductService {

    private static final int PAGE_SIZE = 8;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findOne(String idproduct) {
        return productRepository.findOne(idproduct);
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Override
    public void updateView(String idproduct) {
        productRepository.updateView(idproduct);
    }

    @Override
    public List<Product> findByProductTransactionType(TransactionType transactionType) {
        return productRepository.findByTransactionType(transactionType, new PageRequest(0, 12)).getContent();
    }

    @Override
    public List<Product> findByCategoryAndPrice(Category category, float minPrice, float maxPrice) {
        return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
    }

    @Override
    public List<Product> findByCategoryAndPriceBetweenAndProductIdNotIn(Category category, float minPrice,
                                                                        float maxPrice, String productId) {
        return productRepository.findByCategoryAndPriceBetweenAndProductIdNotIn(category, minPrice, maxPrice, productId);
    }

    @Override
    public Page<Product> getProductPaging(int pagenumber) {
        PageRequest request = new PageRequest(pagenumber - 1, PAGE_SIZE);
        return productRepository.findAll(request);
    }

    @Override
    public void deleteProduct(String productId) throws ProductNotFoundException {
        if (productRepository.getOne(productId) != null) {
            productRepository.delete(productId);
        } else {
            throw new ProductNotFoundException("Product not found with Id : " + productId);
        }
    }

    @Override
    public void save(ProductDto productDto) throws ParseException {
        Product product = new Product();
        product.setCategory(categoryRepository.getOne(productDto.getCategoryId()));
        product.setDescription(productDto.getDescription());
        product.setCreateDate(new Date());
        product.setManufactureDate(formatStringToDate(productDto.getManufactureDate()));
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setSupplier(supplierRepository.getOne(productDto.getSupplierId()));
        product.setTransactionType(transactionTypeRepository.getOne(productDto.getTransactionTypeId()));
        product.setName(productDto.getName());
        List<Image> images = getImageUrlFromMultiFile(productDto.getImages(), product);
        product.setImages(images);
        productRepository.save(product);
        imageRepository.save(images);
    }

    private Date formatStringToDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(date);
    }

    private List<Image> getImageUrlFromMultiFile(List<MultipartFile> multipartFiles, Product product) {
        List<Image> images = new ArrayList<>();
        if (null != multipartFiles && multipartFiles.size() > 0) {
            int i = 1;
            for (MultipartFile multipartFile : multipartFiles) {
                String fileName = multipartFile.getOriginalFilename();
                Image image = new Image();
                image.setImageUrl(fileName);
                image.setImageTitle(product.getName() + " " + i);
                image.setProduct(product);
                images.add(image);
                try {
                    String uploadsDir = "/resource/images/product/";
                    String realPathtoUploads =  request.getServletContext().getRealPath(uploadsDir);
                    String orgName = multipartFile.getOriginalFilename();
                    String filePath = realPathtoUploads + orgName;
                    File dest = new File(filePath);
                    multipartFile.transferTo(dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return images;
    }

    @Override
    public Page<Product> findProductPaging(TransactionType transactionType, Pageable pageable) {
        return productRepository.findByTransactionType(transactionType, pageable);
    }


}
