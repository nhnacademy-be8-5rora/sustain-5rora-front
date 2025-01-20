package store.aurora.order.controller;

import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.address.dto.UserAddressDTO;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.cart.dto.CartItemDTO;

import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.AddressClient;
import store.aurora.feign_client.book.BookClient;
import store.aurora.feign_client.order.OrderTemporaryStorageClient;
import store.aurora.feign_client.order.TossClient;
import store.aurora.feign_client.order.WrapClient;
import store.aurora.feign_client.point.PointHistoryClient;
import store.aurora.order.dto.*;
import store.aurora.order.exception.BookClientResolveFailException;
import store.aurora.order.exception.OrderTemporaryStorageClientResolverFailException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");
    private static final String BOOK_LIST = "bookList";
    private static final String TOTAL_PRICE = "totalPrice";
    private static final String AVAILABLE_POINTS = "availablePoints";
    private static final String WRAP_LIST = "wrapList";
    private static final String ADDRESS_LIST = "addressList";
    private static final String TODAY = "today";

    @Value("${toss.client-key}")
    private String tossClientKey;
    @Value("${toss.secret-key}")
    private String secretKey;

    private final BookClient bookClient;
    private final OrderTemporaryStorageClient orderTemporaryStorageClient;
    private final TossClient tossClient;
    private final PointHistoryClient pointHistoryClient;
    private final WrapClient wrapClient;
    private final AddressClient addressClient;

    //장바구니 구매
    @GetMapping("/order/cart/checkout")
    public String cartCheckoutForm(Principal principal,HttpServletRequest request){

        String queryString = request.getQueryString();
        log.info("query={}", queryString);

        if(principal == null){
            return "redirect:/order/cart/checkout/non-member?" + queryString;
        }

        return "redirect:/order/cart/checkout/member?" + queryString;
    }

    //회원의 장바구니 구매
    @GetMapping("/order/cart/checkout/member")
    public String memberCartCheckoutForm(@RequestParam("book-id") List<Long> bookIds,
                                         @RequestParam("quantity") List<Integer> quantities,
                                         @CookieValue(SecurityConstants.TOKEN_COOKIE_NAME) String jwtCookie,
                                         Model model){

        //1. 카트에서 상품꺼내기
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for (int i = 0; i < bookIds.size(); i++) {
            cartItemDTOS.add(new CartItemDTO(bookIds.get(i), quantities.get(i)));
        }

        //2.책정보 가져오기
        List<CheckoutBookDTO> bookList = null;
        try{
            bookList = cartItemToCheckoutBook(cartItemDTOS);
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        //3. 포장지 리스트 가져오기
        List<WrapResponseDTO> wrapList = getWrapList();

        //4. 회원의 주소 가져오기
        List<String> addressList = addressClient.getUserAddresses(SecurityConstants.BEARER_TOKEN_PREFIX + jwtCookie).stream().map(address ->
                address.getRoadAddress() + " " + address.getAddrDetail()).toList();

        log.info("checkout book list={}", bookList);

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));
        model.addAttribute(AVAILABLE_POINTS, pointHistoryClient.getAvailablePoints(SecurityConstants.BEARER_TOKEN_PREFIX + jwtCookie));
        model.addAttribute(WRAP_LIST, wrapList);
        model.addAttribute(ADDRESS_LIST, addressList);
        model.addAttribute(TODAY, LocalDate.now());

        return "order/order-form-member";
    }

    //비회원의 장바구니 구매
    @GetMapping("/order/cart/checkout/non-member")
    public String nonMemberCartCheckoutForm(@RequestParam("book-id") List<Long> bookIds,
                                            @RequestParam("quantity") List<Integer> quantities,
                                            Model model){

        //1. 카트에서 상품꺼내기
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for (int i = 0; i < bookIds.size(); i++) {
            cartItemDTOS.add(new CartItemDTO(bookIds.get(i), quantities.get(i)));
        }

        //2.책정보 가져오기
        List<CheckoutBookDTO> bookList = null;
        try{
            bookList = cartItemToCheckoutBook(cartItemDTOS);
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        //3. 포장지 리스트 가져오기
        List<WrapResponseDTO> wrapList = getWrapList();

        log.info("checkout book list={}", bookList);

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));
        model.addAttribute(WRAP_LIST, wrapList);
        model.addAttribute(TODAY, LocalDate.now());

        return "order/order-form-non-member";
    }

    //즉시구매
    @GetMapping("/order/direct/checkout")
    public String directCheckoutForm(@RequestParam("book-id")Long bookId,
                                     @RequestParam("quantity")Integer quantity,
                                     Principal principal){
        if(principal == null){
            return "redirect:/order/direct/checkout/non-member?book-id=" + bookId + "&quantity=" + quantity;
        }
        return "redirect:/order/direct/checkout/member?book-id=" + bookId + "&quantity=" + quantity;
    }

    //회원의 즉시구매
    @GetMapping("/order/direct/checkout/member")
    public String memberDirectCheckoutForm(@RequestParam("book-id")Long bookId,
                                           @RequestParam("quantity")Integer quantity,
                                           @CookieValue(SecurityConstants.TOKEN_COOKIE_NAME) String jwtCookie,
                                           Model model){

        List<CheckoutBookDTO> bookList = null;
        try{
            bookList = cartItemToCheckoutBook(List.of(new CartItemDTO(bookId, quantity)));
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        List<WrapResponseDTO> wrapList = getWrapList();

        List<String> addressList = addressClient.getUserAddresses(SecurityConstants.BEARER_TOKEN_PREFIX + jwtCookie).stream().map(address ->
                address.getRoadAddress() + " " + address.getAddrDetail()).toList();

        log.info("checkout book list={}", bookList);

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));
        model.addAttribute(AVAILABLE_POINTS, pointHistoryClient.getAvailablePoints(SecurityConstants.BEARER_TOKEN_PREFIX + jwtCookie));
        model.addAttribute(WRAP_LIST, wrapList);
        model.addAttribute(ADDRESS_LIST, addressList);
        model.addAttribute(TODAY, LocalDate.now());

        return "order/order-form-member";
    }

    //비회원의 즉시구매
    @GetMapping("/order/direct/checkout/non-member")
    public String nonMemberDirectCheckoutForm(@RequestParam("book-id")Long bookId,
                                              @RequestParam("quantity")Integer quantity, Model model){

        List<CheckoutBookDTO> bookList = null;
        try{
             bookList = cartItemToCheckoutBook(List.of(new CartItemDTO(bookId, quantity)));
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        log.info("checkout book list={}", bookList);

        List<WrapResponseDTO> wrapList = getWrapList();

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));
        model.addAttribute(WRAP_LIST, wrapList);
        model.addAttribute(TODAY, LocalDate.now());

        return "order/order-form-non-member";
    }

    /*
    * 주문자의 정보를 저장하고 결제창주소 보내기  백엔드에서 할일
    * */
    @PostMapping("/order/submit")
    @ResponseBody
    public SubmitResponse orderSubmit(@RequestBody OrderRequestDto orderRequestDto){

        log.info("order request={}", orderRequestDto);
        ResponseEntity<SubmitResponse> submitResponseResponseEntity;
        try{
             submitResponseResponseEntity = orderTemporaryStorageClient.saveOrderInfo(orderRequestDto);
        }catch (FeignException e){
            throw new OrderTemporaryStorageClientResolverFailException(e);
        }

        //todo 상태코드에 따라 달리 처리하기
        HttpStatusCode statusCode = submitResponseResponseEntity.getStatusCode();
        if(statusCode.is5xxServerError() || statusCode.is4xxClientError()){
            log.info("주문정보 저장 실패:{}", statusCode);
            return new SubmitResponse("/");
        }

        log.info("response body={}", submitResponseResponseEntity.getBody());

        return submitResponseResponseEntity.getBody();
    }

    @GetMapping("/order/payment")
    public String paymentForm(@RequestParam("order-id") String orderId, Model model){

        //페인클라이언트로 결제에 필요한 정보 요청
        ResponseEntity<OrderResponseDto> orderResponseDtoResponseEntity;
        try{
            orderResponseDtoResponseEntity = orderTemporaryStorageClient.getOrderInfo(orderId);
        }catch (FeignException e){
            throw new OrderTemporaryStorageClientResolverFailException(e);
        }

        //todo 상태코드에 따라 달리 처리하기
        HttpStatusCode statusCode = orderResponseDtoResponseEntity.getStatusCode();
        if(statusCode.is5xxServerError() || statusCode.is4xxClientError()){
            log.info("주문정보 불러오기 실패:{}", statusCode);
            return "redirect:/";
        }

        OrderResponseDto orderResponseDto = orderResponseDtoResponseEntity.getBody();
        log.info("paymentInfo={}", orderResponseDto);

        //결제 관련 키
        model.addAttribute("clientKey", tossClientKey);
        model.addAttribute("customerKey", orderResponseDto.getCustomerKey());

        // 상품 정보
        model.addAttribute("amount", Map.of("currency", orderResponseDto.getCurrency(), "value", orderResponseDto.getValue()));
        model.addAttribute("orderId", orderId); //주문 번호
        model.addAttribute("orderName", orderResponseDto.getOrderName());
        return "order/payment";
    }

    //결제처리 페이지
    @GetMapping("/order/payment/success")
    public String paymentSuccess(@RequestParam("orderId") String orderId,
                                 @RequestParam("paymentKey") String paymentKey,
                                 @RequestParam("amount") Integer amount,
                                 HttpServletResponse response,
                                 Principal principal){

        log.info("orderId={}, paymentKey={}, amount={}", orderId, paymentKey, amount);
        //1. toss 결제 확정 api 호출 todo 결제 확정 api어디서 호출할지 논의
        ResponseEntity<String> stringResponseEntity = tossClient.confirmCheck(makeAuthorization(secretKey), new PaymentRequest(paymentKey, amount, orderId));
        log.info("토스 결제 내역: {}", stringResponseEntity.getBody());

        //2. db에 저장
        ResponseEntity<Long> orderIdResponse;
        try {
            orderIdResponse = orderTemporaryStorageClient.orderComplete(new OrderCompleteRequestDto( orderId, paymentKey, amount,Objects.isNull(principal)));
        }catch (FeignException e){
            throw new OrderTemporaryStorageClientResolverFailException(e);
        }

        //db 저장 실패
        HttpStatusCode statusCode = orderIdResponse.getStatusCode();
        if(statusCode.is4xxClientError() || statusCode.is5xxServerError()){
            return "redirect:/";
        }

        //3. 비회원의 카트 비우기
        removeNonMemberCart(response);

        return "redirect:/order/complete?completed=" + orderIdResponse.getBody();
    }

    @GetMapping("/order/payment/fail")
    public String paymentFail(){

        log.info("payment fail");

        return "redirect:/";
    }

    @GetMapping("/order/complete")
    public String orderCompleteForm(@RequestParam("completed") String orderId, Model model){
        model.addAttribute("orderId", orderId);
        return "order/order-complete";
    }

    private List<CheckoutBookDTO> cartItemToCheckoutBook(List<CartItemDTO> cartItems) throws FeignException{
        return cartItems.stream().map(ci -> {
            BookDetailsDto bookDetailsDto = bookClient.getBookDetails(ci.getBookId()).getBody();
            return new CheckoutBookDTO(ci.getBookId(), bookDetailsDto.getTitle(), bookDetailsDto.getSalePrice(), ci.getQuantity());
        }).toList();
    }

    private int calculateTotalPrice(List<CheckoutBookDTO> checkoutBookList){
        return checkoutBookList.stream().mapToInt( b -> b.price() * b.quantity()).sum();
    }

    private String makeAuthorization(String secretKey){
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }

    private void removeNonMemberCart(HttpServletResponse response){
        Cookie cookie = new Cookie("CART", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private List<WrapResponseDTO> getWrapList(){
        return wrapClient.getWrapList().getBody();
    }
}
