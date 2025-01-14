package store.aurora.book.util;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.Collections;

public class PaginationUtil {

    public static <T> void addPaginationAttributes(Model model, Page<T> page, String modelName, int rangeSize) {
        model.addAttribute(modelName, page != null ? page.getContent() : Collections.emptyList());
        model.addAttribute("currentPage", page != null ? page.getNumber() : 0);
        model.addAttribute("totalPages", page != null ? page.getTotalPages() : 0);

        if (page != null) {
            int totalPages = page.getTotalPages();
            int currentPage = page.getNumber();
            int startPage = (currentPage / rangeSize) * rangeSize;
            int endPage = Math.min(startPage + rangeSize - 1, totalPages - 1);

            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
        } else {
            model.addAttribute("startPage", 0);
            model.addAttribute("endPage", 0);
        }

        model.addAttribute("rangeSize", rangeSize);
    }
}
