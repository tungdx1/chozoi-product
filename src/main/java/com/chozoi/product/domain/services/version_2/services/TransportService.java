package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.ShippingPartner;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.repositories.postgres.ShippingPartnerRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransportService {
  @Autowired private ShippingPartnerRepository shippingPartnerRepository;

  /**
   * Check transport for product
   *
   * @param product
   */
  @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.SERIALIZABLE)
  public void checkTransport(Product product) throws Exception {
    List<ShippingPartner> shippingPartners = shippingPartnerRepository.findAll();
    List<ProductVariant> productVariant = product.getVariants();
    Integer[] size = product.getPackingSize();
    Integer weight = ObjectUtils.defaultIfNull(product.getWeight(), -1);
    AtomicInteger count_accept = new AtomicInteger(0);
    shippingPartners.forEach(
        shippingPartner -> {
          // check price
          AtomicInteger count_error = new AtomicInteger(0);
          productVariant.forEach(
              variant -> {
                if (variant.getSalePrice() > shippingPartner.getMaxValue()) count_error.incrementAndGet();
              });
          // check weight
          if (weight > shippingPartner.getMaxWeight()) count_error.incrementAndGet();
          // check packingsize
          Integer[] sizePartner = shippingPartner.getMaxSize();
          Arrays.sort(size);
          Arrays.sort(sizePartner);
          List<Integer> sizeList = Arrays.asList(size);
          List<Integer> sizePartnerList = Arrays.asList(sizePartner);
          for (int i = 0; i < size.length; i++)
              if (sizeList.get(i) > sizePartnerList.get(i)) count_error.incrementAndGet();

          if (count_error.get() == 0) count_accept.incrementAndGet();
        });
    if (count_accept.get() == 0) throw new Exception(ExceptionMessage.NO_MATCHING_SHIPPING_UNITS);
  }
}
