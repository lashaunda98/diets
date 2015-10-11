package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import es.victorgf87.diets.classes.WrapperInterface;

/**
 * Created by Victor on 09/10/2015 - 18:15.
 */
@Root(strict = false)
public class RecipeWrapperList implements WrapperInterface<Recipe>
{
    @ElementList(entry="Recipe",inline = true)
    public List<Recipe> list;

    @Override
    public List<Recipe> getList() {
        return list;
    }
}