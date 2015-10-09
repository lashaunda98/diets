package es.victorgf87.diets.classes.exerciseactivities;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Victor on 09/10/2015 - 18:15.
 */
@Root(strict = false)
public class ExerciseActivityWrapperList
{
    @ElementList(entry="activity",inline = true)
    public List<ExerciseActivity> list;
}