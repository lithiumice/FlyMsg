package online.hualin.flymsg.model.entiy;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:TODO
 * Create Time:2017/9/30 15:33
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class SearchResult<T> {


    private int total_count;
    private boolean incomplete_results;
    private List<T> items;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public boolean isIncomplete_results() {
        return incomplete_results;
    }

    public void setIncomplete_results(boolean incomplete_results) {
        this.incomplete_results = incomplete_results;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

}
