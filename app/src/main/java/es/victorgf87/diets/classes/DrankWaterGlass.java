package es.victorgf87.diets.classes;

import java.util.Date;

/**
 * Created by Victor Gonzalez on 27/9/15.
 */
public class DrankWaterGlass
{
    private Date date;
    private Integer id;

    public DrankWaterGlass()
    {
        this.date=new Date(System.currentTimeMillis());
    }

    public DrankWaterGlass(Integer id, Date date) {
        this.date = date;
        this.id = id;
    }

    public DrankWaterGlass(Date date) {
        this.date = date;
        this.id=null;
    }

    public long getDateTimeStamp()
    {
        return date.getTime();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
