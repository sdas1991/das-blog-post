package com.das.blogservice.controller;

import com.das.blogservice.model.Widget;
import com.das.blogservice.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WidgetController {

    private final WidgetService widgetService;

    @QueryMapping
    public List<Widget> widgets() {
        return widgetService.getAllWidgets();
    }

    @QueryMapping
    public List<Widget> activeWidgets() {
        return widgetService.getActiveWidgets();
    }

    @QueryMapping
    public Widget widget(@Argument String id) {
        return widgetService.getWidgetById(id).orElse(null);
    }

    @QueryMapping
    public List<Widget> widgetsByPostId(@Argument String postId) {
        return widgetService.getWidgetsByPostId(postId);
    }

    @MutationMapping
    public Widget createWidget(@Argument Map<String, Object> input) {
        Widget widget = mapInputToWidget(input);
        return widgetService.createWidget(widget);
    }

    @MutationMapping
    public Widget updateWidget(@Argument String id, @Argument Map<String, Object> input) {
        Widget widget = mapInputToWidget(input);
        return widgetService.updateWidget(id, widget);
    }

    @MutationMapping
    public Boolean deleteWidget(@Argument String id) {
        return widgetService.deleteWidget(id);
    }

    @MutationMapping
    public List<Widget> reorderWidgets(@Argument List<String> widgetIds) {
        return widgetService.reorderWidgets(widgetIds);
    }

    private Widget mapInputToWidget(Map<String, Object> input) {
        Widget.WidgetConfig config = null;

        if (input.containsKey("config") && input.get("config") != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> configMap = (Map<String, String>) input.get("config");
            config = Widget.WidgetConfig.builder()
                    .layout(configMap.get("layout"))
                    .backgroundColor(configMap.get("backgroundColor"))
                    .textColor(configMap.get("textColor"))
                    .apiEndpoint(configMap.get("apiEndpoint"))
                    .linkUrl(configMap.get("linkUrl"))
                    .build();
        }

        return Widget.builder()
                .title((String) input.get("title"))
                .shortDescription((String) input.get("shortDescription"))
                .imageUrl((String) input.get("imageUrl"))
                .postId((String) input.get("postId"))
                .displayOrder(input.containsKey("displayOrder")
                        ? (Integer) input.get("displayOrder")
                        : 0)
                .active(input.containsKey("active")
                        ? (Boolean) input.get("active")
                        : true)
                .config(config)
                .build();
    }
}
