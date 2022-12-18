package org.fightjc.xybot.module.genshin.dao;

import org.apache.ibatis.annotations.Param;
import org.fightjc.xybot.module.genshin.pojo.GroupCalendarBean;
import org.fightjc.xybot.module.genshin.pojo.GroupCalendarRecordBean;

import java.util.List;

public interface GenshinDao {

    /**
     * 获取所有推送原神日历记录
     * @return
     */
    List<GroupCalendarBean> getAllGroupCalendar();

    /**
     * 获取特定群的推送原神日历记录
     * @param groupId
     * @return
     */
    GroupCalendarBean getGroupCalendar(@Param("groupId") Long groupId);

    /**
     * 插入一条推送原神日历记录
     * @param groupCalendarBean
     */
    void updateGroupCalendar(GroupCalendarBean groupCalendarBean);

    /**
     * 更新一条推送原神日历记录
     * @param groupCalendarBean
     */
    void createGroupCalendar(GroupCalendarBean groupCalendarBean);

    /**
     * 插入一条修改推送原神日历记录
     * @param groupCalendarRecordBean
     */
    void createGroupCalendarRecord(GroupCalendarRecordBean groupCalendarRecordBean);
}
