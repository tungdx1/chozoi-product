package com.chozoi.product.domain.services.version_2;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.domain.services.version_2.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseFacade {
  @Autowired protected Product2Service product2Service;

  @Autowired protected Shop2Service shop2Service;
  @Autowired protected AuctionService auctionService;

  @Autowired protected Category2Service categoryService;

  @Autowired protected ProductStatService productStatService;

  @Autowired protected TransportService transportService;

  @Autowired protected ProductDraftService productDraftService;

  @Autowired protected ProductEvent productEvent;
  @Autowired protected ModelMapper modelMapper;
}
