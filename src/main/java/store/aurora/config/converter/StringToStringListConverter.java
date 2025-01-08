package store.aurora.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class StringToStringListConverter implements Converter<String, List<String>> {
    @Override
    public List<String> convert(String source) {
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(source.split(",")) // ,로 구분하여 List<String>으로 변환
                .map(String::trim)  // 공백 제거
                .toList();
    }
}
