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
import hvcntt.org.shoppingweb.dao.entity.Supplier;
import hvcntt.org.shoppingweb.dao.entity.TransactionType;
import hvcntt.org.shoppingweb.dao.repository.*;
import hvcntt.org.shoppingweb.exception.ProductNotFoundException;
import hvcntt.org.shoppingweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
public class ProductServiceImpl implements ProductService {

    private static final int PAGE_SIZE = 8;

    @Value("${UPLOAD_DIR_TARGET}")
    private String UPLOAD_DIR_TARGET;

    @Value("${UPLOAD_DIR_SRC}")
    private String UPLOAD_DIR_SRC;

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
    public Product findOne(String productId) {
        return productRepository.findOne(productId);
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Override
    public void updateView(String productId) {
        productRepository.updateView(productId);
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
    public Page<Product> getProductPaging(int pageNumber) {
        PageRequest request = new PageRequest(pageNumber - 1, PAGE_SIZE);
        return productRepository.findAll(request);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public void deleteProduct(String productId) throws ProductNotFoundException {
        if (productRepository.getOne(productId) != null) {
            productRepository.delete(productId);
        } else {
            throw new ProductNotFoundException("Product not found with Id : " + productId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
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
        List<MultipartFile> multipartFiles = getMultipartFiles(productDto);
        List<Image> images = getImageUrlFromMultiFile(multipartFiles, product);
        product.setImages(images);
        productRepository.save(product);
        imageRepository.save(images);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public void update(ProductDto productDto, String productId) throws ParseException {
        Product product = productRepository.findOne(productId);
        product.setCategory(categoryRepository.getOne(productDto.getCategoryId()));
        String description = productDto.getDescription();
        if (!description.isEmpty()) {
            product.setDescription(description);
        }
        product.setCreateDate(new Date());
        product.setManufactureDate(formatStringToDate(productDto.getManufactureDate()));
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setSupplier(supplierRepository.getOne(productDto.getSupplierId()));
        product.setTransactionType(transactionTypeRepository.getOne(productDto.getTransactionTypeId()));
        product.setName(productDto.getName());
        List<MultipartFile> multipartFiles = getMultipartFiles(productDto);
        if (!multipartFiles.isEmpty() && multipartFiles.size() > 0) {
            List<Image> images = getImageUrlFromMultiFile(multipartFiles, product);
            if (!images.isEmpty()) {
                List<Image> imagesByProduct = imageRepository.findByProduct(product);
                if (!imagesByProduct.isEmpty()) {
                    for (Image image : imagesByProduct){
                        imageRepository.delete(image);
                    }
                }
                product.setImages(images);
                productRepository.save(product);
                imageRepository.save(images);
            } else {
                productRepository.save(product);
            }

        } else {
            productRepository.save(product);
        }
    }

    private List<MultipartFile> getMultipartFiles(ProductDto productDto) {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(productDto.getImage1());
        multipartFiles.add(productDto.getImage2());
        multipartFiles.add(productDto.getImage3());
        return multipartFiles;
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
                if (!fileName.isEmpty()) {
                    Image image = new Image();
                    image.setImageUrl(fileName);
                    image.setImageTitle(product.getName() + " " + i);
                    image.setProduct(product);
                    images.add(image);
                }
                try {
                    String targetPathToUploads = request.getServletContext().getRealPath(UPLOAD_DIR_TARGET);
                    if (!new File(targetPathToUploads).exists()) {
                        new File(targetPathToUploads).mkdir();
                    }
                    String filePathTarget = targetPathToUploads + fileName;
                    String filePathSrc = UPLOAD_DIR_SRC + fileName;
                    File destTarget = new File(filePathTarget);
                    File destSrc = new File(filePathSrc);
                    multipartFile.transferTo(destTarget);
                    multipartFile.transferTo(destSrc);
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

    @Override
    public List<Product> findByTransactionType(TransactionType transactionType) {
        return productRepository.findByTransactionType(transactionType);
    }

    @Override
    public List<Product> findByIds(List<String> productIds) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds){
            Product product = productRepository.findOne(productId);
            products.add(product);
        }
        return products;
    }

	@Override
	public List<Product> findBySupplier(Supplier supplier) {
		return productRepository.findBySupplier(supplier);
	}

	@Override
	public List<Product> getPriceHighestToLower() {
		return productRepository.findAll(new Sort(Direction.ASC, "price"));
	}

	@Override
	public List<Product> getPriceLowerToHighest() {
		return productRepository.findAll(new Sort(Direction.DESC, "price"));
	}

	@Override
	public List<Product> findByTransactionType(TransactionType transactionType, Sort sort) {
		return productRepository.findByTransactionType(transactionType, sort);
	}

	@Override
	public List<Product> findByTransactionType(List<Product> products, TransactionType transactionType, Sort sort) {
		return productRepository.findByTransactionType(products, transactionType, sort);
	}

	@Override
	public List<Product> getHighView() {
		return productRepository.findAll(new PageRequest(0, 8,Direction.DESC,"viewNumber")).getContent();
	}

    @Override
    public List<Product> findByCategory(String categoryId) {
        Category category = categoryRepository.findOne(categoryId);
        return productRepository.findByCategory(category);
    }


}
