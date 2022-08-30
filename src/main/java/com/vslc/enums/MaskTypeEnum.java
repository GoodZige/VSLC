package com.vslc.enums;

/**
 * Created by chenlele
 * 2018/6/9 14:53
 */
public enum MaskTypeEnum {
    MASK_LUNG(1, "lung", "肺分割结果", null),
    MASK_AIRWAY(2, "airWay", "气管", null),
    MASK_ARTERY(3, "artery", "动脉", null),
    MASK_VEIN(4, "vein", "静脉", null),
    MASK_NODULE(5, "nodule", "病灶", "病灶标定结果"),
    MASK_MATRIX(6, "matrix", "矩阵", null)
    ;

    private Integer type; //类型标识

    private String name; //文件名（不包含后缀）

    private String node; //节点名

    private String parentNode; //父节点，null则无

    MaskTypeEnum(Integer type, String name, String node, String parentNode) {
        this.type = type;
        this.name = name;
        this.node = node;
        this.parentNode = parentNode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getParentNode() {
        return parentNode;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
    }
}
