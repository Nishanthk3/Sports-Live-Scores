package com.nishanth.sportsscore.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName(value = "espn")
public class Espn {
	public List<Match> matches;
	@JsonIgnore
	public List<News> news;
	@JsonIgnore
	public List<Specials> specials;
	@JsonIgnore
	public Track track;
	public List<Match> getMatch() {
		return matches;
	}
	public void setMatch(List<Match> matches) {
		this.matches = matches;
	}

	
}