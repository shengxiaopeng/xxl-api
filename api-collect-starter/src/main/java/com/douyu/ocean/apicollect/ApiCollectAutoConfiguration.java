package com.douyu.ocean.apicollect;

import com.alibaba.fastjson.JSON;
import com.douyu.ocean.api.collect.common.ApiInfo;
import com.douyu.ocean.api.collect.common.InterfaceInfo;
import com.douyu.ocean.api.collect.common.Parameter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


/**
 * ${DESCRIPTION}
 *
 * @author sxp
 * @create 2018/7/7.
 * <p>
 * https://blog.csdn.net/revitalizing/article/details/71036970
 */
@Configuration
@ConditionalOnClass({RequestMappingHandlerMapping.class})
@ConditionalOnProperty("ocean-api.project.name")
public class ApiCollectAutoConfiguration implements ApplicationContextAware {

    @Value("${ocean-api.project.name}")
    private String projectName;

    @Bean
    public ServletRegistrationBean apiCollectServlet() {
        return new ServletRegistrationBean(new ApiCollectServlet(projectName), "/api-collect");
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApiCollectAutoConfiguration.applicationContext = applicationContext;
    }

    static class ApiCollectServlet extends HttpServlet {

        private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap(8);
        private static final HashSet primitiveTypes1 = new HashSet(64);

        static {
            primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
            primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
            primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
            primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
            primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
            primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
            primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
            primitiveWrapperTypeMap.put(Short.class, Short.TYPE);

            primitiveTypes1.addAll(primitiveWrapperTypeMap.keySet());
            primitiveTypes1.addAll(primitiveWrapperTypeMap.values());
            primitiveTypes1.addAll(Arrays.asList(new Class[]{Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class, Object.class, Object[].class}));
            primitiveTypes1.addAll(Arrays.asList(new Class[]{boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class}));
            primitiveTypes1.add(Void.TYPE);
        }

        private String projectName;

        public ApiCollectServlet(String projectName) {
            super();
            this.projectName = projectName;
        }

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();

            RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> infoMap = mapping.getHandlerMethods();

            List<InterfaceInfo> interfaceInfos = new ArrayList<InterfaceInfo>();
            for (RequestMappingInfo info : infoMap.keySet()) {

                System.out.println(">>>>>>>>>>>>>>>>>>one interface<<<<<<<<<<<<<<<<<<<<<<<");
                //第一步，获取url
                String path = bracketTrim(info.getPatternsCondition().toString());
                System.out.println(path);

                //排除一些系统接口
                if ("/error".equals(path)) {
                    continue;
                }

                String headers = bracketTrim(info.getHeadersCondition().toString());
                String consumes = bracketTrim(info.getConsumesCondition().toString());
                String produces = bracketTrim(info.getProducesCondition().toString());
                String methods = bracketTrim(info.getMethodsCondition().toString());

                //获取参数信息
                HandlerMethod handlerMethod = infoMap.get(info);
                MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
                Method method = handlerMethod.getMethod();
                String[] parameterNames = u.getParameterNames(method);
                Class<?>[] parameterTypes = method.getParameterTypes();

                List<Parameter> parameterList = new ArrayList<Parameter>();
                int i = 0;
                for (Class type : parameterTypes) {

                    if (HttpServletRequest.class.isAssignableFrom(type)
                            || HttpServletResponse.class.isAssignableFrom(type)
                            || Model.class.isAssignableFrom(type)
                            || File.class.isAssignableFrom(type)
                            || MultipartFile.class.isAssignableFrom(type)) {
                        i++;
                        continue;
                    }

                    if (primitiveTypes1.contains(type)) {
                        Parameter parameter = new Parameter(parameterNames[i], type.getSimpleName());
                        parameterList.add(parameter);
                    } else {
                        //如果不是基础类型，需要反射填充其各个属性
                        //type 是复合类型
                        Field[] declaredFields = type.getDeclaredFields();
                        for (Field field : declaredFields) {
                            Parameter parameter = new Parameter(field.getName(), field.getType().getSimpleName());
                            parameterList.add(parameter);
                        }

                    }

                    i++;
                }
                System.out.println(JSON.toJSONString(parameterList));

                InterfaceInfo interfaceInfo = new InterfaceInfo(path, headers, methods, consumes, produces, parameterList);
                interfaceInfos.add(interfaceInfo);
            }

            ApiInfo projectInfo = new ApiInfo();
            projectInfo.setName(projectName);
            projectInfo.setInterfaceInfos(interfaceInfos);

            resp.setContentType("application/json");
            resp.getWriter().print(JSON.toJSONString(projectInfo, true));

        }

        /**
         * 去除左右两侧的 []
         * @param input
         * @return
         */
        private static String bracketTrim(String input) {
            if (StringUtils.isEmpty(input))
                return input;

            return input.substring(1, input.length() - 1);
        }

    }

}


