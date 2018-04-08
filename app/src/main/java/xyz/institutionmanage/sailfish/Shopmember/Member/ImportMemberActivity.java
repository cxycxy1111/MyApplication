package xyz.institutionmanage.sailfish.Shopmember.Member;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseActivity;
import xyz.institutionmanage.sailfish.Util.Ref;

public class ImportMemberActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG="IMemberActivity:";
    private String str_path;
    private File file;
    private TextView tv_name,tv_path;
    private Button btn_clear,btn_select,btn_import;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_member);
        initComponent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Ref.REQCODE_ADD:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Uri uri = data.getData();
                        if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                            str_path = uri.getPath();
                            tv_path.setText(str_path);
                            if (str_path.length()>1) {
                                String[] str = str_path.split("/");
                                tv_name.setText(str[str.length-1]);
                            }
                            break;
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                            str_path = getPath(this, uri);
                            tv_path.setText(str_path);
                            if (str_path.length()>1) {
                                String[] str = str_path.split("/");
                                tv_name.setText(str[str.length-1]);
                            }
                        } else {//4.4以下下系统调用方法
                            str_path = getRealPathFromURI(uri);
                            tv_path.setText(str_path);
                            if (str_path.length()>1) {
                                String[] str = str_path.split("/");
                                tv_name.setText(str[str.length-1]);
                            }
                        }
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_a_import_member:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                intent.setType("application/vnd.ms-excel");//无类型限制
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, Ref.REQCODE_ADD);
                select();
                break;
                /**
            case R.id.btn_import_a_import_member:
                if ("".equals(str_path)) {
                    Toast.makeText(this,"你暂未选择文件",Toast.LENGTH_SHORT).show();
                }else {
                    beforeImport();
                }
                break;
                 **/
            default:break;
        }
    }

    private void initComponent() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        toolbar.setTitle("批量导入会员");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_name = (TextView) findViewById(R.id.tv_main_name_a_import_member);
        tv_path = (TextView)findViewById(R.id.tv_main_path_a_import_member);
        btn_import = (Button)findViewById(R.id.btn_import_a_import_member);
        btn_select = (Button)findViewById(R.id.btn_select_a_import_member);

        btn_import.setOnClickListener(this);
        btn_select.setOnClickListener(this);
    }

    private void select() {

    }

    @Override
    public int checkSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    private List<Map<String,String>> beforeImport() {
        List<Map<String,String>> list = new ArrayList<>();
        file = new File(str_path);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] b = new byte[inputStream.available()];
            System.out.println(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.print(inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            if (file.exists()) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (file.getName().endsWith(".xls")) {
                    list = dealWithXls(inputStream);
                }else if (file.getName().endsWith(".xlsx")){
                    list = dealWithXlsx(inputStream);
                }
            }

        }
         **/
        return list;
    }

    private List<Map<String,String>> dealWithXls(InputStream inputStream) {
        List<Map<String,String>> list = new ArrayList<>();
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1;i < sheet.getLastRowNum();i++) {
            HSSFRow row = sheet.getRow(i);
            Map<String,String> map = new HashMap<>();
            for (int j = 0;j < 6;j++) {
                HSSFCell cell = row.getCell(j);
                switch (j) {
                    case 0:
                        map.put("name",cell.getStringCellValue());
                        break;
                    case 1:
                        map.put("login_name",cell.getStringCellValue());
                        break;
                    case 2:
                        map.put("password",cell.getStringCellValue());
                        break;
                    case 3:
                        map.put("birthday",cell.getStringCellValue());
                        break;
                    case 4:
                        map.put("phone",cell.getStringCellValue());
                        break;
                    case 5:
                        map.put("im",cell.getStringCellValue());
                        break;
                    default:break;
                }
            }
            list.add(map);
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Map<String,String>> dealWithXlsx(InputStream inputStream) {
        List<Map<String,String>> list = new ArrayList<>();
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0;i < sheet.getLastRowNum();i++) {
            Map<String,String> map = new HashMap<>();
            XSSFRow row = sheet.getRow(i);
            for (int j = 0;j < 6;j++) {
                XSSFCell cell = row.getCell(j);
                switch (j) {
                    case 0:
                        map.put("name",cell.getStringCellValue());
                        break;
                    case 1:
                        map.put("login_name",cell.getStringCellValue());
                        break;
                    case 2:
                        map.put("password",cell.getStringCellValue());
                        break;
                    case 3:
                        map.put("birthday",cell.getStringCellValue());
                        break;
                    case 4:
                        map.put("phone",cell.getStringCellValue());
                        break;
                    case 5:
                        map.put("im",cell.getStringCellValue());
                        break;
                    default:break;
                }
            }
            list.add(map);
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
