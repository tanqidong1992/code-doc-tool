package com.hngd.parser.javadoc.extension;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.parser.javadoc.BlockTag;

/**
 * @author tqd
 */
public class TimeBlockTag extends BlockTag {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TimeBlockTag.class);
    public Date createTime;
    public String createTimeStr;

    /**
     * @author
     * @since 0.0.1
     */
    public TimeBlockTag() {
        super("@time");
    }

    @Override
    public String onParseStart(String line) {
        line = super.onParseStart(line);
        createTime = parseTime(line);
        if (createTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            createTimeStr = sdf.format(createTime).split(" ")[0];
        } else {
            createTimeStr = null;
        }
        return createTimeStr;
    }


    public static Date parseTime(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String source = timeStr;
        Date date = null;
        if (source.contains("下午")) {
            source = source.replace("下午", "");
            try {
                date = sdf.parse(source);
            } catch (Exception e) {
                logger.debug("parsing date str:["+timeStr+"] failed", e);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 12);
            date = calendar.getTime();
        } else if(source.contains("上午")){
            source = source.replace("上午", "");
            try {
                date = sdf.parse(source);
            } catch (Exception e) {
                logger.debug("parsing date str:["+timeStr+"] failed", e);
            }
        }else {
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                date = sdf.parse(source);
            } catch (Exception e) {
                logger.debug("parsing date str:["+timeStr+"] failed", e);
            }
        }
        return date;
    }

    
}
