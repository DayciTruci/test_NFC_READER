package com.example.test1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test1.DBHelper;
import com.example.test1.Data;

public class MainActivity extends Activity {

    private NfcAdapter nfcAdapter;
    private DBHelper dbHelper;

    private boolean is_delete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // NFC 어댑터 가져오기
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // NFC 기능이 꺼져있는 경우 Toast 메시지로 안내
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC 기능이 꺼져 있습니다. NFC를 켜주세요.", Toast.LENGTH_LONG).show();
        }

        dbHelper = new DBHelper(this);

        Button dbViewButton = findViewById(R.id.db_view);
        dbViewButton.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, activity FROM activity", null);

            StringBuilder sb = new StringBuilder();

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String activity = cursor.getString(cursor.getColumnIndexOrThrow("activity"));

                sb.append("id: ").append(id).append(", activity: ").append(activity).append("\n");
            }

            if (sb.length() == 0) {
                Toast.makeText(MainActivity.this, "등록된 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("등록된 데이터");
                builder.setMessage(sb.toString());
                builder.setPositiveButton("확인", null);
                builder.show();
            }

            cursor.close();
            db.close();
        });

        Button deleteButton = findViewById(R.id.delete);
        deleteButton.setOnClickListener(v -> {
            is_delete = !is_delete;
            if (is_delete)
                deleteButton.setText("제거");

            else
                deleteButton.setText("등록");

            Toast.makeText(MainActivity.this, "delete = " + is_delete, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // NFC 태그를 읽었을 때 호출되는 메소드
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            final String id = bytesToHex(tag.getId());
            System.out.println("READED ID = " + id);

            boolean is_inserted = dbHelper.insert_check(id);

            if (is_inserted == false) {
                if (is_delete)
                    Toast.makeText(MainActivity.this, "새 카드 등록을 원하시면 id 제거를 비활성화 하여주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                else
                    insertID(id);

            } else {
                if (is_delete)
                    deleteID(id);

                else
                    Toast.makeText(MainActivity.this, "이미 등록된 ID입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Foreground Dispatch 설정
        if (nfcAdapter != null) {
            Intent intent = new Intent(this, getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // PendingIntent 생성
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

            // Foreground Dispatch 설정
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Foreground Dispatch 해제
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private void deleteID(String id) {
        System.out.println("DELETE ID");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(id + "를 정말로 제거하시겠습니까?");

        builder.setPositiveButton("제거", (dialog, which) -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("activity", "id=?", new String[]{id});

            Toast.makeText(MainActivity.this, id + "가 테이블에서 제거되었습니다.", Toast.LENGTH_SHORT).show();

            db.close();
            dialog.dismiss();
        });

        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void insertID(String id) {
        System.out.println("INSERT ID");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("값 입력");
        final EditText input = new EditText(MainActivity.this);
        builder.setView(input);

        builder.setPositiveButton("등록", (dialog, which) -> {
            String textValue = input.getText().toString();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", id);
            values.put("activity", textValue);
            long newRowId = db.insert("activity", null, values);

            if (newRowId != -1) {
                Toast.makeText(MainActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

            db.close();
            dialog.dismiss();
        });

        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }
}