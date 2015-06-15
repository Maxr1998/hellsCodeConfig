package de.Maxr1998.xposed.hellscode;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        TextView title = (TextView) findViewById(R.id.title_text);
        SpannableStringBuilder sb = new SpannableStringBuilder(getString(R.string.hells_code));
        sb.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(getAssets(), "Unicode_IEC_symbol.ttf")), 6, 7, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        sb.setSpan(new RelativeSizeSpan(0.7f), 6, 7, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        title.setText(sb);

        Button openDialog = (Button) findViewById(R.id.dialog_button);
        openDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HellsCodeSetupDialog().show(getSupportFragmentManager(), "dialog");
            }
        });
    }
}
