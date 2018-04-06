package org.geelato.core.gql.execute;

import java.util.List;

/**
 * Created by hongxq on 2015/6/3.
 */
public class Page<E> {
    private int pageNum;
    private int pageSize;
    private int limit;
    private int startRow;
    private int offset;
    private int endRow;
    private long total;
    private int pages;
    private List<E> result;

    /**
     * @param pageNum 开始记录数 =page(客户端请求参数中)=offset(in mysql)，第一条记录从1开始，而不是从0开始
     * @param pageSize 每页记录数 =pageSize(客户端请求参数中)=limit(in mysql)
     */
    public Page(int pageNum, int pageSize) {
        this.setPageNum(pageNum);
        this.setPageSize(pageSize);
    }

    public List<E> getResult() {
        return result;
    }

    public void setResult(List<E> result) {
        this.result = result;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getEndRow() {
        this.endRow = pageNum * pageSize;
        return endRow;
    }

//    public void setEndRow(int endRow) {
//        this.endRow = endRow;
//    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
        this.offset = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.limit=pageSize;
    }

    public int getStartRow() {
        this.startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        return startRow;
    }

//    public void setStartRow(int startRow) {
//        this.startRow = startRow;
//    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }


    public int getOffset() {
        return offset;
    }


    @Override
    public String toString() {
        return "Page{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", startRow=" + startRow +
                ", endRow=" + endRow +
                ", total=" + total +
                ", pages=" + pages +
                '}';
    }
}
