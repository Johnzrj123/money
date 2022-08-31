package com.bjpn.money.utils;

public class Page {
    private Long total;
    private Integer pageNum=1;
    private Integer startIndex=0;
    private Integer content=9;
    private Integer pageCount;
    private Integer nextPage;
    private Integer prePage;

    public Page() {
    }

    public Page(Long total, String pageNum) {
        this.total = total;
        if(pageNum!=null) {
            this.pageNum = Integer.parseInt(pageNum);
        }
        startIndex=(this.pageNum-1)*content;
        if(this.total%content==0){
            pageCount=(int)(this.total/content);
        }else{
            pageCount=(int)(this.total/content+1);
        }
        if(this.pageNum<pageCount) {
            nextPage = this.pageNum + 1;
        }else{
            nextPage=pageCount;
        }if(this.pageNum>1) {
            prePage = this.pageNum-1;
        }else{
            prePage=1;
        }
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public Integer getPrePage() {
        return prePage;
    }

    public void setPrePage(Integer prePage) {
        this.prePage = prePage;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getContent() {
        return content;
    }

    public void setContent(Integer content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", pageNum=" + pageNum +
                ", startIndex=" + startIndex +
                ", content=" + content +
                ", pageCount=" + pageCount +
                ", nextPage=" + nextPage +
                ", prePage=" + prePage +
                '}';
    }
}
