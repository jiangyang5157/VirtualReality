package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.base.data.RegularExpressionUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Debug usage: IP Address configuration
 * @author Yang
 * @since 11/4/2016
 */
public class EntryActivity extends AppCompatActivity {

    public static final String IP_ADDRESS_KEY = "IP_ADDRESS_KEY";

    private TextInputLayout layoutIpAddress;
    private TextInputEditText etIpAddress;
    private Button btnDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        setupViews();
    }

    private void setupViews() {
        layoutIpAddress = (TextInputLayout) findViewById(R.id.layout_ip_address);
        etIpAddress = (TextInputEditText) findViewById(R.id.et_ip_address);
        etIpAddress.setText(getLastIpAddress(getApplicationContext()));
        btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(btnDoneOnClickListener);
    }

    private Button.OnClickListener btnDoneOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String ipAddress = contentFilter(etIpAddress.getText().toString().trim(), RegularExpressionUtils.IP_ADDRESS_REGEX);
            layoutIpAddress.setErrorEnabled(ipAddress == null);
            if (layoutIpAddress.isErrorEnabled()) {
                layoutIpAddress.setError(AppUtils.getString(getApplicationContext(), R.string.error_invalid));
            } else {
                setLastIpAddress(getApplicationContext(), ipAddress);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private String contentFilter(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        boolean find = matcher.find();
        if (find) {
            return content.substring(matcher.start(), matcher.end());
        } else {
            return null;
        }
    }

    public static boolean setLastIpAddress(Context context, String ipAddress) {
        AssetUtils.IP_ADDRESS = ipAddress;
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(IP_ADDRESS_KEY, ipAddress).commit();
    }

    public static String getLastIpAddress(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(IP_ADDRESS_KEY, AssetUtils.IP_ADDRESS);
    }
}
