package org.geelato.core.meta.model.view;


import org.geelato.core.meta.model.field.ColumnMeta;

public class ViewMeta {
    private TableView viewMeta;
    private String viewName;
    private String  viewType;
    private String viewConstruct;
    public ViewMeta(String viewName, String viewType, String viewConstruct) {
        this.viewName = viewName;
        this.viewType = viewType;
        this.viewConstruct = viewConstruct;
    }
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

    public String getViewConstruct() {
        return viewConstruct;
    }

    public void setViewConstruct(String viewConstruct) {
        this.viewConstruct = viewConstruct;
    }
}
