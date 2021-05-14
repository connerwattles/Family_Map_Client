package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private Switch lifeStoryLines;
    private Switch familyTreeLines;
    private Switch spouseLines;
    private Switch fatherFilter;
    private Switch motherFilter;
    private Switch maleFilter;
    private Switch femaleFilter;
    private RelativeLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DataCache dataCache = DataCache.getInstance();

        lifeStoryLines = findViewById(R.id.lifeStorySwitch);
        lifeStoryLines.setChecked(dataCache.isLifeStoryLines());
        familyTreeLines = findViewById(R.id.familyTreeSwitch);
        familyTreeLines.setChecked(dataCache.isFamilyTreeLines());
        spouseLines = findViewById(R.id.spouseSwitch);
        spouseLines.setChecked(dataCache.isSpouseLines());

        fatherFilter = findViewById(R.id.fatherFilterSwitch);
        fatherFilter.setChecked(dataCache.isFatherFilter());
        motherFilter = findViewById(R.id.motherFilterSwitch);
        motherFilter.setChecked(dataCache.isMotherFilter());

        maleFilter = findViewById(R.id.maleFilterSwitch);
        maleFilter.setChecked(dataCache.isMaleFilter());
        femaleFilter = findViewById(R.id.femaleFilterSwitch);
        femaleFilter.setChecked(dataCache.isFemaleFilter());

        logout = findViewById(R.id.logoutField);


        lifeStoryLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setLifeStoryLines(isChecked);
            }
        });

        familyTreeLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFamilyTreeLines(isChecked);
            }
        });

        spouseLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setSpouseLines(isChecked);
            }
        });

        fatherFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFatherFilter(isChecked);
            }
        });

        motherFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setMotherFilter(isChecked);
            }
        });

        maleFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setMaleFilter(isChecked);
            }
        });

        femaleFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFemaleFilter(isChecked);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    public void openMainActivity() {
        DataCache dataCache = DataCache.getInstance();
        dataCache.destroyCurrentSession();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}