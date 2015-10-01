package es.victorgf87.diets.classes.recipes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Víctor on 01/10/2015.
 */
public class Menu
{
    private List<Recipe> breakfast, morning, lunch, afternoon, dinner;
    private String description;

    public Menu(String description, List<Recipe> afternoon, List<Recipe> breakfast, List<Recipe> dinner, List<Recipe> lunch, List<Recipe> morning)
    {
        this.description=description;
        this.afternoon = afternoon;
        this.breakfast = breakfast;
        this.dinner = dinner;
        this.lunch = lunch;
        this.morning = morning;
    }

    public Menu()
    {
        this.description=null;
        this.afternoon = new ArrayList<Recipe>();
        this.breakfast = new ArrayList<Recipe>();
        this.dinner = new ArrayList<Recipe>();
        this.lunch = new ArrayList<Recipe>();
        this.morning = new ArrayList<Recipe>();
    }
}
