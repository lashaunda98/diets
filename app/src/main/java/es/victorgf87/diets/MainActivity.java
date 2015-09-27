package es.victorgf87.diets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.victorgf87.diets.classes.DrankWaterGlass;
import es.victorgf87.diets.classes.ExerciseActivity;
import es.victorgf87.diets.storers.StorerFactory;

public class MainActivity extends AppCompatActivity
{
    @Bind(R.id.main_activity_btnAddGlass)Button btnAddGlass;

    @OnClick(R.id.main_activity_btnAddGlass)
    void addGlassClick()
    {
        Date date=new Date(System.currentTimeMillis());
        DrankWaterGlass newGlass=new DrankWaterGlass(date);
        StorerFactory.create(MainActivity.this).addDrankWaterGlass(newGlass);

        List<DrankWaterGlass> glasses=StorerFactory.create(MainActivity.this).getAllGlasses();
        int a=3;
        int b=a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        List<ExerciseActivity> activities= StorerFactory.create(this).getActivitiesList();
        int a=3;
        int b=a;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.menu_main_action_go_weights:
                changeActivity(WeightsActivity.class);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void changeActivity(Class klass)
    {
        Intent inte=new Intent();
        inte.setClass(this,klass);
        startActivity(inte);
    }
}
