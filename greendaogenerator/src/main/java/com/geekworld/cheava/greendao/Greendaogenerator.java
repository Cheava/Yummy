package com.geekworld.cheava.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Greendaogenerator {
    //自动生成类的包名
    public static String PACKAGE_NAME = "com.geekworld.cheava.greendao";
    /**
     *
     */
    //这里的OUT_DIR就是我们新建的java-gem的路径
    public static String OUT_DIR = "D:\\Cheava\\Data\\workspace\\Yummy\\app\\src\\main\\java-gen";

    public static void main(String[] args) throws Exception {
        // 1、创建一个用于添加实体（entity）的模式（Schema）对象
        Schema schema = new Schema(1, PACKAGE_NAME); //第一个参数为数据库的版本号
        // Schema schema = new Schema(2, PACKAGE_NAME);版本号升级
        // 2、获得了Schema对象后就可以添加实体了，也就是添加数据库表了。
        addScreenContent(schema);
        // 3、利用DaoGenerator类生成代码，并将自动生成的代码放到指定的目录
        new DaoGenerator().generateAll(schema, OUT_DIR);
    }

    private static void addScreenContent(Schema schema) {
        // 4、一个实体类就关联到数据库中的一张表，此处表名为：ScreenWord
        Entity entity = schema.addEntity("ScreenWord");
        // 5、设置数据库表中的字段（greenDao会自动根据实体类的属性来创建表字段，并赋予默认值）
        entity.addIdProperty().autoincrement();
        entity.addStringProperty("word").notNull();
        entity = schema.addEntity("ScreenImage");
        // 5、设置数据库表中的字段（greenDao会自动根据实体类的属性来创建表字段，并赋予默认值）
        entity.addIdProperty().autoincrement();
        entity.addStringProperty("image").notNull();
        //entity.addStringProperty("address");  升级时添加的新列名
    }
}
