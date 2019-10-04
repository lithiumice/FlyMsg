package online.hualin.flymsg.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import online.hualin.flymsg.R;

public class FileBrowseAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public FileBrowseAdapter( @Nullable List data) {
        super(R.layout.file_browse, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.setText(R.id.file_name, item);
    }

}
