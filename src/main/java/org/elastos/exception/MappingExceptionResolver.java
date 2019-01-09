package org.elastos.exception;

import com.alibaba.fastjson.JSON;
import org.elastos.conf.RetCodeConfiguration;
import org.elastos.entity.ReturnMsgEntity;
import org.elastos.util.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;


/**
 * Created by wanghan on 2018/11/25.
 */

@Component
public class MappingExceptionResolver implements HandlerExceptionResolver {
    @Autowired
    private RetCodeConfiguration retCodeConfiguration;

    private Logger logger = LoggerFactory.getLogger(MappingExceptionResolver.class);

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) {
        if (request.getHeader("Content-Type").contains("application/json")) {
            // 如果是ajax请求，JSON格式返回
            try {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();

                writer.write(this.handleError(exception));

                writer.flush();
                writer.close();
                exception.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String handleError(Exception ex) {


        long status = 0;

        if (ex instanceof ApiInternalException) {
            status = retCodeConfiguration.NTERNAL_ERROR();
        } else {
            status = retCodeConfiguration.BAD_REQUEST();
        }
        String msg = ex.getMessage();
        if (ex instanceof InvocationTargetException) {
            msg = ((InvocationTargetException) ex).getTargetException().getMessage();
        }
        if (StrKit.isBlank(msg)) {
            msg = ex.getCause().getMessage();
        }
        if (StrKit.isBlank(msg)) {
            msg = ex.toString();
        }
        return JSON.toJSONString(new ReturnMsgEntity().setResult(msg).setStatus(status));

    }
}
