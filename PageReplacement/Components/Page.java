package PageReplacement.Components;

public class Page {
    private int pageNumber;

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Page() { }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public String toString() {
        return String.valueOf(pageNumber);
    }
}
