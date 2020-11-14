package com.chozoi.product.domain.services;

import com.chozoi.product.app.responses.BlogHomeResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BlogService {
  public List<BlogHomeResponse> getForHome() {
    List<BlogHomeResponse> responses = new ArrayList<>();
    getFirst(responses);
    getSecond(responses);
    getThird(responses);
    getFourth(responses);
    responses.sort(Comparator.comparing(BlogHomeResponse::getCreatedAt).reversed());
    return responses;
  }

  private void getFourth(List<BlogHomeResponse> responses) {
    BlogHomeResponse blogHomeResponse =
        BlogHomeResponse.builder()
            .title("MÁY TIỆT TRÙNG BÌNH SỮA LÀ GÌ? CÓ NÊN MUA KHÔNG?")
            .createdAt(LocalDateTime.now().minusHours(96))
            .link("https://blog.chozoi.vn/may-tiet-trung-binh-sua-la-gi/")
            .imageUrl("https://blog.chozoi.vn/wp-content/uploads/2020/05/unnamed-66-300x157.jpg")
            .description(
                "Chất dinh dưỡng có trong sữa mẹ hay sữa công thức đều rất dễ sinh ra vi khuẩn. Vì thế làm sạch và tiệt trùng bình sữa sẽ giúp loại bỏ vi khuẩn, bảo vệ hệ miễn dịch của bé không bị vi khuẩn tấn công")
            .build();
    responses.add(blogHomeResponse);
  }

  private void getThird(List<BlogHomeResponse> responses) {
    BlogHomeResponse blogHomeResponse =
        BlogHomeResponse.builder()
            .title("11 CÁCH LÀM TRẮNG DA MẶT TỰ NHIÊN KHÔNG BẮT NẮNG TẠI NHÀ NHANH NHẤT")
            .createdAt(LocalDateTime.now().minusHours(72))
            .link(
                "https://blog.chozoi.vn/11-cach-lam-trang-da-mat-tu-nhien-khong-bat-nang-tai-nha-nhanh-nhat/")
            .imageUrl("https://blog.chozoi.vn/wp-content/uploads/2020/05/unnamed-11-300x200.jpg")
            .description(
                "Cách làm trắng da mặt tự nhiên không bắt nắng tại nhà nhanh nhất bằng các nguyên liệu cực rẻ, đơn giản mà hiệu quả! Áp dụng ngay các cách làm trắng da mặt")
            .build();
    responses.add(blogHomeResponse);
  }

  private void getSecond(List<BlogHomeResponse> responses) {
    BlogHomeResponse blogHomeResponse =
        BlogHomeResponse.builder()
            .title("ƯU ĐIỂM VÀ NHƯỢC ĐIỂM CỦA NỒI CHIÊN KHÔNG DẦU – CÓ NÊN MUA KHÔNG")
            .createdAt(LocalDateTime.now().minusHours(48))
            .link(
                "https://blog.chozoi.vn/uu-diem-va-nhuoc-diem-cua-noi-chien-khong-dau-co-nen-mua-khong/")
            .imageUrl("https://blog.chozoi.vn/wp-content/uploads/2020/05/unnamed-32-300x300.jpg")
            .description(
                "Chị em nào hay lướt internet hoặc hay mày mò tìm hiểu chắc đều không lạ gì “Nồi chiên không dầu”. Hiện đang là một trong những item hot nhất nhà bếp trong năm 2019 này.")
            .build();
    responses.add(blogHomeResponse);
  }

  private void getFirst(List<BlogHomeResponse> responses) {
    BlogHomeResponse blogHomeResponse =
        BlogHomeResponse.builder()
            .title("MÁY HÚT MÙI CÓ TÁC DỤNG GÌ? NHỮNG ĐIỀU CẦN LƯU Ý KHI SỬ DỤNG MÁY HÚT MÙI")
            .createdAt(LocalDateTime.now().minusHours(7))
            .link(
                "https://blog.chozoi.vn/may-hut-mui-co-tac-dung-gi-nhung-dieu-can-luu-y-khi-su-dung-may-hut-mui/")
            .imageUrl(
                "https://blog.chozoi.vn/wp-content/uploads/2020/05/pasted-image-0-300x300.png")
            .description(
                "Nhà bếp là nơi các chị em trổ tài nấu nướng chính vì thế mà không gian bếp luôn có những mùi rất khó chịu do thực phẩm, dầu mỡ gây ra khiến cho bầu không khí không còn trong")
            .build();
    responses.add(blogHomeResponse);
  }
}
