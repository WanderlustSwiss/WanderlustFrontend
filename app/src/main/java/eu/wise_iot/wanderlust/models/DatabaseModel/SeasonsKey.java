package eu.wise_iot.wanderlust.models.DatabaseModel;


import org.joda.time.DateTime;

public class SeasonsKey {

    private int key;
    private String name;
    private String start;
    private String end;

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public DateTime getStart() {

        String[] dates = start.split("\\."); //pos 0: day, pos 1: month
        return new DateTime(DateTime.now().getYear(), Integer.parseInt(dates[1]), Integer.parseInt(dates[0]), 0, 0);
    }

    public DateTime getEnd() {
        String[] dates = end.split("\\."); //pos 0: day, pos 1: month
        return new DateTime(DateTime.now().getYear(), Integer.parseInt(dates[1]), Integer.parseInt(dates[0]), 0, 0);
    }
}
