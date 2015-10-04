package es.victorgf87.diets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.dacer.androidcharts.LineView;

import java.util.ArrayList;
import java.util.Calendar;
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
    @Bind(R.id.main_activity_glasses_linear_chart)LineView lineViewGlassesChart;

    @OnClick(R.id.main_activity_btnAddGlass)
    void addGlassClick()
    {
        DrankWaterGlass newGlass=new DrankWaterGlass();
        StorerFactory.create(MainActivity.this).addDrankWaterGlass(newGlass);

        paintGlassesChart();
    }

    private void paintGlassesChart()
    {

        List<DrankWaterGlass>glassesCollection=StorerFactory.create(MainActivity.this).getAllGlasses();
        if(glassesCollection.size()==0)return;

        ArrayList<String>bottomTexts=new ArrayList<>();
        ArrayList<ArrayList<Integer>> dataList=new ArrayList<>();

        String textBefore=getDayMonthString(glassesCollection.get(0).getDate());

        int count=0;
        for(DrankWaterGlass glass:glassesCollection)
        {
            String currentText=getDayMonthString(glass.getDate());
            if(currentText.equals(textBefore))
            {
                count++;
            }
            else {
                bottomTexts.add(textBefore);
                ArrayList<Integer>newNumber=new ArrayList<>();
                newNumber.add(count);
                dataList.add(newNumber);
                count=0;
            }
            textBefore=currentText;
        }

        bottomTexts.add(textBefore);
        ArrayList<Integer>newNumber=new ArrayList<>();
        newNumber.add(count);
        dataList.add(newNumber);



        lineViewGlassesChart.setDrawDotLine(false); //optional
        lineViewGlassesChart.setShowPopup(LineView.SHOW_POPUPS_All); //optional
        lineViewGlassesChart.setBottomTextList(bottomTexts);
        lineViewGlassesChart.setDataList(dataList);




    }

    @Override
    protected void onResume() {
        super.onResume();
        paintGlassesChart();
        try {
            bla();
        }
        catch(Exception e)
        {
            int a=3;
            int b=a;
        }
    }



    private void bla()
    {

    }

    private String getDayMonthString(Date date)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        String day=""+calendar.get(Calendar.DAY_OF_MONTH);
        String month=""+(calendar.get(Calendar.MONTH)+1);
        return day+"/"+month;
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
