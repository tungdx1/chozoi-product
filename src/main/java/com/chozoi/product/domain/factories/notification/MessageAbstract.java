package com.chozoi.product.domain.factories.notification;

import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.elasticsearch.ProfileEs;
import com.chozoi.product.domain.producers.MailProducer;
import com.chozoi.product.domain.producers.NotificationMessageProducer;
import com.chozoi.product.domain.repositories.elasticsearch.AuctionInstantBidRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProfileRepository;
import com.chozoi.product.domain.repositories.postgres.ProductReportIssueRepository;
import com.chozoi.product.domain.repositories.postgres.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MessageAbstract {
  @Autowired protected UserRepository userRepository;
  @Autowired protected ProfileRepository profileRepository;
  @Autowired protected ProductEsRepository productEsRepository;
  @Autowired protected AuctionInstantBidRepository auctionInstantBidRepository;
  @Autowired protected ProductReportIssueRepository reportIssueRepository;

  @Autowired protected MailProducer mailproducer;
  @Autowired protected NotificationMessageProducer notificationProducer;

  protected String getUserName(Integer id) {
    ProfileEs profileEs = profileRepository.findById(id).orElse(new ProfileEs());
    return profileEs.getName();
  }

  protected String getImageProduct(Long id) {
    try {
      ProductEs productEs =
          productEsRepository.findById(id).orElseThrow(() -> new Exception("not found"));
      return productEs.getImages().get(0).getImageUrl();
    } catch (Exception e) {
      return "";
    }
  }
}
