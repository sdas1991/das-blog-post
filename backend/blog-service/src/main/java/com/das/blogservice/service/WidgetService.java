package com.das.blogservice.service;

import com.das.blogservice.model.Widget;
import com.das.blogservice.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WidgetService {

    private final WidgetRepository widgetRepository;
    private final S3Service s3Service;

    /**
     * Get all active widgets for display on home page
     */
    public List<Widget> getActiveWidgets() {
        return widgetRepository.findByActiveOrderByDisplayOrderAsc(true);
    }

    /**
     * Get all widgets (for admin)
     */
    public List<Widget> getAllWidgets() {
        return widgetRepository.findAllByOrderByDisplayOrderAsc();
    }

    /**
     * Get widget by ID
     */
    public Optional<Widget> getWidgetById(String id) {
        return widgetRepository.findById(id);
    }

    /**
     * Get widgets by post ID
     */
    public List<Widget> getWidgetsByPostId(String postId) {
        return widgetRepository.findByPostId(postId);
    }

    /**
     * Create a new widget
     */
    public Widget createWidget(Widget widget) {
        widget.setCreatedAt(LocalDateTime.now());
        widget.setUpdatedAt(LocalDateTime.now());

        // Set default values if not provided
        if (widget.getActive() == null) {
            widget.setActive(true);
        }
        if (widget.getDisplayOrder() == null) {
            widget.setDisplayOrder(0);
        }

        Widget savedWidget = widgetRepository.save(widget);
        log.info("Created widget with ID: {}", savedWidget.getId());
        return savedWidget;
    }

    /**
     * Update an existing widget
     */
    public Widget updateWidget(String id, Widget widget) {
        Optional<Widget> existingWidget = widgetRepository.findById(id);

        if (existingWidget.isEmpty()) {
            throw new RuntimeException("Widget not found with id: " + id);
        }

        Widget updatedWidget = existingWidget.get();
        updatedWidget.setTitle(widget.getTitle());
        updatedWidget.setShortDescription(widget.getShortDescription());
        updatedWidget.setPostId(widget.getPostId());
        updatedWidget.setActive(widget.getActive());
        updatedWidget.setDisplayOrder(widget.getDisplayOrder());
        updatedWidget.setConfig(widget.getConfig());
        updatedWidget.setUpdatedAt(LocalDateTime.now());

        // Update image URL only if a new one is provided
        if (widget.getImageUrl() != null && !widget.getImageUrl().isEmpty()) {
            // If there's an old image and it's different from the new one, delete the old one
            if (updatedWidget.getImageUrl() != null &&
                    !updatedWidget.getImageUrl().equals(widget.getImageUrl())) {
                try {
                    s3Service.deleteFile(updatedWidget.getImageUrl());
                } catch (Exception e) {
                    log.error("Failed to delete old image: {}", e.getMessage());
                }
            }
            updatedWidget.setImageUrl(widget.getImageUrl());
        }

        Widget saved = widgetRepository.save(updatedWidget);
        log.info("Updated widget with ID: {}", id);
        return saved;
    }

    /**
     * Delete a widget
     */
    public Boolean deleteWidget(String id) {
        Optional<Widget> widget = widgetRepository.findById(id);

        if (widget.isEmpty()) {
            return false;
        }

        // Delete associated S3 image if exists
        if (widget.get().getImageUrl() != null && !widget.get().getImageUrl().isEmpty()) {
            try {
                s3Service.deleteFile(widget.get().getImageUrl());
            } catch (Exception e) {
                log.error("Failed to delete widget image from S3: {}", e.getMessage());
            }
        }

        widgetRepository.deleteById(id);
        log.info("Deleted widget with ID: {}", id);
        return true;
    }

    /**
     * Reorder widgets
     */
    public List<Widget> reorderWidgets(List<String> widgetIds) {
        for (int i = 0; i < widgetIds.size(); i++) {
            String widgetId = widgetIds.get(i);
            Optional<Widget> widget = widgetRepository.findById(widgetId);
            if (widget.isPresent()) {
                Widget w = widget.get();
                w.setDisplayOrder(i);
                w.setUpdatedAt(LocalDateTime.now());
                widgetRepository.save(w);
            }
        }
        return getAllWidgets();
    }
}
