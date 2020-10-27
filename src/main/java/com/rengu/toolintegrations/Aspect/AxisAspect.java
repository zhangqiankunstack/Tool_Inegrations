//package com.rengu.toolintegrations.Aspect;
//
//import com.rengu.toolintegrations.Controller.ToolWorkFlowNodeController;
//import com.rengu.toolintegrations.Entity.*;
//import com.rengu.toolintegrations.Repository.AxisRepository;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @program: Tool_Inegrations
// * @author: Zhangqiankun
// * @create: 2020-09-08 14:08
// **/
//
//
//@Slf4j
//@Aspect
//@Component
//@Api(tags = {"坐标轴切面类"})
//public class AxisAspect {
//    private final AxisRepository axisRepository;
//
//    @Autowired
//    public AxisAspect(AxisRepository axisRepository) {
//        this.axisRepository = axisRepository;
//    }
//
//    @Pointcut(value = "execution(public * com.rengu.toolintegrations.Controller..*(..))")
//    private void requestPonitCut() {
//    }
//
//    @Before(value = "requestPonitCut()")
//    public void doBefore(JoinPoint joinPoint) {
//    }
//
//    @AfterReturning(pointcut = "requestPonitCut()", returning = "result")
//    public void doAfterReturning(JoinPoint joinPoint, ResultEntity result) {
//            // 坐标轴接口
//            if (joinPoint.getTarget().getClass().equals(ToolWorkFlowNodeController.class)) {
//                switch (joinPoint.getSignature().getName()) {
//                    case "bindToolById": {
//                        AxisEntity axisEntity = (AxisEntity) result.getData();
//                        axisRepository.save(axisEntity);
//                        break;
//                    }
//                    case "bindToolConsequenceById":{
//                        AxisEntity axisEntity = (AxisEntity) result.getData();
//                        axisRepository.save(axisEntity);
//                    }
//                    default:
//                }
//            }
//        }
//
//    @AfterThrowing(pointcut = "requestPonitCut()", throwing = "exception")
//    public void doAfterThrowing(JoinPoint joinPoint, RuntimeException exception) {
//    }
//
//    @After(value = "requestPonitCut()")
//    public void doAfter(JoinPoint joinPoint) {
//    }
//}
