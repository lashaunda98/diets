package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Víctor on 01/10/2015.
 */
@Root(name="Menu", strict = false)
public class Menu
{
    @Attribute(name="id")
    private Integer id;

    @Element(name="Breakfast")
    public MealWrapper breakfast;

    @Element(name="Morning")
    public MealWrapper morning;

    @Element(name="Lunch")
    public MealWrapper lunch;

    @Element(name="Afternoon")
    public MealWrapper afternoon;

    @Element(name="Dinner")
    public MealWrapper dinner;

    public Integer getId() {
        return id;
    }

    public MealWrapper getBreakfast() {
        return breakfast;
    }

    public MealWrapper getMorning() {
        return morning;
    }

    public MealWrapper getLunch() {
        return lunch;
    }

    public MealWrapper getAfternoon() {
        return afternoon;
    }

    public MealWrapper getDinner() {
        return dinner;
    }

    public Menu()
    {

    }
    /*
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
    }*/
}
