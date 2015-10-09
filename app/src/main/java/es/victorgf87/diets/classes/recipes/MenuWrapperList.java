package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Victor on 09/10/2015 - 18:15.
 */
@Root(strict = false)
public class MenuWrapperList
{
    @ElementList(entry="Menu",inline = true)
    public List<Menu> list;
}