import java.time.DayOfWeek;

public class Utils {
    public static DayOfWeek getDay(String s){
        switch (s){
            case "Mon": return DayOfWeek.MONDAY;
            case "Tue": return DayOfWeek.TUESDAY;
            case "Wed": return DayOfWeek.WEDNESDAY;
            case "Thu": return DayOfWeek.THURSDAY;
            case "Fri": return DayOfWeek.FRIDAY;
        }
        return null;
    }
}
