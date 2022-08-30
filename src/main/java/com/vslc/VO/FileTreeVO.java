package com.vslc.VO;

import java.util.List;
import java.util.Map;

/**
 * easyui filetree
 * Created by chenlele
 * 2018/4/27 10:51
 */
public class FileTreeVO {

    private String text;

    private String iconCls;

    private String url;

    private String state = "open";

    private Boolean checked;

    private Map<String, Object> attributes;

    private List<FileTreeVO> children;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<FileTreeVO> getChildren() {
        return children;
    }

    public void setChildren(List<FileTreeVO> children) {
        this.children = children;
    }
}
