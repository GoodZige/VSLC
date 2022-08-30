package com.vslc.model;

public class Position {

	private Byte num;

	private Integer x;

	private Integer y;

	public Position() {

	}

	public Position(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}

	public Position(Byte num, Integer x, Integer y) {
		this.num = num;
		this.x = x;
		this.y = y;
	}

	public Byte getNum() {
		return num;
	}

	public void setNum(Byte num) {
		this.num = num;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}
}
