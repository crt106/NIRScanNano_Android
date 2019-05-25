package com.kstechnologies.nanoscan.event.base;

import java.util.HashMap;


/**
 * 准备用EventBus替代之前的本地广播策略 但是由于{@link com.kstechnologies.nanoscan.service.NanoBLEService}这个服务实在是太屎
 * 先暂时替代由其发出的广播
 * 原来的广播策略非常杂乱无章 满眼全是注册解注不说
 *  甚至有A广播到B B接收到后再不做任何处理发一条新的广播给A A再继续执行 搞不懂这是什么神奇的操作
 * TODO 更改所有本地广播策略
 * TODO 清理父包下不必要的事件
 * @author crt106 on 2019/5/24.
 */
public abstract class BaseEvent {

    /**
     * 用于将NanoAction与事件event对应起来的HashMap
     *
     * @see com.kstechnologies.nirscannanolibrary.KSTNanoSDK
     */
    public static HashMap<String, Class<?>> EventMap;

    /**
     * 获取本事件对应的NanaAction的名称
     * @return
     */
    protected abstract String getActionName();

//    static {
//        EventMap = new HashMap<>();
//        //获取Event下的所有事件
//        Package pkg = Package.getPackage("com.kstechnologies.nanoscan.event");
//        List<Class<?>> classList = ClassUtil.getAllClassByPackageName(pkg);
//        for (Class cl : classList) {
//            try {
//                BaseEvent bs = (BaseEvent) cl.newInstance();
//                EventMap.put(bs.getActionName(), cl);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
