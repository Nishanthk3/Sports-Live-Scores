package com.nishanth.sportsscore.api;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName(value = "matches")
public class Match {
	public String b1;
	public String b1d;
	public String b2;
	public String b2d;
	public String c;
	public String id;
	public String r;
	public String s;
	public String state;
	public String t;
	public String w;
	
	public String getB1() {
		return b1;
	}
	public void setB1(String b1) {
		this.b1 = b1;
	}
	public String getB1d() {
		return b1d;
	}
	public void setB1d(String b1d) {
		this.b1d = b1d;
	}
	public String getB2() {
		return b2;
	}
	public void setB2(String b2) {
		this.b2 = b2;
	}
	public String getB2d() {
		return b2d;
	}
	public void setB2d(String b2d) {
		this.b2d = b2d;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getR() {
		return r;
	}
	public void setR(String r) {
		this.r = r;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getW() {
		return w;
	}
	public void setW(String w) {
		this.w = w;
	}
	
	public String getTitle()
	{
		return (this.b1+this.b1d+" vs "+this.b2+this.b2d).replace("&amp;", "&").replace("&nbsp;", " ");
	}
	
	public String getDescription()
	{
		return (this.state+" "+this.t).replace("&amp;", "&").replace("&nbsp;", " ");
	}
}