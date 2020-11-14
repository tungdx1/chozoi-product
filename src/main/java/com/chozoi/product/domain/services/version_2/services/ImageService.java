package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductImage;
import com.chozoi.product.domain.entities.postgres.types.ProductImageState;
import com.chozoi.product.domain.repositories.postgres.ProductImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ImageService {
  @Autowired private ProductImageRepository productImageRepository;

  /**
   * remove with image in productOld not in productNew update image in productOld and productNew
   * create image with id null
   *
   * @param productOld
   * @param productNew
   */
  public void handleForUpdate(Product productOld, Product productNew) throws Exception {
    List<ProductImage> imagesOld = productOld.getImages();
    List<ProductImage> imagesInput = productNew.getImages();
    imagesOld.forEach(
        image -> {
          if (checkExist(image, imagesInput)) image.setUpdateStatus(false);
          else {
            image.setUpdateStatus(true);
            if (image.getState().equals(ProductImageState.PENDING)) image.setState(ProductImageState.DELETED);
          }
        });
    imagesInput.forEach(
        image -> {
          if (image.getId() == null) {
            image.setState(ProductImageState.PENDING);
            image.setUpdateStatus(false);
            imagesOld.add(image);
          }
        });
    productNew.setImages(imagesOld);
  }

  private boolean checkExist(ProductImage image, List<ProductImage> imagesInput) {
    List<ProductImage> imagesInput1 =
        imagesInput.stream().filter(image1 -> image1.getId() != null).collect(Collectors.toList());
    if (imagesInput1.size() == 0) return false;
    List<ProductImage> productImages =
        imagesInput1.stream()
            .filter(image1 -> image1.getId().equals(image.getId()))
            .collect(Collectors.toList());
    return productImages.size() > 0;
  }

  /**
   * image pending to public
   *
   * @param productNew
   * @param productOld
   */
  public void acceptImage(Product productNew, Product productOld) {

    List<ProductImage> imagesDelete =
        productOld.getImages().stream()
            .filter(ProductImage::getUpdateStatus)
            .collect(Collectors.toList());
    List<ProductImage> imageApproved =
        productOld.getImages().stream()
            .filter(image -> !image.getUpdateStatus())
            .collect(Collectors.toList());
    imagesDelete.forEach(ProductImage::setDelete);
    imageApproved.forEach(ProductImage::setPublic);
    productNew.setImages(productOld.getImages());
  }
}
