package com.databasemeta.ahn.util;

import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.i;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;
import static j2html.TagCreator.option;
import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.select;
import static j2html.TagCreator.span;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import j2html.tags.ContainerTag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MysqlTypeToHtmlTagCopy {
    public static final ArrayList<String> inputTypeList ;
    public static final List<String> inputList = List.of(
        "text", "password", "checkbox", "radio", "button", "submit", "reset", 
        "file", "hidden", "email", "number", "date", "datetime-local", "month", "week", "time", 
        "color", "range", "search", "tel", "url","tel" );
        static {
            inputTypeList = new ArrayList<>(inputList);
        }
    /**
     * MySQL 타입에 따라 적절한 HTML 폼 태그(input, select 등) 반환
     * ENUM 타입은 옵션을 자동으로 추출해 <option> 태그로 변환
     * @param columnName 컬럼명
     * @param mysqlType MySQL 타입 문자열 (예: VARCHAR, INT, ENUM('Y','N') 등)
     * @return HTML 태그 문자열
     */
    public static String generateRawSearchInputTag(String tableName, String columnName, String mysqlType, String columnKey, String comments) {
        if (mysqlType == null) return "<input type='text' name='" + columnName + "' />";

        String type = mysqlType.toUpperCase();
        int idx = type.indexOf('(');
        String baseType = (idx > 0) ? type.substring(0, idx) : type;
        
        // 정수 타입
        if (baseType.equals("INT") || baseType.equals("INTEGER") ||
            baseType.equals("BIGINT") || baseType.equals("SMALLINT") ||
            baseType.equals("TINYINT") || baseType.equals("MEDIUMINT")) {
            if (columnKey.equalsIgnoreCase("PRI")){
                return input()
                .withType("number")
                .withName(tableName + "_"+ columnName)
                .withId(tableName + "_"+ columnName)
                .withPlaceholder(columnName + "(정수)를 입력헤주세요")
                .render();
            }else {
                return input()
                .withType("number")
                .withName(tableName + "-start_" + columnName)
                .withId(tableName + "_start_" +columnName)
                .withPlaceholder(columnName + "(정수)를 입력헤주세요").render() + " - " + 
                input()
                .withType("number")
                .withName(tableName + "_end_" +columnName)
                .withId(tableName + "_end_" +columnName)
                .withPlaceholder(columnName + "(정수)를 입력헤주세요").render();
            }
        }

        // ENUM 타입 자동 옵션 변환
        if (baseType.equalsIgnoreCase("ENUM")) {
            // 괄호 안의 값 추출
            int start = mysqlType.indexOf('(');
            int end = mysqlType.lastIndexOf(')');
            if (start > 0 && end > start) {
                String optionsStr = mysqlType.substring(start + 1, end).replace("'", "");
                // 옵션 분리: 작은따옴표로 감싼 값만 추출
                String[] options = optionsStr.split(",");
                List<String> optionValuesList = List.of(options);
                log.info(optionValuesList.toString());
                Map<String, String> commentMap = new HashMap<>();
                if (comments != null && "".equals(comments) && " ".equals(comments)){
                    String[] commentArr = comments.split(",");
                    for (String comment : commentArr){
                        log.info(comment.strip());
                        commentMap.put(comment.split("=")[0].strip(),comment.split("=")[1].strip());
                    }
                }
                return select(
                            option().withValue("").withText(columnName + "를 선택해 주세요"),
                            each(optionValuesList, value -> option(commentMap.get(value)).withValue(value))
                        )
                        .withName(tableName + "_"+ columnName)
                        .withId(tableName + "_"+ columnName)
                        .attr("style", "height:25px")
                        .render();
                           
            } else {
                return null;
            }
        }

        // 문자열 계열
        if (baseType.equals("VARCHAR") || baseType.equals("CHAR") || baseType.equals("TEXT") ||
            baseType.equals("SET")) {
                if (comments != null){
                    if (comments.toUpperCase().contains("PASS")){
                        return input()
                        .withType("password")
                        .withName(tableName + "_"+ columnName)
                        .withId(tableName + "_"+ columnName)
                        .withPlaceholder(columnName + "을 입력헤주세요")
                        .render();
                    }else {
                        return input()
                        .withType("text")
                        .withName(tableName + "_"+ columnName)
                        .withId(tableName + "_"+ columnName)
                        .withPlaceholder(columnName + "을 입력헤주세요")
                        .render();
                    }
                    
                }
        }

        // 날짜/시간
        if (baseType.equals("DATETIME") || baseType.equals("TIMESTAMP")) {
            LocalDate today = LocalDate.now(); // 오늘 날짜 동적 구하기
            LocalDateTime startOfDay = today.atStartOfDay();  
            //LocalDateTime endOfDay = today.atTime(LocalTime.MAX); 

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            String formatted1 = startOfDay.format(formatter);
            //String formatted2 = endOfDay.format(formatter);
            return 
                input()
                    .withName(tableName + "_start_" +columnName)
                    .withType("datetime-local")
                    .withId(tableName + "_start_" +columnName)
                    .withValue(formatted1)
                    .render() + " - " + 
                 input()
                    .withName(tableName + "_end_" +columnName)
                    .withType("datetime-local")
                    .withId(tableName + "_end_" +columnName)
                    .withValue(formatted1)
                    .render();
        }
        if (baseType.equals("DATE")) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formatted = now.format(formatter);
            return 
                input()
                    .withName(tableName + "_start_" +columnName)
                    .withType("date")
                    .withId(tableName + "_start_" +columnName)
                    .withValue(formatted)
                    .render() + " - " + 
                input()
                    .withName(tableName + "_end_" +columnName)
                    .withType("date")
                    .withId(tableName + "_end_" +columnName)
                    .withValue(formatted)
                    .render();
          
        }
        if (baseType.equals("TIME")) {
            LocalTime now = LocalTime.now();
            String formattedTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
            return 
                input()
                    .withName(tableName + "_start_" +columnName)
                    .withType("time")
                    .withId(tableName + "_start_" +columnName)
                    .withValue(formattedTime)
                    .render() + " - " + 
                input()
                    .withName(tableName + "_end_" +columnName)
                    .withType("time")
                    .withId(tableName + "_end_" +columnName)
                    .withValue(formattedTime)
                    .withPlaceholder(columnName + "을 입력헤주세요")
                    .render();
          
        }

        
        // 기타는 기본 text
        return div().withClass("col-sm-4 col-md-3 col-12").with(
                div().withClass("form-group").with(
                label(columnName.toUpperCase()).attr("for", tableName + "_" + columnName),
                input()
                    .withType("text")
                    .withClass("form-control")
                    .withId(tableName + "_" + columnName)
                    .withName(tableName + "_" + columnName)
                    .withPlaceholder(columnName + "을 입력헤주세요"))).render();
    }

    public static String generateStyledSearchInputTag(String tableName, String columnName, String mysqlType, String columnKey, String comments) {
        if (mysqlType == null) return "<input type='text' name='" + columnName + "' />";
        String type = mysqlType.toUpperCase();
        int idx = type.indexOf('(');
        String baseType = (idx > 0) ? type.substring(0, idx) : type;
        
        // 정수 타입
        if (baseType.equals("INT") || baseType.equals("INTEGER") ||
            baseType.equals("BIGINT") || baseType.equals("SMALLINT") ||
            baseType.equals("TINYINT") || baseType.equals("MEDIUMINT")) {
            String pkStr = "";
            if (columnKey !=null && columnKey.toLowerCase().contains("pri")){
                 pkStr = "(PK)";
            }

            if (columnKey !=null && columnKey.equalsIgnoreCase("PRI")){

                return 
                    div().withClass("form-group").with(
                        label(columnName.toUpperCase() + " " + pkStr).attr("for", tableName + "_" + columnName).withStyle("font-size : 0.8rem;"),
                    input()
                    .withName(tableName + "_" + columnName)
                    .withId(tableName + "_" + columnName)
                    .withType("number")
                    .withClass("form-control text-sm form-control-sm")
                    .withPlaceholder("정수")).render();

            }else {
                return  
                    div().withClass("form-group").with(
                        label(columnName.toUpperCase() + " " + pkStr).withStyle("font-size : 0.8rem;"),
                        div().withClass("input-group").with(
                            div().withClass("input-group-prepend"),
                            input()
                                .withType("number")
                                .withName(tableName + "_start_"+ columnName)
                                .withId(tableName + "_start_"+ columnName)
                                .withClass("form-control float-right text-sm form-control-sm")
                                .withPlaceholder("정수")
                                ,
                            rawHtml(" - "), // 구분자
                            input()
                                .withType("number")
                                .withClass("form-control float-right text-sm form-control-sm")
                                .withName(tableName + "_end_"+ columnName)
                                .withId(tableName + "_end_"+ columnName)
                                .withPlaceholder("정수")
                               
                        )
                    )
                .render();
            }
        }


        // ENUM 타입 자동 옵션 변환
        if (baseType.equalsIgnoreCase("ENUM")) {
            System.out.println("이넘타입 " + baseType);
            // 괄호 안의 값 추출
            int start = mysqlType.indexOf('(');
            int end = mysqlType.lastIndexOf(')');
            if (start > 0 && end > start) {
                String optionsStr = mysqlType.substring(start + 1, end).replace("'", "");
                // 옵션 분리: 작은따옴표로 감싼 값만 추출
                String[] options = optionsStr.split(",");
                List<String> optionValuesList = List.of(options);
                log.info(optionValuesList.toString());
                Map<String, String> commentMap = new HashMap<>();
                System.out.println("comment :" + comments);
                if (comments != null && "".equals(comments) && " ".equals(comments)){
                    String[] commentArr = comments.split(",");
                    for (String comment : commentArr){
                        log.info(comment.strip());
                        commentMap.put(comment.split("=")[0].strip(),comment.split("=")[1].strip());
                    }
                }
                    return  
                             div().withClass("form-group")
                                .with(
                                    label(columnName.toUpperCase()).withStyle("font-size : 0.8rem;"),
                                    select(
                                        option().withValue("").withText(columnName + " 를 선택해 주세요"),
                                        each(optionValuesList, value -> option(commentMap.get(value)).withValue(value))
                                    ).withClass("form-control text-sm form-control-sm")
                                    .withName(tableName + "_" +columnName)
                                    .withId(tableName + "_" +columnName)
                                )
                            .render();
                } else {
                    return null;
                }
        }

        // 문자열 계열
        if (baseType.equals("VARCHAR") || baseType.equals("CHAR") || baseType.equals("TEXT") ||
            baseType.equals("SET")) {
                String typeText = MysqlTypeToHtmlTag.inputTypeList.contains(comments.toLowerCase().strip()) ? comments : "text";
                String pkStr = "";
                if (columnKey !=null && columnKey.toLowerCase().contains("pri")){
                     pkStr = "(PK)";
                }
                return 
                            div().withClass("form-group").with(
                                label(columnName.toUpperCase() + " " + pkStr ).attr("for", tableName + "_" + columnName).withStyle("font-size : 0.8rem;"),
                            input()
                            .withName(tableName + "_" + columnName)
                            .withId(tableName + "_" + columnName)
                            .withType(typeText)
                            .withClass("form-control text-sm form-control-sm")
                            .withPlaceholder( "")).render();
             
        }

        if (baseType.equals("DATETIME") || baseType.equals("TIMESTAMP") || baseType.equals("DATE") || baseType.equals("DATE") || baseType.equals("TIME")) {
            String dateTypeText =  "";
            String valueText1 = "";
            String valueText2 = "";
            LocalDate today = LocalDate.now(); // 오늘 날짜 동적 구하기
            LocalDateTime startOfDay = today.atStartOfDay();  
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX); 
            
            if (baseType.equals("DATETIME") || baseType.equals("TIMESTAMP") ){
                dateTypeText = "datetime-local";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                valueText1 = startOfDay.format(formatter);
                valueText2 = valueText1;
            }else if (baseType.equals("DATE")){
                dateTypeText = "date";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                valueText1 = startOfDay.format(formatter);
                valueText2 = valueText1;
            }else if (baseType.equals("TIME")){
                dateTypeText = "time";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'T'HH:mm");
                valueText1 = startOfDay.format(formatter);
                valueText2 =  valueText1; 
            }
           
            return 
                div().withClass("form-group").with(
                    label(columnName.toUpperCase() + " ( 동일할 시 검색에서 제외 )").withStyle("font-size : 0.8rem;"),
                    div().withClass("input-group").with(
                        div().withClass("input-group-prepend").with(
                          
                        ),
                        input()
                            .withType(dateTypeText)
                            .withName(tableName + "_start_"+ columnName)
                            .withId(tableName + "_start_"+ columnName)
                            .withClass("form-control float-right text-sm form-control-sm")
                            .withValue(valueText1),
                        rawHtml(" - "), // 구분자
                        input()
                            .withType(dateTypeText)
                            .withClass("form-control float-right text-sm form-control-sm")
                            .withName(tableName + "_end_"+ columnName)
                            .withId(tableName + "_end_"+ columnName)
                            .withValue(valueText2)
                    )
                    // <!-- /.input group -->
                        )
                    .render();
        }
           
     

        
        // 기타는 기본 text
        return 
                div().withClass("form-group").with(
                label(columnName.toUpperCase()).attr("for", tableName + "_"+ columnName).withStyle("font-size : 0.8rem;"),
                input()
                    .withType("text")
                    .withClass("form-control text-sm")
                    .withId(tableName + "_"+ columnName)
                    .withPlaceholder("")).render();
    }

    public static String generateStyledColWrapedSearchInputTag(String tableName, String columnName, String mysqlType, String columnKey, String comments , String colString) {
        if (mysqlType == null) return "<input type='text' name='" + columnName + "' />";

        String type = mysqlType.toUpperCase();
        int idx = type.indexOf('(');
        String baseType = (idx > 0) ? type.substring(0, idx) : type;
        
        // 정수 타입
        if (baseType.equals("INT") || baseType.equals("INTEGER") ||
            baseType.equals("BIGINT") || baseType.equals("SMALLINT") ||
            baseType.equals("TINYINT") || baseType.equals("MEDIUMINT")) {
            if (columnKey.equalsIgnoreCase("PRI")){

                return div().withClass(colString).with(
                    div().withClass("form-group").with(
                        label(columnName.toUpperCase()).attr("for", tableName + "_" + columnName).withStyle("font-size : 0.8rem;"),
                    input()
                    .withName(tableName + "_" + columnName)
                    .withId(tableName + "_" + columnName)
                    .withType("number")
                    .withClass("form-control form-control-sm")
                    .withPlaceholder("정수"))).render();

            }else {
                return  div().withClass(colString).with(
                    div().withClass("form-group").with(
                        label(columnName.toUpperCase()).withStyle("font-size : 0.8rem;"),
                        div().withClass("input-group").with(
                            div().withClass("input-group-prepend"),
                            input()
                                .withType("number")
                                .withName(tableName + "_start_"+ columnName)
                                .withId(tableName + "_start_"+ columnName)
                                .withClass("form-control float-right form-control-sm")
                                .withPlaceholder("정수")
                                ,
                            rawHtml(" - "), // 구분자
                            input()
                                .withType("number")
                                .withClass("form-control float-right form-control-sm")
                                .withName(tableName + "_end_"+ columnName)
                                .withId(tableName + "_end_"+ columnName)
                                .withPlaceholder("정수")
                               
                        )
                    )
                )
                .render();
            }
        }


        // ENUM 타입 자동 옵션 변환
        if (baseType.equalsIgnoreCase("ENUM")) {
            // 괄호 안의 값 추출
            int start = mysqlType.indexOf('(');
            int end = mysqlType.lastIndexOf(')');
            if (start > 0 && end > start) {
                String optionsStr = mysqlType.substring(start + 1, end).replace("'", "");
                // 옵션 분리: 작은따옴표로 감싼 값만 추출
                String[] options = optionsStr.split(",");
                List<String> optionValuesList = List.of(options);
                log.info(optionValuesList.toString());
                Map<String, String> commentMap = new HashMap<>();
                if (comments != null && "".equals(comments) && " ".equals(comments)){
                    String[] commentArr = comments.split(",");
                    for (String comment : commentArr){
                        log.info(comment.strip());
                        commentMap.put(comment.split("=")[0].strip(),comment.split("=")[1].strip());
                    }
                }
                    return  div()
                            .withClass(colString)
                            .with(
                                div().withClass("form-group")
                                .with(
                                    label(columnName.toUpperCase()).withStyle("font-size : 0.8rem;"),
                                    select(
                                        option().withValue("").withText(columnName + " 를 선택해 주세요"),
                                        each(optionValuesList, value -> option(commentMap.get(value)).withValue(value))
                                    ).withClass("form-control form-control-sm")
                                    .withName(tableName + "_" +columnName)
                                    .withId(tableName + "_" +columnName)
                                )
                            ).render();
                } else {
                    return null;
                }
        }

        // 문자열 계열
        if (baseType.equals("VARCHAR") || baseType.equals("CHAR") || baseType.equals("TEXT") ||
            baseType.equals("SET")) {
                String typeText = MysqlTypeToHtmlTag.inputTypeList.contains(comments.toLowerCase().strip()) ? comments : "text";
                return div().withClass(colString).with(
                            div().withClass("form-group").with(
                                label(columnName.toUpperCase()).attr("for", tableName + "_" + columnName).withStyle("font-size : 0.8rem;"),
                            input()
                            .withName(tableName + "_" + columnName)
                            .withId(tableName + "_" + columnName)
                            .withType(typeText)
                            .withClass("form-control form-control-sm")
                            .withPlaceholder( ""))).render();
             
        }

        if (baseType.equals("DATETIME") || baseType.equals("TIMESTAMP") || baseType.equals("DATE") || baseType.equals("DATE") || baseType.equals("TIME")) {
            String dateTypeText =  "";
            String valueText1 = "";
            String valueText2 = "";
            LocalDate today = LocalDate.now(); // 오늘 날짜 동적 구하기
            LocalDateTime startOfDay = today.atStartOfDay();  
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX); 
            
            if (baseType.equals("DATETIME") || baseType.equals("TIMESTAMP") ){
                dateTypeText = "datetime-local";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                valueText1 = startOfDay.format(formatter);
                valueText2 = valueText1;
            }else if (baseType.equals("DATE")){
                dateTypeText = "date";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                valueText1 = startOfDay.format(formatter);
                valueText2 =valueText1;
            }else if (baseType.equals("TIME")){
                dateTypeText = "time";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'T'HH:mm");
                valueText1 = startOfDay.format(formatter);
                valueText2 = valueText1;
            }
           
            return div().withClass(colString).with(
                div().withClass("form-group").with(
                    label(columnName.toUpperCase()).withStyle("font-size : 0.8rem;"),
                    div().withClass("input-group").with(
                        div().withClass("input-group-prepend").with(
                            span().withClass("input-group-text").with(
                                i().withClass("far fa-clock")
                            )
                        ),
                        input()
                            .withType(dateTypeText)
                            .withName(tableName + "_start_"+ columnName)
                            .withId(tableName + "_start_"+ columnName)
                            .withClass("form-control float-right form-control-sm")
                            .withValue(valueText1),
                        rawHtml(" - "), // 구분자
                        input()
                            .withType(dateTypeText)
                            .withClass("form-control float-right form-control-sm")
                            .withName(tableName + "_end_"+ columnName)
                            .withId(tableName + "_end_"+ columnName)
                            .withValue(valueText2)
                    )
                    // <!-- /.input group -->
                        )
                    ).render();
        }
           
     

        
        // 기타는 기본 text
        return div().withClass(colString).with(
                div().withClass("form-group").with(
                label(columnName.toUpperCase()).attr("for", tableName + "_"+ columnName).withStyle("font-size : 0.8rem;"),
                input()
                    .withType("text")
                    .withClass("form-control form-control-sm")
                    .withId(tableName + "_"+ columnName)
                    .withPlaceholder(""))).render();
    }

     //아이템 생성 참을 위한 태그 
     //생성과 업데이트에서는 시간 관련한 태그는 필요가 없음
     public static String generateStyledCrateInputTag(String tableName, String columnName, String extra, String mysqlType, String columnKey, String comments , String colString) {
        if (mysqlType == null) return "<input type='text' name='" + columnName + "' />";

        String type = mysqlType.toUpperCase();
        int idx = type.indexOf('(');
        String baseType = (idx > 0) ? type.substring(0, idx) : type;
        
        // 정수 타입
        if (baseType.equals("INT") || baseType.equals("INTEGER") ||
            baseType.equals("BIGINT") || baseType.equals("SMALLINT") ||
            baseType.equals("TINYINT") || baseType.equals("MEDIUMINT")) {
            
            j2html.tags.specialized.InputTag inputTag = input()
                .withName(tableName + "_" + columnName)
                .withId(tableName + "_" + columnName)
                .withType("number")
                .withClass("form-control form-control-sm")
                .withPlaceholder("정수");

            if (extra != null && extra.toLowerCase().contains("auto")) {
                inputTag = inputTag.attr("readonly", "readonly");
               
            }
            String pkStr = "";
            if (columnKey !=null && columnKey.toLowerCase().contains("pri")){
                pkStr = "(PK)";
            }
            ContainerTag divTag = div().withClass(colString).with(
                div().withClass("form-group").with(
                    label(columnName.toUpperCase() + " " + pkStr).attr("for", tableName + "_" + columnName).withStyle("font-size : 0.8rem;"),
                    inputTag
                )
            );
          
            return divTag.render();
        }


        // ENUM 타입 자동 옵션 변환
        if (baseType.equalsIgnoreCase("ENUM")) {
            // 괄호 안의 값 추출
            int start = mysqlType.indexOf('(');
            int end = mysqlType.lastIndexOf(')');
            if (start > 0 && end > start) {
                String optionsStr = mysqlType.substring(start + 1, end).replace("'", "");
                // 옵션 분리: 작은따옴표로 감싼 값만 추출
                String[] options = optionsStr.split(",");
                List<String> optionValuesList = List.of(options);
                log.info(optionValuesList.toString());
                Map<String, String> commentMap = new HashMap<>();
                if (comments != null && "".equals(comments) && " ".equals(comments)){
                    String[] commentArr = comments.split(",");
                    for (String comment : commentArr){
                        log.info(comment.strip());
                        commentMap.put(comment.split("=")[0].strip(),comment.split("=")[1].strip());
                    }
                }
                    return  div()
                            .withClass(colString)
                            .with(
                                div().withClass("form-group")
                                .with(
                                    label(columnName.toUpperCase()).withStyle("font-size : 0.8rem;"),
                                    select(
                                        option().withValue("").withText(columnName + " 를 선택해 주세요"),
                                        each(optionValuesList, value -> option(commentMap.get(value)).withValue(value))
                                    ).withClass("form-control form-control-sm")
                                    .withName(tableName + "_" +columnName)
                                    .withId(tableName + "_" +columnName)
                                )
                            ).render();
                } else {
                    return null;
                }
        }

        // 문자열 계열
        if (baseType.equals("VARCHAR") || baseType.equals("CHAR") || baseType.equals("TEXT") ||
            baseType.equals("SET")) {
                String typeText = MysqlTypeToHtmlTag.inputTypeList.contains(comments.toLowerCase().strip()) ? comments : "text";
                String pkStr = "";
                if (columnKey !=null && columnKey.toLowerCase().contains("pri")){
                    pkStr = "(PK)";
                }
                return div()
                    .withClass(colString)
                    .with(
                        div().withClass("form-group").with(
                            label(columnName.toUpperCase() + " " + pkStr).attr("for", tableName + "_" + columnName).withStyle("font-size : 0.8rem;"),
                            input()
                            .withName(tableName + "_" + columnName)
                            .withId(tableName + "_" + columnName)
                            .withType(typeText)
                            .withClass("form-control form-control-sm")
                            .withPlaceholder( ""))).render();
             
        }

        
        // 위조건에 안걸리면 공백을 빈공백을 반환해서 렌더링이 없게 함 
        return "";
    }



}
