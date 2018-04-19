package com.lichi.goodrongyi.mvp.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 默小小 on 2017/12/19.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotCircleBean implements Serializable{
    public int pageNum;

    public int pageSize;

    public int size;

    public int startRow;

    public int endRow;

    public int total;

    public int pages;

    public List<DateList> list;

    public int prePage;

    public int nextPage;

    public boolean isFirstPage;

    public boolean isLastPage;

    public boolean hasPreviousPage;

    public boolean hasNextPage;

    public int navigatePages;

    public List<Integer> navigatepageNums;

    public int navigateFirstPage;

    public int navigateLastPage;

    public int firstPage;

    public int lastPage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateList implements Serializable {
        public int id;

        public String name;

        public String nickname;

        public String createUserId;

        public String createTime;

        public String updateTime;

        public String url;

        public String introduce;

        public int count;

        public int sort1;

        public int sort2;

    }
}