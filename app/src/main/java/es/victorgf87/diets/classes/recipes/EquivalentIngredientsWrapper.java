package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Victor on 09/10/2015 - 19:36.
 */
@Root
public class EquivalentIngredientsWrapper
{
    @ElementList(entry="Equivalence",inline = true)
    public List<EquivalentIngredient> list;
}
