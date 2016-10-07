package com.geekworld.cheava.yummy.bean;

/*
* @class Word
* @desc  语录类
* @author wangzh
*/
public class Word {


    /**
     * code : 1
     * notice : {"date":"2016-09-28","title":"宅言api维护通知","content":"宅言数据构调整，维护时间2016-09-28~2016-09-30"}
     * data : {"id":"1627","taici":"活在只承认实力的世界，可能的确是布满靳棘。","cat":"a","catcn":"动画","show":null,"source":"学园救援团"}
     */

    private String code;
    /**
     * date : 2016-09-28
     * title : 宅言api维护通知
     * content : 宅言数据构调整，维护时间2016-09-28~2016-09-30
     */

    private NoticeBean notice;
    /**
     * id : 1627
     * taici : 活在只承认实力的世界，可能的确是布满靳棘。
     * cat : a
     * catcn : 动画
     * show : null
     * source : 学园救援团
     */

    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public NoticeBean getNotice() {
        return notice;
    }

    public void setNotice(NoticeBean notice) {
        this.notice = notice;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class NoticeBean {
        private String date;
        private String title;
        private String content;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class DataBean {
        private String id;
        private String taici;
        private String cat;
        private String catcn;
        private Object show;
        private String source;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTaici() {
            return taici;
        }

        public void setTaici(String taici) {
            this.taici = taici;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }

        public String getCatcn() {
            return catcn;
        }

        public void setCatcn(String catcn) {
            this.catcn = catcn;
        }

        public Object getShow() {
            return show;
        }

        public void setShow(Object show) {
            this.show = show;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
}
