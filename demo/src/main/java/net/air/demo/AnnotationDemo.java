package net.air.demo;

import org.reflections.Reflections;

import java.lang.annotation.*;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * 注解+反射演示类
 * 通过注解的方式获得各手机品牌的用户
 *
 * @author air
 */
public class AnnotationDemo {

    /**
     * 用户注解
     * 注解包含一个用户名称，默认用户名“air”
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(value = Users.class)
    @interface User {
        String name() default "air";
    }

    /**
     * 用户组注解
     * 包含多个用户注解
     */
    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Users {
        User[] value();
    }

    /**
     * 手机品牌枚举
     */
    enum PhoneBrand {
        Iphone_X, Huawei_P20, Xiaomi_9, OtherPhone
    }

    /**
     * 电话注解
     * 包含电话品牌类型，默认是其他手机品牌
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Phone {
        PhoneBrand phoneBrand() default PhoneBrand.OtherPhone;
    }

    /**
     * 电话用户接口
     */
    interface PhoneUser {
    }

    /**
     * 手机用户抽象类
     * 演示反射获取电话用户注解信息
     */
    static abstract class AbstractPhoneUser implements PhoneUser {
        /**
         * 重写Object类toString方法，反射获取注解内容
         * @return 注解内容
         */
        @Override
        public String toString() {
            StringBuilder outStr = new StringBuilder(1);
            PhoneBrand phoneBrand = null;
            if (this.getClass().isAnnotationPresent(Phone.class)) {
                phoneBrand = this.getClass().getAnnotation(Phone.class).phoneBrand();
            }
            outStr.append(phoneBrand).append("=[");
            if (this.getClass().isAnnotationPresent(Users.class)) {
                Users users = this.getClass().getAnnotation(Users.class);
                for (User user : users.value()) {
                    outStr.append(user);
                }
            }
            outStr.append("]");
            return outStr.toString();
        }
    }

    /**
     * 苹果手机用户类
     */
    @User(name = "乔布斯")
    @User(name = "库克")
    @Phone(phoneBrand = PhoneBrand.Iphone_X)
    static class AppleUser extends AbstractPhoneUser {
    }

    /**
     * 华为手机用户类
     */
    @User(name = "任正非")
    @User(name = "孟晚舟")
    @Phone(phoneBrand = PhoneBrand.Huawei_P20)
    static class HuaweiUser extends AbstractPhoneUser {
    }

    /**
     * 小米手机用户类
     */
    @User(name = "雷军")
    @User(name = "林斌")
    @Phone(phoneBrand = PhoneBrand.Xiaomi_9)
    static class XiaomiUser extends AbstractPhoneUser {
    }

    /**
     * 其他手机用户类
     */
    @User(name = "张三")
    @User
    @Phone
    static class OtherUser extends AbstractPhoneUser {
    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections("net.air.demo");
        Set<Class<? extends PhoneUser>> phoneUserClasses = reflections.getSubTypesOf(PhoneUser.class);
        for (Class<? extends PhoneUser> phoneUserClass : phoneUserClasses) {
            if (!Modifier.isAbstract(phoneUserClass.getModifiers())) {
                PhoneUser phoneUser = phoneUserClass.newInstance();
                System.out.println(phoneUser);
            }
        }
    }
}