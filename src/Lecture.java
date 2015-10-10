import java.time.DayOfWeek;
import java.time.LocalTime;


public class Lecture {
    private String title;
    private String location;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    public Lecture(String title, String location, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime){
        this.title = title;
        this.location = location;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString(){
        return ("Title: " + title + ". Location: " + location + ". Day of the week: " + dayOfWeek + ". Start time: "
                + startTime + ". End time: " + endTime);
    }
}
