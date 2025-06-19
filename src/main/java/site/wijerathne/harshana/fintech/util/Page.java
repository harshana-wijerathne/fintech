package site.wijerathne.harshana.fintech.util;

import lombok.Getter;

import java.util.List;

@Getter
public class Page<T> {
    // Getters
    private final List<T> content;
    private final int currentPage;
    private final int pageSize;
    private final int totalRecords;
    private final int totalPages;

    public Page(List<T> content, int currentPage, int pageSize,
                int totalRecords, int totalPages) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
    }

    public boolean hasNext() { return currentPage < totalPages; }
    public boolean hasPrevious() { return currentPage > 1; }
}
