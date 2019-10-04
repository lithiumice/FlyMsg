package online.hualin.flymsg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;

import online.hualin.flymsg.R;
import online.hualin.flymsg.adapter.FileBrowseAdapter;

public class FileBrowserActivity extends AppCompatActivity {

    private String root;
    private String currentPath;

    private ArrayList<String> targets=new ArrayList<>();
    private ArrayList<String> paths =new ArrayList<>();

    private File targetFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setToolbar(toolbar, 1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button doneButton=findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        root = Environment.getExternalStorageDirectory().getAbsoluteFile().getPath();
//        root = "/";
        currentPath = root;

        targetFile = null;
        targets = null;
        paths = null;

        showDir(currentPath);
    }

    public void selectDirectory(View view) {
        targetFile = new File(currentPath);

        returnTarget();
    }

    private void returnTarget() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("FilePath", targetFile.getPath());
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    public void setCurrentPathText(String message) {
        TextView textView = findViewById(R.id.main_titile);
        textView.setText(message);
    }

    public void showDir(String targetDir) {
        setCurrentPathText(currentPath);

        targets=new ArrayList<>();
        paths=new ArrayList<>();

        File f = new File(targetDir);
        File[] directoryContents = f.listFiles();

        if (!targetDir.equals(root)) {
            targets.add(root);
            paths.add(root);
            targets.add("../");
            paths.add(f.getParent());
        }

        for (File target : directoryContents) {
            paths.add(target.getPath());

            if (target.isDirectory()) {
                targets.add(target.getName() + "/");
            } else {
                targets.add(target.getName());
            }
        }

//        Log.d("file list",targets.size()+"");

        RecyclerView recyclerView = findViewById(R.id.file_browse_rv);
        FileBrowseAdapter fileBrowseAdapter = new FileBrowseAdapter(targets);
        fileBrowseAdapter.openLoadAnimation();
        recyclerView.setAdapter(fileBrowseAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
//        ArrayAdapter<String> arrayAdapter=new RecyclerView.Adapter<String>(this,android.R.layout.simple_list_item_1,targets);
//        recyclerView.setAdapter(arrayAdapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {

                                                @Override
                                                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                                                    File f = new File(paths.get(position));
                                                    if (f.isFile()) {
                                                        targetFile = f;
                                                        returnTarget();
                                                    } else {
                                                        if (f.canRead()) {
                                                            currentPath = paths.get(position);
                                                            showDir(paths.get(position));
                                                        }
                                                    }
                                                }

                                            }
        );
    }
    public void setToolbar(Toolbar toolbar, int indicator) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                if (indicator == 0) {
                    indicator = R.drawable.ic_menu;
                } else if (indicator == 1) {
                    indicator = R.drawable.ic_arrow_back;
                }
                getSupportActionBar().setHomeAsUpIndicator(indicator);

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
