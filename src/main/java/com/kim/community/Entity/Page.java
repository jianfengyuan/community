package com.kim.community.Entity;
/**
 * 封裝分頁信息
 * */
public class Page {
    private int current = 1;
    private int limit = 10;
    private int rows;
    // 查詢路徑(用於複用分頁鏈接)
    private String Path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit>= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows>=0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    // 通過當前頁計算出當前頁的起始行
    public int getOffset() {
        return (current - 1) * limit;
    }

    // 獲取總頁數
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", Path='" + Path + '\'' +
                '}';
    }
}
