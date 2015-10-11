package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Victor on 10/10/2015 - 05:34.
 */
@Root(strict = false)
public class MealWrapper
{
    @Attribute(name="description", required = false)
    private String description;

    @ElementList(entry = "Recipe", required = false, inline = true)
    List<Recipe>recipes;

    public MealWrapper()
    {

    }

    public String getDescription() {
        return description;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }
}
