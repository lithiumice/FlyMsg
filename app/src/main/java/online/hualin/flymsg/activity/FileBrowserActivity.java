package online.hualin.flymsg.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.View.FileBrowsePopup;
import online.hualin.flymsg.adapter.FileBrowseAdapter;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;

public class FileBrowserActivity extends LiteBaseActivity implements View.OnClickListener {

    private String root;
    private String currentPath;

    private ArrayList<String> targets = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();

    private File targetFile;
    private SharedPreferences pref;
    private CheckBox checkBox;
    private String downloadRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        Toolbar toolbar = findViewById(R.id.toolbar);
        checkBox = findViewById(R.id.is_remmember_path);
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pref.edit().putBoolean("RemmemberPath", checkBox.isChecked()).apply();

            }
        });
//        setToolbar(toolbar, 1);
        pref = App.getPref();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button saveButton = findViewById(R.id.sort_path);
        saveButton.setOnClickListener(this);
        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        boolean isRememberPath = pref.getBoolean("RemmemberPath", false);
        checkBox.setChecked(isRememberPath);

        downloadRoot = Environment.getExternalStorageDirectory().getAbsoluteFile().getPath();

        if (isRememberPath) {
            root = App.getPref().getString("LastPath", downloadRoot);
        } else {
            root = downloadRoot;
        }


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

        targets = new ArrayList<>();
        paths = new ArrayList<>();

        File f = new File(targetDir);
        File[] directoryContents = f.listFiles();

        if (!targetDir.equals(downloadRoot)) {
            targets.add(downloadRoot);
            paths.add(downloadRoot);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {

                                                @Override
                                                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                                                    File f = new File(paths.get(position));
                                                    if (f.isFile()) {
                                                        targetFile = f;

                                                        if (checkBox.isChecked()) {
                                                            pref.edit().putBoolean("RemmemberPath", true).apply();
                                                            pref.edit().putString("LastPath", f.getParent()).apply();
                                                        } else {
                                                            pref.edit().putBoolean("RemmemberPath", false).apply();
                                                        }

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
        if (item.getItemId() == android.R.id.home) {
            if (targets.get(1).equals("../")) {
                String f = paths.get(1);
                showDir(f);
            }
            else
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sort_path) {
//            pref.edit().putBoolean("RemmemberPath", checkBox.isChecked()).apply();

//            new FileBrowsePopup(getBaseContext()).showPopupWindow(v);
            QuickPopupBuilder.with(getApplicationContext())
                    .contentView(R.layout.file_browser_popup)
                    .config(new QuickPopupConfig()
                    .gravity(Gravity.TOP)
                    .withClick(R.id.sort_alphabet_acs, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pref.edit().putString("FileBrowseSort","acs").apply();
                        }
                    }))
                    .show(v);
        }
    }
}
