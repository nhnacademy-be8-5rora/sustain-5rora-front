package store.aurora.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class StringToLongListConverter implements Converter<String, List<Long>> {
    @Override
    public List<Long> convert(String source) {
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(source.split(",")) //,로 구분지어서 list화
                .map(String::trim)      //공백제거용
                .map(Long::parseLong)
                .toList();
    }
}
