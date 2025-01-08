package store.aurora.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.cart.dto.CartItemDTO;

import store.aurora.feign_client.book.BookClient;
import store.aurora.feign_client.order.OrderTemporaryStorageClient;
import store.aurora.order.dto.CheckoutBookDTO;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.dto.OrderResponseDto;
import store.aurora.order.dto.SubmitResponse;
import store.aurora.order.exception.BookClientResolveFailException;
import store.aurora.order.exception.CartEmptyException;
import store.aurora.order.exception.OrderTemporaryStorageClientResolverFailException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");
    private static final String BOOK_LIST = "bookList";
    private static final String TOTAL_PRICE = "totalPrice";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BookClient bookClient;
    private final OrderTemporaryStorageClient orderTemporaryStorageClient;

    //장바구니 구매
    @GetMapping("/order/cart/checkout")
    public String cartCheckoutForm(Principal principal){
        if(principal == null){
            return "redirect:/order/cart/checkout/non-member";
        }

        return "redirect:/order/cart/checkout/member";
    }

    //회원의 장바구니 구매
    @GetMapping("/order/cart/checkout/member")
    public String memberCartCheckoutForm(@CookieValue(value = "CART", required = false) String cartValueAsString, Model model){

        //1. 쿠키에서 상품꺼내기
        if(cartValueAsString == null){
            throw new CartEmptyException();
        }
        List<CartItemDTO> cartItemDTOS = stringToCartItemDto(cartValueAsString);
        if(cartItemDTOS.isEmpty()){
            throw new CartEmptyException(); //todo 바꾸기
        }

        //2.책정보 가져오기
        List<CheckoutBookDTO> bookList = null;
        try{
            bookList = cartItemToCheckoutBook(cartItemDTOS);
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        log.info("checkout book list={}", bookList);

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));

        return "/order/order-form-member";
    }

    //비회원의 장바구니 구매
    @GetMapping("/order/cart/checkout/non-member")
    public String nonMemberCartCheckoutForm(@CookieValue(value = "CART", required = false) String cartValueAsString, Model model){

        //1. 쿠키에서 상품꺼내기
        if(cartValueAsString == null){
            throw new CartEmptyException();
        }
        List<CartItemDTO> cartItemDTOS = stringToCartItemDto(cartValueAsString);
        if(cartItemDTOS.isEmpty()){
            throw new CartEmptyException(); //todo 바꾸기
        }

        //2.책정보 가져오기
        List<CheckoutBookDTO> bookList = null;
        try{
            bookList = cartItemToCheckoutBook(cartItemDTOS);
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        log.info("checkout book list={}", bookList);

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));

        return "/order/order-form-non-member";
    }

    //즉시구매
    @GetMapping("/order/direct/checkout")
    public String directCheckoutForm(@RequestParam("item-id")Long itemId, @RequestParam("quantity")Integer quantity, Principal principal){
        if(principal == null){
            return "redirect:/order/direct/checkout/non-member?item-id=" + itemId + "&quantity=" + quantity;
        }
        return "redirect:/order/direct/checkout/member?item-id=" + itemId + "&quantity=" + quantity;
    }

    //회원의 즉시구매
    @GetMapping("/order/direct/checkout/member")
    public String memberDirectCheckoutForm(@RequestParam("item-id")Long itemId, @RequestParam("quantity")Integer quantity, Model model){

        List<CheckoutBookDTO> bookList = null;
        try{
            bookList = cartItemToCheckoutBook(List.of(new CartItemDTO(itemId, quantity)));
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        log.info("checkout book list={}", bookList);

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));

        return "/order/order-form-member";
    }

    //비회원의 즉시구매
    @GetMapping("/order/direct/checkout/non-member")
    public String nonMemberDirectCheckoutForm(@RequestParam("item-id")Long itemId, @RequestParam("quantity")Integer quantity, Model model){

        List<CheckoutBookDTO> bookList = null;
        try{
             bookList = cartItemToCheckoutBook(List.of(new CartItemDTO(itemId, quantity)));
        }catch (FeignException e){
            throw new BookClientResolveFailException(e);
        }

        log.info("checkout book list={}", bookList);

        model.addAttribute(BOOK_LIST, bookList);
        model.addAttribute(TOTAL_PRICE, calculateTotalPrice(bookList));

        return "/order/order-form-non-member";
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
        model.addAttribute("clientKey", "test_ck_Z61JOxRQVEY4gEJjlQd0VW0X9bAq");
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
                      @RequestParam("amount") Integer amount){

        log.info("orderId={}, paymentKey={}, amount={}", orderId, paymentKey, amount);
        //todo: db에 값을 넣는 api 호출
        log.info("결제처리 로직이 들어가야함");

        return "redirect:/";
    }

    @GetMapping("/order/payment/fail")
    public String paymentFail(){

        log.info("payment fail");

        return "redirect:/";
    }

    private List<CartItemDTO> stringToCartItemDto(String encodedValue){

        String decodedValue = URLDecoder.decode(encodedValue, StandardCharsets.UTF_8);

        try{
            return objectMapper.readValue(decodedValue, new TypeReference<List<CartItemDTO>>() {});
        } catch (JsonMappingException e) {
            log.info("데이터 형식이 잘못되었습니다. value={}", decodedValue);
            throw new RuntimeException(e); //todo 예외 바꾸기
        } catch (JsonProcessingException e) {
            log.info("JSON 처리 중 문제가 발생했습니다. value={}", decodedValue);
            throw new RuntimeException(e);
        }
    }

    private List<CheckoutBookDTO> cartItemToCheckoutBook(List<CartItemDTO> cartItems) throws FeignException{
        return cartItems.stream().map(ci -> {
            BookDetailsDto bookDetailsDto = bookClient.getBookDetails(ci.getBookId()).getBody();
            return new CheckoutBookDTO(ci.getBookId(), bookDetailsDto.getTitle(), bookDetailsDto.getSalePrice(), ci.getQuantity());
        }).toList();
    }

    private int calculateTotalPrice(List<CheckoutBookDTO> checkoutBookList){
        return checkoutBookList.stream().mapToInt(CheckoutBookDTO::price).sum();
    }
}
