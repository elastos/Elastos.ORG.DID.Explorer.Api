package org.elastos.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ListPage {
    public static <T>List<T> getListPage(List<T> list, int offset, int size){
        Pageable pageable = PageRequest.of(offset, size);
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        List<T> subList = list.subList(start, end);
        return subList;
    }
}
