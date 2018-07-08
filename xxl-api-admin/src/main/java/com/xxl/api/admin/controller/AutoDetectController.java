package com.xxl.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.douyu.ocean.api.collect.common.ApiInfo;
import com.douyu.ocean.api.collect.common.DetectResult;
import com.douyu.ocean.api.collect.common.InterfaceInfo;
import com.xxl.api.admin.controller.annotation.PermessionLimit;
import com.xxl.api.admin.core.consistant.RequestConfig;
import com.xxl.api.admin.core.model.ReturnT;
import com.xxl.api.admin.core.model.XxlApiDocument;
import com.xxl.api.admin.core.model.XxlApiProject;
import com.xxl.api.admin.core.util.JacksonUtil;
import com.xxl.api.admin.dao.IXxlApiDocumentDao;
import com.xxl.api.admin.dao.IXxlApiProjectDao;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:sxp
 * create at 2018/7/7 01:07;00
 */
@Controller
@RequestMapping("/detect")
public class AutoDetectController {

    @Resource
    private IXxlApiProjectDao xxlApiProjectDao;

    @Resource
    private IXxlApiDocumentDao xxlApiDocumentDao;

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    @PermessionLimit(limit=true,superUser = true)
    public ReturnT detect(Integer projectId,String projectName) throws URISyntaxException {
        //0 验证
        //getProjectInfo
        XxlApiProject project=null;
        if(projectId!=null){
           project=  xxlApiProjectDao.load(projectId);
        }
        if(project==null||projectName!=null){
            XxlApiProject query = new XxlApiProject();
            query.setName(projectName);
            project = xxlApiProjectDao.getUnique(query);
        }

        if(project==null){
            return new ReturnT(ReturnT.FAIL_CODE, "不存在对应的项目");
        }

        //1 通过http请求获取接口信息
        ApiInfo apiInfo = getApiInfoByHttp(project);

        //2 将接口数据，添加或更新到数据库中
        List<XxlApiDocument> documents = resolveXxlApiDocumentsFromApiInfo(project, apiInfo);

        //3 将解析得到接口信息，添加或更新到数据库中
        DetectResult detectResult = saveOrUpdateDocuments(project, documents);

        return new ReturnT(detectResult);
    }

    private ApiInfo getApiInfoByHttp(XxlApiProject project) throws URISyntaxException {
        String baseUrlQa = project.getBaseUrlQa();
        baseUrlQa = baseUrlQa.concat("/api-collect");
        String forObject = restTemplate.getForObject(new URI(baseUrlQa), String.class);
        return JSON.parseObject(forObject, ApiInfo.class);
    }

    /**
     * 将解析得到接口信息，添加或更新到数据库中
     * @param xxlApiProject
     * @param documents
     * @return
     */
    private DetectResult saveOrUpdateDocuments(XxlApiProject xxlApiProject, List<XxlApiDocument> documents) {
        int updateRows = 0;
        int addRows = 0;

        //save or update to db
        for (XxlApiDocument document : documents) {
            //get by project_id and request_url
            XxlApiDocument q = new XxlApiDocument();
            q.setRequestUrl(document.getRequestUrl());
            q.setProjectId(xxlApiProject.getId());
            XxlApiDocument load = xxlApiDocumentDao.getUnique(q);

            if (load == null || load.getName() == null) {
                xxlApiDocumentDao.add(document);
                addRows++;
            } else {
                document.setId(load.getId());
                xxlApiDocumentDao.updateByDetect(document);
                updateRows++;
            }

        }

        return new DetectResult(updateRows, addRows, xxlApiProject.getName());
    }

    /**
     * 将Http接口获取的ApiInfo转换为数据库存储的ApiDocuments
     * @param project
     * @param apiInfo
     * @return
     */
    private List<XxlApiDocument> resolveXxlApiDocumentsFromApiInfo(XxlApiProject project, ApiInfo apiInfo) {
        List<InterfaceInfo> interfaceInfos = apiInfo.getInterfaceInfos();
        List<XxlApiDocument> documents = new ArrayList<>();
        for (InterfaceInfo interfaceInfo : interfaceInfos) {
            XxlApiDocument document = new XxlApiDocument();
            document.setProjectId(project.getId());
            document.setGroupId(0);
            document.setStatus(0);
            document.setStarLevel(0);
            document.setRequestUrl(interfaceInfo.getPath());
            document.setName(interfaceInfo.getPath());

            //set method
            String methods = interfaceInfo.getMethods();
            String method = "GET";
            for (RequestConfig.RequestMethodEnum m : RequestConfig.RequestMethodEnum.values()) {
                if (methods.toUpperCase().contains(m.name())) {
                    method = m.name();
                }
            }

            document.setRequestMethod(method);
            document.setQueryParams(JacksonUtil.writeValueAsString(interfaceInfo.getParameters()));

            //set headers
            List<Map<String,String>> headerList=new ArrayList<>();

            String headers = interfaceInfo.getHeaders();
            if (!StringUtils.isEmpty(headers)) {
                String[] headerArr = headers.split(",");
                for (String header : headerArr) {
                    Map<String, String> headerMap = new HashMap<>(4);
                    String[] keyAndValue = header.split(":");

                    if (keyAndValue.length > 0) {
                        String key = keyAndValue[0];
                        String value = keyAndValue[1];

                        headerMap.put("key", key);
                        headerMap.put("value", value);
                    }
                    headerList.add(headerMap);

                }
            }
            document.setRequestHeaders(JacksonUtil.writeValueAsString(headerList));

            //set response content-type
            String produces = interfaceInfo.getProduces();
            String respType = "JSON";
            for (RequestConfig.ResponseContentType type : RequestConfig.ResponseContentType.values()) {
                if (produces.contains(type.type)) {
                    respType = type.name();
                }
            }

            document.setResponseDatatypeId(0);
            document.setSuccessRespType(respType);
            document.setFailRespType(respType);

            documents.add(document);

        }
        return documents;
    }


}
