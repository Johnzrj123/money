package com.bjpn.money.model;

import java.io.Serializable;

/**
 * 总记录数、总页数、当前页、首页、尾页、每页显示条数
 */
public class PageModel implements Serializable {
    private Long totalCount;
    private Long totalPage;
    private Long cunPage;
    private Integer firstPage=1;
    private Integer pageSize=10;

    public PageModel() {
    }

    public PageModel(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalPage() {
        return totalCount%pageSize>0 ? totalCount/pageSize+1 : totalCount/pageSize;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getCunPage() {
        return cunPage;
    }

    public void setCunPage(Long cunPage) {
        this.cunPage = cunPage;
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
