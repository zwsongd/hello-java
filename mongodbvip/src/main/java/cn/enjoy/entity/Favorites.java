package cn.enjoy.entity;

import java.util.List;

public class Favorites {
	private List<String> movies;
	private List<String> cites;
	public List<String> getMovies() {
		return movies;
	}
	public void setMovies(List<String> movies) {
		this.movies = movies;
	}
	public List<String> getCites() {
		return cites;
	}
	public void setCites(List<String> cites) {
		this.cites = cites;
	}
	@Override
	public String toString() {
		return "Favorites [movies=" + movies + ", cites=" + cites + "]";
	}

}
