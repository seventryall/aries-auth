package com.star.aries.auth.common.pojo;

import lombok.Data;

@Data
public class Pagination {

    private String sql;

    //一页显示的记录数
    private int pageSize;
    //当前页码
    private int currentPage;
    //起始行数
    private int startIndex;
    //结束行数
//    private int lastIndex;
    //记录总数
//    private int totalRows;
    //总页数
//    private int totalPages;

    public Pagination(String sql, int currentPage) {
        this(sql, currentPage, 10);
    }

    public Pagination(String sql, int currentPage, int pageSize) {
        //Assert.hasText(sql, "sql empty");
        this.sql = sql.trim();
        if (this.sql.endsWith(";")) {
            this.sql = this.sql.substring(0, this.sql.length() - 1);
        }
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        setStartIndex();
    }

    public String getCountSQL() {
        return "SELECT COUNT(1) FROM (" + sql + ") aliasForPage";
    }

    public String getPageSQL() {
        String pageSQL = sql + " limit " + startIndex + "," + pageSize;
        return pageSQL;
    }

    /**
     * 计算起始行数
     */
    private void setStartIndex() {
        this.startIndex = (currentPage - 1) * pageSize;
    }

}
