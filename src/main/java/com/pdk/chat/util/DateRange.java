package com.pdk.chat.util;

import java.util.Date;

/**
 * Created by hubo on 2015/10/27
 */
public class DateRange {

    private Date start;

    private Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public DateRange(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

}
