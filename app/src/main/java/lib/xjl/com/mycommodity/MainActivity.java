package lib.xjl.com.mycommodity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xjl.commodity_view.CommodityView;

public class MainActivity extends AppCompatActivity {
    CommodityView commodityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commodityView = findViewById(R.id.commodity_view);
        commodityView.setInitCount("1");//设置初始显示的数量
        commodityView.setNumberChangeListener(new CommodityView.NumberChangeListener() {
            @Override
            public void afterNumberChanged(int amount) {
                Toast.makeText(MainActivity.this, "当前数量为" + amount, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
