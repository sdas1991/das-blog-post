import { defineStore } from 'pinia'
import { widgetService, blogService } from '@/services/api'

export const useWidgetStore = defineStore('widget', {
  state: () => ({
    widgets: [],
    activeWidgets: [],
    currentWidget: null,
    loading: false,
    error: null
  }),

  getters: {
    sortedWidgets: (state) => {
      return [...state.widgets].sort((a, b) => a.displayOrder - b.displayOrder)
    },

    sortedActiveWidgets: (state) => {
      return [...state.activeWidgets].sort((a, b) => a.displayOrder - b.displayOrder)
    }
  },

  actions: {
    async fetchWidgets() {
      this.loading = true
      this.error = null
      try {
        this.widgets = await widgetService.getWidgets()
      } catch (error) {
        this.error = error.message || 'Failed to fetch widgets'
        console.error('Error fetching widgets:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchActiveWidgets() {
      this.loading = true
      this.error = null
      try {
        this.activeWidgets = await widgetService.getActiveWidgets()
      } catch (error) {
        this.error = error.message || 'Failed to fetch active widgets'
        console.error('Error fetching active widgets:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchWidget(id) {
      this.loading = true
      this.error = null
      try {
        this.currentWidget = await widgetService.getWidget(id)
        return this.currentWidget
      } catch (error) {
        this.error = error.message || 'Failed to fetch widget'
        console.error('Error fetching widget:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createWidget(widgetData) {
      this.loading = true
      this.error = null
      try {
        const newWidget = await widgetService.createWidget(widgetData)
        this.widgets.push(newWidget)
        if (newWidget.active) {
          this.activeWidgets.push(newWidget)
        }
        return newWidget
      } catch (error) {
        this.error = error.message || 'Failed to create widget'
        console.error('Error creating widget:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateWidget(id, widgetData) {
      this.loading = true
      this.error = null
      try {
        const updatedWidget = await widgetService.updateWidget(id, widgetData)

        // Update in widgets array
        const widgetIndex = this.widgets.findIndex(w => w.id === id)
        if (widgetIndex !== -1) {
          this.widgets[widgetIndex] = updatedWidget
        }

        // Update in active widgets array
        const activeIndex = this.activeWidgets.findIndex(w => w.id === id)
        if (updatedWidget.active) {
          if (activeIndex !== -1) {
            this.activeWidgets[activeIndex] = updatedWidget
          } else {
            this.activeWidgets.push(updatedWidget)
          }
        } else if (activeIndex !== -1) {
          this.activeWidgets.splice(activeIndex, 1)
        }

        return updatedWidget
      } catch (error) {
        this.error = error.message || 'Failed to update widget'
        console.error('Error updating widget:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteWidget(id) {
      this.loading = true
      this.error = null
      try {
        await widgetService.deleteWidget(id)

        // Remove from widgets array
        this.widgets = this.widgets.filter(w => w.id !== id)

        // Remove from active widgets array
        this.activeWidgets = this.activeWidgets.filter(w => w.id !== id)

        return true
      } catch (error) {
        this.error = error.message || 'Failed to delete widget'
        console.error('Error deleting widget:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async reorderWidgets(widgetIds) {
      this.loading = true
      this.error = null
      try {
        await widgetService.reorderWidgets(widgetIds)
        await this.fetchWidgets()
      } catch (error) {
        this.error = error.message || 'Failed to reorder widgets'
        console.error('Error reordering widgets:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async uploadImage(file) {
      try {
        const response = await blogService.uploadWidgetImage(file)
        return response.url
      } catch (error) {
        console.error('Error uploading image:', error)
        throw error
      }
    },

    async deleteImage(imageUrl) {
      try {
        await blogService.deleteWidgetImage(imageUrl)
      } catch (error) {
        console.error('Error deleting image:', error)
        throw error
      }
    },

    clearError() {
      this.error = null
    }
  }
})
