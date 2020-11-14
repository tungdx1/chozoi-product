package com.chozoi.product.domain.services.design_patterns.change_state_product;

import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.design_patterns.change_state_product.state.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StateFactory {
  @Autowired private PendingState pendingState;
  @Autowired private DraftState draftState;
  @Autowired private DeleteState deleteState;
  @Autowired private PublicState publicState;
  @Autowired private ReadyState readyState;
  @Autowired private RejectState rejectState;
  @Autowired private RejectAllState rejectAllState;
  @Autowired private ReportState reportState;
  @Autowired private StopState stopState;

  private StateFactory() {}

  public StateProduct getState(ProductState type) {
    switch (type) {
      case PENDING:
        return pendingState;
      case DRAFT:
        return draftState;
      case DELETED:
        return deleteState;
      case PUBLIC:
        return publicState;
      case READY:
        return readyState;
      case REJECT:
        return rejectState;
      case REJECTPRODUCT:
        return rejectAllState;
      case REPORT:
        return reportState;
      case STOPPED:
        return stopState;
      default:
        throw new IllegalArgumentException("This bank type is unsupported");
    }
  }
}
