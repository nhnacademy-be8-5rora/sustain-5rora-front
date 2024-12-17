package store.aurora.config.security.exception.userInfoNotFound;

public class PaycoUserInfoNotFound extends Oauth2UserInfoNotFound{
    public PaycoUserInfoNotFound(int code, String resultMessage) {
        super(String.format("code=%d, resulteMessage=%s", code, resultMessage));
    }
}
