package es.victorgf87.diets.storers;

import android.content.Context;

/**
 * Created by Víctor on 26/09/2015.
 */
public class StorerFactory
{
    public static StorerInterface create(Context context)
    {
        return new DBStorer(context);
    }
}
