package com.chozoi.product.domain.schedules.notification;

import com.chozoi.product.domain.entities.postgres.NotificationSegment;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.repositories.postgres.NotificationSegmentRepository;
import com.chozoi.product.domain.schedules.notification.handle.TypeHandle;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class ScheduleNotification {
  @Autowired private NotificationSegmentRepository notificationSegmentRepository;
  @Autowired private Factory factory;

  @Scheduled(cron = "0/10 * * * * *")
  public void cronMessage() throws Exception {
    List<String> states =
        Arrays.asList(
            String.valueOf(SegmentState.TIME_REMAIN), String.valueOf(SegmentState.ORDER_PENDING));
    List<NotificationSegment> segments =
        notificationSegmentRepository.findByObjectTypeAndObjectValueIn(
            SegmentObjectType.AUCTION, states);
    // stopped
    TypeHandle stoppedHandle = factory.getTypeHandle(SegmentState.STOPPED);
    stoppedHandle.handle(segments);
    //    // to winner
    TypeHandle winnerHandle = factory.getTypeHandle(SegmentState.WINER);
    winnerHandle.handle(segments);
    // time remain for auction
    TypeHandle timeRemainHandle = factory.getTypeHandle(SegmentState.TIME_REMAIN);
    timeRemainHandle.handle(segments);
    // for order pending
    TypeHandle orderPendingHandle = factory.getTypeHandle(SegmentState.ORDER_PENDING);
    orderPendingHandle.handle(segments);
  }
}
