package org.geelato.core.meta.model.view;


public class ViewMeta {
    private TableView viewMeta;
    private String viewName;
    private String  viewType;

    public TableView getViewMeta() {
        return viewMeta;
    }

    public void setViewMeta(TableView viewMeta) {
        this.viewMeta = viewMeta;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}
