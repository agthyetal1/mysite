package com.oil.action;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.oil.tool.CommAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/tool")
public class ToolAction extends CommAction {

    @RequestMapping(value = "/ddl2bean.do",method = RequestMethod.POST)
    @ResponseBody
    Object ddl2bean(String ddl) {
        if(ddl == null) {
            return failure("ddl不能为空");
        }
        ddl = ddl.replace("(15,4)", "(15_4)");
        String[] ddls = ddl.split(",");
        StringBuilder bean = new StringBuilder();
        StringBuilder mapper = new StringBuilder();
        for (int i = 0; i < ddls.length; i++) {
            String str = ddls[i];
            str = str.trim();
            String[] str2 = str.split(" ");
            if(str2.length < 2) {
                continue;
            }
            String col = str2[0];
            col = col.toLowerCase();
            String type = str2[1];
            if(type.toLowerCase().startsWith("number(1)") || type.toLowerCase().startsWith("number(2)") || type.toLowerCase().startsWith("number(3)")) {
                type = "Integer";
            } else if(type.toLowerCase().startsWith("number(15_4)")) {
                type = "Double";
            } else if(type.toLowerCase().startsWith("number")) {
                type = "Long";
            } else if(type.toLowerCase().startsWith("timestamp") || type.toLowerCase().startsWith("date")) {
                type = "Date";
            } else {
                type = "String";
            }
            String col2 = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, col);
            if("Date".equals(type)) {
                bean.append("@Temporal(TemporalType.TIMESTAMP)\r\n");
            }
            if("id".equals(col)) {
                bean.append("@Id\r\n");
                bean.append("@GeneratedValue(strategy = GenerationType.IDENTITY,generator = \"select xxx.nextval from dual\")\r\n");
            }
            bean.append("private "+type+" " + col2 + ";\r\n");

            if(col.equalsIgnoreCase("id")) {
                mapper.append("<id column=\""+col+"\" property=\""+col2+"\"/>\r\n");
            } else {
                mapper.append("<result column=\""+col+"\" property=\""+col2+"\"/>\r\n");
            }
        }
        return success( ImmutableMap.of("bean", bean.toString(), "mapper", mapper.toString()));
    }
}
