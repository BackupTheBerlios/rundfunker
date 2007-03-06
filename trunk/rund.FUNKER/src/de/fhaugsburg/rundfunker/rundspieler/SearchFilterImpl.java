package de.fhaugsburg.rundfunker.rundspieler;


public class SearchFilterImpl implements SearchFilter {

    private String filter;
    
    private String searchString;
    
    public void setFilter(String filter) {
        this.filter= filter;

    }

    public void setSearchString(String searchString) {
        this.searchString=searchString;

    }

    public String getFilter() {
        return filter;
    }

    public String getSearchString() {
        return searchString;
    }

}
