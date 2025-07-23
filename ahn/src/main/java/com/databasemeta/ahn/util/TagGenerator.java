package com.databasemeta.ahn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.TdTag;
import j2html.tags.specialized.ThTag;
import j2html.tags.specialized.TrTag;

import static j2html.TagCreator.*;

public class TagGenerator {
  public static String generateListTag(String tableKey, List<String> columnHeaderList, List<Map<String, Object>> columnBodyDataList){
        // 1. 컬럼 헤더 정의
        List<String> columnHeaders = columnHeaderList;

        // 2. 바디 데이터 정의
        List<Map<String, String>> rows = Optional.ofNullable(columnBodyDataList)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .map(map -> map.entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey() == null ? "" : e.getKey(),
                e -> e.getValue() == null ? "" : e.getValue().toString(),
                (v1, v2) -> v1 // key 중복시 첫 번째 값 사용
            ))
        )
        .collect(Collectors.toList());

        // 3. thead 동적 생성 (타입 명확히!)
        ThTag[] thTags = columnHeaders.stream()
            .map(header -> th().withText(header.toUpperCase()))
            .toArray(ThTag[]::new);

        DomContent theadTag = thead(
            tr(thTags)
        );

        // 4. tbody 동적 생성 (타입 명확히!)
        TrTag[] trTags = rows.stream()
        .map(row -> tr(
            columnHeaders.stream()
                .map(header -> td().withText(row.getOrDefault(header, "")))
                .toArray(TdTag[]::new)
        ))
        .toArray(TrTag[]::new);

        DomContent tbodyTag = tbody(trTags);

        // 5. 전체 카드 구조
        DivTag card = div().withClass("row").with(
            div().withClass("col-12 small").with(
                div().withClass("card card-success").with(
                    div().withClass("card-header").withStyle("background-color: #343a40;").with(
                        h3().withClass("card-title")
                            .withStyle("font-size: 13px;")
                            .withText( tableKey.toUpperCase() + " LIST"),
                        div().withClass("card-tools").with(
                            button().withType("button")
                                .attr("data-card-widget", "collapse")
                                .withClass("btn btn-tool").with(
                                    i().withClass("fas fa-minus")
                                )
                        )
                    ),
                    div().withClass("card-body table-responsive p-0").with(
                        table().withClass("table text-nowrap table-bordered table-hover table-sm").with(
                            theadTag,
                            tbodyTag
                        )
                    )
                )
            )
        );
        return card.render();
  }

  
  public static String generateListTag(String tableKey, List<Map<String, Object>> columnBodyDataList){
    List<String> keyList = new ArrayList<>(columnBodyDataList.get(0).keySet());
    return TagGenerator.generateListTag(tableKey, keyList, columnBodyDataList);
  }
}
