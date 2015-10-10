package es.victorgf87.diets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.victorgf87.diets.classes.recipes.Recipe;
import es.victorgf87.diets.storers.StorerFactory;

public class ShowRecipesActivity extends AppCompatActivity
{
    @Bind(R.id.activity_show_recipes_listViewRecipes)ListView list;


    private class Adapt extends BaseAdapter
    {
        private List<Recipe>recipes;
        public Adapt(List<Recipe>recipes)
        {
            this.recipes=recipes;
        }

        @Override
        public int getCount() {
            return recipes.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            TextView txt=new TextView(ShowRecipesActivity.this);
            txt.setText(recipes.get(position).getName());
            return txt;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipes);
        ButterKnife.bind(this);
        List<Recipe>recipes=StorerFactory.create(this).getAllRecipes();
        list.setAdapter(new Adapt(recipes));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id=item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
